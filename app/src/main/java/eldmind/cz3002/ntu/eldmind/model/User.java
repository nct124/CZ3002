package eldmind.cz3002.ntu.eldmind.model;

/**
 * Created by n on 23/2/2017.
 */

public class User {
    private int phone;
    private String firebaseToken;

    public int getPhone() {
        return phone;
    }

    public void setPhone(int phone) {
        this.phone = phone;
    }

    public String getFirebaseToken() {
        return firebaseToken;
    }

    public void setFirebaseToken(String firebaseToken) {
        this.firebaseToken = firebaseToken;
    }
}
