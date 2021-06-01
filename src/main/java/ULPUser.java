import javax.net.ssl.*;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.security.SecureRandom;



public class ULPUser {
    public static void main(String[] args) {

        try{

            SSLContext ctx = Constants.getCtx("/certs/ulpTrustStore1.jts","/certs/ulpKeyStore1.jks");
            SSLSocket socketBoard = Constants.getClientSocket(ctx,6868);

            InputStream inputB = socketBoard.getInputStream();
            OutputStream outputB = socketBoard.getOutputStream();
            PrintWriter writerB = new PrintWriter(outputB,true);
            BufferedReader readerB = new BufferedReader(new InputStreamReader(inputB));

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


                String requestB = "Request";
                writerB.println(requestB);

                try{
                    var isFound = Constants.checkBoard("Write[User]:Request");
                    if(isFound){
                        System.out.println("Board: Write Success");
                    }
                }catch (InterruptedException e) {
                    e.printStackTrace();
                }

                String requestP = "Request";
                writerP.println(requestP);

                System.out.println("Signal[Provider]:Request");

                String providerRead = readerP.readLine();
                System.out.println("Read[Provider]:"+providerRead);

                try{
                    var isFound = Constants.checkBoard("Write[Provider]:Respond");
                    if(isFound){
                        System.out.println("Board: Write Success");


                        //User is using service
                        Thread.sleep(1000);

                        //User is using service


                        requestB = "Testimonial";
                        writerB.println(requestB);

                        isFound = Constants.checkBoard("Write[User]:Testimonial");
                        if(isFound){
                            System.out.println("Board: Write Success");
                            requestP = "Testimonial";
                            writerP.println(requestP);
                        }
                    }
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
