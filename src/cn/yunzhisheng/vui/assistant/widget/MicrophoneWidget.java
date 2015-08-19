/**
 * Copyright (c) 2012-2014 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * @FileName : MicrophoneWidget.java
 * @ProjectName : vui_car_assistant
 * @PakageName : cn.yunzhisheng.vui.assistant.widget
 * @Author : Brant
 * @CreateDate : 2014-11-26
 */
package cn.yunzhisheng.vui.assistant.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import cn.yunzhisheng.common.util.LogUtil;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Brant
 * @CreateDate : 2014-11-26
 * @ModifiedBy : Brant
 * @ModifiedDate: 2014-11-26
 * @Modified:
 * 2014-11-26: 实现基本功能
 */
public class MicrophoneWidget extends AppWidgetProvider {
	public static final String TAG = "MicrophoneWidget";

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		LogUtil.d(TAG, "onUpdate");
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		LogUtil.d(TAG, "onReceive:intent " + intent);
		super.onReceive(context, intent);
		final String action = intent.getAction();
		// if (ACTION_UPDATE_ALL.equals(action)) {
		// // “更新”广播
		// updateAllAppWidgets(context, AppWidgetManager.getInstance(context),
		// idsSet);
		// } else if (intent.hasCategory(Intent.CATEGORY_ALTERNATIVE)) {
		// // “按钮点击”广播
		// Uri data = intent.getData();
		// int buttonId = Integer.parseInt(data.getSchemeSpecificPart());
		// if (buttonId == BUTTON_SHOW) {
		// Log.d(TAG, "Button wifi clicked");
		// Toast.makeText(context, "Button Clicked", Toast.LENGTH_SHORT).show();
		// }
		// }

	}
}
