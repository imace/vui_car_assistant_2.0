/*
 * Copyright (C) 2010 ZXing authors
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.yunzhisheng.vui.assistant.util;

import java.io.IOException;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.util.Log;
import cn.yunzhisheng.common.util.LogUtil;

/**
 * Manages beeps and vibrations for {@link CaptureActivity}.
 */
public final class BeepPlayer {
	private static final String TAG = "BeepPlayer";

	private static final float DEFAULT_VOLUME = 1.0f;
	private final Context mContext;
	private MediaPlayer mMediaPlayer;
	private float mVolume;
	private OnCompletionListener mOnCompleteListener;

	public BeepPlayer(Context context) {
		mContext = context;
		mMediaPlayer = buildMediaPlayer(mContext);
	}

	public void setVolume(float volume) {
		LogUtil.d(TAG, "setVolume:volume " + volume);
		if (volume < 0.0f || volume > 1.0f) {
			LogUtil.w(TAG, "volume out of range[0,1]");
			return;
		}
		mVolume = volume;
	}

	public void release() {
		if (mMediaPlayer != null) {
			mMediaPlayer.reset();
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
	}

	public void playBeepSound(int rawId, boolean looping, OnCompletionListener completeCallback) {
		LogUtil
			.d(TAG, "playBeepSound:rawId " + rawId + ",lopping " + looping + ",completeCallback " + completeCallback);
		mOnCompleteListener = completeCallback;
		AssetFileDescriptor file = mContext.getResources().openRawResourceFd(rawId);

		if (mMediaPlayer == null) {
			throw new RuntimeException("MediaPlayer has been released.");
		}
		if (mMediaPlayer.isPlaying()) {
			mMediaPlayer.stop();
		}

		mMediaPlayer.reset();
		mMediaPlayer.setLooping(looping);

		try {
			mMediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
			mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mMediaPlayer.prepare();
			mMediaPlayer.start();
			LogUtil.d(TAG, "set volume:" + mVolume);
			mMediaPlayer.setVolume(mVolume, mVolume);
		} catch (IllegalStateException ioe) {
			LogUtil.printStackTrace(ioe);
		} catch (IllegalArgumentException e) {
			LogUtil.printStackTrace(e);
		} catch (IOException e) {
			LogUtil.printStackTrace(e);
		} finally {
			try {
				file.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void stop() {
		if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
			mMediaPlayer.stop();
		}
	}

	private MediaPlayer buildMediaPlayer(Context context) {
		MediaPlayer mediaPlayer = new MediaPlayer();
		mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				if (mOnCompleteListener != null) {
					mOnCompleteListener.onCompletion(null);
				}
			}
		});

		return mediaPlayer;
	}
}
