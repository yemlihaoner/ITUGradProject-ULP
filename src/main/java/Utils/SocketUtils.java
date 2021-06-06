package Utils;
import javax.net.ssl.*;
import java.io.*;
import java.security.KeyStore;
import java.security.SecureRandom;

public class SocketUtils {
    public static SSLContext getCtx(String trustPath, String keyPath) {
        try{
            final char [] storePassword = new char[]{'1','2','3','4','5','6'};
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            InputStream tstore = SocketUtils.class.getResourceAsStream(trustPath);
            trustStore.load(tstore, storePassword);
            tstore.close();
            TrustManagerFactory tmf = TrustManagerFactory
                    .getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(trustStore);

            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            InputStream kstore = SocketUtils.class.getResourceAsStream(keyPath);
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
