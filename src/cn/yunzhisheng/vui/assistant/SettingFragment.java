package cn.yunzhisheng.vui.assistant;

import android.R.integer;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.yunzhisheng.common.util.LogUtil;
import cn.yunzhisheng.preference.PrivatePreference;
import com.ilincar.voice.R;
import cn.yunzhisheng.vui.assistant.preference.UserPreference;
import cn.yunzhisheng.vui.assistant.slidingmenu.lib.app.SlidingFragmentActivity;
import cn.yunzhisheng.vui.assistant.talk.TalkService;
import cn.yunzhisheng.vui.assistant.widget.CustomDialogMap;

@SuppressLint("ValidFragment")
public class SettingFragment extends Fragment implements OnClickListener {

	public static final String TAG = "SettingFragment";

	public static final String ACTION_SHOW_HELP = "cn.yunzhisheng.voice.mobile.SHOW_HELP_PAGE";

	// public static final String ACTION_UPDATE_CITY =
	// "cn.yunzhisheng.voice.mobile.UPDATE_CITY";
	public static final String ACTION_SHOW_TV_SETTING = "cn.yunzhisheng.voice.mobile.SHOW_TV_SETTING";
	public static final String ACTION_HIDE_TV_SETTING = "cn.yunzhisheng.voice.mobile.HIDE_TV_SETTING";
	public static final String TTS_VOICE_SPEED = "TTS_VOICE_SPEED";

	private View mView;
	private Context mContext;
	private ImageView mWakeUp;
	private ImageView mShowFloat;
	private ImageView amap_select, baidu_select, slow_select, default_select, fast_select;
	private AlertDialog dlg;
	private ImageView mSettingReturn;
	private UserPreference mUserPreference;
	private RelativeLayout mSettingWakeup, mSettingHelp;
	private RelativeLayout amapPannel, baiduPannel, slowPannel, defaultPannel, fastPannel;
	private TextView mTextViewWakeupLbl, mTextViewFloatViewLbl;

	private RelativeLayout mSettingMap, mTtsSpeed;
	private TextView mTextViewMap, mTextViewTtsSpeed;
	
	private SettingFragmentListener mSettingFragmentListener = null;

	private OnClickListener monclClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(TalkService.TTS_EVENT_ON_VOCIE_SPEED);

			switch (v.getId()) {
			case R.id.map_setting_cancel:
				dlg.dismiss();
				break;
			case R.id.amapPannel:
				amap_select.setVisibility(View.VISIBLE);
				baidu_select.setVisibility(View.INVISIBLE);
				mUserPreference.putString(UserPreference.KEY_MAP, UserPreference.MAP_VALUE_AMAP);
				updateMap();
				break;
			case R.id.baiduPannel:
				amap_select.setVisibility(View.INVISIBLE);
				baidu_select.setVisibility(View.VISIBLE);
				mUserPreference.putString(UserPreference.KEY_MAP, UserPreference.MAP_VALUE_BAIDU);
				updateMap();
				break;
			case R.id.slowPannel:
				slow_select.setVisibility(View.VISIBLE);
				default_select.setVisibility(View.INVISIBLE);
				fast_select.setVisibility(View.INVISIBLE);
				intent.putExtra(TTS_VOICE_SPEED,
						UserPreference.SLOW_VOICE_SPEED);
				mContext.sendBroadcast(intent);
				mUserPreference.putString(UserPreference.KEY_TTS_SPEED,
						UserPreference.TTS_SPEED_SLOW);
				updateTtsSpeed();
				break;
			case R.id.defaultPannel:
				slow_select.setVisibility(View.INVISIBLE);
				default_select.setVisibility(View.VISIBLE);
				fast_select.setVisibility(View.INVISIBLE);
				intent.putExtra(TTS_VOICE_SPEED,
						UserPreference.DEFAULT_VOICE_SPEED);
				mContext.sendBroadcast(intent);
				mUserPreference.putString(UserPreference.KEY_TTS_SPEED,
						UserPreference.TTS_SPEED_DEFAULT);
				updateTtsSpeed();
				break;
			case R.id.fastPannel:
				slow_select.setVisibility(View.INVISIBLE);
				default_select.setVisibility(View.INVISIBLE);
				fast_select.setVisibility(View.VISIBLE);
				intent.putExtra(TTS_VOICE_SPEED,
						UserPreference.FAST_VOICE_SPEED);
				mContext.sendBroadcast(intent);
				mUserPreference.putString(UserPreference.KEY_TTS_SPEED,
						UserPreference.TTS_SPEED_FAST);
				updateTtsSpeed();
				break;
			default:
				break;
			}

			dlg.dismiss();
		}
	};

	public SettingFragment() {
		super();
	}
	
	public SettingFragment(SettingFragmentListener listener) {
		super();
		mSettingFragmentListener = listener;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate : ");
		super.onCreate(savedInstanceState);
		mUserPreference = new UserPreference(getActivity());
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.setting_layout, null);
		return mView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.d(TAG, "onActivityCreated : ");
		mContext = getActivity();
		setupViews();
	}

	private void setupViews() {
		mSettingWakeup = (RelativeLayout) mView.findViewById(R.id.setting_wakeup);
		PrivatePreference.init(mContext);
		String wakeup_switch = PrivatePreference.getValue("wakeup_switch", "OFF");
		if (wakeup_switch.equals("OFF")) {
			mSettingWakeup.setVisibility(View.GONE);
		}

		mWakeUp = (ImageView) mView.findViewById(R.id.setting_wakeup_switch);
		mWakeUp.setOnClickListener(this);
		mTextViewWakeupLbl = (TextView) mView.findViewById(R.id.settingWakeupLbl);
		if (mUserPreference != null
				&& mUserPreference.getBoolean(UserPreference.KEY_ENABLE_WAKEUP, UserPreference.DEFAULT_WAKEUP)) {
			mWakeUp.setBackgroundResource(R.drawable.setting_open);
			mTextViewWakeupLbl.setText(R.string.wakeup_command);
		} else {
			mWakeUp.setBackgroundResource(R.drawable.setting_closed);
			mTextViewWakeupLbl.setText(R.string.wakeup_closed);
		}

		mShowFloat = (ImageView) mView.findViewById(R.id.setting_float_switch);
		mShowFloat.setOnClickListener(this);
		mTextViewFloatViewLbl = (TextView) mView.findViewById(R.id.textViewFloatViewLbl);
		if (mUserPreference != null
				&& mUserPreference.getBoolean(UserPreference.KEY_ENABLE_FLOATSHOW, UserPreference.DEFAULT_FLOATSHOW)) {
			mShowFloat.setBackgroundResource(R.drawable.setting_open);
			mTextViewFloatViewLbl.setText(R.string.float_window_open);
		} else {
			mShowFloat.setBackgroundResource(R.drawable.setting_closed);
			mTextViewFloatViewLbl.setText(R.string.float_window_closed);
		}

		mSettingMap = (RelativeLayout) mView.findViewById(R.id.setting_map);
		mTextViewMap = (TextView) mSettingMap.findViewById(R.id.textViewMapLbl);

		mTtsSpeed = (RelativeLayout) mView.findViewById(R.id.tts_speed);
		mTextViewTtsSpeed = (TextView) mTtsSpeed
				.findViewById(R.id.textViewTtsLbl);

		mSettingMap.setOnClickListener(this);
		mTtsSpeed.setOnClickListener(this);

		updateMap();
		updateTtsSpeed();
		mSettingReturn = (ImageView) mView.findViewById(R.id.setting_return);
		mSettingReturn.setOnClickListener(this);

		mSettingHelp = (RelativeLayout) mView.findViewById(R.id.setting_help);
		mSettingHelp.setOnClickListener(this);
	}

	private void updateMap() {
		String map = mUserPreference.getString(UserPreference.KEY_MAP, UserPreference.MAP_VALUE_AMAP);
		if (UserPreference.MAP_VALUE_AMAP.equals(map)) {
			mTextViewMap.setText(R.string.amap_with_bracket);
		} else if (UserPreference.MAP_VALUE_BAIDU.equals(map)) {
			mTextViewMap.setText(R.string.baidu_with_bracket);
		} else {
			mTextViewMap.setText(R.string.unknown);
		}
	}

	private void updateTtsSpeed() {
		String ttsSpeed = mUserPreference.getString(UserPreference.KEY_TTS_SPEED, UserPreference.TTS_SPEED_DEFAULT);
		if (UserPreference.TTS_SPEED_SLOW.equals(ttsSpeed)) {
			mTextViewTtsSpeed.setText(R.string.tts_speed_slow_with_bracket);
		} else if (UserPreference.TTS_SPEED_DEFAULT.equals(ttsSpeed)) {
			mTextViewTtsSpeed.setText(R.string.tts_speed_default_with_bracket);
		} else if (UserPreference.TTS_SPEED_FAST.equals(ttsSpeed)) {
			mTextViewTtsSpeed.setText(R.string.tts_speed_fast_with_bracket);
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.setting_wakeup_switch:
			if (mUserPreference != null
					&& mUserPreference.getBoolean(UserPreference.KEY_ENABLE_WAKEUP, UserPreference.DEFAULT_WAKEUP)) {
				mWakeUp.setBackgroundResource(R.drawable.setting_closed);
				mUserPreference.putBoolean(UserPreference.KEY_ENABLE_WAKEUP, false);
				mContext.sendBroadcast(new Intent(TalkService.WAKEUP_EVENT_STOP));
			} else {
				mWakeUp.setBackgroundResource(R.drawable.setting_open);
				mTextViewWakeupLbl.setText(R.string.wakeup_command);
				mUserPreference.putBoolean(UserPreference.KEY_ENABLE_WAKEUP, true);
				mContext.sendBroadcast(new Intent(TalkService.WAKEUP_EVENT_START));
				// showWakeUpRemind();
			}
			break;

		case R.id.setting_float_switch:
			if (mUserPreference != null
					&& mUserPreference
							.getBoolean(UserPreference.KEY_ENABLE_FLOATSHOW, UserPreference.DEFAULT_FLOATSHOW)) {
				mShowFloat.setBackgroundResource(R.drawable.setting_closed);
				mTextViewFloatViewLbl.setText(R.string.float_window_closed);
				mUserPreference.putBoolean(UserPreference.KEY_ENABLE_FLOATSHOW, false);
			} else {
				mShowFloat.setBackgroundResource(R.drawable.setting_open);
				mTextViewFloatViewLbl.setText(R.string.float_window_open);
				mUserPreference.putBoolean(UserPreference.KEY_ENABLE_FLOATSHOW, true);
			}
			break;
		case R.id.setting_map:
			if (mContext == null) {
				LogUtil.e(TAG, "mContext null!");
				return;
			}

			final String map = mUserPreference.getString(UserPreference.KEY_MAP, UserPreference.MAP_VALUE_AMAP);

			dlg = new AlertDialog.Builder(mContext).create();
			dlg.setCanceledOnTouchOutside(true);
			dlg.show();
			Window windowMap = dlg.getWindow();
			windowMap.setContentView(R.layout.custom_dialog_view_map);
			Button cancelBtnMap = (Button) windowMap.findViewById(R.id.map_setting_cancel);
			cancelBtnMap.setOnClickListener(monclClickListener);
			amap_select = (ImageView) windowMap.findViewById(R.id.amap_select);
			baidu_select = (ImageView) windowMap.findViewById(R.id.baidu_select);
			amapPannel = (RelativeLayout) windowMap.findViewById(R.id.amapPannel);
			baiduPannel = (RelativeLayout) windowMap.findViewById(R.id.baiduPannel);
			amapPannel.setOnClickListener(monclClickListener);
			baiduPannel.setOnClickListener(monclClickListener);

			if (map.equals(UserPreference.MAP_VALUE_AMAP)) {
				amap_select.setVisibility(View.VISIBLE);
			} else {
				baidu_select.setVisibility(View.VISIBLE);
			}
			break;
		case R.id.tts_speed:
			if (mContext == null) {
				LogUtil.d(TAG, "mContext null");
				return;
			}
			final String speed = mUserPreference.getString(UserPreference.KEY_TTS_SPEED,
					UserPreference.TTS_SPEED_DEFAULT);

			dlg = new AlertDialog.Builder(mContext).create();
			dlg.setCanceledOnTouchOutside(true);
			dlg.show();
			Window windowTTS = dlg.getWindow();
			windowTTS.setContentView(R.layout.custom_dialog_view_tts);

			Button cancelBtnTTS = (Button) windowTTS
					.findViewById(R.id.map_setting_cancel);

			cancelBtnTTS.setOnClickListener(monclClickListener);

			slow_select = (ImageView) windowTTS.findViewById(R.id.slow_select);
			default_select = (ImageView) windowTTS
					.findViewById(R.id.default_select);
			fast_select = (ImageView) windowTTS.findViewById(R.id.fast_select);

			slowPannel = (RelativeLayout) windowTTS
					.findViewById(R.id.slowPannel);
			defaultPannel = (RelativeLayout) windowTTS
					.findViewById(R.id.defaultPannel);
			fastPannel = (RelativeLayout) windowTTS
					.findViewById(R.id.fastPannel);
			slowPannel.setOnClickListener(monclClickListener);
			defaultPannel.setOnClickListener(monclClickListener);
			fastPannel.setOnClickListener(monclClickListener);

			if (speed.equals(UserPreference.TTS_SPEED_SLOW)) {
				slow_select.setVisibility(View.VISIBLE);
			} else if (speed.equals(UserPreference.TTS_SPEED_DEFAULT)) {
				default_select.setVisibility(View.VISIBLE);
			} else {
				fast_select.setVisibility(View.VISIBLE);
			}
			break;
		case R.id.setting_help:
//			SlidingFragmentActivity SFA1 = (SlidingFragmentActivity) getActivity();
//			SFA1.toggle();
//			MainActivity act = (MainActivity) getActivity();
//			act.jumpHelpScreen();
			if(mSettingFragmentListener!=null){
				mSettingFragmentListener.onClickBtn(R.id.setting_help);
			}
			break;
		case R.id.setting_return:
//			SlidingFragmentActivity SFA = (SlidingFragmentActivity) getActivity();
//			SFA.toggle();
			if(mSettingFragmentListener!=null){
				mSettingFragmentListener.onClickBtn(R.id.setting_return);
			}
			break;
		}
	}

	public interface OnExitAppListener {
		public void onExitApp();
	}

	private void showWakeUpRemind() {
		AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
		dialog.setMessage(R.string.wake_up_remind);
		dialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				mWakeUp.setBackgroundResource(R.drawable.setting_open);
				mTextViewWakeupLbl.setText(R.string.wakeup_command);
				mUserPreference.putBoolean(UserPreference.KEY_ENABLE_WAKEUP, true);
				mContext.sendBroadcast(new Intent(TalkService.WAKEUP_EVENT_START));
			}
		});
		dialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				mWakeUp.setBackgroundResource(R.drawable.setting_closed);
				mTextViewWakeupLbl.setText(R.string.wakeup_closed);
				// mUserPreference.putBoolean(UserPreference.KEY_ENABLE_WAKEUP,
				// false);
				// mContext.sendBroadcast(new
				// Intent(TalkService.WAKEUP_EVENT_STOP));
			}
		});
		dialog.show();
	}
	
	public interface SettingFragmentListener{
		public void onClickBtn(int btnId);
	}
}
