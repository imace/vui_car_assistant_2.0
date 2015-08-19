package cn.yunzhisheng.vui.assistant.gaode;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.AMapNaviViewOptions;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviPath;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.ilincar.voice.R;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Conquer
 * @CreateDate : 2014-2-24
 * @ModifiedBy : Conquer
 * @ModifiedDate: 2014-2-24
 * @Modified:
 * 2014-2-24: 实现基本功能
 */
public class GaodeRouteActivity extends Activity implements  AMapNaviListener{
	private AMapNaviView mAmapAMapNaviView;
	public final static String[] strActions = { "无", "自车", "左转", "右转", "左前方行驶",
		"右前方行驶", "左后方行驶", "右后方行驶", "左转掉头", "直行", "到达途经点", "进入环岛", "驶出环岛",
		"到达服务区", "到达收费站", "到达目的地", "进入隧道", "靠左", "靠右", "通过人行横道", "通过过街天桥",
		"通过地下通道", "通过广场", "到道路斜对面" };
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
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_simplegps_navi2);
		computeRoute();
		init(savedInstanceState);
	}

	/**
	 * 初始化
	 * 
	 * @param savedInstanceState
	 */
	private void init(Bundle savedInstanceState) {
		mAmapAMapNaviView = (AMapNaviView) findViewById(R.id.simplenavimap);
		AMapNaviViewOptions viewOptions = mAmapAMapNaviView.getViewOptions();
		viewOptions.setLayoutVisible(false);
		
		mAmapAMapNaviView.onCreate(savedInstanceState);
	 
		TTSController.getInstance(this).startSpeaking();
		
		// 设置模拟速度
		 AMapNavi.getInstance(this).setEmulatorNaviSpeed(100);
		// 开启模拟导航
		AMapNavi.getInstance(this).startNavi(AMapNavi.EmulatorNaviMode);
		AMapNavi.getInstance(this).setAMapNaviListener(this);
	 
		AMapNaviPath naviPath=AMapNavi.getInstance(this).getNaviPath();
		if(naviPath!=null){
			double length = ((int) (naviPath.getAllLength() / (double) 1000 * 10))
					/ (double) 10;
			// 不足分钟 按分钟计
			int time = (naviPath.getAllTime() + 59) / 60;
			StringBuffer sbf = new StringBuffer();
			sbf.append("路线总时间：");
			sbf.append(time+"分钟");
			sbf.append("  路线总距离：");
			sbf.append(length+"公里");
			Log.d("way", sbf.toString());
		}
		
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
//		AMapNavi.getInstance(GaodeRouteActivity2.this)
//				.setAMapNaviListener(this);
		AMapNavi.getInstance(this).calculateDriveRoute(
				mStartPoints, mEndPoints, null, AMapNavi.DrivingDefault);
//		mRouteCalculatorProgressDialog.show();
	}

	/**
	 * 
	 * 返回键监听事件
	 * */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
		}
		return super.onKeyDown(keyCode, event);
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
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mAmapAMapNaviView.onDestroy();
		//界面结束 停止语音播报
		TTSController.getInstance(this).stopSpeaking();
	}

	 

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
		  
		// TODO Auto-generated method stub  
		
	}

	@Override
	public void onCalculateRouteSuccess() {
		  
		// TODO Auto-generated method stub  
		
	}

	@Override
	public void onEndEmulatorNavi() {
		  
		// TODO Auto-generated method stub  
		
	}

	@Override
	public void onGetNavigationText(int arg0, String arg1) {
		  
	 
		
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
	public void onNaviInfoUpdate(NaviInfo naveinfo) {
		  if(naveinfo==null){
			  return;
		  }
		StringBuffer sbf = new StringBuffer();
		sbf.append("当前路名：");
		sbf.append(naveinfo.getCurrentRoadName());
		sbf.append(" 下条路名：");
		sbf.append(naveinfo.getNextRoadName());
		sbf.append(" 当前方向引导：");
		sbf.append(strActions[naveinfo.m_Icon]);
		sbf.append(" 下一导航点距离：");
		sbf.append(naveinfo.getCurStepRetainDistance());
		sbf.append(" 剩余旅程：");
		sbf.append(naveinfo.getPathRetainDistance());
		if(naveinfo.getCameraDistance()!=-1){
			sbf.append(" 摄像头类型：");
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
}
