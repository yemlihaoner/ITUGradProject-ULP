package Utils;
import java.text.SimpleDateFormat;

//Constant values that are used by many piece of codes.
public class Constants {
    public static final int delay = 1000;                       //1 second delay for timeouts or Thread.sleep() in millis
    public enum Comment {Success, Error, Timeout, NoService}    //Comment types in Testimonial as enum.
    public enum Role {BulletinBoard, Provider, User, Verifier}  //Role types in BulletinBoard interactions.

    //Date format to print date data on log publishes
    public static SimpleDateFormat dateFormatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss");

    //Supported protocols by SSL socket
    public static final String[] protocols = new String[] {"TLSv1","TLSv1.1","TLSv1.2","TLSv1.3"};

    //Supported ciphers by SSL socket
    public static final String[] cipher_suites = new String[] {"TLS_AES_128_GCM_SHA256"};

    //index.html format before logs
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
            "            margin-left: 5%;\n" +
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
            "        <th>TimeStamp</th>\n" +
            "        <th>N</th>\n" +
            "        <th>Log</th>\n" +
            "        <th>Content</th>\n" +
            "    </tr>";

    //index.html format after logs
    public static String html_after="\n" +
            "</table>\n" +
            "</body>\n" +
            "</html>";
}
