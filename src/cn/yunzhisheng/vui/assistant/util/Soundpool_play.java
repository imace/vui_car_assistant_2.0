package cn.yunzhisheng.vui.assistant.util;


import cn.yunzhisheng.common.util.LogUtil;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;


public class Soundpool_play {

	private static final String TAG = "BeepPlayer";
	private static final float DEFAULT_VOLUME = 1.0f;// Default volume

	private final Context mContext;
	private SoundPool mMediaPlayer;
	private OnLoadCompleteListener mOnCompleteListener;
	private int hitOkSfx = -1;
	public Soundpool_play(Context context) {
		mContext = context;
		mMediaPlayer = buildMediaPlayer(mContext);
	}

	
	
	public void playBeepSound(int rawId, boolean looping, OnLoadCompleteListener completeCallback) {
		LogUtil
			.d(TAG, "playBeepSound:rawId " + rawId + ",lopping " + looping + ",completeCallback " + completeCallback);
		mOnCompleteListener = completeCallback;
//		AssetFileDescriptor file = mContext.getResources().openRawResourceFd(rawId);
	

		if (mMediaPlayer == null) {
			throw new RuntimeException("SoundPool has been released.");
		}
		hitOkSfx = mMediaPlayer.load(mContext,rawId, 0);
		if (mMediaPlayer != null && hitOkSfx != -1) {
			mMediaPlayer.play(hitOkSfx, 1, 1, 0, 0, 1);
		}
		
	
	}
	public void release() {
		if (mMediaPlayer != null) {
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
	}
	
	public void stop() {
		if (mMediaPlayer != null && hitOkSfx == -1) {
			mMediaPlayer.stop(hitOkSfx);
		}
	}
	
	private SoundPool buildMediaPlayer(Context mContext2) {
		SoundPool sound = new SoundPool(10, AudioManager.STREAM_SYSTEM,5);
		sound.setOnLoadCompleteListener(new OnLoadCompleteListener() {
			
			@Override
			public void onLoadComplete(SoundPool arg0, int arg1, int arg2) {
				if (mOnCompleteListener != null) {
					mOnCompleteListener.onLoadComplete(null,arg1,arg2);
				}
				
			}
		});
		return sound;
	}

}
