/**
 * Copyright (c) 2012-2014 Yunzhisheng(Shanghai) Co.Ltd. All right reserved.
 * @FileName : PhoneStateReceiver.java
 * @ProjectName : Tool
 * @PakageName : cn.yunzhisheng.phone
 * @Author : Brant
 * @CreateDate : 2014-6-21
 */
package cn.yunzhisheng.phone;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import cn.yunzhisheng.common.util.LogUtil;
import cn.yunzhisheng.vui.assistant.oem.RomCustomSetting;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Brant
 * @CreateDate : 2014-6-21
 * @ModifiedBy : Brant
 * @ModifiedDate: 2014-6-21
 * @Modified: 2014-6-21: 实现基本功能
 */
public class PhoneStateReceiver extends BroadcastReceiver {
	private static final String TAG = "PhoneStateReceiver";
	private static final String ACTION_PHONE_STATE = "android.intent.action.PHONE_STATE";
	private static final String ACTION_PHONE_STATE_RING = "cn.yunzhisheng.intent.action.call.ring";
	private static final String ACTION_PHONE_STATE_HANGUP = "cn.yunzhisheng.intent.action.call.hangup";
	private static boolean mIncomingFlag = false;
	private static String mPhoneNumber = null;
	private static ReentrantReadWriteLock mListenerLock = new ReentrantReadWriteLock();
	private static Lock mListenerReadLock = mListenerLock.readLock();
	private static Lock mListenerWriteLock = mListenerLock.writeLock();

	private static List<PhoneStateListener> mListeners = new ArrayList<PhoneStateListener>();

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "onReceive:intent " + intent);
		if (mListenerReadLock == null) {
			return;
		}
		try {
			mListenerReadLock.lock();
			if (mListeners == null || mListeners.isEmpty()) {
				return;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			mListenerReadLock.unlock();
		}
		String action = intent.getAction();
		if (Intent.ACTION_NEW_OUTGOING_CALL.equals(action)) {
			mPhoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
		} else if (ACTION_PHONE_STATE.equals(action)) {
			// 来电
			TelephonyManager tm = (TelephonyManager) context
					.getSystemService(Service.TELEPHONY_SERVICE);
			mPhoneNumber = intent
					.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
			switch (tm.getCallState()) {
			case TelephonyManager.CALL_STATE_RINGING:
				Log.d(TAG, "call in ringing :" + mPhoneNumber);
				onCallStateChanged(TelephonyManager.CALL_STATE_RINGING,
						mPhoneNumber);
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:
				Log.d(TAG, "call in offhook");
				onCallStateChanged(TelephonyManager.CALL_STATE_OFFHOOK,
						mPhoneNumber);
				break;
			case TelephonyManager.CALL_STATE_IDLE:
				Log.d(TAG, "call in idle");
				onCallStateChanged(TelephonyManager.CALL_STATE_IDLE,
						mPhoneNumber);
				break;
			default:
				Log.e(TAG, "unknown phont state:" + tm.getCallState());
				break;
			}
		} else if (ACTION_PHONE_STATE_RING.equals(action)) { // 蓝牙电话来电广播
			mPhoneNumber = intent.getStringExtra("phoneNumber");
			onCallStateChanged(TelephonyManager.CALL_STATE_RINGING,
					mPhoneNumber);
		} else if (ACTION_PHONE_STATE_HANGUP.equals(action)) { // 蓝牙挂断
			// 播报tts
			onCallStateChanged(TelephonyManager.CALL_STATE_IDLE, "hangup");
		} 
	}

	public static void registerPhoneStateListener(PhoneStateListener listener) {
		Log.d(TAG, "registerPhoneStateListener:listener " + listener);
		if (listener == null) {
			Log.w(TAG, "param listener null!");
			return;
		}
		try {
			mListenerWriteLock.lock();
			mListeners.add(listener);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			mListenerWriteLock.unlock();
		}
	}

	public static void unregisterPhoneStateListener(PhoneStateListener listener) {
		Log.d(TAG, "unregisterPhoneStateListener:listener " + listener);
		try {
			mListenerWriteLock.lock();
			mListeners.remove(listener);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			mListenerWriteLock.unlock();
		}
	}

	private static void clearPhoneStateListener() {
		Log.d(TAG, "clearPhoneStateListener");
		try {
			mListenerWriteLock.lock();
			mListeners.clear();
			mListeners = null;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			mListenerWriteLock.unlock();
		}
	}

	private void onCallStateChanged(int state, String incomingNumber) {
		try {
			mListenerReadLock.lock();
			for (PhoneStateListener l : mListeners) {
				l.onCallStateChanged(state, incomingNumber);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			mListenerReadLock.unlock();
		}
	}

	public static void release() {
		LogUtil.d(TAG, "release");
		clearPhoneStateListener();
		mListenerReadLock = null;
		mListenerWriteLock = null;
		mListenerLock = null;
	}
}
