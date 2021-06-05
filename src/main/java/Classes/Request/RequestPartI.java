package Classes.Request;

import java.util.Date;

public class RequestPartI implements java.io.Serializable {
    public Proposal proposal;  //Proposal of the request(userID,host URI etc)
    public String R_ks;        //Random value to define log between User and Provider
    public Date date;       //Timestamp of request

    public RequestPartI(Date date_now, String R_ks, Proposal proposal) {
        this.proposal=proposal;
        this.R_ks=R_ks;
        this.date=date_now;
    }
}
