/**
 * Copyright (c) 2012-2014 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * @FileName : GaodeMapSdk.java
 * @ProjectName : vui_assistant
 * @PakageName : cn.yunzhisheng.vui.assistant.gaode
 * @Author : Conquer
 * @CreateDate : 2014-2-24
 */
package cn.yunzhisheng.vui.assistant.gaode;

import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Conquer
 * @CreateDate : 2014-2-24
 * @ModifiedBy : Conquer
 * @ModifiedDate: 2014-2-24
 * @Modified:
 * 2014-2-24: 实现基本功能
 */
public class GaodeMapSdk {
	public static final String TAG = "GaodeMapSdk";

	public static void showLocation(Context context, String title, String content, double lat, double lng) {
		if (context == null) {
			return;
		}

		Intent intentRoutePlan = new Intent(context, GaodeLocationActivity.class);
		intentRoutePlan.putExtra(GaodeLocationActivity.TAG_LAT, lat);
		intentRoutePlan.putExtra(GaodeLocationActivity.TAG_LNG, lng);
		intentRoutePlan.putExtra(GaodeLocationActivity.TAG_TITLE, title);
		intentRoutePlan.putExtra(GaodeLocationActivity.TAG_CONTENT, content);
		intentRoutePlan.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intentRoutePlan);
	}

	public static void showRoute(Context context, String mode, double fromLat, double fromLng, String fromCity,
									String fromPoi, double toLat, double toLng, String toCity, String toPoi) {
		if (context == null) {
			return;
		}

		Intent intentRoutePlan = new Intent(context, GaodeRouteActivity2.class);
		intentRoutePlan.putExtra(GaodeRouteActivity2.TAG_MODE, mode);
		intentRoutePlan.putExtra(GaodeRouteActivity2.TAG_FROM_LAT, fromLat);
		intentRoutePlan.putExtra(GaodeRouteActivity2.TAG_FROM_LNG, fromLng);
		intentRoutePlan.putExtra(GaodeRouteActivity2.TAG_FROM_CITY, fromCity);
		intentRoutePlan.putExtra(GaodeRouteActivity2.TAG_FROM_POI, fromPoi);
		intentRoutePlan.putExtra(GaodeRouteActivity2.TAG_TO_LAT, toLat);
		intentRoutePlan.putExtra(GaodeRouteActivity2.TAG_TO_LNG, toLng);
		intentRoutePlan.putExtra(GaodeRouteActivity2.TAG_TO_CITY, toCity);
		intentRoutePlan.putExtra(GaodeRouteActivity2.TAG_TO_POI, toPoi);	
		intentRoutePlan.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intentRoutePlan);
	}
}
