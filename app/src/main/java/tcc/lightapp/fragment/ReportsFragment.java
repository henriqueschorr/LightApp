package tcc.lightapp.fragment;

import android.os.Bundle;
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
import java.util.Collections;
import java.util.List;

import tcc.lightapp.R;
import tcc.lightapp.adapter.ReportsAdapter;
import tcc.lightapp.models.Report;
import tcc.lightapp.utils.Constants;

public class ReportsFragment extends Fragment {
    private View mFragmentView;
    protected RecyclerView mRecyclerView;
    private ReportsAdapter mReportAdapter;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser user;

    private List<Report> reports = new ArrayList<Report>();

    private String mPatientUid;

    public static ReportsFragment newInstance(String patientUid) {
        Bundle args = new Bundle();
        args.putString(Constants.ARG_UID, patientUid);
        ReportsFragment fragment = new ReportsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);

        Bundle mArgs = getArguments();
        mPatientUid = mArgs.getString(Constants.ARG_UID);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mFragmentView = inflater.inflate(R.layout.fragment_patients, container, false);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        user = mAuth.getCurrentUser();

        mReportAdapter = new ReportsAdapter(reports, mFragmentView.getContext());
        mRecyclerView = (RecyclerView) mFragmentView.findViewById(R.id.recyclerView);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mFragmentView.getContext()));
        mRecyclerView.setAdapter(mReportAdapter);

        getReports();
        return mFragmentView;
    }

    public void getReports() {
        DatabaseReference userReportDatabase = mDatabase.child(Constants.ARG_USERS).child(mPatientUid).child(Constants.ARG_REPORTS);

        userReportDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                reports = mReportAdapter.getReports();
                for (DataSnapshot reportSnapshot : dataSnapshot.getChildren()) {
                    Report report = reportSnapshot.getValue(Report.class);
                    if (!reports.contains(report)) {
                        mReportAdapter.addReport(report);
                        Collections.reverse(reports);
                        mReportAdapter.notifyItemInserted(reports.indexOf(report));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
