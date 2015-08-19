/**
 * Copyright (c) 2012-2012 Yunzhisheng(Shanghai) Co.Ltd. All right reserved.
 * @FileName : BootReceiver.java
 * @ProjectName : VAssistantMemo
 * @PakageName : cn.yunzhisheng.vui.assistant.memo
 * @Author : yuanzhe
 * @CreateDate : 2012-8-6
 */
package cn.yunzhisheng.vui.assistant.memo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import cn.yunzhisheng.common.util.LogUtil;

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
public class ReminderBroadCastReceiver extends BroadcastReceiver {
	public static final String TAG = "BootReceiver";
	private static final String ACTION_BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED";
	public static final String ACTION_APP_UPGRADE = "cn.yunzhisheng.intent.action.APP_UPGRADE";

	/**
	 * @Description : onReceive
	 * @Author : Brant
	 * @CreateDate : 2012-8-6
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context,
	 * android.content.Intent)
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		LogUtil.d(TAG, "onReceive:" + intent);
		String action = intent.getAction();
		if (ACTION_BOOT_COMPLETED.equals(action) || ACTION_APP_UPGRADE.equals(action)) {
			intent.setClass(context, ReminderService.class);
			intent.setAction(ReminderManager.ACTION_REGISTER_REMINDER);
			context.startService(intent);
		} else if (ReminderManager.ACTION_REMINDER_GO_OFF.equals(action)) {
			int id = intent.getIntExtra(ReminderManager.REMINDER_ID, 0);
			if (id == 0) {
				LogUtil.e(TAG, "Receive id 0 memo!");
			} else {
//				ReminderGoOffActivity.push(id);
//				intent.setClass(context, ReminderGoOffActivity.class);
//				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				context.startActivity(intent);
			}
		}
	}
}
