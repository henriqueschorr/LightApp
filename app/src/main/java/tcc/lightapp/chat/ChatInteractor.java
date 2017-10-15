package tcc.lightapp.chat;

import android.content.Context;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import tcc.lightapp.fcm.FcmNotificationBuilder;
import tcc.lightapp.models.ChatMessage;
import tcc.lightapp.utils.Constants;
import tcc.lightapp.utils.SharedPrefUtil;

/**
 * Created by Henrique on 01/10/2017.
 */

public class ChatInteractor implements ChatContract.Interactor {
    private static final String TAG = "ChatInteractor";

    private ChatContract.OnSendMessageListener mOnSendMessageListener;
    private ChatContract.OnGetMessagesListener mOnGetMessagesListener;

    public ChatInteractor(ChatContract.OnSendMessageListener onSendMessageListener) {
        this.mOnSendMessageListener = onSendMessageListener;
    }

    public ChatInteractor(ChatContract.OnGetMessagesListener onGetMessagesListener) {
        this.mOnGetMessagesListener = onGetMessagesListener;
    }

    public ChatInteractor(ChatContract.OnSendMessageListener onSendMessageListener,
                          ChatContract.OnGetMessagesListener onGetMessagesListener) {
        this.mOnSendMessageListener = onSendMessageListener;
        this.mOnGetMessagesListener = onGetMessagesListener;
    }

    @Override
    public void sendMessageToFirebaseUser(final Context context, final ChatMessage chatMessage, final String receiverFirebaseToken, boolean isIndividual) {
        final String room_type_1 = chatMessage.senderUid + "_" + chatMessage.receiverUid;
        final String room_type_2 = chatMessage.receiverUid + "_" + chatMessage.senderUid;

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        if (isIndividual) {
            databaseReference.child(Constants.ARG_CHAT_ROOMS).getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(room_type_1)) {
                        Log.e(TAG, "sendMessageToFirebaseUser: " + room_type_1 + " exists");
                        databaseReference.child(Constants.ARG_CHAT_ROOMS).child(room_type_1).child(String.valueOf(chatMessage.timestamp)).setValue(chatMessage);
                    } else if (dataSnapshot.hasChild(room_type_2)) {
                        Log.e(TAG, "sendMessageToFirebaseUser: " + room_type_2 + " exists");
                        databaseReference.child(Constants.ARG_CHAT_ROOMS).child(room_type_2).child(String.valueOf(chatMessage.timestamp)).setValue(chatMessage);
                    } else {
                        Log.e(TAG, "sendMessageToFirebaseUser: success");
                        databaseReference.child(Constants.ARG_CHAT_ROOMS).child(room_type_1).child(String.valueOf(chatMessage.timestamp)).setValue(chatMessage);
                        getMessageFromFirebaseUser(chatMessage.senderUid, chatMessage.receiverUid);
                    }
                    // send push notification to the receiver
//                TODO: Send pushNotification
//                sendPushNotificationToReceiver(chatMessage.sender,
//                        chatMessage.message,
//                        chatMessage.senderUid,
//                        new SharedPrefUtil(context).getString(Constants.ARG_FIREBASE_TOKEN),
//                        receiverFirebaseToken);
//                mOnSendMessageListener.onSendMessageSuccess();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    mOnSendMessageListener.onSendMessageFailure("Unable to send message: " + databaseError.getMessage());
                }
            });
        } else {
            databaseReference.child(Constants.ARG_GROUPS).child(chatMessage.receiverUid).child(Constants.ARG_CHAT_ROOM).getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    databaseReference.child(Constants.ARG_GROUPS).child(chatMessage.receiverUid).child(Constants.ARG_CHAT_ROOM).child(String.valueOf(chatMessage.timestamp)).setValue(chatMessage);

                    // send push notification to the receiver
//                TODO: Send pushNotification
//                sendPushNotificationToReceiver(chatMessage.sender,
//                        chatMessage.message,
//                        chatMessage.senderUid,
//                        new SharedPrefUtil(context).getString(Constants.ARG_FIREBASE_TOKEN),
//                        receiverFirebaseToken);
//                mOnSendMessageListener.onSendMessageSuccess();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    mOnSendMessageListener.onSendMessageFailure("Unable to send message: " + databaseError.getMessage());
                }
            });
        }
    }

    private void sendPushNotificationToReceiver(String username,
                                                String message,
                                                String uid,
                                                String firebaseToken,
                                                String receiverFirebaseToken) {
        FcmNotificationBuilder.initialize()
                .title(username)
                .message(message)
                .username(username)
                .uid(uid)
                .firebaseToken(firebaseToken)
                .receiverFirebaseToken(receiverFirebaseToken)
                .send();
    }

    @Override
    public void getMessageFromFirebaseUser(String senderUid, String receiverUid) {
        final String room_type_1 = senderUid + "_" + receiverUid;
        final String room_type_2 = receiverUid + "_" + senderUid;

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child(Constants.ARG_CHAT_ROOMS).getRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(room_type_1)) {
                    Log.e(TAG, "getMessageFromFirebaseUser: " + room_type_1 + " exists");
                    FirebaseDatabase.getInstance()
                            .getReference()
                            .child(Constants.ARG_CHAT_ROOMS)
                            .child(room_type_1).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            ChatMessage chatMessage = dataSnapshot.getValue(ChatMessage.class);
                            mOnGetMessagesListener.onGetMessagesSuccess(chatMessage);
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            mOnGetMessagesListener.onGetMessagesFailure("Unable to get message: " + databaseError.getMessage());
                        }
                    });
                } else if (dataSnapshot.hasChild(room_type_2)) {
                    Log.e(TAG, "getMessageFromFirebaseUser: " + room_type_2 + " exists");
                    FirebaseDatabase.getInstance()
                            .getReference()
                            .child(Constants.ARG_CHAT_ROOMS)
                            .child(room_type_2).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            ChatMessage chatMessage = dataSnapshot.getValue(ChatMessage.class);
                            mOnGetMessagesListener.onGetMessagesSuccess(chatMessage);
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            mOnGetMessagesListener.onGetMessagesFailure("Unable to get message: " + databaseError.getMessage());
                        }
                    });
                } else {
                    Log.e(TAG, "getMessageFromFirebaseUser: no such room available");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mOnGetMessagesListener.onGetMessagesFailure("Unable to get message: " + databaseError.getMessage());
            }
        });
    }

    @Override
    public void getMessageFromFirebaseGroup(String senderUid, final String groupKey) {

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child(Constants.ARG_GROUPS).child(groupKey).child(Constants.ARG_CHAT_ROOM).getRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                databaseReference.child(Constants.ARG_GROUPS).child(groupKey).child(Constants.ARG_CHAT_ROOM).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        ChatMessage chatMessage = dataSnapshot.getValue(ChatMessage.class);
                        mOnGetMessagesListener.onGetMessagesSuccess(chatMessage);
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        mOnGetMessagesListener.onGetMessagesFailure("Unable to get message: " + databaseError.getMessage());
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mOnGetMessagesListener.onGetMessagesFailure("Unable to get message: " + databaseError.getMessage());
            }
        });
    }
}