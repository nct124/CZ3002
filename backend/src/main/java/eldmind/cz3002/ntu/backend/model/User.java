package eldmind.cz3002.ntu.backend.model;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

/**
 * Created by n on 23/2/2017.
 */

@Entity
public class User {
    @Id private long Id;
    private String firebaseToken;

    public long getPhoneNumber() {
        return Id;
    }

    public void setPhoneNumber(long phoneNumber) {
        this.Id = phoneNumber;
    }

    public String getFirebaseToken() {
        return firebaseToken;
    }

    public void setFirebaseToken(String firebaseToken) {
        this.firebaseToken = firebaseToken;
    }
}
