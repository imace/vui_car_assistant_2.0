/**
 * Copyright (c) 2012-2013 Yunzhisheng(Shanghai) Co.Ltd. All right reserved.
 * @FileName : WindowService.java
 * @ProjectName : Voizard
 * @PakageName : cn.yunzhisheng.voizard.service
 * @Author : Brant
 * @CreateDate : 2013-5-27
 */
package cn.yunzhisheng.vui.assistant;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import cn.yunzhisheng.common.util.LogUtil;
import cn.yunzhisheng.phone.PhoneStateReceiver;
import com.ilincar.voice.R;
import cn.yunzhisheng.vui.assistant.preference.UserPreference;
import cn.yunzhisheng.vui.assistant.session.SessionManager;
import cn.yunzhisheng.vui.assistant.util.PackageUtil;
import cn.yunzhisheng.vui.assistant.view.MicrophoneControl;
import cn.yunzhisheng.vui.assistant.view.ScreenFloatView;
import cn.yunzhisheng.vui.assistant.view.SessionContainer;
import cn.yunzhisheng.vui.assistant.view.SessionLinearLayout;
import cn.yunzhisheng.vui.assistant.view.SessionLinearLayout.DispatchKeyEventListener;
import cn.yunzhisheng.vui.assistant.view.SessionLinearLayout.OnTouchEventListener;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Brant
 * @CreateDate : 2013-5-27
 * @ModifiedBy : Brant
 * @ModifiedDate: 2013-5-27
 * @Modified: 2013-5-27: 实现基本功能
 */
@SuppressLint("InlinedApi")
public class WindowService extends Service {
	private static final String TAG = "WindowService";
	// private static final String FLOAT_WINDOW_X = "float_window_X";
	// private static final String FLOAT_WINDOW_Y = "float_window_Y";

	// public static final String ACTION_START_MONITOR =
	// "cn.yunzhisheng.voizard.ACTION.START_MONITOR";
	// public static final String ACTION_STOP_MONITOR =
	// "cn.yunzhisheng.voizard.ACTION.STOP_MONITOR";
	public static final String ACTION_START_HOME_TIMER = "cn.yunzhisheng.vui.assistant.ACTION.START_HOME_TIMER";
	public static final String ACTION_STOP_HOME_TIMER = "cn.yunzhisheng.vui.assistant.ACTION.STOP_HOME_TIMER";

	public static final String ACTION_START_WAKEUP = "cn.yunzhisheng.vui.assistant.ACTION.START_WAKEUP";
	public static final String ACTION_STOP_WAKEUP = "cn.yunzhisheng.vui.assistant.ACTION.STOP_WAKEUP";

	public static final String EXTRA_KEY_DISMISS_FLOAT_WINDOW = "DISMISS_FLOAT_WINDOW";
	public static final String EXTRA_KEY_START_TALK_FROM = "START_TALK_FROM";
	public static final String START_TALK_FROM_MAIN_ACTIVITY = "START_FROM_MAIN_ACTIVITY";
	public static final String START_TALK_FROM_FLOAT_MIC = "START_FROM_FLOAT_MIC";
	public static final String START_TALK_FROM_SHAKE = "START_FROM_SHAKE";
	public static final String START_TALK_FROM_SIMULATE = "START_FROM_SIMULATE";
	public static final String START_TALK_FROM_WIDGET = "START_FROM_WIDGET";
	public static final String START_TALK_FROM_WAKEUP = "START_FROM_WAKEUP";
	public static final String START_TALK_FROM_HARDWARE = "START_FROM_HARDWARE";
	public static final String START_TALK_FROM_OTHER = "START_FROM_OTHER";

	public static final String CANCEL_TALK_FROM_SCREEN_OFF = "CANCEL_FROM_SCREEN_OFF";
	public static final String CANCEL_TALK_FROM_BACK_KEY = "CANCEL_FROM_BACK_KEY";
	public static final String CANCEL_TALK_FROM_MANUAL = "CANCEL_FROM_MANUAL";

	private List<String> mLauncherPackage;
	private Point mWindowSize = new Point();
	private WindowManager mWindowManager;

	private SessionLinearLayout mViewRoot;
	private SessionContainer mSessionContainer;
	private MicrophoneControl mMicrophoneControl;
	private SessionManager mSessionManager = null;

	private WindowManager.LayoutParams mWindowParams = new WindowManager.LayoutParams();
	private UserPreference mPreference;
	private boolean mEnableWakeup;
	private boolean mEnableFloatMic;
	private boolean mPendingStartMicChecker;
	private ScreenFloatView mScreeFloatView;
	private Button mCancelBtn;
    private boolean mStartTalking = false;
    private Runnable mStartTalkRunnable = new Runnable() {
        @Override
        public void run() {
            mStartTalking = false;
        }
    };

	private BroadcastReceiver mScreenReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			LogUtil.d(TAG, "onReceive:intent " + intent);
			String action = intent.getAction();
			if (Intent.ACTION_SCREEN_ON.equals(action)) {
				updateEnableFloatMic();
				startFloatMicChecker(0);
				mSessionManager.onResume();
			} else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
				stopFloatMicChecker();
				mSessionManager.onPause();
			} else if (Intent.ACTION_USER_PRESENT.equals(action)) {
				stopFloatMicChecker();
				mSessionManager.onResume();
			}
		}

	};
	private DispatchKeyEventListener mDispatchKeyEventListener = new DispatchKeyEventListener() {

		@Override
		public boolean dispatchKeyEvent(KeyEvent event) {
			if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
				if (cancelTalk(false, CANCEL_TALK_FROM_BACK_KEY)) {
					mSessionManager.cancelSession();
				}
				return true;
			}
			return false;
		}
	};

	private OnTouchEventListener mOnTouchEventListener = new OnTouchEventListener() {

		@Override
		public boolean onTouchEvent(MotionEvent event) {
			final int x = (int) event.getX();
			final int y = (int) event.getY();
			if ((event.getAction() == MotionEvent.ACTION_DOWN)
					&& ((x < 0) || (x >= mViewRoot.getWidth()) || (y < 0) || (y >= mViewRoot
							.getHeight()))) {
				mSessionManager.cancelSessionWithTTS();
				return true;
			} else if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
				mSessionManager.cancelSessionWithTTS();
				return true;
			}
			return false;
		}

	};

	private OnClickListener mOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View view) {
			if (view == mScreeFloatView.getFloatMicInstance()) {
				startTalk(START_TALK_FROM_FLOAT_MIC);
			} else if (view.getId() == R.id.bottomCancelBtn) {
				LogUtil.d(TAG, "cancel button onClick");
				// mSessionManager.cancelSession();

				// Message msg = new Message();
				// msg.what = SessionPreference.MESSAGE_UI_OPERATE_PROTOCAL;
				// msg.obj = protocal;
				// mSessionManagerHandler.sendMessage(msg);
				mSessionManager.cancelSessionWithTTS();
			}
		}
	};

	private Handler mHandler = new Handler();
	private Runnable mRunnable = new Runnable() {
		@Override
		public void run() {
			if (mEnableFloatMic) {
				if ((PackageUtil.isHome(WindowService.this, mLauncherPackage) || PackageUtil
						.isRunningForeground(WindowService.this))) {
					showFloatView();
				} else {
					hideFloatView();
				}
				startFloatMicChecker(500);
			} else {
				hideFloatView();
			}
		}
	};

	private OnSharedPreferenceChangeListener mPreferenceChangeListener = new OnSharedPreferenceChangeListener() {

		@Override
		public void onSharedPreferenceChanged(
				SharedPreferences sharedPreferences, String key) {
			LogUtil.d(TAG, "onSharedPreferenceChanged: key " + key);
			if (UserPreference.KEY_ENABLE_FLOATSHOW.equals(key)) {
				updateEnableFloatMic();
				if (mEnableFloatMic) {
					startFloatMicChecker(0);
				}
			}
		}
	};

	@Override
	public void onCreate() {
		LogUtil.d(TAG, "onCreate");
		super.onCreate();
		mPreference = new UserPreference(this);
		mScreeFloatView = new ScreenFloatView(this);
		mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		mViewRoot = (SessionLinearLayout) View.inflate(this,
				R.layout.window_service_main, null);
		findViews();
		mSessionManager = new SessionManager(this, mSessionContainer,
				mMicrophoneControl);
		mLauncherPackage = PackageUtil.getLauncherPackages(this);

		initWindowParams();
		initUserPreference();
		registerListener();
		registerReceiver();
		//startFloatMicChecker(0);
        mPendingStartMicChecker = true;
        if (PackageUtil.isRunningForeground(WindowService.this)) {
            mSessionManager.showInitView();
        }
        SessionManager.mIsFirstInitDone = true;

		// handleOntimer.postDelayed(checkAppVisibled, 100);
		Intent intent = new Intent("cn.yunzhisheng.intent.virtualKeyServer");
		startService(intent);
		Intent killNotificationIntent = new Intent(this,
				KillNotificationService.class);
		startService(killNotificationIntent);
	}

	private void startFloatMicChecker(long delay) {
		// LogUtil.d(TAG, "startFloatMicChecker: delay " + delay);
		mHandler.postDelayed(mRunnable, delay);
	}

	private void stopFloatMicChecker() {
		LogUtil.d(TAG, "stopFloatMicChecker");
		setFloatMicEnable(false);
		mHandler.removeCallbacks(mRunnable);
	}

	private void findViews() {
		mSessionContainer = (SessionContainer) mViewRoot
				.findViewById(R.id.sessionContainer);
		mMicrophoneControl = (MicrophoneControl) mViewRoot
				.findViewById(R.id.microphoneControl);
		mCancelBtn = (Button) mViewRoot.findViewById(R.id.bottomCancelBtn);
	}

	private void initUserPreference() {
		updateEnableWakeup();
		updateEnableFloatMic();
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	private void updateDisplaySize() {
		Display display = mWindowManager.getDefaultDisplay();
		if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR2) {
			mWindowSize.y = display.getHeight();
			mWindowSize.x = display.getWidth();
		} else {
			display.getSize(mWindowSize);
		}
		LogUtil.d(TAG, "updateDisplaySize:x " + mWindowSize.x + ",y "
				+ mWindowSize.y);
	}

	private void registerReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_ON);
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		filter.addAction(Intent.ACTION_USER_PRESENT);
		filter.addAction(Intent.ACTION_CONFIGURATION_CHANGED);
		registerReceiver(mScreenReceiver, filter);
	}

	private void unregisterReceiver() {
		unregisterReceiver(mScreenReceiver);
	}

	private void registerListener() {
		mViewRoot.setDispatchKeyEventListener(mDispatchKeyEventListener);
		mViewRoot.setOnTouchEventListener(mOnTouchEventListener);
		mScreeFloatView.setOnClickListener(mOnClickListener);
		mCancelBtn.setOnClickListener(mOnClickListener);
		mPreference
				.registerOnSharedPreferenceChangeListener(mPreferenceChangeListener);
	}

	private void unregisterListener() {
		mViewRoot.setDispatchKeyEventListener(null);
		mCancelBtn.setOnClickListener(null);
		mScreeFloatView.setOnClickListener(null);
		mPreference
				.unregisterOnSharedPreferenceChangeListener(mPreferenceChangeListener);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private void resetWindowParamsFlags() {
		mWindowParams.flags &= ~(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
				| WindowManager.LayoutParams.FLAG_IGNORE_CHEEK_PRESSES
				| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
				| WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
				| WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
				| WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

	private void initWindowParams() {
		mWindowParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
		mWindowParams.format = PixelFormat.RGBA_8888;
		resetWindowParamsFlags();
		mWindowParams.flags =
		// WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
		// |
		WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		mWindowParams.gravity = Gravity.CENTER;
		// mWindowParams.windowAnimations = R.anim.slide_right_in;
		Resources res = getResources();
		int width = res.getDimensionPixelSize(R.dimen.window_width);
		int height = res.getDimensionPixelSize(R.dimen.window_height);
		mWindowParams.width = width;
		mWindowParams.height = height;

		updateDisplaySize();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		LogUtil.d(TAG, "onStartCommand: intent " + intent);
		if (intent != null) {
			String action = intent.getAction();
			if (MessageReceiver.ACTION_START_TALK.equals(action)) {
				String from = intent.getStringExtra(EXTRA_KEY_START_TALK_FROM);
				if(from != null && from.length() > 0){
					startTalk(from);
				}
			} else if (MessageReceiver.ACTION_STOP_TALK.equals(action)) {
				stopTalk();
			} else if (MessageReceiver.ACTION_CANCEL_TALK.equals(action)) {
				cancelTalk();
			} else if (ACTION_START_WAKEUP.equals(action)) {
				mSessionManager.requestStartWakeup();
			}
			// else if (ACTION_START_MONITOR.equals(action)) {
			// startMonitor();
			// } else if (ACTION_STOP_MONITOR.equals(action)) {
			// stopMonitor();
			// }
			// else
			// if (ACTION_START_HOME_TIMER.equals(action)) {
			// startHomeTimer();
			// } else if (ACTION_STOP_HOME_TIMER.equals(action)) {
			// stopHomeTimer();
			// }
		}
		// if (mEnableWakeup) {
		// return START_STICKY;
		// }
		return START_STICKY;
	}

	private void startTalk(String from) {
		LogUtil.d(TAG, "startTalk:from " + from);
		if (TextUtils.isEmpty(from)) {
			throw new RuntimeException("Start talk form can't be empty!");
		} else {
            if (mStartTalking) {
                return;
            }
            mStartTalking = true;
            mHandler.removeCallbacks(mStartTalkRunnable);
            mHandler.postDelayed(mStartTalkRunnable, 2000);
			mSessionManager.startTalk(from);
		}
	}

	private void stopTalk() {
		LogUtil.d(TAG, "stopTalk");
		mSessionManager.stopTalk();
	}

	private void cancelTalk() {
		LogUtil.d(TAG, "cancelTalk");
		mSessionManager.cancelTalk(true);
	}

	public void onReset() {
		LogUtil.d(TAG, "onReset");
		dismiss();
		// Reset recognizer mode to OFFLINE_ONLY
		// PrivatePreference.setValue("vui_recognizer_mode", "OFFLINE_ONLY");
	}

	private void show(View view) {
		if (view == null || view.isShown()) {
			return;
		}
		// 弹出框强制横屏
		if (mWindowParams.screenOrientation != ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE) {
			mWindowParams.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE;
		}

		mWindowManager.addView(view, mWindowParams);
	}

	public void addPrepareView(View view) {
		if (view == null) {
			LogUtil.w(TAG, "addSessionView: view null,return!");
			return;
		}
		dismiss();
		Resources res = getResources();
		int width = res.getDimensionPixelSize(R.dimen.prepare_windows_width);
		int height = res.getDimensionPixelSize(R.dimen.prepare_windows_height);
		WindowManager.LayoutParams WindowParams = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
				WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
						| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		WindowParams.gravity = Gravity.CENTER;
		WindowParams.width = width;
		WindowParams.height = height;
		WindowParams.format = PixelFormat.RGBA_8888;
        if (view.isShown()) {
            mWindowManager.removeViewImmediate(view);
        }
		mWindowManager.addView(view, WindowParams);
	}

	public void show() {
		if (mViewRoot.isShown()) {
			return;
		}
		LogUtil.d(TAG, "show");
		stopFloatMicChecker();
		hideFloatView();
		mPendingStartMicChecker = true;
		show(mViewRoot);
	}

	public void dismiss() {
		if (!mViewRoot.isShown()) {
			return;
		}
		LogUtil.d(TAG, "dismiss");
		mWindowManager.removeViewImmediate(mViewRoot);
		if (mPendingStartMicChecker) {
			mPendingStartMicChecker = false;
			updateEnableFloatMic();
			startFloatMicChecker(0);
		}
	}

	public void dimissView(View view) {
		if (!view.isShown()) {
			return;
		}
		LogUtil.d(TAG, "prepare view dismiss");
		mWindowManager.removeViewImmediate(view);
		if (mPendingStartMicChecker) {
			mPendingStartMicChecker = false;
			updateEnableFloatMic();
			startFloatMicChecker(0);
		}
	}

	public Point getWindowSize() {
		return mWindowSize;
	}

	// public void setWindowSize(int width, int height) {
	// mWindowSize.x = width;
	// mWindowSize.y = height;
	// }

	public void addView(View view, int windowWidth, int windowHeight) {
		LogUtil.d(TAG, "addView:windowWidth " + windowWidth + ",windowHeight "
				+ windowHeight);
		mWindowParams.width = windowWidth;
		mWindowParams.height = windowHeight;
		addView(view);
	}

	private void updateEnableWakeup() {
		mEnableWakeup = mPreference
				.getBoolean(UserPreference.KEY_ENABLE_WAKEUP,
						UserPreference.DEFAULT_WAKEUP);
		LogUtil.d(TAG, "updateEnableWakeup: mEnableWakeup " + mEnableWakeup);
	}

	private void updateEnableFloatMic() {
		LogUtil.d(TAG, "updateEnableFloatMic");
		setFloatMicEnable(mPreference.getShowFloat());
	}

	private void setFloatMicEnable(boolean enable) {
		LogUtil.d(TAG, "setFloatMicEnable: enable " + enable);
		mEnableFloatMic = enable;
	}

	public void addView(View view) {
		LogUtil.d(TAG, "addView");
		dismiss();
		mWindowParams.gravity = Gravity.CENTER;
		mWindowParams.x = 0;
		mWindowParams.y = 0;
		resetWindowParamsFlags();
		mWindowParams.flags =
		// WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
		// |
		WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		if (view == null || !canTextInput(view)) {
			mWindowParams.flags |= WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;
		}

		if ((mWindowParams.softInputMode & WindowManager.LayoutParams.SOFT_INPUT_IS_FORWARD_NAVIGATION) == 0) {
			mWindowParams.softInputMode |= WindowManager.LayoutParams.SOFT_INPUT_IS_FORWARD_NAVIGATION;
		}

		show(view);
	}

	// public void addSmsContentView(View v) {
	// Dialog dialog = new Dialog(WindowService.this);
	// dialog.setContentView(v);
	// dialog.setCanceledOnTouchOutside(true);
	// dialog.show();
	// }

	// public void updateViewPosition(int x) {
	// updateViewPosition(x, mWindowParams.y);
	// }

	// public void updateViewPosition(int x, int y) {
	// LogUtil.d(TAG, "updateViewPosition:x " + x + ",y " + y);
	// if (x == mWindowParams.x && y == mWindowParams.y) {
	// return;
	// }
	//
	// mWindowParams.x = x;
	// mPreference.putInt(FLOAT_WINDOW_X, x);
	//
	// mWindowParams.y = y;
	// mPreference.putInt(FLOAT_WINDOW_Y, y);
	//
	// mWindowManager.updateViewLayout(mViewContainer, mWindowParams);
	// }

	static boolean canTextInput(View v) {
		if (v.onCheckIsTextEditor()) {
			return true;
		}

		if (!(v instanceof ViewGroup)) {
			return false;
		}

		ViewGroup vg = (ViewGroup) v;
		int i = vg.getChildCount();
		while (i > 0) {
			i--;
			v = vg.getChildAt(i);
			if (canTextInput(v)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		LogUtil.d(TAG, "onLowMemory");
	}

	@Override
	public void onDestroy() {
		LogUtil.d(TAG, "onDestroy");
		super.onDestroy();
		dismiss();
		unregisterReceiver();
		unregisterListener();
		stopFloatMicChecker();
		mOnClickListener = null;
		mScreenReceiver = null;
		mSessionManager.cancelTalk(false);
		mSessionManager.onDestroy();
		PhoneStateReceiver.release();
		mSessionManager = null;
		mWindowParams = null;
		mWindowSize = null;
		mLauncherPackage.clear();
		mLauncherPackage = null;
	}

	private boolean cancelTalk(boolean callback, String from) {
		LogUtil.d(TAG, "cancelTalk:from " + from);
		return mSessionManager.cancelTalk(callback);
	}

	public void showFloatView() {
		if (!mScreeFloatView.isShown()) {
			mScreeFloatView.show();
		}
	}

	public void hideFloatView() {
		if (mScreeFloatView.isShown()) {
			mScreeFloatView.hide();
		}
	}

	public void hideCancelBtn(boolean b) {
		if (b) {
			mViewRoot.findViewById(R.id.cancelDivider).setVisibility(View.GONE);
			mViewRoot.findViewById(R.id.bottomCancelBtn).setVisibility(
					View.GONE);
		} else {
			mViewRoot.findViewById(R.id.cancelDivider).setVisibility(
					View.VISIBLE);
			mViewRoot.findViewById(R.id.bottomCancelBtn).setVisibility(
					View.VISIBLE);
		}
	}

    public void startFloatFirst() {
        mHandler.postDelayed(mRunnable, 0);
    }
}
