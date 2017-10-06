package tcc.lightapp.models;

/**
 * Created by Henrique on 17/09/2017.
 */

public class User {
    public String authID;
    public String firebaseToken;
    public String userName;
    public String email;
    public boolean available;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String email, String authID, String firebaseToken) {
        this.userName = username;
        this.email = email;
        this.authID = authID;
        this.firebaseToken = firebaseToken;
        this.available = false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return authID.equals(user.authID);

    }

    @Override
    public int hashCode() {
        return authID.hashCode();
    }

}
