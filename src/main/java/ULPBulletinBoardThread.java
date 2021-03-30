import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;

public class ULPBulletinBoardThread extends Thread{
        private Socket socket;
        private ArrayList<Log> logs;

        public ULPBulletinBoardThread(Socket socket, ArrayList<Log> logs) {
            this.socket = socket;
            this.logs=logs;
        }

        public void run(){
            try{
                InputStream input = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                OutputStream output = socket.getOutputStream();
                PrintWriter writer = new PrintWriter(output, true);
                Date date;
                String text = "start";

                writer.println("Board is Connected");

                text = reader.readLine();
                System.out.println("Read:"+text);
                if(text.contains("Request")){
                    date = new Date(System.currentTimeMillis());
                    logs.add(new Log(
                            date,"type-a","status-a","Write[User]:"+text
                    ));
                    System.out.println("Write[User]:"+text);
                    writer.println(text);
                    text="Respond";
                    System.out.println("Write[User]:"+text);
                    logs.add(new Log(
                            date,"type-a","status-a","Write[User]:"+text
                    ));
                    writer.println(text);
                }else if(text.contains("Respond")){
                    date = new Date(System.currentTimeMillis());
                    logs.add(new Log(
                            date,"type-a","status-a","Write[User]:"+text
                    ));
                    System.out.println("Write[Provider]:"+text);
                    writer.println(text);
                }
                PrintWriter outputFile = new PrintWriter("./src/main/index.html");

                outputFile.println(Constants.html_before);
                for(int i =0;i<logs.size();i++){
                    var row =
                    "        <td>"+Constants.dateFormatter.format(logs.get(i).time)+"</td>"+
                    "        <td>"+logs.get(i).type+"</td>"+
                    "        <td>"+logs.get(i).status+"</td>"+
                    "        <td>"+logs.get(i).text+"</td>";

                    outputFile.println("    <tr>");
                    outputFile.println(row);
                    outputFile.println("    </tr>");
                }
                outputFile.println(Constants.html_after);
                outputFile.close();
                socket.close();
            }catch (IOException ex) {
                System.out.println("Server exception: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
}
