import Classes.*;
import Utils.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import javax.net.ssl.SSLSocket;
import java.io.*;
import java.net.UnknownHostException;
import java.security.*;

public class ULPVerifier extends Thread {
    public void run() {
        try{
            //Initialize BouncyCastle
            Security.addProvider(new BouncyCastleProvider());
            //Asymmetric RSA keys pubKey and privKey are initialized.
            PublicKey boardPubKey;
            SubLog lastSubLog=null;

            //Initialize Certificate for socket
            SSLSocket socketBoard = SocketUtils.getClientSocket("/certs/ulpTrustStore1.jts","/certs/ulpKeyStore1.jks",6868);

            //Socket read and write variables are prepared.
            InputStream inputB = socketBoard.getInputStream();
            OutputStream outputB = socketBoard.getOutputStream();
            ObjectInputStream obj_InputB = new ObjectInputStream(inputB);
            ObjectOutputStream obj_outB = new ObjectOutputStream(outputB);

            System.out.println("Bulletin Board is connecting...");
            try{
                obj_outB.writeObject(Constants.Role.Verifier);
                boardPubKey =  SocketUtils.getInputObject(obj_InputB,"PublicKey");
                System.out.println("Keys are exchanged");

                while (true) {
                    Thread.sleep(Constants.delay/10);
                    VerifierAnswer answer = CheckBoard.checkVerifier(boardPubKey,lastSubLog);
                    if(answer!=null){
                        System.out.println((answer.isVerified?"Log is Verified: ":"Log is not Verified: ")+Constants.dateFormatter.format(answer.log.time));
                        lastSubLog=answer.log;
                    }
               }
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
