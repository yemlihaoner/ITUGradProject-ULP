package Classes.Response;

import Classes.Request.Proposal;

import java.util.Date;

public class ResponsePartI implements java.io.Serializable {
    public String a_m;      //Masked Agreement
    public String R_ks;     //Random value to define log between User and Provider
    public Date date;       //Timestamp of request

    public ResponsePartI(Date date_now, String R_ks, String a_m) {
        this.a_m=a_m;
        this.R_ks=R_ks;
        this.date=date_now;
    }
}
