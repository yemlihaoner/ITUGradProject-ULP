import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ULPProvider {
    public static void main(String[] args){
        try{
            SSLContext ctx = Constants.getCtx("/certs/ulpTrustStore2.jts","/certs/ulpKeyStore2.jks");
            //System.setProperty("javax.net.debug", "all");
            SSLServerSocket serverSocket = Constants.getServerSocket(ctx,6800);

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
