package tcc.lightapp.activity;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import tcc.lightapp.R;
import tcc.lightapp.adapter.ChatAdapter;
import tcc.lightapp.chat.ChatContract;
import tcc.lightapp.chat.ChatPresenter;
import tcc.lightapp.models.ChatMessage;
import tcc.lightapp.utils.Constants;

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
    protected RecyclerView mRecyclerView;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent intent = getIntent();
        Bundle args = intent.getExtras();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mSenderEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        mSenderUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mReceiverEmail = args.getString(Constants.ARG_RECEIVER);
        mReceiverUid = args.getString(Constants.ARG_RECEIVER_UID);
        mReceiverFirebaseToken = args.getString(Constants.ARG_FIREBASE_TOKEN);

        mTextMessage = (EditText) findViewById(R.id.edit_text_message);
        mSendMessage = (Button) findViewById(R.id.send_text_message);

        mSendMessage.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
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

        getChatRoom();
    }

    public void getChatRoom() {
        mChatPresenter = new ChatPresenter(this);
        mChatPresenter.getMessage(mSenderUid, mReceiverUid);
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
}
