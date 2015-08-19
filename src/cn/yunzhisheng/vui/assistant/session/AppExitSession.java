package cn.yunzhisheng.vui.assistant.session;
import org.json.JSONObject;

import cn.yunzhisheng.common.util.LogUtil;
import cn.yunzhisheng.vui.assistant.oem.RomCustomSetting;
import cn.yunzhisheng.vui.assistant.preference.SessionPreference;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

public class AppExitSession extends BaseSession {
	public static final String TAG = "AppExitSession";

	public AppExitSession(Context context, Handler sessionManagerHandler) {
		super(context, sessionManagerHandler);
	}
	
	public void putProtocol(JSONObject jsonProtocol) {
		super.putProtocol(jsonProtocol);

		JSONObject resultObject = getJSONObject(mDataObject, "result");

		String packageName = getJsonValue(resultObject, "package_name", "");
		String className = getJsonValue(resultObject, "class_name", "");

		String url = getJsonValue(resultObject, "url", "");
		if (packageName != null && !"".equals(packageName) && className != null && !"".equals(className)) {
			addQuestionViewText(mQuestion);
			addAnswerViewText(mAnswer);
			LogUtil.d(TAG, "--AppLaunchSession mAnswer : " + mAnswer + "--");
			playTTS(mTTS);

		} else if (url != null && !"".equals(url)) {
			addAnswerViewText(mAnswer);
			playTTS(mTTS);
		}

		//关闭应用，发送广播
		//RomCustomSetting.romCustomCloseApp(mContext, packageName);
		Intent intent = new Intent();
		intent.setAction(RomCustomSetting.EXIT_APP);
		intent.putExtra("appPara", packageName);
		mContext.sendBroadcast(intent);
		LogUtil.d("YunZhiSheng","退出应用 ：" + intent.getStringExtra("appPara"));
		mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_DONE);
	}
}
