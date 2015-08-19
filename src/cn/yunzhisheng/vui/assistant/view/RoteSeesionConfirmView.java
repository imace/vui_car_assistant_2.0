package cn.yunzhisheng.vui.assistant.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.ilincar.voice.R;

public class RoteSeesionConfirmView extends FrameLayout implements ISessionView{

	public static final String TAG = "CallWaitingDialog";
	private TextView mTextViewName, mTextViewPhoneNumber, mTextViewAttribution;

	private IRoteContentViewListener mListener;
	private CountDownTimer mCountDownTimer;
	private ProgressBar mProgressBarWaiting;


	public RoteSeesionConfirmView(Context context, AttributeSet attrs, int defStyle) {
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

	}

	public RoteSeesionConfirmView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public RoteSeesionConfirmView(Context context) {
		this(context, null);
	}

	private void findViews() {
		mTextViewName = (TextView) findViewById(R.id.callDialogName);
		mTextViewPhoneNumber = (TextView) findViewById(R.id.callDialogNumber);
		mProgressBarWaiting = (ProgressBar) findViewById(R.id.progressBarWaiting);
	}

	public IRoteContentViewListener getListener() {
		return mListener;
	}

	public void setListener(IRoteContentViewListener mListener) {
		this.mListener = mListener;
	}

	public void setModeConfirm() {
//		if (mBtnCallOk != null) {
//			mBtnCallOk.setText("确\t\t定");
//		}
	}

	public void setModeNomal() {
	}

	public void initView(Drawable drawable, String contactName, String phoneNumber, String attribution) {

		mTextViewName.setText(contactName);
		mTextViewPhoneNumber.setText(phoneNumber);

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
				Log.d(TAG, "onFinish");
				mProgressBarWaiting.setProgress(0);
				if (mListener != null) {
					Log.d(TAG, "onFinish  ok");
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

	public static interface IRoteContentViewListener {
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
