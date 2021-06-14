package Utils;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;
import java.util.*;

//Functional Utilities
public class FuncUtils {
    //Gets current date-time
    public static Date getDate() throws ParseException {
        Date date = new Date(System.currentTimeMillis());
        String date_tmp = Constants.dateFormatter.format(date);
        date = Constants.dateFormatter.parse(date_tmp);
        return date;
    }

    //Checks if log is saved in last 5 min
    public static boolean isDateValid(Date toCompare) {
        Date date_now = new Date(System.currentTimeMillis());
        Date date_minus_5 = new Date(System.currentTimeMillis() - (5 * 60 * 1000));
        return toCompare.before(date_now) && toCompare.after(date_minus_5);
    }
    //Creates a binary salt for mask
    public static byte[] generateMask(){
        Random random = new Random();
        byte[] salt = new byte[400];
        for (int i =0; i<16;i++){
            salt[i] = (byte)random.nextInt(2);
        }
        return salt;
    }

    //Mask salt on Contract
    public static byte[] hashMask(char[] contract, byte[] mask) {
        PBEKeySpec spec = new PBEKeySpec(contract, mask, 1000, 256);
        Arrays.fill(contract, Character.MIN_VALUE);
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            return skf.generateSecret(spec).getEncoded();
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            throw new AssertionError("Error while hashing: " + e.getMessage(), e);
        } finally {
            spec.clearPassword();
        }
    }

    //XOR operations for Contract and Mask
    public static byte[] xor(byte[] b1, byte[] b2) {
        byte[] oneAndTwo = new byte[Math.min(b1.length, b2.length)];
        for (int i = 0; i < oneAndTwo.length; i++) {
            oneAndTwo[i] = (byte) (((int) b1[i]) ^ ((int) b2[i]));
        }
        return oneAndTwo;
    }
}
