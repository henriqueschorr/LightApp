package tcc.lightapp.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;

import tcc.lightapp.R;
import tcc.lightapp.chat.ChatContract;
import tcc.lightapp.chat.ChatPresenter;
import tcc.lightapp.models.ChatMessage;
import tcc.lightapp.utils.Constants;

public class ChatActivity extends AppCompatActivity implements ChatContract.View {
    private String mSenderEmail;
    private String mReceiverEmail;
    private String mSenderUid;
    private String mReceiverUid;
    private String mReceiverFirebaseToken;
    private EditText mTextMessage;
    private ChatPresenter mChatPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent intent = getIntent();
        Bundle args = intent.getExtras();

        mSenderEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        mSenderUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mReceiverEmail = args.getString(Constants.ARG_RECEIVER);
        mReceiverUid = args.getString(Constants.ARG_RECEIVER_UID);
        mReceiverFirebaseToken = args.getString(Constants.ARG_FIREBASE_TOKEN);

        mTextMessage = (EditText) findViewById(R.id.edit_text_message);

        mChatPresenter = new ChatPresenter(this);
//        mChatPresenter.getMessage(FirebaseAuth.getInstance().getCurrentUser().getUid(),
//                args.getString(Constants.ARG_RECEIVER_UID));
    }


    private void sendMessage() {
        String message = mTextMessage.getText().toString();

        ChatMessage chatMessage = new ChatMessage(mSenderEmail,
                mReceiverEmail,
                mSenderUid,
                mReceiverUid,
                message,
                System.currentTimeMillis());
        mChatPresenter.sendMessage(this,
                chatMessage,
                mReceiverFirebaseToken);
    }

    @Override
    public void onSendMessageSuccess() {
//        mETxtMessage.setText("");
//        Toast.makeText(getActivity(), "Message sent", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSendMessageFailure(String message) {
//        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onGetMessagesSuccess(ChatMessage chatMessage) {
//        if (mChatRecyclerAdapter == null) {
//            mChatRecyclerAdapter = new ChatRecyclerAdapter(new ArrayList<Chat>());
//            mRecyclerViewChat.setAdapter(mChatRecyclerAdapter);
//        }
//        mChatRecyclerAdapter.add(chat);
//        mRecyclerViewChat.smoothScrollToPosition(mChatRecyclerAdapter.getItemCount() - 1);
    }

    @Override
    public void onGetMessagesFailure(String message) {
//        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}
