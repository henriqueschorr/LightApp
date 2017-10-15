package tcc.lightapp.fragment.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import tcc.lightapp.R;
import tcc.lightapp.models.User;
import tcc.lightapp.utils.Constants;

/**
 * Created by Henrique on 14/10/2017.
 */

public class AddPatientDialog extends DialogFragment {

    public static void showDialog(android.support.v4.app.FragmentManager fm) {
        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag("dialog_add_patient");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        new AddPatientDialog().show(ft, "dialog_add_patient");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = (View) inflater.inflate(R.layout.dialog_add_patient, null);
        final EditText patientEmailField = (EditText) view.findViewById(R.id.patient_email);

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.add_patient)
                .setView(view)
                .setPositiveButton(R.string.action_add,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                String patientEmail = patientEmailField.getText().toString();
                                addPatient(patientEmail);
                                dialog.dismiss();
                            }
                        }
                ).setNegativeButton(R.string.action_cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        }).create();
    }

    public void addPatient(final String patientEmail) {

        FirebaseAuth auth = FirebaseAuth.getInstance();
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference userDatabase = database.child(Constants.ARG_USERS);
        final FirebaseUser user = auth.getCurrentUser();

        userDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    User patient = userSnapshot.getValue(User.class);
                    if(patient.email.equals(patientEmail)) {
                        userDatabase.child(user.getUid()).child(Constants.ARG_PATIENTS).child(patient.authID).setValue(patient.userName + "_" + patientEmail);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
