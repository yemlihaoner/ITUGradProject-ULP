package Classes;

public class EncryptData implements java.io.Serializable {
    public byte[] esn; //esn
    public byte[] eepa1;//encrypted encrypted shared key
    public byte[] eepa2;//encrypted encrypted shared key

    public EncryptData(byte[] esn, byte[] eepa1, byte[] eepa2) {
        this.esn=esn;
        this.eepa1=eepa1;
        this.eepa2=eepa2;
    }
}
