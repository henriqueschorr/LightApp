package tcc.lightapp.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import tcc.lightapp.R;
import tcc.lightapp.utils.Constants;

public class EventFragment extends Fragment {
    private View mFragmentView;

    private String mLocation;
    private String mDate;
    private String mTime;

    private TextView mLocationField;
    private TextView mDateField;
    private TextView mTimeField;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);

        Bundle args = getArguments();
        mLocation = args.getString(Constants.ARG_EVENT_LOCATION);
        mDate = args.getString(Constants.ARG_EVENT_DATE);
        mTime = args.getString(Constants.ARG_EVENT_TIME);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mFragmentView = inflater.inflate(R.layout.fragment_event, container, false);

        getFields();
        setFieldsContent();

        return mFragmentView;
    }

    public void getFields() {
        mLocationField = mFragmentView.findViewById(R.id.event_location);
        mDateField = mFragmentView.findViewById(R.id.event_date);
        mTimeField = mFragmentView.findViewById(R.id.event_time);
    }

    public void setFieldsContent() {
        mLocationField.setText(mLocation);
        mDateField.setText(mDate);
        mTimeField.setText(mTime);
    }

}
