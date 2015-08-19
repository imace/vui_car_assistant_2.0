/**
 * Copyright (c) 2012-2013 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * @FileName : BaiduMap.java
 * @ProjectName : vui_assistant
 * @PakageName : cn.yunzhisheng.vui.assistant.baidu
 * @Author : Dancindream
 * @CreateDate : 2013-9-6
 */
package cn.yunzhisheng.vui.assistant.baidu;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.navi.BaiduMapNavigation;
import com.baidu.mapapi.navi.NaviPara;

import cn.yunzhisheng.common.util.LogUtil;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Dancindream
 * @CreateDate : 2013-9-6
 * @ModifiedBy : Dancindream
 * @ModifiedDate: 2013-9-6
 * @Modified:
 * 2013-9-6: 实现基本功能
 */
public class BaiduMap {
	public static final String TAG = "BaiduMap";
	public static final String ROUTE_MODE_WALKING = "walking"; // 步行
	public static final String ROUTE_MODE_DRIVING = "driving"; // 驾车
	public static final String ROUTE_MODE_TRANSIT = "transit"; // 公交
	private static boolean mHasBaiduMapClient = false;

	public static void init(Context context) {
		mHasBaiduMapClient = hasBaiduMapClient(context);
	}

	private static boolean hasBaiduMapClient(Context context) {
		PackageInfo packageInfo;
		try {
			packageInfo = context.getPackageManager().getPackageInfo("com.baidu.BaiduMap", 0);
		} catch (NameNotFoundException e) {
			packageInfo = null;
		}
		if (packageInfo == null) {
			return false;
		} else {
			return true;
		}
	}

	public static void showLocation(Context context, String title, String content, String lat, String lng) {
		if (context == null) {
			return;
		}

		mHasBaiduMapClient = hasBaiduMapClient(context);
		if (mHasBaiduMapClient) {
			BaiduUriApi.showLocation(context, title, content, lat, lng);
		} else {
			BaiduMapSdk.showLocation(context, title, content, lat, lng);
		}
	}
	
	
	 public static void startNavi(double mLat1 , double mLon1, double mLat2 , double mLon2, Context a) {
	    	LogUtil.i(TAG, "-starNav-" +mLat1+" "+ mLon1 +" " +mLat2+" "+mLon2);
	    	
	    	LatLng start = new LatLng(mLat1, mLon1); 
	  		LatLng end = new LatLng(mLat2, mLon2); 
	    	  
			NaviPara params = new NaviPara();
			params.endName = "到这里结束";
			params.endPoint = end;
			params.startName = "从这里开始";
			params.startPoint = start;
			BaiduMapNavigation.openBaiduMapNavi(params, a);
		}

	public static void showRoute(Context context, String mode, double fromLat, double fromLng, String fromCity,
									String fromPoi, double toLat, double toLng, String toCity, String toPoi) {
		if (context == null) {
			LogUtil.e(TAG, "showRoute:context null!");
			return;
		}

		//add by ch 最新的百度sdk 不需要判断是否安装,百度接口默认自己判断
		mHasBaiduMapClient = hasBaiduMapClient(context);
//		if (!mHasBaiduMapClient) {
		BaiduMapSdk.showRoute(context,mHasBaiduMapClient , mode, fromLat, fromLng, fromCity, fromPoi, toLat, toLng, toCity, toPoi);
//		} else {
//			BaiduUriApi.showRoute(context, mode, fromLat, fromLng, fromCity, fromPoi, toLat, toLng, toCity, toPoi);
//		}
	}

}
