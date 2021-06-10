package Classes.Response;

import Classes.EncryptData;
import Classes.Request.RequestPartI;
import Classes.Request.RequestPartII;

public class Response implements java.io.Serializable {
    public EncryptData first;
    public EncryptData second;
    public Response(EncryptData partI, EncryptData partII) {
        this.first=partI;
        this.second=partII;
    }
}
