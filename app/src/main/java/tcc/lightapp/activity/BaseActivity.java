package tcc.lightapp.activity;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

/**
 * Created by Henrique on 16/09/2017.
 */

public class BaseActivity extends AppCompatActivity {

    protected Context getContext() {
        return this;
    }

    protected void toast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
