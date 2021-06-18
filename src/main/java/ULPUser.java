import Classes.*;
import Classes.Response.Response;
import Classes.Request.*;
import Classes.Response.ResponsePartI;
import Classes.Response.ResponsePartII;
import Classes.Testimonial.*;
import Utils.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import javax.net.ssl.*;
import java.io.*;
import java.net.UnknownHostException;
import java.security.*;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ULPUser extends Thread {

    //In order to run phase vi in timeout thread, I needed to use it as global.
    PhaseVI phaseVI=null;
    boolean isServiceClosed=false;

    public void run() {
        try{
            //Initialize BouncyCastle
            Security.addProvider(new BouncyCastleProvider());
            //Asymmetric RSA keys pubKey and privKey are initialized.
            KeyPair pair = KeyUtils.createKeyForRSA();
            Key sharedKey = KeyUtils.createKeyForAES();
            PublicKey pubKey = pair.getPublic();
            PrivateKey privKey = pair.getPrivate();
            PublicKey boardPubKey;
            PublicKey providerPubKey;

            //Initialize Certificate for socket
            SSLSocket socketBoard = SocketUtils.getClientSocket("/certs/ulpTrustStore1.jts","/certs/ulpKeyStore1.jks",6868);

            //Socket read and write variables are prepared.
            InputStream inputB = socketBoard.getInputStream();
            OutputStream outputB = socketBoard.getOutputStream();
            ObjectOutputStream obj_WriterB = new ObjectOutputStream(outputB);
            ObjectInputStream obj_InputB = new ObjectInputStream(inputB);

            try{
                //Initialize Certificate for socket
                SSLSocket socketProvider = SocketUtils.getClientSocket("/certs/ulpTrustStore2.jts","/certs/ulpKeyStore2.jks",6800);

                //Socket read and write variables are prepared.
                InputStream inputP = socketProvider.getInputStream();
                OutputStream outputP = socketProvider.getOutputStream();
                ObjectOutputStream obj_WriterP = new ObjectOutputStream(outputP);
                ObjectInputStream obj_InputP = new ObjectInputStream(inputP);

                Date date_now = FuncUtils.getDate();
                obj_WriterB.writeObject(Constants.Role.User);

                obj_WriterB.writeObject(pubKey);
                obj_WriterP.writeObject(pubKey);
                boardPubKey =  SocketUtils.getInputObject(obj_InputB,"PublicKey");
                providerPubKey =  SocketUtils.getInputObject(obj_InputP,"PublicKey");
                obj_WriterB.writeObject(providerPubKey);
                System.out.println("Keys are exchanged");

                Contract contract = new Contract("testUser","testHost","localhost:6800","Request");
                String R_ks=UUID.randomUUID().toString();
                String R_ksb=UUID.randomUUID().toString();
                RequestPartI partI = new RequestPartI(date_now,R_ks, contract);
                RequestPartII partII = new RequestPartII(date_now,R_ksb);

                EncryptData req_enData1 = Cryptography.encryptObjectAES(partI,sharedKey,privKey,providerPubKey);
                EncryptData req_enData2 = Cryptography.encryptObjectAES(partII,sharedKey,privKey,boardPubKey);

                Request request = new Request(req_enData1,req_enData2);

                String N = null;
                try{
                    while(N==null){
                        System.out.println("Sending Request to Bulletin board");
                        obj_WriterB.writeObject(request);
                        Thread.sleep(Constants.delay/2);

                        N = CheckBoard.checkNForRequest(request,boardPubKey);
                        if(N==null){
                            Thread.sleep(Constants.delay*5);
                            N = CheckBoard.checkNForRequest(request,boardPubKey);
                        }
                    }
                    System.out.println("Board: Request Write Success");
                }catch (InterruptedException e) {
                    e.printStackTrace();
                }
                PhaseIII phaseIII = new PhaseIII(N, R_ks,R_ksb);
                EncryptData enc_phaseIII = Cryptography.encryptObjectAES(phaseIII,sharedKey,privKey,providerPubKey);

                Response response  = null;
                byte[] M;
                try {
                    try {
                        TimeOutRunner.runWithTimeout(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    while (phaseVI == null) {
                                        obj_WriterP.writeObject(enc_phaseIII);
                                        Thread.sleep(Constants.delay / 2);

                                        System.out.println("Signal[Provider]: PhaseIII");
                                        try {
                                            TimeOutRunner.runWithTimeout(new Runnable() {
                                                @Override
                                                public void run() {
                                                    try {
                                                        phaseVI = SocketUtils.getPhaseObject(obj_InputP, "PhaseVI", privKey, providerPubKey);
                                                    }
                                                    catch (InterruptedException e) {
                                                        System.out.println("Interrupted: "+e.getMessage());

                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }, 15, TimeUnit.SECONDS);
                                        }
                                        catch (TimeoutException e) {
                                            System.out.println("Timeout: "+e.getMessage());
                                        }
                                    }
                                }
                                catch (InterruptedException e) {
                                    System.out.println("Interrupted: "+e.getMessage());

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }, 65, TimeUnit.SECONDS);
                    }
                    catch (TimeoutException e) {
                        System.out.println("Timeout: "+e.getMessage());
                        response  = CheckBoard.checkResponse(N,boardPubKey);
                    }

                    //This is the 8. attack&precaution scenario of the protocol
                    //The PhaseVI is lost but response log is logged in Bulletin Board
                    //We need to retrieve Mask value directly from the Bulletin Board
                    if(response!=null){
                        obj_WriterB.writeObject(new LostPacket("PhaseVI is lost."));
                        M = SocketUtils.getPhaseObject(obj_InputB, "M", privKey, boardPubKey);
                    }else{
                        M = phaseVI.M;
                    }
                    System.out.println("Read[Provider]: {Mask: "+ Arrays.toString(M) +"}");

                    //Get response and decrypt object to receive access token
                    response  = CheckBoard.checkResponse(N,boardPubKey);
                    ResponsePartI res_partI = Cryptography.decryptObjectAES(response.first, privKey, providerPubKey);
                    byte[] accessToken_A = FuncUtils.xor(res_partI.a_m,M);
                    String accessToken =  new String(accessToken_A);

                    //User is using service
                    //During that time, it also checks for alternative scenario
                    try {
                        TimeOutRunner.runWithTimeout(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    PhaseIX phaseIX = SocketUtils.getPhaseObject(obj_InputP,"PhaseIX",privKey,providerPubKey);
                                    System.out.println("Alternative Scenario run.\n{PhaseIX Comment: "+phaseIX.comment+"}\n");
                                    isServiceClosed=true;
                                }
                                catch (InterruptedException e) {
                                    System.out.println("Interrupted: "+e.getMessage());

                                } catch (Exception e) {
                                    System.out.println("Exception: "+e.getMessage());
                                    //e.printStackTrace();
                                }
                            }
                        }, 5, TimeUnit.SECONDS);
                    }
                    catch (TimeoutException e) {
                        //Timeout for user.
                        //Run alternative scenario
                        isServiceClosed=false;
                    }
                    catch (Exception e){
                        System.out.println("Exception: "+e.getMessage());
                    }
                    //Thread.sleep(Constants.delay*5);

                    //User is using service
                    if(isServiceClosed){
                        Thread.sleep(Constants.delay*5);
                        socketBoard.close();
                        return;
                    }

                    date_now = FuncUtils.getDate();
                    TestimonialPartI t_partI = new TestimonialPartI(date_now, R_ks, M==null?Constants.Comment.Error:Constants.Comment.Success);
                    TestimonialPartII t_partII = new TestimonialPartII(date_now, R_ksb, N);

                    EncryptData tes_enData1 = Cryptography.encryptObjectAES(t_partI,sharedKey,privKey,providerPubKey);
                    EncryptData tes_enData2 = Cryptography.encryptObjectAES(t_partII,sharedKey,privKey,boardPubKey);

                    Testimonial testimonial = new Testimonial(tes_enData1,tes_enData2);

                    Testimonial testimonial1 =null;
                    while(testimonial1==null){
                        Thread.sleep(Constants.delay/2);
                        obj_WriterB.writeObject(testimonial);

                        testimonial1 = CheckBoard.checkTestimonial(N,boardPubKey);
                        if(testimonial1==null){
                            Thread.sleep(Constants.delay*10);
                            testimonial1 = CheckBoard.checkTestimonial(N,boardPubKey);
                        }
                        System.out.println("Board: Testimonial Write Success");
                    }

                    PhaseIX phaseIX = new PhaseIX(N,M==null?Constants.Comment.Error:Constants.Comment.Success);
                    EncryptData enc_phaseIX = Cryptography.encryptObjectAES(phaseIX,sharedKey,privKey,providerPubKey);

                    obj_WriterP.writeObject(enc_phaseIX);
                    System.out.println("Signal[Provider]:PhaseIX");

                }catch (InterruptedException e) {
                    e.printStackTrace();
                }

                socketProvider.close();
            }
            catch (UnknownHostException ex) {
                System.out.println("Server not found: " + ex.getMessage());

            } catch (IOException ex) {
                System.out.println("I/O error: " + ex.getMessage());
            }

            System.out.println("Operation successful. Socket is closing...");
            socketBoard.close();
        }
        catch (UnknownHostException ex) {
            System.out.println("Server not found: " + ex.getMessage());

        } catch (IOException ex) {
            System.out.println("I/O error: " + ex.getMessage());
        }catch (Exception e){
            System.out.println("Exception: " + e.getMessage());

        }
    }
}
