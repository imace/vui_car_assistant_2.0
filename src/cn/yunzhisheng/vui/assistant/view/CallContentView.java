/**
 * Copyright (c) 2012-2012 Yunzhisheng(Shanghai) Co.Ltd. All right reserved.
 * @FileName : CallWaitingDialog.java
 * @ProjectName : iShuoShuo2
 * @PakageName : cn.yunzhisheng.vui.assistant.view
 * @Author : Brant
 * @CreateDate : 2012-11-14
 */
package cn.yunzhisheng.vui.assistant.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.ilincar.voice.R;

public class CallContentView extends FrameLayout implements ISessionView {
	public static final String TAG = "CallWaitingDialog";
	private TextView mTextViewName, mTextViewPhoneNumber, mTextViewAttribution;
//	private Button mBtnCancel, mBtnCallOk;
//	private ImageView mImageViewAvatar;

	private ICallContentViewListener mListener;
	private CountDownTimer mCountDownTimer;
	private ProgressBar mProgressBarWaiting;

//	private OnClickListener mOnClickListener = new OnClickListener() {
//
//		@Override
//		public void onClick(View v) {
//			switch (v.getId()) {
//			case R.id.btnCallOk:
//				if (mCountDownTimer != null) {
//					mCountDownTimer.cancel();
//				}
//				if (mListener != null) {
//					mListener.onOk();
//				}
//				break;
//			case R.id.btnCallCancel:
//				if (mCountDownTimer != null) {
//					mCountDownTimer.cancel();
//				}
//
//				if (mListener != null) {
//					mListener.onCancel();
//				}
//				break;
//			}
//
//		}
//	};

	public CallContentView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		Resources res = getResources();

		int left = (int) (res.getDimension(R.dimen.call_content_view_margin_left) + 0.5);
		int right = (int) (res.getDimension(R.dimen.call_content_view_margin_right) + 0.5);
		int top = (int) (res.getDimension(R.dimen.call_content_view_margin_top) + 0.5);
		int bottom = (int) (res.getDimension(R.dimen.call_content_view_margin_bottom) + 0.5);

		setPadding(left, top, right, bottom);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.call_content_view, this, true);
		findViews();
//		mBtnCallOk.setOnClickListener(mOnClickListener);
//		mBtnCancel.setOnClickListener(mOnClickListener);

	}

	public CallContentView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CallContentView(Context context) {
		this(context, null);
	}

	private void findViews() {
//		mImageViewAvatar = (ImageView) findViewById(R.id.imageViewCallDialogContactAvatar);
		mTextViewName = (TextView) findViewById(R.id.callDialogName);
		mTextViewPhoneNumber = (TextView) findViewById(R.id.callDialogNumber);
//		mTextViewAttribution = (TextView) findViewById(R.id.callDialogAttribution);
		mProgressBarWaiting = (ProgressBar) findViewById(R.id.progressBarWaiting);
//		mBtnCancel = (Button) findViewById(R.id.btnCallCancel);
//		mBtnCallOk = (Button) findViewById(R.id.btnCallOk);
	}

	public ICallContentViewListener getListener() {
		return mListener;
	}

	public void setListener(ICallContentViewListener mListener) {
		this.mListener = mListener;
	}

	public void setModeConfirm() {
//		if (mBtnCallOk != null) {
//			mBtnCallOk.setText("确\t\t定");
//		}
	}

	public void setModeNomal() {
//		if (mBtnCallOk != null) {
//			//mBtnCallOk.setText("立即拨打");
//			mBtnCallOk.setVisibility(View.GONE);
//		}
//		
//		if (mBtnCancel != null) {
//			//mBtnCallOk.setText("立即拨打");
//			mBtnCancel.setVisibility(View.GONE);
//		}
	}

	public void initView(Drawable drawable, String contactName, String phoneNumber, String attribution) {
//		if (drawable != null) {
////			mImageViewAvatar.setImageDrawable(drawable);
//		} else {
////			mImageViewAvatar.setImageResource(R.drawable.ic_contact_avatar_new);
//		}

		mTextViewName.setText(contactName);
		mTextViewPhoneNumber.setText(phoneNumber);
//		if (TextUtils.isEmpty(attribution)) {
//			mTextViewAttribution.setText(R.string.unknown);
//		} else {
//			/**2014-11-14 yujun*/
//			mTextViewAttribution.setTextSize(18);
//			/**-------------------*/
//			mTextViewAttribution.setText("("+attribution+")");
//		}

	}

	public void startCountDownTimer(final long countDownMillis) {
	    mProgressBarWaiting.setVisibility(View.VISIBLE);
		mCountDownTimer = new CountDownTimer(countDownMillis, 100) {

			public void onTick(long millisUntilFinished) {
				int progress = (int) ((countDownMillis - millisUntilFinished) / (float) countDownMillis * mProgressBarWaiting
					.getMax());
				mProgressBarWaiting.setProgress(mProgressBarWaiting.getMax() - progress);
			}

			public void onFinish() {
				mProgressBarWaiting.setProgress(0);
				if (mListener != null) {
					mListener.onOk();
				}
			}
		}.start();
	}

	public void cancelCountDownTimer() {
		if (mCountDownTimer != null) {
			mCountDownTimer.cancel();
		}
	}

	@Override
	public void release() {

	}

	@Override
	public boolean isTemporary() {
		return true;
	}

	public static interface ICallContentViewListener {
		public void onCancel();

		public void onOk();
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

	}
}
