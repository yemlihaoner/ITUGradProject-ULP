package Classes.Response;

import Classes.Request.RequestPartI;
import Classes.Request.RequestPartII;

public class Response implements java.io.Serializable {
    public ResponsePartI first;
    public ResponsePartII second;
    public Response(ResponsePartI partI, ResponsePartII partII) {
        this.first=partI;
        this.second=partII;
    }
}
