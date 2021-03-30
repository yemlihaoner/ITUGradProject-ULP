import java.util.Date;

public class Log {
    public Date time;
    public String type;
    public String status;
    public String text;
    public Log(Date time,String type,String status,String text){
        this.time=time;
        this.type = type;
        this.status=status;
        this.text=text;
    }
}
