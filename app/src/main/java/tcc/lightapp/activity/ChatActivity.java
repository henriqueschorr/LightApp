package tcc.lightapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import tcc.lightapp.R;
import tcc.lightapp.adapter.ChatAdapter;
import tcc.lightapp.chat.ChatContract;
import tcc.lightapp.chat.ChatPresenter;
import tcc.lightapp.models.ChatMessage;
import tcc.lightapp.utils.Constants;
import tcc.lightapp.utils.FirebaseChatMainApp;

public class ChatActivity extends BaseActivity implements ChatContract.View {
    private String mSenderEmail;
    private String mReceiverEmail;
    private String mSenderUid;
    private String mReceiverUid;
    private String mReceiverFirebaseToken;
    private EditText mTextMessage;
    private Button mSendMessage;
    private ChatPresenter mChatPresenter;
    private ChatAdapter mChatAdapter;
    private List<ChatMessage> mChatMessages = new ArrayList<ChatMessage>();
    private boolean isIndividual;
    private Toolbar mToolbar;
    protected RecyclerView mRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mToolbar = setUpToolbar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        Bundle args = intent.getExtras();

        isIndividual = args.getBoolean(Constants.ARG_INDIVIDUAL);

        mSenderEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        mSenderUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (isIndividual) {
            mReceiverEmail = args.getString(Constants.ARG_RECEIVER_EMAIL);
            mReceiverUid = args.getString(Constants.ARG_RECEIVER_UID);
            getSupportActionBar().setTitle(args.getString(Constants.ARG_RECEIVER_NAME));

        } else {
            mReceiverEmail = args.getString(Constants.ARG_GROUP_NAME);
            mReceiverUid = args.getString(Constants.ARG_GROUP_KEY);
            getSupportActionBar().setTitle(mReceiverEmail);
        }

        mReceiverFirebaseToken = args.getString(Constants.ARG_FIREBASE_TOKEN);


        mTextMessage = (EditText) findViewById(R.id.edit_text_message);
        mSendMessage = (Button) findViewById(R.id.send_text_message);

        mSendMessage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendMessage();
            }
        });

        mChatPresenter = new ChatPresenter(this);

        mChatAdapter = new ChatAdapter(mChatMessages, this);
        mRecyclerView = (RecyclerView) this.findViewById(R.id.recycler_view_chat);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mChatAdapter);

        FirebaseChatMainApp.setChatActivityOpen(true);

        getChatRoom();
//        TODO: Fix Send button (Fix it in the right side of the screen)
    }

    public void getChatRoom() {
        mChatPresenter = new ChatPresenter(this);
        mChatPresenter.getMessage(mSenderUid, mReceiverUid, isIndividual);
    }

    private void sendMessage() {
        String message = mTextMessage.getText().toString();
        mTextMessage.setText("");
        ChatMessage chatMessage = null;

        chatMessage = new ChatMessage(mSenderEmail,
                mReceiverEmail,
                mSenderUid,
                mReceiverUid,
                message,
                System.currentTimeMillis());

        mChatPresenter.sendMessage(this,
                chatMessage,
                mReceiverFirebaseToken,
                isIndividual);
    }

    @Override
    public void onSendMessageSuccess() {
        mTextMessage.setText("");
        Toast.makeText(this, "Message sent", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSendMessageFailure(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onGetMessagesSuccess(ChatMessage chatMessage) {
        mChatAdapter.add(chatMessage);
        mRecyclerView.smoothScrollToPosition(mChatAdapter.getItemCount() - 1);
    }

    @Override
    public void onGetMessagesFailure(String message) {
//        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        FirebaseChatMainApp.setChatActivityOpen(false);
    }

}
