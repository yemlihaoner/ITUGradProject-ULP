package Classes.Response;
import Classes.EncryptData;

public class Response implements java.io.Serializable {
    public EncryptData first;           //Provider related part of the response
    public EncryptData second;          //BulletinBoard related part of the response
    public Response(EncryptData partI, EncryptData partII) {
        this.first=partI;
        this.second=partII;
    }
}
