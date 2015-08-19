/**
 * Copyright (c) 2012-2013 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * @FileName : TalkShowSession.java
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
import cn.yunzhisheng.common.util.LogUtil;
import cn.yunzhisheng.vui.assistant.MainApplication;
import com.ilincar.voice.R;
import cn.yunzhisheng.vui.assistant.preference.SessionPreference;
import cn.yunzhisheng.vui.assistant.preference.UserPreference;

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
public class ErrorShowSession extends BaseSession {
	public static final String TAG = "ErrorShowSession";
	protected UserPreference mUserPreference = new UserPreference(mContext);

	/**
	 * @Author : Dancindream
	 * @CreateDate : 2013-9-6
	 * @param context
	 * @param sessionManagerHandler
	 */
	public ErrorShowSession(Context context, Handler sessionManagerHandler) {
		super(context, sessionManagerHandler);
	}

	public void putProtocol(JSONObject jsonProtocol) {
		super.putProtocol(jsonProtocol);
		addQuestionViewText(mQuestion);
		MainApplication.mErrorRetalkCount++;
		if ("-63605".equals(mErrorCode)) {
			Message msg = mSessionManagerHandler.obtainMessage(
				SessionPreference.MESSAGE_PLAY_BEEP_SOUND,
				R.raw.error_tone,
				0);
			mSessionManagerHandler.sendMessage(msg);
		}
		addAnswerViewText(mAnswer);
		playTTS(mTTS);
	}

	@Override
	public void onTTSEnd() {
		LogUtil.d(TAG, "onTTSEnd");
		super.onTTSEnd();
		mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_DONE);
	}
}
