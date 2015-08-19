/**
 * ib* Copyright (c) 2012-2012 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * @FileName : TalkServicePresentor.java
 * @ProjectName : V Plus 1.0
 * @PakageName : cn.yunzhisheng.vui.assistant.talk
 * @Author : Dancindream
 * @CreateDate : 2012-5-22
 */
package cn.yunzhisheng.vui.assistant.talk;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import cn.yunzhisheng.common.util.LogUtil;
import cn.yunzhisheng.tts.offline.TTSPlayerListener;

import com.android.kwmusic.KWMusicService;

public class TalkServicePresentor {
	public static final String TAG = "TalkServicePresentor";

	private Context mContext = null;
	private ITalkServicePresentorListener mServicePresentorListener = null;
	private ITalkServiceWakeupListener mServiceWakeupListener = null;
	private TTSPlayerListener mTTSPlayerListener;
	private ITalkService mTalkService = null;
	private String mPackageName;

	private ServiceConnection mServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			LogUtil.d(TAG, "onServiceDisconnected");
			mTalkService = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			LogUtil.d(TAG, "onServiceConnected");
			mTalkService = ITalkService.Stub.asInterface(service);
		}
	};

	private BroadcastReceiver mServiceReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			LogUtil.d(TAG, "onReceive:intent " + intent);
			String action = intent.getAction();
			Bundle bundle = intent.getExtras();
			if (mServicePresentorListener == null) {
				LogUtil.e(TAG, "onReceive:mServicePresentorListener null!");
				return;
			}
			if (mServiceWakeupListener == null) {
				LogUtil.e(TAG, "onReceive:mServiceWakeupListener null!");
				return;
			}

			if (mTTSPlayerListener == null) {
				LogUtil.e(TAG, "onReceive:mTTSPlayerListener null!");
				return;
			}

			try {
				if (action.equals(TalkService.TALK_EVENT_ON_INITDONE)) {
					mServicePresentorListener.onTalkInitDone();
				} else if (action.equals(TalkService.TALK_EVENT_ON_SESSION_PROTOCAL)) {
					// 20150316 add by Dancindream Fix FAILED BINDER TRANSACTION
					// & android.os.TransactionTooLargeException
					Bundle extras = intent.getExtras();
					String id = extras.getString(TalkService.TALK_DATA_PROTOCAL);
					String protocol = TalkService.getSessionProtocal(id);
					mServicePresentorListener.onSessionProtocal(protocol);
				} else if (action.equals(TalkService.TALK_EVENT_ON_START)) {
					mServicePresentorListener.onTalkStart();
				} else if (action.equals(TalkService.TALK_EVENT_ON_STOP)) {
					mServicePresentorListener.onTalkStop();
				} else if (action.equals(TalkService.TALK_EVENT_ON_RECORDING_START)) {
					mServicePresentorListener.onTalkRecordingStart();
				} else if (action.equals(TalkService.TALK_EVENT_ON_RECORDING_STOP)) {
					mServicePresentorListener.onTalkRecordingStop();
				} else if (action.equals(TalkService.TALK_EVENT_ON_CANCEL)) {
					mServicePresentorListener.onTalkCancel();
				} else if (TalkService.TALK_EVENT_RESULT.equals(action)) {
					String result = bundle.getString(TalkService.TALK_DATA_RESULT);
					mServicePresentorListener.onTalkResult(result);
				} else if (action.equals(TalkService.WAKEUP_EVENT_ON_INITDONE)) {
					mServiceWakeupListener.onInitDone();
				} else if (action.equals(TalkService.WAKEUP_EVENT_ON_START)) {
					mServiceWakeupListener.onStart();
				} else if (action.equals(TalkService.WAKEUP_EVENT_ON_STOP)) {
					mServiceWakeupListener.onStop();
				} else if (action.equals(TalkService.WAKEUP_EVENT_ON_SUCCESS)) {
					mServiceWakeupListener.onSuccess();
				} else if (action.equals(TalkService.WAKEUP_EVENT_ON_ERROR)) {
					Bundle ex = intent.getExtras();
					int code = ex.getInt(TalkService.TALK_DATA_ERROR_CODE);
					String message = ex.getString(TalkService.TALK_DATA_ERROR_MESSAGE);
					mServiceWakeupListener.onError(code, message);
				} else if (TalkService.TTS_EVENT_ON_PLAY_BEGIN.equals(action)) {
					mTTSPlayerListener.onPlayBegin();
				} else if (TalkService.TTS_EVENT_ON_PLAY_END.equals(action)) {
					mTTSPlayerListener.onPlayEnd();
				} else if (TalkService.TTS_EVENT_ON_ERROR.equals(action)) {
					mTTSPlayerListener.onError(null);
				} else if (TalkService.TTS_EVENT_ON_CANCEL.equals(action)) {
					mTTSPlayerListener.onCancel();
				} else if (TalkService.TTS_EVENT_ON_BUFFER.equals(action)) {
					mTTSPlayerListener.onBuffer();
				} else if (action.equals(TalkService.TALK_EVENT_ON_DATA_DONE)) {
					mServicePresentorListener.onTalkDataDone();
				} else if (TalkService.ACTIVE_EVENT_STATUS_CHANGED_MESSAGE.equals(action)) {
					int flag = bundle.getInt(TalkService.ACTIVE_DATA_STATUS);
					mServicePresentorListener.onActiveStatusChanged(flag);
				} else if (TalkService.ACTIVE_EVENT_ISACTIVE_MESSAGE.equals(action)) {
					boolean flag = bundle.getBoolean(TalkService.ACTIVE_STATUS);
					LogUtil.d(TAG, "talk service presentor is active: " + flag + "");
					mServicePresentorListener.isActive(flag);
				}
			} catch (Exception e) {
				mTTSPlayerListener.onCancel();// 如果出现异常，则流程取消
				LogUtil.printStackTrace(e);
			}

		}
	};

	public TalkServicePresentor(Context context, ITalkServicePresentorListener l, ITalkServiceWakeupListener ll,
								TTSPlayerListener TTSListener) {
		mContext = context;
		mServicePresentorListener = l;
		mServiceWakeupListener = ll;
		mTTSPlayerListener = TTSListener;
		getPackageInfo(context);
		registReceiver();
		Intent intent = new Intent(mContext, TalkService.class);
		// mContext.startService(intent);
		mContext.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
		
		Intent intentMusic = new Intent(context, KWMusicService.class);
		context.startService(intentMusic);
	}

	private void registReceiver() {
		LogUtil.d(TAG, "registReceiver");
		IntentFilter filter = new IntentFilter();
		filter.addAction(TalkService.TALK_EVENT_ON_INITDONE);
		filter.addAction(TalkService.TALK_EVENT_ON_SESSION_PROTOCAL);
		filter.addAction(TalkService.TALK_EVENT_ON_START);
		filter.addAction(TalkService.TALK_EVENT_ON_STOP);
		filter.addAction(TalkService.TALK_EVENT_ON_CANCEL);
		filter.addAction(TalkService.WAKEUP_EVENT_ON_INITDONE);
		filter.addAction(TalkService.WAKEUP_EVENT_ON_START);
		filter.addAction(TalkService.WAKEUP_EVENT_ON_STOP);
		filter.addAction(TalkService.TALK_EVENT_ON_RECORDING_START);
		filter.addAction(TalkService.TALK_EVENT_ON_RECORDING_STOP);
		filter.addAction(TalkService.TALK_EVENT_RESULT);
		filter.addAction(TalkService.WAKEUP_EVENT_ON_SUCCESS);
		filter.addAction(TalkService.WAKEUP_EVENT_ON_ERROR);
		filter.addAction(TalkService.TTS_EVENT_ON_PLAY_BEGIN);
		filter.addAction(TalkService.TTS_EVENT_ON_PLAY_END);
		filter.addAction(TalkService.TTS_EVENT_ON_ERROR);
		filter.addAction(TalkService.TTS_EVENT_ON_CANCEL);
		filter.addAction(TalkService.TTS_EVENT_ON_BUFFER);
		filter.addAction(TalkService.TALK_EVENT_ON_DATA_DONE);
		filter.addAction(TalkService.ACTIVE_EVENT_STATUS_CHANGED_MESSAGE);
		filter.addAction(TalkService.ACTIVE_EVENT_ISACTIVE_MESSAGE);
		filter.addCategory(mPackageName);
		mContext.registerReceiver(mServiceReceiver, filter);
	}

	private void unregistReceiver() {
		try {
		    if(mContext != null){
		        mContext.unregisterReceiver(mServiceReceiver);
		    }
		} catch (Exception e) {
			LogUtil.printStackTrace(e);
		}
	}

	public void startWakeup() {
		LogUtil.d(TAG, "startWakeup");
		if(mContext != null){
		    mContext.sendBroadcast(new Intent(TalkService.WAKEUP_EVENT_START));
		}
	}

	public void stopWakeup() {
		LogUtil.d(TAG, "stopWakeup");
		if(mContext != null){
		    mContext.sendBroadcast(new Intent(TalkService.WAKEUP_EVENT_STOP));
		}
	}
	
	public void cancelWakeup() {
		LogUtil.d(TAG, "cancelWakeup");
		if(mContext != null){
		    mContext.sendBroadcast(new Intent(TalkService.WAKEUP_EVENT_CANCEL));
		}
	}

	public void releaseSession() {
		LogUtil.d(TAG, "releaseSession");
		if(mContext != null){
		    mContext.sendBroadcast(new Intent(TalkService.SESSION_EVENT_REALEASE));
		}
	}
	
	public void setRecognizerType(String type) {
		LogUtil.d(TAG, "setRecognizerType type : " + type);
		//为防止广播异常，取消广播操作。
/*		final Intent intent=new Intent(TalkService.SET_RECOGNIZERTYPE);  
        intent.putExtra("recognizerType", type);  
		mContext.sendBroadcast(intent);*/
		try {
			mTalkService.setRecognizerTalkType(type);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public void startTalk() {
		LogUtil.d(TAG, "startTalk");
		try {
			if (mTalkService != null) {
				mTalkService.startTalk();
			}
		} catch (RemoteException e) {
			LogUtil.printStackTrace(e);
		}
	}

	public void stopTalk() {
		LogUtil.d(TAG, "stopTalk");
		try {
			if (mTalkService != null) {
				mTalkService.stopTalk();
			}
		} catch (RemoteException e) {
			LogUtil.printStackTrace(e);
		}
	}

	public void putCustomText(String text) {
		LogUtil.d(TAG, "putCustomText text:" + text);
		try {
			if (mTalkService != null) {
				mTalkService.putCustomText(text);
			}
		} catch (RemoteException e) {
			LogUtil.printStackTrace(e);
		}
	}

	public void setProtocal(String protocal) {
		LogUtil.d(TAG, "setProtocal protocal:" + protocal);
		try {
			if (mTalkService != null) {
				mTalkService.setProtocal(protocal);
			}
		} catch (RemoteException e) {
			LogUtil.printStackTrace(e);
		}
	}

	public void cancelTalk(boolean callback) {
		LogUtil.d(TAG, "cancelTalk: callback " + callback);
		try {
			if (mTalkService != null) {
				mTalkService.cancelTalk(callback);
			}
		} catch (RemoteException e) {
			LogUtil.printStackTrace(e);
		}
	}

	public void playTTS(String tts) {
		LogUtil.d(TAG, "playTTS:tts " + tts);
		try {
			if (mTalkService != null) {
				mTalkService.playTTS(tts);
			}
		} catch (RemoteException e) {
			LogUtil.printStackTrace(e);
		}
	}

	public void cancelTTS() {
		LogUtil.d(TAG, "cancelTTS");
		try {
			if (mTalkService != null) {
				mTalkService.cancelTTS();
			}
		} catch (RemoteException e) {
			LogUtil.printStackTrace(e);
		}
	}

	public void onStart() {
		LogUtil.d(TAG, "onStart");
		try {
			if (mTalkService != null) {
				mTalkService.onStart();
			}
		} catch (RemoteException e) {
			LogUtil.printStackTrace(e);
		}
	}

	public void onStop() {
		LogUtil.d(TAG, "onStop");
		try {
			if (mTalkService != null) {
				mTalkService.onStop();
			}
		} catch (RemoteException e) {
			LogUtil.printStackTrace(e);
		}
	}

	public void onPause() {
		LogUtil.d(TAG, "onPause");
		unregistReceiver();

		cancelTalk(false);
	}

	public void onResume() {
		LogUtil.d(TAG, "onResume");
		registReceiver();
	}

	public void onDestroy() {
		mServicePresentorListener = null;
		mServiceWakeupListener = null;
		mTTSPlayerListener = null;
		try {
		    if(mContext != null){
		        mContext.unbindService(mServiceConnection);
		    }
		} catch (Exception e) {
			LogUtil.printStackTrace(e);
		}
		unregistReceiver();
		mContext = null;
	}

	private void getPackageInfo(Context context) {
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			mPackageName = info.packageName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			mPackageName = null;
		}
		LogUtil.i(TAG, "-presentor-getPackageName is--" + mPackageName);
	}

	@SuppressWarnings("finally")
	public String getName(String number) {
		String name = "";
		try {
			name = mTalkService.getContactName(number);
		} catch (RemoteException e) {
			e.printStackTrace();
		} finally {
			return name;
		}
	}
}
