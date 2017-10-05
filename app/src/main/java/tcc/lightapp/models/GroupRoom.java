package tcc.lightapp.models;

import java.util.List;

/**
 * Created by Henrique on 04/10/2017.
 */

public class GroupRoom {
    public String groupKey;
    public String groupName;
    public String adminUid;
    public List<String> membersUid;
    public List<ChatMessage> messages;

    public GroupRoom(String groupKey, String groupName, String adminUid, List<String> membersUid) {
        this.groupKey = groupKey;
        this.groupName = groupName;
        this.adminUid = adminUid;
        this.membersUid = membersUid;
    }

    public String getRoomId() {
        return groupKey;
    }

    public void setRoomId(String roomId) {
        this.groupKey = roomId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<String> getMemberUid() {
        return membersUid;
    }

    public void setMemberUid(List<String> memberUid) {
        this.membersUid = memberUid;
    }

    public List<ChatMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<ChatMessage> messages) {
        this.messages = messages;
    }
}
