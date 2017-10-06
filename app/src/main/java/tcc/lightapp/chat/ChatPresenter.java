package tcc.lightapp.chat;

import android.content.Context;

import tcc.lightapp.models.ChatMessage;

/**
 * Created by Henrique on 01/10/2017.
 */

public class ChatPresenter implements ChatContract.Presenter, ChatContract.OnSendMessageListener,
        ChatContract.OnGetMessagesListener {
    private ChatContract.View mView;
    private ChatInteractor mChatInteractor;

    public ChatPresenter(ChatContract.View view) {
        this.mView = view;
        mChatInteractor = new ChatInteractor(this, this);
    }

    @Override
    public void sendMessage(Context context, ChatMessage chatMessage, String receiverFirebaseToken, boolean isIndividual) {
        mChatInteractor.sendMessageToFirebaseUser(context, chatMessage, receiverFirebaseToken, isIndividual);
    }

    @Override
    public void getMessage(String senderUid, String receiverUid, boolean isIndividual) {
        if (isIndividual) {
            mChatInteractor.getMessageFromFirebaseUser(senderUid, receiverUid);
        } else {
            mChatInteractor.getMessageFromFirebaseGroup(senderUid, receiverUid);
        }
    }

    @Override
    public void onSendMessageSuccess() {
        mView.onSendMessageSuccess();
    }

    @Override
    public void onSendMessageFailure(String message) {
        mView.onSendMessageFailure(message);
    }

    @Override
    public void onGetMessagesSuccess(ChatMessage chatMessage) {
        mView.onGetMessagesSuccess(chatMessage);
    }

    @Override
    public void onGetMessagesFailure(String message) {
        mView.onGetMessagesFailure(message);
    }
}
