package io.github.gatimus.spellingbee;


import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.app.AlertDialog.Builder;

public class About extends DialogFragment {

    private static final String TAG = "About:";
    private Builder builder;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.v(TAG, "Create");
        super.onCreateDialog(savedInstanceState);
        builder = new Builder(getActivity());
        builder.setTitle(R.string.action_about);
        builder.setMessage(R.string.msg_about);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Log.i(TAG, "Ok");
                dialog.dismiss();
            }
        });
        return builder.create();
    } //onCreateDialog

} //class