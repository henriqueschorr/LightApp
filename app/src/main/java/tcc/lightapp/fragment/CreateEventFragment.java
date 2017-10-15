package tcc.lightapp.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import tcc.lightapp.R;
import tcc.lightapp.fragment.dialog.SelectGroupDialog;
import tcc.lightapp.models.Event;
import tcc.lightapp.models.GroupRoom;
import tcc.lightapp.utils.Constants;


public class CreateEventFragment extends Fragment {
    private View mFragmentView;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser user;
    private EditText mEventNameField;
    private EditText mLocationField;
    private EditText mDateField;
    private EditText mTimeField;
    private Button mCreateEventButton;
    private String mGroupKey;
    private String mGroupName;
//    private String mEventKey;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        Bundle mArgs = getArguments();

        if (mArgs != null) {
            mGroupKey = mArgs.getString(Constants.ARG_GROUP_KEY);
            mGroupName = mArgs.getString(Constants.ARG_GROUP_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mFragmentView = inflater.inflate(R.layout.fragment_create_event, container, false);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        user = mAuth.getCurrentUser();

        mEventNameField = mFragmentView.findViewById(R.id.event_name);
        mLocationField = mFragmentView.findViewById(R.id.event_location);
        mDateField = mFragmentView.findViewById(R.id.event_date);
        mTimeField = mFragmentView.findViewById(R.id.event_time);
        mCreateEventButton = mFragmentView.findViewById(R.id.create_event_button);

        mCreateEventButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference eventsDatabase = mDatabase.child(Constants.ARG_EVENTS);
                String eventName = mEventNameField.getText().toString();
                String eventLocation = mLocationField.getText().toString();
                String eventDate = mDateField.getText().toString();
                String eventTime = mTimeField.getText().toString();

                Event event = null;

                if (mGroupKey != null) {
                    Map<String, String> groupKey = new HashMap<String, String>();
                    groupKey.put(mGroupKey, mGroupName);

                    event = new Event(mGroupKey, user.getUid(), user.getDisplayName(), user.getEmail(), eventName, eventLocation, eventDate, eventTime, groupKey);
                    eventsDatabase.child(mGroupKey).setValue(event);
                    getGroupMembers();
                    Toast.makeText(getContext(), getResources().getString(R.string.event_created), Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                } else {
                    Bundle args = new Bundle();

                    args.putString(Constants.ARG_EVENT_NAME, eventName);
                    args.putString(Constants.ARG_EVENT_LOCATION, eventLocation);
                    args.putString(Constants.ARG_EVENT_DATE, eventDate);
                    args.putString(Constants.ARG_EVENT_TIME, eventTime);

                    SelectGroupDialog dialog = new SelectGroupDialog();
                    dialog.setArguments(args);
                    dialog.show(getFragmentManager(), "dialog_select_group");
                }

            }
        });

        return mFragmentView;
    }

    public void getGroupMembers() {
        DatabaseReference groupsDatabase = mDatabase.child(Constants.ARG_GROUPS);
        final DatabaseReference EventInvitedDatabase = mDatabase.child(Constants.ARG_EVENTS).child(mGroupKey).child(Constants.ARG_INVITED_USERS);

        groupsDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot groupSnapshot : dataSnapshot.getChildren()) {
                    GroupRoom groupRoom = groupSnapshot.getValue(GroupRoom.class);
                    for (String memberKey : groupRoom.membersUid.keySet()) {
                        EventInvitedDatabase.child(memberKey).setValue(groupRoom.membersUid.get(memberKey));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
