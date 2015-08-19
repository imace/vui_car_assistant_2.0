package cn.yunzhisheng.vui.assistant.session;

import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import cn.yunzhisheng.common.JsonTool;
import cn.yunzhisheng.common.util.LogUtil;
import com.ilincar.voice.R;
import cn.yunzhisheng.vui.assistant.preference.SessionPreference;
import cn.yunzhisheng.vui.assistant.view.NoPerSonContentView;

public class TalkShowMiddleSession extends CommBaseSession{
	public static final String TAG = "TalkShowMiddleSession";

	TalkShowMiddleSession(Context context, Handler sessionManagerHandler) {
		super(context, sessionManagerHandler);
		// TODO Auto-generated constructor stub
	}
	public void putProtocol(JSONObject jsonProtocol) {
		super.putProtocol(jsonProtocol);
		
//		addQuestionViewText(mAnswer);
		
		if(SessionPreference.DOMAIN_CALL.equals(mOriginType) || SessionPreference.DOMAIN_SMS.equals(mOriginType)) {
			NoPerSonContentView view = new NoPerSonContentView(mContext);
			view.setShowText(R.string.call_with_no_name);
			addAnswerView(view,true);
			
			addAnswerViewText(mAnswer);
		} else {
			addAnswerViewText(mAnswer);
		}
		
		playTTS(mTTS);
	}
}
