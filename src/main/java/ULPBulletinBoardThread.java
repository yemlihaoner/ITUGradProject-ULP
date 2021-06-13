import Classes.Log;
import Classes.PhaseIX;
import Classes.Request.Request;
import Classes.Request.RequestPartII;
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
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

//Users and Providers connects to Bulletin Board. Exchanges keys and publishes signed logs.
public class ULPBulletinBoardThread extends Thread {
    private final Socket socket;
    private final KeyPair pair;

    public ULPBulletinBoardThread(Socket socket, KeyPair keyPair
    ) {
        this.socket = socket;
        this.pair = keyPair;
    }
    Testimonial testimonial;
    boolean isFromProvider=false;
    boolean isNotEOF=true;

    public void run() {
        try {
            //Asymmetric RSA keys pubKey and privKey are initialized.
            PublicKey pubKey = pair.getPublic();
            PrivateKey privKey = pair.getPrivate();
            PublicKey userPubKey;
            PublicKey providerPubKey;

            //Socket read and write variables are prepared.
            InputStream input = socket.getInputStream();
            OutputStream output = socket.getOutputStream();
            ObjectOutputStream obj_Writer = new ObjectOutputStream(output);
            ObjectInputStream obj_Input = new ObjectInputStream(input);

            Date date;
            Log log;

            Constants.Role type = SocketUtils.getInputObject(obj_Input, "Role");
            if (type.equals(Constants.Role.User)){
                obj_Writer.writeObject(pubKey);
                userPubKey = SocketUtils.getInputObject(obj_Input, "PublicKey");
                providerPubKey = SocketUtils.getInputObject(obj_Input, "PublicKey");
                System.out.println("Keys are exchanged");

                Request req = SocketUtils.getInputObject(obj_Input, "Request");
                RequestPartII req_partII = Cryptography.decryptObjectAES(req.second, privKey, userPubKey);
                date = FuncUtils.getDate();
                String N = UUID.randomUUID().toString();
                SubLog req_log = new SubLog(date, SerializationUtils.serialize(req), N);

                log = new Log(
                        req_log.time, req_log.object, req_log.N,
                        SignatureUtils.sign(SerializationUtils.serialize(req_log), privKey));
                updateFile(log);

                System.out.println("Write[User]: Request");

                try {
                    testimonial = SocketUtils.getTestimonialObject(obj_Input, obj_Writer, N, privKey, pubKey, providerPubKey);
                    TestimonialPartII tes_partII = Cryptography.decryptObjectAES(testimonial.second, privKey, userPubKey);

                    if (!FuncUtils.isDateValid(tes_partII.date) || !tes_partII.R_ksb.equals(req_partII.R_ksb))
                        return;

                    date = FuncUtils.getDate();
                    SubLog tes_log = new SubLog(date, SerializationUtils.serialize(testimonial), N);
                    log = new Log(
                            tes_log.time, tes_log.object, tes_log.N,
                            SignatureUtils.sign(SerializationUtils.serialize(tes_log), privKey));
                    updateFile(log);
                    System.out.println("Write[User]: Testimonial");
                }catch (EOFException e){
                    System.out.println("Server is closed service");
                }

            }
            else if (type.equals(Constants.Role.Provider)){

                obj_Writer.writeObject(pubKey);
                providerPubKey = SocketUtils.getInputObject(obj_Input, "PublicKey");
                userPubKey = SocketUtils.getInputObject(obj_Input, "PublicKey");

                Response response = SocketUtils.getInputObject(obj_Input, "Response");
                ResponsePartII partII = Cryptography.decryptObjectAES(response.second, privKey, providerPubKey);
                Request req = CheckBoard.checkRequest(partII.n, pubKey);
                RequestPartII req_partII = Cryptography.decryptObjectAES(req.second, privKey, userPubKey);

                if (!FuncUtils.isDateValid(partII.date) || !partII.R_ksb.equals(req_partII.R_ksb))
                    return;

                date = FuncUtils.getDate();
                SubLog res_log = new SubLog(date, SerializationUtils.serialize(response), partII.n);

                log = new Log(
                        res_log.time, res_log.object, res_log.N,
                        SignatureUtils.sign(SerializationUtils.serialize(res_log), privKey));
                updateFile(log);
                System.out.println("Write[Provider]: Response");
                try {
                    TimeOutRunner.runWithTimeout(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                while (testimonial==null && isNotEOF){
                                    try {
                                        TimeOutRunner.runWithTimeout(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    testimonial = SocketUtils.getInputObject(obj_Input,"Testimonial");
                                                    isFromProvider=true;
                                                }
                                                catch (InterruptedException e) {
                                                    System.out.println("Interrupted: "+e.getMessage());

                                                }catch (EOFException e) {
                                                    //EOF for provider testimonial check.
                                                    isNotEOF=false;
                                                } catch (Exception e) {
                                                    //System.out.println("Exception: "+e.getMessage());
                                                    e.printStackTrace();
                                                }
                                            }
                                        }, 15, TimeUnit.SECONDS);
                                    }
                                    catch (TimeoutException e) {
                                        //Timeout for provider testimonial check.
                                        System.out.println("Timeput: "+e.getMessage());
                                        testimonial = CheckBoard.checkTestimonial(partII.n,pubKey);
                                        isFromProvider=false;
                                    }
                                    catch (EOFException e) {
                                        //Timeout for provider testimonial check.
                                        System.out.println("Timeput: "+e.getMessage());
                                        isNotEOF=false;
                                    }
                                }
                            }
                            catch (InterruptedException e) {
                                System.out.println("Interrupted: "+e.getMessage());

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, 2, TimeUnit.MINUTES);
                }
                catch (TimeoutException e) {
                    //Timeout for user.
                    //Run alternative scenario
                    System.out.println("Timeput: "+e.getMessage());
                }
                if(isFromProvider){
                    TestimonialPartII tes_partII = Cryptography.decryptObjectAES(testimonial.second, privKey, providerPubKey);

                    if (!FuncUtils.isDateValid(tes_partII.date) || !tes_partII.R_ksb.equals(req_partII.R_ksb))
                        return;

                    date = FuncUtils.getDate();
                    SubLog tes_log = new SubLog(date, SerializationUtils.serialize(testimonial), partII.n);
                    log = new Log(
                            tes_log.time, tes_log.object, tes_log.N,
                            SignatureUtils.sign(SerializationUtils.serialize(tes_log), privKey));
                    updateFile(log);
                    System.out.println("Write[Provider]: Testimonial");
                }
            }else if (type.equals(Constants.Role.Verifier)){
                obj_Writer.writeObject(pubKey);
                System.out.println("Public Key is sent to Verifier");
            }

            System.out.println("Operation successful. Socket is closing...");
            socket.close();
        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    private void updateFile(Log log) throws IOException {
        BufferedReader file = new BufferedReader(new FileReader("./src/main/index.html"));
        StringBuilder inputBuffer = new StringBuilder();
        String line;

        while ((line = file.readLine()) != null) {
            inputBuffer.append(line);
            inputBuffer.append('\n');
        }
        file.close();
        String inputStr = inputBuffer.toString();
        int endIndex = inputStr.indexOf("</tr>")+5;
        String row =
                "\n<tr>\n<td>" + Constants.dateFormatter.format(log.time) + "</td>" +
                        "<td>" + log.N + "</td>" +
                        "<td>" + log.object + "</td>" +
                        "<td>" + log.content + "</td>\n</tr>";
        String newFileContent = inputStr.substring(0,endIndex)+row+inputStr.substring(endIndex);
        FileOutputStream fileOut = new FileOutputStream("./src/main/index.html");
        fileOut.write(newFileContent.getBytes());
        fileOut.close();
    }
}