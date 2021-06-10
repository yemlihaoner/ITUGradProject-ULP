package Classes;

import java.io.Serializable;
import java.util.Date;

public class SubLog implements Serializable {
    public Date time;
    public String object;
    public String N;
    public SubLog(Date time, String object, String N){
        this.time=time;
        this.object = object;
        this.N=N;
    }
}
