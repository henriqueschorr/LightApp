package tcc.lightapp.domain;

/**
 * Created by Henrique on 17/09/2017.
 */

public class User {
    public String userName;
    public String email;
    public boolean available;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String email) {
        this.userName = username;
        this.email = email;
        this.available = false;
    }

    public void setAvailable(){
        available = true;
    }

    public void setUnavailable(){
        available = false;
    }
}
