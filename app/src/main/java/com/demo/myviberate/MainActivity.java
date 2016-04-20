package com.demo.myviberate;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

	private Button btn_setTime;
	private Button btn_setVibrate;
	private Button btn_finish_timeSpace;
	private RelativeLayout layout_time_left;
	private TextView tv_timeSpace;
	private NumberPicker numberPicker;
	private TextView tv_left;

	private Button btn_stopVibrate;

	private Vibrator vibrator;

	private static final int TIME_LEFT_COUNTDOWN = 0x111;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == TIME_LEFT_COUNTDOWN) {
				int timeLeft = msg.arg1;
				tv_left.setText (String.valueOf (timeLeft));
				if (timeLeft>0){
					Message message = new Message ();
					message.what = TIME_LEFT_COUNTDOWN;
					message.arg1 = timeLeft - 1;
					handler.sendMessageDelayed (message,1000);
				}
			}
		}
	};

	private void initView() {
		btn_setTime = (Button) findViewById(R.id.btn_setTime);
		btn_setVibrate = (Button) findViewById(R.id.btn_setVibrate);
		tv_timeSpace = (TextView) findViewById(R.id.tv_timeSpace);
		tv_left = (TextView) findViewById(R.id.tv_timeLeft);
		layout_time_left = (RelativeLayout) findViewById(R.id.layout_time_left);

		final View view = LayoutInflater.from(this).inflate(
				R.layout.num_picker, null, false);

		final AlertDialog dialog = new AlertDialog.Builder(this).create();

		btn_setTime.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.setView(view);
				dialog.setCanceledOnTouchOutside(false);
				dialog.show();
			}
		});

		numberPicker = (NumberPicker) view.findViewById(R.id.numberPicker);
		numberPicker.setMinValue(1);
		numberPicker.setMaxValue(24);

		btn_finish_timeSpace = (Button) view
				.findViewById(R.id.btn_finish_timeSpace);
		btn_finish_timeSpace.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int count = numberPicker.getValue();
				dialog.cancel();
				startCountDown(count); // 开始倒数
				startClock();// 启动闹钟
			}
		});

	}

	/*
	 * 开始倒计时
	 */
	private void startCountDown(int count) {
		layout_time_left.setVisibility(View.VISIBLE);
		tv_timeSpace.setText(String.valueOf(count));
		tv_left.setText(String.valueOf(count));

		Message message = new Message ();
		message.what = TIME_LEFT_COUNTDOWN;
		message.arg1 = count - 1;
		handler.sendMessageDelayed (message,1000);
	}

	/*
	 * 启动闹钟
	 */
	private void startClock() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.set(Calendar.SECOND,
				Integer.parseInt(tv_timeSpace.getText().toString()));

		Intent intent = new Intent(MainActivity.this, AlertActivity.class);
		intent.putExtra("msg", "你该打酱油了");
		PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);

		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
				pi);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
	}
}
