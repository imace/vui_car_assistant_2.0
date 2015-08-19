package cn.yunzhisheng.vui.assistant;

import java.util.ArrayList;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import cn.yunzhisheng.common.DeviceTool;
import cn.yunzhisheng.common.util.LogUtil;
import com.ilincar.voice.R;
import cn.yunzhisheng.vui.assistant.slidingmenu.lib.SlidingMenu;
import cn.yunzhisheng.vui.assistant.slidingmenu.lib.app.SlidingFragmentActivity;

public class MainActivity extends SlidingFragmentActivity implements OnClickListener {
	private final static String TAG = "MainActivity";
	private SlidingMenu msSlidingMenu;
	private ImageView startSpeak, mSideBtn;
	private TextView mVersionText;

	private ArrayList<View> mViewList = new ArrayList<View>();
	private LayoutInflater mLayoutInflater;
	private ViewGroup indicatorViewGroup;

	private ImageView mImageView;
	private ImageView[] mImageViews;
	private ViewPager mViewPager;
	private View mLayoutLine5;

	private void initViewPager() {
		startSpeak = (ImageView) findViewById(R.id.startSpeak);
		startSpeak.setOnClickListener(this);

		mVersionText = (TextView) findViewById(R.id.tvVersion);
		mVersionText.setText("V"+DeviceTool.getAppVersionName(this));
		mSideBtn = (ImageButton) findViewById(R.id.side_see_list_btn);
		mSideBtn.setOnClickListener(this);

		mImageViews = new ImageView[mViewList.size()];

		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(pagerAdapter);
        mLayoutLine5 = mViewList.get(0).findViewById(R.id.line5);

		indicatorViewGroup = (LinearLayout) findViewById(R.id.viewGroup);

		Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_dot_normal);
		for (int i = 0; i < mViewList.size(); i++) {
			mImageView = new ImageView(MainActivity.this);
			mImageView.setLayoutParams(new LayoutParams(bitmap.getWidth(), bitmap.getHeight()));
			mImageView.setPadding(0, 20, 0, 20);

			if (i == 0) {
				mImageView.setBackgroundResource(R.drawable.icon_dot_selected);
			} else {
				mImageView.setBackgroundResource(R.drawable.icon_dot_normal);
			}
			mImageViews[i] = mImageView;
			indicatorViewGroup.addView(mImageViews[i]);
		}

		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				for (int i = 0; i < mImageViews.length; i++) {
					if (i == arg0) {
						mImageViews[i].setBackgroundResource(R.drawable.icon_dot_selected);
					} else {
						mImageViews[i].setBackgroundResource(R.drawable.icon_dot_normal);
					}
				}
                if (mLayoutLine5 != null) {
                    mLayoutLine5.setVisibility(arg0 != 0 ? View.VISIBLE
                            : View.INVISIBLE);
                }
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
                switch (arg0) {
                case ViewPager.SCROLL_STATE_IDLE:
                    if (mLayoutLine5 != null) {
                        int currentItem = mViewPager.getCurrentItem();
                        mLayoutLine5
                                .setVisibility(currentItem != 0 ? View.VISIBLE
                                        : View.INVISIBLE);
                    }
                    break;
                case ViewPager.SCROLL_STATE_DRAGGING:
                    if (mLayoutLine5 != null) {
                        mLayoutLine5.setVisibility(View.VISIBLE);
                    }
                    break;
                }

			}
		});
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		LogUtil.d(TAG, "onCreate");
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_start);
		mLayoutInflater = getLayoutInflater();

		initSlideMenu();
		// 添加layout
		mViewList.add(mLayoutInflater.inflate(R.layout.pager_layout1, null));
		mViewList.add(mLayoutInflater.inflate(R.layout.pager_layout2, null));

		initViewPager();

		setVolumeControlStream(AudioManager.STREAM_MUSIC);

		Intent i = new Intent(this, WindowService.class);
		// i.setAction(WindowService.ACTION_START_WAKEUP);
		startService(i);		
	}

	private void initSlideMenu() {
		msSlidingMenu = getSlidingMenu();
		msSlidingMenu.setMode(SlidingMenu.RIGHT);
		setBehindContentView(R.layout.right_menu_frame);
		msSlidingMenu.setSlidingEnabled(true);
		msSlidingMenu.setShadowDrawable(R.drawable.menu_shadow);
		msSlidingMenu.setShadowWidth(30);
		msSlidingMenu.setSecondaryShadowDrawable(R.drawable.shadow);
		getSupportFragmentManager().beginTransaction().replace(R.id.right_menu_frame, new SettingFragment()).commit();
		msSlidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		msSlidingMenu.setBehindScrollScale(0);
		msSlidingMenu.setFadeDegree(1f);
		msSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
	}

	@Override
	public void onClick(View v) {
		int key = v.getId();
		switch (key) {
		case R.id.startSpeak:			
			startTalk();
			break;
		case R.id.side_see_list_btn:
//			showSecondaryMenu();	
			Intent intent = new Intent(this, SettingActivity.class);
			startActivity(intent);
			
			break;
		}
	}

	private void startTalk() {
		Intent intent = new Intent(this, WindowService.class);
		intent.setAction(MessageReceiver.ACTION_START_TALK);
		intent.putExtra(WindowService.EXTRA_KEY_START_TALK_FROM, WindowService.START_TALK_FROM_MAIN_ACTIVITY);
		startService(intent);
	}

	private OnClickListener mOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.bluetooth:
				LogUtil.d(TAG, "bluetooth button onclicked");
				startHelpScreen(0);
				break;

			case R.id.music:
				LogUtil.d(TAG, "music button onclicked");
				startHelpScreen(1);
				break;

			case R.id.radio:
				LogUtil.d(TAG, "broadcast button onclicked");
				startHelpScreen(2);
				break;

			case R.id.poi:
				LogUtil.d(TAG, "poi button onclicked");
				startHelpScreen(3);
				break;

			case R.id.setting:
				LogUtil.d(TAG, "setting button onclicked");
				startHelpScreen(4);
				break;

			case R.id.weather:
				LogUtil.d(TAG, "weather button onclicked");
				startHelpScreen(5);
				break;
			case R.id.stock:
				LogUtil.d(TAG, "stock button onclicked");
				startHelpScreen(6);
				break;
			default:
				break;
			}
		}
	};

	PagerAdapter pagerAdapter = new PagerAdapter() {

		@Override
		public int getCount() {
			return mViewList.size();
		}

		@Override
		public Object instantiateItem(View container, int position) {
			switch (position) {
			case 0:
				((ViewPager) container).addView(mViewList.get(position), 0);
				LinearLayout bluetooth = (LinearLayout) findViewById(R.id.bluetooth);
				LinearLayout music = (LinearLayout) findViewById(R.id.music);
				LinearLayout broadcast = (LinearLayout) findViewById(R.id.radio);
				LinearLayout poi = (LinearLayout) findViewById(R.id.poi);
				LinearLayout setting = (LinearLayout) findViewById(R.id.setting);
				bluetooth.setOnClickListener(mOnClickListener);
				music.setOnClickListener(mOnClickListener);
				broadcast.setOnClickListener(mOnClickListener);
				poi.setOnClickListener(mOnClickListener);
				setting.setOnClickListener(mOnClickListener);
				break;

			case 1:
				((ViewPager) container).addView(mViewList.get(position), 1);
				LinearLayout weather = (LinearLayout) findViewById(R.id.weather);
				LinearLayout stock = (LinearLayout) findViewById(R.id.stock);
				weather.setOnClickListener(mOnClickListener);
				stock.setOnClickListener(mOnClickListener);
				break;
			}

			return mViewList.get(position);
		}

		@Override
		public void destroyItem(View container, int position, Object object) {
			((ViewPager) container).removeView(mViewList.get(position));
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

	};
	
	private void startHelpScreen(int position){
        Intent intent = new Intent(this, HelpActivity.class);
        intent.putExtra("position", position);
        startActivity(intent);
	}
	
}