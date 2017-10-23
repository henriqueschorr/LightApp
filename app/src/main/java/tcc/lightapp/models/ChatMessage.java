package tcc.lightapp.models;

/**
 * Created by Henrique on 01/10/2017.
 */
public class ChatMessage {
    public String sender;
    public String senderName;
    public String receiver;
    public String senderUid;
    public String receiverUid;
    public String message;
    public long timestamp;

    public ChatMessage() {
    }

    public ChatMessage(String sender, String senderName, String receiver, String senderUid, String receiverUid, String message, long timestamp) {
        this.sender = sender;
        this.senderName = senderName;
        this.receiver = receiver;
        this.senderUid = senderUid;
        this.receiverUid = receiverUid;
        this.message = message;
        this.timestamp = timestamp;
    }
}

