package tcc.lightapp.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import tcc.lightapp.R;
import tcc.lightapp.models.Event;
import tcc.lightapp.utils.Constants;


public class CreateEventFragment extends Fragment{
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

    public static CreateEventFragment newInstance(String groupKey, String groupName) {
        Bundle args = new Bundle();
        args.putString(Constants.ARG_GROUP_KEY, groupKey);
        args.putString(Constants.ARG_GROUP_NAME, groupName);
        CreateEventFragment fragment = new CreateEventFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        Bundle mArgs = getArguments();
        mGroupKey = mArgs.getString(Constants.ARG_GROUP_KEY);
        mGroupName = mArgs.getString(Constants.ARG_GROUP_NAME);
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

        mCreateEventButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view){
                DatabaseReference events = mDatabase.child(Constants.ARG_EVENTS).push();
                String eventKey = events.getKey();
                String eventName = mEventNameField.getText().toString();
                String eventLocation = mLocationField.getText().toString();
                String eventDate = mDateField.getText().toString();
                String eventTime = mTimeField.getText().toString();

                Map<String, String> groupKey = new HashMap<String, String>();
                groupKey.put(mGroupKey, mGroupName);

                Event event = new Event (eventKey, user.getUid(), eventName, eventLocation, eventDate, eventTime, groupKey);

                events.setValue(event);

                getActivity().finish();
            }
        });

        return mFragmentView;
    }
}
