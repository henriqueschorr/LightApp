package tcc.lightapp.fragment;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import tcc.lightapp.adapter.EventAdapter;
import tcc.lightapp.models.Event;
import tcc.lightapp.utils.Constants;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventFragment extends Fragment {
    private View mFragmentView;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser user;
    private List<Event> events = new ArrayList<Event>();
    private EventAdapter mEventAdapter;
    protected RecyclerView mRecyclerView;

    public static EventFragment newInstance() {
        EventFragment fragment = new EventFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mFragmentView = inflater.inflate(R.layout.fragment_event, container, false);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        user = mAuth.getCurrentUser();

        mEventAdapter = new EventAdapter(events, mFragmentView.getContext());
        mRecyclerView = (RecyclerView) mFragmentView.findViewById(R.id.recyclerView);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mFragmentView.getContext()));
        mRecyclerView.setAdapter(mEventAdapter);
//
        getEvents();
//
        setClickListener();

        return mFragmentView;
    }

    public void setClickListener() {
        FloatingActionButton createGroupButton = (FloatingActionButton) mFragmentView.findViewById(R.id.create_event);

        createGroupButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
//                TODO: Create Activity with event creation form
            }
        });
    }

    public void getEvents() {
        DatabaseReference databaseReference = mDatabase.child(Constants.ARG_EVENTS).getRef();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                events = mEventAdapter.getEvents();
                for (DataSnapshot groupSnapshot : dataSnapshot.getChildren()) {
                    Event event = groupSnapshot.getValue(Event.class);
                    if(!events.contains(event)) {
                        mEventAdapter.addEvent(event);
                        mEventAdapter.notifyItemInserted(events.indexOf(event));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
