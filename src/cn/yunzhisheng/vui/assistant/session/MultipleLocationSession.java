/**
 * Copyright (c) 2012-2014 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * @FileName : MultipleLocationSession.java
 * @ProjectName : vui_car_assistant
 * @PakageName : cn.yunzhisheng.vui.assistant.session
 * @Author : Brant
 * @CreateDate : 2014-10-29
 */
package cn.yunzhisheng.vui.assistant.session;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import cn.yunzhisheng.asr.utils.LogUtil;
import com.ilincar.voice.R;
import cn.yunzhisheng.vui.assistant.view.PickLocationView;
import cn.yunzhisheng.vui.modes.LocationInfo;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Brant
 * @CreateDate : 2014-10-29
 * @ModifiedBy : Brant
 * @ModifiedDate: 2014-10-29
 * @Modified:
 * 2014-10-29: 实现基本功能
 */
public class MultipleLocationSession extends SelectCommonSession {
	public static final String TAG = "MultipleLocationSession";

	private PickLocationView mPickLocationView;
	private ArrayList<LocationInfo> mLocationInfos = new ArrayList<LocationInfo>();
	
	private String pageNum = "";
	private String page_content = "";
	private String ttsAnswer = "";
	
	public MultipleLocationSession(Context context, Handler sessionManagerHandler) {
		super(context, sessionManagerHandler);
	}

	public void putProtocol(JSONObject jsonProtocol) {
		super.putProtocol(jsonProtocol);
		LogUtil.d("---- jsonProtocol----",jsonProtocol.toString());
		JSONArray dataArray = getJsonArray(mJsonObject, "locations");
//		String endPage = getJsonValue(getJSONObject(jsonProtocol, "data"), "endPage");
//		String firstPage = getJsonValue(getJSONObject(jsonProtocol, "data"), "firstPage");
		String endPage = getJsonValue(mJsonObject, "endPage");
		String firstPage = getJsonValue(mJsonObject, "firstPage");
		pageNum = getJsonValue(mJsonObject, "pagNum");
		ttsAnswer = getJsonValue(mJsonObject, "ttsAnswer");
		page_content = getJsonValue(mJsonObject, "page_content");
		LogUtil.d(TAG,"endPage = " +endPage + ";firstPage = " +firstPage +";pageNum = " +pageNum+";page_content = " +page_content);
		LogUtil.d(TAG,"endPage = " +endPage );
	
		
		if (dataArray != null) {
			mAnswer = mContext.getString(R.string.say_number_choose);
			addSessionAnswerText(mAnswer);
			mTtsText = ttsAnswer;
			playTTS(mTtsText);
			for (int i = 0; i < dataArray.length(); i++) {
				JSONObject item = getJSONObject(dataArray, i);
				LocationInfo info = new LocationInfo();
				info.setName(getJsonValue(item, "name"));
				info.setAddress(getJsonValue(item, "address"));
				info.setType(getJsonValue(item, "type", -1));
				info.setCity(getJsonValue(item, "city"));
				info.setProvider(getJsonValue(item, "provider"));

				info.setLatitude(getJsonValue(item, "lat", 0d));
				info.setLongitude(getJsonValue(item, "lng", 0d));
				mLocationInfos.add(info);
				mDataItemProtocalList.add(getJsonValue(item, "onSelected"));
			}
			if (mPickLocationView == null) {
				mPickLocationView = new PickLocationView(mContext);
				if (pageNum.equals("")) {
					mPickLocationView.setPageNum(-1);
				}else{
					mPickLocationView.setPageNum(Integer.parseInt(pageNum) + 1);
				}
				if (page_content.equals("")) {
					mPickLocationView.setPageContent(-1);
				}else{
					mPickLocationView.setPageContent(Integer.parseInt(page_content));
				}
				
				mPickLocationView.initView(mLocationInfos);
				mPickLocationView.setPickListener(mPickViewListener);
			}
			addSessionView(mPickLocationView, false);
		}
		
		if (endPage.equals("endPage")) {
			playTTS(mContext.getString(R.string.now_last_page));
			
		}
		LogUtil.d(TAG,"endPage = " +firstPage );
		if(firstPage.equals("firstPage")){
			playTTS(mContext.getString(R.string.now_first_page));
		}
		
	}

	@Override
	public void release() {
		super.release();
		if (mPickLocationView != null) {
			mPickLocationView.setPickListener(null);
			mPickLocationView = null;
		}
		mLocationInfos.clear();
		mLocationInfos = null;
	}
}
