package tcc.lightapp.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Henrique on 04/10/2017.
 */

public class GroupRoom {
    public String groupKey;
    public String groupName;
    public String adminUid;
    public boolean isPrivate;
    public Map<String, String> membersUid;
    public List<ChatMessage> chatMessages;

    public GroupRoom(){
    }

    public GroupRoom(String groupKey, String groupName, String adminUid, String userName, String userEmail, boolean isPrivate) {
        this.groupKey = groupKey;
        this.groupName = groupName;
        this.adminUid = adminUid;
        this.isPrivate = isPrivate;
        this.membersUid = new HashMap<String, String>();
        this.membersUid.put(adminUid, userName + "_" + userEmail);
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
