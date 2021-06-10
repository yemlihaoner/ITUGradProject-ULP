package Utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Base64;

public class SerializationUtils {

    public static <T extends Serializable> String serialize(T item) {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        final ObjectOutputStream objectOutputStream;
        try {
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(item);
            objectOutputStream.close();
            return Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
        } catch (IOException e) {
            throw new Error(e);
        }
    }

    public static <T extends Serializable> T deserialize(String data) {
        try {
            byte[] dataBytes = Base64.getDecoder().decode(data);
            final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(dataBytes);
            final ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);

            final T obj = (T) objectInputStream.readObject();

            objectInputStream.close();
            return obj;
        } catch (IOException e) {
            throw new Error(e);
        } catch (ClassNotFoundException e) {
            throw new Error(e);
        }
    }

}