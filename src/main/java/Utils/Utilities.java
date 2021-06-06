/*
import javax.xml.bind.DatatypeConverter;



public class Utilities {
    /*
     * Converts a byte to hex digit and writes to the supplied buffer
     */
/*
    public static String toHexString(byte[] block) {
        StringBuilder strBuilder = new StringBuilder();
        for(byte val : block) {
            strBuilder.append(String.format("%02x", val&0xff));
        }
        return strBuilder.toString();
    }
    public static byte[] toByteArray( String block) {
        byte[] val = new byte[block.length() / 2];
        for (int i = 0; i < val.length; i++) {
            int index = i * 2;
            int j = Integer.parseInt(block.substring(index, index + 2), 16);
            val[i] = (byte) j;
        }
        return val;
    }

    public static String toHexString(byte[] array) {
        return DatatypeConverter.printHexBinary(array);
    }

    public static byte[] toByteArray(String s) {
        return DatatypeConverter.parseHexBinary(s);
    }
}
*/