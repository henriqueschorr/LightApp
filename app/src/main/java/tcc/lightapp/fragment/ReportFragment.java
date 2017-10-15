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

    private String mPositive;
    private String mNegative;
    private String mNeutral;
    private String mClassified;
    private String mNotClassified;
    private String mTotalWords;

    private TextView mPositiveField;
    private TextView mNegativeField;
    private TextView mNeutralField;
    private TextView mClassifiedField;
    private TextView mNotClassifiedField;
    private TextView mTotalWordsField;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);

        Bundle args = getArguments();
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

        getFields();
        setFieldsContent();

        return mFragmentView;
    }

    public void getFields() {
        mPositiveField = mFragmentView.findViewById(R.id.report_positive);
        mNegativeField = mFragmentView.findViewById(R.id.report_negative);
        mNeutralField = mFragmentView.findViewById(R.id.report_neutral);
        mClassifiedField = mFragmentView.findViewById(R.id.report_classified);
        mNotClassifiedField = mFragmentView.findViewById(R.id.report_not_classified);
        mTotalWordsField = mFragmentView.findViewById(R.id.report_total_words);
    }

    public void setFieldsContent() {
        mPositiveField.setText(mPositive);
        mNegativeField.setText(mNegative);
        mNeutralField.setText(mNeutral);
        mClassifiedField.setText(mClassified);
        mNotClassifiedField.setText(mNotClassified);
        mTotalWordsField.setText(mTotalWords);
    }

}
