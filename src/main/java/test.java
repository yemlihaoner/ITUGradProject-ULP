import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
public class test {

    public static void main(String[] args) throws IOException {
        URL url = new URL("http://localhost:8080");

        URLConnection con = url.openConnection();
        InputStream is =con.getInputStream();

        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        String line = null;

        while ((line = br.readLine()) != null) {
            if(line.contains("<td>")){
                line = line.replace("<td>","");
                var split_line = line.split("</td>");
                System.out.println(split_line[0]+split_line[1]+split_line[2]+split_line[3]);
            }
        }
        System.exit(0);
    }
}