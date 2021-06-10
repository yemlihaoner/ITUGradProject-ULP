import Classes.EncryptData;
import Classes.PhaseIII;
import Classes.PhaseIX;
import Classes.PhaseVI;
import Classes.Response.Response;
import Classes.Request.*;
import Classes.Testimonial.*;
import Utils.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.net.ssl.*;
import java.io.*;
import java.net.UnknownHostException;
import java.security.*;
import java.util.Date;
import java.util.UUID;

public class ULPUser {
    public static void main(String[] args) {

        try{
            Security.addProvider(new BouncyCastleProvider());
            KeyPair pair = KeyUtils.createKeyForRSA();
            Key sharedKey = KeyUtils.createKeyForAES();
            PublicKey pubKey = pair.getPublic();
            PrivateKey privKey = pair.getPrivate();
            PublicKey boardPubKey;
            PublicKey providerPubKey;

            SSLContext ctx = SocketUtils.getSSLContext("/certs/ulpTrustStore1.jts","/certs/ulpKeyStore1.jks");
            assert ctx != null;
            SSLSocket socketBoard = SocketUtils.getClientSocket(ctx,6868);

            InputStream inputB = socketBoard.getInputStream();
            OutputStream outputB = socketBoard.getOutputStream();
            PrintWriter writerB = new PrintWriter(outputB,true);
            BufferedReader readerB = new BufferedReader(new InputStreamReader(inputB));
            ObjectOutputStream obj_WriterB = new ObjectOutputStream(outputB);
            ObjectInputStream obj_InputB = new ObjectInputStream(inputB);

            try{
                ctx = SocketUtils.getSSLContext("/certs/ulpTrustStore2.jts","/certs/ulpKeyStore2.jks");
                assert ctx != null;
                SSLSocket socketProvider = SocketUtils.getClientSocket(ctx,6800);

                InputStream inputP = socketProvider.getInputStream();
                OutputStream outputP = socketProvider.getOutputStream();
                PrintWriter writerP = new PrintWriter(outputP,true);
                BufferedReader readerP = new BufferedReader(new InputStreamReader(inputP));
                ObjectOutputStream obj_WriterP = new ObjectOutputStream(outputP);
                ObjectInputStream obj_InputP = new ObjectInputStream(inputP);


                Date date_now = new Date(System.currentTimeMillis());
                writerB.println("User");

                obj_WriterB.writeObject(pubKey);
                boardPubKey = (PublicKey)obj_InputB.readObject();
                obj_WriterP.writeObject(pubKey);
                providerPubKey = (PublicKey)obj_InputP.readObject();

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
                try{
                    while(response==null){
                        obj_WriterP.writeObject(enc_phaseIII);
                        Thread.sleep(Constants.delay/2);

                        System.out.println("Signal[Provider]: PhaseIII");

                        EncryptData enc_phaseVI = (EncryptData) obj_InputP.readObject();
                        PhaseVI phaseVI =  Cryptography.decryptObjectAES(enc_phaseVI,privKey,providerPubKey);
                        System.out.println("Read[Provider]: {Mask: "+phaseVI.M+"}\n");

                        response = CheckBoard.checkResponse(N,boardPubKey);
                        if(response==null){
                            Thread.sleep(Constants.delay*5);
                            response = CheckBoard.checkResponse(N,boardPubKey);
                        }
                    }

                    //User is using service
                    Thread.sleep(1000);
                    //User is using service

                    date_now = new Date(System.currentTimeMillis());
                    TestimonialPartI t_partI = new TestimonialPartI(date_now, R_ks, "comment");
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
                            Thread.sleep(Constants.delay*5);
                            testimonial1 = CheckBoard.checkTestimonial(N,boardPubKey);
                        }
                        System.out.println("Board: Testimonial Write Success");
                    }

                    PhaseIX phaseIX = new PhaseIX(N,"Comment");
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
