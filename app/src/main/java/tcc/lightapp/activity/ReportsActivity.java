package tcc.lightapp.activity;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import tcc.lightapp.R;
import tcc.lightapp.fragment.ReportsFragment;
import tcc.lightapp.models.Report;
import tcc.lightapp.utils.Constants;

public class ReportsActivity extends BaseActivity {
    private DatabaseReference mDatabase;

    private ReportsFragment mFragment;
    private ProgressBar mProgressBar;

    private String mPatientUid;
    private String mPatientName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

        mFragment = new ReportsFragment();

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.reports_container,
                mFragment,
                ReportsFragment.class.getSimpleName());
        mFragment.setArguments(args);
        fragmentTransaction.commit();



//        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
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
//            Toast.makeText(mFragment.getContext(), getResources().getString(R.string.report_generating), Toast.LENGTH_SHORT).show();

            generateReport();

            return true;
        }
//        return super.onOptionsItemSelected(menuItem);
        return true;
    }

    public void generateReport(){
//        mProgressBar.setVisibility(View.VISIBLE);
                URL url = null;
                try {
                    url = new URL("https://us-central1-lightapp-d3dc5.cloudfunctions.net/doSentimentAnalysis?userUid=" + mPatientUid);
                } catch (MalformedURLException e) {
                    //erro
                }

                try {
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");

            String response = readStream(urlConnection.getInputStream());

            urlConnection.disconnect();

            response = response.replaceAll("[\\{\\}\"]+", "");
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

        } catch (IOException e) {
            //error
        }
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
