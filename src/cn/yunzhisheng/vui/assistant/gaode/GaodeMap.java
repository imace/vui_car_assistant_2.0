/**
 * Copyright (c) 2012-2014 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * @FileName : GaodeMap.java
 * @ProjectName : vui_assistant
 * @PakageName : cn.yunzhisheng.vui.assistant.baidu
 * @Author : Conquer
 * @CreateDate : 2014-2-24
 */
package cn.yunzhisheng.vui.assistant.gaode;

import cn.yunzhisheng.common.util.LogUtil;
import com.ilincar.voice.R;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Handler;
import android.widget.Toast;

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
public class GaodeMap {
	public static final String TAG = "GaodeMap";
	public static final String ROUTE_MODE_WALKING = "walking"; // 步行
	public static final String ROUTE_MODE_DRIVING = "driving"; // 驾车
	public static final String ROUTE_MODE_TRANSIT = "transit"; // 公交
	private static boolean mHasGaodeMapClient = false;

	// public static void init(Context context) {
	// mHasGaodeMapClient = hasGaodeMapClient(context);
	// }

	private static boolean hasGaodeMapClient(Context context) {
		PackageInfo packageInfo;
		try {
			packageInfo = context.getPackageManager().getPackageInfo("com.autonavi.minimap", 0);
		} catch (NameNotFoundException e) {
			packageInfo = null;
		}
		if (packageInfo == null) {
			return false;
		} else {
			return true;
		}
	}

	public static void showLocation(Context context, String title, String content, double lat, double lng) {
		if (context == null) {
			return;
		}

		mHasGaodeMapClient = hasGaodeMapClient(context);
		if (mHasGaodeMapClient) {
			GaodeUriApi.showLocation(context, title, content, lat, lng);
		} else {
			GaodeMapSdk.showLocation(context, title, content, lat, lng);
			// Toast.makeText(context, "没有 客户端~~~~", 2000).show();
		}
	}

	public static void showRoute(Context context, String mode, double fromLat, double fromLng, String fromCity,
									String originFromPOI, String fromPoi, double toLat, double toLng, String toCity,
									String toPoi) {
		if (context == null) {
			LogUtil.e(TAG, "showRoute:context null!");
			return;
		}

		mHasGaodeMapClient = hasGaodeMapClient(context);
		if (mHasGaodeMapClient) {
			
//			if ("CURRENT_LOC".equals(originFromPOI)) {
				GaodeUriApi.startNavi(context, toLat, toLng, toPoi, 2, 0);
//			} else {
//				GaodeUriApi.showRoute(context, mode, fromLat, fromLng, fromCity, fromPoi, toLat, toLng, toCity, toPoi);
//			}
		} else {
			Toast.makeText(context, R.string.gaode_install_map, Toast.LENGTH_SHORT).show();
			GaodeMapSdk.showRoute(context, mode, fromLat, fromLng, fromCity, fromPoi, toLat, toLng, toCity, toPoi);
		}
	}

	public static void openAMapClient(Context context) {
		if (context == null) {
			return;
		}

		mHasGaodeMapClient = hasGaodeMapClient(context);
		if (mHasGaodeMapClient) {
			GaodeUriApi.openAMap(context);
		} else {
			Toast.makeText(context, R.string.gaode_nofind_map, Toast.LENGTH_SHORT).show();
		}
	}
}
