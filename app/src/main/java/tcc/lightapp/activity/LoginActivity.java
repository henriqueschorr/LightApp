package tcc.lightapp.activity;


import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import tcc.lightapp.R;
import tcc.lightapp.models.User;
import tcc.lightapp.utils.Constants;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends BaseActivity implements
        View.OnClickListener {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static final String TAG = "EmailPassword";
    private EditText mEmailField;
    private EditText mPasswordField;
    private EditText mNameField;
    private Button mRegisterButton;
    private Button mLoginButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

        mEmailField = (EditText) findViewById(R.id.email);
        mPasswordField = (EditText) findViewById(R.id.password);
        mNameField = (EditText) findViewById(R.id.name);

        mRegisterButton = (Button) findViewById(R.id.register_button);
        mLoginButton = (Button) findViewById(R.id.login_button);


        mRegisterButton.setOnClickListener(this);
        mLoginButton.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        if (mAuth.getCurrentUser() != null) {
            //Navigate to Main Activity
            Intent intent = new Intent(getContext(), MainActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onRestart() {
        super.onRestart();
        setupLogin();
    }

    public void createAccount(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            String name = mNameField.getText().toString();

                            //Sets the Display Name of the User
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(name).build();
                            user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "User profile updated.");
                                    }
                                }
                            });

                            //Creates the user in the RealTime Database
                            createUser(name);

                            //Navigate to Main Activity
                            Intent intent = new Intent(getContext(), MainActivity.class);
                            Bundle params = new Bundle();
                            params.putString(Constants.ARG_USER_NAME, name);
                            intent.putExtras(params);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            toast("Authentication failed.");
                        }
                    }
                });
    }

    private void signIn(String email, String password) {
        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            //Navigate to Main Activity
                            Intent intent = new Intent(getContext(), MainActivity.class);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            toast("Authentication failed.");
                        }
                    }
                });
        // [END sign_in_with_email]
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.register_button) {
            //If you are registering, shows the Name field to register it
            setupRegister();

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        } else if (i == R.id.login_button) {
            if (mLoginButton.getText().equals(getString(R.string.action_register))) {
                validateForm();
                createAccount(mEmailField.getText().toString(), mPasswordField.getText().toString());
            } else {
                validateForm();
                signIn(mEmailField.getText().toString(), mPasswordField.getText().toString());
            }
        }
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Required.");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Required.");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        if (mNameField.getVisibility() == View.VISIBLE) {
            String name = mNameField.getText().toString();
            if (TextUtils.isEmpty(name)) {
                mNameField.setError("Required.");
                valid = false;
            } else {
                mNameField.setError(null);
            }
        }
        return valid;
    }

    public void createUser(String userName) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        String email = mAuth.getCurrentUser().getEmail();
        String userId = mAuth.getCurrentUser().getUid();
        String firebaseToken = FirebaseInstanceId.getInstance().getToken();

        User user = new User(userName, email, userId, firebaseToken);

        mDatabase.child(Constants.ARG_USERS).child(userId).setValue(user);
    }

    public void setupRegister() {
        mNameField.setVisibility(View.VISIBLE);
        TextInputLayout textInputLayout = (TextInputLayout) findViewById(R.id.name_parent);
        textInputLayout.setVisibility(View.VISIBLE);

        mRegisterButton.setVisibility(View.INVISIBLE);
        mLoginButton.setText(R.string.action_register);
    }

    public void setupLogin() {
        mNameField.setVisibility(View.INVISIBLE);
        TextInputLayout textInputLayout = (TextInputLayout) findViewById(R.id.name_parent);
        textInputLayout.setVisibility(View.INVISIBLE);

        mRegisterButton.setVisibility(View.VISIBLE);
        mLoginButton.setText(R.string.action_sign_in);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        setupLogin();
        return true;
    }


}

