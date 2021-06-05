import Classes.PhaseIII;
import Classes.PhaseIX;
import Classes.PhaseVI;
import Classes.Request.Proposal;
import Classes.Request.Request;
import Classes.Request.RequestPartI;
import Classes.Request.RequestPartII;
import Classes.Testimonial.Testimonial;
import Classes.Testimonial.TestimonialPartI;
import Classes.Testimonial.TestimonialPartII;
import javax.net.ssl.*;
import java.io.*;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.UUID;


public class ULPUser {
    public static void main(String[] args) {

        try{

            SSLContext ctx = Constants.getCtx("/certs/ulpTrustStore1.jts","/certs/ulpKeyStore1.jks");
            SSLSocket socketBoard = Constants.getClientSocket(ctx,6868);

            InputStream inputB = socketBoard.getInputStream();
            OutputStream outputB = socketBoard.getOutputStream();
            PrintWriter writerB = new PrintWriter(outputB,true);
            BufferedReader readerB = new BufferedReader(new InputStreamReader(inputB));
            ObjectOutputStream obj_WriterB = new ObjectOutputStream(outputB);
            ObjectInputStream obj_InputB = new ObjectInputStream(inputB);
            /*
            System.out.println("USER: Generate DH keypair ...");
            KeyPairGenerator userKpairGen = KeyPairGenerator.getInstance("DH");
            userKpairGen.initialize(2048);
            KeyPair userKpair = userKpairGen.generateKeyPair();
            */

            try{
                ctx = Constants.getCtx("/certs/ulpTrustStore2.jts","/certs/ulpKeyStore2.jks");
                SSLSocket socketProvider = Constants.getClientSocket(ctx,6800);

                InputStream inputP = socketProvider.getInputStream();
                OutputStream outputP = socketProvider.getOutputStream();
                PrintWriter writerP = new PrintWriter(outputP,true);
                BufferedReader readerP = new BufferedReader(new InputStreamReader(inputP));
                ObjectOutputStream obj_WriterP = new ObjectOutputStream(outputP);
                ObjectInputStream obj_InputP = new ObjectInputStream(inputP);


                var date_now = new Date(System.currentTimeMillis());

                Proposal proposal = new Proposal("testUser","testHost","localhost:6800","Request");
                RequestPartI partI = new RequestPartI(date_now,UUID.randomUUID().toString(),proposal);
                RequestPartII partII = new RequestPartII(date_now,UUID.randomUUID().toString());
                Request req = new Request(partI,partII);
                writerB.println("User");

                /*
                //Exchanging Keys for DH
                writerB.println(userKpair.getPublic().getEncoded());            //Send User Public Key to board after encode
                writerP.println(userKpair.getPublic().getEncoded());            //Send User Public Key to provider after encode

                System.out.println("USER: Initialize ...");
                KeyAgreement userKeyAgree = KeyAgreement.getInstance("DH");
                userKeyAgree.init(userKpair.getPrivate());

                byte [] providerKey = readerP.readLine().getBytes();              //Get Provider Public Key

                X509EncodedKeySpec ks = new X509EncodedKeySpec(providerKey);      //Decode recived key
                KeyFactory kf = KeyFactory.getInstance("RSA");
                PublicKey providerPublicKey = kf.generatePublic(ks);

                Key upKey = userKeyAgree.doPhase(providerPublicKey, false);
                writerB.println(Arrays.toString(upKey.getEncoded()));                                //Send UP public key to Board


                byte [] pbRead = readerP.readLine().getBytes();                      //Get Board-Provider mix key from Board
                ks = new X509EncodedKeySpec(pbRead);                         //Decode recived key
                kf = KeyFactory.getInstance("RSA");
                PublicKey bpKey = kf.generatePublic(ks);

                userKeyAgree.doPhase(bpKey, true);

                byte[] sharedSecret = userKeyAgree.generateSecret();        //Shared ubp key is achieved
                System.out.println("SharedSecret: "+ Arrays.toString(sharedSecret));
                */

                String answer = null;
                try{
                    while(answer==null){
                        obj_WriterB.writeObject(req);
                        Thread.sleep(Constants.delay/2);

                        answer = Constants.checkBoard(req.second.R_ksb,"Request");
                        if(answer==null){
                            Thread.sleep(Constants.delay*5);
                        }
                    }
                    System.out.println("Board: Write Success");
                }catch (InterruptedException e) {
                    e.printStackTrace();
                }
                String N = answer;
                PhaseIII phaseIII = new PhaseIII(N, partI.R_ks, partII.R_ksb);

                answer = null;
                try{
                    while(answer==null){
                        obj_WriterP.writeObject(phaseIII);
                        Thread.sleep(Constants.delay/2);

                        System.out.println("Signal[Provider]:Request");
                        PhaseVI phaseVI = (PhaseVI) obj_InputP.readObject();
                        System.out.println("Read[Provider]:"+phaseVI.N);

                        answer = Constants.checkBoard(phaseIII.N,"Response");
                        if(answer==null){
                            Thread.sleep(Constants.delay*5);
                        }
                    }

                    System.out.println("Board: Write Success");

                    //User is using service
                    Thread.sleep(1000);
                    //User is using service

                    date_now = new Date(System.currentTimeMillis());
                    TestimonialPartI t_partI = new TestimonialPartI(date_now, phaseIII.R_ks, "comment");
                    TestimonialPartII t_partII = new TestimonialPartII(date_now, phaseIII.R_ksb, phaseIII.N);
                    Testimonial testimonial = new Testimonial(t_partI,t_partII);

                    answer =null;
                    while(answer==null){
                        Thread.sleep(Constants.delay/2);
                        obj_WriterB.writeObject(testimonial);

                        answer = Constants.checkBoard(N,"Testimonial");
                        if(answer==null){
                            Thread.sleep(Constants.delay*5);
                        }
                    }

                    PhaseIX phaseIX = new PhaseIX(phaseIII.N,"Comment");
                    System.out.println("Board: Write Success");
                    obj_WriterP.writeObject(phaseIX);

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
