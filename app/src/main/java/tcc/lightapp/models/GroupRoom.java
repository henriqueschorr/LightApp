package tcc.lightapp.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Henrique on 04/10/2017.
 */

public class GroupRoom {
    public String groupKey;
    public String groupName;
    public String adminUid;
    public List<String> membersUid;
    public List<ChatMessage> chatMessages;

    public GroupRoom(){
    }

    public GroupRoom(String groupKey, String groupName, String adminUid) {
        this.groupKey = groupKey;
        this.groupName = groupName;
        this.adminUid = adminUid;
        this.membersUid = new ArrayList<String>();
        this.membersUid.add(adminUid);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GroupRoom groupRoom = (GroupRoom) o;

        return groupKey.equals(groupRoom.groupKey);

    }

    @Override
    public int hashCode() {
        return groupKey.hashCode();
    }
}
