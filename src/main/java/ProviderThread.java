import Classes.EncryptData;
import Classes.PhaseIII;
import Classes.PhaseIX;
import Classes.PhaseVI;
import Classes.Request.Request;
import Classes.Request.RequestPartI;
import Classes.Response.*;
import Classes.Testimonial.Testimonial;
import Classes.Testimonial.TestimonialPartI;
import Classes.Testimonial.TestimonialPartII;
import Utils.*;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class ProviderThread extends Thread {
    private final Socket socketUser;

    public ProviderThread(Socket socketUser) {
        this.socketUser = socketUser;
    }

    public void run() {
        try{
            //Asymmetric RSA keys pubKey and privKey are initialized.
            KeyPair pair = KeyUtils.createKeyForRSA();
            Key sharedKey = KeyUtils.createKeyForAES();
            PublicKey pubKey = pair.getPublic();
            PrivateKey privKey = pair.getPrivate();
            PublicKey userPubKey;
            PublicKey boardPubKey;

            //Socket read and write variables are prepared.
            InputStream inputU = socketUser.getInputStream();
            OutputStream outputU = socketUser.getOutputStream();
            ObjectOutputStream obj_WriterU = new ObjectOutputStream(outputU);
            ObjectInputStream obj_InputU = new ObjectInputStream(inputU);

            //To check if PhaseIX runs timeout error. If timeout exceeds, runs alternative scenario
            boolean isTimeout=false;
            //Flag to test if service is not available for some reason.
            //if set to true runs alternative scenario.
            boolean isNoService=false;

            try{
                //Initialize Certificate for socket
                SSLContext ctx = SocketUtils.getSSLContext("/certs/ulpTrustStore1.jts","/certs/ulpKeyStore1.jks");
                SSLSocket socketBoard = SocketUtils.getClientSocket(ctx,6868);

                //Socket read and write variables are prepared.
                InputStream inputB = socketBoard.getInputStream();
                OutputStream outputB = socketBoard.getOutputStream();
                PrintWriter writerB = new PrintWriter(outputB,true);
                ObjectOutputStream obj_WriterB = new ObjectOutputStream(outputB);
                ObjectInputStream obj_InputB = new ObjectInputStream(inputB);

                System.out.println("Key are exchanging...");
                obj_WriterU.writeObject(pubKey);
                userPubKey = SocketUtils.getInputObject(obj_InputU,"PublicKey");

                obj_WriterB.writeObject(Constants.Role.Provider);

                obj_WriterB.writeObject(pubKey);
                obj_WriterB.writeObject(userPubKey);
                boardPubKey = SocketUtils.getInputObject(obj_InputB,"PublicKey");

                PhaseIII phaseIII = SocketUtils.getPhaseObject(obj_InputU,"PhaseIII",privKey,userPubKey);

                Request request=null;

                while(request==null){
                    request = CheckBoard.checkRequest(phaseIII.N,boardPubKey);
                    if(request==null){
                        Thread.sleep(Constants.delay*5);
                        request = CheckBoard.checkRequest(phaseIII.N,boardPubKey);
                    }
                }
                System.out.println("Read[Board]:Request");

                RequestPartI requestPartI = Cryptography.decryptObjectAES( request.first, privKey, userPubKey);
                Date date_now = FuncUtils.getDate();

                byte[] M = FuncUtils.generateMask();    //Mask
                char[] C = SerializationUtils.serialize(requestPartI.contract).toCharArray();
                String C_M = SerializationUtils.serialize(FuncUtils.hashMask(C,M));
                String A = SignatureUtils.sign(C_M,privKey);    //Access Token
                byte[] A_M = FuncUtils.xor(A.getBytes(StandardCharsets.UTF_8),M);

                /*
                byte[] A_inBytes = FuncUtils.xor(A_M,M);
                String A_copy = new String(A_inBytes);
                boolean verify_A = SignatureUtils.verify(C_M,A_copy,pubKey);
                */

                ResponsePartI partI = new ResponsePartI(date_now,phaseIII.R_ks,A_M);
                ResponsePartII partII = new ResponsePartII(date_now, phaseIII.R_ksb, M,phaseIII.N);

                EncryptData res_enData1 = Cryptography.encryptObjectAES(partI,sharedKey,privKey,userPubKey);
                EncryptData res_enData2 = Cryptography.encryptObjectAES(partII,sharedKey,privKey,boardPubKey);

                Response resp = new Response(res_enData1,res_enData2);

                Response response1;


                try{
                    response1 = null;
                    while(response1==null){
                        Thread.sleep(Constants.delay/2);

                        obj_WriterB.writeObject(resp);
                        System.out.println("Write[Provider]: Response");

                        response1 = CheckBoard.checkResponse(phaseIII.N,boardPubKey);
                        if(response1==null){
                            Thread.sleep(Constants.delay*5);
                            response1 = CheckBoard.checkResponse(phaseIII.N,boardPubKey);
                        }
                    }

                    PhaseVI phaseVI = new PhaseVI(phaseIII.N,M);
                    EncryptData enc_phaseVI = Cryptography.encryptObjectAES(phaseVI,sharedKey,privKey,userPubKey);

                    obj_WriterU.writeObject(enc_phaseVI);
                    System.out.println("Signal[User]: Response");
                    try {
                        TimeOutRunner.runWithTimeout(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    PhaseIX phaseIX = SocketUtils.getPhaseObject(obj_InputU,"PhaseIX",privKey,userPubKey);
                                    System.out.println("Read[User]: "+phaseIX.N);
                                }
                                catch (InterruptedException e) {
                                    System.out.println("Interrupted: "+e.getMessage());

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }, isNoService?0:2, TimeUnit.MINUTES);
                    }
                    catch (TimeoutException e) {
                        //Timeout for user.
                        //Run alternative scenario
                        System.out.println("Timeput: "+e.getMessage());
                        isTimeout = true;
                    }

                }catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if(isNoService||isTimeout){

                    //First close user connection to interrupts
                    PhaseIX phaseIX = new PhaseIX(phaseIII.N,isNoService?Constants.Comment.NoService:Constants.Comment.Timeout);
                    EncryptData enc_phaseIX = Cryptography.encryptObjectAES(phaseIX,sharedKey,privKey,userPubKey);

                    obj_WriterU.writeObject(enc_phaseIX);
                    System.out.println("Signal[Provider]:PhaseIX");

                    //Then, publish testimonial into Bulletin board
                    date_now = FuncUtils.getDate();

                    TestimonialPartI t_partI = new TestimonialPartI(date_now, phaseIII.R_ks, isNoService?Constants.Comment.NoService:Constants.Comment.Timeout);
                    TestimonialPartII t_partII = new TestimonialPartII(date_now, phaseIII.R_ksb, phaseIII.N);

                    EncryptData tes_enData1 = Cryptography.encryptObjectAES(t_partI,sharedKey,privKey,userPubKey);
                    EncryptData tes_enData2 = Cryptography.encryptObjectAES(t_partII,sharedKey,privKey,boardPubKey);

                    Testimonial testimonial = new Testimonial(tes_enData1,tes_enData2);

                    Testimonial testimonial1 =null;
                    while(testimonial1==null){
                        Thread.sleep(Constants.delay/2);
                        obj_WriterB.writeObject(testimonial);

                        testimonial1 = CheckBoard.checkTestimonial(phaseIII.N,boardPubKey);
                        if(testimonial1==null){
                            Thread.sleep(Constants.delay*10);
                            testimonial1 = CheckBoard.checkTestimonial(phaseIII.N,boardPubKey);
                        }
                        System.out.println("Board: Testimonial Write Success");
                    }
                }
                socketBoard.close();
            }
            catch (UnknownHostException ex) {
                System.out.println("Server not found: " + ex.getMessage());

            } catch (IOException ex) {
                System.out.println("I/O error: " + ex.getMessage());
            }

            System.out.println("Operation successful. Socket is closing...");
            socketUser.close();
        }catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }catch (Exception e){
            System.out.println("Server exception: " + e.getMessage());

        }
    }


}
