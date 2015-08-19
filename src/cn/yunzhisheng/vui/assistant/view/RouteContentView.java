/**
 * Copyright (c) 2012-2013 Yunzhisheng(Shanghai) Co.Ltd. All right reserved.
 * @FileName : RouteContentView.java
 * @ProjectName : iShuoShuo2
 * @PakageName : cn.yunzhisheng.vui.assistant.view
 * @Author : Brant
 * @CreateDate : 2013-1-28
 */
package cn.yunzhisheng.vui.assistant.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import cn.yunzhisheng.vui.assistant.baidu.BaiduMap;
import com.ilincar.voice.R;
import cn.yunzhisheng.vui.assistant.gaode.GaodeMap;
import cn.yunzhisheng.vui.assistant.preference.SessionPreference;
import cn.yunzhisheng.vui.assistant.widget.TabWidget;
import cn.yunzhisheng.vui.assistant.widget.TabWidget.OnTabSelectionChanged;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Brant
 * @CreateDate : 2013-1-28
 * @ModifiedBy : Brant
 * @ModifiedDate: 2013-1-28
 * @Modified: 2013-1-28: 实现基本功能
 */
public class RouteContentView extends FrameLayout implements ISessionView {
	public static final String TAG = "RouteContentView";
	private LayoutInflater mInflater;
	private TabWidget mTabWidget;
	private TextView mTextViewStartPoi, mTextViewEndPoi;
	private View mBtnViewMap;
	private int mCurrentTab = -1;

	private double mStartLat, mStartLng, mEndLat, mEndLng;
	private String mStartCity, mStartPoi;
	private String mEndCity, mEndPoi;
	private String mMethod;
	private OnTabSelectionChanged mTabSelectionChangedListener = new OnTabSelectionChanged() {

		@Override
		public void onTabSelectionChanged(int tabIndex, boolean clicked) {
			switch (tabIndex) {
			case 0:
				mMethod = SessionPreference.VALUE_ROUTE_METHOD_WALK;
				break;
			case 1:
				mMethod = SessionPreference.VALUE_ROUTE_METHOD_BUS;
				break;
			case 2:
				mMethod = SessionPreference.VALUE_ROUTE_METHOD_CAR;
				break;
			}
			setCurrentTab(tabIndex);
		}
	};

	private OnClickListener mOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			String mode = "";
			switch (mCurrentTab) {
			case 0:
				mode = BaiduMap.ROUTE_MODE_WALKING;
				break;
			case 1:
				mode = BaiduMap.ROUTE_MODE_TRANSIT;
				break;
			case 2:
			default:
				mode = BaiduMap.ROUTE_MODE_DRIVING;
				break;
			}

			GaodeMap.showRoute(
				getContext(),
				mode,
				mStartLat,
				mStartLng,
				mStartCity,
				"",
				mStartPoi,
				mEndLat,
				mEndLng,
				mEndCity,
				mEndPoi);
		}
	};

	public RouteContentView(Context context) {
		super(context);
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mInflater.inflate(R.layout.route_content_view, this, true);
		findViews();
		setListener();
		init();
	}

	private void findViews() {
		mTabWidget = (TabWidget) findViewById(android.R.id.tabs);
		mTextViewStartPoi = (TextView) findViewById(R.id.textViewRouteStartPoi);
		mTextViewEndPoi = (TextView) findViewById(R.id.textViewRouteEndPoi);
		mBtnViewMap = findViewById(R.id.linearLayoutOpenMap);
	}

	private void setListener() {
		mTabWidget.setTabSelectionListener(mTabSelectionChangedListener);
		mBtnViewMap.setOnClickListener(mOnClickListener);
	}

	private void init() {
		mTabWidget.setDrawBottomStrips(false);
		// mTabWidget.setDividerDrawable(R.drawable.ic_tab_divider);
		// mTabWidget.addView(getTabIndicator(R.drawable.btn_route_method_foot_bg));
		// mTabWidget.addView(getTabIndicator(R.drawable.btn_route_method_bus_bg));
		// mTabWidget.addView(getTabIndicator(R.drawable.btn_route_method_car_bg));
	}

	private View getTabIndicator(int icon) {
		View tab = mInflater.inflate(R.layout.route_tab_indicator, mTabWidget, false);
		ImageView imgIcon = (ImageView) tab.findViewById(R.id.icon);
		imgIcon.setImageResource(icon);
		return tab;
	}

	public void setStart(double lat, double lng, String city, String poi) {
		mStartLat = lat;
		mStartLng = lng;
		mStartCity = city;
		mStartPoi = poi;

		mTextViewStartPoi.setText(mStartPoi);
	}

	public void setEnd(double lat, double lng, String city, String poi) {
		mEndLat = lat;
		mEndLng = lng;
		mEndCity = city;
		mEndPoi = poi;

		mTextViewEndPoi.setText(mEndPoi);
	}

	public void setMethod(String method) {
		mMethod = method;
		if (SessionPreference.VALUE_ROUTE_METHOD_WALK.equals(method)) {
			setCurrentTab(0);
		} else if (SessionPreference.VALUE_ROUTE_METHOD_BUS.equals(method)) {
			setCurrentTab(1);
		} else if (SessionPreference.VALUE_ROUTE_METHOD_CAR.equals(method)) {
			setCurrentTab(2);
		}
	}

	public void setCurrentTab(int index) {
		if (index < 0 || index >= mTabWidget.getTabCount()) {
			return;
		}

		if (index == mCurrentTab) {
			return;
		}

		mCurrentTab = index;

		// Call the tab widget's focusCurrentTab(), instead of just
		// selecting the tab.
		mTabWidget.focusCurrentTab(mCurrentTab);
	}

	@Override
	public boolean isTemporary() {
		return true;
	}

	@Override
	public void release() {

	}
}
