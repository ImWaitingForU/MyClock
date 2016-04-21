package com.demo.myviberate;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

	private Button btn_setTime;
	private Button btn_finish_timeSpace;
	private Button btn_setVibrateTime;
	private Button btn_setVibrateFinish;
	private Button btn_cancelAlock;
	private RelativeLayout layout_time_left;
	private TextView tv_timeSpace_sec;
	private TextView tv_timeSpace_min;
	private NumberPicker np_min;
	private NumberPicker np_sec;
	private TextView tv_left;
	private EditText et_t1;
	private EditText et_t2;
	private EditText et_t3;
	private EditText et_t4;

	private View setTimeView;

	private View setVibrateView;
	// 设置默认震动模式
	private int vibrateTime1 = 100;
	private int vibrateTime2 = 2000;
	private int vibrateTime3 = 500;
	private int vibrateTime4 = 2000;

	private AlertDialog dialogSetTime;
	private AlertDialog dialogSetVibrate;

	private MyCountDown myCountDown;
	private static final int TIME_LEFT_COUNTDOWN = 0x111;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == TIME_LEFT_COUNTDOWN) {
				int timeLeft = msg.arg1;
				tv_left.setText(String.valueOf(timeLeft));
				if (timeLeft > 0) {
					Message message = new Message();
					message.what = TIME_LEFT_COUNTDOWN;
					message.arg1 = timeLeft - 1;
					handler.sendMessageDelayed(message, 1000);
				}
			}
		}
	};

	private void initView() {
		btn_setTime = (Button) findViewById(R.id.btn_setTime);
		btn_setVibrateTime = (Button) findViewById(R.id.btn_setVibrateTime);
		btn_cancelAlock = (Button) findViewById(R.id.btn_cancelAlock);
		tv_timeSpace_sec = (TextView) findViewById(R.id.tv_timeSpace_sec);
		tv_timeSpace_min = (TextView) findViewById(R.id.tv_timeSpace_min);
		tv_left = (TextView) findViewById(R.id.tv_timeLeft);
		layout_time_left = (RelativeLayout) findViewById(R.id.layout_time_left);

		setTimeView = LayoutInflater.from(this).inflate(R.layout.num_picker,
				null, false);
		setVibrateView = LayoutInflater.from(this).inflate(
				R.layout.set_vibrate_time, null, false);
		dialogSetTime = new AlertDialog.Builder(this).create();
		dialogSetVibrate = new AlertDialog.Builder(this).create();

		btn_setTime.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// 如果正在计时则不允许计时
				if (tv_left.getText().toString().equals("0")) {
					dialogSetTime.setView(setTimeView);
					dialogSetTime.setCanceledOnTouchOutside(false);
					dialogSetTime.show();
				} else {
					Toast.makeText(MainActivity.this, "上一个闹钟还没结束",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		btn_setVibrateTime.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialogSetVibrate.setView(setVibrateView);
				dialogSetVibrate.setTitle("设置震动方式");
				dialogSetVibrate.setCanceledOnTouchOutside(false);
				dialogSetVibrate.show();
			}
		});

		btn_cancelAlock.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				cancelAlock();
				tv_timeSpace_min.setText("0");
				tv_timeSpace_sec.setText("0");
				tv_left.setText("0");
				Toast.makeText(MainActivity.this, "闹钟已取消", Toast.LENGTH_SHORT)
						.show();
			}
		});

		initSetTimeView();

		initSetVibrate();

	}

	/*
	 * 初始化设置震动对话框
	 */
	private void initSetVibrate() {
		et_t1 = (EditText) setVibrateView.findViewById(R.id.et_t1);
		et_t2 = (EditText) setVibrateView.findViewById(R.id.et_t2);
		et_t3 = (EditText) setVibrateView.findViewById(R.id.et_t3);
		et_t4 = (EditText) setVibrateView.findViewById(R.id.et_t4);

		btn_setVibrateFinish = (Button) setVibrateView
				.findViewById(R.id.btn_setVibrateFinish);
		btn_setVibrateFinish.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (vibrateTime1 == 0 && vibrateTime2 == 0 && vibrateTime3 == 0
						&& vibrateTime4 == 0) {
					Toast.makeText(MainActivity.this, "未设置震动，使用默认模式",
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(MainActivity.this, "设置震动模式成功",
							Toast.LENGTH_SHORT).show();
					vibrateTime1 = Integer.valueOf(et_t1.getText().toString()) * 1000;
					vibrateTime2 = Integer.valueOf(et_t2.getText().toString()) * 1000;
					vibrateTime3 = Integer.valueOf(et_t3.getText().toString()) * 1000;
					vibrateTime4 = Integer.valueOf(et_t4.getText().toString()) * 1000;
					Log.d("time", vibrateTime1 + "-" + vibrateTime2 + "-"
							+ vibrateTime3 + "-" + vibrateTime4);
				}
				dialogSetVibrate.cancel();
			}
		});
	}

	/*
	 * 初始化设置时间对话框
	 */
	private void initSetTimeView() {
		np_min = (NumberPicker) setTimeView.findViewById(R.id.np_min);
		np_sec = (NumberPicker) setTimeView.findViewById(R.id.np_sec);
		np_min.setMinValue(0);
		np_min.setMaxValue(100); // 最大100分钟
		np_sec.setMinValue(0);
		np_sec.setMaxValue(60); // 最多60秒

		btn_finish_timeSpace = (Button) setTimeView
				.findViewById(R.id.btn_finish_timeSpace);
		btn_finish_timeSpace.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int min = np_min.getValue();
				int sec = np_sec.getValue();
				dialogSetTime.cancel();
				if (min == 0 && sec == 0) {
					Toast.makeText(MainActivity.this, "取消时间设定",
							Toast.LENGTH_SHORT).show();
				} else {
					// startCountDown(min, sec); // 开始倒数
					// startClock();// 启动闹钟
					tv_timeSpace_sec.setText(String.valueOf(sec));
					tv_timeSpace_min.setText(String.valueOf(min));
					myCountDown = new MyCountDown (min*60*1000+sec*1000,1000);
					myCountDown.start ();//开始计时
				}
			}
		});
	}

	/*
		自定义倒计时类,参数是总时长，时间间隔
	 */
	private class MyCountDown extends CountDownTimer{

		/**
		 * @param millisInFuture    The number of millis in the future from the call
		 *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
		 *                          is called.
		 * @param countDownInterval The interval along the way to receive
		 *                          {@link #onTick(long)} callbacks.
		 */
		public MyCountDown (long millisInFuture, long countDownInterval) {
			super (millisInFuture, countDownInterval);
		}

		@Override
		public void onTick (long millisUntilFinished) {
			tv_left.setText(millisUntilFinished/1000+"");
		}

		@Override
		public void onFinish () {
			tv_left.setText("0");
			startClock (); //倒计时结束启动闹钟
		}
	}

	/*
	 * 开始倒计时
	 */
	private void startCountDown(int min, int sec) {
		layout_time_left.setVisibility(View.VISIBLE);
		tv_timeSpace_sec.setText(String.valueOf(sec));
		tv_timeSpace_min.setText(String.valueOf(min));

		int count = min * 60 + sec;
		// 倒计时只用秒显示
		tv_left.setText(String.valueOf(count));

		Message message = new Message();
		message.what = TIME_LEFT_COUNTDOWN;
		message.arg1 = count - 1;
		handler.sendMessageDelayed(message, 1000);
	}

	/*
	 * 取消闹钟
	 */
	private void cancelAlock() {
		Intent intent = new Intent(MainActivity.this, AlertActivity.class);
		PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);
		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		alarmManager.cancel(pi);
		myCountDown.cancel ();
	}

	/*
	 * 启动闹钟
	 */
	private void startClock() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		// 设置定时 分/秒
		calendar.add(Calendar.MINUTE,
				Integer.parseInt(tv_timeSpace_min.getText().toString()));
		calendar.add(Calendar.SECOND,
				Integer.parseInt(tv_timeSpace_sec.getText().toString()));

		Intent intent = new Intent(MainActivity.this, AlertActivity.class);
		Log.d("timeSend", vibrateTime1 + "-" + vibrateTime2 + "-"
				+ vibrateTime3 + "-" + vibrateTime4);
		intent.putExtra("vibrateTime1", vibrateTime1);
		intent.putExtra("vibrateTime2", vibrateTime2);
		intent.putExtra("vibrateTime3", vibrateTime3);
		intent.putExtra("vibrateTime4", vibrateTime4);
		intent.putExtra("ttt", "tt");
		Log.d("timeSend2",
				intent.getIntExtra("vibrateTime1", 100) + "-"
						+ intent.getIntExtra("vibrateTime2", 100) + "-"
						+ intent.getIntExtra("vibrateTime3", 100) + "-"
						+ intent.getIntExtra("vibrateTime4", 100));

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
