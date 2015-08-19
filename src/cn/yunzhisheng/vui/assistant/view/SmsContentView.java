/**
 * Copyright (c) 2012-2012 Mango(Shanghai) Co.Ltd. All right reserved.
 * @FileName : SmsComponent.java
 * @ProjectName : iShuoShuo2
 * @PakageName : cn.yunzhisheng.vui.assistant.view
 * @Author : Brant
 * @CreateDate : 2012-11-7
 */
package cn.yunzhisheng.vui.assistant.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import cn.yunzhisheng.common.util.LogUtil;
import com.ilincar.voice.R;
import cn.yunzhisheng.vui.assistant.view.TextEditerDialog.ITextEditorListener;

public class SmsContentView extends FrameLayout implements ISessionView {
	public static final String TAG = "SmsContentView";
	// private ImageView mImageViewAvatar;
	private TextView mTextViewName, mTextViewPhoneNumber;
	// private TextView mTextViewMessageContentLength;
	private EditText mEditTextSmsInput;
	private Button mBtnCancel, mBtnSend, mBtnReput;
	// private Button mBtnClearSmsContent;
	// private String mName, mPhoneNumber;
	private ISmsContentViewListener mListener;
	private TextEditerDialog mTextEditerDialog;

	private OnTouchListener mOnTouchListener = new View.OnTouchListener() {
		public boolean onTouch(View view, MotionEvent motionEvent) {
			LogUtil.d(TAG, "--onTouch--");

			if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
				if (mTextEditerDialog == null) {
					mTextEditerDialog = new TextEditerDialog(getContext(),
							R.style.TextEditorDialog);
					mTextEditerDialog
							.setTextEditorListener(new ITextEditorListener() {

								@Override
								public void onResult(String text) {
									LogUtil.d(TAG, "--onResult text : " + text);
									setMessage(text);
									if (mListener != null) {
										LogUtil.d(TAG, "--mListener != null--");
										mListener.onEndEdit(text);
									}
								}

								@Override
								public void onCancel() {
									LogUtil.d(TAG, "--onCancel--");
									if (mListener != null) {
										LogUtil.d(TAG, "--mListener != null--");
										mListener.onEndEdit(getMessage());
									}
								}
							});
				}
				LogUtil.d(TAG, "--mTextEditerDialog.setText(getMessage())--");
				
				mTextEditerDialog.setText(getMessage());
				
				if (mListener != null) {
					mListener.onBeginEdit();
				}
			}

			return false;
		}
	};

	private OnClickListener mOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btnCancelSms:
				LogUtil.d(TAG, "onCancel");
				if (mListener != null) {
					mListener.onCancel();
				}
				break;
			case R.id.btnSendSms:
				if (mListener != null) {
					mListener.onOk();
				}
				break;
			case R.id.btnReput:
				if (mListener != null) {
					mListener.onClearMessage();
				}
				break;
			}
		}
	};
	

	private TextWatcher textWatcher = new TextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			// TODO Auto-generated method stub
			if (TextUtils.isEmpty(getMessage())) {
				setReputBtnDisable(true);
			} else {
				setReputBtnDisable(false);
			}
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			
		}
	};


	public SmsContentView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.sms_content_view, this, true);
		findViews();
		setListener();
	}

	public SmsContentView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SmsContentView(Context context) {
		this(context, null);
	}

	private void findViews() {
		// mImageViewAvatar = (ImageView) findViewById(R.id.imageViewAvatar);
		mTextViewName = (TextView) findViewById(R.id.textViewContactName);
		mTextViewPhoneNumber = (TextView) findViewById(R.id.textViewPhoneNumber);
		mEditTextSmsInput = (EditText) findViewById(R.id.editTextSmsInput);
		/**
		 * 2014-11-11 yujun mTextViewMessageContentLength = (TextView)
		 * findViewById(R.id.textViewSmsContentLength); mBtnClearSmsContent =
		 * (Button) findViewById(R.id.btnClearSmsContent); -------------------
		 */
		mBtnSend = (Button) findViewById(R.id.btnSendSms);
		mBtnCancel = (Button) findViewById(R.id.btnCancelSms);
		mBtnReput = (Button) findViewById(R.id.btnReput);
		
		if (TextUtils.isEmpty(getMessage())) {
			setReputBtnDisable(true);
		} else {
			setReputBtnDisable(false);
		}
	}
	
	public void setReputBtnDisable(boolean b) {
		if (b) {
			mBtnReput.setTextColor(getResources().getColor(R.color.grey));
			mBtnReput.setEnabled(false);
		} else {
			mBtnReput.setTextColor(getResources().getColor(R.color.grey_white));
			mBtnReput.setEnabled(true);
		}
	}

	private void setListener() {
		mBtnSend.setOnClickListener(mOnClickListener);
		mBtnCancel.setOnClickListener(mOnClickListener);
		mBtnReput.setOnClickListener(mOnClickListener);
		mEditTextSmsInput.setOnClickListener(mOnClickListener);
		mEditTextSmsInput.setOnTouchListener(mOnTouchListener);
		mEditTextSmsInput.addTextChangedListener(textWatcher);
		// mEditTextSmsInput.setOnClickListener(mOnClickListener);
		// mEditTextSmsInput.setOnFocusChangeListener(mFocusChangeListener);
	}

	public void initRecipient(Drawable drawable, String name, String phoneNumber) {
		// if (drawable != null) {
		// mImageViewAvatar.setImageDrawable(drawable);
		// } else {
		// mImageViewAvatar.setImageResource(R.drawable.ic_contact_avatar_new);
		// }
		mTextViewName.setText(name);
		mTextViewPhoneNumber.setText(phoneNumber);
	}

	public void clearMessage() {
		mEditTextSmsInput.setText("");
	}

	public void setMessage(String msg) {
		mEditTextSmsInput.setText(msg);
		int length = mEditTextSmsInput.getText().length();
		mEditTextSmsInput.setSelection(length);
		/**
		 * 2014-11-11 yujun
		 * mTextViewMessageContentLength.setText(String.valueOf(length));
		 * --------------------
		 */
	}

	public String getMessage() {
		return mEditTextSmsInput.getText().toString();
	}

	public ISmsContentViewListener getListener() {
		return mListener;
	}

	public void setListener(ISmsContentViewListener l) {
		this.mListener = l;
	}

	public boolean becomeFirstRespond() {
		mEditTextSmsInput.setFocusable(true);
		mEditTextSmsInput.dispatchTouchEvent(MotionEvent.obtain(
				SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
				MotionEvent.ACTION_DOWN, 0, 0, 0));
		mEditTextSmsInput.dispatchTouchEvent(MotionEvent.obtain(
				SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
				MotionEvent.ACTION_UP, 0, 0, 0));
		boolean tag = mEditTextSmsInput.requestFocus();
		return tag;

	}

	@Override
	public boolean isTemporary() {
		return true;
	}

	@Override
	public void release() {
		if (mTextEditerDialog != null) {
			mTextEditerDialog.cancel();
			mTextEditerDialog = null;
		}
		mOnClickListener = null;
	}

	public static interface ISmsContentViewListener {
		public void onBeginEdit();

		public void onEndEdit(String msg);

		public void onCancel();

		public void onOk();

		public void onClearMessage();
	}
}
