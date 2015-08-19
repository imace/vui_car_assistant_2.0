/**
 * Copyright (c) 2012-2012 Mango(Shanghai) Co.Ltd. All right reserved.
 * @FileName : PickView.java
 * @ProjectName : iShuoShuo2
 * @PakageName : cn.yunzhisheng.vui.assistant.view
 * @Author : Brant
 * @CreateDate : 2012-11-7
 */
package cn.yunzhisheng.vui.assistant.view;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import com.ilincar.voice.R;

public class PickBaseView extends LinearLayout implements ISessionView {
	public static final String TAG = "PickView";
	protected static final int PICK_VIEW_TAG_BUTTON_CANCEL = -1;
	private boolean mHasHeader;

	// protected Button mBtnCancel;
	protected IPickListener mPickListener;
	protected LinearLayout mContainer;
	protected LayoutInflater mLayoutInflater;
	protected OnClickListener mOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Integer tag = (Integer) v.getTag();
			onViewClick(tag);
		}
	};

	public PickBaseView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mLayoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		setOrientation(VERTICAL);
		setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));

		Resources res = context.getResources();
		mContainer = new LinearLayout(context);
		mContainer.setOrientation(VERTICAL);
		int height = res
				.getDimensionPixelSize(R.dimen.pick_view_cancel_button_height);
		int textSize = res
				.getDimensionPixelSize(R.dimen.pick_view_cancel_button_textsize);
		// mBtnCancel = new Button(context);
		// mBtnCancel.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
		// height));
		//
		// mBtnCancel.setText(R.string.cancel);
		// mBtnCancel.setTextSize(textSize);
		// mBtnCancel.setBackgroundResource(R.drawable.btn_call_cancle_bg);
		addView(mContainer);
		// addView(mBtnCancel);
		// mBtnCancel.setTag(PICK_VIEW_TAG_BUTTON_CANCEL);
		// mBtnCancel.setOnClickListener(mOnClickListener);
		/*
		 * mContainer = new LinearLayout(context);
		 * mContainer.setOrientation(VERTICAL); addView(mContainer);
		 * mLayoutInflater = (LayoutInflater)
		 * context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		 * mLayoutInflater.inflate(R.layout.pickphone_content_view, this, true);
		 * mBtnCancel = (Button) findViewById(R.id.btnCancel);
		 * mBtnCancel.setOnClickListener(mOnClickListener);
		 */
	}

	public PickBaseView(Context context) {
		this(context, null);
	}

	public void setHeader(View view) {
		mContainer.addView(view, 0);
		mHasHeader = true;
	}

	public void removeHeader() {
		if (mHasHeader) {
			mContainer.removeViewAt(0);
			mHasHeader = false;
		}
	}

	protected void addItem(View view) {
		mContainer.addView(view);
		view.setOnClickListener(mOnClickListener);
		view.setTag(mHasHeader ? mContainer.getChildCount() - 2 : mContainer
				.getChildCount() - 1);// 设置tag
	}

	public int getItemCount() {
		if (mHasHeader) {
			return mContainer.getChildCount() - 1;
		} else {
			return mContainer.getChildCount();
		}
	}

	public View getItem(int index) {
		int count = getItemCount();
		if (index >= 0 && index < count) {
			return mHasHeader ? mContainer.getChildAt(index + 1) : mContainer
					.getChildAt(index);
		}
		return null;
	}

	public IPickListener getPickListener() {
		return mPickListener;
	}

	public void setPickListener(IPickListener mPickListener) {
		this.mPickListener = mPickListener;
	}

	@Override
	public boolean isTemporary() {
		return true;
	}

	protected void onViewClick(int tag) {
		/*
		 * if (tag == PICK_VIEW_TAG_BUTTON_CANCEL) { if (mPickListener != null)
		 * { mPickListener.onPickCancel(); } return; } else
		 */if (tag >= 0) {
			if (mPickListener != null) {
				mPickListener.onItemPicked(tag);
			}
			return;
		}
	}

	@Override
	public void release() {

	}

	public static interface IPickListener {
		void onItemPicked(int position);

		void onPickCancel();
		void onNext();
		void onPre();
	}

	public void getPageNumber() {

	}

    public void updateLayoutParams() {
        Resources res = getResources();
        if (getItemCount() < 3) {
            setLayoutParams(new LayoutParams(res
                    .getDimensionPixelSize(R.dimen.window_width), res
                    .getDimensionPixelSize(R.dimen.session_contain_height)));
        } else {
            setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        }
    }
}
