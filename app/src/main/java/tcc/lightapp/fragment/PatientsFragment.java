package tcc.lightapp.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import tcc.lightapp.adapter.UserAdapter;
import tcc.lightapp.models.User;
import tcc.lightapp.utils.Constants;


public class PatientsFragment extends Fragment {
    private View mFragmentView;
    protected RecyclerView mRecyclerView;
    private UserAdapter mPatientAdapter;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser user;

    private List<User> patients = new ArrayList<User>();

    private static final String TAG = "PatientList";

    public static PatientsFragment newInstance() {
        PatientsFragment fragment = new PatientsFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mFragmentView = inflater.inflate(R.layout.fragment_patients, container, false);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        user = mAuth.getCurrentUser();

        mPatientAdapter = new UserAdapter(patients, mFragmentView.getContext(), onClickUser());
        mRecyclerView = (RecyclerView) mFragmentView.findViewById(R.id.recyclerView);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mFragmentView.getContext()));
        mRecyclerView.setAdapter(mPatientAdapter);

        getPatients();
        return mFragmentView;
    }

    public void getPatients() {
        DatabaseReference userDatabase = mDatabase.child(Constants.ARG_USERS).getRef();
        DatabaseReference patientDatabase = userDatabase.child(user.getUid()).child(Constants.ARG_PATIENTS);

        patientDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                patients = mPatientAdapter.getUsers();
                for (DataSnapshot patientSnapshot : dataSnapshot.getChildren()) {
                    String patientUid = patientSnapshot.getKey();
                    String[] patientData = patientSnapshot.getValue().toString().split("_");
                    String patientName = patientData[0];
                    String patientEmail = patientData[1];

                    User userPatient = new User(patientName, patientEmail, patientUid);
                    if (!patients.contains(patientUid)) {
                        mPatientAdapter.addUser(userPatient);
                        mPatientAdapter.notifyItemInserted(patients.indexOf(patientUid));

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private UserAdapter.UserOnClickListener onClickUser() {
        return new UserAdapter.UserOnClickListener() {
            @Override
            public void onClickUser(View view, int idx) {
                //TODO: create patients report activity
                //TODO:navigate to patients report
//                User user = availableUsers.get(idx);
//                Intent intent = new Intent(getContext(), ChatActivity.class);
//                intent.putExtra(Constants.ARG_RECEIVER_NAME, user.userName);
//                intent.putExtra(Constants.ARG_RECEIVER_EMAIL, user.email);
//                intent.putExtra(Constants.ARG_RECEIVER_UID, user.authID);
//                intent.putExtra(Constants.ARG_FIREBASE_TOKEN, user.firebaseToken);
//                intent.putExtra(Constants.ARG_INDIVIDUAL, true);
//                startActivity(intent);
            }
        };
    }
}
