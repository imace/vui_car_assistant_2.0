/**
 * Copyright (c) 2012-2013 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * @FileName : RouteShowSession.java
 * @ProjectName : vui_assistant
 * @PakageName : cn.yunzhisheng.vui.assistant.session
 * @Author : Dancindream
 * @CreateDate : 2013-9-6
 */
package cn.yunzhisheng.vui.assistant.session;

import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;
import cn.yunzhisheng.common.util.LogUtil;
import cn.yunzhisheng.preference.PrivatePreference;
import cn.yunzhisheng.vui.assistant.baidu.BaiduMap;
import com.ilincar.voice.R;
import cn.yunzhisheng.vui.assistant.gaode.GaodeMap;
import cn.yunzhisheng.vui.assistant.preference.SessionPreference;
import cn.yunzhisheng.vui.assistant.preference.UserPreference;
import cn.yunzhisheng.vui.assistant.view.RoteSeesionConfirmView;
import cn.yunzhisheng.vui.assistant.view.RoteSeesionConfirmView.IRoteContentViewListener;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Dancindream
 * @CreateDate : 2013-9-6
 * @ModifiedBy : Dancindream
 * @ModifiedDate: 2013-9-6
 * @Modified: 2013-9-6: 实现基本功能
 */
public class RouteShowSession extends BaseSession {
	public static final String TAG = "RouteShowSession";

	// private RouteContentView mRouteContentView = null;
	private static final int DELAY_CALL_TIME = 5000;
	public String mCancelProtocal  = "";
	
	private RoteSeesionConfirmView mRoteView = null;
	private IRoteContentViewListener mRoteListener = new IRoteContentViewListener(){

		@Override
		public void onCancel() {
			// TODO Auto-generated method stub
			onUiProtocal(mCancelProtocal);
		}

		@Override
		public void onOk() {
			// TODO Auto-generated method stub
			mSessionManagerHandler
			.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_DONE);
			if ("AMAP".equals(poiVendor) || "GAODE".equals(poiVendor)) {
				GaodeMap.showRoute(mContext, BaiduMap.ROUTE_MODE_DRIVING, fromLat,
						fromLng, fromCity, originFromPOI, fromPoi, toLat, toLng,
						toCity, toPoi);
			} else if ("BAIDU".equals(poiVendor)) {
				BaiduMap.showRoute(mContext, BaiduMap.ROUTE_MODE_DRIVING, fromLat,
						fromLng, fromCity, fromPoi, toLat, toLng, toCity, toPoi);
			} else {
				Toast.makeText(mContext, "Unsupported map.", Toast.LENGTH_SHORT)
					.show();
			}
			mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_DONE);
		}
		
	};

	/**
	 * @Author : Dancindream
	 * @CreateDate : 2013-9-6
	 * @param context
	 * @param sessionManagerHandler
	 */
	public RouteShowSession(Context context, Handler sessionManagerHandler) {
		super(context, sessionManagerHandler);
	}

	double fromLat;
	double fromLng;
	String fromCity;
	String fromPoi;
	double toLat;
	double toLng;
	String toCity;
	String toPoi;
	String originFromPOI;
	String poiVendor;
	
	
	public void putProtocol(JSONObject jsonProtocol) {
		super.putProtocol(jsonProtocol);
		addQuestionViewText(mQuestion);

		JSONObject resultObject = getJSONObject(mDataObject, "result");
		mCancelProtocal = getJsonValue(resultObject, SessionPreference.KEY_ON_CANCEL);

		fromLat = getJsonValue(resultObject, "fromLatitude", 0d);
		fromLng = getJsonValue(resultObject, "fromLongtitude", 0d);
		fromCity = getJsonValue(resultObject, "fromCity", "");
		fromPoi = getJsonValue(resultObject, "fromPosition", "");
		toLat = getJsonValue(resultObject, "toLatitude", 0d);
		toLng = getJsonValue(resultObject, "toLongtitude", 0d);
		toCity = getJsonValue(resultObject, "toCity", "");
		toPoi = getJsonValue(resultObject, "toPosition", "");
		originFromPOI = getJsonValue(resultObject, "originFromPOI");
		poiVendor = PrivatePreference.getValue("poi_vendor",
				UserPreference.MAP_VALUE_AMAP);
		
		if (mRoteView == null) {
			mRoteView = new RoteSeesionConfirmView(mContext);
			mRoteView.initView(
				null,
				"正在为你导航到"+toPoi,
				"",
				"");

			mRoteView.setListener(mRoteListener);
		}

		Message msg = mSessionManagerHandler.obtainMessage(SessionPreference.MESSAGE_ADD_ANSWER_VIEW, mRoteView);
		mSessionManagerHandler.sendMessage(msg);
		
		
		addAnswerViewText(mAnswer);
		playTTS(mContext.getString(R.string.route_delay, toPoi));//吾比5插报更自然 
		

//		if ("AMAP".equals(poiVendor) || "GAODE".equals(poiVendor)) {
//			GaodeMap.showRoute(mContext, BaiduMap.ROUTE_MODE_DRIVING, fromLat,
//					fromLng, fromCity, originFromPOI, fromPoi, toLat, toLng,
//					toCity, toPoi);
//		} else if ("BAIDU".equals(poiVendor)) {
//			BaiduMap.showRoute(mContext, BaiduMap.ROUTE_MODE_DRIVING, fromLat,
//					fromLng, fromCity, fromPoi, toLat, toLng, toCity, toPoi);
//		} else {
//			Toast.makeText(mContext, "Unsupported map.", Toast.LENGTH_SHORT)
//					.show();
//		}

//		 mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_DONE);
	}

	@Override
	public void onTTSEnd() {
		LogUtil.d(TAG, "onTTSEnd");
		super.onTTSEnd();
		mRoteView.startCountDownTimer(DELAY_CALL_TIME);
		
	}
}
