package cn.yunzhisheng.vui.assistant.view;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;
import cn.yunzhisheng.common.util.LogUtil;
import cn.yunzhisheng.vui.assistant.MessageReceiver;
import cn.yunzhisheng.vui.assistant.WindowService;
import com.ilincar.voice.R;
import cn.yunzhisheng.vui.assistant.preference.UserPreference;

@SuppressLint({ "HandlerLeak" })
@SuppressWarnings("unused")
public class ScreenFloatView extends FloatView {
	private static final String TAG = "ScreenFloatView";
	private static final String INPUT_VIEW_X = "fv_x";
	private static final String INPUT_VIEW_Y = "fv_y";
	private static final long AUTO_HIDE_DELAY = 3000;
	private static final String TAG_BTN_FOLAT_MIC = "mic";

	private UserPreference mUserPreference;
	private Context mContext;
	private int mLastPostionX = 0;
	private int mLastPostionY = 0;
	private boolean mHasMoved;
	private double mDef = 0;
	private ImageView mBtnFloatMic;
	private OnClickListener mListener;
	private Handler mHandler = new Handler();
	private Runnable mRunnableHide = new Runnable() {

		@TargetApi(Build.VERSION_CODES.HONEYCOMB)
		@Override
		public void run() {
			if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
				LogUtil.d(TAG, "start alpha animataion.");
				AlphaAnimation animation = new AlphaAnimation(1.0f, 0.5f);
				animation.setDuration(500);
				animation.setFillAfter(true);
				animation.setRepeatCount(0);
				mBtnFloatMic.startAnimation(animation);
			} 
//			else {
//				LogUtil.d(TAG, "setAlpha.");
//				mBtnFloatMic.setAlpha(0.5f);
//			}
		}
	};

	private OnTouchListener mOnTouchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// LogUtil.d(TAG, "onTouch: " + event);
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				mHasMoved = false;
				mLastPostionX = (int) event.getRawX();
				mLastPostionY = (int) event.getRawY();
				mBtnFloatMic.setImageResource(R.drawable.float_mic_pressed);
				mHandler.removeCallbacks(mRunnableHide);
				if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
					LogUtil.d(TAG, "onTouch: start animation");
					AlphaAnimation animation = new AlphaAnimation(0.5f, 1.0f);
					animation.setDuration(200);
					animation.setFillAfter(true);
					animation.setRepeatCount(0);
					mBtnFloatMic.startAnimation(animation);
				} else {
					LogUtil.d(TAG, "onTouch: setAlpha");
					mBtnFloatMic.setAlpha(1.0f);
				}
				break;
			case MotionEvent.ACTION_MOVE:
				int x = (int) event.getRawX();
				int y = (int) event.getRawY();
				if (Math.abs(x - mLastPostionX) > mDef && Math.abs(y - mLastPostionY) > mDef) {
					mHasMoved = true;
					x -= getWidth() / 2;
					y -= getHeight();
					updateViewPosition(x, y);
				}
				break;
			case MotionEvent.ACTION_UP:
				mBtnFloatMic.setImageResource(R.drawable.float_mic_normal);
				if (mHasMoved) {
					requestAutoDock();
				}
				saveViewPostion();
				break;
			}
			return false;
		}
	};

	// private int isMoveTalkCancle(float startMove, float endMove) {
	// float mMoveY1 = startMove - endMove;
	// float mMoveY2 = endMove - startMove;
	// if (mMoveY1 >= 130) {
	// mMoveTalkCancel = 1;
	// startY = endMove;
	// } else if (mMoveY2 >= 120) {
	// mMoveTalkCancel = 2;
	// startY = endMove;
	// }
	// LogUtil.d(TAG, "mMoveTalkCancel = " + mMoveTalkCancel);
	// return mMoveTalkCancel;
	// }

	private OnClickListener mOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			LogUtil.d(TAG, "--float mic button clicked!--");
			if (mListener != null && !mHasMoved) {
				mListener.onClick(v);
			}
		}
	};

	public ScreenFloatView(Context context) {
		super(context);
		mContext = context;
		mUserPreference = new UserPreference(mContext);

		mDef = mContext.getResources().getDimensionPixelSize(R.dimen.float_window_def);
		initViewStyle();
		initViewCtrls();

		// mGestureDetector = new GestureDetector(context, onGestureListener);
		// mGestureDetector.setIsLongpressEnabled(true);
	}

	private void requestAutoDock() {
		LogUtil.d(TAG, "requestAutoDock");
		int x = mLastPostionX, y = mLastPostionY;
		if (x <= mWindowSize.x / 2) {
			x = 0;
		} else {
			x = mWindowSize.x - getWidth();
		}
		updateViewPosition(x, y);

//		resetHideTimer();
	}

	private void resetHideTimer() {
		mHandler.removeCallbacks(mRunnableHide);
		mHandler.postDelayed(mRunnableHide, AUTO_HIDE_DELAY);
	}

	private void initViewStyle() {
		mWindowParams.type = android.view.WindowManager.LayoutParams.TYPE_PHONE
								| android.view.WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
								| android.view.WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
		mWindowParams.format = PixelFormat.RGBA_8888;
		mWindowParams.flags = (WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
								| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
								| WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);

		mWindowParams.gravity = Gravity.LEFT | Gravity.TOP;
		// mWindowParams.windowAnimations = R.style.PauseDialogAnimation;
		mWindowParams.width = LayoutParams.WRAP_CONTENT;
		mWindowParams.height = LayoutParams.WRAP_CONTENT;
	}

	private void initViewCtrls() {
		Context context = getContext();
		mBtnFloatMic = new ImageView(context);
		// mBtnFloatMic.setText("按住说话");
		mBtnFloatMic.setTag(TAG_BTN_FOLAT_MIC);
		mBtnFloatMic.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		mBtnFloatMic.setImageResource(R.drawable.float_mic_normal);
		mBtnFloatMic.setClickable(true);
		mBtnFloatMic.setOnClickListener(mOnClickListener);
		mBtnFloatMic.setOnTouchListener(mOnTouchListener);
		addView(mBtnFloatMic);

		// startTalk();
	}

	private void saveViewPostion() {
		mUserPreference.putInt(INPUT_VIEW_X, mWindowParams.x);
		mUserPreference.putInt(INPUT_VIEW_Y, mWindowParams.y);
	}

	private void getViewPostion() {
		mWindowParams.x = mLastPostionX = mUserPreference.getInt(INPUT_VIEW_X, mWindowSize.x - getWidth());
		mWindowParams.y = mLastPostionY = mUserPreference.getInt(INPUT_VIEW_Y, mWindowSize.y / 2 - getHeight());
	}

	private void updateViewPosition(int x, int y) {
		LogUtil.d(TAG, "updateViewPosition");
		mWindowParams.x = mLastPostionX = x;
		mWindowParams.y = mLastPostionY = y;
		mWindowManager.updateViewLayout(this, mWindowParams);
	}

	private void showSpeakView() {
		mBtnFloatMic.setVisibility(View.GONE);
	}

	private void hideSpeakView() {
		mBtnFloatMic.setVisibility(View.VISIBLE);
	}

	public void setOnClickListener(OnClickListener l) {
		mListener = l;
	}

	public ImageView getFloatMicInstance(){
		return mBtnFloatMic;
	}
	@Override
	public void show() {
		LogUtil.d(TAG, "show");
		getViewPostion();
		super.show();
//		resetHideTimer();
	}

	@Override
	public void hide() {
		LogUtil.d(TAG, "hide");
		super.hide();
	}
}
