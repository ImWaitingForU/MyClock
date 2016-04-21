package com.demo.myviberate;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class AlertActivity extends AppCompatActivity {

	private Vibrator vibrator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alert);

		vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

		Intent intent = getIntent();
		int t1 = intent.getIntExtra("vibrateTime1", 0);
		int t2 = intent.getIntExtra("vibrateTime2", 0);
		int t3 = intent.getIntExtra("vibrateTime3", 0);
		int t4 = intent.getIntExtra("vibrateTime4", 0);

        Log.d("timeget", intent.toString ());
		Log.d("timeget", t1 + "--" + t2 + "--" + t3 + "--" + t4);

		vibrator.vibrate(new long[]{t1, t2, t3, t4}, 0);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("闹钟响啦~~")
				.setOnDismissListener(new DialogInterface.OnDismissListener() {
					@Override
					public void onDismiss(DialogInterface dialog) {
						vibrator.cancel();
						AlertActivity.this.finish();
					}
				}).show();
	}
}
