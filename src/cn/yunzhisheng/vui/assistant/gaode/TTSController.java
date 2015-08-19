package cn.yunzhisheng.vui.assistant.gaode;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.media.AudioManager;
import android.text.TextUtils;
import android.util.Log;
import cn.yunzhisheng.common.util.LogUtil;
import cn.yunzhisheng.tts.offline.TTSPlayerListener;
import cn.yunzhisheng.tts.offline.basic.ITTSControl;
import cn.yunzhisheng.tts.offline.basic.TTSFactory;
import cn.yunzhisheng.tts.offline.common.USCError;
import cn.yunzhisheng.vui.assistant.preference.UserPreference;

import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.NaviInfo;
/**
 * 语音播报组件
 *
 */
public class TTSController implements  AMapNaviListener {

	public static TTSController ttsManager;
	private Context mContext;
	private float TTS_SPEED_SLOW = 0;
	private float TTS_SPEED_STANDARD = 13;
	private float TTS_SPEED_FAST = 20;
	// 合成对象.
//	private SpeechSynthesizer mSpeechSynthesizer;
	private static final String TAG = "TTSController";
	
	private static ITTSControl mTTSControl;
	TTSController(Context context) {
		mContext = context;
	}

	public static TTSController getInstance(Context context) {
		if (ttsManager == null) {
			ttsManager = new TTSController(context);
		}
		return ttsManager;
	}

	public void init() {
//		SpeechUser.getUser().login(mContext, null, null,
//				"appid=" + mContext.getString(R.string.app_id), listener);
		// 初始化合成对象.
//		mSpeechSynthesizer = SpeechSynthesizer.createSynthesizer(mContext);
//		initSpeechSynthesizer();
		// 初始化语音合成对象sand5g2nsc6a5bizpyc2tlzcuzdl646vdek5eti6
		getPackageInfo();
		mTTSControl = TTSFactory.createTTSControl(mContext, "");
		mTTSControl.setStreamType(AudioManager.STREAM_MUSIC);
		mTTSControl.initTTSEngine(mContext);
		mTTSControl.setTTSListener(mTTSPlayerListener);
//		String ttsSpeed = mUserPreference.getString(UserPreference.KEY_TTS_SPEED, UserPreference.TTS_SPEED_DEFAULT);
		String ttsSpeed ="TTS_SPEED_DEFAULT";
		Log.d(TAG, "speed in sp  : " + ttsSpeed);
		//由于TTS_SPEED_DEFAULT与TTS_SPEED_STANDARD都指代默认速度，需要更改
		String ttsSpeed_value = null;
		if("TTS_SPEED_DEFAULT".equals(ttsSpeed)){
			ttsSpeed_value = "standard";
		}else if("TTS_SPEED_SLOW".equals(ttsSpeed)){
			ttsSpeed_value = "slow";
		}else if("TTS_SPEED_FAST".equals(ttsSpeed)){
			ttsSpeed_value = "fast";
		}
		Log.d(TAG, "The init speed : " + ttsSpeed_value);
		if(ttsSpeed_value != null){
			setTTSSpeed(ttsSpeed_value);
		}
	}
	private void setTTSSpeed(String speed){		
		if(speed!=null&&speed.length()==0){
			Log.d(TAG, "TTS Speed value is empty");
			return;
		}
		if (speed.equals("slow")) {
			mTTSControl.setVoiceSpeed2(TTS_SPEED_SLOW);
			Log.d(TAG, "Slow speed : " + TTS_SPEED_SLOW);
		} else if (speed.equals("standard")) {
			mTTSControl.setVoiceSpeed2(TTS_SPEED_STANDARD);
			Log.d(TAG, "Default speed : " + TTS_SPEED_STANDARD);
		} else if (speed.equals("fast")) {
			mTTSControl.setVoiceSpeed2(TTS_SPEED_FAST);
			Log.d(TAG, "Fast speed : " + TTS_SPEED_FAST);
		}
	}
	/**
	 * 使用SpeechSynthesizer合成语音，不弹出合成Dialog.
	 * 
	 * @param
	 */
	public void playText(String playText) {
		if (!isfinish) {
			return;
		}
//		if (null == mSpeechSynthesizer) {
//			// 创建合成对象.
//			mSpeechSynthesizer = SpeechSynthesizer.createSynthesizer(mContext);
//			initSpeechSynthesizer();
//		}
//		// 进行语音合成.
//		mSpeechSynthesizer.startSpeaking(playText, this);
		playTTS(playText);

	}

	public void stopSpeaking() {
//		if (mSpeechSynthesizer != null)
//			mSpeechSynthesizer.stopSpeaking();
		if (mTTSControl != null){
			mTTSControl.cancel();
		}
	}
	public void startSpeaking() {
		 isfinish=true;
	}
	private TTSPlayerListener mTTSPlayerListener = new TTSPlayerListener() {

		@Override
		public void onTtsData(byte[] arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPlayEnd() {
//			LogUtil.d(TAG, "onPlayEnd");
//			showMessage(TalkService.TTS_EVENT_ON_PLAY_END);
			// unmuteMusicStream();
//			isfinish = true;
		}

		@Override
		public void onPlayBegin() {
			Log.d(TAG, "onPlayBegin");
//			showMessage(TalkService.TTS_EVENT_ON_PLAY_BEGIN);
//			isfinish =false;
			// muteMusicStream();
		}

		@Override
		public void onInitFinish() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onError(USCError arg0) {
//			showMessage(TalkService.TTS_EVENT_ON_ERROR);
//			isfinish = true;
		}

		@Override
		public void onCancel() {
//			showMessage(TalkService.TTS_EVENT_ON_CANCEL);
//			isfinish = true;
		}

		@Override
		public void onBuffer() {
//			showMessage(TalkService.TTS_EVENT_ON_BUFFER);
		}
	};

	/**
	 * 用户登录回调监听器.
	 */
//	private SpeechListener listener = new SpeechListener() {
//
//		@Override
//		public void onData(byte[] arg0) {
//		}
//
//		@Override
//		public void onCompleted(SpeechError error) {
//			if (error != null) {
//
//			}
//		}
//
//		@Override
//		public void onEvent(int arg0, Bundle arg1) {
//		}
//	};

//	@Override
//	public void onBufferProgress(int arg0, int arg1, int arg2, String arg3) {
//		// TODO Auto-generated method stub
//
//	}

	boolean isfinish = true;

//	@Override
//	public void onCompleted(SpeechError arg0) {
//		// TODO Auto-generated method stub
//		isfinish = true;
//	}

//	@Override
//	public void onSpeakBegin() {
//		// TODO Auto-generated method stub
//		isfinish = false;
//
//	}

//	@Override
//	public void onSpeakPaused() {
//		// TODO Auto-generated method stub
//
//	}

//	@Override
//	public void onSpeakProgress(int arg0, int arg1, int arg2) {
//		// TODO Auto-generated method stub
//
//	}

//	@Override
//	public void onSpeakResumed() {
//		// TODO Auto-generated method stub
//
//	}

	public static void destroy() {
//		if (mSpeechSynthesizer != null) {
//			mSpeechSynthesizer.stopSpeaking();
//		}
		if (mTTSControl != null) {
			mTTSControl.cancel();
			mTTSControl.releaseTTSEngine();
		}
		
		mTTSControl = null;
	}

	@Override
	public void onArriveDestination() {
		// TODO Auto-generated method stub
		this.playText("到达目的地");
	}

	@Override
	public void onArrivedWayPoint(int arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onCalculateRouteFailure(int arg0) {
		 this.playText("路径计算失败，请检查网络或输入参数");
	}

	@Override
	public void onCalculateRouteSuccess() {
		String calculateResult = "路径计算就绪";

		this.playText(calculateResult);
	}

	@Override
	public void onEndEmulatorNavi() {
		this.playText("导航结束");

	}

	@Override
	public void onGetNavigationText(int arg0, String arg1) {
		// TODO Auto-generated method stub
		this.playText(arg1);
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
	public void onReCalculateRouteForTrafficJam() {
		// TODO Auto-generated method stub
		this.playText("前方路线拥堵，路线重新规划");
	}

	@Override
	public void onReCalculateRouteForYaw() {

		this.playText("您已偏航");
	}

	@Override
	public void onStartNavi(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTrafficStatusUpdate() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onGpsOpenStatus(boolean arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNaviInfoUpdated(AMapNaviInfo arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNaviInfoUpdate(NaviInfo arg0) {
		  
		// TODO Auto-generated method stub  
		
	}
	
	private void playTTS(String text) {
		if (TextUtils.isEmpty(text)) {
			Log.w(TAG, "playTTS:text empty!");
			return;
		}
		showMessage(text);
	}

	private String mPackageName;
	private void getPackageInfo() {
		try {
			PackageInfo info = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
			mPackageName = info.packageName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			mPackageName = null;
		}
		LogUtil.i(TAG, "-getPackageName is--" + mPackageName);
	}
	
	private void showMessage(String message) {
		LogUtil.d(TAG, "showMessage: message " + message);
		Intent intent = new Intent("cn.yunzhisheng.intent.tts.text");
		intent.putExtra("text", message);
		mContext.sendBroadcast(intent);
	}
}
