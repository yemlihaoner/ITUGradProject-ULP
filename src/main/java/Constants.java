import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;


public class Constants {
    public static final int delay = 1000; // in millis

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
            "        <th>LogID</th>\n" +
            "        <th>Content</th>\n" +
            "    </tr>";
    public static String html_after="\n" +
            "</table>\n" +
            "</body>\n" +
            "</html>";
    public static SimpleDateFormat dateFormatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss");

    public static String checkBoard(String id,String type) throws IOException, InterruptedException {
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
                //System.out.println(split_line[0]+split_line[1]+split_line[2]+split_line[3]);
                var index = type.equals("Request")?3:2;
                if(split_line[1].contains(type) && split_line[index].contains(id) ){
                    System.out.println( split_line[3] + " - " + split_line[1] + " is Found!");
                    return split_line[2].replaceAll(" ","");
                }
            }
        }
        //System.exit(0);
        return null;
    }

    public static SSLContext getCtx(String trustPath, String keyPath) {
        try{
            final char [] storePassword = new char[]{'1','2','3','4','5','6'};
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            InputStream tstore = Constants.class.getResourceAsStream(trustPath);
            trustStore.load(tstore, storePassword);
            tstore.close();
            TrustManagerFactory tmf = TrustManagerFactory
                    .getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(trustStore);

            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            InputStream kstore = Constants.class.getResourceAsStream(keyPath);
            keyStore.load(kstore, storePassword);
            KeyManagerFactory kmf = KeyManagerFactory
                    .getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(keyStore, storePassword);
            SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(),
                    SecureRandom.getInstanceStrong());
            return ctx;
        }
        catch (Exception ex){
            System.out.println("Key GTX exception: " + ex.getMessage());
            ex.printStackTrace();
        }
        return null;
    }

    public static final String[] protocols = new String[] {"TLSv1","TLSv1.1","TLSv1.2","TLSv1.3"};
    public static final String[] cipher_suites = new String[] {"TLS_AES_128_GCM_SHA256"};


    public static SSLSocket getClientSocket(SSLContext ctx, int port) throws IOException {
        SSLSocket socket = (SSLSocket) ctx.getSocketFactory().createSocket("localhost", port);
        socket.setEnabledProtocols(Constants.protocols);
        socket.setEnabledCipherSuites(Constants.cipher_suites);
        return socket;
    }

    public static SSLServerSocket getServerSocket(SSLContext ctx, int port) throws IOException {
        SSLServerSocket socket = (SSLServerSocket) ctx.getServerSocketFactory().createServerSocket(port);
        socket.setEnabledCipherSuites(Constants.cipher_suites);
        socket.setEnabledProtocols(Constants.protocols);
        return socket;
    }
}
