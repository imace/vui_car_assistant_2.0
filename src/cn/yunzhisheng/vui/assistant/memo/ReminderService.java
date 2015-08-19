/**
 * Copyright (c) 2012-2012 Yunzhisheng(Shanghai) Co.Ltd. All right reserved.
 * @FileName : ReminderService.java
 * @ProjectName : VAssistantMemo
 * @PakageName : cn.yunzhisheng.vui.assistant.memo
 * @Author : yuanzhe
 * @CreateDate : 2012-8-6
 */
package cn.yunzhisheng.vui.assistant.memo;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import cn.yunzhisheng.common.util.LogUtil;
import cn.yunzhisheng.vui.assistant.IDataControl;
import cn.yunzhisheng.vui.assistant.MainActivity;
import cn.yunzhisheng.vui.assistant.MainApplication;
import com.ilincar.voice.R;

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
public class ReminderService extends Service {
	public static final String TAG = "ReminderService";

	private IDataControl mDataControl = null;
	private ReminderManager mReminderManager;
	private NotificationManager mNotificationManager;
	private ServiceHandler mServiceHandler;
	private Looper mServiceLooper;

	/**
	 * @Description : onBind
	 * @Author : Brant
	 * @CreateDate : 2012-8-6
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mDataControl = ((MainApplication) getApplication()).getDataControl();
		mReminderManager = new ReminderManager(this);
		// Start up the thread running the service. Note that we create a
		// separate thread because the service normally runs in the process's
		// main thread, which we don't want to block.
		HandlerThread thread = new HandlerThread(TAG, Process.THREAD_PRIORITY_BACKGROUND);
		thread.start();

		mServiceLooper = thread.getLooper();
		mServiceHandler = new ServiceHandler(mServiceLooper);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Message msg = mServiceHandler.obtainMessage();
		msg.arg1 = startId;
		msg.obj = intent;
		mServiceHandler.sendMessage(msg);
		return Service.START_NOT_STICKY;
	}

	@Override
	public void onDestroy() {
		mServiceLooper.quit();
		super.onDestroy();
	}

	private final class ServiceHandler extends Handler {
		public ServiceHandler(Looper looper) {
			super(looper);
		}

		/**
		 * Handle incoming transaction requests.
		 * The incoming requests are initiated by the MMSC Server or by the MMS
		 * Client itself.
		 */
		@Override
		public void handleMessage(Message msg) {
			final int serviceId = msg.arg1;
			Intent intent = (Intent) msg.obj;

			LogUtil.v(TAG, "handleMessage serviceId: " + serviceId + " intent: " + intent);
			String action = intent.getAction();
			StringBuffer missedMemoStringBuilder = new StringBuffer();
			int missMemoNumber = 0;
			if (ReminderManager.ACTION_REGISTER_REMINDER.equals(action) && mDataControl != null) {
				Cursor cursor = mDataControl.getMemoCursor("status=1", null);
				while (cursor.moveToNext()) {
					long timespan = System.currentTimeMillis();
					int id = cursor.getInt(0);
					long time = cursor.getLong(3);
					if (time > timespan) {
						// register
						mReminderManager.register(id, time);
					} else if (time == timespan) {
						Intent reminderGoOff = new Intent(ReminderManager.ACTION_REMINDER_GO_OFF);
						reminderGoOff.putExtra(ReminderManager.REMINDER_ID, id);
						sendBroadcast(reminderGoOff);
					} else if (time > 0) {
						// Missed
						missMemoNumber++;
						String title = cursor.getString(1);
						missedMemoStringBuilder.append(title);
						missedMemoStringBuilder.append(",");
						mDataControl.markMemoGoOff(id);
					}
				}
				cursor.close();
				cursor = null;
				if (missedMemoStringBuilder.length() > 0) {
					missedMemoStringBuilder.deleteCharAt(missedMemoStringBuilder.length() - 1);
				}
				if (missMemoNumber > 0) {
					if (mNotificationManager == null) {
						mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
					}

					Notification notification = new Notification(R.drawable.ic_launcher, String.format(
						getString(R.string.missed_reminder_during_shutdown),
						missedMemoStringBuilder.toString()), System.currentTimeMillis());
					notification.defaults |= Notification.DEFAULT_SOUND;
					notification.flags |= Notification.FLAG_AUTO_CANCEL;
					notification.number = missMemoNumber;
					intent.setClass(ReminderService.this, MainActivity.class);
					PendingIntent pendingIntent = PendingIntent.getActivity(
						ReminderService.this,
						0,
						intent,
						PendingIntent.FLAG_ONE_SHOT);
					notification.setLatestEventInfo(
						ReminderService.this,
						getString(R.string.missed_reminder),
						missedMemoStringBuilder.toString(),
						pendingIntent);
					mNotificationManager.notify(0, notification);
				}
			}
			stopSelfResult(serviceId);
		}
	}
}
