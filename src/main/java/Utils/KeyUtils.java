package Utils;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.security.*;


public class KeyUtils
{
    public static SecretKey createKeyForAES()
            throws NoSuchAlgorithmException, NoSuchProviderException
    {
        SecureRandom random = new SecureRandom();
        KeyGenerator generator = KeyGenerator.getInstance("AES");
        generator.init(128, random);

        return generator.generateKey();
    }

    public static KeyPair createKeyForRSA()
            throws NoSuchAlgorithmException, NoSuchProviderException
    {
        SecureRandom random = new SecureRandom();
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "BC");
        generator.initialize(2048, random);

        return generator.generateKeyPair();
    }
}
