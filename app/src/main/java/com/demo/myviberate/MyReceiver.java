package com.demo.myviberate;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Administrator on 2016/4/23.
 */
public class MyReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		// 接收到广播
		Intent i = new Intent(context, AlertActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		int t1 = intent.getIntExtra("vibrateTime1", 0);
		int t2 = intent.getIntExtra("vibrateTime2", 0);
		int t3 = intent.getIntExtra("vibrateTime3", 0);
		int t4 = intent.getIntExtra("vibrateTime4", 0);
		Log.d ("intent", "广播接受的震动-"+t1+"-"+t2+"-"+t3+"-"+t4);

		i.putExtra("vibrateTime1", t1);
		i.putExtra("vibrateTime2", t2);
		i.putExtra("vibrateTime3", t3);
		i.putExtra("vibrateTime4", t4);

		Log.d ("intent", "广播发送的震动-"+t1+"-"+t2+"-"+t3+"-"+t4);


		context.startActivity(i);
	}
}
