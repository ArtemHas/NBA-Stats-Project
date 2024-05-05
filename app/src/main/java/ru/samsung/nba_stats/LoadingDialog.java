package ru.samsung.nba_stats;

import android.app.Activity;
import android.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;

public class LoadingDialog {
    private Activity activity;
    private AlertDialog dialog;
    public LoadingDialog(Activity myActivity){
        this.activity = myActivity;
    }
    void startLoadingAnimation(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.custom_dialog, null));
        builder.setCancelable(false);
        dialog = builder.create();
        dialog.show();
        Log.e("the dialog should be shown", "the dialog should be shown");
    }
    void dismissDialog(){
        dialog.dismiss();
    }
}
