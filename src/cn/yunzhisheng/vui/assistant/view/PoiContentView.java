/**
 * Copyright (c) 2012-2013 Yunzhisheng(Shanghai) Co.Ltd. All right reserved.
 * @FileName : PoiContentView.java
 * @ProjectName : iShuoShuo2
 * @PakageName : cn.yunzhisheng.vui.assistant.view
 * @Author : Brant
 * @CreateDate : 2013-1-30
 */
package cn.yunzhisheng.vui.assistant.view;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.ilincar.voice.R;
import cn.yunzhisheng.vui.assistant.view.ELinearLayout.OnItemClickListener;
import cn.yunzhisheng.vui.assistant.view.ELinearLayout.ViewBinder;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Brant
 * @CreateDate : 2013-1-30
 * @ModifiedBy : Brant
 * @ModifiedDate: 2013-1-30
 * @Modified: 2013-1-30: 实现基本功能
 */
public class PoiContentView extends FrameLayout implements ISessionView {
	public static final String TAG = "PoiContentView";

	private int mExpandItemIndex = -1;
	private LinearLayout mHeaders;
	private ELinearLayout mList;
	private TextView mTextViewDataSource;

	public PoiContentView(Context context, int resource, int dividerRes) {
		this(context);
		mList.setBindResource(resource);
		mList.setDividerDrawable(dividerRes);
		mList.setDividerHorizontalMargin(context.getResources().getDimensionPixelSize(R.dimen.poi_list_divier_margin));
	}

	private PoiContentView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.poi_content_view, this, true);
		findViews();
	}

	private PoiContentView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	private PoiContentView(Context context) {
		this(context, null);
	}

	private void findViews() {
		mHeaders = (LinearLayout) findViewById(R.id.linearLayoutPoiHeader);
		mList = (ELinearLayout) findViewById(R.id.listPoi);
		mTextViewDataSource = (TextView) findViewById(R.id.textViewDataSource);
	}

	public LinearLayout getHeaderContainer() {
		return mHeaders;
	}

	public void addHeader(View view) {
		mHeaders.addView(view);
	}

	public void setDataSource(Drawable drawable, String source) {
		mTextViewDataSource.setText(source);
		drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
		mTextViewDataSource.setCompoundDrawables(drawable, null, null, null);
	}

	public void setPoiData(List<? extends Map<String, ?>> data) {
		mList.setData(data);
	}

	public void setPoiListViewBinder(ViewBinder viewBinder) {
		mList.setViewBinder(viewBinder);
	}

	public void setOnItemClickListener(OnItemClickListener listener) {
		mList.setOnItemClickListener(listener);
	}

	public View getItemViewAt(int index) {
		if (index >= 0 && index < mList.getItemCount()) {
			return mList.getItemViewAt(index);
		}
		return null;
	}

	@Override
	public boolean isTemporary() {
		return true;
	}

	@Override
	public void release() {

	}

	public int getExpandItemIndex() {
		return mExpandItemIndex;
	}

	public void setExpandItemIndex(int expandItemIndex) {
		mExpandItemIndex = expandItemIndex;
	}

	public Map<String, ?> getData(int position) {
		return mList.getData(position);
	}
}
