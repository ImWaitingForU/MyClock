package com.demo.myviberate;

import java.util.ArrayList;
import java.util.List;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

    private AlertDialog dialogSetTime;
    private AlertDialog dialogSetVibrate;

    private MyCountDown myCountDown;

    private boolean isAlramming; // 判断当前是否存在一个倒计时的闹钟

    private RadioGroup radioGroup;
    private List<List<Integer>> vibrateList; // 存放用户自定义振动模式,每一个模式用List保存四个值

    private void initView () {
        btn_setTime = (Button) findViewById (R.id.btn_setTime);
        btn_setVibrateTime = (Button) findViewById (R.id.btn_setVibrateTime);
        btn_cancelAlock = (Button) findViewById (R.id.btn_cancelAlock);
        tv_timeSpace_sec = (TextView) findViewById (R.id.tv_timeSpace_sec);
        tv_timeSpace_min = (TextView) findViewById (R.id.tv_timeSpace_min);
        tv_left = (TextView) findViewById (R.id.tv_timeLeft);
        layout_time_left = (RelativeLayout) findViewById (R.id.layout_time_left);
        radioGroup = (RadioGroup) findViewById (R.id.radioGroup);
        vibrateList = new ArrayList<> ();
        // 把默认震动添加到集合里
        addToVibrateList (500, 2000, 500, 2000);

        setTimeView = LayoutInflater.from (this).inflate (R.layout.num_picker, null, false);
        setVibrateView = LayoutInflater.from (this).inflate (R.layout.set_vibrate_time, null, false);
        dialogSetTime = new AlertDialog.Builder (this).create ();
        dialogSetVibrate = new AlertDialog.Builder (this).create ();

        btn_setTime.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                // 如果正在计时则不允许计时
                if (tv_left.getText ().toString ().equals ("0")) {
                    dialogSetTime.setView (setTimeView);
                    dialogSetTime.setCanceledOnTouchOutside (false);
                    dialogSetTime.show ();
                } else {
                    Toast.makeText (MainActivity.this, "上一个闹钟还没结束", Toast.LENGTH_SHORT).show ();
                }
            }
        });

        btn_setVibrateTime.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                dialogSetVibrate.setView (setVibrateView);
                dialogSetVibrate.setTitle ("设置震动方式");
                dialogSetVibrate.setCanceledOnTouchOutside (false);
                dialogSetVibrate.show ();
            }
        });

        btn_cancelAlock.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                if (isAlramming) {
                    cancelAlock ();
                    layout_time_left.setVisibility (View.GONE);
                    tv_timeSpace_min.setText ("0");
                    tv_timeSpace_sec.setText ("0");
                    tv_left.setText ("0");
                    Toast.makeText (MainActivity.this, "闹钟已取消", Toast.LENGTH_SHORT).show ();
                } else {
                    Toast.makeText (MainActivity.this, "未启动闹钟", Toast.LENGTH_SHORT).show ();
                }
            }
        });

        radioGroup.setOnCheckedChangeListener (new RadioGroup.OnCheckedChangeListener () {
            @Override
            public void onCheckedChanged (RadioGroup group, int checkedId) {
                Toast.makeText (MainActivity.this, "onChecedChanged", Toast.LENGTH_SHORT).show ();

            }
        });

        initSetTimeView ();
        initSetVibrate ();

    }

    /*
     * 初始化设置震动对话框
     */
    private void initSetVibrate () {
        et_t1 = (EditText) setVibrateView.findViewById (R.id.et_t1);
        et_t2 = (EditText) setVibrateView.findViewById (R.id.et_t2);
        et_t3 = (EditText) setVibrateView.findViewById (R.id.et_t3);
        et_t4 = (EditText) setVibrateView.findViewById (R.id.et_t4);

        btn_setVibrateFinish = (Button) setVibrateView.findViewById (R.id.btn_setVibrateFinish);
        btn_setVibrateFinish.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                if (et_t1.getText ().toString ().equals ("0") && et_t2.getText ().toString ().equals ("0") &&
                        et_t3.getText ().toString ().equals ("0") && et_t4.getText ().toString ().equals ("0")) {
                    Toast.makeText (MainActivity.this, "未设置震动", Toast.LENGTH_SHORT).show ();
                } else {
                    Toast.makeText (MainActivity.this, "设置震动模式成功", Toast.LENGTH_SHORT).show ();
                    // 设置完成，保存震动模式到vibrateList,并更新RadioButton
                    addToVibrateList (Integer.valueOf (et_t1.getText ().toString ()) * 1000,
                                      Integer.valueOf (et_t2.getText ().toString ()) * 1000,
                                      Integer.valueOf (et_t3.getText ().toString ()) * 1000,
                                      Integer.valueOf (et_t4.getText ().toString ()) * 1000);
                }
                dialogSetVibrate.cancel ();
            }
        });
    }

    /*
     * 初始化设置时间对话框
     */
    private void initSetTimeView () {
        np_min = (NumberPicker) setTimeView.findViewById (R.id.np_min);
        np_sec = (NumberPicker) setTimeView.findViewById (R.id.np_sec);
        np_min.setMinValue (0);
        np_min.setMaxValue (100); // 最大100分钟
        np_sec.setMinValue (0);
        np_sec.setMaxValue (60); // 最多60秒

        btn_finish_timeSpace = (Button) setTimeView.findViewById (R.id.btn_finish_timeSpace);
        btn_finish_timeSpace.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                int min = np_min.getValue ();
                int sec = np_sec.getValue ();
                dialogSetTime.cancel ();
                if (isAlramming) {
                    Toast.makeText (MainActivity.this, "已存在一个闹钟了", Toast.LENGTH_SHORT).show ();
                    return;
                }
                if (min == 0 && sec == 0) {
                    Toast.makeText (MainActivity.this, "取消时间设定", Toast.LENGTH_SHORT).show ();
                } else {
                    layout_time_left.setVisibility (View.VISIBLE);
                    tv_timeSpace_sec.setText (String.valueOf (sec));
                    tv_timeSpace_min.setText (String.valueOf (min));
                    startClock ();// 启动闹钟
                    myCountDown = new MyCountDown (min * 60 * 1000 + sec * 1000 + 1000, 1000);
                    myCountDown.start ();// 开始计时
                    isAlramming = true; // 正在进行一个闹钟
                }
            }
        });
    }

    /*
     * 自定义倒计时类,参数是总时长，时间间隔
     */
    private class MyCountDown extends CountDownTimer {

        /**
         *
         * @param millisInFuture    The number of millis in the future from the call to
         *                          {@link #start()} until the countdown is done and
         *                          {@link #onFinish()} is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public MyCountDown (long millisInFuture, long countDownInterval) {
            super (millisInFuture, countDownInterval);
        }

        @Override
        public void onTick (long millisUntilFinished) {
            tv_left.setText (millisUntilFinished / 1000 + "");
        }

        @Override
        public void onFinish () {
            Log.d ("intent", "onFinish()");
            tv_left.setText ("0");
            isAlramming = false; // 一个闹钟结束了
            layout_time_left.setVisibility (View.GONE);
        }
    }

    /*
     * 取消闹钟
     */
    private void cancelAlock () {
        Intent intent = new Intent (MainActivity.this, MyReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast (this, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService (ALARM_SERVICE);
        alarmManager.cancel (pi);
        myCountDown.cancel ();
        isAlramming = false;
    }

    /*
     * 设置闹钟,在确定闹钟时间间隔后调用
     */
    private void startClock () {

        //获取当前选定的震动模式
        int vibrateTime1 = vibrateList.get (radioGroup.getCheckedRadioButtonId ()).get (0);
        int vibrateTime2 = vibrateList.get (radioGroup.getCheckedRadioButtonId ()).get (1);
        int vibrateTime3 = vibrateList.get (radioGroup.getCheckedRadioButtonId ()).get (2);
        int vibrateTime4 = vibrateList.get (radioGroup.getCheckedRadioButtonId ()).get (3);

        Intent intent = new Intent (MainActivity.this, MyReceiver.class);
        intent.putExtra ("vibrateTime1", vibrateTime1);
        intent.putExtra ("vibrateTime2", vibrateTime2);
        intent.putExtra ("vibrateTime3", vibrateTime3);
        intent.putExtra ("vibrateTime4", vibrateTime4);

        Log.d ("intent", "发送的震动-" + vibrateTime1 + "-" + vibrateTime2 + "-" + vibrateTime3 + "-" + vibrateTime4);

        PendingIntent pi = PendingIntent.getBroadcast (this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        long firstTime = SystemClock.elapsedRealtime ();
        long intervalTime = Integer.parseInt (tv_timeSpace_min.getText ().toString ()) * 60 * 1000 +
                Integer.parseInt ((tv_timeSpace_sec.getText ().toString ()))* 1000+ 1000;
        Log.d ("intent", "interval--" + intervalTime / 1000);

        AlarmManager alarmManager = (AlarmManager) getSystemService (ALARM_SERVICE);
        alarmManager.setRepeating (AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime, intervalTime, pi);
    }

    /*
     * 把振动模式添加到vibrate集合里,并更新radiogroup视图
     */
    private void addToVibrateList (int mVibrateTime1, int mVibrateTime2, int mVibrateTime3, int mVibrateTime4) {
        List<Integer> customVibrateList = new ArrayList<Integer> ();
        customVibrateList.add (mVibrateTime1);
        customVibrateList.add (mVibrateTime2);
        customVibrateList.add (mVibrateTime3);
        customVibrateList.add (mVibrateTime4);
        vibrateList.add (customVibrateList);

        RadioButton radioButton = (RadioButton) LayoutInflater.from (MainActivity.this)
                                                              .inflate (R.layout.radio_layout, radioGroup, false);
        //设置新rb的id为集合大小-1,即从0开始
        radioButton.setId (vibrateList.size () - 1);

        if (vibrateList.size () == 1) {
            radioButton.setText ("默认震动");
        } else {
            radioButton.setText ("自定义震动" + (vibrateList.size () - 1));
        }

        radioGroup.addView (radioButton);

        //添加完后设置选中这个rb
        radioButton.setChecked (true);
    }

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_main);
        initView ();
    }
}
