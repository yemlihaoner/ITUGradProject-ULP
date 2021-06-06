import Classes.PhaseIII;
import Classes.PhaseIX;
import Classes.PhaseVI;
import Classes.Request.Request;
import Classes.Response.Response;
import Classes.Response.ResponsePartI;
import Classes.Response.ResponsePartII;
import Utils.CheckBoard;
import Utils.Constants;
import Utils.SocketUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;
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
            ObjectOutputStream obj_WriterP = new ObjectOutputStream(outputP);
            ObjectInputStream obj_InputP = new ObjectInputStream(inputP);

            //TODO:

            //Socket side
            //Object serialization write:
            //ObjectOutputStream outStream = new ObjectOutputStream(outputP);
            //Object serialization read:
            //ObjectInputStream inStream = new ObjectInputStream(inputP);

            //To get object:
            //Object objectName = (Object) inStream.readObject();
            //To send object:
            //outStream.writeObject(objectName);


            /*
            byte [] userRead = readerP.readLine().getBytes();              //Get User Public Key

            X509EncodedKeySpec ks = new X509EncodedKeySpec(userRead);      //Decode received key
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
                SSLContext ctx = SocketUtils.getCtx("/certs/ulpTrustStore1.jts","/certs/ulpKeyStore1.jks");
                SSLSocket socketBoard = SocketUtils.getClientSocket(ctx,6868);

                //InputStream inputB = socketBoard.getInputStream();
                OutputStream outputB = socketBoard.getOutputStream();
                PrintWriter writerB = new PrintWriter(outputB,true);
                //BufferedReader readerB = new BufferedReader(new InputStreamReader(inputB));
                ObjectOutputStream obj_WriterB = new ObjectOutputStream(outputB);
                //ObjectInputStream obj_InputB = new ObjectInputStream(inputB);

                /*
                writerB.println("Board"+userRead.toString());

                byte [] boardRead = readerB.readLine().getBytes();              //Get Board Public Key

                ks = new X509EncodedKeySpec(boardRead);      //Decode received key
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

                PhaseIII phaseIII = (PhaseIII) obj_InputP.readObject();
                Request request;
                try{
                    request = CheckBoard.checkRequest(phaseIII.N);
                    if(request!=null){
                        System.out.println("Read[Board]:Request");
                    }
                }catch (InterruptedException e) {
                    e.printStackTrace();
                }

                var date_now = new Date(System.currentTimeMillis());
                ResponsePartI partI = new ResponsePartI(date_now,phaseIII.R_ks,"a_m");
                ResponsePartII partII = new ResponsePartII(date_now, phaseIII.R_ksb, UUID.randomUUID().toString(),phaseIII.N);
                Response resp = new Response(partI,partII);

                String requestB = "Respond";
                Response response1;

                try{
                    response1 = null;
                    while(response1==null){
                        Thread.sleep(Constants.delay/2);
                        writerB.println("Provider");

                        obj_WriterB.writeObject(resp);
                        System.out.println("Write[Provider]:"+requestB);

                        response1 = CheckBoard.checkResponse(phaseIII.N);
                        if(response1==null){
                            Thread.sleep(Constants.delay*5);
                        }
                    }

                    String M = UUID.randomUUID().toString();
                    PhaseVI phaseVI = new PhaseVI(phaseIII.N,M);
                    obj_WriterP.writeObject(phaseVI);

                    System.out.println("Signal[User]:Respond");

                    PhaseIX phaseIX = (PhaseIX)obj_InputP.readObject();
                    System.out.println("Read[User]: "+phaseIX.N);
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
