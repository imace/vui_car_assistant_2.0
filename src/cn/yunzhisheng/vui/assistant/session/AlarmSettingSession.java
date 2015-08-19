package cn.yunzhisheng.vui.assistant.session;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.provider.AlarmClock;
import cn.yunzhisheng.common.util.LogUtil;

public class AlarmSettingSession extends CommBaseSession {
	public static final String TAG = "AlarmSettingSession";

	public AlarmSettingSession(Context context, Handler sessionManagerHandler) {
		super(context, sessionManagerHandler);
	}

	public void putProtocol(JSONObject jsonProtocol) {
		super.putProtocol(jsonProtocol);
		String time = getJsonValue(mJsonObject, "time");
		String timeArr[] = time.split(":");
		int hour = Integer.parseInt(timeArr[0]);
		int minute = Integer.parseInt(timeArr[1]);
		String repeats = getJsonValue(mJsonObject, "repeat");
		this.startAlarmAction(hour, minute, repeats);
		
		/**2014-11-10 yujun*/
		LogUtil.d(TAG, "--AlarmSettingSession mAnswer : "+mAnswer);
		//playTTS(mAnswer);
		/**------------------*/
		playTTS(mTTS);
	}

	protected void startAlarmAction(int hour, int minute, String repeat) {
		Intent openNewAlarm = new Intent(AlarmClock.ACTION_SET_ALARM);
		openNewAlarm.putExtra(AlarmClock.EXTRA_HOUR, hour);
		openNewAlarm.putExtra(AlarmClock.EXTRA_MINUTES, minute);
		openNewAlarm.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		mContext.startActivity(openNewAlarm);

	}

}
