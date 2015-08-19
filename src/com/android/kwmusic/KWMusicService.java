/**
 * Copyright (c) 2012-2013 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * @FileName : KWMusicService.java
 * @ProjectName :
 * @PakageName : com.android.kwmusic
 * @Author : kevinliao
 * @CreateDate : 2014-12-2
 */
package com.android.kwmusic;

import java.util.List;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import cn.kuwo.autosdk.api.KWAPI;
import cn.kuwo.autosdk.api.OnPlayEndListener;
import cn.kuwo.autosdk.api.OnSearchListener;
import cn.kuwo.autosdk.api.PlayEndType;
import cn.kuwo.autosdk.api.PlayMode;
import cn.kuwo.autosdk.api.PlayState;
import cn.kuwo.autosdk.api.SearchStatus;
import cn.kuwo.autosdk.bean.Music;
import cn.yunzhisheng.common.util.LogUtil;
import cn.yunzhisheng.vui.assistant.media.TrackInfo;
import cn.yunzhisheng.vui.assistant.preference.CommandPreference;

public class KWMusicService extends Service implements OnSearchListener {
	private static final String TAG = "KWMusicService";
	private KWAPI mKwapi;
	private Context mContext;

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, "--onReceive:intent : " + intent);
			Bundle bundle = intent.getExtras();
			if (bundle != null) {
				for (String key : bundle.keySet()) {
					Log.d(TAG, "--key : " + key + ",value : " + bundle.get(key));
				}
			}
			String action = intent.getAction();
			String cmd = intent.getStringExtra(CommandPreference.CMDNAME);

			Log.d(TAG, "--cmd : " + cmd + ",action : " + action);
			try {
				if (CommandPreference.CMDTOGGLEPAUSE.equals(cmd)
						|| CommandPreference.TOGGLEPAUSE_ACTION.equals(action)) {
					setPlayState(KWPlayState.STATE_PAUSE);
				} else if (CommandPreference.CMDPAUSE.equals(cmd)
						|| CommandPreference.PAUSE_ACTION.equals(action)) {
					setPlayState(KWPlayState.STATE_PAUSE);
				} else if (CommandPreference.CMDPLAY.equals(cmd)) {
					setPlayState(KWPlayState.STATE_PLAY);
				} else if (CommandPreference.CMDPREVIOUS.equals(cmd)
						|| CommandPreference.PREVIOUS_ACTION.equals(action)) {
					setPlayState(KWPlayState.STATE_PRE);
				} else if (CommandPreference.CMDNEXT.equals(cmd)
						|| CommandPreference.NEXT_ACTION.equals(action)) {
					setPlayState(KWPlayState.STATE_NEXT);
				} else if (CommandPreference.CMDSTOP.equals(cmd)) {
					exitKwApp();
				} else if (CommandPreference.CMDOPEN_MUSIC.equals(cmd)) {
					// startKwApp();
					contiuneKwApp();
				} else if (CommandPreference.CMDFULL_CYCLE.equals(cmd)) {
					setPlayModel(KWPlayMode.MODE_ALL_CIRCLE);
				} else if (CommandPreference.CMDSINGLE_CYCLE.equals(cmd)) {
					setPlayModel(KWPlayMode.MODE_SINGLE_CIRCLE);
				} else if (CommandPreference.CMDORDER_PLAYBACK.equals(cmd)) {
					setPlayModel(KWPlayMode.MODE_ALL_ORDER);
				} else if (CommandPreference.CMDSHUFFLE_PLAYBACK.equals(cmd)) {
					setPlayModel(KWPlayMode.MODE_ALL_RANDOM);
				} else if (CommandPreference.CMDOPEN_DESK_LYRIC.equals(cmd)) {
					openDeskLyric();
				} else if (CommandPreference.CMDCLOSE_DESK_LYRIC.equals(cmd)) {
					closeDeskLyric();
				} else if (CommandPreference.ACTION_MUSIC_DATA.equals(action)) {
					if (bundle != null) {
						TrackInfo track = (TrackInfo) bundle
								.getParcelable("track");
						if (bundle != null && track != null) {
							playClientMusics(track.getTitle(),
									track.getArtist(), track.getAlbum());
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "--onCreate--");
		mKwapi = KWAPI.createKWAPI(this, "auto");
		mContext = this;
		registReceiver();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "--onStartCommand--");
		return START_NOT_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.d(TAG, "--onBind--");
		return null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "--onDestroy--");
		unregistReceiver();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void searchFinshed(SearchStatus arg0, boolean arg1, List arg2,
			boolean arg3) {
		Log.d(TAG, "--searchFinshed--");
		List<Music> musics = (List<Music>) arg2;
		if (arg0 == SearchStatus.SUCCESS) {
			// 搜索成功，可以将歌曲列表展现出来选择性的播放
			if (musics.size() > 0) {
				mKwapi.playMusic(this, musics.get(0));
			}
		} else {
			Toast.makeText(this, "在线歌曲搜索失败！", Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * 添加action，注册广播
	 */
	private void registReceiver() {
		IntentFilter commandFilter = new IntentFilter();
		commandFilter.addAction(CommandPreference.SERVICECMD);
		commandFilter.addAction(CommandPreference.TOGGLEPAUSE_ACTION);
		commandFilter.addAction(CommandPreference.PAUSE_ACTION);
		commandFilter.addAction(CommandPreference.NEXT_ACTION);
		commandFilter.addAction(CommandPreference.PREVIOUS_ACTION);
		commandFilter.addAction(CommandPreference.ACTION_MUSIC_DATA);
		mContext.registerReceiver(mReceiver, commandFilter);
	}

	/**
	 * 取消广播
	 */
	private void unregistReceiver() {
		try {
			mContext.unregisterReceiver(mReceiver);
		} catch (Exception e) {
			LogUtil.printStackTrace(e);
		}
	}

	/*------------------kw接口封装----------------*/
	/**
	 * 在线搜索播放第一条
	 */
	private void searchOnlineMusic(String key) {
		Log.d(TAG, "--searchOnlineMusic-->key : " + key);
		if (mKwapi == null)
			reCreateKWAPI();
		mKwapi.searchOnlineMusic(key, this);
	}

	/**
	 * 启动kw，非自动播放
	 */
	private void startKwApp() {
		Log.d(TAG, "--startKwApp--");
		if (mKwapi == null)
			reCreateKWAPI();
		mKwapi.startAPP(mContext, true);// startApp()是启动程序并自动播放上次播放过的歌曲，你这种情况需要调用playClientMusics这个接口，歌曲的参数信息传空即可。
	}

	/**
	 * 启动kw，并自动播放
	 */
	private void contiuneKwApp() {
		Log.d(TAG, "--contiuneKwApp--");
		if (mKwapi == null)
			reCreateKWAPI();
		mKwapi.playClientMusics(mContext, null, null, null);
	}

	/**
	 * 关闭kw
	 */
	private void exitKwApp() {
		Log.d(TAG, "--exitKwApp--");
		if (mKwapi == null)
			reCreateKWAPI();
		mKwapi.exitAPP(mContext);
	}

	/**
	 * 打开桌面歌词
	 */
	private void openDeskLyric() {

		Log.d(TAG, "打开桌面歌词");
		if (mKwapi == null)
			reCreateKWAPI();
		mKwapi.openDeskLyric(mContext);

	}

	/**
	 * 关闭桌面歌词
	 */
	private void closeDeskLyric() {
		Log.d(TAG, "关闭桌面歌词");
		if (mKwapi == null)
			reCreateKWAPI();
		mKwapi.closeDeskLyric(mContext);
	}

	/**
	 * 设置播放模式
	 * 
	 * @param mode
	 */
	private void setPlayModel(KWPlayMode mode) {
		Log.d(TAG, "--setPlayModel--");
		if (mKwapi == null)
			reCreateKWAPI();
		switch (mode) {
		case MODE_ALL_CIRCLE:
			// 循环播放
			mKwapi.setPlayMode(this, PlayMode.MODE_ALL_CIRCLE);
			break;
		case MODE_SINGLE_CIRCLE:
			// 单曲循环
			mKwapi.setPlayMode(this, PlayMode.MODE_SINGLE_CIRCLE);
			break;
		case MODE_ALL_ORDER:
			// 顺序播放
			mKwapi.setPlayMode(this, PlayMode.MODE_ALL_ORDER);
			break;
		case MODE_ALL_RANDOM:
			// 随机播放
			mKwapi.setPlayMode(this, PlayMode.MODE_ALL_RANDOM);
		default:
			break;
		}
	}

	/**
	 * 设置模式
	 * 
	 * @param model
	 *            模式
	 */
	private void setPlayState(KWPlayState state) {
		Log.d(TAG, "--setPlayState--");
		if (mKwapi == null)
			reCreateKWAPI();
		switch (state) {
		case STATE_PLAY:// 播放
			mKwapi.setPlayState(mContext, PlayState.STATE_PLAY);
			break;
		case STATE_PAUSE:// 暂停
			mKwapi.setPlayState(mContext, PlayState.STATE_PAUSE);
			break;
		case STATE_PRE:// 上一个
			mKwapi.setPlayState(mContext, PlayState.STATE_PRE);
			break;
		case STATE_NEXT:// 下一个
			mKwapi.setPlayState(mContext, PlayState.STATE_NEXT);
			break;
		default:
			break;
		}
	};

	/**
	 * 播放用户搜索的歌曲
	 * 
	 * @param name
	 *            歌名
	 * @param singer
	 *            歌手
	 * @param album
	 *            专辑
	 */
	private void playClientMusics(String name, String singer, String album) {
		Log.d(TAG, "--playClientMusics--");
		if (mKwapi == null)
			reCreateKWAPI();
		mKwapi.playClientMusics(mContext, name, singer, album);
	}

	/**
	 * 根据路径打开本地音乐
	 * 
	 * @param path
	 */
	private void playLocalMusic(String path) {
		Log.d(TAG, "--playLocalMusic--");
		if (mKwapi == null)
			reCreateKWAPI();
		mKwapi.playLocalMusic(mContext, path);
	}

	/**
	 * 重新创建对象
	 * 
	 * @return
	 */
	private void reCreateKWAPI() {
		mKwapi = KWAPI.createKWAPI(this, "auto");
	}

	/**
	 * 播放模式
	 * 
	 * @author kevin
	 * 
	 */
	private enum KWPlayMode {
		MODE_ALL_CIRCLE, MODE_SINGLE_CIRCLE, MODE_ALL_ORDER, MODE_ALL_RANDOM;
	}

	/**
	 * 播放状态
	 * 
	 * @author kevin
	 * 
	 */
	private enum KWPlayState {
		STATE_PLAY, STATE_PAUSE, STATE_PRE, STATE_NEXT;
	}

}
