package tcc.lightapp.fragment;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import tcc.lightapp.R;
import tcc.lightapp.models.GroupRoom;

/**
 * A simple {@link Fragment} subclass.
 */
public class GroupFragment extends Fragment {
    private View mFragmentView;
    //    private FloatingActionButton mCreateGroup;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser user;

    public static GroupFragment newInstance() {
        GroupFragment fragment = new GroupFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mFragmentView = inflater.inflate(R.layout.fragment_group, container, false);


        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        user = mAuth.getCurrentUser();

        setClickListener();

        return mFragmentView;
    }

    public void setClickListener() {
        FloatingActionButton createGroupButton = (FloatingActionButton) mFragmentView.findViewById(R.id.create_group);

        createGroupButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                createGroup();
            }
        });
    }

    public void createGroup() {
        DatabaseReference groups = mDatabase.child("groups").push();
        String groupKey = groups.getKey();

        GroupRoom groupRoom = new GroupRoom(groupKey, "Group 1", user.getUid());

        groups.setValue(groupRoom);
    }

}
