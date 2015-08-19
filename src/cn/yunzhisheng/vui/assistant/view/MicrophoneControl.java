/**
 * Copyright (c) 2012-2012 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * @FileName : MicrophoneControl.java
 * @ProjectName : V Plus 1.0
 * @PakageName : cn.yunzhisheng.vui.assistant.assistant.view
 * @Author : Brant
 * @CreateDate : 2012-5-24
 */
package cn.yunzhisheng.vui.assistant.view;

import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import cn.yunzhisheng.common.util.LogUtil;
import com.ilincar.voice.R;
import cn.yunzhisheng.vui.assistant.preference.AssistantPreference;

@SuppressLint("HandlerLeak")
public class MicrophoneControl extends FrameLayout {
	public static final String TAG = "MicrophoneControl";

	private ImageView mBtnMic, mCancelBtn;
	private ImageView mImageViewMicRecognize;
	private ImageView mVoiceLevel;
	private RotateAnimation mRotateAnimationMicRecognize;
	private TextView mTextViewAnswer;
	private int mVolume;
	private Timer mTimer;
	private TimerTask mTimerTask;

	private Handler mHandler = new Handler();

	public MicrophoneControl(Context context) {
		this(context, null);
	}

	public MicrophoneControl(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public MicrophoneControl(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		View.inflate(context, R.layout.mic_control, this);
		mBtnMic = (ImageView) findViewById(R.id.btnMic);
		mCancelBtn = (ImageView) findViewById(R.id.cancelBtn);
		mVoiceLevel = (ImageView) findViewById(R.id.voice_level);
		mTextViewAnswer = (TextView) findViewById(R.id.text_answer);
		int normal_text_size = 24;
		mTextViewAnswer.setTextSize(normal_text_size);
		mImageViewMicRecognize = (ImageView) findViewById(R.id.imageViewRecognize);
		mRotateAnimationMicRecognize = new RotateAnimation(
			0,
			360,
			Animation.RELATIVE_TO_SELF,
			0.5f,
			Animation.RELATIVE_TO_SELF,
			0.5f);
		mRotateAnimationMicRecognize.setDuration(1000);
		mRotateAnimationMicRecognize.setInterpolator(new LinearInterpolator());
		mRotateAnimationMicRecognize.setRepeatCount(Animation.INFINITE);

		mTimer = new Timer(TAG + "_Volume_Timer");
	}

	@Override
	public void setOnClickListener(OnClickListener l) {
		mBtnMic.setOnClickListener(l);
		mCancelBtn.setOnClickListener(l);
		mVoiceLevel.setOnClickListener(l);
	}


	public void setAnswerText(String text) {
		LogUtil.d(TAG, "setAnswerText:text " + text);
		mTextViewAnswer.setText(text);
	}

	public void setAnswerText(int textRes) {
		LogUtil.d(TAG, "setAnswerText");
		mTextViewAnswer.setText(textRes);
	}

	public void setEnabled(boolean enable) {
		if (enable) {
			mBtnMic.setEnabled(true);
		} else {
			mBtnMic.setEnabled(false);
		}
	}

	public void onPrepare() {
		findViewById(R.id.ly2).setBackgroundDrawable(getResources().getDrawable(R.drawable.mic_status_bk));
		mBtnMic.setEnabled(false);
		mTextViewAnswer.setText(R.string.mic_prepare);
		mImageViewMicRecognize.setVisibility(View.INVISIBLE);
	}

	public void onIdle(boolean resetMicrophoneText) {
		if (mTimerTask != null) {
			mTimerTask.cancel();
		}
		findViewById(R.id.ly2).setBackgroundDrawable(getResources().getDrawable(R.drawable.mic_status_bk));
		mBtnMic.setVisibility(View.VISIBLE);
		mImageViewMicRecognize.setVisibility(View.GONE);
		mVoiceLevel.setVisibility(View.GONE);
		mImageViewMicRecognize.clearAnimation();
		mBtnMic.setEnabled(true);
	}

	public void onRecording() {
		// 开始接收声音
		findViewById(R.id.ly2).setBackgroundDrawable(getResources().getDrawable(R.drawable.mic_status_bk_speaking));
		mBtnMic.setVisibility(View.GONE);
		mTextViewAnswer.setText(R.string.mic_recording);
		mVoiceLevel.setVisibility(View.VISIBLE);
		mImageViewMicRecognize.setVisibility(View.GONE);
		if (mTimerTask != null) {
			mTimerTask.cancel();
		}
		mTimerTask = new TimerTask() {

			@Override
			public void run() {
				updateVolume((int) AssistantPreference.mRecordingVoiceVolume);
			}
		};
		mTimer.scheduleAtFixedRate(mTimerTask, 0, 200);
	}

	public void onProcess() {
		if (mTimerTask != null) {
			mTimerTask.cancel();
		}
		findViewById(R.id.ly2).setBackgroundDrawable(getResources().getDrawable(R.drawable.mic_status_bk));
		mVoiceLevel.setVisibility(View.GONE);
		mBtnMic.setVisibility(View.GONE);
		mTextViewAnswer.setText(R.string.mic_processing);
		mImageViewMicRecognize.setVisibility(View.VISIBLE);
		mImageViewMicRecognize.startAnimation(mRotateAnimationMicRecognize);
	}

	private void updateVolume(int volume) {
		mVolume = volume;
		if (mVolume < 10) {
			setVoiceLevel(1);
		} else if (mVolume < 20) {
			setVoiceLevel(2);
		} else if (mVolume < 30) {
			setVoiceLevel(3);
		} else if (mVolume < 40) {
			setVoiceLevel(4);
		} else if (mVolume < 50) {
			setVoiceLevel(5);
		} else if (mVolume < 60) {
			setVoiceLevel(6);
		} else if (mVolume < 70) {
			setVoiceLevel(7);
		} else if (mVolume < 80) {
			setVoiceLevel(8);
		} else if (mVolume < 90) {
			setVoiceLevel(9);
		} else {
			setVoiceLevel(10);
		}
	}

	public void setVoiceLevel(final int level) {
		mHandler.post(new Runnable() {

			@Override
			public void run() {
				mVoiceLevel.getDrawable().setLevel(level);
				// mMicSoundRender.startVoiceAnim();
				// mImageViewMicRecognizeBg.setVisibility(View.VISIBLE);
				mImageViewMicRecognize.setVisibility(View.GONE);
			}
		});

	}

	public void onDestroy() {
		// mMicSoundRender.onDestroy();
		// mMicSoundRender = null;
		setOnClickListener(null);
		if (mTimerTask != null) {
			mTimerTask.cancel();
		}
		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}

		mRotateAnimationMicRecognize = null;
	}
}
