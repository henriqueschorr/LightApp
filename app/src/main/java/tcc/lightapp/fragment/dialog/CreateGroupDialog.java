package tcc.lightapp.fragment.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import tcc.lightapp.R;
import tcc.lightapp.models.GroupRoom;
import tcc.lightapp.utils.Constants;

/**
 * Created by Henrique on 04/10/2017.
 */

public class CreateGroupDialog extends DialogFragment {

    public static void showDialog(android.support.v4.app.FragmentManager fm) {
        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag("dialog_create_group");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        new CreateGroupDialog().show(ft, "dialog_create_group");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = (View) inflater.inflate(R.layout.dialog_create_group, null);
        final EditText groupNameField = (EditText) view.findViewById(R.id.group_name);
        final CheckBox isPrivateCheckBox = (CheckBox) view.findViewById(R.id.is_private);

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.create_group)
                .setView(view)
                .setPositiveButton(R.string.action_confirm,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                String groupName = groupNameField.getText().toString();
                                boolean isPrivate = isPrivateCheckBox.isChecked();
                                createGroup(groupName, isPrivate);
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

    public void createGroup(String groupName, boolean isPrivate) {

        FirebaseAuth auth = FirebaseAuth.getInstance();
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = auth.getCurrentUser();

        DatabaseReference groups = database.child(Constants.ARG_GROUPS).push();
        String groupKey = groups.getKey();

        GroupRoom groupRoom = new GroupRoom(groupKey, groupName, user.getUid(), isPrivate);

        groups.setValue(groupRoom);

        //TODO: add groups to events
//        database.child(Constants.ARG_USERS).child(user.getUid()).child(Constants.ARG_GROUP_ADMIN).child(groupKey).setValue(groupName);
    }
}
