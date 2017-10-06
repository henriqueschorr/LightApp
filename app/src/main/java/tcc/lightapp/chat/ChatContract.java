package tcc.lightapp.chat;

import android.content.Context;

import tcc.lightapp.models.ChatMessage;

/**
 * Created by Henrique on 01/10/2017.
 */

public interface ChatContract {
    interface View {
        void onSendMessageSuccess();

        void onSendMessageFailure(String message);

        void onGetMessagesSuccess(ChatMessage chatMessage);

        void onGetMessagesFailure(String message);
    }

    interface Presenter {
        void sendMessage(Context context, ChatMessage chatMessage, String receiverFirebaseToken, boolean isIndividual);

        void getMessage(String senderUid, String receiverUid, boolean isIndividual);
    }

    interface Interactor {
        void sendMessageToFirebaseUser(Context context, ChatMessage chatMessage, String receiverFirebaseToken, boolean isIndividual);

        void getMessageFromFirebaseUser(String senderUid, String receiverUid);

        void getMessageFromFirebaseGroup(String senderUid, String groupKey);
    }

    interface OnSendMessageListener {
        void onSendMessageSuccess();

        void onSendMessageFailure(String message);
    }

    interface OnGetMessagesListener {
        void onGetMessagesSuccess(ChatMessage chatMessage);

        void onGetMessagesFailure(String message);
    }
}