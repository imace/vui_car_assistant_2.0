package cn.yunzhisheng.vui.assistant.session;

import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import cn.yunzhisheng.common.util.LogUtil;
import cn.yunzhisheng.vui.assistant.preference.SessionPreference;
import cn.yunzhisheng.vui.assistant.preference.UserPreference;

public class CommBaseSession extends BaseSession implements ISessionUpdate {
	public static String TAG = "CommBaseSession";
	public String mCancelProtocal = null;
	public String mOkProtocal = null;
	public JSONObject mJsonObject = null;
	protected UserPreference mUserPreference = new UserPreference(mContext);
	protected String mTtsText = "";
	protected boolean mBlockAutoStart;

	protected void addTextCommonView() {
		if (mIsNeedAddTextView) {
			addSessionAnswerText(mQuestion);
			addSessionAnswerText(mAnswer);
		}
	}

	protected void editShowContent() {

	}

	CommBaseSession(Context context, Handler sessionManagerHandler) {
		super(context, sessionManagerHandler);
//		TAG = this.getClass().getName();
		mIsNeedAddTextView = true;
	}

	public void addSessionAnswerText(String ttsText) {
		addAnswerViewText(ttsText);
	}

	public void addSessionView(View view) {
		addAnswerView(view);
	}

	public void addSessionView(View view, boolean fullScroll) {
		addAnswerView(view, fullScroll);
	}

	public void putProtocol(JSONObject jsonObject) {
		LogUtil.d(TAG, "putProtocol: jsonObject " + jsonObject);
		super.putProtocol(jsonObject);
		mJsonObject = getJSONObject(mDataObject, SessionPreference.KEY_RESULT);
		mCancelProtocal = getJsonValue(mJsonObject, SessionPreference.KEY_ON_CANCEL);
		mOkProtocal = getJsonValue(mJsonObject, SessionPreference.KEY_ON_OK);
		addTextCommonView();
	}

	public void setAutoStart(boolean b) {
		this.mBlockAutoStart = b;
	}

	@Override
	public void onTTSEnd() {
		super.onTTSEnd();
		if (mUserPreference.getAutoStartMicInLoop() && !mBlockAutoStart) {
			mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_START_TALK);
		}
	}

	public void requestStartTalk() {
		cancelTTS();
	}

	public void requestStopTalk() {
	}

	@Override
	public void editSession() {
		editShowContent();
	}

	@Override
	public void updateSession(JSONObject jsonObject) {

	}

	@Override
	public void release() {
		super.release();
		mJsonObject = null;
		mUserPreference = null;
	}

}