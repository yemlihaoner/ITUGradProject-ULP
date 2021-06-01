import javax.net.ssl.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ULPBulletinBoard {
    public static void main(String[] args){
        ArrayList<Log> logs = new ArrayList<Log>();
     //   ArrayList<LogKeyPair> logKeys = new ArrayList<LogKeyPair>();
        try{
            SSLContext ctx = Constants.getCtx("/certs/ulpTrustStore1.jts","/certs/ulpKeyStore1.jks");
            SSLServerSocket serverSocket = Constants.getServerSocket(ctx,6868);

            System.out.println("Server is listening on port " + 6868);
            while (true) {
                Thread.sleep(Constants.delay);
                SSLSocket socket = (SSLSocket)serverSocket.accept();
                System.out.println("New client connected");

                new ULPBulletinBoardThread(socket,logs
                        //,logKeys
                        ).start();
            }
        }catch (IOException | InterruptedException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

}
