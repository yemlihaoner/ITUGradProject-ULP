import Classes.Log;
import Utils.Constants;
import Utils.KeyUtils;
import Utils.SocketUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.net.ssl.*;
import java.io.*;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.util.ArrayList;

public class ULPBulletinBoard {
    public static void main(String[] args){
        ArrayList<Log> logs = new ArrayList<Log>();
        try{
            Security.addProvider(new BouncyCastleProvider());
            SSLContext ctx = SocketUtils.getSSLContext("/certs/ulpTrustStore1.jts","/certs/ulpKeyStore1.jks");
            SSLServerSocket serverSocket = SocketUtils.getServerSocket(ctx,6868);
            KeyPair pair = KeyUtils.createKeyForRSA();

            System.out.println("Server is listening on port " + 6868);
            while (true) {
                Thread.sleep(Constants.delay);
                SSLSocket socket = (SSLSocket)serverSocket.accept();
                System.out.println("New client connected");

                new ULPBulletinBoardThread(socket,logs,pair
                        ).start();
            }
        }catch (IOException | InterruptedException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            e.printStackTrace();
        }
    }

}
