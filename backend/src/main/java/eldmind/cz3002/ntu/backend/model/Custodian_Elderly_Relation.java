package eldmind.cz3002.ntu.backend.model;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
/**
 * Created by n on 7/3/2017.
 */
@Entity
public class Custodian_Elderly_Relation {
    @Id
    private String Id; //format: "custodianNum elderlyNum"
    private long custodianNum;//FK of User
    private long elderlyNum;//FK of User
    private String verifyCode;
    private String status; //PENDING OR CONFIRMED
    public Custodian_Elderly_Relation(){}
    public Custodian_Elderly_Relation(String id, long custodianNum, long elderlyNum, String verifyCode, String status) {
        this.Id = id;
        this.custodianNum = custodianNum;
        this.elderlyNum = elderlyNum;
        this.verifyCode = verifyCode;
        this.status = status;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public long getCustodianNum() {
        return custodianNum;
    }

    public void setCustodianNum(long custodianNum) {
        this.custodianNum = custodianNum;
    }

    public long getElderlyNum() {
        return elderlyNum;
    }

    public void setElderlyNum(long elderlyNum) {
        this.elderlyNum = elderlyNum;
    }

    public String getVerifyCode() {
        return verifyCode;
    }

    public void setVerifyCode(String verifyCode) {
        this.verifyCode = verifyCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
