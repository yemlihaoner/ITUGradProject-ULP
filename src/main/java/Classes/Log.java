package Classes;

import java.util.Date;

public class Log {
    public Date time;
    public String type;
    public String N;
    public String content;
    public Log(Date time,String type,String N,String content){
        this.time=time;
        this.type=type;
        this.N=N;
        this.content=content;
    }
}
