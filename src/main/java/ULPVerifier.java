import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class ULPVerifier {
    public static void main(String[] args) {

        try(Socket socket = new Socket("localhost",6868)){

            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output,true);

            Console console =System.console();
            String text = "start";

            while (!text.equals("bye")) {
                text =console.readLine("Enter text: ");
                writer.println(text);

                InputStream input = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                String time = reader.readLine();

                System.out.println(time);
            }
            socket.close();
        }
        catch (UnknownHostException ex) {
            System.out.println("Server not found: " + ex.getMessage());

        } catch (IOException ex) {
            System.out.println("I/O error: " + ex.getMessage());
        }
    }
}
