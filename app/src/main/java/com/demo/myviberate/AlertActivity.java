package com.demo.myviberate;

import android.content.DialogInterface;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class AlertActivity extends AppCompatActivity {

    private Vibrator  vibrator;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_alert);

        vibrator = (Vibrator) getSystemService (VIBRATOR_SERVICE);
        vibrator.vibrate (new long[]{0,2000,500,2000},0);

        AlertDialog.Builder builder = new AlertDialog.Builder (this);
        builder.setMessage ("闹钟响啦~~").setOnDismissListener (new DialogInterface.OnDismissListener () {
            @Override
            public void onDismiss (DialogInterface dialog) {
                vibrator.cancel ();
                AlertActivity.this.finish ();
            }
        }).show ();
    }
}
