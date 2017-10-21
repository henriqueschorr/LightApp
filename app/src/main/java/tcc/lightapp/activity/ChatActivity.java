package tcc.lightapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import tcc.lightapp.R;
import tcc.lightapp.adapter.ChatAdapter;
import tcc.lightapp.chat.ChatContract;
import tcc.lightapp.chat.ChatPresenter;
import tcc.lightapp.fragment.dialog.AddMemberDialog;
import tcc.lightapp.models.ChatMessage;
import tcc.lightapp.utils.Constants;
import tcc.lightapp.utils.FirebaseChatMainApp;

public class ChatActivity extends BaseActivity implements ChatContract.View {
    protected RecyclerView mRecyclerView;
    private ChatPresenter mChatPresenter;
    private ChatAdapter mChatAdapter;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private DatabaseReference mUserDatabase;
    private FirebaseUser mUser;

    private String mSenderUid;
    private String mSenderEmail;
    private String mReceiverUid;
    private String mReceiverName;
    private String mReceiverEmail;
    private String mReceiverFirebaseToken;
    private String mGroupAdmin;
    private boolean isIndividual;
    private boolean isFriend;

    private ProgressBar mProgressBar;
    private EditText mTextMessage;
    private Button mSendMessage;

    private List<ChatMessage> mChatMessages = new ArrayList<ChatMessage>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        setUpToolbar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mUser = mAuth.getCurrentUser();

        Intent intent = getIntent();
        Bundle args = intent.getExtras();

        isIndividual = args.getBoolean(Constants.ARG_INDIVIDUAL);
        isFriend = args.getBoolean(Constants.ARG_FRIEND);
        mReceiverFirebaseToken = args.getString(Constants.ARG_FIREBASE_TOKEN);

        mSenderEmail = mUser.getEmail();
        mSenderUid = mUser.getUid();


        if (isIndividual) {
            mReceiverName = args.getString(Constants.ARG_RECEIVER_NAME);
            mReceiverEmail = args.getString(Constants.ARG_RECEIVER_EMAIL);
            mReceiverUid = args.getString(Constants.ARG_RECEIVER_UID);
            getSupportActionBar().setTitle(mReceiverName);

        } else {
            mReceiverEmail = args.getString(Constants.ARG_GROUP_NAME);
            mReceiverUid = args.getString(Constants.ARG_GROUP_KEY);
            mGroupAdmin = args.getString(Constants.ARG_GROUP_ADMIN);
            getSupportActionBar().setTitle(mReceiverEmail);
        }

        mUserDatabase = mDatabase.child(Constants.ARG_USERS).child(mSenderUid);

        mReceiverFirebaseToken = args.getString(Constants.ARG_FIREBASE_TOKEN);

        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
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
    }

    public void getChatRoom() {
        mProgressBar.setVisibility(View.VISIBLE);
        mChatPresenter = new ChatPresenter(this);
        mChatPresenter.getMessage(mSenderUid, mReceiverUid, isIndividual);
    }

    private void sendMessage() {
        String message = mTextMessage.getText().toString();
        mTextMessage.setText("");
        ChatMessage chatMessage = null;

        chatMessage = new ChatMessage(mSenderEmail,
                mUser.getDisplayName(),
                mReceiverEmail,
                mSenderUid,
                mReceiverUid,
                message,
                System.currentTimeMillis());

        mChatPresenter.sendMessage(this,
                chatMessage,
                mReceiverFirebaseToken,
                isIndividual);

        mDatabase.child(Constants.ARG_USERS).child(mSenderUid).child(Constants.ARG_MESSAGES).child(chatMessage.message).setValue(chatMessage.message);
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
        mProgressBar.setVisibility(View.GONE);
        mRecyclerView.smoothScrollToPosition(mChatAdapter.getItemCount() - 1);
    }

    @Override
    public void onGetMessagesFailure(String message) {
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        FirebaseChatMainApp.setChatActivityOpen(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isIndividual) {
            if (isFriend) {
                getMenuInflater().inflate(R.menu.menu_chat_friend, menu);
            } else {
                getMenuInflater().inflate(R.menu.menu_chat_indiv, menu);
            }
        } else {
            if (mGroupAdmin.equals(mSenderUid)) {
                getMenuInflater().inflate(R.menu.menu_chat_group_admin, menu);
            } else {
                getMenuInflater().inflate(R.menu.menu_chat_group, menu);
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int item = menuItem.getItemId();
        if (item == R.id.action_add_member) {
            Bundle args = new Bundle();
            args.putString(Constants.ARG_GROUP_KEY, mReceiverUid);
            AddMemberDialog dialog = new AddMemberDialog();
            dialog.setArguments(args);

            FragmentManager fragmentManager = getSupportFragmentManager();
            dialog.show(fragmentManager, "dialog_add_member");
            return true;
        } else if (item == R.id.action_add_friend) {
            addFriend();
            return true;
        } else if (item == R.id.action_create_event) {
            //Navigate to Create Event Activity
            Intent intent = new Intent(getContext(), CreateEventActivity.class);
            Bundle params = new Bundle();
            params.putString(Constants.ARG_GROUP_KEY, mReceiverUid);
            params.putString(Constants.ARG_GROUP_NAME, mReceiverEmail);
            intent.putExtras(params);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    public void addFriend() {
        mUserDatabase.child(Constants.ARG_FRIENDS).child(mReceiverUid).setValue(mReceiverName + "_" + mReceiverEmail);
        toast(getResources().getString(R.string.friend_added));
    }

}
