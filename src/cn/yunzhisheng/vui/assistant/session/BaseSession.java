package cn.yunzhisheng.vui.assistant.session;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import cn.yunzhisheng.common.JsonTool;
import cn.yunzhisheng.common.util.LogUtil;
import com.ilincar.voice.R;
import cn.yunzhisheng.vui.assistant.preference.SessionPreference;

/**
 * @Module : Session层基础类
 * @Comments : 用于构建整个Session层的生命周期和提供核心方法
 * @Author : Dancindream
 * @CreateDate : 2014-4-1
 * @ModifiedBy : Dancindream
 * @ModifiedDate: 2014-4-1
 * @Modified:
 * 2014-4-1: 实现基本功能
 */
public abstract class BaseSession {
	private static final String TAG = "BaseSession";
	protected static final String[] MESSAGE_TRIM_END = new String[] { "。", "，" };
	public boolean mIsNeedAddTextView;
	protected Handler mSessionManagerHandler = null;
	protected Context mContext = null;

	protected String mDomain = "";
	protected String mType = "", mOriginType = "";
	protected String mQuestion = "", mAnswer = "", mTTS = "";
	protected String mOriginCode;
	protected String mErrorCode;
	protected JSONObject mDataObject = null;

	private boolean mReleased = false;

	public BaseSession(Context context, Handler sessionManagerHandler) {
		mContext = context;
		mSessionManagerHandler = sessionManagerHandler;
	}

	/**
	 * @Description : TODO 主流程，将协议导入Session
	 * @Author : Dancindream
	 * @CreateDate : 2014-4-1
	 * @param jsonProtocolArray
	 */
	public void putProtocol(JSONArray jsonProtocolArray) {
		try {
			if (jsonProtocolArray != null && jsonProtocolArray.length() > 0) {
				putProtocol(jsonProtocolArray.getJSONArray(0));
			}
		} catch (JSONException e) {
			showUnSupport();
			return;
		}
	}

	/**
	 * @Description : TODO 主流程，将协议导入Session
	 * @Author : Dancindream
	 * @CreateDate : 2014-4-1
	 * @param jsonProtocol
	 */
	public void putProtocol(JSONObject jsonProtocol) {
		LogUtil.d(TAG, "putProtocol:" + jsonProtocol);
		mDomain = JsonTool.getJsonValue(jsonProtocol, SessionPreference.KEY_DOMAIN, "");
		mType = JsonTool.getJsonValue(jsonProtocol, SessionPreference.KEY_TYPE, "");
		mOriginType = JsonTool.getJsonValue(jsonProtocol, SessionPreference.KEY_ORIGIN_TYPE);
		mOriginCode = JsonTool.getJsonValue(jsonProtocol, SessionPreference.KEY_ORIGIN_CODE);
		mDataObject = JsonTool.getJSONObject(jsonProtocol, SessionPreference.KEY_DATA);

		if (mDataObject != null) {
			mQuestion = JsonTool.getJsonValue(mDataObject, SessionPreference.KEY_QUESTION, "");
			mErrorCode = JsonTool.getJsonValue(mDataObject, SessionPreference.KEY_ERROR_CODE);
			if (TextUtils.isEmpty(mQuestion)) {
				mQuestion = JsonTool.getJsonValue(mDataObject, SessionPreference.KEY_TEXT);
			}
			mAnswer = JsonTool.getJsonValue(mDataObject, SessionPreference.KEY_ANSWER, "");
			mTTS = JsonTool.getJsonValue(mDataObject, SessionPreference.KEY_TTS_ANSWER, "");
			if (TextUtils.isEmpty(mTTS)) {
				LogUtil.w(TAG, "mTTS empty,use mAnswer instead!");
				mTTS = mAnswer;
			}
		}
	}

	/**
	 * @Description : TODO 主流程，获取ActivityResult的数据时触发
	 * @Author : Dancindream
	 * @CreateDate : 2014-4-1
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	}

	/**
	 * @Description : TODO 主流程，TTS结束时触发
	 * @Author : Dancindream
	 * @CreateDate : 2014-4-1
	 */
	public void onTTSEnd() {
		LogUtil.d(TAG, "onTTSEnd");
	}

	/**
	 * @Description : TODO 主方法，在不支持时的统一处理方案
	 * @Author : Dancindream
	 * @CreateDate : 2014-4-1
	 */
	protected void showUnSupport() {
	}

	/**
	 * @Description : TODO 主流程，在取消操作时触发
	 * @Author : Dancindream
	 * @CreateDate : 2014-4-1
	 */
	public void cancelSession() {
		mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_CANCEL_TALK);
		addAnswerViewText(mContext.getString(R.string.operation_cancel));
		mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_CANCEL);
	}

	/**
	 * @Description : TODO 主方法，判断是否已经release
	 * @Author : Dancindream
	 * @CreateDate : 2014-4-1
	 * @return
	 */
	public boolean isReleased() {
		return mReleased;
	}

	/**
	 * @Description : TODO 主方法，获取Session的Domain字段
	 * @Author : Dancindream
	 * @CreateDate : 2014-4-1
	 * @return
	 */
	public String getSessionDomain() {
		return mDomain;
	}

	/**
	 * @Description : TODO 主方法，release Session
	 * @Author : Dancindream
	 * @CreateDate : 2014-4-1
	 */
	public void release() {
		LogUtil.d(TAG, "release");
		mReleased = true;
		mContext = null;
	}

	/**
	 * @Description : TODO 主方法，播报文字
	 * @Author : Dancindream
	 * @CreateDate : 2014-4-1
	 * @param text
	 */
	protected void playTTS(String text) {
		LogUtil.d("TTS", "playTTS:" + text + ";this:" + this);
		Message msg = mSessionManagerHandler.obtainMessage(SessionPreference.MESSAGE_REQUEST_PLAY_TTS);
		Bundle bundle = new Bundle();
		bundle.putString(SessionPreference.KEY_TEXT, text);
		msg.setData(bundle);
		mSessionManagerHandler.sendMessage(msg);
	}

	protected void playTTSWithEndRunnable(String text, Runnable runnable) {
		LogUtil.d("TTS", "playTTSWithEndRunnable:" + text);
		Message msg = mSessionManagerHandler.obtainMessage(SessionPreference.MESSAGE_REQUEST_PLAY_TTS);
		Bundle bundle = new Bundle();
		bundle.putString(SessionPreference.KEY_TEXT, text);
		msg.setData(bundle);
		msg.obj = runnable;
		mSessionManagerHandler.sendMessage(msg);
	}

	/**
	 * @Description : TODO 主方法，取消播报
	 * @Author : Dancindream
	 * @CreateDate : 2014-4-1
	 */
	protected void cancelTTS() {
		mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_REQUEST_CANCEL_TTS);
	}

	/**
	 * @Description : TODO 主方法，将问句（用户说的内容）添加到主界面容器
	 * @Author : Dancindream
	 * @CreateDate : 2014-4-1
	 * @param text
	 */
	protected void addQuestionViewText(String text) {
		Message msg = new Message();
		msg.what = SessionPreference.MESSAGE_ADD_QUESTION_TEXT;
		msg.obj = text;
		mSessionManagerHandler.sendMessage(msg);
	}

	/**
	 * @Description : TODO 主方法，将View（语音魔方要显示的内容）添加到主界面容器
	 * @Author : Dancindream
	 * @CreateDate : 2014-4-1
	 * @param view
	 */
	protected void addAnswerView(View view) {
		addAnswerView(view, true);
	}

	protected void addAnswerView(View view, boolean fullScroll) {
		if (view == null) {
			return;
		}
		// SessionContainer.addViewNow(view.hashCode());
		Message msg = new Message();
		msg.what = SessionPreference.MESSAGE_ADD_ANSWER_VIEW;
		msg.obj = view;
		msg.arg1 = fullScroll ? 1 : 0;
		mSessionManagerHandler.sendMessage(msg);
	}

	/**
	 * @Description : TODO 主方法，将答句（语音魔方说的内容）添加到主界面容器
	 * @Author : Dancindream
	 * @CreateDate : 2014-4-1
	 * @param text
	 */
	protected void addAnswerViewText(String text) {
		Message msg = new Message();
		msg.what = SessionPreference.MESSAGE_ADD_ANSWER_TEXT;
		msg.obj = text;
		mSessionManagerHandler.sendMessage(msg);
	}

	/**
	 * @Description : TODO 主方法，当界面操作后触发，需将触发协议返还语音魔方
	 * @Author : Dancindream
	 * @CreateDate : 2014-4-1
	 * @param protocal
	 */
	protected void onUiProtocal(String protocal) {
		LogUtil.d(TAG, "onUiProtocal : " + protocal);
		Message msg = new Message();
		msg.what = SessionPreference.MESSAGE_UI_OPERATE_PROTOCAL;
		msg.obj = protocal;
		mSessionManagerHandler.sendMessage(msg);
	}

	protected String transDateTTS(String str) {
		if (TextUtils.isEmpty(str)) {
			return "";
		}

		String regEx = "([0-9]+)-([0-9]+)-([0-9]+)";
		String year = "";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		if (m.find()) {
			int size = m.groupCount();
			if (size > 1) {
				year = m.group(1);
			}
		}
		str = str.replaceAll("([0-9]+)-([0-9]+)-([0-9]+)", transNumberTTS(year) + mContext.getString(R.string.tts_date));
		return str;
	}

	protected String transNumberTTS(String number) {
		if (TextUtils.isEmpty(number)) {
			return "";
		}
		String str1 = "0123456789";
		String str2 = mContext.getString(R.string.number_capital);

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < number.length(); i++) {
			char c = number.charAt(i);
			int index = str1.indexOf(c);
			if (index >= 0) {
				sb.append(str2.charAt(index));
			}
		}
		return sb.toString();
	}

	public static JSONObject getJSONObject(JSONObject jsonObj, String name) {
		return JsonTool.getJSONObject(jsonObj, name);
	}

	public static JSONObject getJSONObject(JSONArray jsonArr, int index) {
		return JsonTool.getJSONObject(jsonArr, index);
	}

	public static String getJsonValue(JSONObject json, String key) {
		return JsonTool.getJsonValue(json, key);
	}

	public static String getJsonValue(JSONObject json, String key, String defValue) {
		return JsonTool.getJsonValue(json, key, defValue);
	}

	public static boolean getJsonValue(JSONObject json, String key, boolean defValue) {
		return JsonTool.getJsonValue(json, key, defValue);
	}

	public static int getJsonValue(JSONObject json, String key, int defValue) {
		return JsonTool.getJsonValue(json, key, defValue);
	}

	public static long getJsonValue(JSONObject json, String key, long defValue) {
		if (json != null && json.has(key)) {
			try {
				return json.getLong(key);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return defValue;

	}

	public static double getJsonValue(JSONObject jsonObj, String key, double defValue) {
		if (jsonObj != null) {
			try {
				return jsonObj.getDouble(key);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return defValue;
	}

	public static JSONArray getJsonArray(JSONObject jsonObj, String key) {
		return JsonTool.getJsonArray(jsonObj, key);
	}

}