/**
 * Copyright (c) 2012-2013 Yunzhisheng(Shanghai) Co.Ltd. All right reserved.
 * @FileName : ELinearLayout.java
 * @ProjectName : iShuoShuo2
 * @PakageName : cn.yunzhisheng.vui.assistant.view
 * @Author : Brant
 * @CreateDate : 2013-1-31
 */
package cn.yunzhisheng.vui.assistant.view;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.ilincar.voice.R;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Brant
 * @CreateDate : 2013-1-31
 * @ModifiedBy : Brant
 * @ModifiedDate: 2013-1-31
 * @Modified: 2013-1-31: 实现基本功能
 */
public class ELinearLayout extends LinearLayout {
	public static final String TAG = "ELinearLayout";
	private static final int DEFAULT_PAGE_SIZE = 5;
	private ViewBinder mViewBinder;
	private List<? extends Map<String, ?>> mData;
	private LayoutInflater mInflater;
	private int mResource;

	private Drawable mDividerDrawable;

	private LinearLayout mItemsContainer;
	private LinearLayout mFooterContainer;
	private View mLoadMoreView;

	private int mCurrentPage;
	private int mPageSize;
	private int mDividerHorizontalMargin;
	private OnItemClickListener mOnItemClickListener;
	private OnClickListener mOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.linearLayoutListItemLoadMore:
				mCurrentPage++;
				bindData();
				break;
			default:
				if (mOnItemClickListener != null) {
					int position = (Integer) v.getTag();
					mOnItemClickListener.onItemClicked(position, mData.get(position));
				}
				break;
			}

		}
	};

	public ELinearLayout(Context context) {
		this(context, null);
	}

	public ELinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOrientation(VERTICAL);
		mItemsContainer = new LinearLayout(context);
		mItemsContainer.setOrientation(VERTICAL);
		addView(mItemsContainer);

		mFooterContainer = new LinearLayout(context);
		mFooterContainer.setOrientation(VERTICAL);
		addView(mFooterContainer);

		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mLoadMoreView = mInflater.inflate(R.layout.list_item_load_more, mFooterContainer, false);
		setListener();
	}

	public void setDividerHorizontalMargin(int pixel) {
		mDividerHorizontalMargin = pixel;
	}

	private void setListener() {
		mLoadMoreView.setOnClickListener(mOnClickListener);
	}

	public int getPageSize() {
		if (mPageSize > 0) {
			return mPageSize;
		}
		return DEFAULT_PAGE_SIZE;
	}

	public void setPageSize(int pageSize) {
		mPageSize = pageSize;
	}

	public int getDataSize() {
		return mData == null ? 0 : mData.size();
	}

	/**
	 * Current list item count.
	 * 
	 * @Description : getItemCount
	 * @Author : Brant
	 * @CreateDate : 2013-1-31
	 * @return
	 */
	public int getItemCount() {
		int dataSize = getDataSize();
		int count = (mCurrentPage + 1) * getPageSize();
		if (count > dataSize) {
			return dataSize;
		} else {
			return count;
		}
	}

	public Map<String, ?> getData(int position) {
		if (position >= 0 && position < getDataSize()) {
			return mData.get(position);
		}
		return null;
	}

	public void setBindResource(int resource) {
		mResource = resource;
	}

	public void setDividerDrawable(Drawable drawable) {
		mDividerDrawable = drawable;
	}

	/**
	 * Sets the drawable to use as a divider between the list items.
	 * 
	 * @param resId
	 * the resource identifier of the drawable to use as a divider.
	 */
	public void setDividerDrawable(int resId) {
		mDividerDrawable = getResources().getDrawable(resId);
	}

	public void setOnItemClickListener(OnItemClickListener listener) {
		mOnItemClickListener = listener;
	}

	private void bindData() {
		int count = getItemCount();
		if (count > 0) {
			for (int position = 0; position < count; position++) {
				View child = getItemViewAt(position);
				if (child == null) {
					if (mDividerDrawable != null && position != 0) {
						addDividerView();
					}
					child = createViewFromResource(position, this, mResource);
					mItemsContainer.addView(child);
				} else {
					bindView(position, child);
				}
				child.setTag(position);
				child.setClickable(true);
				child.setFocusable(true);
				child.setOnClickListener(mOnClickListener);
			}
			// remove excess children
			int time = mItemsContainer.getChildCount() - 2 * count + 1;
			for (; time > 0; time--) {
				mItemsContainer.removeViewAt(mItemsContainer.getChildCount() - 1);
			}

			// add load more view

			if (getDataSize() > getPageSize() * (mCurrentPage + 1)) {
				if (!mFooterContainer.equals(mLoadMoreView.getParent())) {
					mFooterContainer.addView(mLoadMoreView);
				}
			} else {
				if (mFooterContainer.equals(mLoadMoreView.getParent())) {
					mFooterContainer.removeView(mLoadMoreView);
				}
			}
		} else {
			mItemsContainer.removeAllViews();
			mFooterContainer.removeView(mLoadMoreView);
		}
	}

	public List<? extends Map<String, ?>> getData() {
		return mData;
	}

	/**
	 * You should call setBindResource first.
	 * 
	 * @Description : setData
	 * @Author : Brant
	 * @CreateDate : 2013-1-31
	 * @param data
	 */
	public void setData(List<? extends Map<String, ?>> data) {
		if (mData != data) {
			if (mData != null) {
				mData.clear();
			}
		}
		mData = data;
		bindData();
	}

	public void setViewBinder(ViewBinder viewBinder) {
		mViewBinder = viewBinder;
	}

	private View createViewFromResource(int position, ViewGroup parent, int resource) {
		View v = mInflater.inflate(resource, parent, false);
		bindView(position, v);
		return v;
	}

	private void bindView(int position, View view) {
		final Map<String, ?> dataSet = mData.get(position);
		if (dataSet != null && mViewBinder != null) {
			mViewBinder.bindViewData(position, view, dataSet);
		}
	}

	public void setViewImage(ImageView v, int value) {
		v.setImageResource(value);
	}

	public void setViewImage(ImageView v, String value) {
		try {
			v.setImageResource(Integer.parseInt(value));
		} catch (NumberFormatException nfe) {
			v.setImageURI(Uri.parse(value));
		}
	}

	public void setViewText(TextView v, String text) {
		v.setText(text);
	}

	public View getItemViewAt(int index) {
		int trueIndex;
		if (mDividerDrawable == null) {
			trueIndex = index;
		} else {
			trueIndex = 2 * index;
		}
		return mItemsContainer.getChildAt(trueIndex);
	}

	private void addDividerView() {
		if (mDividerDrawable == null) {
			throw new RuntimeException("Divider drawable null!");
		}
		ImageView divider = new ImageView(getContext());
		LinearLayout.LayoutParams lp = new LayoutParams(LayoutParams.FILL_PARENT, mDividerDrawable.getIntrinsicHeight());
		lp.setMargins(mDividerHorizontalMargin, 0, mDividerHorizontalMargin, 0);
		divider.setLayoutParams(lp);
		divider.setBackgroundDrawable(mDividerDrawable);
		mItemsContainer.addView(divider);
	}

	public void addFooterView(View view) {
		mFooterContainer.addView(view);
	}

	public void removeFooterView(View view) {
		mFooterContainer.removeView(view);
	}

	public void resetPageIndex() {
		mCurrentPage = 0;
	}

	public static interface ViewBinder {
		void bindViewData(int position, View view, Map<String, ?> data);
	}

	public static interface OnItemClickListener {
		void onItemClicked(int position, Map<String, ?> data);
	}
}
