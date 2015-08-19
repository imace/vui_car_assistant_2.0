/**
 * Copyright (c) 2012-2014 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * @FileName : BroadcastSession.java
 * @ProjectName : CarPlay
 * @PakageName : cn.yunzhisheng.vui.assistant.session
 * @Author : Brant
 * @CreateDate : 2014-9-18
 */
package cn.yunzhisheng.vui.assistant.session;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import cn.yunzhisheng.common.JsonTool;
import cn.yunzhisheng.common.util.LogUtil;
import com.ilincar.voice.R;
import cn.yunzhisheng.vui.assistant.model.BroadcastChannelInfo;
import cn.yunzhisheng.vui.assistant.model.BroadcastFrequencyInfo;
import cn.yunzhisheng.vui.assistant.model.BroadcastStationInfo;
import cn.yunzhisheng.vui.assistant.preference.SessionPreference;
import cn.yunzhisheng.vui.assistant.view.BroadcastContentView;
import cn.yunzhisheng.vui.assistant.view.NoPerSonContentView;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Brant
 * @CreateDate : 2014-9-18
 * @ModifiedBy : Brant
 * @ModifiedDate: 2014-9-18
 * @Modified:
 * 2014-9-18: 实现基本功能
 */
public class BroadcastSession extends BaseSession {
	public static final String TAG = "BroadcastSession";

	private BroadcastStationInfo mBroadcastStation;
	private BroadcastContentView mBroadcastContentView;
	private static final String ERROR_CODE_FREQUENCY_INVALID = "FREQUENCY_INVALID";

	private int mFrequency;
	private String mBandType;

	public BroadcastSession(Context context, Handler sessionManagerHandler) {
		super(context, sessionManagerHandler);
	}

	@Override
	public void putProtocol(JSONObject jsonProtocol) {
		super.putProtocol(jsonProtocol);
		LogUtil.d(TAG, "--jsonProtocol-->" + jsonProtocol);
		if (SessionPreference.VALUE_CODE_ANSWER.equals(mOriginCode)) {
			String errorCode = JsonTool.getJsonValue(mDataObject, SessionPreference.KEY_CODE);
			String errorMsg = JsonTool.getJsonValue(mDataObject, SessionPreference.KEY_MESSAGE);
			if (ERROR_CODE_FREQUENCY_INVALID.equals(errorCode)) {
				mAnswer = errorMsg;
				addAnswerViewText(mAnswer);
			}
			LogUtil.d(TAG, "--BroadcastSession mAnswer111 : " + mAnswer + "--");
			if (mAnswer.equals(mContext.getString(R.string.error_number))) {
				mAnswer = mContext.getString(R.string.search_in);
			}
			playTTS(mAnswer);
			mAnswer = mContext.getString(R.string.open_broadcast);
			addAnswerViewText(mAnswer);
		} else {
			processBroadcastStationInfo();
		}
	}

	private void processBroadcastStationInfo() {
		mBroadcastStation = new BroadcastStationInfo();
		mBroadcastStation.setStation(getJsonValue(mDataObject, SessionPreference.KEY_STATION));
		mBroadcastStation.setMsg(getJsonValue(mDataObject, SessionPreference.KEY_MSG));

		JSONArray jsonChannelArr = getJsonArray(mDataObject, SessionPreference.KEY_CHANNEL_LIST);
		if (jsonChannelArr != null) {
			for (int i = 0; i < jsonChannelArr.length(); i++) {
				try {
					JSONObject jsonChannelObj = jsonChannelArr.getJSONObject(i);
					BroadcastChannelInfo broadcastChannelInfo = new BroadcastChannelInfo();
					broadcastChannelInfo.setChannel(getJsonValue(jsonChannelObj, SessionPreference.KEY_CHANNEL));
					JSONArray jsonFrequencyArr = getJsonArray(jsonChannelObj, SessionPreference.KEY_FREQUENCY_LIST);
					for (int j = 0; j < jsonFrequencyArr.length(); j++) {
						try {
							JSONObject jsonFrequencyObj = jsonFrequencyArr.getJSONObject(i);
							BroadcastFrequencyInfo frequencyInfo = new BroadcastFrequencyInfo();
							frequencyInfo.setFrequency(getJsonValue(
								jsonFrequencyObj,
								SessionPreference.KEY_FREQUENCY,
								0.0));

							frequencyInfo.setType(getJsonValue(jsonFrequencyObj, SessionPreference.KEY_TYPE));
							frequencyInfo.setUnit(getJsonValue(jsonFrequencyObj, SessionPreference.KEY_UNIT));
							broadcastChannelInfo.addFrequency(frequencyInfo);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					mBroadcastStation.addChannel(broadcastChannelInfo);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		addQuestionViewText(mQuestion);
		if (TextUtils.isEmpty(mAnswer)) {
			mAnswer = mBroadcastStation.getMsg();
		}
		if (TextUtils.isEmpty(mAnswer)) {
			mAnswer = mContext.getString(R.string.broadcast_query_result);
		}

		if (mBroadcastStation.getChannelInfos().isEmpty()) {
			mAnswer = mContext.getString(R.string.broadcast_query_result_empty);
			addAnswerViewText(mAnswer);
			LogUtil.d(TAG, "--BroadcastSession mAnswer222 : " + mAnswer + "--");
			playTTS(mAnswer);
		} else if (mBroadcastStation.getChannelInfos().size() == 1) {
			List<BroadcastFrequencyInfo> frequencyInfos = mBroadcastStation
				.getChannelInfos()
				.get(0)
				.getFrequencyInfos();
			if (frequencyInfos.size() >= 1) {
				if (mBroadcastStation.getStation() == null) {
					mBroadcastStation.setStation("");
				}
				BroadcastFrequencyInfo frequencyInfo = frequencyInfos.get(0);
				mAnswer = String.format(
					mContext.getString(R.string.change_broadcast_format),
					mBroadcastStation.getStation() + "," + frequencyInfo.getType() + frequencyInfo.getFrequency()
							+ frequencyInfo.getUnit());

				LogUtil.d(TAG, "--frequencyInfo-->" + frequencyInfo);

				mBandType = frequencyInfo.getType();
				if (mBandType.equals("FM")) {
				    mAnswer = mContext.getString(R.string.now_fm,frequencyInfo.getFrequency());
					mFrequency = (int) (frequencyInfo.getFrequency() * 1000);
					// TTSAnswer = mAnswer.replace("调", "条").replace("MHZ",
					// "兆赫");
				} else if (mBandType.equals("AM")) {
				    mAnswer = mContext.getString(R.string.now_am, frequencyInfo.getFrequency());
					mFrequency = (int) (frequencyInfo.getFrequency());
					// TTSAnswer = mAnswer.replace("调", "条").replace("KHZ",
					// "千赫");
				}
				String TTSAnswer = mAnswer;

		        NoPerSonContentView view = new NoPerSonContentView(mContext);
		        view.setShowText(mAnswer);
		        addAnswerView(view, true);
				addAnswerViewText(mAnswer);
				LogUtil.d(TAG, "--TTSAnswer-->" + TTSAnswer);
				playTTS(mAnswer);
			}
		} else {
			mBroadcastContentView = new BroadcastContentView(mContext);
			mBroadcastContentView.setBroadcastResult(mBroadcastStation);
			addAnswerView(mBroadcastContentView);
		}
	}
	
	@Override
	public void onTTSEnd() {
		super.onTTSEnd();
		mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_DONE);
		mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_DISMISS_WINDOW);
	}
}
