import Classes.EncryptData;
import Classes.PhaseIII;
import Classes.PhaseIX;
import Classes.PhaseVI;
import Classes.Request.Request;
import Classes.Response.*;
import Utils.*;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.Key;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;
import java.util.UUID;


public class ProviderThread extends Thread {
    private final Socket socketProvider;

    public ProviderThread(Socket socketProvider) {
        this.socketProvider = socketProvider;
    }

    public void run() {
        try{
            KeyPair pair = KeyUtils.createKeyForRSA();
            Key sharedKey = KeyUtils.createKeyForAES();
            PublicKey pubKey = pair.getPublic();
            PrivateKey privKey = pair.getPrivate();
            PublicKey userPubKey;
            PublicKey boardPubKey;

            InputStream inputU = socketProvider.getInputStream();
            OutputStream outputU = socketProvider.getOutputStream();
            PrintWriter writerU = new PrintWriter(outputU,true);
            BufferedReader readerU = new BufferedReader(new InputStreamReader(inputU));
            ObjectOutputStream obj_WriterU = new ObjectOutputStream(outputU);
            ObjectInputStream obj_InputU = new ObjectInputStream(inputU);

            try{
                SSLContext ctx = SocketUtils.getSSLContext("/certs/ulpTrustStore1.jts","/certs/ulpKeyStore1.jks");
                SSLSocket socketBoard = SocketUtils.getClientSocket(ctx,6868);

                InputStream inputB = socketBoard.getInputStream();
                OutputStream outputB = socketBoard.getOutputStream();
                PrintWriter writerB = new PrintWriter(outputB,true);
                //BufferedReader readerB = new BufferedReader(new InputStreamReader(inputB));
                ObjectOutputStream obj_WriterB = new ObjectOutputStream(outputB);
                ObjectInputStream obj_InputB = new ObjectInputStream(inputB);

                System.out.println("Key are exchanging...");
                obj_WriterU.writeObject(pubKey);
                writerB.println("Provider");
                obj_WriterB.writeObject(pubKey);
                userPubKey = SocketUtils.getInputObject(obj_InputU,"PublicKey");
                boardPubKey = SocketUtils.getInputObject(obj_InputB,"PublicKey");

                PhaseIII phaseIII = SocketUtils.getPhaseObject(obj_InputU,"PhaseIII",privKey,userPubKey);

                Request request;
                try{
                    request = CheckBoard.checkRequest(phaseIII.N,boardPubKey);
                    if(request!=null){
                        System.out.println("Read[Board]:Request");
                    }
                }catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Date date_now = CheckBoard.getDate();
                String M = UUID.randomUUID().toString();
                ResponsePartI partI = new ResponsePartI(date_now,phaseIII.R_ks,"a_m");
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

                    PhaseIX phaseIX = SocketUtils.getPhaseObject(obj_InputU,"PhaseIX",privKey,userPubKey);

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

            System.out.println("Operation successful. Socket is closing...");
            socketProvider.close();
        }catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }catch (Exception e){
            System.out.println("Server exception: " + e.getMessage());

        }
    }


}
