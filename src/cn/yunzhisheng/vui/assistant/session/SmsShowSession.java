package cn.yunzhisheng.vui.assistant.session;

import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;
import cn.yunzhisheng.common.util.LogUtil;
import com.ilincar.voice.R;
import cn.yunzhisheng.vui.assistant.model.Telephony.Threads;
import cn.yunzhisheng.vui.assistant.preference.SessionPreference;
import cn.yunzhisheng.vui.assistant.sms.SmsException;
import cn.yunzhisheng.vui.assistant.sms.SmsMessageSender;
import cn.yunzhisheng.vui.assistant.sms.SmsSenderService;
import cn.yunzhisheng.vui.modes.PhoneNumberInfo;

public class SmsShowSession extends CommBaseSession {
	public static final String TAG = "SmsShowSession";

	SmsShowSession(Context context, Handler sessionManagerHandler) {
		super(context, sessionManagerHandler);
	}

	public void putProtocol(JSONObject jsonObject) {
		LogUtil.d(TAG, "putProtocol: jsonObject " + jsonObject);
		super.putProtocol(jsonObject);
		mBlockAutoStart = true;
		PhoneNumberInfo mSelectedPhoneNumberInfo = new PhoneNumberInfo();
		mSelectedPhoneNumberInfo.setNumber(getJsonValue(mJsonObject, "number"));
		String sendText = getJsonValue(mJsonObject, "content");
		if (sendText == null || "".equals(sendText)) {
			sendText = "";
		}
		String phoneNumber = mSelectedPhoneNumberInfo.getNumber();
		String[] phoneNumberArray = { phoneNumber };
		try {
			SmsMessageSender smsSender = new SmsMessageSender(
				mContext,
				phoneNumberArray,
				sendText,
				Threads.getOrCreateThreadId(mContext, mSelectedPhoneNumberInfo.getNumber()));
			smsSender.sendMessage(SmsSenderService.NO_TOKEN);
			playTTS(mTTS);
		} catch (Exception e) {
			e.printStackTrace();
			playTTS(mContext.getString(R.string.send_sms_exception));
		}

		mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_DONE);
	}
}
