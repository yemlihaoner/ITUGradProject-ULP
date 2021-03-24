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

            String boardRead = readerB.readLine();
            boardRead = readerB.readLine();
            System.out.println("Board: "+boardRead);

            try(Socket socketProvider = new Socket("localhost",6800)){
                InputStream inputP = socketProvider.getInputStream();
                OutputStream outputP = socketProvider.getOutputStream();
                PrintWriter writerP = new PrintWriter(outputP,true);
                BufferedReader readerP = new BufferedReader(new InputStreamReader(inputP));


                String requestP = "Request";
                writerP.println(requestP);

                System.out.println(requestP+" is sent to Provider.");

                String providerRead = readerP.readLine();
                System.out.println("Provider: "+providerRead);

                boardRead = readerB.readLine();
                System.out.println("Board: "+boardRead);

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
