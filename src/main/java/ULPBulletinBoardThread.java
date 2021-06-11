import Classes.Log;
import Classes.Request.Request;
import Classes.Response.Response;
import Classes.Response.ResponsePartII;
import Classes.SubLog;
import Classes.Testimonial.Testimonial;
import Classes.Testimonial.TestimonialPartII;
import Utils.*;
import java.io.*;
import java.net.Socket;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class ULPBulletinBoardThread extends Thread{
        private Socket socket;
        private ArrayList<Log> logs;

        public ULPBulletinBoardThread(Socket socket, ArrayList<Log> logs
        ) {
            this.socket = socket;
            this.logs=logs;
        }

        public void run(){
            try{
                KeyPair pair = KeyUtils.createKeyForRSA();
                PublicKey pubKey = pair.getPublic();
                PrivateKey privKey = pair.getPrivate();
                PublicKey userPubKey;
                PublicKey providerPubKey;

                InputStream input = socket.getInputStream();
                OutputStream output = socket.getOutputStream();
                PrintWriter writer = new PrintWriter(output,true);
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                ObjectOutputStream obj_Writer = new ObjectOutputStream(output);
                ObjectInputStream obj_Input = new ObjectInputStream(input);

                Date date;

                String type = reader.readLine();
                if(type.equals("User")){
                    obj_Writer.writeObject(pubKey);
                    userPubKey = SocketUtils.getInputObject(obj_Input,"PublicKey");
                    System.out.println("Keys are exchanged");

                    Request req = SocketUtils.getInputObject(obj_Input,"Request");
                    date = CheckBoard.getDate();
                    String N = UUID.randomUUID().toString();
                    SubLog req_log = new SubLog(date,SerializationUtils.serialize(req),N);

                    logs.add(new Log(
                            req_log.time,req_log.object,req_log.N,
                            SignatureUtils.sign(SerializationUtils.serialize(req_log),privKey)));
                    updateFile(logs);

                    System.out.println("Write[User]: Request");
                    Testimonial testimonial = SocketUtils.getInputObject(obj_Input,"Testimonial");
                    TestimonialPartII partII = Cryptography.decryptObjectAES(testimonial.second,privKey,userPubKey);


                    date = CheckBoard.getDate();
                    SubLog tes_log = new SubLog(date,SerializationUtils.serialize(testimonial),N);


                    logs.add(new Log(
                            tes_log.time,tes_log.object,tes_log.N,
                            SignatureUtils.sign(SerializationUtils.serialize(tes_log),privKey)));
                    updateFile(logs);
                    System.out.println("Write[User]: Testimonial");
                }else if (type.equals("Provider")){
                    obj_Writer.writeObject(pubKey);
                    providerPubKey = SocketUtils.getInputObject(obj_Input,"PublicKey");

                    Response response = SocketUtils.getInputObject(obj_Input,"Response");
                    ResponsePartII partII = Cryptography.decryptObjectAES(response.second,privKey,providerPubKey);

                    date = CheckBoard.getDate();
                    SubLog res_log = new SubLog(date,SerializationUtils.serialize(response),partII.n);
                    String seri = SerializationUtils.serialize(res_log);

                    logs.add(new Log(
                            res_log.time,res_log.object,res_log.N,
                            SignatureUtils.sign(seri,privKey)));
                    updateFile(logs);
                    System.out.println("Write[Provider]: Response");

                }

                System.out.println("Operation successful. Socket is closing...");
                socket.close();
            }catch (IOException ex) {
                System.out.println("Server exception: " + ex.getMessage());
                ex.printStackTrace();
            }catch (Exception e){
                System.out.println("Exception: " + e.getMessage());
            }
        }

    private void updateFile(ArrayList<Log> logs) throws FileNotFoundException {
        PrintWriter outputFile = new PrintWriter("./src/main/index.html");

        outputFile.println(Constants.html_before);
        for (Log log : logs) {
            String row =
                    "<td>" + Constants.dateFormatter.format(log.time) + "</td>" +
                            "<td>" + log.N + "</td>" +
                            "<td>" + log.object + "</td>" +
                            "<td>" + log.content + "</td>";

            outputFile.println("<tr>");
            outputFile.println(row);
            outputFile.println("</tr>");
        }
        outputFile.println(Constants.html_after);
        outputFile.close();
    }
}
