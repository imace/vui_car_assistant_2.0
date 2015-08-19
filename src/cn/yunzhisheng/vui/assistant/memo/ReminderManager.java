/**
 * Copyright (c) 2012-2012 Yunzhisheng(Shanghai) Co.Ltd. All right reserved.
 * @FileName : ReminderManager.java
 * @ProjectName : VAssistantMemo
 * @PakageName : cn.yunzhisheng.vui.assistant.memo
 * @Author : yuanzhe
 * @CreateDate : 2012-8-6
 */
package cn.yunzhisheng.vui.assistant.memo;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Brant
 * @CreateDate : 2012-8-6
 * @ModifiedBy : Brant
 * @ModifiedDate: 2012-8-6
 * @Modified:
 * 2012-8-6: 实现基本功能
 */
public class ReminderManager {
	public static final String TAG = "ReminderManager";
	public static final String ACTION_REGISTER_REMINDER = "cn.yunzhisheng.intent.action.REGISTER_REMINDER";
	public static final String ACTION_REMINDER_GO_OFF = "cn.yunzhisheng.intent.action.REMINDER_GO_OFF";
	public static final String REMINDER_ID = "reminder_id";
	private Context mContext;
	private AlarmManager mAlarmManager;

	public ReminderManager(Context context) {
		mContext = context;
		mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
	}

	public void register(int requestCode, long triggerAtTime) {
		Intent intent = new Intent(ACTION_REMINDER_GO_OFF);
		intent.putExtra(REMINDER_ID, requestCode);
		PendingIntent sender = PendingIntent.getBroadcast(mContext, requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		mAlarmManager.set(AlarmManager.RTC_WAKEUP, triggerAtTime, sender);
	}

	public void cancel(int requestCode) {
		Intent intent = new Intent(ACTION_REMINDER_GO_OFF);
		PendingIntent sender = PendingIntent.getBroadcast(mContext, requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		mAlarmManager.cancel(sender);
	}
}
