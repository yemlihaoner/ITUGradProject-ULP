package Utils;
import Classes.*;
import Classes.Request.Request;
import Classes.Response.Response;
import Classes.Response.ResponsePartII;
import Classes.Testimonial.Testimonial;
import javax.net.ssl.*;
import java.io.*;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;

//Socket connection related utilities
public class SocketUtils {

    //Generates SSlContext from certificate paths.
    public static SSLContext getSSLContext(String trustPath, String keyPath) {
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

    //Generates SSLSocket from paths and port
    public static SSLSocket getClientSocket(String trustPath, String keyPath,int port) throws IOException {
        SSLContext ctx = SocketUtils.getSSLContext(trustPath,keyPath);
        SSLSocket socket = (SSLSocket) ctx.getSocketFactory().createSocket("localhost", port);
        socket.setEnabledProtocols(Constants.protocols);
        socket.setEnabledCipherSuites(Constants.cipher_suites);
        return socket;
    }

    //Generates SSLServerSocket from paths and port
    public static SSLServerSocket getServerSocket(String trustPath, String keyPath, int port) throws IOException {
        SSLContext ctx = SocketUtils.getSSLContext(trustPath,keyPath);
        SSLServerSocket socket = (SSLServerSocket) ctx.getServerSocketFactory().createServerSocket(port);
        socket.setEnabledCipherSuites(Constants.cipher_suites);
        socket.setEnabledProtocols(Constants.protocols);
        return socket;
    }

    //Special Safe ObjectInputStream readObject to check Mask value requests and receives testimonial on socket.
    public static <T extends Serializable> T getTestimonialObject(ObjectInputStream obj_Input,ObjectOutputStream obj_out,String N,
                                                   PrivateKey privKey,PublicKey boardPubKey,PublicKey providerPubKey) throws Exception {
        Object readObject;
        boolean isNotFound=true;
        do {
            readObject = obj_Input.readObject();
            if (readObject instanceof Testimonial) {
                isNotFound = false;
            }else if(readObject instanceof LostPacket && ((LostPacket) readObject).content.equals("PhaseVI is lost.")){
                Response response = CheckBoard.checkResponse(N,boardPubKey);
                ResponsePartII partII = Cryptography.decryptObjectAES(response.second, privKey, providerPubKey);
                obj_out.writeObject(partII.m);
            }
        } while (isNotFound);
        return (T) readObject;
    }

    //Safe ObjectInputStream readObject to get testimonial on socket.
    public static <T extends Serializable> T getInputObject(ObjectInputStream obj_Input, String objType)
            throws Exception {
        boolean isNotFound=true;
        Object readObject=null;
        switch (objType) {
            case "Request":
                do {
                    readObject = obj_Input.readObject();
                    if (readObject instanceof Request) {
                        isNotFound = false;
                    }
                } while (isNotFound);
                break;
            case "Response":
                do {
                    readObject = obj_Input.readObject();
                    if (readObject instanceof Response) {
                        isNotFound = false;
                    }
                } while (isNotFound);
                break;
            case "Testimonial":
                do {
                    readObject = obj_Input.readObject();
                    if (readObject instanceof Testimonial) {
                        isNotFound = false;
                    }
                } while (isNotFound);
                break;
            case "PublicKey":
                do {
                    readObject = obj_Input.readObject();
                    if (readObject instanceof PublicKey) {
                        isNotFound = false;
                    }
                } while (isNotFound);
                break;
            case "Role":
                do {
                    readObject = obj_Input.readObject();
                    if (readObject instanceof Constants.Role) {
                        isNotFound = false;
                    }
                } while (isNotFound);
                break;
        }
        return (T) readObject;
    }

    //Safe ObjectInputStream readObject to get phase objects on socket.
    public static <T extends Serializable> T getPhaseObject(ObjectInputStream obj_Input, String objType, PrivateKey privKey, PublicKey pubKey) throws Exception {
        boolean isNotFound=true;
        Object readObject;
        Object phase = null;
        switch (objType) {
            case "PhaseIII":
                do {
                    readObject = obj_Input.readObject();
                    if (readObject instanceof EncryptData) {
                        phase = Cryptography.decryptObjectAES((EncryptData) readObject, privKey, pubKey);
                        if (phase instanceof PhaseIII) {
                            isNotFound = false;
                        }
                    }
                } while (isNotFound);
                break;
            case "PhaseVI":
                do {
                    readObject = obj_Input.readObject();
                    if (readObject instanceof EncryptData) {
                        phase = Cryptography.decryptObjectAES((EncryptData) readObject, privKey, pubKey);
                        if (phase instanceof PhaseVI) {
                            isNotFound = false;
                        }
                    }
                } while (isNotFound);
                break;
            case "PhaseIX":
                do {
                    readObject = obj_Input.readObject();
                    if (readObject instanceof EncryptData) {
                        phase = Cryptography.decryptObjectAES((EncryptData) readObject, privKey, pubKey);
                        if (phase instanceof PhaseIX) {
                            isNotFound = false;
                        }
                    }
                } while (isNotFound);
                break;
            case "M":
                do {
                    readObject = obj_Input.readObject();
                    if (readObject instanceof byte[]) {
                        phase=readObject;
                        isNotFound=false;
                    }
                } while (isNotFound);
                break;
        }

        return (T) phase;
    }
}
