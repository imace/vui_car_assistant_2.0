/**
 * Copyright (c) 2012-2014 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * @FileName : KillNotificationService.java
 * @ProjectName : vui_car_assistant
 * @PakageName : cn.yunzhisheng.vui.assistant
 * @Author : Brant
 * @CreateDate : 2014-12-8
 */
package cn.yunzhisheng.vui.assistant;

import cn.yunzhisheng.common.util.LogUtil;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Brant
 * @CreateDate : 2014-12-8
 * @ModifiedBy : Brant
 * @ModifiedDate: 2014-12-8
 * @Modified:
 * 2014-12-8: 实现基本功能
 */
public class KillNotificationService extends Service {
	public static final String TAG = "KillNotificationService";

	public static int NOTIFICATION_ID = 0;
	private NotificationManager mNotificationManager;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return Service.START_STICKY;
	}

	@Override
	public void onCreate() {
		LogUtil.d(TAG, "onCreate");
		mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		mNotificationManager.cancel(NOTIFICATION_ID);
	}

}
