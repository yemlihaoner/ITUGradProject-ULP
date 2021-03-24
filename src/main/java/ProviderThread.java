import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class ProviderThread extends Thread{
    private Socket socketProvider;

    public ProviderThread(Socket socketProvider) {
        this.socketProvider = socketProvider;
    }

    public void run(){
        try{
            InputStream inputP = socketProvider.getInputStream();
            OutputStream outputP = socketProvider.getOutputStream();
            PrintWriter writerP = new PrintWriter(outputP,true);
            BufferedReader readerP = new BufferedReader(new InputStreamReader(inputP));

            String UserRead = readerP.readLine();
            System.out.println("Read[User]: "+UserRead);

            try(Socket socketBoard = new Socket("localhost",6868)){
                InputStream inputB = socketBoard.getInputStream();
                OutputStream outputB = socketBoard.getOutputStream();
                PrintWriter writerB = new PrintWriter(outputB,true);
                BufferedReader readerB = new BufferedReader(new InputStreamReader(inputB));

                String bilboardRead = readerB.readLine();
                System.out.println("Read[Board]: "+bilboardRead);

                String requestB = "Respond";
                writerB.println(requestB);
                System.out.println("Write[Board]: "+requestB);

                bilboardRead = readerB.readLine();
                System.out.println("Read[Board]: "+bilboardRead);

                writerP.println(requestB);
                System.out.println("Write[Board]: "+requestB);

                socketBoard.close();
            }
            catch (UnknownHostException ex) {
                System.out.println("Server not found: " + ex.getMessage());

            } catch (IOException ex) {
                System.out.println("I/O error: " + ex.getMessage());
            }

            socketProvider.close();
        }catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }


}
