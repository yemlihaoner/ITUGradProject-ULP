package Classes.Request;
import Classes.EncryptData;

public class Request implements java.io.Serializable {
    public EncryptData first;       //Provider related part of the request
    public EncryptData second;      //BulletinBoard related part of the request

    public Request(EncryptData partI, EncryptData partII) {
        this.first=partI;
        this.second=partII;
    }
}
