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

		Intent intentFromBroadcast = getIntent();
		int getT1 = intentFromBroadcast.getIntExtra("vibrateTime1", 0);
		int getT2 = intentFromBroadcast.getIntExtra("vibrateTime2", 0);
		int getT3 = intentFromBroadcast.getIntExtra("vibrateTime3", 0);
		int getT4 = intentFromBroadcast.getIntExtra("vibrateTime4", 0);

		Log.d("intent", "闹钟接收到的震动-"+getT1 + "--" + getT2 + "--" + getT3 + "--" + getT4);

		vibrator.vibrate(new long[]{getT1, getT2, getT3, getT4}, 0);

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
