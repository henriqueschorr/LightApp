package tcc.lightapp.domain;

/**
 * Created by Henrique on 17/09/2017.
 */

public class User {
    public String authID;
    public String userName;
    public String email;
    public boolean available;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String email, String authID) {
        this.userName = username;
        this.email = email;
        this.authID = authID;
        this.available = false;
    }

    public void setAvailable() {
        available = true;
    }

    public void setUnavailable() {
        available = false;
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

    public String getAuthID() {
        return authID;
    }

    public void setAuthID(String authID) {
        this.authID = authID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
