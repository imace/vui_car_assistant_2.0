/**
 * Copyright (c) 2012-2013 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * @FileName : AppLaunchSession.java
 * @ProjectName : vui_assistant
 * @PakageName : cn.yunzhisheng.vui.assistant.session
 * @Author : Dancindream
 * @CreateDate : 2013-9-6
 */
package cn.yunzhisheng.vui.assistant.session;

import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import cn.yunzhisheng.common.util.LogUtil;
import cn.yunzhisheng.vui.assistant.oem.RomControl;
import cn.yunzhisheng.vui.assistant.preference.SessionPreference;
import cn.yunzhisheng.vui.assistant.view.WebContentView;

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
public class AppLaunchSession extends BaseSession {
	public static final String TAG = "AppLaunchSession";

	/**
	 * @Author : Dancindream
	 * @CreateDate : 2013-9-6
	 * @param context
	 * @param sessionManagerHandler
	 */

	public AppLaunchSession(Context context, Handler sessionManagerHandler) {
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
			// playTTS(mAnswer);
			playTTS(mTTS);

			RomControl.enterControl(mContext, RomControl.ROM_APP_LAUNCH, packageName, className);
		} else if (url != null && !"".equals(url)) {
			// WebContentView webContentView = new WebContentView(mContext);
			// webContentView.setUrl(url);

			addAnswerViewText(mAnswer);
			// addAnswerView(webContentView);
			// LogUtil.d(TAG, "--AppLaunchSession mAnswer : " + mAnswer + "--");
			// playTTS(mAnswer);
			playTTS(mTTS);
			RomControl.enterControl(mContext, RomControl.ROM_BROWSER_URL, url);
		}

		mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_DONE);
	}
}
