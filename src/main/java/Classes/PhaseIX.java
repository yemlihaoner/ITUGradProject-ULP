package Classes;

import Utils.Constants;

public class PhaseIX implements java.io.Serializable{
    public String N;
    public Constants.Comment comment;
    public PhaseIX(String N, Constants.Comment comment){
        this.N=N;
        this.comment=comment;
    }
}
