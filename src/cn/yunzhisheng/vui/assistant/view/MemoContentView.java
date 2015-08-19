/**
 * Copyright (c) 2012-2012 Yunzhisheng(Shanghai) Co.Ltd. All right reserved.
 * @FileName : MemoContentView.java
 * @ProjectName : iShuoShuo2
 * @PakageName : cn.yunzhisheng.vui.assistant.view
 * @Author : Brant
 * @CreateDate : 2012-11-19
 */
package cn.yunzhisheng.vui.assistant.view;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.ilincar.voice.R;
import cn.yunzhisheng.vui.assistant.util.Util;
import cn.yunzhisheng.vui.modes.MemoInfo;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Brant
 * @CreateDate : 2012-11-19
 * @ModifiedBy : Brant
 * @ModifiedDate: 2012-11-19
 * @Modified:
 * 2012-11-19: 实现基本功能
 */
public class MemoContentView extends FrameLayout implements ISessionView {
	public static final String TAG = "MemoContentView";
	private static final String DATE_FORMAT = "yyyy-MM-dd";
	private static final String TIME_FORMAT = "HH:mm";
	private SimpleDateFormat mDateFormat = new SimpleDateFormat(DATE_FORMAT);
	private SimpleDateFormat mTimeFormat = new SimpleDateFormat(TIME_FORMAT);
	private TextView mTextViewTitle, mTextViewDate, mTextViewTime, mTextViewDueTimeNote;
	private Button mBtnCancel, mBtnOk;
	private IMemoContentViewListener mListener;
	private OnClickListener mOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btnCancelMemo:
				if (mListener != null) {
					mListener.onCancel();
				}
				break;
			case R.id.btnSaveMemo:
				if (mListener != null) {
					mListener.onOk();
				}
				break;
			}

		}
	};

	public MemoContentView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.memo_content_view, this, true);
		findViews();
		setListener();
	}

	public MemoContentView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public MemoContentView(Context context) {
		this(context, null);
	}

	private void findViews() {
		mTextViewTitle = (TextView) findViewById(R.id.textViewMemoTitle);
		mTextViewDate = (TextView) findViewById(R.id.textViewMemoDueDate);
		mTextViewTime = (TextView) findViewById(R.id.textViewMemoDueTime);
		mBtnCancel = (Button) findViewById(R.id.btnCancelMemo);
		mBtnOk = (Button) findViewById(R.id.btnSaveMemo);
		mTextViewDueTimeNote = (TextView) findViewById(R.id.textViewMemoDueDateNote);
	}

	private void setListener() {
		mBtnCancel.setOnClickListener(mOnClickListener);
		mBtnOk.setOnClickListener(mOnClickListener);
	}

	@Override
	public boolean isTemporary() {
		return true;
	}

	@Override
	public void release() {
		mOnClickListener = null;

	}

	public IMemoContentViewListener getListener() {
		return mListener;
	}

	public void setListener(IMemoContentViewListener mListener) {
		this.mListener = mListener;
	}

	public void setMemoInfo(MemoInfo memoInfo) {
		mTextViewTitle.setText(memoInfo.title);
		Date date = new Date(memoInfo.dueTime);
		mTextViewDate
			.setText(String.format(getContext().getString(R.string.memo_date_format), mDateFormat.format(date)));
		mTextViewTime
			.setText(String.format(getContext().getString(R.string.memo_time_format), mTimeFormat.format(date)));

		Calendar after = Calendar.getInstance();
		after.setTimeInMillis(memoInfo.dueTime);
		int days = Util.daysOfTwo(Calendar.getInstance(), after);
		switch (days) {
		case 0:
			mTextViewDueTimeNote.setText(R.string.readable_time_today);
			break;
		case 1:
			mTextViewDueTimeNote.setText(R.string.readable_time_tomorrow);
			break;
		case 2:
			mTextViewDueTimeNote.setText(R.string.readable_time_day_after_tomorrow);
			break;
		case 3:
			mTextViewDueTimeNote.setText(R.string.three_days_later);
			break;
		case 4:
			mTextViewDueTimeNote.setText(R.string.four_days_later);
			break;
		case 5:
			mTextViewDueTimeNote.setText(R.string.five_days_later);
			break;
		case 6:
			mTextViewDueTimeNote.setText(R.string.six_days_later);
			break;
		case 7:
			mTextViewDueTimeNote.setText(R.string.seven_days_later);
			break;
		default:
			mTextViewDueTimeNote.setText(R.string.future);
			break;
		}

	}

	public static interface IMemoContentViewListener {
		public void onCancel();

		public void onOk();
	}
}
