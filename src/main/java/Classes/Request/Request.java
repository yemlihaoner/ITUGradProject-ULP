package Classes.Request;

public class Request implements java.io.Serializable {
    public RequestPartI first;
    public RequestPartII second;

    public Request(RequestPartI partI, RequestPartII partII) {
        this.first=partI;
        this.second=partII;
    }
}
