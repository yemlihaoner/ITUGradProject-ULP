import Utils.Constants;
import Utils.SocketUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;
import java.io.*;
import java.security.Security;

//Provider or Host Role is defined as below. It creates a server socket and starts listening.
//If anyone connects, it runs a thread to run proposed protocol.
public class ULPProvider {
    public static void main(String[] args){
        try{
            Security.addProvider(new BouncyCastleProvider());
            SSLServerSocket serverSocket = SocketUtils.getServerSocket("/certs/ulpTrustStore2.jts","/certs/ulpKeyStore2.jks",6800);

            System.out.println("Server is listening on port " + 6800);

            while (true) {
                Thread.sleep(Constants.delay);
                SSLSocket socket = (SSLSocket) serverSocket.accept();
                System.out.println("New client connected");

                new ProviderThread(socket).start();
            }
        }catch (IOException | InterruptedException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
