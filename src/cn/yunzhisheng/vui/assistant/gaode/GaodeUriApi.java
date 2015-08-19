/**
 * Copyright (c) 2012-2014 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * @FileName : GaodeUriApi.java
 * @ProjectName : vui_assistant
 * @PakageName : cn.yunzhisheng.vui.assistant.gaode
 * @Author : Conquer
 * @CreateDate : 2014-2-24
 */
package cn.yunzhisheng.vui.assistant.gaode;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import cn.yunzhisheng.common.util.LogUtil;
import cn.yunzhisheng.vui.assistant.baidu.BaiduMap;

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
public class GaodeUriApi {
	public static final String TAG = "GaodeUriApi";

	private static String act = Intent.ACTION_VIEW;
	private static String cat = Intent.CATEGORY_DEFAULT;
	private static String pkg = "com.autonavi.minimap";

	public static void showLocation(Context context, String title, String content, double lat, double lng) {
		Log.d(TAG, "showLocation title : " + title + " content :　" + content);
		String data = "androidamap://viewMap?sourceApplication=ishuoshuo&poiname=" + content + "&lat=" + lat + "&lon="
						+ lng + "&dev=0";
		// String data = "androidamap://myLocation?sourceApplication=ishuoshuo";
		Log.d(TAG, "URI " + data);
		Intent intent = new Intent(act);
		intent.addCategory(cat);
		intent.setPackage(pkg);
		intent.setData(Uri.parse(data.trim()));
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

	public static void showRoute(Context context, String model, double slat, double slng, String sName, String sCity,
									double dLat, double dLng, String dName, String dCity) {
		Log.d(TAG, "showRoute model :　" + model + " sName :　" + sName + " dName : " + dName);
		// dev 起终点是否偏移（0：lat 和lon 是已经加密后的，不需要国测加密 1：需要国测加密）
		// m 驾车方式 =0（速度快）=1（费用少） =2（路程短）=3 不走高速 =4（躲避拥堵）=5（不走高速且避免收费）
		// =6（不走高速且躲避拥堵） =7（躲避收费和拥堵） =8（不走高速躲避收费和拥堵）。
		// 公交 =0（速度快）=1（费用少） =2（换乘较少）=3（步行少）=4（舒适）=5（不乘地铁）
		// t = 1(公交) =2（驾车） =4(步行)
		// showType 显示方式 show=0 (列表) =1(地图)
		int m = 1;
		if (BaiduMap.ROUTE_MODE_DRIVING.equals(model)) {
			m = 2;
		} else if (BaiduMap.ROUTE_MODE_TRANSIT.equals(model)) {
			m = 1;
		} else if (BaiduMap.ROUTE_MODE_WALKING.equals(model)) {
			m = 4;
		}

		String data = "androidamap://route?sourceApplication=vui_car_assistant&" + "slat=" + slat + "&slon=" + slng
						+ "&sname=" + sName + "&dlat=" + dLat + "&dlon=" + dLng + "&dname=" + dName + "&"
						+ "dev=0&m=2&t=" + m + "&showType=1";
		Log.d(TAG, "showRoute data :" + data);
		Intent intent = new Intent(act);
		intent.addCategory(cat);
		intent.setPackage(pkg);
		intent.setData(Uri.parse(data.trim()));
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

	/**
	 * 
	 * @Description : startNavi
	 * @Author : Brant
	 * @CreateDate : 2015-1-16
	 * @param context
	 * @param toLat:目的地纬度
	 * @param toLng:目的地经度
	 * @param name:POI名称
	 * @param style:导航方式(0 速度快; 1 费用少; 2 路程短; 3 不走高速；4 躲避拥堵；5 不走高速且避免收费；6
	 * 不走高速且躲避拥堵；7 躲避收费和拥堵；8 不走高速躲避收费和拥堵))
	 * @param dev:是否偏移(0:lat 和 lon 是已经加密后的,不需要国测加密; 1:需要国测加密)
	 */
	public static void startNavi(Context context, double toLat, double toLng, String name, int style, int dev) {
		LogUtil.d(TAG, "startNavi:toLat " + toLat + ",toLng " + toLng + ",name " + name + ",style " + style + ",dev "
						+ dev);

		AMapUri uri = new AMapUri("navi");
		uri.addParam("sourceApplication", "vui_car_assistant");
		uri.addParam("poiname", name);
		uri.addParam("lat", toLat);
		uri.addParam("lon", toLng);
		uri.addParam("dev", dev);
		uri.addParam("style", style);

		String dat = uri.getDatString();
		LogUtil.d(TAG, "dat=" + dat);

		Intent intent = new Intent();
		intent.addCategory(cat);
		intent.setPackage(pkg);
		intent.setData(Uri.parse(dat));
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

	public static void openAMap(Context context) {
		String data = "androidamap://route?sourceApplication=ishuoshuo";
		Intent intent = new Intent(act);
		intent.addCategory(cat);
		intent.setPackage(pkg);
		intent.setData(Uri.parse(data.trim()));
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}
}
