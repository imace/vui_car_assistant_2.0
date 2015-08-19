package cn.yunzhisheng.vui.assistant;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.res.Configuration;
import cn.yunzhisheng.common.net.Network;
import cn.yunzhisheng.common.util.LogUtil;
import cn.yunzhisheng.vui.assistant.preference.AssistantPreference;
import cn.yunzhisheng.vui.assistant.talk.TalkService;
import cn.yunzhisheng.vui.assistant.util.CrashHandler;

public class MainApplication extends Application {
	private static final String TAG = "MainApplication";
	public static int mErrorRetalkCount = 0;

	@SuppressLint("NewApi")
	@Override
	public void onCreate() {
		super.onCreate();
		AssistantPreference.init(MainApplication.this);
		init();
		LogUtil.d(TAG, "onCreate");
		CrashHandler crasHandler = CrashHandler.getInstance();
		crasHandler.init(this);
	}

	public void init() {
		Network.checkNetworkConnected(MainApplication.this);
	}

	@Override
	public void onTerminate() {
		LogUtil.d(TAG, "onTerminate");
		super.onTerminate();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}

	public IDataControl getDataControl() {
		return TalkService.getDataControl();
	}
}
