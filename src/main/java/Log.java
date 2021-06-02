import java.util.Date;

public class Log {
    public Date time;
    public String type;
    public String id;
    public String text;
    public Log(Date time,String type,String id,String text){
        this.time=time;
        this.type = type;
        this.id=id;
        this.text=text;
    }
}
