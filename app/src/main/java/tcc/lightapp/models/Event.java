package tcc.lightapp.models;

import java.util.Date;
import java.util.List;

/**
 * Created by Henrique on 05/10/2017.
 */

public class Event {
    public String eventKey;
    public String adminUid;
    public String eventName;
    public String location;
    public Date date;
    public List<String> groupsKey;
    public List<String> confirmedUsersUid;

    public Event(){}

    public Event(String eventKey, String adminUid, String eventName, String location, Date date, List<String> groupsKey) {
        this.eventKey = eventKey;
        this.adminUid = adminUid;
        this.eventName = eventName;
        this.location = location;
        this.date = date;
        this.groupsKey = groupsKey;
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
