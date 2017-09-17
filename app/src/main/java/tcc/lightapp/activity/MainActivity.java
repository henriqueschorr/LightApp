package tcc.lightapp.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import tcc.lightapp.R;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    FirebaseUser user;
    String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        Bundle args = intent.getExtras();

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        if(args != null) {
            userName = args.getString("nome");
        } else {
            userName = user.getDisplayName();
        }

        TextView mNome = (TextView) findViewById(R.id.nome);

        mNome.setText("Bem vindo " + userName);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        mAuth.signOut();
        finish();
        return true;
    }

}
