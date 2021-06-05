package Classes.Request;

import java.util.Date;

public class Proposal implements java.io.Serializable {
    public String userID;
    public String hostID;
    public String hostURI;
    public String accessType;
    public Proposal(String userID, String hostID, String hostURI, String accessType){
        this.userID=userID;
        this.hostID=hostID;
        this.hostURI=hostURI;
        this.accessType=accessType;
    }
}
