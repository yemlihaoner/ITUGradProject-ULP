package Utils;
import Classes.Request.Request;
import Classes.Response.Response;
import Classes.SubLog;
import Classes.Testimonial.Testimonial;
import Classes.VerifierAnswer;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.security.PublicKey;


//Operations related to checking logs reported by bulletin board.
//A line is written in logs as:
//Time  |   N  |   Object   |   Signature
public class CheckBoard {
    //Verifiers call this function in order to verify signed log.
    public static VerifierAnswer checkVerifier(PublicKey publicKey,SubLog subLog) {
        try {
            Thread.sleep(500);
            URL url = new URL("http://localhost:8080");
            URLConnection con = url.openConnection();
            InputStream is =con.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;

            while (!(line = br.readLine()).startsWith("<td>")) {}

            line = line.replace("<td>","");
            String[] split_line = line.split("</td>");
            SubLog sub_log = new SubLog(split_line);
            if(subLog!=null && subLog.time.equals(sub_log.time))
                return null;
            boolean isVerified = SignatureUtils.verify(SerializationUtils.serialize(sub_log),split_line[3],publicKey);
            return new VerifierAnswer(sub_log,isVerified);
        }catch (Exception e){
            return null;
        }

    }

    //Users call this function to get N value from signed request log.
    public static String checkNForRequest(Request req, PublicKey publicKey) throws Exception {
        Thread.sleep(500);
        URL url = new URL("http://localhost:8080");
        URLConnection con = url.openConnection();
        InputStream is =con.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        String line;

        while ((line = br.readLine()) != null) {
            if(line.contains("<td>")){
                line = line.replace("<td>","");
                String[] split_line = line.split("</td>");
                if(SerializationUtils.serialize(req).equals(split_line[2])){
                    SubLog sub_log = new SubLog(split_line);
                    boolean isVerified = SignatureUtils.verify(SerializationUtils.serialize(sub_log),split_line[3],publicKey);
                    if(isVerified)
                        return sub_log.N;
                }
            }
        }
        return null;
    }

    //Providers call this function to get request object regarding of the
    //N value that comes from user's interaction of phaseIII.
    public static Request checkRequest(String N, PublicKey publicKey) throws Exception {
        Thread.sleep(500);
        URL url = new URL("http://localhost:8080");
        URLConnection con = url.openConnection();
        InputStream is =con.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line;

        while ((line = br.readLine()) != null) {
            if(line.contains("<td>")){
                line = line.replace("<td>","");
                String[] split_line = line.split("</td>");
                if(split_line[1].equals(N)){
                    SubLog sub_log = new SubLog(split_line);
                    boolean isVerified = SignatureUtils.verify(SerializationUtils.serialize(sub_log),split_line[3],publicKey);
                    if(isVerified)
                        return SerializationUtils.deserialize(sub_log.object);
                }
            }
        }
        return null;
    }

    //Users call this function to get response object regarding of the
    //N value after take signal of phaseVI from provider.
    public static Response checkResponse(String N, PublicKey publicKey) throws Exception {
        Thread.sleep(500);
        URL url = new URL("http://localhost:8080");
        URLConnection con = url.openConnection();
        InputStream is =con.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line;

        while ((line = br.readLine()) != null) {
            if(line.contains("<td>")){
                line = line.replace("<td>","");
                String[] split_line = line.split("</td>");
                if(split_line[1].equals(N)){
                    SubLog sub_log = new SubLog(split_line);
                    boolean isVerified = SignatureUtils.verify(SerializationUtils.serialize(sub_log),split_line[3],publicKey);
                    if(isVerified){
                        Object deserialized = SerializationUtils.deserialize(sub_log.object);
                        if(deserialized instanceof Response){
                            return (Response)deserialized;
                        }
                    }

                }

            }
        }
        return null;
    }

    //Mostly users, and for the alternative scenario providers, call this function to check
    //if testimonial is successfully signed and published regarding of N value.
    public static Testimonial checkTestimonial(String N, PublicKey publicKey) throws Exception {
        Thread.sleep(500);
        URL url = new URL("http://localhost:8080");
        URLConnection con = url.openConnection();
        InputStream is =con.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line;

        while ((line = br.readLine()) != null) {
            if(line.contains("<td>")){
                line = line.replace("<td>","");
                String[] split_line = line.split("</td>");
                if(split_line[1].equals(N)){
                    SubLog sub_log = new SubLog(split_line);
                    boolean isVerified = SignatureUtils.verify(SerializationUtils.serialize(sub_log),split_line[3],publicKey);
                    if(isVerified){
                        Object deserialized = SerializationUtils.deserialize(sub_log.object);
                        if(deserialized instanceof Testimonial){
                            return (Testimonial)deserialized;
                        }
                    }

                }
            }
        }
        return null;
    }
}
