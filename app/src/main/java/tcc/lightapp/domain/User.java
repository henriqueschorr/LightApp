package tcc.lightapp.domain;

/**
 * Created by Henrique on 17/09/2017.
 */

public class User {
    public String userName;
    public String email;
    public boolean online;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String email) {
        this.userName = username;
        this.email = email;
        this.online = false;
    }
}
