/**
 * Copyright (c) 2012-2014 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * @FileName : MusicPresentor.java
 * @ProjectName : CarPlay
 * @PakageName : cn.yunzhisheng.vui.assistant.media
 * @Author : Brant
 * @CreateDate : 2014-9-16
 */
package cn.yunzhisheng.vui.assistant.media;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import cn.yunzhisheng.common.util.LogUtil;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Brant
 * @CreateDate : 2014-9-16
 * @ModifiedBy : Brant
 * @ModifiedDate: 2014-9-16
 * @Modified:
 * 2014-9-16: 实现基本功能
 */
public class MusicServicePresentor implements ServiceConnection {
	public static final String TAG = "MusicServicePresentor";
	public static final int ERROR_CODE_BINDER_DISCONNECTED = 1;
	private Context mContext;
	private IMusicService mService;
	private IMusicListener mListener;

	private BroadcastReceiver mServiceReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			LogUtil.d(TAG, "onReceive:intent " + intent);
			if (mListener == null) {
				LogUtil.w(TAG, "mListener null!");
				return;
			}
			String action = intent.getAction();
			if (MusicPlaybackService.EVENT_MUSIC_ON_TRACK_ERROR.equals(action)) {
				int errorCode = intent.getIntExtra(MusicPlaybackService.DATA_ERROR_CODE, -1);
				mListener.onTrackStreamError(errorCode);
			} else if (MusicPlaybackService.EVENT_MUSIC_ON_TRACK_START.equals(action)) {
				mListener.onTrackStart();
			} else if (MusicPlaybackService.EVENT_MUSIC_ON_TRACK_STOP.equals(action)) {
				mListener.onTrackStop();
			} else if (MusicPlaybackService.EVENT_MUSIC_ON_TRACK_PAUSE.equals(action)) {
				mListener.onTrackPause();
			} else if (MusicPlaybackService.EVENT_MUSIC_ON_TRACK_CHANGED.equals(action)) {
				int index = intent.getIntExtra(MusicPlaybackService.DATA_INDEX, -1);
				TrackInfo track = intent.getParcelableExtra(MusicPlaybackService.DATA_TRACK);
				mListener.onTrackChanged(index, track);
			} else if (MusicPlaybackService.EVENT_MUSIC_ON_TRACK_BUFFER.equals(action)) {
				int percent = intent.getIntExtra(MusicPlaybackService.DATA_BUFFER_PERCENT, -1);
				mListener.onTrackBuffering(percent);
			} else if (MusicPlaybackService.EVENT_MUSIC_ON_TRACK_PLAY_PROGRESS.equals(action)) {
				int length = intent.getIntExtra(MusicPlaybackService.DATA_TRACK_LENGTH, -1);
				int duration = intent.getIntExtra(MusicPlaybackService.DATA_TRACK_DURATION, -1);
				mListener.onTrackProgress(length, duration);
			}
		}
	};

	public MusicServicePresentor(Context context, IMusicListener listener) {
		mContext = context;
		setListener(listener);
		registReceiver();
		bindService();
	}

	public void setListener(IMusicListener listener) {
		mListener = listener;
	}

	private void registReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(MusicPlaybackService.EVENT_MUSIC_ON_TRACK_ERROR);
		filter.addAction(MusicPlaybackService.EVENT_MUSIC_ON_TRACK_START);
		filter.addAction(MusicPlaybackService.EVENT_MUSIC_ON_TRACK_STOP);
		filter.addAction(MusicPlaybackService.EVENT_MUSIC_ON_TRACK_PAUSE);
		filter.addAction(MusicPlaybackService.EVENT_MUSIC_ON_TRACK_CHANGED);
		filter.addAction(MusicPlaybackService.EVENT_MUSIC_ON_TRACK_BUFFER);
		filter.addAction(MusicPlaybackService.EVENT_MUSIC_ON_TRACK_PLAY_PROGRESS);
		mContext.registerReceiver(mServiceReceiver, filter);
	}

	private void unregistReceiver() {
		try {
			mContext.unregisterReceiver(mServiceReceiver);
		} catch (Exception e) {
			LogUtil.printStackTrace(e);
		}
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		LogUtil.d(TAG, "onServiceConnected");
		mService = IMusicService.Stub.asInterface(service);
		if (mListener != null) {
			mListener.onServiceConnected();
		}
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		LogUtil.d(TAG, "onServiceDisconnected");
		mService = null;
		if (mListener != null) {
			mListener.onServiceDisconnected();
		}
	}

	public void setPlayList(List<TrackInfo> playList) {
		LogUtil.d(TAG, "setPlayList:playList " + playList);
		if (mService == null) {
			rebindService();
			return;
		}
		try {
			mService.setPlayList(playList);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void play() {
		LogUtil.d(TAG, "play");
		if (mService == null) {
			rebindService();
			return;
		}
		try {
			mService.play();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void pause() {
		LogUtil.d(TAG, "pause");
		if (mService == null) {
			rebindService();
			return;
		}
		try {
			mService.pause();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void stop() {
		LogUtil.d(TAG, "stop");
		if (mService == null) {
			rebindService();
			return;
		}
		try {
			mService.stop();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void prev() {
		LogUtil.d(TAG, "prev");
		if (mService == null) {
			rebindService();
			return;
		}
		try {
			mService.prev();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void next() {
		LogUtil.d(TAG, "next");
		if (mService == null) {
			rebindService();
			return;
		}
		try {
			mService.next();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void skipTo(int index) {
		LogUtil.d(TAG, "skipTo:index " + index);
		if (mService == null) {
			rebindService();
			return;
		}
		try {
			mService.skipTo(index);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean isPlaying() {
		LogUtil.d(TAG, "isPlaying");
		if (mService == null) {
			rebindService();
			return false;
		}
		try {
			return mService.isPlaying();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public TrackInfo getCurrentTrack() {
		LogUtil.d(TAG, "getCurrentTrack");
		if (mService == null) {
			rebindService();
			return null;
		}
		try {
			return mService.getCurrentTrack();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public int getCurrentTrackIndex() {
		LogUtil.d(TAG, "getCurrentTrackIndex");
		if (mService == null) {
			rebindService();
			return -1;
		}
		try {
			return mService.getCurrentTrackIndex();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}

	public List<TrackInfo> getPlayList() {
		LogUtil.d(TAG, "getPlayList");
		if (mService == null) {
			rebindService();
			return null;
		}
		try {
			return mService.getPlayList();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public void exit() {
		LogUtil.d(TAG, "exit");
		stop();
		mContext.unbindService(this);
		Intent intent = new Intent(mContext, MusicPlaybackService.class);
		mContext.stopService(intent);
		release();
	}

	private void bindService() {
		LogUtil.d(TAG, "bindService");
		Intent intent = new Intent(mContext, MusicPlaybackService.class);
		// mContext.startService(intent);
		mContext.bindService(intent, this, Context.BIND_AUTO_CREATE);
	}

	private void rebindService() {
		LogUtil.d(TAG, "rebindService");
		bindService();
		if (mListener != null) {
			mListener.onBinderError(ERROR_CODE_BINDER_DISCONNECTED);
		}
	}

	private void release() {
		LogUtil.d(TAG, "release");
		unregistReceiver();
		mServiceReceiver = null;
		mContext = null;
	}

}
