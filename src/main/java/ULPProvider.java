import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ULPProvider {
    public static void main(String[] args){
        try( ServerSocket serverSocket = new ServerSocket(6800)){
            System.out.println("Server is listening on port " + 6800);

            while (true) {
                Thread.sleep(1000);
                Socket socket = serverSocket.accept();
                System.out.println("New client connected");

                new ProviderThread(socket).start();
            }
        }catch (IOException | InterruptedException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
