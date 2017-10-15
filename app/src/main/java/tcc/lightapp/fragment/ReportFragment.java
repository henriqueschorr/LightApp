package tcc.lightapp.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

import tcc.lightapp.R;
import tcc.lightapp.models.Report;
import tcc.lightapp.utils.Constants;

public class ReportFragment extends Fragment {
    private View mFragmentView;
    private DatabaseReference mDatabase;
    private String mPatientUid;
    private String mReportKey;
    private String mPositive;
    private String mNegative;
    private String mNeutral;
    private String mClassified;
    private String mNotClassified;
    private String mTotalWords;
    private TextView mTimestampField;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);

        Bundle args = getArguments();
        mPatientUid = args.getString(Constants.ARG_UID);
        mReportKey = args.getString(Constants.ARG_REPORT_TIMESTAMP);
        mPositive = args.getString(Constants.ARG_REPORT_POSITIVE);
        mNegative = args.getString(Constants.ARG_REPORT_NEGATIVE);
        mNeutral = args.getString(Constants.ARG_REPORT_NEUTRAL);
        mClassified = args.getString(Constants.ARG_REPORT_CLASSIFIED);
        mNotClassified = args.getString(Constants.ARG_REPORT_NOT_CLASSIFIED);
        mTotalWords = args.getString(Constants.ARG_REPORT_TOTAL_WORDS);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mFragmentView = inflater.inflate(R.layout.fragment_report, container, false);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        getFields();
        setFieldsContent();

        return  mFragmentView;
    }

    public void getFields(){
        mTimestampField = mFragmentView.findViewById(R.id.report_timestamp);
    }

    public void setFieldsContent(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Long timestamp = Long.parseLong(mReportKey);
        Date resultdate = new Date(timestamp);
        String date = sdf.format(resultdate);

        mTimestampField.setText(date);
    }

}
