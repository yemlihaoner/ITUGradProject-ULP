package Classes;

import Utils.Constants;

public class PhaseIX implements java.io.Serializable{
    public String N;                                //Unique N value
    public Constants.Comment comment;               //Comment value (Check Constants.Comment for details)
    public PhaseIX(String N, Constants.Comment comment){
        this.N=N;
        this.comment=comment;
    }
}
