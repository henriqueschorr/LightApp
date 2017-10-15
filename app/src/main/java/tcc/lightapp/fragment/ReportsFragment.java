package tcc.lightapp.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import tcc.lightapp.R;
import tcc.lightapp.activity.ReportActivity;
import tcc.lightapp.adapter.ReportsAdapter;
import tcc.lightapp.models.Report;
import tcc.lightapp.utils.Constants;

public class ReportsFragment extends Fragment {
    private View mFragmentView;
    protected RecyclerView mRecyclerView;
    private ReportsAdapter mReportAdapter;
    private ProgressBar mProgressBar;

    private DatabaseReference mDatabase;

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

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mProgressBar = (ProgressBar) mFragmentView.findViewById(R.id.progress_bar);

        mReportAdapter = new ReportsAdapter(reports, mFragmentView.getContext(), onClickReport());
        mRecyclerView = (RecyclerView) mFragmentView.findViewById(R.id.recyclerView);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mFragmentView.getContext()));
        mRecyclerView.setAdapter(mReportAdapter);

        getReports();
        return mFragmentView;
    }

    public void getReports() {
        DatabaseReference reportDatabase = mDatabase.child(Constants.ARG_REPORTS);

        mProgressBar.setVisibility(View.VISIBLE);

        reportDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                reports = mReportAdapter.getReports();
                for (DataSnapshot reportSnapshot : dataSnapshot.getChildren()) {
                    Report report = reportSnapshot.getValue(Report.class);
                    if (!reports.contains(report) && report.userUid.equals(mPatientUid)) {
                        mReportAdapter.addReport(report);
                        Collections.reverse(reports);
                        mReportAdapter.notifyItemInserted(reports.indexOf(report));
                    }
                }
                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private ReportsAdapter.ReportOnClickListener onClickReport() {
        return new ReportsAdapter.ReportOnClickListener() {
            @Override
            public void onClickReport(View view, int idx) {
                Report report = reports.get(idx);
                Intent intent = new Intent(getContext(), ReportActivity.class);
                intent.putExtra(Constants.ARG_UID, report.userUid);
                intent.putExtra(Constants.ARG_REPORT_TIMESTAMP, String.valueOf(report.timestamp));
                intent.putExtra(Constants.ARG_REPORT_POSITIVE, String.valueOf(report.positiveWords));
                intent.putExtra(Constants.ARG_REPORT_NEGATIVE, String.valueOf(report.negativeWords));
                intent.putExtra(Constants.ARG_REPORT_NEUTRAL, String.valueOf(report.neutralWords));
                intent.putExtra(Constants.ARG_REPORT_CLASSIFIED, String.valueOf(report.classifiedWords));
                intent.putExtra(Constants.ARG_REPORT_NOT_CLASSIFIED, String.valueOf(report.notClassifiedWords));
                intent.putExtra(Constants.ARG_REPORT_TOTAL_WORDS, String.valueOf(report.totalWords));
                startActivity(intent);
            }
        };
    }
}
