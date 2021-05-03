import javax.crypto.KeyAgreement;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;
import java.io.*;
import java.net.Socket;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class ULPBulletinBoardThread extends Thread{
        private Socket socket;
        private ArrayList<Log> logs;
        private ArrayList<LogKeyPair> logKeys;

        public ULPBulletinBoardThread(Socket socket, ArrayList<Log> logs,ArrayList<LogKeyPair> logKeys) {
            this.socket = socket;
            this.logs=logs;
            this.logKeys=logKeys;
        }

        public void run(){
            try{
                InputStream input = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                OutputStream output = socket.getOutputStream();
                PrintWriter writer = new PrintWriter(output,true);

                Date date;
                String text;

                String type = reader.readLine();
                if(type.equals("User")){

                    String userReadStr = reader.readLine();
                    byte [] userRead = userReadStr.getBytes();              //Get User Public Key
                    System.out.println("BOARD: "+userRead.toString());

                    X509EncodedKeySpec ks = new X509EncodedKeySpec(userRead);      //Decode recived key
                    KeyFactory kf = KeyFactory.getInstance("RSA");
                    PublicKey userPublicKey = kf.generatePublic(ks);
                    System.out.println("BOARD: "+userPublicKey.toString());

                    DHParameterSpec dhParamShared = ((DHPublicKey)userPublicKey).getParams();

                    System.out.println("BOARD: Generate DH keypair ...");
                    KeyPairGenerator boardKpairGen = KeyPairGenerator.getInstance("DH");
                    boardKpairGen.initialize(dhParamShared);
                    KeyPair boardKpair = boardKpairGen.generateKeyPair();

                    System.out.println("BOARD: Initialize ...");
                    KeyAgreement boardKeyAgree = KeyAgreement.getInstance("DH");
                    boardKeyAgree.init(boardKpair.getPrivate());


                    Key ubKey = boardKeyAgree.doPhase(userPublicKey, false);
                    logKeys.add(new LogKeyPair(userRead.toString(),Arrays.toString(boardKpair.getPublic().getEncoded()),Arrays.toString(ubKey.getEncoded())));   //Save UP Key into logKeys

                    byte [] upRead = reader.readLine().getBytes();              //Get UP Key

                    ks = new X509EncodedKeySpec(upRead);                       //Decode recived key
                    kf = KeyFactory.getInstance("RSA");
                    PublicKey upKey = kf.generatePublic(ks);

                    boardKeyAgree.doPhase(upKey, true);

                    byte[] sharedSecret = boardKeyAgree.generateSecret();
                    System.out.println("SharedSecret: "+ Arrays.toString(sharedSecret));



                    text = reader.readLine();
                    System.out.println("Read:"+text);

                    date = new Date(System.currentTimeMillis());

                    logs.add(new Log(
                            date,"type-a","status-a","Write[User]:"+text
                    ));
                    System.out.println("Write[User]:"+text);

                    text = reader.readLine();
                    System.out.println("Read:"+text);

                    logs.add(new Log(
                            date,"type-a","status-a","Write[User]:"+text
                    ));
                    System.out.println("Write[User]:"+text);
                }else if (type.startsWith("Board")){
                    String userKey = type.substring(5);
                    LogKeyPair logKey = null;
                    for (LogKeyPair logkey:logKeys
                         ) {
                        if(logkey.userKey.equals(userKey)){
                            logKey=logkey;
                            break;
                        }
                    }
                    if(logKey==null)return;
                    writer.println(logKey.boardKey);                            //Send Board public key to Provider
                    writer.println(logKey.UBKey);                            //Send Board public key to Provider


                    text = reader.readLine();
                    System.out.println("Read:"+text);

                    date = new Date(System.currentTimeMillis());
                    logs.add(new Log(
                            date,"type-a","status-a","Write[Provider]:"+text
                    ));
                    System.out.println("Write[Provider]:"+text);
                }
                PrintWriter outputFile = new PrintWriter("./src/main/index.html");



                outputFile.println(Constants.html_before);
                for (Log log : logs) {
                    var row =
                            "        <td>" + Constants.dateFormatter.format(log.time) + "</td>" +
                                    "        <td>" + log.type + "</td>" +
                                    "        <td>" + log.status + "</td>" +
                                    "        <td>" + log.text + "</td>";

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
            }catch (Exception e){
                System.out.println("Exception: " + e.getMessage());
            }
        }
}
