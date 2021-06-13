package Classes.Response;

import Classes.Request.Contract;

public class TokenA implements java.io.Serializable {
    public Contract contract;
    public int mask;
    public TokenA(Contract contract, int mask){
        this.contract =contract;
        this.mask=mask;
    }
}
