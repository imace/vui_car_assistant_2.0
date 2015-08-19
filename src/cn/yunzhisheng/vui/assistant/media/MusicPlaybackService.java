/**
 * Copyright (c) 2012-2013 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * @FileName : MusicPlaybackService.java
 * @ProjectName : iShuoShuo2_work
 * @PakageName : cn.yunzhisheng.vui.assistant.media
 * @Author : CavanShi
 * @CreateDate : 2013-3-27
 */
package cn.yunzhisheng.vui.assistant.media;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Stack;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import cn.yunzhisheng.common.util.LogUtil;
import cn.yunzhisheng.vui.assistant.MainActivity;
import com.ilincar.voice.R;
import cn.yunzhisheng.vui.assistant.preference.CommandPreference;
import cn.yunzhisheng.vui.assistant.preference.UserPreference;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : CavanShi
 * @CreateDate : 2013-3-27
 * @ModifiedBy : CavanShi
 * @ModifiedDate: 2013-3-27
 * @Modified:
 * 2013-3-27: 实现基本功能
 */
public class MusicPlaybackService extends Service {
	public static final String TAG = "MusicPlaybackService";

	public static final String EVENT_MUSIC_ON_TRACK_ERROR = "MUSIC_ON_TRACK_ERROR";
	public static final String EVENT_MUSIC_ON_TRACK_START = "MUSIC_ON_TRACK_START";
	public static final String EVENT_MUSIC_ON_TRACK_STOP = "MUSIC_ON_TRACK_STOP";
	public static final String EVENT_MUSIC_ON_TRACK_PAUSE = "MUSIC_ON_TRACK_PAUSE";
	public static final String EVENT_MUSIC_ON_TRACK_CHANGED = "MUSIC_ON_TRACK_CHANGED";
	public static final String EVENT_MUSIC_ON_TRACK_BUFFER = "MUSIC_ON_TRACK_BUFFER";
	public static final String EVENT_MUSIC_ON_TRACK_PLAY_PROGRESS = "MUSIC_ON_TRACK_PLAY_PROGRESS";

	public static final String DATA_ERROR_CODE = "ERROR_CODE";
	public static final String DATA_INDEX = "INDEX";
	public static final String DATA_TRACK = "TRACK";
	public static final String DATA_BUFFER_PERCENT = "BUFFER_PERCENT";
	public static final String DATA_TRACK_LENGTH = "TRACK_LENGTH";
	public static final String DATA_TRACK_DURATION = "TRACK_DURATION";

	public static final String PLAYSTATE_CHANGED = "com.android.music.playstatechanged";
	public static final String META_CHANGED = "com.android.music.metachanged";
	public static final String QUEUE_CHANGED = "com.android.music.queuechanged";

	// private static final int MSG_ACTION_PLAY = 0x01;
	// private static final int MSG_ACTION_PREV = 0x02;
	// private static final int MSG_ACTION_NEXT = 0x03;
	// private static final int MSG_ACTION_SELECT = 0x07;
	// private static final int MSG_ACTION_STOP = 0x08;
	// private static final int MSG_ACTION_PAUSE = 0x09;
	private static final int FOCUSCHANGE = 4;
	private static final int FADEDOWN = 5;
	private static final int FADEUP = 6;

	public static final int IDLE = 0x01;
	public static final int BUFF = 0x02;
	public static final int PLAYING = 0x03;
	public static final int PAUSE = 0x04;
	public static final int STOP = 0x05;
	public static final int ERROR = 0x06;

	private static final int mNotificationCode = 33901;

	private int mServiceStatus = IDLE;
	private int mCurrentTime = 0;
	private int mCurrentBuffPercent = 0;
	private int mErrorCode = 0;

	private NotificationManager mNotificationManager;

	private PlayerEngine mPlayEngine;

	private UserPreference mPreferenceAction;
	private AudioManager mAudioManager;
	// used to track what type of audio focus loss caused the playback to pause
	private boolean mPausedByTransientLossOfFocus = false;

	private MusicServiceStub mBinder = new MusicServiceStub(this);

	private Handler mHandler = new Handler() {
		float mCurrentVolume = 1.0f;

		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case FADEDOWN:
				mCurrentVolume -= .05f;
				if (mCurrentVolume > .2f) {
					mHandler.sendEmptyMessageDelayed(FADEDOWN, 10);
				} else {
					mCurrentVolume = .2f;
				}
				if (mPlayEngine != null) {
					mPlayEngine.setVolume(mCurrentVolume);
				}
				break;
			case FADEUP:
				mCurrentVolume += .01f;
				if (mCurrentVolume < 1.0f) {
					mHandler.sendEmptyMessageDelayed(FADEUP, 10);
				} else {
					mCurrentVolume = 1.0f;
				}
				if (mPlayEngine != null) {
					mPlayEngine.setVolume(mCurrentVolume);
				}
				break;
			case FOCUSCHANGE:
				// This code is here so we can better synchronize it with the
				// code that
				// handles fade-in
				switch (msg.arg1) {
				case AudioManager.AUDIOFOCUS_LOSS:
					LogUtil.v(TAG, "AudioFocus: received AUDIOFOCUS_LOSS");
					if (isPlaying()) {
						mPausedByTransientLossOfFocus = false;
					}
					pause();
					break;
				case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
					mHandler.removeMessages(FADEUP);
					mHandler.sendEmptyMessage(FADEDOWN);
					break;
				case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
					LogUtil.v(TAG, "AudioFocus: received AUDIOFOCUS_LOSS_TRANSIENT");
					if (isPlaying()) {
						mPausedByTransientLossOfFocus = true;
					}
					pause();
					break;
				case AudioManager.AUDIOFOCUS_GAIN:
					LogUtil.v(TAG, "AudioFocus: received AUDIOFOCUS_GAIN");
					if (!isPlaying() && mPausedByTransientLossOfFocus) {
						mPausedByTransientLossOfFocus = false;
						mCurrentVolume = 0f;
						mPlayEngine.setVolume(mCurrentVolume);
						play(); // also queues a fade-in
					} else {
						mHandler.removeMessages(FADEDOWN);
						mHandler.sendEmptyMessage(FADEUP);
					}
					break;
				default:
					LogUtil.e(TAG, "Unknown audio focus change code");
				}
				break;
			}
		};
	};

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			LogUtil.d(TAG, "onReceive:intent " + intent);

			Bundle bundle = intent.getExtras();
			if (bundle != null) {
				for (String key : bundle.keySet()) {
					LogUtil.d(TAG, "		key " + key + ",value " + bundle.get(key));
				}
			}
			String action = intent.getAction();
			String cmd = intent.getStringExtra(CommandPreference.CMDNAME);
			try {

				if (CommandPreference.CMDTOGGLEPAUSE.equals(cmd) || CommandPreference.TOGGLEPAUSE_ACTION.equals(action)) {
					if (isPlaying()) {
						pause();
						mPausedByTransientLossOfFocus = false;
					} else {
						play();
					}
				} else if (CommandPreference.CMDPAUSE.equals(cmd) || CommandPreference.PAUSE_ACTION.equals(action)) {
					pause();
					mPausedByTransientLossOfFocus = false;
				} else if (CommandPreference.CMDPLAY.equals(cmd)) {
					play();
				} else if (CommandPreference.CMDPREVIOUS.equals(cmd)
							|| CommandPreference.PREVIOUS_ACTION.equals(action)) {
					prev();
				} else if (CommandPreference.CMDNEXT.equals(cmd) || CommandPreference.NEXT_ACTION.equals(action)) {
					next();
				} else if (CommandPreference.CMDSTOP.equals(cmd)) {
					stop();
					mPausedByTransientLossOfFocus = false;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	private PlayerEngineListener mPlayerEngineListener = new PlayerEngineListener() {
		@Override
		public void onTrackStreamError(int what) {
			hideNotification();
			mErrorCode = what;
			mServiceStatus = ERROR;
			mPreferenceAction.putBoolean("IS_MUSIC_PLAYING", false);
			Bundle extras = new Bundle();
			extras.putInt(DATA_ERROR_CODE, what);
			showMessage(EVENT_MUSIC_ON_TRACK_ERROR, extras);
		}

		@Override
		public void onTrackStop() {
			mServiceStatus = STOP;
			hideNotification();
			showMessage(EVENT_MUSIC_ON_TRACK_STOP);
		}

		@Override
		public void onTrackStart() {
			mServiceStatus = PLAYING;
			mAudioManager.requestAudioFocus(
				mAudioFocusListener,
				AudioManager.STREAM_MUSIC,
				AudioManager.AUDIOFOCUS_GAIN);
			showMessage(EVENT_MUSIC_ON_TRACK_START);
		}

		@Override
		public void onTrackPause() {
			mServiceStatus = PAUSE;
			hideNotification();
			showMessage(EVENT_MUSIC_ON_TRACK_PAUSE);
		}

		@Override
		public void onTrackChanged(int index, TrackInfo track) {
			LogUtil.d(TAG, "onTrackChanged: index " + index + ", track " + track);
			mServiceStatus = BUFF;
			Bundle extras = new Bundle();
			extras.putParcelable(DATA_TRACK, track);
			extras.putInt(DATA_INDEX, index);
			showMessage(EVENT_MUSIC_ON_TRACK_CHANGED, extras);
			showNotification(track.getTitle(), track.getArtist());
		}

		@Override
		public void onTrackBuffering(int percent) {
			mCurrentBuffPercent = percent;
			Bundle extras = new Bundle();
			extras.putInt(DATA_BUFFER_PERCENT, percent);
			showMessage(EVENT_MUSIC_ON_TRACK_BUFFER, extras);
		}

		@Override
		public void onTrackProgress(int seconds, int duration) {
			mCurrentTime = seconds;
			Bundle extras = new Bundle();
			extras.putInt(DATA_TRACK_LENGTH, seconds);
			extras.putInt(DATA_TRACK_DURATION, duration);
			showMessage(EVENT_MUSIC_ON_TRACK_PLAY_PROGRESS, extras);
		}
	};

	private OnAudioFocusChangeListener mAudioFocusListener = new OnAudioFocusChangeListener() {
		public void onAudioFocusChange(int focusChange) {
			mHandler.obtainMessage(FOCUSCHANGE, focusChange, 0).sendToTarget();
		}
	};

	public void onCreate() {
		LogUtil.d(TAG, "onCreate");
		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);// 获取声音管理器
		mPlayEngine = new PlayerEngine();
		mPlayEngine.registerListener(mPlayerEngineListener);
		initNotification();
		mPreferenceAction = new UserPreference(this);

		IntentFilter commandFilter = new IntentFilter();
		commandFilter.addAction(CommandPreference.SERVICECMD);
		commandFilter.addAction(CommandPreference.TOGGLEPAUSE_ACTION);
		commandFilter.addAction(CommandPreference.PAUSE_ACTION);
		commandFilter.addAction(CommandPreference.NEXT_ACTION);
		commandFilter.addAction(CommandPreference.PREVIOUS_ACTION);
		registerReceiver(mReceiver, commandFilter);
	}

	private void initNotification() {
		if (mNotificationManager == null) {
			mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		}
	}

	private void showNotification(String title, String artist) {
		Notification nc = new Notification(R.drawable.ic_launcher, title + "-" + artist, System.currentTimeMillis());
		nc.flags = Notification.FLAG_ONGOING_EVENT;
		nc.setLatestEventInfo(
			MusicPlaybackService.this,
			title,
			artist,
			PendingIntent.getActivity(
				this,
				0,
				new Intent(this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
				0));
		mNotificationManager.notify(mNotificationCode, nc);
	}

	private void hideNotification() {
		mNotificationManager.cancel(mNotificationCode);
	}

	public boolean isPlaying() {
		if (mPlayEngine != null) {
			return mPlayEngine.isPlaying();
		}
		return false;
	}

	public int getMusicServiceStatus() {
		return mServiceStatus;
	}

	public int getCurrentTime() {
		return mCurrentTime;
	}

	public int getCurrentIndex() {
		return mPlayEngine.getPlaylist().getSelectedIndex();
	}

	public TrackInfo getCurrentTrack() {
		return mPlayEngine.getCurrentPlayTrack();
	}

	public int getCurrentTrackDuration() {
		if (mPlayEngine.getCurrentPlayTrack() != null) {
			return mPlayEngine.getCurrentPlayTrack().getDuration();
		}
		return 0;
	}

	public int getCurrentBuffPrecent() {
		return mCurrentBuffPercent;
	}

	public int getErrorCode() {
		return mErrorCode;
	}

	public void play() {
		LogUtil.d(TAG, "play");
		mPlayEngine.play();
		mHandler.removeMessages(FADEDOWN);
		mHandler.sendEmptyMessage(FADEUP);
	}

	public void pause() {
		LogUtil.d(TAG, "pause");
		mHandler.removeMessages(FADEUP);
		if (mPlayEngine.isPlaying()) {
			mPlayEngine.pause();
		}
	}

	public void stop() {
		LogUtil.d(TAG, "stop");
		mPlayEngine.stop();
	}

	public void prev() {
		LogUtil.d(TAG, "prev");
		mPlayEngine.prev();
	}

	public void next() {
		LogUtil.d(TAG, "next");
		mPlayEngine.next();
	}

	public void skipTo(int position) {
		LogUtil.d(TAG, "skipTo: position " + position);
		if (position == mPlayEngine.getPlaylist().getSelectedIndex()) {
			return;
		}
		mPlayEngine.skipTo(position);
	}

	@Override
	public void onDestroy() {
		LogUtil.d(TAG, "onDestroy");
		mAudioManager.abandonAudioFocus(mAudioFocusListener);
		unregisterReceiver(mReceiver);
		if (mPlayEngine != null) {
			mPlayEngine.unregisterListener(mPlayerEngineListener);
			mPlayEngine.release();
			mPlayEngine = null;
		}

		hideNotification();
	}

	// public void startPlay() {
	// if (mPlayEngine == null) {
	// mPlayEngine = new PlayerEngine();
	// }
	// mPlayEngine.registerListener(mPlayerEngineListener);
	//
	// if (mPlayEngine.getPlaylist() != null && mPlayEngine.getPlaylist().size()
	// > 0) {
	// int index = mPlayEngine.getPlaylist().getSelectedIndex();
	// mPlayEngine.getPlaylist().select(index);
	// mPlayEngine.play();
	// } else {
	// }
	// }

	public TrackInfo getCurrentPlayTrack() {
		return mPlayEngine.getCurrentPlayTrack();
	}

	@Override
	public IBinder onBind(Intent intent) {
		LogUtil.d(TAG, "onBind:intent " + intent);
		return mBinder;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		LogUtil.d(TAG, "onUnbind:intent " + intent);
		return super.onUnbind(intent);
	}

	public void setPlayList(List<TrackInfo> playList) {
		if (mPlayEngine != null) {
			mPlayEngine.openPlaylist(new PlayList(playList));
		}
	}

	public PlayList getPlayList() {
		if (mPlayEngine != null) {
			return mPlayEngine.getPlaylist();
		}
		return null;
	}

	private void showMessage(String message, Bundle extras) {
		Intent intent = new Intent(message);
		intent.putExtras(extras);
		sendBroadcast(intent);
	}

	private void showMessage(String message) {
		Intent intent = new Intent(message);
		sendBroadcast(intent);
	}

	/*
	 * By making this a static class with a WeakReference to the Service, we
	 * ensure that the Service can be GCd even when the system process still
	 * has a remote reference to the stub.
	 */
	static class MusicServiceStub extends IMusicService.Stub {
		WeakReference<MusicPlaybackService> mService;

		MusicServiceStub(MusicPlaybackService service) {
			mService = new WeakReference<MusicPlaybackService>(service);
		}

		@Override
		public void setPlayList(List<TrackInfo> playList) throws RemoteException {
			mService.get().setPlayList(playList);
		}

		@Override
		public void play() throws RemoteException {
			mService.get().play();
		}

		@Override
		public void pause() throws RemoteException {
			mService.get().pause();
		}

		@Override
		public void stop() throws RemoteException {
			mService.get().stop();
		}

		@Override
		public void prev() throws RemoteException {
			mService.get().prev();
		}

		@Override
		public void next() throws RemoteException {
			mService.get().next();
		}

		@Override
		public void skipTo(int index) throws RemoteException {
			mService.get().skipTo(index);
		}

		@Override
		public boolean isPlaying() throws RemoteException {
			return mService.get().isPlaying();
		}

		@Override
		public TrackInfo getCurrentTrack() throws RemoteException {
			return mService.get().getCurrentTrack();
		}

		@Override
		public int getCurrentTrackIndex() throws RemoteException {
			return mService.get().getCurrentIndex();
		}

		@Override
		public List<TrackInfo> getPlayList() throws RemoteException {
			return mService.get().getPlayList().getAllTracks();
		}
	}
}
