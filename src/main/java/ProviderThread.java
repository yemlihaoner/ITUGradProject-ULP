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

                try{
                    var isFound = Constants.checkBoard("Write[User]:Request");
                    if(isFound){
                        System.out.println("Read[Board]:Request");
                    }
                }catch (InterruptedException e) {
                    e.printStackTrace();
                }

                String requestB = "Respond";
                writerB.println(requestB);
                System.out.println("Write[Board]:"+requestB);

                try{
                    var isFound = Constants.checkBoard("Write[Provider]:Respond");
                    if(isFound){
                        writerP.println(requestB);
                        System.out.println("Signal[User]:Respond");
                    }
                }catch (InterruptedException e) {
                    e.printStackTrace();
                }



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
