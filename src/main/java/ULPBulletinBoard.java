import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ULPBulletinBoard {
    public static void main(String[] args){
        try( ServerSocket serverSocket = new ServerSocket(6868)){
            System.out.println("Server is listening on port " + 6868);

            while (true) {
                Thread.sleep(1000);
                Socket socket = serverSocket.accept();
                System.out.println("New client connected");

                new ULPBulletinBoardThread(socket).start();
            }
        }catch (IOException | InterruptedException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

}
