import java.io.*;
import java.net.Socket;

public class ULPBulletinBoardThread extends Thread{
        private Socket socket;

        public ULPBulletinBoardThread(Socket socket) {
            this.socket = socket;
        }

        public void run(){
            try{
                InputStream input = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                OutputStream output = socket.getOutputStream();
                PrintWriter writer = new PrintWriter(output, true);

                writer.println("Board is Connected");

                String text = "start";
                text = reader.readLine();
                System.out.println("Read:"+text);
                if(text.contains("Request")){
                    System.out.println("Write[User]:"+text);
                    writer.println(text);
                    text="Respond";
                    System.out.println("Write[User]:"+text);
                    writer.println(text);
                }else if(text.contains("Respond")){
                    System.out.println("Write[Provider]:"+text);
                    writer.println(text);
                }

                socket.close();
            }catch (IOException ex) {
                System.out.println("Server exception: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
}
