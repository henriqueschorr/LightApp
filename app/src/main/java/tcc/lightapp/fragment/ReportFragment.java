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
    private String mPositivePhrase;
    private String mNegativePhrase;
    private String mNeutralPhrase;
    private String mPositivePhraseThis;
    private String mNegativePhraseThis;
    private String mNeutralPhraseThis;
    private String mPositiveGrowth;
    private String mNegativeGrowth;

    private TextView mPositiveField;
    private TextView mNegativeField;
    private TextView mNeutralField;
    private TextView mClassifiedField;
    private TextView mNotClassifiedField;
    private TextView mTotalWordsField;
    private TextView mPositivePhraseField;
    private TextView mNegativePhraseField;
    private TextView mNeutralPhraseField;
    private TextView mPositiveGrowthField;
    private TextView mNegativeGrowthField;

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
        mPositivePhrase = args.getString(Constants.ARG_REPORT_POSITIVE_PHRASE);
        mNegativePhrase = args.getString(Constants.ARG_REPORT_NEGATIVE_PHRASE);
        mPositivePhraseThis = args.getString(Constants.ARG_REPORT_POSITIVE_PHRASE_THIS);
        mNegativePhraseThis = args.getString(Constants.ARG_REPORT_NEGATIVE_PHRASE_THIS);
        mNeutralPhraseThis = args.getString(Constants.ARG_REPORT_NEUTRAL_PHRASE_THIS);
        mNeutralPhrase = args.getString(Constants.ARG_REPORT_NEUTRAL_PHRASE);
        mPositiveGrowth = args.getString(Constants.ARG_REPORT_POSITIVE_GROWTH);
        mNegativeGrowth = args.getString(Constants.ARG_REPORT_NEGATIVE_GROWTH);
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
//        mPositiveField = mFragmentView.findViewById(R.id.report_positive);
//        mNegativeField = mFragmentView.findViewById(R.id.report_negative);
//        mNeutralField = mFragmentView.findViewById(R.id.report_neutral);
//        mClassifiedField = mFragmentView.findViewById(R.id.report_classified);
//        mNotClassifiedField = mFragmentView.findViewById(R.id.report_not_classified);
//        mTotalWordsField = mFragmentView.findViewById(R.id.report_total_words);
        mPositivePhraseField = mFragmentView.findViewById(R.id.report_positive_phrase);
        mNegativePhraseField = mFragmentView.findViewById(R.id.report_negative_phrase);
        mPositiveGrowthField = mFragmentView.findViewById(R.id.report_positive_growth);
        mNegativeGrowthField = mFragmentView.findViewById(R.id.report_negative_growth);
        mNeutralPhraseField = mFragmentView.findViewById(R.id.report_neutral_phrase);
    }

    public void setFieldsContent() {
//        mPositiveField.setText(mPositive);
//        mNegativeField.setText(mNegative);
//        mNeutralField.setText(mNeutral);
//        mClassifiedField.setText(mClassified);
//        mNotClassifiedField.setText(mNotClassified);
//        mTotalWordsField.setText(mTotalWords);
        mPositivePhraseField.setText(mPositivePhraseThis);
        mNegativePhraseField.setText(mNegativePhraseThis);
        mNeutralPhraseField.setText(mNeutralPhraseThis);

        double noGrowth = 666;

        if (Double.valueOf(mPositiveGrowth) == noGrowth) {
            mPositiveGrowth = getResources().getString(R.string.no_growth);
        } else if (Double.valueOf(mPositiveGrowth) > 0){
            mPositiveGrowth = "+" + mPositiveGrowth + " %";
        } else {
            mPositiveGrowth = mPositiveGrowth + " %";
        }

        if (Double.valueOf(mNegativeGrowth) == noGrowth){
            mNegativeGrowth = getResources().getString(R.string.no_growth);
        } else if (Double.valueOf(mNegativeGrowth) > 0){
            mNegativeGrowth = "+" + mNegativeGrowth + " %";
        } else {
            mNegativeGrowth = mNegativeGrowth + " %";
        }

        mPositiveGrowthField.setText(mPositiveGrowth);
        mNegativeGrowthField.setText(mNegativeGrowth);
    }

}
