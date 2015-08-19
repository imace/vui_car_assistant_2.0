/**
 * Copyright (c) 2012-2013 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * @FileName	: TranslationShowSession.java
 * @ProjectName	: vui_assistant
 * @PakageName	: cn.yunzhisheng.vui.assistant.session
 * @Author		: Dancindream
 * @CreateDate	: 2013-9-3
 */
package cn.yunzhisheng.vui.assistant.session;

import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import cn.yunzhisheng.common.util.LogUtil;

/**
 * @Module		: 隶属模块名
 * @Comments	: 描述
 * @Author		: Dancindream
 * @CreateDate	: 2013-9-3
 * @ModifiedBy	: Dancindream
 * @ModifiedDate: 2013-9-3
 * @Modified: 
 * 2013-9-3: 实现基本功能
 */
public class TranslationShowSession extends BaseSession {
	public static final String TAG = "TranslationShowSession";
	/**
	 * @Author		: Dancindream
	 * @CreateDate	: 2013-9-3
	 * @param context
	 * @param sessionManagerHandler
	 */
	public TranslationShowSession(Context context, Handler sessionManagerHandler) {
		super(context, sessionManagerHandler);
	}
	
	public void putProtocol(JSONObject jsonProtocol) {
		super.putProtocol(jsonProtocol);
		
		addQuestionViewText(mQuestion);
		addAnswerViewText(mAnswer);
		LogUtil.d(TAG, "--TranslationShowSession mAnswer : "+mAnswer+"--");
		//playTTS(mAnswer);
		playTTS(mTTS);
	}
}
