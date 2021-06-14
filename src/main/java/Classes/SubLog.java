package Classes;
import Utils.Constants;
import java.io.Serializable;
import java.text.ParseException;
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
    public SubLog(String[] split_line) throws ParseException {
        this.time = Constants.dateFormatter.parse(split_line[0]);
        this.object=split_line[2];
        this.N=split_line[1];
    }
}
