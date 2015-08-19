/**
 * Copyright (c) 2012-2013 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * @FileName : RomSystemSetting.java
 * @ProjectName : vui_assistant
 * @PakageName : cn.yunzhisheng.vui.assistant.oem
 * @Author : Dancindream
 * @CreateDate : 2013-9-9
 */
package cn.yunzhisheng.vui.assistant.oem;

import android.app.WallpaperManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.provider.Settings;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Dancindream
 * @CreateDate : 2013-9-9
 * @ModifiedBy : Dancindream
 * @ModifiedDate: 2013-9-9
 * @Modified:
 * 2013-9-9: 实现基本功能
 */
public class RomSystemSetting {
	public static final String TAG = "RomSystemSetting";

	private static void startActivityFromService(Context context, Intent intent) {
		try {
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void openDisplaySettings(Context context) {
		startActivityFromService(context, new Intent(Settings.ACTION_DISPLAY_SETTINGS));
	}

	public static void openTimeSettings(Context context) {
		startActivityFromService(context, new Intent(Settings.ACTION_DATE_SETTINGS));
	}

	public static void openSoundSettings(Context context) {
		startActivityFromService(context, new Intent(Settings.ACTION_SOUND_SETTINGS));
	}

	public static void openWallPaperSettings(Context context) {
		startActivityFromService(context, new Intent(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER));
	}

	public static void setBluetoothEnabled(boolean enabled) {
		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		if (adapter != null) {
			if (adapter.isEnabled() != enabled) {
				if (enabled) {
					adapter.enable();
				} else {
					adapter.disable();
				}
			}
		}
	}

	public static void setFlightModeEnabled(Context context, boolean enabled) {
		Settings.System.putInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, enabled ? 1 : 0);
		Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
		intent.putExtra("state", enabled);
		context.sendBroadcast(intent);
	}

	public static void setAutoOrientationEnabled(Context context, boolean enabled) {
		Settings.System.putInt(context.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, enabled ? 1 : 0);
	}

	public static void setRingerMode(Context context, boolean silent, boolean vibrate) {
		AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		if (silent) {
			mAudioManager.setRingerMode(vibrate ? AudioManager.RINGER_MODE_VIBRATE : AudioManager.RINGER_MODE_SILENT);
		} else {
			mAudioManager.setRingerMode(vibrate ? AudioManager.RINGER_MODE_VIBRATE : AudioManager.RINGER_MODE_NORMAL);
			mAudioManager.setVibrateSetting(
				AudioManager.VIBRATE_TYPE_RINGER,
				vibrate ? AudioManager.VIBRATE_SETTING_ON : AudioManager.VIBRATE_SETTING_OFF);
		}
	}

	public static void openUrl(Context context, String url) {
		try {
			Intent intent = new Intent();
			intent.setAction("android.intent.action.VIEW");
			Uri contentUri = Uri.parse(url);
			intent.setData(contentUri);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public static void openCallLog(Context context) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(android.provider.CallLog.Calls.CONTENT_URI);
		// intent.putExtra("android.provider.extra.CALL_TYPE_FILTER",
		// CallLog.Calls.MISSED_TYPE);
		startActivityFromService(context, intent);
	}

	// 音量加减----vain
	public static int RaiseOrLowerVolume(Context context, boolean isAdd, int volumeValue) {
		AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		if (isAdd) {
			for (int i = 0; i < volumeValue; i++) {//FLAG_REMOVE_SOUND_AND_VIBRATE
				am.adjustStreamVolume(
					AudioManager.STREAM_MUSIC,
					AudioManager.ADJUST_RAISE,
					AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);//FLAG_SHOW_UI    FX_FOCUS_NAVIGATION_UP
			}
		} else {
			for (int i = 0; i < volumeValue; i++) {
				am.adjustStreamVolume(
					AudioManager.STREAM_MUSIC,
					AudioManager.ADJUST_LOWER,
					AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
			}
		}
		// 返回当前媒体音量
		return am.getStreamVolume(AudioManager.STREAM_MUSIC);
	}

	// 最大音量--vain
	public static int setMaxVolume(Context context) {
		AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

		am.setStreamVolume(AudioManager.STREAM_MUSIC, am.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
		// 返回当前媒体音量
		return am.getStreamVolume(AudioManager.STREAM_MUSIC);
	}

	// 最小音量--vain
	public static int setMinVolume(Context context) {
		AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		am.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
		// 返回当前媒体音量
		return am.getStreamVolume(AudioManager.STREAM_MUSIC);
	}

	// 设置到某个音量值--vain
	public static int setVolume(Context context, int volumeValue) {
		AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		am.setStreamVolume(AudioManager.STREAM_MUSIC, volumeValue, 0);
		// 返回当前媒体音量
		return am.getStreamVolume(AudioManager.STREAM_MUSIC);
	}
}
