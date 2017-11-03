package tcc.lightapp.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import tcc.lightapp.R;
import tcc.lightapp.adapter.ReportsAdapter;
import tcc.lightapp.models.Report;
import tcc.lightapp.utils.Constants;

public class ReportsActivity extends BaseActivity {
    protected RecyclerView mRecyclerView;
    private ReportsAdapter mReportAdapter;
//    private Context mContext;

    private DatabaseReference mDatabase;

    //    private ReportsFragment mFragment;
    private LinearLayout mProgressBar;

    private String mPatientUid;
    private String mPatientName;

    private List<Report> reports = new ArrayList<Report>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setProgressBarIndeterminateVisibility(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        setUpToolbar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        Bundle args = intent.getExtras();

        mPatientUid = args.getString(Constants.ARG_UID);
        mPatientName = args.getString(Constants.ARG_USER_NAME);

        this.setTitle(mPatientName);

        mReportAdapter = new ReportsAdapter(reports, this.getContext(), onClickReport());
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        mRecyclerView.setAdapter(mReportAdapter);

        mProgressBar = (LinearLayout) findViewById(R.id.progress_bar);
        
        getReports();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_report, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int item = menuItem.getItemId();
        if (item == R.id.action_generate_report) {
            generateReport();

            return true;
        }
        return super.onOptionsItemSelected(menuItem);
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
                intent.putExtra(Constants.ARG_REPORT_POSITIVE_PHRASE, String.valueOf(report.positivePhrases));
                intent.putExtra(Constants.ARG_REPORT_NEGATIVE_PHRASE, String.valueOf(report.negativePhrases));
                intent.putExtra(Constants.ARG_REPORT_NEUTRAL_PHRASE, String.valueOf(report.neutralPhrases));
                startActivity(intent);
            }
        };
    }

    public void generateReport() {
        String url = "https://us-central1-lightapp-d3dc5.cloudfunctions.net/doSentimentAnalysis?userUid=" + mPatientUid;

        HttpGetRequest httpGetRequest = new HttpGetRequest();
        try {
            httpGetRequest.execute(url).get();
        } catch (ExecutionException e) {
            //error
        } catch (InterruptedException e) {
            //error
        }

    }

    public class HttpGetRequest extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {
            String stringUrl = params[0];
            String response = null;

            try {
                URL url = new URL(stringUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");

                response = readStream(urlConnection.getInputStream());

                urlConnection.disconnect();
            } catch (MalformedURLException e) {
                //erro
            } catch (IOException e) {
                //error
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            String response = result.replaceAll("[\\{\\}\"]+", "");
            String[] data = response.split(",");
            String[] positiveWord = data[0].split(":");
            String[] negativeWord = data[1].split(":");
            String[] neutralWord = data[2].split(":");
            String[] classifiedWord = data[3].split(":");
            String[] notClassifiedWord = data[4].split(":");
            String[] totalWords = data[5].split(":");
            String[] positivePhrase = data[6].split(":");
            String[] negativePhrase = data[7].split(":");
            String[] neutralPhrase = data[8].split(":");

            Report report = new Report(
                    System.currentTimeMillis(),
                    mPatientUid,
                    Integer.parseInt(positiveWord[1]),
                    Integer.parseInt(negativeWord[1]),
                    Integer.parseInt(neutralWord[1]),
                    Integer.parseInt(classifiedWord[1]),
                    Integer.parseInt(notClassifiedWord[1]),
                    Integer.parseInt(totalWords[1]),
                    Integer.parseInt(positivePhrase[1]),
                    Integer.parseInt(negativePhrase[1]),
                    Integer.parseInt(neutralPhrase[1]));

            DatabaseReference userReportDatabase = mDatabase.child(Constants.ARG_USERS).child(mPatientUid).child(Constants.ARG_REPORTS);
            DatabaseReference reportDatabase = mDatabase.child(Constants.ARG_REPORTS);
            userReportDatabase.child(String.valueOf(report.timestamp)).setValue(String.valueOf(report.timestamp));
            reportDatabase.child(String.valueOf(report.timestamp)).setValue(report);

            mProgressBar.setVisibility(View.GONE);

            super.onPostExecute(result);
        }

        private String readStream(InputStream in) {
            BufferedReader reader = null;
            StringBuffer response = new StringBuffer();
            try {
                reader = new BufferedReader(new InputStreamReader(in));
                String line = "";
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return response.toString();
        }

    }

}
