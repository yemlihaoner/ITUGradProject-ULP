package Utils;
import Classes.EncryptData;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.Serializable;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;

public class Cryptography {
    private static final String initVector = "encryptionIntVec";
    private static final String rsaType = "RSA/ECB/PKCS1Padding";

    public static <T extends Serializable> EncryptData encryptObjectAES(T part, Key sharedKey, PrivateKey key, PublicKey socketPubKey) throws Exception {
        byte[] esn = Cryptography.encryptAES(SerializationUtils.serialize(part),sharedKey);
        byte[] enca = Cryptography.encryptRSA(sharedKey.getEncoded(),key);
        byte[] enca1 = new byte[240] ;
        byte[] enca2 = new byte[enca.length-240];

        System.arraycopy(enca, 0, enca1, 0, enca1.length);
        System.arraycopy(enca, enca1.length, enca2, 0, enca2.length);

        byte[] eepa1 = Cryptography.encryptRSA(enca1,socketPubKey);
        byte[] eepa2 = Cryptography.encryptRSA(enca2,socketPubKey);
        return new EncryptData(esn,eepa1,eepa2);
    }

    public static <T extends Serializable> T decryptObjectAES(EncryptData part, PrivateKey key, PublicKey socketPubKey) throws Exception {
        byte[] enca1 = Cryptography.decryptRSA(part.eepa1,key);
        byte[] enca2 = Cryptography.decryptRSA(part.eepa2,key);
        byte[] enca = new byte[enca1.length + enca2.length];

        System.arraycopy(enca1, 0, enca, 0, enca1.length);
        System.arraycopy(enca2, 0, enca, enca1.length, enca2.length);

        Key sharedKey = new SecretKeySpec(Cryptography.decryptRSA(enca,socketPubKey),"AES");
        return SerializationUtils.deserialize(Cryptography.decryptAES(part.esn,sharedKey));
    }

    public static byte[] encryptAES (String plainText, Key key ) throws Exception
    {
        IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding","BC");
        cipher.init(Cipher.ENCRYPT_MODE, key,iv);
        return  cipher.doFinal(plainText.getBytes());
    }

    public static String decryptAES (byte[] cipherTextArray, Key key) throws Exception
    {
        IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding","BC");
        cipher.init(Cipher.DECRYPT_MODE, key,iv);
        byte[] decryptedTextArray = cipher.doFinal(cipherTextArray);
        return new String(decryptedTextArray);
    }

    public static byte[] encryptRSA (byte[] textInBytes, Key key) throws Exception
    {
        Cipher cipher = Cipher.getInstance(rsaType,"BC");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(textInBytes);
    }

    public static byte[] decryptRSA (byte[] cipherTextArray, Key key) throws Exception
    {
        Cipher cipher = Cipher.getInstance(rsaType,"BC");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(cipherTextArray);
    }

}
