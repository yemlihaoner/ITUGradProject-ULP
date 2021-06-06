import Classes.Log;
import Classes.Request.Request;
import Classes.Response.Response;
import Classes.Testimonial.Testimonial;
import Utils.Constants;
import Utils.SerializationUtils;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class ULPBulletinBoardThread extends Thread{
        private Socket socket;
        private ArrayList<Log> logs;
      //  private ArrayList<LogKeyPair> logKeys;

        public ULPBulletinBoardThread(Socket socket, ArrayList<Log> logs
        //        ,ArrayList<LogKeyPair> logKeys
        ) {
            this.socket = socket;
            this.logs=logs;
       //     this.logKeys=logKeys;
        }

        public void run(){
            try{
                InputStream input = socket.getInputStream();
                OutputStream output = socket.getOutputStream();
                PrintWriter writer = new PrintWriter(output,true);
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                ObjectOutputStream obj_WriterP = new ObjectOutputStream(output);
                ObjectInputStream obj_InputP = new ObjectInputStream(input);

                Date date;
                String text,id;

                String type = reader.readLine();
                if(type.equals("User")){

                    /*
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


                    */
                    Request req = (Request) obj_InputP.readObject();

                    /*text = reader.readLine();
                    while(!text.equals("Request")){
                        Thread.sleep(Constants.delay);
                        text = reader.readLine();
                    }
                    */
                    date = new Date(System.currentTimeMillis());
                    String N = UUID.randomUUID().toString();
                    /*logs.add(new Log(
                            req.first.date, req.first.proposal.accessType,N,req.second.R_ksb
                    ));*/

                    logs.add(new Log(
                            date, SerializationUtils.serialize(req),N,"encrypted"
                    ));
                    updateFile(logs);
                    System.out.println("Write[User]:"+req.toString());

                    Testimonial testimonial = (Testimonial) obj_InputP.readObject();

                    //text = reader.readLine();
                    //while(!text.equals("Testimonial")){
                    //    Thread.sleep(Constants.delay);
                    //    text = reader.readLine();
                    //}
                    //System.out.println("Read:"+text);
                    date = new Date(System.currentTimeMillis());

                    logs.add(new Log(
                            date,SerializationUtils.serialize(testimonial),N,"encrypted"
                    ));
                    updateFile(logs);

                    //System.out.println("Write[User]:"+text);
                }else if (type.equals("Provider")){
                    /*
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
                    */
                    Response response = (Response) obj_InputP.readObject();

                    System.out.println("Read: "+"Response"+" ID: "+response.second.n);

                    date = new Date(System.currentTimeMillis());
                    /*logs.add(new Log(
                            date,"Response",response.second.n,response.second.R_ksb
                    ));*/

                    logs.add(new Log(
                            date, SerializationUtils.serialize(response),response.second.n,"encrypted"
                    ));
                    updateFile(logs);
                }

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
            var row =
                    "        <td>" + Constants.dateFormatter.format(log.time) + "</td>" +
                            "        <td>" + log.N + "</td>" +
                            "        <td>" + log.object + "</td>" +
                            "        <td>" + log.content + "</td>";

            outputFile.println("    <tr>");
            outputFile.println(row);
            outputFile.println("    </tr>");
        }
        outputFile.println(Constants.html_after);
        outputFile.close();
    }
}
