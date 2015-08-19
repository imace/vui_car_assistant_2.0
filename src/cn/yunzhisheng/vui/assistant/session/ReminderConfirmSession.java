/**
 * Copyright (c) 2012-2013 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * @FileName : ReminderConfirmSession.java
 * @ProjectName : vui_assistant
 * @PakageName : cn.yunzhisheng.vui.assistant.session
 * @Author : Dancindream
 * @CreateDate : 2013-9-9
 */
package cn.yunzhisheng.vui.assistant.session;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import cn.yunzhisheng.common.util.LogUtil;
import com.ilincar.voice.R;
import cn.yunzhisheng.vui.assistant.preference.SessionPreference;
import cn.yunzhisheng.vui.assistant.view.MemoContentView;
import cn.yunzhisheng.vui.assistant.view.MemoContentView.IMemoContentViewListener;
import cn.yunzhisheng.vui.modes.MemoInfo;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Dancindream
 * @CreateDate : 2013-9-9
 * @ModifiedBy : Dancindream
 * @ModifiedDate: 2013-9-9
 * @Modified:
 * 2013-9-9: 实现基本功能
 */
public class ReminderConfirmSession extends BaseSession {
	public static final String TAG = "ReminderConfirmSession";
	private MemoContentView mMemoContentView = null;
	private MemoInfo mMemoInfo = null;

	private String mOkProtocal = "";
	private String mCancelProtocal = "";

	private IMemoContentViewListener mMemoContentViewListener = new IMemoContentViewListener() {

		@Override
		public void onOk() {
			onUiProtocal(mOkProtocal);
		}

		@Override
		public void onCancel() {
			onUiProtocal(mCancelProtocal);
		}
	};

	/**
	 * @Author : Dancindream
	 * @CreateDate : 2013-9-9
	 * @param context
	 * @param sessionManagerHandler
	 */
	public ReminderConfirmSession(Context context, Handler sessionManagerHandler) {
		super(context, sessionManagerHandler);
	}

	public void putProtocol(JSONObject jsonProtocol) {
		super.putProtocol(jsonProtocol);

		JSONObject resultObject = getJSONObject(mDataObject, "result");

		mMemoInfo = new MemoInfo();
		String time = getJsonValue(resultObject, "time", "");
		String content = getJsonValue(resultObject, "content", mContext.getString(R.string.domain_alarm));

		mOkProtocal = getJsonValue(resultObject, "onOK", "");
		mCancelProtocal = getJsonValue(resultObject, "onCancel", "");

		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		mMemoInfo.createTime = System.currentTimeMillis();
		mMemoInfo.status = 1;
		mMemoInfo.title = content;
		try {
			mMemoInfo.dueTime = dateformat.parse(time).getTime();
		} catch (ParseException e) {
			mMemoInfo.dueTime = -9999;
		}

		addQuestionViewText(mQuestion);
		addAnswerViewText(mAnswer);
		mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_DONE);

		mMemoContentView = new MemoContentView(mContext);
		mMemoContentView.setListener(mMemoContentViewListener);
		mMemoContentView.setMemoInfo(mMemoInfo);

		addAnswerView(mMemoContentView);
		LogUtil.d(TAG, "--ReminderConfirmSession mAnswer : " + mAnswer + "--");
		//playTTS(mAnswer);
		playTTS(mTTS);
		mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_DONE);
	}
}
