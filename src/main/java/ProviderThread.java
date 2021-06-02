import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.UUID;


public class ProviderThread extends Thread {
    private final Socket socketProvider;

    public ProviderThread(Socket socketProvider) {
        this.socketProvider = socketProvider;
    }

    public void run() {
        try{
            InputStream inputP = socketProvider.getInputStream();
            OutputStream outputP = socketProvider.getOutputStream();
            PrintWriter writerP = new PrintWriter(outputP,true);
            BufferedReader readerP = new BufferedReader(new InputStreamReader(inputP));

            /*
            byte [] userRead = readerP.readLine().getBytes();              //Get User Public Key

            X509EncodedKeySpec ks = new X509EncodedKeySpec(userRead);      //Decode recived key
            KeyFactory kf = KeyFactory.getInstance("RSA");
            PublicKey userPublicKey = kf.generatePublic(ks);
            System.out.println("BOARD: "+userPublicKey.toString());

            DHParameterSpec dhParamShared = ((DHPublicKey)userPublicKey).getParams();

            System.out.println("PROVIDER: Generate DH keypair ...");
            KeyPairGenerator providerKpairGen = KeyPairGenerator.getInstance("DH");
            providerKpairGen.initialize(dhParamShared);
            KeyPair providerKpair = providerKpairGen.generateKeyPair();

            System.out.println("PROVIDER: Initialize ...");
            KeyAgreement providerKeyAgree = KeyAgreement.getInstance("DH");
            providerKeyAgree.init(providerKpair.getPrivate());

            writerP.println(Arrays.toString(providerKpair.getPublic().getEncoded()));                            //Send PB public key to Provider

            */

            try{
                SSLContext ctx = Constants.getCtx("/certs/ulpTrustStore1.jts","/certs/ulpKeyStore1.jks");
                SSLSocket socketBoard = Constants.getClientSocket(ctx,6868);

                //InputStream inputB = socketBoard.getInputStream();
                OutputStream outputB = socketBoard.getOutputStream();
                PrintWriter writerB = new PrintWriter(outputB,true);
                //BufferedReader readerB = new BufferedReader(new InputStreamReader(inputB));

                /*
                writerB.println("Board"+userRead.toString());

                byte [] boardRead = readerB.readLine().getBytes();              //Get Board Public Key

                ks = new X509EncodedKeySpec(boardRead);      //Decode recived key
                kf = KeyFactory.getInstance("RSA");
                PublicKey boardPublicKey = kf.generatePublic(ks);

                Key pbKey = providerKeyAgree.doPhase(boardPublicKey, false);
                writerP.println(Arrays.toString(pbKey.getEncoded()));                            //Send PB public key to Provider


                boardRead = readerB.readLine().getBytes();                      //Get Board-Provider mix key from Board
                ks = new X509EncodedKeySpec(boardRead);                         //Decode received key
                kf = KeyFactory.getInstance("RSA");
                PublicKey ubKey = kf.generatePublic(ks);

                providerKeyAgree.doPhase(ubKey, true);
                byte[] sharedSecret = providerKeyAgree.generateSecret();        //Shared ubp key is achieved
                System.out.println("SharedSecret: "+ Arrays.toString(sharedSecret));
                */

                String UserRead = readerP.readLine();
                String uniqueID = readerP.readLine();
                System.out.println("Read[User]: "+UserRead+ "ID: "+uniqueID);


                try{
                    var isFound = Constants.checkBoard(uniqueID,"Request");
                    if(isFound){
                        System.out.println("Read[Board]:Request");
                    }
                }catch (InterruptedException e) {
                    e.printStackTrace();
                }
                writerB.println("Provider");

                String requestB = "Respond";
                writerB.println(requestB);
                writerB.println(uniqueID);
                System.out.println("Write[Provider]:"+requestB);

                try{
                    var isFound = Constants.checkBoard(uniqueID,"Respond");
                    if(isFound){
                        writerP.println(requestB);
                        System.out.println("Signal[User]:Respond");

                        UserRead = readerP.readLine();
                        System.out.println("Read[User]: "+UserRead);
                    }
                }catch (InterruptedException e) {
                    e.printStackTrace();
                }

                socketBoard.close();
            }
            catch (UnknownHostException ex) {
                System.out.println("Server not found: " + ex.getMessage());

            } catch (IOException ex) {
                System.out.println("I/O error: " + ex.getMessage());
            }

            socketProvider.close();
        }catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }catch (Exception e){
            System.out.println("Server exception: " + e.getMessage());

        }
    }


}
