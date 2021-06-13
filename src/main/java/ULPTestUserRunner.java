import Utils.Constants;
import Utils.SocketUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.security.Security;

public class ULPTestUserRunner {
    public static void main(String[] args){
        try{
            for (int i = 0;i<6;i++) {
                System.out.println("Running a new user");
                Thread.sleep(Constants.delay/5);
                new ULPUser().start();
            }
        }catch (InterruptedException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
