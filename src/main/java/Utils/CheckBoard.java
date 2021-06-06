package Utils;

import Classes.Request.Request;
import Classes.Response.Response;
import Classes.Testimonial.Testimonial;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class CheckBoard {

    public static String checkNForRequest(Request req) throws IOException, InterruptedException {
        Thread.sleep(500);
        URL url = new URL("http://localhost:8080");
        URLConnection con = url.openConnection();
        InputStream is =con.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        String line = null;

        //A line is writen as:
        //Time  |   Object  |   N   |   Content
        while ((line = br.readLine()) != null) {
            if(line.contains("<td>")){
                line = line.replace("<td>","");
                var split_line = line.split("</td>");
                //System.out.println(split_line[0]+split_line[1]+split_line[2]+split_line[3]);
                String base64 =split_line[2].replaceAll(" ","");
                if(SerializationUtils.serialize(req).equals(base64)){
                    return split_line[1].replaceAll(" ","");
                }
            }
        }
        return null;
    }
    public static Request checkRequest(String N) throws IOException, InterruptedException {
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
                var split_line = line.split("</td>");
                String base64 =split_line[2].replaceAll(" ","");
                if(split_line[1].contains(N)){
                    Request req = SerializationUtils.deserialize(base64);
                    return req;
                }
            }
        }
        return null;
    }
    public static Response checkResponse(String N) throws IOException, InterruptedException {
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
                var split_line = line.split("</td>");
                String base64 =split_line[2].replaceAll(" ","");

                if(split_line[1].contains(N)){
                    Object deserialized = SerializationUtils.deserialize(base64);
                    if(deserialized instanceof Response){
                        return (Response)deserialized;
                    }
                }

            }
        }
        return null;
    }
    public static Testimonial checkTestimonial(String N) throws IOException, InterruptedException {
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
                var split_line = line.split("</td>");
                //System.out.println(split_line[0]+split_line[1]+split_line[2]+split_line[3]);
                String base64 =split_line[2].replaceAll(" ","");
                if(split_line[1].contains(N)){
                    Object deserialized = SerializationUtils.deserialize(base64);
                    if(deserialized instanceof Testimonial){
                        return (Testimonial)deserialized;
                    }
                }
            }
        }
        return null;
    }

}
