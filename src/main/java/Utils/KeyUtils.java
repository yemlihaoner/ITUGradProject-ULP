package Utils;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.*;

//Key generations
public class KeyUtils
{
    //Generates a 128bit symmetric AES key with BouncyCastle
    public static SecretKey createKeyForAES() throws NoSuchAlgorithmException, NoSuchProviderException
    {
        SecureRandom random = new SecureRandom();
        KeyGenerator generator = KeyGenerator.getInstance("AES" ,"BC");
        generator.init(128, random);

        return generator.generateKey();
    }

    //Generates a 2048bit asymmetric RSA key with BouncyCastle
    public static KeyPair createKeyForRSA() throws NoSuchAlgorithmException, NoSuchProviderException
    {
        SecureRandom random = new SecureRandom();
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "BC");
        generator.initialize(2048, random);

        return generator.generateKeyPair();
    }
}
