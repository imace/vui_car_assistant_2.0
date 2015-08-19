/**
 * Copyright (c) 2012-2012 Yunzhisheng(Shanghai) Co.Ltd. All right reserved.
 * @FileName : MusicInterrupter.java
 * @ProjectName : iShuoShuo2
 * @PakageName : cn.yunzhisheng.vui.assistant.media
 * @Author : Brant
 * @CreateDate : 2012-12-28
 */
package cn.yunzhisheng.vui.assistant.media;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import cn.yunzhisheng.phone.PhoneStateReceiver;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Brant
 * @CreateDate : 2012-12-28
 * @ModifiedBy : Brant
 * @ModifiedDate: 2012-12-28
 * @Modified:
 * 2012-12-28: 实现基本功能
 */
public class MusicInterrupter extends PhoneStateListener {
	public static final String TAG = "MusicInterrupter";
	private Context mContext;
	private WifiManager mWifiManager;
	private WifiLock mWifiLock;

	private IMusicInterruptListener mInterruptListener;

	public MusicInterrupter(Context context) {
		mContext = context;
		mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
	}

	public void setInterruptListener(IMusicInterruptListener listener) {
		mInterruptListener = listener;
	}

	public void start() {
		mWifiLock = mWifiManager.createWifiLock(TAG);
		mWifiLock.setReferenceCounted(false);
		mWifiLock.acquire();
		PhoneStateReceiver.registerPhoneStateListener(this);
	}

	public void stop() {
		if (mWifiLock == null) {
			return;
		}
		mWifiLock.release();
		mWifiLock = null;
		mWifiManager = null;
		PhoneStateReceiver.unregisterPhoneStateListener(this);
	}

	@Override
	public void onCallStateChanged(int state, String incomingNumber) {
		super.onCallStateChanged(state, incomingNumber);
		switch (state) {
		case TelephonyManager.CALL_STATE_RINGING:
			if (mInterruptListener != null) {
				mInterruptListener.onTrigger();
			}
			break;
		case TelephonyManager.CALL_STATE_OFFHOOK:
			if (mInterruptListener != null) {
				mInterruptListener.onResume();
			}
			break;
		}
	}

	public static interface IMusicInterruptListener {
		void onTrigger();

		void onResume();
	}
}
