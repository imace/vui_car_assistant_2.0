package cn.yunzhisheng.vui.assistant.media;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Handler;
import android.os.Message;
import cn.yunzhisheng.common.util.LogUtil;
import cn.yunzhisheng.vui.assistant.media.PlayList.PlaylistPlaybackMode;

public class PlayerEngine {
	private static final String TAG = "PlayerEngine";
	public static final int MUSIC_ERROR_URL_EMPTY = -2;

	private static final long FAIL_TIME_FRAME = 1000;
	private static final int ACCEPTABLE_FAIL_NUMBER = 2;
	private static final int MSG_UPDATE_PROGRESS = 10000;
	private static final int MSG_UPDATE_BUFFER_PROGRESS = 10001;

	private long mLastFailTime;
	private long mTimesFailed;

	private InternalMediaPlayer mCurrentMediaPlayer;
	private PlayList mPlaylist;

	private List<PlayerEngineListener> mListeners = new ArrayList<PlayerEngineListener>();
	private ReentrantReadWriteLock mListenerLock = new ReentrantReadWriteLock();
	private Lock mListenerReadLock = mListenerLock.readLock();
	private Lock mListenerWriteLock = mListenerLock.writeLock();

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			LogUtil.d(TAG, "handleMessage:" + msg);
			super.handleMessage(msg);
			switch (msg.what) {
			case MSG_UPDATE_PROGRESS:
				try {
					if (mCurrentMediaPlayer != null && mCurrentMediaPlayer.isPlaying()) {
						int seconds = mCurrentMediaPlayer.getCurrentPosition() / 1000;
						int duration = mCurrentMediaPlayer.getDuration() / 1000;
						mListenerReadLock.lock();
						for (PlayerEngineListener listener : mListeners) {
							listener.onTrackProgress(seconds, duration);
						}
						sendProgressMessage(1000);
						mListenerReadLock.unlock();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case MSG_UPDATE_BUFFER_PROGRESS:
				try {
					mListenerReadLock.lock();
					for (PlayerEngineListener listener : mListeners) {
						listener.onTrackBuffering(msg.arg1);
					}
					mListenerReadLock.unlock();
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			}
		}
	};

	public PlayerEngine() {
		mLastFailTime = 0;
		mTimesFailed = 0;
	}

	public void release() {
		LogUtil.d(TAG, "release");
		cleanUp();
		try {
			mListenerWriteLock.lock();
			mListeners.clear();
			mListenerWriteLock.unlock();
		} catch (Exception e) {
			e.printStackTrace();
		}
		mListenerReadLock = null;
		mListenerWriteLock = null;
		mListenerLock = null;
	}

	public void registerListener(PlayerEngineListener remoteEngineListener) {
		try {
			mListenerWriteLock.lock();
			if (remoteEngineListener != null && !mListeners.contains(remoteEngineListener)) {
				mListeners.add(remoteEngineListener);
			}
			mListenerWriteLock.unlock();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void unregisterListener(PlayerEngineListener remoteEngineListener) {
		try {
			mListenerWriteLock.lock();
			mListeners.remove(remoteEngineListener);
			mListenerWriteLock.unlock();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void openPlaylist(PlayList playlist) {
		LogUtil.d(TAG, "openPlaylist: playlist " + playlist);
		if (!playlist.isEmpty()) {
			if (mPlaylist != null) {
				mPlaylist.removeAll();
			}
			mPlaylist = playlist;
		} else {
			mPlaylist = null;
		}
	}

	public void pause() {
		LogUtil.d(TAG, "pause");
		if (mCurrentMediaPlayer == null) {
			return;
		}

		if (mCurrentMediaPlayer.preparing) {
			mCurrentMediaPlayer.playAfterPrepare = false;
			return;
		}

		if (mCurrentMediaPlayer.isPlaying()) {
			mCurrentMediaPlayer.pause();

			try {
				mListenerReadLock.lock();
				for (PlayerEngineListener listener : mListeners) {
					listener.onTrackPause();
				}
				mListenerReadLock.unlock();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// public void resume() {
	// if (mCurrentMediaPlayer == null) {
	// return;
	// }
	// if (!mCurrentMediaPlayer.isPlaying()) {
	// mCurrentMediaPlayer.start();
	// sendProgressMessage(200);
	// try {
	// mListenerReadLock.lock();
	// for (PlayerEngineListener listener : mListeners) {
	// listener.onTrackStart();
	// }
	// mListenerReadLock.unlock();
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	// }

	public void play() {
		LogUtil.d(TAG, "play");
		startSteaming();
		try {
			mListenerReadLock.lock();
			for (PlayerEngineListener listener : mListeners) {
				listener.onTrackStart();
			}
			mListenerReadLock.unlock();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void startSteaming() {
		LogUtil.d(TAG, "startSteaming");
		if (mCurrentMediaPlayer != null && mPlaylist != null && mPlaylist.size() > 0) {
			try {
				mListenerReadLock.lock();
				for (PlayerEngineListener listener : mListeners) {
					listener.onTrackChanged(mPlaylist.getSelectedIndex(), mPlaylist.getSelectedTrack());
				}
				mListenerReadLock.unlock();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		if (mCurrentMediaPlayer == null) {
			if (mPlaylist != null && mPlaylist.size() > 0) {
				buildMediaPlayer(mPlaylist.getSelectedTrack());
			} else {
				LogUtil.d(TAG, "mPlayList empty!");
			}
		} else {
			if (mPlaylist != null && mPlaylist.size() > 0) {
				if (mCurrentMediaPlayer.preparing) {
					mCurrentMediaPlayer.playAfterPrepare = true;
					return;
				}

				if (mCurrentMediaPlayer.track != mPlaylist.getSelectedTrack()) {
					releaseCurrentMediaPlayer();
					buildMediaPlayer(mPlaylist.getSelectedTrack());
				} else {
					mCurrentMediaPlayer.start();
					sendProgressMessage(200);
				}
			}

		}
	}

	private void releaseCurrentMediaPlayer() {
		LogUtil.d(TAG, "releaseCurrentMediaPlayer");
		if (mCurrentMediaPlayer != null) {
			try {
				mCurrentMediaPlayer.stop();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} finally {
				mCurrentMediaPlayer.release();
			}
			mCurrentMediaPlayer = null;
		}
	}

	public void prev() {
		if (mPlaylist != null) {
			mPlaylist.selectPrev();
			play();
		}
	}

	public void next() {
		if (mPlaylist != null) {
			mPlaylist.selectNext();
			play();
		}
	}

	public void skipTo(int index) {
		mPlaylist.select(index);
		play();
	}

	public void stop() {
		LogUtil.d(TAG, "stop");
		if (mCurrentMediaPlayer != null && mCurrentMediaPlayer.isPlaying()) {
			mCurrentMediaPlayer.stop();
		}
		try {
			mListenerReadLock.lock();
			for (PlayerEngineListener listener : mListeners) {
				listener.onTrackStop();
			}
			mListenerReadLock.unlock();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void cleanUp() {
		LogUtil.d(TAG, "cleanUp");
		releaseCurrentMediaPlayer();
		if (mPlaylist != null) {
			mPlaylist.removeAll();
		}
	}

	private void buildMediaPlayer(TrackInfo track) {
		LogUtil.d(TAG, "buildMediaPlayer: track " + track);
		if (track == null) {
			LogUtil.w(TAG, "track null,return!");
			return;
		}
		mCurrentMediaPlayer = new InternalMediaPlayer();
		String streamUrl = track.getUrl();

		try {
			mCurrentMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mCurrentMediaPlayer.setDataSource(streamUrl);
			mCurrentMediaPlayer.track = track;

			mCurrentMediaPlayer.setOnCompletionListener(new OnCompletionListener() {

				public void onCompletion(MediaPlayer mp) {
					if (!mPlaylist.isLastTrackOnList()
						|| mPlaylist.getPlaylistPlaybackMode() == PlaylistPlaybackMode.REPEAT
						|| mPlaylist.getPlaylistPlaybackMode() == PlaylistPlaybackMode.SHUFFLE_AND_REPEAT) {
						next();
					} else {
						stop();
					}
				}
			});
			mCurrentMediaPlayer.setOnPreparedListener(new OnPreparedListener() {

				public void onPrepared(MediaPlayer mp) {
					LogUtil.d(TAG, "onPrepared");
					mCurrentMediaPlayer.preparing = false;

					if (mCurrentMediaPlayer.playAfterPrepare) {
						mCurrentMediaPlayer.playAfterPrepare = false;

						if (mCurrentMediaPlayer.track != mPlaylist.getSelectedTrack()) {
							mCurrentMediaPlayer.stop();
							mCurrentMediaPlayer.release();
							mCurrentMediaPlayer = null;

							play();
						} else {
							mCurrentMediaPlayer.start();
							sendProgressMessage(200);
						}
					}
				}
			});

			mCurrentMediaPlayer.setOnBufferingUpdateListener(new OnBufferingUpdateListener() {

				public void onBufferingUpdate(MediaPlayer mp, int percent) {
					sendBufferMessage(percent);
				}
			});

			mCurrentMediaPlayer.setOnErrorListener(new OnErrorListener() {

				public boolean onError(MediaPlayer mp, int what, int extra) {
					LogUtil.e(TAG, "onError: what " + what + ", extra " + extra);
					if (what == MediaPlayer.MEDIA_ERROR_UNKNOWN) {
						try {
							mListenerReadLock.lock();
							for (PlayerEngineListener listener : mListeners) {
								listener.onTrackStreamError(what);
							}
							mListenerReadLock.unlock();
						} catch (Exception e) {
							e.printStackTrace();
						}
						releaseCurrentMediaPlayer();
						stop();
						return true;
					}

					if (what == -1) {
						long failTime = System.currentTimeMillis();
						if (failTime - mLastFailTime > FAIL_TIME_FRAME) {
							mTimesFailed = 1;
							mLastFailTime = failTime;
						} else {
							mTimesFailed++;
							if (mTimesFailed > ACCEPTABLE_FAIL_NUMBER) {
								try {
									mListenerReadLock.lock();
									for (PlayerEngineListener listener : mListeners) {
										listener.onTrackStreamError(what);
									}
									mListenerReadLock.unlock();
								} catch (Exception e) {
									e.printStackTrace();
								}
								stop();
								return true;
							}
						}
					}
					return false;
				}
			});

			mCurrentMediaPlayer.preparing = true;
			mCurrentMediaPlayer.playAfterPrepare = true;
			mCurrentMediaPlayer.prepareAsync();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public PlayList getPlaylist() {
		return mPlaylist;
	}

	public void setVolume(float vol) {
		LogUtil.d(TAG, "setVolume:vol " + vol);
		if (mCurrentMediaPlayer == null) {
			LogUtil.e(TAG, "mCurrentMediaPlayer null!");
			return;
		}
		mCurrentMediaPlayer.setVolume(vol, vol);
	}

	public boolean isPlaying() {
		if (mCurrentMediaPlayer == null) {
			return false;
		}

		if (mCurrentMediaPlayer.preparing) {
			return false;
		}

		return mCurrentMediaPlayer.isPlaying();
	}

	public void setPlaybackMode(PlaylistPlaybackMode aMode) {
		mPlaylist.setPlaylistPlaybackMode(aMode);
	}

	public PlaylistPlaybackMode getPlaybackMode() {
		return mPlaylist.getPlaylistPlaybackMode();
	}

	public void forward(int time) {
		mCurrentMediaPlayer.seekTo(mCurrentMediaPlayer.getCurrentPosition() + time);
	}

	public void rewind(int time) {
		mCurrentMediaPlayer.seekTo(mCurrentMediaPlayer.getCurrentPosition() - time);
	}

	public void sendBufferMessage(int percent) {
		try {
			mListenerReadLock.lock();
			if (!mListeners.isEmpty()) {
				Message msg = mHandler.obtainMessage(MSG_UPDATE_BUFFER_PROGRESS);
				msg.arg1 = percent;
				mHandler.sendMessage(msg);
			}
			mListenerReadLock.unlock();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendProgressMessage(long delay) {
		LogUtil.d(TAG, "sendProgressMessage:delay " + delay);
		try {
			mListenerReadLock.lock();
			if (!mListeners.isEmpty()) {
				mHandler.removeMessages(MSG_UPDATE_BUFFER_PROGRESS);
				mHandler.sendEmptyMessageDelayed(MSG_UPDATE_PROGRESS, delay);
			}
			mListenerReadLock.unlock();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public TrackInfo getCurrentPlayTrack() {
		if (mCurrentMediaPlayer != null) {
			return mCurrentMediaPlayer.track;
		}
		return null;
	}

	private class InternalMediaPlayer extends MediaPlayer {
		public cn.yunzhisheng.vui.assistant.media.TrackInfo track;
		public boolean preparing = false;
		public boolean playAfterPrepare = true;
	}
}
