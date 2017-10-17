package tcc.lightapp.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import tcc.lightapp.R;
import tcc.lightapp.activity.CreateEventActivity;
import tcc.lightapp.activity.EventActivity;
import tcc.lightapp.adapter.EventsAdapter;
import tcc.lightapp.models.Event;
import tcc.lightapp.utils.Constants;

public class EventsFragment extends Fragment {
    private View mFragmentView;
    protected RecyclerView mRecyclerView;
    private EventsAdapter mEventAdapter;
    private ProgressBar mProgressBar;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser user;

    private List<Event> events = new ArrayList<Event>();

    public static EventsFragment newInstance() {
        EventsFragment fragment = new EventsFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mFragmentView = inflater.inflate(R.layout.fragment_events, container, false);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        user = mAuth.getCurrentUser();

        mProgressBar = (ProgressBar) mFragmentView.findViewById(R.id.progress_bar);

        mEventAdapter = new EventsAdapter(events, mFragmentView.getContext(), onClickEvent());
        mRecyclerView = (RecyclerView) mFragmentView.findViewById(R.id.recyclerView);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mFragmentView.getContext()));
        mRecyclerView.setAdapter(mEventAdapter);

        getEvents();

        setClickListener();

        return mFragmentView;
    }

    public void getEvents() {
        DatabaseReference databaseReference = mDatabase.child(Constants.ARG_EVENTS);

        mProgressBar.setVisibility(View.VISIBLE);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                events = mEventAdapter.getEvents();
                for (DataSnapshot groupSnapshot : dataSnapshot.getChildren()) {
                    Event event = groupSnapshot.getValue(Event.class);
                    if (!events.contains(event)) {
                        mEventAdapter.addEvent(event);
                        mEventAdapter.notifyItemInserted(events.indexOf(event));
                    }
                }
                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void setClickListener() {
        FloatingActionButton createGroupButton = (FloatingActionButton) mFragmentView.findViewById(R.id.create_event);

        createGroupButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CreateEventActivity.class);
                startActivity(intent);
            }
        });
    }

    private EventsAdapter.EventOnClickListener onClickEvent() {
        return new EventsAdapter.EventOnClickListener() {
            @Override
            public void onClickEvent(View view, int idx) {
                Event event = events.get(idx);
                Intent intent = new Intent(getContext(), EventActivity.class);
                intent.putExtra(Constants.ARG_EVENT_NAME, event.eventName);
                intent.putExtra(Constants.ARG_EVENT_LOCATION, event.location);
                intent.putExtra(Constants.ARG_EVENT_DATE, event.date);
                intent.putExtra(Constants.ARG_EVENT_TIME, event.time);
                startActivity(intent);
            }
        };
    }

}
