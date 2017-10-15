package tcc.lightapp.models;

import java.sql.Time;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Henrique on 05/10/2017.
 */

public class Event {
    public String eventKey;
    public String adminUid;
    public String eventName;
    public String location;
    public String date;
    public String time;
    public Map<String, String> groupsKey;
    public Map<String, String> invitedUsersUid;
    public Map<String, String> confirmedUsersUid;

    public Event(){}

    public Event(String eventKey, String adminUid, String userName, String userEmail, String eventName, String location, String date, String time) {
        this.eventKey = eventKey;
        this.adminUid = adminUid;
        this.eventName = eventName;
        this.location = location;
        this.date = date;
        this.time = time;
        invitedUsersUid = new HashMap<>();
        invitedUsersUid.put(adminUid, userName + "_" + userEmail);
    }

    public Event(String eventKey, String adminUid, String userName, String userEmail, String eventName, String location, String date, String time, Map<String, String> groupsKey) {
        this.eventKey = eventKey;
        this.adminUid = adminUid;
        this.eventName = eventName;
        this.location = location;
        this.date = date;
        this.time = time;
        this.groupsKey = groupsKey;
        invitedUsersUid = new HashMap<>();
        invitedUsersUid.put(adminUid, userName + "_" + userEmail);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Event event = (Event) o;

        return eventKey.equals(event.eventKey);

    }

    @Override
    public int hashCode() {
        return eventKey.hashCode();
    }
}
