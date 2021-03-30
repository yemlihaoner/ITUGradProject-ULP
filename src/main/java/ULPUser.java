import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class ULPUser {
    public static void main(String[] args) {

        try(Socket socketBoard = new Socket("localhost",6868)){
            InputStream inputB = socketBoard.getInputStream();
            OutputStream outputB = socketBoard.getOutputStream();
            PrintWriter writerB = new PrintWriter(outputB,true);
            BufferedReader readerB = new BufferedReader(new InputStreamReader(inputB));


            String requestB = "Request";
            writerB.println(requestB);
            try{
                var isFound = Constants.checkBoard("Write[User]:Request");
                if(isFound){
                    System.out.println("Board: Write Success");
                }
            }catch (InterruptedException e) {
                e.printStackTrace();
            }

            try(Socket socketProvider = new Socket("localhost",6800)){
                InputStream inputP = socketProvider.getInputStream();
                OutputStream outputP = socketProvider.getOutputStream();
                PrintWriter writerP = new PrintWriter(outputP,true);
                BufferedReader readerP = new BufferedReader(new InputStreamReader(inputP));


                String requestP = "Request";
                writerP.println(requestP);

                System.out.println("Signal[Provider]:Request");

                String providerRead = readerP.readLine();
                System.out.println("Read[Provider]:"+providerRead);

                try{
                    var isFound = Constants.checkBoard("Write[Provider]:Respond");
                    if(isFound){
                        System.out.println("Board: Write Success");
                    }
                }catch (InterruptedException e) {
                    e.printStackTrace();
                }

                socketProvider.close();
            }
            catch (UnknownHostException ex) {
                System.out.println("Server not found: " + ex.getMessage());

            } catch (IOException ex) {
                System.out.println("I/O error: " + ex.getMessage());
            }

            socketBoard.close();
        }
        catch (UnknownHostException ex) {
            System.out.println("Server not found: " + ex.getMessage());

        } catch (IOException ex) {
            System.out.println("I/O error: " + ex.getMessage());
        }
    }
}
