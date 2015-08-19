package cn.yunzhisheng.vui.assistant.gaode;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import cn.yunzhisheng.vui.assistant.MainActivity;
import com.ilincar.voice.R;

import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.AMapNaviViewListener;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;

/**
 * 
 * 实时导航界面
 * */
public class GaodeRouteActivity2 extends Activity implements AMapNaviListener,
		AMapNaviViewListener {
	public static final String TAG = "GaodeRouteActivity";
	public static final String TAG_MODE = "TAG_MODE";
	public static final String TAG_FROM_LAT = "TAG_FROM_LAT";
	public static final String TAG_FROM_LNG = "TAG_FROM_LNG";
	public static final String TAG_FROM_CITY = "TAG_FROM_CITY";
	public static final String TAG_FROM_POI = "TAG_FROM_POI";
	public static final String TAG_TO_LAT = "TAG_TO_LAT";
	public static final String TAG_TO_LNG = "TAG_TO_LNG";
	public static final String TAG_TO_CITY = "TAG_TO_CITY";
	public static final String TAG_TO_POI = "TAG_TO_POI";
	// 起点终点
	private NaviLatLng mNaviStart = null;
	private NaviLatLng mNaviEnd = null;
	// 起点终点列表
	private ArrayList<NaviLatLng> mStartPoints = new ArrayList<NaviLatLng>();
	private ArrayList<NaviLatLng> mEndPoints = new ArrayList<NaviLatLng>();
	private AMapNaviView mAmapAMapNaviView;
	private ProgressDialog mRouteCalculatorProgressDialog;// 路径规划过程显示状态
	
	private static Intent intent;
	public final static String[] strActions = { "无", "自车", "左转", "右转", "左前方行驶",
		"右前方行驶", "左后方行驶", "右后方行驶", "左转掉头", "直行", "到达途经点", "进入环岛", "驶出环岛",
		"到达服务区", "到达收费站", "到达目的地", "进入隧道", "靠左", "靠右", "通过人行横道", "通过过街天桥",
		"通过地下通道", "通过广场", "到道路斜对面" };

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_simplegps_navi2);
		TTSController ttsManager = TTSController.getInstance(this);// 初始化语音模块
		// ttsManager.init();
		AMapNavi.getInstance(this).setAMapNaviListener(ttsManager);// 设置语音模块播报
		initView(savedInstanceState);
		computeRoute();
		intent=new Intent("com.ilincar.map.startnavig");
		sendBroadcast(intent);
	}

	private void computeRoute() {
		Intent intent = getIntent();
		double startLat = intent.getDoubleExtra(TAG_FROM_LAT, 0);
		double startLng = intent.getDoubleExtra(TAG_FROM_LNG, 0);
		double endLat = intent.getDoubleExtra(TAG_TO_LAT, 0);
		double endLng = intent.getDoubleExtra(TAG_TO_LNG, 0);
		mNaviStart = new NaviLatLng(startLat, startLng);
		mNaviEnd = new NaviLatLng(endLat, endLng);

		mStartPoints.add(mNaviStart);
		mEndPoints.add(mNaviEnd);
		AMapNavi.getInstance(GaodeRouteActivity2.this)
				.setAMapNaviListener(this);
		AMapNavi.getInstance(GaodeRouteActivity2.this).calculateDriveRoute(
				mStartPoints, mEndPoints, null, AMapNavi.DrivingDefault);
		mRouteCalculatorProgressDialog.show();
	}

	private void initView(Bundle savedInstanceState) {
		mAmapAMapNaviView = (AMapNaviView) findViewById(R.id.simplenavimap);
		mAmapAMapNaviView.onCreate(savedInstanceState);
		mAmapAMapNaviView.setAMapNaviViewListener(this);
		mRouteCalculatorProgressDialog = new ProgressDialog(this);
		mRouteCalculatorProgressDialog.setCancelable(true);
		// TTSController.getInstance(this).startSpeaking();
	}

	// ---------------------导航回调--------------------
	@Override
	public void onArriveDestination() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onArrivedWayPoint(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCalculateRouteFailure(int arg0) {
		mRouteCalculatorProgressDialog.dismiss();

	}

	@Override
	public void onCalculateRouteSuccess() {
		mRouteCalculatorProgressDialog.dismiss();
		TTSController.getInstance(this).startSpeaking();
		AMapNavi.getInstance(this).startNavi(AMapNavi.GPSNaviMode);
	}

	@Override
	public void onEndEmulatorNavi() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onGetNavigationText(int arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onGpsOpenStatus(boolean arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onInitNaviFailure() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onInitNaviSuccess() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLocationChange(AMapNaviLocation arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNaviInfoUpdated(AMapNaviInfo arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onReCalculateRouteForTrafficJam() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onReCalculateRouteForYaw() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStartNavi(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTrafficStatusUpdate() {
		// TODO Auto-generated method stub

	}

	// ---------------------导航View事件回调-----------------------------
	@Override
	public void onNaviCancel() {

	}

	@Override
	public void onNaviMapMode(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNaviSetting() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNaviTurnClick() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNextRoadClick() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onScanViewButtonClick() {
		// TODO Auto-generated method stub

	}

	// 返回键处理事件
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.addCategory(Intent.CATEGORY_HOME);
			startActivity(intent);
		}
		return super.onKeyDown(keyCode, event);

	}

	@Override
	public void onNaviInfoUpdate(NaviInfo naveinfo) {
		if (naveinfo == null)
			return;
		intent=new Intent("com.ilincar.map.AMap");
		StringBuffer sbf = new StringBuffer();
		sbf.append("当前路名：");
		sbf.append(naveinfo.getCurrentRoadName());
		intent.putExtra("CurrentRoadName", naveinfo.getCurrentRoadName());
		sbf.append(" 下条路名：");
		sbf.append(naveinfo.getNextRoadName());
		intent.putExtra("NextRoadName", naveinfo.getNextRoadName());
		sbf.append(" 当前方向引导：");
		sbf.append(strActions[naveinfo.m_Icon]);
		intent.putExtra("m_Icon", naveinfo.m_Icon);
		sbf.append(" 下一导航点距离：");
		sbf.append(naveinfo.getCurStepRetainDistance());
		intent.putExtra("CurStepRetainDistance", naveinfo.getCurStepRetainDistance());
		sbf.append(" 剩余旅程：");
		sbf.append(naveinfo.getPathRetainDistance());
		intent.putExtra("PathRetainDistance", naveinfo.getPathRetainDistance());
		intent.putExtra("CameraDistance", naveinfo.getCameraDistance());
		if(naveinfo.getCameraDistance()!=-1){
			sbf.append(" 摄像头类型：");
			intent.putExtra("CameraType", naveinfo.getCameraType());
			if(naveinfo.getCameraType() == 0){
				sbf.append("测速");
			}
			if(naveinfo.getCameraType() == 1){
				sbf.append("监控");
			}
			sbf.append(" 摄像头距离：");
			sbf.append(naveinfo.getCameraDistance());
		}
		Log.d("way", sbf.toString());
		sendBroadcast(intent);
	}

	// ------------------------------生命周期方法---------------------------
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mAmapAMapNaviView.onSaveInstanceState(outState);
	}

	@Override
	public void onResume() {
		super.onResume();
		mAmapAMapNaviView.onResume();

	}

	@Override
	public void onPause() {
		super.onPause();
		mAmapAMapNaviView.onPause();
		AMapNavi.getInstance(this).stopNavi();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		AMapNavi.getInstance(this).removeAMapNaviListener(this);
		TTSController.getInstance(this).stopSpeaking();
		TTSController.destroy();
		mAmapAMapNaviView.onDestroy();
	}

	@Override
	public void onLockMap(boolean arg0) {
		// TODO Auto-generated method stub
	}

	public void onclick(View v) {
		switch (v.getId()) {
		case R.id.back:
			intent=new Intent("com.ilincar.map.startnavig");
			sendBroadcast(intent);
			finish();
			break;
		default:
			break;
		}
	}
}
