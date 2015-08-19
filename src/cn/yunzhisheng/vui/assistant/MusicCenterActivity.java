/**
 * Copyright (c) 2012-2014 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * @FileName : MusicCenterActivity.java
 * @ProjectName : CarPlay
 * @PakageName : cn.yunzhisheng.vui.assistant
 * @Author : Brant
 * @CreateDate : 2014-9-16
 */
package cn.yunzhisheng.vui.assistant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import cn.yunzhisheng.common.util.LogUtil;
import com.ilincar.voice.R;
import cn.yunzhisheng.vui.assistant.media.IMusicListener;
import cn.yunzhisheng.vui.assistant.media.MusicPlaybackService;
import cn.yunzhisheng.vui.assistant.media.MusicServicePresentor;
import cn.yunzhisheng.vui.assistant.media.PlayerEngine;
import cn.yunzhisheng.vui.assistant.media.TrackInfo;
import cn.yunzhisheng.vui.assistant.session.MusicShowSession;

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
public class MusicCenterActivity extends Activity implements IMusicListener, OnClickListener, OnItemClickListener {
	public static final String TAG = "MusicCenterActivity";
	public static final String EXTRA_DATA = "data";

	private static final int DIALOG_EXIT = 0;
	private TextView mTextViewSong, mTextViewArtist, mTextViewProgress;
	private ProgressBar mProgressBarMusic;
	private Button mBtnPrev, mBtnPlayPause, mBtnNext;
	private ListView mListMusic;
	private MusicListAdapter mAdapter;
	private ArrayList<HashMap<String, Object>> mData;
	private String mLabelProgress;
	private MusicServicePresentor mMusicServicePresentor;
	private boolean mServiceConnected;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		LogUtil.d(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_music_center);
		findViews();
		setListener();

		mData = new ArrayList<HashMap<String, Object>>();
		mAdapter = new MusicListAdapter(this, mData);
		mListMusic.setAdapter(mAdapter);
		mMusicServicePresentor = new MusicServicePresentor(this, this);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
	}

	@Override
	public void onTrackChanged(int index, TrackInfo track) {
		LogUtil.d(TAG, "onTrackChanged:index " + index + ",track " + track);
		onUpdateTrackChanged(index, track);
		onMusicBuff();
	}

	@Override
	public void onTrackStart() {
		LogUtil.d(TAG, "onTrackStart");
		onMusicPlayed();
	}

	@Override
	public void onTrackProgress(int seconds, int duration) {
		// LogUtil.d(TAG, "onTrackProgress:seconds " + seconds + ",duration " +
		// duration);
		onUpdateTrackProgress(seconds, duration);
	}

	@Override
	public void onTrackBuffering(int percent) {
		onUpdateTrackBuffering(percent);
	}

	@Override
	public void onTrackStop() {
		LogUtil.d(TAG, "onTrackStop");
		mLabelProgress = null;
		
		finish();
	}

	@Override
	public void onTrackPause() {
		LogUtil.d(TAG, "onTrackPause");
		onMusicPaused();
	}

	@Override
	public void onTrackStreamError(int what) {
		LogUtil.e(TAG, "onTrackStreamError:what " + what);
		onUpdateTrackStreamError(what);
	}

	@Override
	public void onServiceConnected() {
		LogUtil.d(TAG, "onServiceConnected");
		mServiceConnected = true;
		play();
	}

	@Override
	public void onServiceDisconnected() {
		LogUtil.d(TAG, "onServiceDisconnected");
		mServiceConnected = false;
	}

	public void onUpdateTrackProgress(int seconds, int duration) {
		mLabelProgress = getFromatTimeLength(seconds) + "/" + getFromatTimeLength(duration);
		mTextViewProgress.setText(mLabelProgress);
		mProgressBarMusic.setMax(100);
		int p = (int) (100 * ((float) seconds / duration));
		mProgressBarMusic.setProgress(p);
	}

	public void onUpdateTrackChanged(int index, TrackInfo track) {
		mLabelProgress = null;
		if (track != null) {
			mAdapter.setCurrentItem(index);
			mTextViewSong.setText(track.getTitle());
			mTextViewArtist.setText(track.getArtist());
			refreshMusicProgress();
		}
	}

	private void refreshMusicProgress() {
		mProgressBarMusic.setProgress(0);
		mProgressBarMusic.setSecondaryProgress(0);
		mTextViewProgress.setText("--:--/--:--");
	}

	public void onUpdateTrackBuffering(int percent) {
		mProgressBarMusic.setMax(100);
		mProgressBarMusic.setSecondaryProgress(percent);
	}

	public void onUpdateTrackStreamError(int what) {
		onMusicPaused();
		Log.e(TAG, "music error what=" + what);
		if (what == PlayerEngine.MUSIC_ERROR_URL_EMPTY) {
			Toast.makeText(this, R.string.music_url_empty, Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(this, R.string.music_play_error, Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onBinderError(int errorCode) {
		LogUtil.e(TAG, "onBinderError:errorCode " + errorCode);
		Toast.makeText(this, getString(R.string.music_service_binder_error), Toast.LENGTH_SHORT).show();
	}

	private void findViews() {
		mTextViewSong = (TextView) findViewById(R.id.textViewSong);
		mTextViewArtist = (TextView) findViewById(R.id.textViewArtist);
		mTextViewProgress = (TextView) findViewById(R.id.textViewMusicProgress);
		mProgressBarMusic = (ProgressBar) findViewById(R.id.progressBarMusic);

		mBtnPrev = (Button) findViewById(R.id.btnMusicPrev);
		mBtnPlayPause = (Button) findViewById(R.id.btnMusicPlayPause);
		mBtnNext = (Button) findViewById(R.id.btnMusicNext);
		mListMusic = (ListView) findViewById(android.R.id.list);
	}

	private void setListener() {
		mListMusic.setOnItemClickListener(this);

		mBtnPrev.setOnClickListener(this);
		mBtnNext.setOnClickListener(this);
		mBtnPlayPause.setOnClickListener(this);
	}

	public void updateMusicList(List<TrackInfo> trackList) {
		mData.clear();
		if (trackList == null || trackList.size() == 0) {
			mData.clear();
		} else {
			for (int i = 0; i < trackList.size(); i++) {
				TrackInfo track = trackList.get(i);
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("Song", (i + 1) + "." + track.getTitle());
				map.put("Artist", track.getArtist());
				mData.add(map);
			}

			TrackInfo track = trackList.get(0);
			if (mAdapter != null) {
				mAdapter.setCurrentItem(0);
			}
			if (mTextViewSong != null) {
				mTextViewSong.setText(track.getTitle());
			}
			if (mTextViewArtist != null) {
				mTextViewArtist.setText(track.getArtist());
			}
		}
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnMusicPrev:
			mMusicServicePresentor.prev();
			break;
		case R.id.btnMusicPlayPause:
			if (mMusicServicePresentor.isPlaying()) {
				mMusicServicePresentor.pause();
			} else {
				mMusicServicePresentor.play();
			}
			break;
		case R.id.btnMusicNext:
			mMusicServicePresentor.next();
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		mMusicServicePresentor.skipTo(position);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// int volume =
		// mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		// int maxVol =
		// mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

		switch (keyCode) {
		// case KeyEvent.KEYCODE_VOLUME_DOWN:
		// int volDown = volume-- > 0 ? volume : 0;
		// mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volDown, 1);
		// return true;
		// case KeyEvent.KEYCODE_VOLUME_UP:
		// int volUp = volume++ < maxVol ? volume : maxVol;
		// mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volUp, 1);
		// return true;
		case KeyEvent.KEYCODE_BACK:
			showDialog(DIALOG_EXIT);
			return true;
		}

		return super.onKeyUp(keyCode, event);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_EXIT:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.prompt);
			builder.setMessage(R.string.exit_music);
			builder.setPositiveButton(android.R.string.ok, new Dialog.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int arg1) {
					finish();
					dialog.dismiss();
				}
			});
			builder.setNegativeButton(android.R.string.cancel, null);
			return builder.create();
		}
		return super.onCreateDialog(id);
	}

	private String getFromatTimeLength(int timeInSeconds) {
		int minutes = timeInSeconds / 60;
		int seconds = timeInSeconds % 60;
		return String.format("%02d", minutes) + ":" + String.format("%02d", seconds);
	}

	public void onMusicBuff() {
		mBtnPlayPause.setBackgroundResource(R.drawable.btn_music_pause_bg);
		mTextViewProgress.setText(R.string.music_buffer);
	}

	public void onMusicPlayed() {
		mBtnPlayPause.setBackgroundResource(R.drawable.btn_music_pause_bg);
	}

	public void onMusicPaused() {
		mTextViewProgress.setText(mLabelProgress);
		mBtnPlayPause.setBackgroundResource(R.drawable.btn_music_play_bg);
	}

	private static class MusicListAdapter extends SimpleAdapter {

		private int mColorFocus, mColorNormal;
		private int mCurrentItem = -1;

		public MusicListAdapter(Context context, List<? extends Map<String, ?>> data) {
			super(context, data, R.layout.list_item_music, new String[] { "Song", "Artist" }, new int[] {
				R.id.textViewSong, R.id.textViewArtist });
			Resources res = context.getResources();
			mColorFocus = res.getColor(R.color.state_selected);
			mColorNormal = res.getColor(R.color.light_grey);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = super.getView(position, convertView, parent);
			TextView textViewSong = (TextView) view.findViewById(R.id.textViewSong);
			TextView textViewArtist = (TextView) view.findViewById(R.id.textViewArtist);
			if (position == mCurrentItem) {
				textViewSong.setTextColor(mColorFocus);
				textViewArtist.setTextColor(mColorFocus);
			} else {
				textViewSong.setTextColor(mColorNormal);
				textViewArtist.setTextColor(mColorNormal);
			}
			return view;
		}

		public void setCurrentItem(int position) {
			if (mCurrentItem != position) {
				mCurrentItem = position;
				notifyDataSetChanged();
			}
		}

	}

	@Override
	protected void onResume() {
		LogUtil.d(TAG, "onResume");
		super.onResume();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		if (mServiceConnected) {
			play();
		}
	}

	private void play() {
		LogUtil.d(TAG, "play");
		/**@purpose: fix Intent overload data when play music by Leon***/
//		ArrayList<TrackInfo> trackList = getIntent().getParcelableArrayListExtra(EXTRA_DATA);
		ArrayList<TrackInfo> trackList = MusicShowSession.getMusicList();
		updateMusicList(trackList);
		mMusicServicePresentor.setPlayList(trackList);
		mMusicServicePresentor.play();
	}

	@Override
	protected void onDestroy() {
		LogUtil.d(TAG, "onDestroy");
		super.onDestroy();
		mMusicServicePresentor.setListener(null);
		mMusicServicePresentor.exit();
		mMusicServicePresentor = null;
	}
}
