package Utils;
import Classes.Request.Request;
import Classes.Response.Response;
import Classes.SubLog;
import Classes.Testimonial.Testimonial;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.security.PublicKey;
import java.text.ParseException;
import java.util.Date;

public class CheckBoard {

    private static SubLog getSubLog(String[] split_line) throws ParseException {
        String s_Log = split_line[2].replaceAll("  ","");
        String s_N = split_line[1].replaceAll("  ","");
        String s_Date = split_line[0].replaceAll("  ","");
        Date date = Constants.dateFormatter.parse(s_Date);
        return new SubLog(date,s_Log,s_N);
    }

    public static String checkNForRequest(Request req, PublicKey publicKey) throws Exception {
        Thread.sleep(500);
        URL url = new URL("http://localhost:8080");
        URLConnection con = url.openConnection();
        InputStream is =con.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        String line;
        //A line is writen as:
        //Time  |   Object  |   N   |   Content
        while ((line = br.readLine()) != null) {
            if(line.contains("<td>")){
                line = line.replace("<td>","");
                String[] split_line = line.split("</td>");
                String s_Signed = split_line[3].replaceAll("  ","");
                String s_Log = split_line[2].replaceAll("  ","");
                if(SerializationUtils.serialize(req).equals(s_Log)){
                    SubLog sub_log = getSubLog(split_line);
                    boolean isVerified = SignatureUtils.verify(SerializationUtils.serialize(sub_log),s_Signed,publicKey);
                    if(isVerified)
                        return sub_log.N;
                }
            }
        }
        return null;
    }

    public static Request checkRequest(String N, PublicKey publicKey) throws Exception {
        Thread.sleep(500);
        URL url = new URL("http://localhost:8080");
        URLConnection con = url.openConnection();
        InputStream is =con.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line;

        //A line is writen as:
        //Time  |   Object  |   N   |   Content
        while ((line = br.readLine()) != null) {
            if(line.contains("<td>")){
                line = line.replace("<td>","");
                String[] split_line = line.split("</td>");
                if(split_line[1].contains(N)){
                    SubLog sub_log = getSubLog(split_line);
                    String s_Signed = split_line[3].replaceAll("  ","");
                    boolean isVerified = SignatureUtils.verify(SerializationUtils.serialize(sub_log),s_Signed,publicKey);
                    if(isVerified)
                        return SerializationUtils.deserialize(sub_log.object);
                }
            }
        }
        return null;
    }


    public static Response checkResponse(String N, PublicKey publicKey) throws Exception {
        Thread.sleep(500);
        URL url = new URL("http://localhost:8080");
        URLConnection con = url.openConnection();
        InputStream is =con.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line;

        //A line is writen as:
        //Time  |   Object  |   N   |   Content
        while ((line = br.readLine()) != null) {
            if(line.contains("<td>")){
                line = line.replace("<td>","");
                String[] split_line = line.split("</td>");
                if(split_line[1].contains(N)){
                    SubLog sub_log = getSubLog(split_line);
                    String s_Signed = split_line[3].replaceAll("  ","");
                    boolean isVerified = SignatureUtils.verify(SerializationUtils.serialize(sub_log),s_Signed,publicKey);
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
    public static Testimonial checkTestimonial(String N, PublicKey publicKey) throws Exception {
        Thread.sleep(500);
        URL url = new URL("http://localhost:8080");
        URLConnection con = url.openConnection();
        InputStream is =con.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line;

        //A line is writen as:
        //Time  |   Object  |   N   |   Content
        while ((line = br.readLine()) != null) {
            if(line.contains("<td>")){
                line = line.replace("<td>","");
                String[] split_line = line.split("</td>");
                if(split_line[1].contains(N)){
                    SubLog sub_log = getSubLog(split_line);
                    String s_Signed = split_line[3].replaceAll("  ","");
                    boolean isVerified = SignatureUtils.verify(SerializationUtils.serialize(sub_log),s_Signed,publicKey);
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
