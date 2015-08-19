package cn.yunzhisheng.vui.assistant.session;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import cn.yunzhisheng.common.util.LogUtil;
import cn.yunzhisheng.preference.PrivatePreference;
import com.ilincar.voice.R;
import cn.yunzhisheng.vui.assistant.oem.RomContact;
import cn.yunzhisheng.vui.assistant.oem.RomCustomSetting;
import cn.yunzhisheng.vui.assistant.preference.SessionPreference;
import cn.yunzhisheng.vui.assistant.preference.UserPreference;
import cn.yunzhisheng.vui.assistant.view.CallContentView;
import cn.yunzhisheng.vui.assistant.view.CallContentView.ICallContentViewListener;
import cn.yunzhisheng.vui.modes.ContactInfo;
import cn.yunzhisheng.vui.modes.PhoneNumberInfo;

public class CallConfirmShowSession extends ContactSelectBaseSession {
	public static final String TAG = "CallConfirmShowSession";
	public static final String BACK_PROTOCAL = "{\"service\":\"DOMAIN_KEY\",\"key\":\"back\"}";
	//public static final String OK_PROTOCAL = "{\"message\":\"确定\",\"service\":\"DOMAIN_LOCAL\",\"confirm\":\"ok\"}";
	protected ContactInfo mSelectedContactInfo = new ContactInfo();
	protected PhoneNumberInfo mSelectedPhoneNumberInfo = new PhoneNumberInfo();
	protected CallContentView mCallContentView = null;
	private boolean mStepTag;
	private boolean mCancleConfirmCallTag = true;

	private static final String KEY_DELAY_CALL_PHONE = "KEY_DELAY_CALL_PHONE";
	private static final boolean DEFAULT_DELAY_CALL_PHONE = true;
	private static final int DELAY_CALL_TIME = 5000;
	private boolean mDelayCall = false;

	protected ICallContentViewListener mCallContentViewListener = new ICallContentViewListener() {
		public void onCancel() {
			onUiProtocal(mCancelProtocal);
		}

		public void onOk() {
            executeCall(mSelectedPhoneNumberInfo.getNumber());
            mSessionManagerHandler
                    .sendEmptyMessage(SessionPreference.MESSAGE_SESSION_DONE);
		}
	};

	public CallConfirmShowSession(Context context, Handler handle) {
		super(context, handle);
		mDelayCall = PrivatePreference.getBooleanValue(KEY_DELAY_CALL_PHONE, DEFAULT_DELAY_CALL_PHONE);
		mStepTag = false;
	}

	protected void addTextCommonView() {
		if (!mUserPreference.getConfirmBeforeCall() && !mOkProtocal.equals("")) {
			onUiProtocal(mOkProtocal);
			mStepTag = true;
		} else {
			super.addTextCommonView();
		}
	}

	public void putProtocol(JSONObject jsonProtocol) {
		super.putProtocol(jsonProtocol);
		if (mStepTag) return;
		String displayName = getJsonValue(mJsonObject, "name");
		final String number = getJsonValue(mJsonObject, "number");
		String tag = getJsonValue(mJsonObject, "numberAttribution");
		int photoId = 0;
		try {
			if (mJsonObject.has("pic")) {
				photoId = Integer.parseInt(getJsonValue(mJsonObject, "pic"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		mSelectedContactInfo.setDisplayName(displayName);
		mSelectedContactInfo.setPhotoId(photoId);
		mSelectedContactInfo.setContactId(-1);
		mSelectedPhoneNumberInfo.setNumber(number);
		mSelectedPhoneNumberInfo.setContactId(-1);
		mSelectedPhoneNumberInfo.setId(-1);

		if (mCallContentView == null) {
			mCallContentView = new CallContentView(mContext);
			Drawable drawable = RomContact.loadContactDrawable(mContext, mSelectedContactInfo.getPhotoId());
			mCallContentView.initView(
				drawable,
				mSelectedContactInfo.getDisplayName(),
				mSelectedPhoneNumberInfo.getNumber(),
				tag);

			mCallContentView.setListener(mCallContentViewListener);
		}

		Message msg = mSessionManagerHandler.obtainMessage(SessionPreference.MESSAGE_ADD_ANSWER_VIEW, mCallContentView);
		mSessionManagerHandler.sendMessage(msg);

		if (mCancleConfirmCallTag) {
			onUiProtocal(BACK_PROTOCAL);
			addAnswerViewText(mAnswer);
			setAutoStart(true);
			playTTS(mContext.getString(R.string.call_phone_delay, displayName));//吾比5插报更自然 
		} else {
			startCallAction();
			String ttsAnswer = "";
			if (mType.equals("CONFIRM_CALL")) {
				if (TextUtils.isEmpty(displayName)) {
					ttsAnswer = mContext.getString(R.string.call_phone_confirm, number);
				} else {
					ttsAnswer = mContext.getString(R.string.call_phone_confirm_number, number);
				}
			} else if (TextUtils.isEmpty(displayName)) {
				ttsAnswer = mContext.getString(R.string.connect_number, number);
				mAnswer = ttsAnswer;
			} else {
				ttsAnswer = mContext.getString(R.string.connect_name, displayName);
				mAnswer = ttsAnswer;
			}
			playTTS(ttsAnswer);
			addAnswerViewText(mAnswer);
			mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_DONE);
		}
	}

	protected void startCallAction() {
		mCallContentView.setModeConfirm();
		addSessionView(mCallContentView);
	}

	private void executeCall(String number) {
		LogUtil.d(TAG, "executeCall: number " + number);
		if (mContext != null) {
			try {
//				Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
//				callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				mContext.startActivity(callIntent);
				//add by ch
				String displayName = getJsonValue(mJsonObject, "name");
				Intent intent = new Intent();
				intent.setAction(RomCustomSetting.CALL);
				intent.putExtra("number", displayName+"_"+number);
				mContext.sendBroadcast(intent);
				 
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			LogUtil.e(TAG, "call onOk context is null...");
		}
	}

	@Override
	public void onTTSEnd() {
		LogUtil.d(TAG, "onTTSEnd");
		super.onTTSEnd();
        if (mDelayCall) {
            mCallContentView.startCountDownTimer(DELAY_CALL_TIME);
        } else {
            executeCall(mSelectedPhoneNumberInfo.getNumber());
            mSessionManagerHandler
                    .sendEmptyMessage(SessionPreference.MESSAGE_SESSION_DONE);
        }
	}

	@Override
	public void release() {
		LogUtil.d(TAG, "release");
		super.release();
        if (mDelayCall) {
            mCallContentView.cancelCountDownTimer();
        }
		if (mCallContentView != null) {
			mCallContentView.setListener(null);
		}
	}
}
