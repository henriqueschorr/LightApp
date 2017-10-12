package tcc.lightapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.support.v4.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import tcc.lightapp.R;
import tcc.lightapp.adapter.ChatAdapter;
import tcc.lightapp.chat.ChatContract;
import tcc.lightapp.chat.ChatPresenter;
import tcc.lightapp.fragment.dialog.AddMemberDialog;
import tcc.lightapp.models.ChatMessage;
import tcc.lightapp.models.GroupRoom;
import tcc.lightapp.utils.Constants;
import tcc.lightapp.utils.FirebaseChatMainApp;

public class ChatActivity extends BaseActivity implements ChatContract.View {
    private DatabaseReference mDatabase;
    private DatabaseReference mUserDatabase;
    private String mSenderEmail;
    private String mReceiverName;
    private String mReceiverEmail;
    private String mSenderUid;
    private String mReceiverUid;
    private String mReceiverFirebaseToken;
    private String mGroupAdmin;
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

        mDatabase = FirebaseDatabase.getInstance().getReference();

        Intent intent = getIntent();
        Bundle args = intent.getExtras();

        isIndividual = args.getBoolean(Constants.ARG_INDIVIDUAL);

        mSenderEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        mSenderUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

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
//        mRecyclerView.smoothScrollToPosition(mChatAdapter.getItemCount() - 1);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        String teste;
        if (isIndividual) {
            getMenuInflater().inflate(R.menu.menu_chat_indiv, menu);
            FirebaseDatabase data = mUserDatabase.child(mSenderUid).child(Constants.ARG_GROUP_ADMIN).getDatabase();
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
        } else if (item == R.id.action_create_event) {
            //Navigate to Create Event Activity
            Intent intent = new Intent(getContext(), CreateEventActivity.class);
            Bundle params = new Bundle();
            params.putString(Constants.ARG_GROUP_KEY, mReceiverUid);
            params.putString(Constants.ARG_GROUP_NAME, mReceiverEmail);
            intent.putExtras(params);
            startActivity(intent);
        } else if (item == R.id.action_do_sentiment_analysis) {
            URL url = null;
            try {
                url = new URL("https://us-central1-lightapp-d3dc5.cloudfunctions.net/doSentimentAnalysis?userUid" + mSenderUid);
            } catch (MalformedURLException e) {
                //erro
            }

            try {
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
//                text.setText(readStream(urlConnection.getInputStream()));
                urlConnection.disconnect();
            } catch (IOException e) {
                //error
            }

        }
        return super.onOptionsItemSelected(menuItem);
    }

    public void addFriend() {
        mUserDatabase.child(Constants.ARG_FRIENDS).child(mReceiverUid).setValue(mReceiverName + "_" + mReceiverEmail);
    }

    private String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuffer response = new StringBuffer();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response.toString();
    }
}
