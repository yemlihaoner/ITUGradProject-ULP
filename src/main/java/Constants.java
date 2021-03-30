import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;

public class Constants {
    public static String html_before="<!doctype html>\n" +
            "<html lang=\"en\">\n" +
            "\n" +
            "<head>\n" +
            "    <!-- Required meta tags -->\n" +
            "    <meta charset=\"utf-8\">\n" +
            "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1, shrink-to-fit=no\">\n" +
            "    <title>Bulletin Board Logs</title>\n" +
            "    <style>\n" +
            "        table{\n" +
            "            margin-top: 5%;\n" +
            "            margin-left: 10%;\n" +
            "        }\n" +
            "        table,\n" +
            "        th,\n" +
            "        td {\n" +
            "            padding: 10px;\n" +
            "            border: 1px solid black;\n" +
            "            border-collapse: collapse;\n" +
            "        }\n" +
            "        .header-pad{padding-left: 10%}\n" +
            "    </style>\n" +
            "</head>\n" +
            "<body>\n" +
            "\n" +
            "<h1 class=\"header-pad\">Bulletin Board Log Records</h1>\n" +
            "<table>\n" +
            "    <tr>\n" +
            "        <th>Time</th>\n" +
            "        <th>Type</th>\n" +
            "        <th>Status</th>\n" +
            "        <th>Log</th>\n" +
            "    </tr>";
    public static String html_after="\n" +
            "</table>\n" +
            "</body>\n" +
            "</html>";
    public static SimpleDateFormat dateFormatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss");

    public static boolean checkBoard(String log_to_check) throws IOException, InterruptedException {
        Thread.sleep(500);

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
                    if(split_line[3].contains(log_to_check)){
                        System.out.println(split_line[3] + " is Found!");
                        return true;
                    }
                }
            }
            //System.exit(0);
            return false;
    }

}
