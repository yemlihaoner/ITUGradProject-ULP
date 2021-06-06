package Classes;

import java.util.Date;

public class Log {
    public Date time;
    public String object;
    public String N;
    public String content;
    public Log(Date time, String object, String N, String content){
        this.time=time;
        this.object = object;
        this.N=N;
        this.content=content;
    }
}
