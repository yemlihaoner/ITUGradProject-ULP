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
import java.text.ParseException;
import java.util.Date;

public class CheckBoard {

    private static SubLog getSubLog(String[] split_line) throws ParseException {
        String s_Log = split_line[2];
        String s_N = split_line[1];
        String s_Date = split_line[0];
        Date date = Constants.dateFormatter.parse(s_Date);
        return new SubLog(date,s_Log,s_N);
    }

    public static Date getDate() throws ParseException {
        Date date = new Date(System.currentTimeMillis());
        String date_tmp = Constants.dateFormatter.format(date);
        date = Constants.dateFormatter.parse(date_tmp);
        return date;
    }

    public static boolean isDateValid(Date toCompare) {
        Date date_now = new Date(System.currentTimeMillis());
        Date date_minus_5 = new Date(System.currentTimeMillis() - (5 * 60 * 1000));
        return toCompare.before(date_now) && toCompare.after(date_minus_5);
    }


    public static VerifierAnswer checkVerifier(PublicKey publicKey,SubLog subLog) throws Exception {
        Thread.sleep(500);
        URL url = new URL("http://localhost:8080");
        URLConnection con = url.openConnection();
        InputStream is =con.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line;

        //A line is writen as:
        //Time  |   Object  |   N   |   Content
        while (!(line = br.readLine()).startsWith("<td>")) {}

        line = line.replace("<td>","");
        String[] split_line = line.split("</td>");
        SubLog sub_log = getSubLog(split_line);
        if(subLog!=null && subLog.time.equals(sub_log.time))
            return null;
        boolean isVerified =SignatureUtils.verify(SerializationUtils.serialize(sub_log),split_line[3],publicKey);
        return new VerifierAnswer(sub_log,isVerified);
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
                if(SerializationUtils.serialize(req).equals(split_line[2])){
                    SubLog sub_log =  getSubLog(split_line);
                    boolean isVerified = SignatureUtils.verify(SerializationUtils.serialize(sub_log),split_line[3],publicKey);
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
                if(split_line[1].equals(N)){
                    SubLog sub_log = getSubLog(split_line);
                    boolean isVerified = SignatureUtils.verify(SerializationUtils.serialize(sub_log),split_line[3],publicKey);
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
                if(split_line[1].equals(N)){
                    SubLog sub_log = getSubLog(split_line);
                    String seri = SerializationUtils.serialize(sub_log);
                    boolean isVerified = SignatureUtils.verify(seri,split_line[3],publicKey);
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
                if(split_line[1].equals(N)){
                    SubLog sub_log = getSubLog(split_line);
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
