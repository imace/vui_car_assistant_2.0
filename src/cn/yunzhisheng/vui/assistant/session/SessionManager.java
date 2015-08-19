package cn.yunzhisheng.vui.assistant.session;

import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import cn.yunzhisheng.common.DataTool;
import cn.yunzhisheng.common.JsonTool;
import cn.yunzhisheng.common.util.LogUtil;
import cn.yunzhisheng.phone.PhoneStateReceiver;
import cn.yunzhisheng.phone.Telephony;
import cn.yunzhisheng.tts.offline.basic.TTSPlayerListener;
import cn.yunzhisheng.tts.offline.common.USCError;
import cn.yunzhisheng.vui.assistant.VoiceAssistant;
import cn.yunzhisheng.vui.assistant.WindowService;
import com.ilincar.voice.R;
import cn.yunzhisheng.vui.assistant.model.KnowledgeMode;
import cn.yunzhisheng.vui.assistant.oem.RomSystemSetting;
import cn.yunzhisheng.vui.assistant.preference.SessionPreference;
import cn.yunzhisheng.vui.assistant.preference.UserPreference;
import cn.yunzhisheng.vui.assistant.sms.SmsItem;
import cn.yunzhisheng.vui.assistant.sms.SmsNewObserver;
import cn.yunzhisheng.vui.assistant.sms.SmsNewObserver.ISMSReceiver;
import cn.yunzhisheng.vui.assistant.talk.ITalkServicePresentorListener;
import cn.yunzhisheng.vui.assistant.talk.ITalkServiceWakeupListener;
import cn.yunzhisheng.vui.assistant.talk.TalkService;
import cn.yunzhisheng.vui.assistant.talk.TalkServicePresentor;
import cn.yunzhisheng.vui.assistant.util.AudioUtil;
import cn.yunzhisheng.vui.assistant.util.BeepPlayer;
import cn.yunzhisheng.vui.assistant.view.FunctionView;
import cn.yunzhisheng.vui.assistant.view.MicrophoneControl;
import cn.yunzhisheng.vui.assistant.view.ReceiveCallView;
import cn.yunzhisheng.vui.assistant.view.ReceiveSMSView;
import cn.yunzhisheng.vui.assistant.view.SessionContainer;
import cn.yunzhisheng.vui.assistant.view.SessionContainer.ITTSStatusListener;

/**
 * @Module : Session层核心类
 * @Comments : 根据协议进行Session生成和协议分发，该类为Session层的核心枢纽
 * @Author : Dancindream
 * @CreateDate : 2014-4-1
 * @ModifiedBy : Dancindream
 * @ModifiedDate: 2014-4-1
 * @Modified: 2014-4-1: 实现基本功能
 */
@SuppressWarnings("deprecation")
/*
 * TTS完成度:接听电话,产品形态
 */
@SuppressLint("HandlerLeak")
public class SessionManager implements ITalkServicePresentorListener {
	private static final String TAG = "SessionManager";

	private static final int TALK_STATUS_IDLE = 0x01;
	private static final int TALK_STATUS_LISTENING = 0x02;
	private static final int TALK_STATUS_WAITING = 0x03;

	public static final int TTS_STATUS_IDLE = 0x01;
	public static final int TTS_STATUS_BUFFER = 0x02;
	public static final int TTS_STATUS_PLAYING = 0x03;

	private static final int FIELD_TYPE_NORMAL = -1010000111;
	private static final int FIELD_TYPE_SMS = -1010000112;
	private static final int FIELD_TYPE_CALL = -1010000113;
	private int FIELD_TYPE = FIELD_TYPE_NORMAL;

	private static final int mTalkType_NORMAL = 0;
	private static final int mTalkType_SMS = 1;
	private static final int mTalkType_CALL = 2;
	private int mTalkType = mTalkType_NORMAL;

	WindowService mWindowService = null;
	private FunctionView mFunctionView = null;
	private ReceiveSMSView mReceiveSMSView = null;
	private ReceiveCallView mReceiveCallView = null;
	private SessionContainer mSessionViewContainer = null;
	private MicrophoneControl mMicrophoneControl = null;
	private int mCurrentTalkStatus = TALK_STATUS_IDLE;
	private TalkServicePresentor mTalkServicePresentor = null;
	private BaseSession mCurrentSession = null;
	private BeepPlayer mBeepPlayer = null;

	private UserPreference mPreference;

	public static boolean mIsFirstInitDone = false;
	private boolean mWakeupRecordingState;
	private boolean mWakeupResult;
	private boolean mRecognitionIintDone;
	private boolean mWakeUpInitDone;
	private boolean mRecognitionDataDone;
	private boolean mRecognitionRecordingState;
	private NotificationManager mNotificationManager;
	private boolean isStartTlak = false; // 标志是否开始录音 用于觉得是否发送广播

	private PowerManager mPowerManager;
	private WakeLock mWakeLock;
	private AudioManager mAudioManager;
	private boolean mLastSessionDone = false;
	private Runnable mTTSEndRunnable;
	private SmsNewObserver mSMSObserver;
	private SmsItem mReveivedMessage;

	private String mName, mNumber;
	private UserPreference mUserPreference;
	private int mPhoneState;

	private String mSessionStatus = "";
	private String mType = "";

	public final static String TYPE_COMMAND = "TYPE_COMMAND";
	public final static String TYPE_MUTIPLE = "TYPE_MUTIPLE";
	public final static String TYPE_FREETEXT = "TYPE_FREETEXT";

	View loadingView;
	/**
	 * @Description : TODO 提供主线程的方法调用
	 * @Author : Dancindream
	 * @CreateDate : 2014-4-1
	 */
	private Handler mSessionManagerHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			LogUtil.d(
					TAG,
					"handleMessage:what "
							+ SessionPreference.getMessageName(msg.what));
			switch (msg.what) {
			// TODO 开始语音识别流程
			case SessionPreference.MESSAGE_START_TALK:
				if (mWakeupListener != null) {
					mWakeupListener.onSuccess();
				}
				break;
			// TODO 结束语音录音
			case SessionPreference.MESSAGE_STOP_TALK:
				stopTalk();
				break;
			// TODO 取消语音识别流程
			case SessionPreference.MESSAGE_CANCEL_TALK:
				cancelTalk(true);
				break;
			// TODO 取消Session操作流程
			case SessionPreference.MESSAGE_SESSION_CANCEL:
				setSessionEnd(true);
				releaseCurrentSession();
				releaseSessionCenterSession();
				releaseWakeLock();
				requestStartWakeup();
				if (mPhoneState == TelephonyManager.CALL_STATE_IDLE) {
					resetRingMode();
				}
				break;
			// TODO Session操作流程已完成
			case SessionPreference.MESSAGE_SESSION_DONE:
				setSessionEnd(true);
				releaseCurrentSession();
				releaseSessionCenterSession();
				releaseWakeLock();
				requestStartWakeup();
				mSessionViewContainer.removeAllSessionViews();
				sendEmptyMessage(SessionPreference.MESSAGE_DISMISS_WINDOW);
				if (mPhoneState == TelephonyManager.CALL_STATE_IDLE) {
					resetRingMode();
				}
				break;
			// TODO 设置麦克风为不可用状态
			case SessionPreference.MESSAGE_DISABLE_MICROPHONE:
				mMicrophoneControl.setEnabled(false);
				break;
			// TODO 设置麦克风为可用状态
			case SessionPreference.MESSAGE_ENABLE_MICROPHONE:
				mMicrophoneControl.setEnabled(true);
				break;
			// TODO 播报文字
			case SessionPreference.MESSAGE_REQUEST_PLAY_TTS:
				Bundle bundle = msg.getData();
				Runnable r = null;
				if (msg.obj != null) {
					r = (Runnable) msg.obj;
				}
				playTTSWithEndRunnable(
						bundle.getString(SessionPreference.KEY_TEXT), r);
				break;
			// TODO 取消播报文字
			case SessionPreference.MESSAGE_REQUEST_CANCEL_TTS:
				cancelTTS();
				break;
			// TODO 直接跳转到新的Session
			case SessionPreference.MESSAGE_NEW_PROTOCAL:
				if (mCurrentSession != null) {
					mCurrentSession.release();
					mCurrentSession = null;
				}
				cancelTTS();
				String strProtocolString = (String) msg.obj;
				createSession(strProtocolString);
				break;
			// TODO VAD方式自动结束录音
			case SessionPreference.MESSAGE_VAD_RECORD_STOP:
				mMicrophoneControl.setEnabled(true);
				break;
			// TODO 添加答句文字（语音魔方回答的文字）
			case SessionPreference.MESSAGE_ADD_ANSWER_TEXT:
				String text = (String) msg.obj;
				// mSessionViewContainer.addAnswerView(text);
				mMicrophoneControl.setAnswerText(text);
				break;
			// TODO 添加回答的View（语音魔方需要展现的View）
			case SessionPreference.MESSAGE_ADD_ANSWER_VIEW:
				View view = (View) msg.obj;
				boolean fullScroll = msg.arg1 != 0;
				mSessionViewContainer.removeAllSessionViews();
				mSessionViewContainer.addSessionView(view, fullScroll);
				// LayoutParams params = new LayoutParams(width, height,
				// gravity);
				// mSessionViewContainer.addSessionView(view, params);
				break;
			// TODO 添加问句文字（用户说的话）
			case SessionPreference.MESSAGE_ADD_QUESTION_TEXT:
				// mSessionViewContainer.addQustionView((String) msg.obj);
				mMicrophoneControl.setAnswerText((String) msg.obj);
				break;
			// TODO 返还给语音魔方的协议（用于业务流程跳转）
			case SessionPreference.MESSAGE_UI_OPERATE_PROTOCAL:

				String protocal = (String) msg.obj;
				LogUtil.d(TAG, "MESSAGE_UI_OPERATE_PROTOCAL protocal : "
						+ protocal);

				JSONObject jsonObj = null;

				try {
					jsonObj = new JSONObject(protocal);
				} catch (JSONException e) {
					e.printStackTrace();
					return;
				}
				if (jsonObj.has("confirm")) {
					mTalkServicePresentor.setProtocal(protocal);
					String confirm = JsonTool.getJsonValue(jsonObj, "confirm");
					if ("cancel".equals(confirm)) {
						cancelSession();
					}
				} else if (jsonObj.has("select")) {
					cancelTalk(false);
					mTalkServicePresentor.setProtocal(protocal);
					setSessionEnd(true);
				} else {
					mTalkServicePresentor.setProtocal(protocal);
				}
				break;
			case SessionPreference.MESSAGE_DISMISS_WINDOW:
				if (mWindowService != null) {
					mWindowService.dismiss();
				}
				break;
			case SessionPreference.MESSAGE_SHOW_HELP_VIEW:
				showHelpView();
				break;
			case SessionPreference.MESSAGE_PLAY_REVEIVED_MESSAGE:
				if (mReveivedMessage != null) {
					FIELD_TYPE = FIELD_TYPE_SMS;
					showHelpView();
					String tts = mWindowService.getString(R.string.sms_is,
							mReveivedMessage.getMessage());
					playTTS(tts);
				} else {
					String tts = mWindowService.getString(R.string.sms_no_new);
					playTTS(tts);
				}
				break;

			case SessionPreference.MESSAGE_PLAY_BEEP_SOUND:
				if (mBeepPlayer != null) {
					mBeepPlayer.playBeepSound(msg.arg1, false, null);
				} else {
					LogUtil.w(TAG, "mBeepPlayer null!");
				}
				break;
			case SessionPreference.MESSAGE_ANSWER_CALL:
				setSessionEnd(true);
				cancelSession();
				if (mPhoneState == TelephonyManager.CALL_STATE_IDLE) {
					resetRingMode();
				}
				break;
			}
		}

	};

	private void resetRingMode() {
		if (mAudioManager != null) {
			mAudioManager.setStreamMute(AudioManager.STREAM_RING, false);
		}
	}

	private PhoneStateListener mPhoneStateListener = new PhoneStateListener() {

		public void onCallStateChanged(int state, String incomingNumber) {
			LogUtil.d(TAG, "onCallStateChanged:state " + state
					+ ",incomingNumber " + incomingNumber + ",state : " + state);
			String text = "";

			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:
				LogUtil.d(TAG, "--TelephonyManager.CALL_STATE_RINGING--");
				cancelSession();
				LogUtil.d(
						TAG,
						"STREAM_MUSIC Volume before:"
								+ mAudioManager
										.getStreamVolume(AudioManager.STREAM_MUSIC));
				LogUtil.d(
						TAG,
						"STREAM_RING Volume before:"
								+ mAudioManager
										.getStreamVolume(AudioManager.STREAM_RING));
				mAudioManager.setStreamMute(AudioManager.STREAM_RING, true);
				LogUtil.d(
						TAG,
						"STREAM_MUSIC Volume after:"
								+ mAudioManager
										.getStreamVolume(AudioManager.STREAM_MUSIC));
				LogUtil.d(
						TAG,
						"STREAM_RING Volume after:"
								+ mAudioManager
										.getStreamVolume(AudioManager.STREAM_RING));
				String personName = mTalkServicePresentor
						.getName(incomingNumber);
				if (TextUtils.isEmpty(personName)) {
					text = incomingNumber
							+ mWindowService
									.getString(R.string.phone_answer_cancel);
					mName = mWindowService.getString(R.string.stranger);
				} else {
					text = personName
							+ mWindowService
									.getString(R.string.phone_answer_cancel);
					mName = personName;
				}

				LogUtil.d(TAG, "--personName-->" + personName);
				FIELD_TYPE = FIELD_TYPE_CALL;
				mTalkType = mTalkType_CALL;
				mNumber = incomingNumber;
				playTTSWithEndRunnable(text, new Runnable() {
					@Override
					public void run() {
						mWakeupListener.onSuccess();
					}
				});

				// mAudioManager.setStreamVolume(AudioManager.STREAM_RING,
				// currentVolume, 0);
				// mAudioManager.setStreamMute(AudioManager.STREAM_RING, false);
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:
				LogUtil.d(TAG, "--TelephonyManager.CALL_STATE_OFFHOOK--");
				// mAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
				mAudioManager.setStreamMute(AudioManager.STREAM_RING, false);
				break;
			case TelephonyManager.CALL_STATE_IDLE:
				LogUtil.d(TAG, "--TelephonyManager.CALL_STATE_IDLE--");
				FIELD_TYPE = FIELD_TYPE_NORMAL;
				if (mPhoneState == TelephonyManager.CALL_STATE_RINGING) {
					cancelTalk(false);
					text = mWindowService.getString(R.string.call_canceled);
					mMicrophoneControl.setAnswerText(text);

					playTTSWithEndRunnable(text, new Runnable() {
						@Override
						public void run() {
							cancelSession();
						}
					});
				}
				// mAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
				mAudioManager.setStreamMute(AudioManager.STREAM_RING, false);
				break;
			}
			mPhoneState = state;
		};
	};

	private ISMSReceiver mMessageReveiverListener = new ISMSReceiver() {

		@Override
		public void onMessageReveived(SmsItem msg) {
			// cancelSession();
			LogUtil.d(TAG, "onMessageReveived:msg " + msg);
			mReveivedMessage = msg;
			String tts = "";
			String name = msg.getName();
			if (name != null && !name.equals("")) {
				tts = msg.getName();
			} else {
				tts = msg.getNumber();
				mReveivedMessage.setNumber(tts);
			}
			LogUtil.d(TAG,
					"--Receive message--msg.getName()-->" + msg.getName());
			if (!Telephony.phoneIsInUse(mWindowService)) {
				FIELD_TYPE = FIELD_TYPE_SMS;
				mTalkType = mTalkType_SMS;
				// showHelpView();
				LogUtil.d(TAG,
						mWindowService.getString(R.string.no_phone_run_sms));

				playTTSWithEndRunnable(mWindowService.getString(
						R.string.mms_from_confirm, tts), new Runnable() {
					@Override
					public void run() {
						mWakeupListener.onSuccess();
					}
				});
			}
		}
	};

	/**
	 * @Description : TODO 播报TTS的回调
	 * @Author : Dancindream
	 * @CreateDate : 2014-4-1
	 */
	private TTSPlayerListener mTTSListener = new TTSPlayerListener() {

		/**
		 * @Description : TODO TTS开始播报
		 * @Author : Dancindream
		 * @CreateDate : 2014-4-1
		 * @see cn.yunzhisheng.tts.offline.TTSPlayerListener#onPlayBegin()
		 */
		@Override
		public void onPlayBegin() {
			mSessionViewContainer.onTTSPlay();
		}

		/**
		 * @Description : TODO TTS播报结束
		 * @Author : Dancindream
		 * @CreateDate : 2014-4-1
		 * @see cn.yunzhisheng.tts.offline.TTSPlayerListener#onPlayEnd()
		 */
		@Override
		public void onPlayEnd() {
			// //恢复系统静音 add by ch
			// RomSystemSetting.setVolume(mWindowService, currentVolume);
			if (isStartTlak) {
				isStartTlak = false;
				// TODO 4.执行恢复静音
				addaudio();
			}
			LogUtil.d(TAG, "onPlayEnd: mCurrentSession " + mCurrentSession
					+ ";mTTSEndRunnable : " + mTTSEndRunnable);
			if (mPreference.getBoolean(UserPreference.KEY_ENABLE_TTS, true)) {
				mSessionViewContainer.onTTSStop();
			} else {
				mSessionViewContainer.onTTSUnenabled();
			}
			if (mCurrentSession != null) {
				mCurrentSession.onTTSEnd();

			} else if (mTTSEndRunnable != null) {
				LogUtil.d(TAG, "mTTSEndRunnable is not null");
				mTTSEndRunnable.run();
				mTTSEndRunnable = null;
			}

			// TODO
			// mTTSEndRunnable为空的时候，需要releaseSession dismisWindow cancel
			// startWakeup
			else if (mTTSEndRunnable == null) {
				cancelSession();
			}
		}

		/**
		 * @Description : TODO TTS缓冲中
		 * @Author : Dancindream
		 * @CreateDate : 2014-4-1
		 * @see cn.yunzhisheng.tts.offline.TTSPlayerListener#onBuffer()
		 */
		@Override
		public void onBuffer() {
			mSessionViewContainer.onTTSBuffer();
		}

		/**
		 * @Description : TODO TTS被取消
		 * @Author : Dancindream
		 * @CreateDate : 2014-4-1
		 * @see cn.yunzhisheng.tts.offline.TTSPlayerListener#onCancel()
		 */
		@Override
		public void onCancel() {
			if (mPreference.getBoolean(UserPreference.KEY_ENABLE_TTS, true)) {
				mSessionViewContainer.onTTSStop();
			} else {
				mSessionViewContainer.onTTSUnenabled();
			}
		}

		/**
		 * @Description : TODO TTS异常回调
		 * @Author : Dancindream
		 * @CreateDate : 2014-4-1
		 * @see cn.yunzhisheng.tts.offline.TTSPlayerListener#onError(cn.yunzhisheng.tts.offline.common.USCError)
		 */
		@Override
		public void onError(USCError arg0) {

		}

		/**
		 * @Description : TODO TTS初始化完成
		 * @Author : Dancindream
		 * @CreateDate : 2014-5-10
		 * @see cn.yunzhisheng.tts.offline.TTSPlayerListener#onInitFinish()
		 */
		@Override
		public void onInitFinish() {

		}

		@Override
		public void onTtsData(byte[] arg0) {
		}
	};

	/**
	 * @Description : TODO 唤醒功能抽象回调
	 * @Author : Dancindream
	 * @CreateDate : 2014-4-1
	 */
	private ITalkServiceWakeupListener mWakeupListener = new ITalkServiceWakeupListener() {
		@Override
		public void onSuccess() {
			LogUtil.d(TAG, "mWakeupListener.onSuccess");
			mWakeupResult = true;
			if (mWakeupRecordingState) {
				mTalkServicePresentor.stopWakeup();
			} else {
				startTalk();
			}
		}

		@Override
		public void onStop() {
			LogUtil.d(TAG, "mWakeupListener.onStop");
			mWakeupRecordingState = false;
			cancelWakeupNotification();
			if (mWakeupResult) {
				requestStartTalk();
			}
		}

		@Override
		public void onStart() {
			LogUtil.d(TAG, "mWakeupListener.onStart");
			mWakeupRecordingState = true;
			mWakeupResult = false;
			notifyWakeupRunning();
		}

		@Override
		public void onInitDone() {
			LogUtil.d(TAG, "mWakeupListener.onInitDone");
			mWakeUpInitDone = true;
			requestStartWakeup();
		}

		@Override
		public void onError(int code, String message) {
			LogUtil.e(TAG, "mWakeupListener.onError: code " + code
					+ ", message " + message);
			mWakeupResult = false;
			mTalkServicePresentor.stopWakeup();
		}
	};

	public SessionManager(WindowService windowService,
			SessionContainer sessionViewContainer,
			MicrophoneControl microphoneControl) {
		mWindowService = windowService;
		mUserPreference = new UserPreference(mWindowService);
		mSessionViewContainer = sessionViewContainer;
		mMicrophoneControl = microphoneControl;
		mPowerManager = (PowerManager) windowService
				.getSystemService(Context.POWER_SERVICE);
		mAudioManager = (AudioManager) windowService
				.getSystemService(Context.AUDIO_SERVICE);
		init();
		setListener();
		LogUtil.DEBUG = true;
	}

	private void init() {
		mNotificationManager = (NotificationManager) mWindowService
				.getSystemService(Context.NOTIFICATION_SERVICE);
		mTalkServicePresentor = new TalkServicePresentor(mWindowService, this,
				mWakeupListener, mTTSListener);
		mPreference = new UserPreference(mWindowService);
		mBeepPlayer = new BeepPlayer(mWindowService);
		mBeepPlayer.setVolume(0.3f);
		mSMSObserver = new SmsNewObserver(mWindowService);

		loadingView = View.inflate(mWindowService, R.layout.view_loading, null);
		/*
		 * mSessionViewContainer.addAnswerView(KnowledgeMode.getKnowledgeAnswer(
		 * mWindowService, KnowledgeMode.KNOWLEDGE_STAGE_HELP));
		 */

		showHelpView();
	}

	public void showInitView() {
		if ((TalkService.TALK_INITDONE | mRecognitionIintDone)
				&& mRecognitionDataDone) {
			mWindowService.dimissView(loadingView);
			LogUtil.d(TAG, "showInitView dismiss");
		} else {
			LogUtil.d(TAG, "showInitView removeAllSessionViews");
			mSessionViewContainer.removeAllSessionViews();
			mMicrophoneControl.setVisibility(View.GONE);
			mMicrophoneControl.onPrepare();
			mSessionViewContainer.removeAllSessionViews();
			TextView tx = (TextView) loadingView.findViewById(R.id.init_text);
			tx.setText(R.string.grammar_data_compile);
			mWindowService.addPrepareView(loadingView);
		}
	}

	private void showHelpView() {
		LogUtil.d(TAG, "showHelpView: FIELD_TYPE " + FIELD_TYPE);
		View view = null;
		mSessionViewContainer.removeAllSessionViews();

		if (FIELD_TYPE == FIELD_TYPE_NORMAL) {
			mWindowService.hideCancelBtn(true);
			view = getFunctionView();
			view.setVisibility(View.VISIBLE);
		} else if (FIELD_TYPE == FIELD_TYPE_SMS) {
			view = getReceiveSMSView();
		} else if (FIELD_TYPE == FIELD_TYPE_CALL) {
			mWindowService.hideCancelBtn(true);
			view = getReceiveCALLView();
		}

		FIELD_TYPE = FIELD_TYPE_NORMAL;
		Message msg = new Message();

		if (view != null) {
			LogUtil.d(TAG, "--view != null--");
			// SessionContainer.addViewNow(view.hashCode());
			msg.what = SessionPreference.MESSAGE_ADD_ANSWER_VIEW;
			msg.obj = view;
		} else {
			LogUtil.d(TAG, "--view == null--");
			msg.what = SessionPreference.MESSAGE_SESSION_CANCEL;
		}
		mSessionManagerHandler.sendMessage(msg);
	}

	private void setListener() {
		mSessionViewContainer.setTTSListener(new ITTSStatusListener() {

			@Override
			public void onStatusChanged(int oldStatus, String obj) {
				switch (oldStatus) {
				case SessionContainer.UNENABLED:
					mPreference.putBoolean(UserPreference.KEY_ENABLE_TTS, true);
					mSessionViewContainer.onTTSStop();
					break;
				case SessionContainer.STOPPED:
					// playTTS(obj);
					break;
				case SessionContainer.BUFFERING:
					// cancelTTS();
					break;
				case SessionContainer.PLAYING:
					mPreference
							.putBoolean(UserPreference.KEY_ENABLE_TTS, false);
					// cancelTTS();
					break;
				}
			}
		});

		mMicrophoneControl.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				LogUtil.d(TAG, "mMicrophoneControl on click: "
						+ mCurrentTalkStatus);
				LogUtil.d(TAG, "v.getId():-->" + v.getId());
				LogUtil.d(TAG, "R.id.cancelBtn" + R.id.cancelBtn);
				if (v.getId() == R.id.cancelBtn) {
					cancelSession();
					playTTS(mWindowService.getString(R.string.operation_cancel));
				} else {
					switch (mCurrentTalkStatus) {
					case TALK_STATUS_IDLE:
						if (mWakeupRecordingState) {
							// 模拟唤醒成功
							mWakeupResult = true;
							mTalkServicePresentor.stopWakeup();
						} else {
							requestStartTalk();
						}
						break;
					case TALK_STATUS_LISTENING:
						stopTalk();
						break;
					case TALK_STATUS_WAITING:
						cancelTalk(true);
						Toast.makeText(mWindowService,
								mWindowService.getString(R.string.click_mic),
								Toast.LENGTH_SHORT).show();
						break;
					}
				}
			}
		});

		PhoneStateReceiver.registerPhoneStateListener(mPhoneStateListener);
		mSMSObserver.setMessageReveiverListener(mMessageReveiverListener);
		mSMSObserver.registReceiver();
	}

	private void disableKeyguard() {
		KeyguardManager km = (KeyguardManager) mWindowService
				.getSystemService(Context.KEYGUARD_SERVICE);
		KeyguardLock kl = km.newKeyguardLock("WakeupKeyguard");
		kl.disableKeyguard();
	}

	private void acquireWakeLock(int flags, String tag) {
		if (mWakeLock == null || !mWakeLock.isHeld()) {
			LogUtil.d(TAG, "acquireWakeLock:flags " + flags + ",tag " + tag);
			mWakeLock = mPowerManager.newWakeLock(flags, tag);
			mWakeLock.acquire();
		}
	}

	// 释放设备电源锁i
	private void releaseWakeLock() {
		LogUtil.d(TAG, "releaseWakeLock");
		if (mWakeLock != null && mWakeLock.isHeld()) {
			mWakeLock.release();
			mWakeLock = null;
		}
	}

	private void notifyWakeupRunning() {
		LogUtil.d(TAG, "notifyWakeupRunning");
		String tickerText = mWindowService.getString(R.string.wakeup_started);
		Notification notification = new Notification(R.drawable.ic_launcher,
				tickerText, System.currentTimeMillis());
		notification.flags = Notification.FLAG_ONGOING_EVENT;

		// Intent intent = new Intent(mWindowService, MainActivity.class);
		// PendingIntent pi = PendingIntent.getActivity(this, 0, intent,
		// PendingIntent.FLAG_UPDATE_CURRENT);

		notification.setLatestEventInfo(mWindowService, tickerText,
				mWindowService.getString(R.string.wakeup_started_prompt), null);
		mNotificationManager.notify(0, notification);
	}

	private void cancelWakeupNotification() {
		mNotificationManager.cancel(0);
	}

	/**
	 * @Description : TODO 主动启动语音识别流程
	 * @Author : Dancindream
	 * @CreateDate : 2014-4-1
	 */
	private void startTalk() {
		LogUtil.d(TAG, "startTalk");
		cancelTTS();
		cancelTalk(false);
		// playTTS("请说语音指令");
		mWindowService.show();

		if (mTalkServicePresentor != null) {

			if (mTalkType == mTalkType_NORMAL) {
				if (mType.startsWith("MUTIPLE")) {
					mTalkServicePresentor.setRecognizerType(TYPE_MUTIPLE);
				} else if (mType.startsWith("INPUT_FREETEXT")) {
					mTalkServicePresentor.setRecognizerType(TYPE_FREETEXT);
				} else {
					mTalkServicePresentor.setRecognizerType(TYPE_COMMAND);
				}
				mType = "";
			} else {
				mTalkServicePresentor.setRecognizerType(TYPE_MUTIPLE);
			}
			mCurrentTalkStatus = TALK_STATUS_LISTENING;
			mBeepPlayer.playBeepSound(R.raw.start_tone, false, null);
			// mMicrophoneControl.onRecording();
			mTalkServicePresentor.startTalk();
			acquireWakeLock(PowerManager.FULL_WAKE_LOCK
					| PowerManager.ACQUIRE_CAUSES_WAKEUP
					| PowerManager.ON_AFTER_RELEASE, "SessionWakeLock");
			disableKeyguard();
		} else {
			LogUtil.w(TAG, "mTalkServicePresentor null!");
			mBeepPlayer.playBeepSound(R.raw.error_tone, false, null);
			mMicrophoneControl.setAnswerText(mWindowService
					.getString(R.string.unknown_error));
		}
	}

	public void startTalk(String from) {
		LogUtil.d(TAG, "startTalk:from " + from);
		requestStartTalk();
	}

	/**
	 * @Description : TODO 主动停止录音
	 * @Author : Dancindream
	 * @CreateDate : 2014-4-1
	 */
	public void stopTalk() {
		LogUtil.d(TAG, "stopTalk");
		mBeepPlayer.stop();
		// sound.stop(int_sound);
		if (mTalkServicePresentor != null) {
			mTalkServicePresentor.stopTalk();
		}
		waitForRecognitionResult();
	}

	/**
	 * @Description : TODO 主动取消识别流程
	 * @Author : Dancindream
	 * @CreateDate : 2014-4-1
	 */
	public boolean cancelTalk(boolean callback) {
		LogUtil.d(TAG, "cancelTalk: callback " + callback);

		/*
		 * if (!mRecognitionRecordingState) { return false; }
		 */
		mBeepPlayer.stop();
		resetTalk();
		if (mTalkServicePresentor != null) {
			mTalkServicePresentor.cancelTalk(callback);
		}
		if (!callback) {
			mRecognitionRecordingState = false;
		}
		return true;
	}

	public void releaseSessionCenterSession() {
		LogUtil.d(TAG, "releaseSessionCenterSession");
		if (mTalkServicePresentor != null) {
			mTalkServicePresentor.releaseSession();
		}
	}

	public void cancelSession() {
		LogUtil.d(TAG, "cancelSession");
		if (mTalkType == mTalkType_NORMAL) {
			mTalkServicePresentor.setRecognizerType(TYPE_COMMAND);
		}
		mTTSEndRunnable = null;
		releaseSessionCenterSession();
		releaseCurrentSession();
		cancelTalk(false);
		cancelTTS();
		mBeepPlayer.stop();
		// 当收到MESSAGE_SESSION_DONE的消息再removeView，否则只发消息的session将不会removeView
		// mSessionViewContainer.removeAllSessionViews();
		mSessionManagerHandler
				.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_DONE);
		mSessionManagerHandler
				.sendEmptyMessage(SessionPreference.MESSAGE_DISMISS_WINDOW);
	}

	public void cancelSessionWithTTS() {
		LogUtil.d(TAG, "cancelSession with TTS");
		cancelSession();
		playTTS(mWindowService.getString(R.string.operation_cancel));
	}

	/**
	 * @Description : TODO 等待识别结果流程
	 * @Author : Dancindream
	 * @CreateDate : 2014-4-1
	 */
	private void waitForRecognitionResult() {
		LogUtil.d(TAG, "waitForRecognitionResult");

		/*
		 * if (TALK_STATUS_WAITING == mCurrentTalkStatus) { return; }
		 */

		mCurrentTalkStatus = TALK_STATUS_WAITING;
		mBeepPlayer.playBeepSound(R.raw.wait_tone, true, null);

		// sound.play(int_wait, 1, 1, 0, 0, 1);
		mMicrophoneControl.onProcess();
		mMicrophoneControl.setEnabled(true);
	}

	/**
	 * @Description : 恢复初始状态
	 * @Author : Dancindream
	 * @CreateDate : 2014-4-1
	 */
	public void resetTalk() {
		LogUtil.d(TAG, "resetTalk");
		if (mCurrentTalkStatus == TALK_STATUS_IDLE) {
			return;
		}

		mCurrentTalkStatus = TALK_STATUS_IDLE;
		mMicrophoneControl.onIdle(false);
		mMicrophoneControl.setEnabled(true);
	}

	public void onPause() {
		LogUtil.d(TAG, "onPause");
		cancelTTS();
		mBeepPlayer.stop();
		// sound.stop(int_sound);
		cancelTalk(false);

		// if (mTalkServicePresentor != null) {
		// mTalkServicePresentor.onPause();
		// mTalkServicePresentor.stopWakeup();
		// }

	}

	public void onResume() {
		LogUtil.d(TAG, "onResume");
		// if (mTalkServicePresentor != null) {
		// mTalkServicePresentor.onResume();
		// requestStartWakeup(mWakeupInitDone, mRecognitionInitDone,
		// mRecognitionRecordingState);
		// }
	}

	// 初始化完成
	@Override
	public void onTalkInitDone() {
		LogUtil.d(TAG, "onTalkInitDone");
		mRecognitionIintDone = true;
		mMicrophoneControl.onIdle(true);
		mMicrophoneControl.setEnabled(true);

		if (mFunctionView != null) {
			mFunctionView.initFunctionViews();
		}
	}

	// 语法数据编译完成
	@Override
	public void onTalkDataDone() {
		LogUtil.d(TAG, "onTalkDataDone mIsFirstInitDone : " + mIsFirstInitDone);
		mRecognitionDataDone = true;
		if ((TalkService.TALK_INITDONE | mRecognitionIintDone)
				&& mRecognitionDataDone) {
			if (loadingView.isShown()) {
				LogUtil.d(TAG, "loadingView.isShown()");
				mWindowService.dimissView(loadingView);
			} else {
				mWindowService.startFloatFirst();
			}

			if (mIsFirstInitDone) {
				requestStartWakeup();
			}
		}
	}

	@Override
	public void onTalkStart() {
		LogUtil.d(TAG, "onTalkStart");
		mWindowService.show();
		mMicrophoneControl.onPrepare();
		mMicrophoneControl.setEnabled(true);
	}

	@Override
	public void onTalkStop() {
		LogUtil.d(TAG, "onTalkStop");
		mBeepPlayer.playBeepSound(R.raw.stop_tone, false, null);
		// sound.play(int_stop, 1, 1, 0, 0, 1);
		waitForRecognitionResult();
	}

	@Override
	public void onTalkRecordingStart() {
		// 静音 用于music通道 add by ch
		isStartTlak = true;
		// TODO 1.执行静音
		if (AudioUtil.getcurrentAudio(mWindowService, mAudioManager) > 0) {
			systemaudio();
		}
		mRecognitionRecordingState = true;
		mMicrophoneControl.onRecording();
	}

	@Override
	public void onTalkRecordingStop() {
		// 恢复静音
		// RomSystemSetting.setVolume(mWindowService, currentVolume);
		// 发送静音的广播 用于处理视频 录音机等
		if (isStartTlak) {
			isStartTlak = false;
			// TODO 2.执行恢复静音
			addaudio();
		}
		mRecognitionRecordingState = false;
	}

	@Override
	public void onTalkCancel() {
		// 恢复静音
		try {
			if (isStartTlak) {
				isStartTlak = false;
				// TODO 3.执行恢复静音
				addaudio();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// unmuteSystemStream();
		// RomSystemSetting.setVolume(mWindowService, currentVolume);
		LogUtil.d(TAG, "onTalkCancel");
		mRecognitionRecordingState = false;
		cancelSessionWithTTS();
	}

	@Override
	public void onTalkResult(String result) {
		// 语音识别结果
		LogUtil.d(TAG, "onTalkResult: result " + result);
	}

	@Override
	public void onSessionProtocal(String protocol) {
		LogUtil.d(TAG, "onSessionProtocal: protocol " + protocol);
		resetTalk();
		if (protocol != null && (protocol.indexOf("\"type\":\"WAITING\"") < 0)) {
			mBeepPlayer.playBeepSound(R.raw.stop_tone, false, null);
		}

		createSession(protocol);
	}

	/**
	 * 
	 * @Description : onActiveStatusChanged
	 * @Author : Brant
	 * @CreateDate : 2014-11-25
	 * @see cn.yunzhisheng.vui.assistant.talk.ITalkServicePresentorListener#onActiveStatusChanged(int)
	 * @param flag
	 *            :0注册,1限制注册,2不允许注册,4没有试用过,5,正在试用,6,试用期已过
	 */
	@Override
	public void onActiveStatusChanged(int flag) {
		LogUtil.d(TAG, "onActiveStatusChanged:flag " + flag);
	}

	@Override
	public void isActive(boolean status) {
		LogUtil.d(TAG, "talk service presentor listener is active: " + status
				+ "");
	}

	private void playTTS(String tts) {
		LogUtil.d(TAG, "playTTS:tts " + tts);
		mTalkServicePresentor.playTTS(tts);
	}

	private void playTTSWithEndRunnable(String tts, Runnable runnable) {
		mTTSEndRunnable = runnable;
		playTTS(tts);
	}

	private void cancelTTS() {
		addaudio();
		mTalkServicePresentor.cancelTTS();
	}

	private void requestStartTalk() {
		LogUtil.d(TAG, "requestStartTalk");

		if (mTalkServicePresentor != null) {
			if (mTalkType == mTalkType_NORMAL) {
				if (mType.startsWith("MUTIPLE")) {
					mTalkServicePresentor.setRecognizerType(TYPE_MUTIPLE);
				} else if (mType.startsWith("INPUT_FREETEXT")) {
					mTalkServicePresentor.setRecognizerType(TYPE_FREETEXT);
				} else {
					mTalkServicePresentor.setRecognizerType(TYPE_COMMAND);
				}
				mType = "";
			} else {
				mTalkServicePresentor.setRecognizerType(TYPE_MUTIPLE);
			}
		}
		LogUtil.d(TAG, "mSessionStatus: " + mSessionStatus);
		mMicrophoneControl.setVisibility(View.VISIBLE);

		if (mLastSessionDone
				|| TextUtils.isEmpty(mSessionStatus)
				|| SessionPreference.VALUE_SESSION_END.equals(mSessionStatus)
				|| mTalkType == mTalkType_SMS
				|| (SessionPreference.VALUE_SESSION_SHOW.equals(mSessionStatus) && SessionPreference.VALUE_TYPE_UNSUPPORT_END_SHOW
						.equals(mType))) {
			mSessionViewContainer.removeAllSessionViews();
			showHelpView();
		}

		LogUtil.d(TAG, "startTalk mTalkType : " + mTalkType);

		if (mWakeupRecordingState) {
			mWakeupResult = true;
			mTalkServicePresentor.stopWakeup();
		} else {
			startTalk();
		}
	}

	private void releaseCurrentSession() {
		LogUtil.d(TAG, "releaseCurrentSession");
		setSessionEnd(true);
		mBeepPlayer.stop();
		if (mCurrentSession != null) {
			mCurrentSession.release();
			mCurrentSession = null;
		}
	}

	public void requestStartWakeup() {
		boolean enableWakeup = mUserPreference
				.getBoolean(UserPreference.KEY_ENABLE_WAKEUP,
						UserPreference.DEFAULT_WAKEUP);
		if (enableWakeup) {
			requestStartWakeup(TalkService.WAKEUP_INITDONE | mWakeUpInitDone,
					TalkService.TALK_INITDONE | mRecognitionIintDone,
					mRecognitionDataDone, mRecognitionRecordingState,
					mWakeupRecordingState);
		}
	}

	public void requestStopWakeup() {
		if (mTalkServicePresentor != null && mWakeupRecordingState) {
			LogUtil.d(TAG, "requestStopWakeup mWakeupRecordingState : "
					+ mWakeupRecordingState);
			mTalkServicePresentor.stopWakeup();
		}
	}

	/**
	 * 
	 * @Description : requestStartWakeup
	 * @Author : Brant
	 * @CreateDate : 2014-2-27
	 * @param wakeupInitDone
	 * @param recognitionInitDone
	 * @param recordingState
	 * @param grammarExist
	 */
	private void requestStartWakeup(boolean wakeupInitDone,
			boolean recognitionInitDone, boolean talkDataDone,
			boolean recordingState, boolean wakeupRecordingState) {
		LogUtil.d(TAG, "requestStartWakeup: wakeupInitDone " + wakeupInitDone
				+ ", recognitionInitDone " + recognitionInitDone
				+ ", talkDataDone " + talkDataDone + ", recordingState "
				+ recordingState + ", wakeupRecordingState "
				+ wakeupRecordingState);
		if (wakeupInitDone && recognitionInitDone && talkDataDone
				&& !recordingState && !wakeupRecordingState) {
			mIsFirstInitDone = false;
			if (mTalkServicePresentor != null) {
				mTalkServicePresentor.startWakeup();
			} else {
				LogUtil.e(TAG, "mTalkServicePresentor null!");
			}
		}
	}

	public void onDestroy() {
		LogUtil.d(TAG, "onDestroy");
		cancelSession();
		mTTSListener = null;
		mWakeupListener = null;
		mRecognitionDataDone = false;
		TalkService.TALK_INITDONE = false;
		TalkService.WAKEUP_INITDONE = false;
		resetTalk();

		if (mTalkServicePresentor != null) {
			mTalkServicePresentor.onDestroy();
		}

		mSessionViewContainer.removeAllSessionViews();

		mBeepPlayer.release();
		mMicrophoneControl.onDestroy();
		mMicrophoneControl = null;

		if (mFunctionView != null) {
			mFunctionView.release();
			mFunctionView = null;
		}
		// cancel notification
		cancelWakeupNotification();
		mNotificationManager.cancelAll();
		mNotificationManager = null;
		PhoneStateReceiver.unregisterPhoneStateListener(mPhoneStateListener);
		mPhoneStateListener = null;
		mAudioManager = null;
		mSMSObserver.unregistReceiver();
		mSMSObserver.setMessageReveiverListener(null);
		mMessageReveiverListener = null;
		mSMSObserver = null;
		mReveivedMessage = null;
	}

	public boolean onBackPressed() {
		LogUtil.d(TAG, "onBackPressed");
		boolean result = false;
		// cancelTTS();
		if (mCurrentSession != null) {
			mCurrentSession = null;
			mTalkServicePresentor.setProtocal(VoiceAssistant.BACK_PROTOCAL);
			mSessionViewContainer.removeAllSessionViews();
			String ttsString = KnowledgeMode.getKnowledgeAnswer(mWindowService,
					KnowledgeMode.KNOWLEDGE_STAGE_HELP);
			mSessionViewContainer.addAnswerView(ttsString);
			View view = getFunctionView();
			mSessionViewContainer.addSessionView(view, false);
			cancelTalk(true);
			result = true;
		}

		return result;
	}

	public void onNetWorkChanged() {
		LogUtil.d(TAG, "onNetWorkChanged");
		if (mFunctionView != null) {
			mFunctionView.initFunctionViews();
		}
	}

	private void setSessionEnd(boolean end) {
		LogUtil.d(TAG, "setSessionEnd:end " + end);
		mTalkType = mTalkType_NORMAL;
		mLastSessionDone = end;
	}

	/**
	 * @Description : TODO 核心方法，根据协议生成Session对象，并处理协议分发
	 * @Author : Dancindream
	 * @CreateDate : 2014-4-1
	 * @param protocal
	 */
	private void createSession(String protocal) {
		LogUtil.d(TAG, "createSession:protocal " + protocal);
		Log.d("mg", "执行了这里吧：" + protocal);
		if (mLastSessionDone) {
			releaseCurrentSession();
		}
		JSONObject obj = JsonTool.parseToJSONObject(protocal);
		BaseSession base = null;
		boolean isNeedClear = true;

		if (obj != null) {
			mSessionStatus = JsonTool.getJsonValue(obj,
					SessionPreference.KEY_DOMAIN, "");
			mType = JsonTool.getJsonValue(obj, SessionPreference.KEY_TYPE, "");
			LogUtil.d(TAG, "mSessionStatus: " + mSessionStatus);
			// TODO 如果是一个新领域流程的开始，是否需要将历史记录清理（参数控制）
			if (SessionPreference.VALUE_SESSION_BENGIN.equals(mSessionStatus)
					&& mPreference.getEnableCleanViewWhenSessionBegin()) {
				mSessionViewContainer.removeAllSessionViews();
			} else if (SessionPreference.VALUE_SESSION_END
					.equals(mSessionStatus)) {
				onSessionDone();
			}

			// TODO 根据type进行实例创建
			if (SessionPreference.VALUE_TYPE_SOME_NUMBERS.equals(mType)) {
				base = new MultipleNumbersShowSession(mWindowService,
						mSessionManagerHandler);
				mWindowService.hideCancelBtn(false);
			} else if (SessionPreference.VALUE_TYPE_SOME_PERSON.equals(mType)) {
				base = new MultiplePersonsShowSession(mWindowService,
						mSessionManagerHandler);
				mWindowService.hideCancelBtn(false);
			} else if (SessionPreference.VALUE_TYPE_CALL_ONE_NUMBER
					.equals(mType)) {
				// 直接打电话给谁
				base = new CallConfirmShowSession(mWindowService,
						mSessionManagerHandler);
				mWindowService.hideCancelBtn(false);
			} else if (SessionPreference.VALUE_TYPE_CALL_OK.equals(mType)) {
				base = new CallShowSession(mWindowService,
						mSessionManagerHandler);
			} else if (SessionPreference.VALUE_TYPE_INPUT_MSG_CONTENT
					.equals(mType)) {
				if (mCurrentSession != null
						&& mCurrentSession instanceof SmsInputShowSession) {
					mWindowService.hideCancelBtn(true);
					base = mCurrentSession;
					base.mIsNeedAddTextView = false;
					isNeedClear = false;
				} else {
					mWindowService.hideCancelBtn(true);
					base = new SmsInputShowSession(mWindowService,
							mSessionManagerHandler);
				}
			} else if (SessionPreference.VALUE_TYPE_MUTIPLE_UNSUPPORT
					.equals(mType)
					|| SessionPreference.VALUE_TYPE_CONFIRM_UNSUPPORT
							.equals(mType)) {
				isNeedClear = false;
				base = new UnsupportShowSession(mWindowService,
						mSessionManagerHandler);
			} else if (SessionPreference.VALUE_TYPE_UNSUPPORT_BEGIN_SHOW
					.equals(mType)) {
				if (mPhoneState == TelephonyManager.CALL_STATE_IDLE) {
					showHelpView();
					base = new UnsupportShowSession(mWindowService,
							mSessionManagerHandler);
				} else {
					showHelpView();
					releaseCurrentSession();
					base = new UnsupportEndSession(mWindowService,
							mSessionManagerHandler);
				}
			} else if (SessionPreference.VALUE_TYPE_UNSUPPORT_END_SHOW
					.equals(mType)) {
				showHelpView();
				mSessionViewContainer.removeAllSessionViews();
				releaseCurrentSession();
				base = new UnsupportEndSession(mWindowService,
						mSessionManagerHandler);
			} /*
			 * else if (SessionPreference.VALUE_TYPE_CONTACT_SHOW.equals(type))
			 * { base = new ContactShowSession(mWindowService,
			 * mSessionManagerHandler); }
			 */else if (SessionPreference.VALUE_TYPE_SMS_OK.equals(mType)) {
				base = new SmsShowSession(mWindowService,
						mSessionManagerHandler);

			} else if (SessionPreference.VALUE_TYPE_WAITING.equals(mType)) {
				base = new WaitingSession(mWindowService,
						mSessionManagerHandler);
			} else if (SessionPreference.VALUE_TYPE_WEATHER_SHOW.equals(mType)) {
				base = new WeatherShowSession(mWindowService,
						mSessionManagerHandler);
			} else if (SessionPreference.VALUE_TYPE_WEB_SHOW.equals(mType)) {
				base = new WebShowSession(mWindowService,
						mSessionManagerHandler);
			} else if (SessionPreference.VALUE_TYPE_TRANSLATION_SHOW
					.equals(mType)) {
				base = new TranslationShowSession(mWindowService,
						mSessionManagerHandler);
			} else if (SessionPreference.VALUE_TYPE_STOCK_SHOW.equals(mType)) {
				mMicrophoneControl.setAnswerText("");
				base = new StockShowSession(mWindowService,
						mSessionManagerHandler);
			} else if (SessionPreference.VALUE_TYPE_MUSIC_SHOW.equals(mType)) {
				base = new MusicShowSession(mWindowService,
						mSessionManagerHandler);
			} else if (SessionPreference.VALUE_TYPE_ROUTE_SHOW.equals(mType)) {
				mWindowService.hideCancelBtn(false);
				base = new RouteShowSession(mWindowService,
						mSessionManagerHandler);
			} else if (SessionPreference.VALUE_TYPE_POSITION_SHOW.equals(mType)) {
				base = new PositionShowSession(mWindowService,
						mSessionManagerHandler);
			} else if (SessionPreference.VALUE_TYPE_APP_LAUNCH.equals(mType)) {
				base = new AppLaunchSession(mWindowService,
						mSessionManagerHandler);
			} else if (SessionPreference.VALUE_TYPE_APP_UNINSTALL.equals(mType)) {
				base = new AppUninstallSession(mWindowService,
						mSessionManagerHandler);
			} else if (SessionPreference.VALUE_TYPE_APP_EXIT.equals(mType)) {
				base = new AppExitSession(mWindowService,
						mSessionManagerHandler);
			} else if (SessionPreference.VALUE_TYPE_SETTING.equals(mType)) {
				base = new SettingSession(mWindowService,
						mSessionManagerHandler);
			} else if (SessionPreference.VALUE_TYPE_REMINDER_CONFIRM
					.equals(mType)) {
				base = new ReminderConfirmSession(mWindowService,
						mSessionManagerHandler);
			} else if (SessionPreference.VALUE_TYPE_REMINDER_OK.equals(mType)) {
				base = new ReminderOkSession(mWindowService,
						mSessionManagerHandler);
			} else if (SessionPreference.VALUE_TYPE_INPUT_CONTACT.equals(mType)) {
				base = new TalkShowMiddleSession(mWindowService,
						mSessionManagerHandler);
			} else if (SessionPreference.VALUE_TYPE_TALK_SHOW.equals(mType)) {
				showHelpView();
				base = new TalkShowSession(mWindowService,
						mSessionManagerHandler);
			} else if (SessionPreference.VALUE_TYPE_POI_SHOW.equals(mType)) {
				if (mCurrentSession != null
						&& mCurrentSession instanceof PoiShowSession) {
					base = mCurrentSession;
					isNeedClear = false;
				} else {
					base = new PoiShowSession(mWindowService,
							mSessionManagerHandler);
				}
			} else if (SessionPreference.VALUE_TYPE_MULTIPLE_SHOW.equals(mType)) {
				base = new MultipleShowSession(mWindowService,
						mSessionManagerHandler);
			} else if (SessionPreference.VALUE_TYPE_ERROR_SHOW.equals(mType)) {
				base = new ErrorShowSession(mWindowService,
						mSessionManagerHandler);
			} else if (SessionPreference.VALUE_TYPE_ALARM_SHOW.equals(mType)) {
				base = new AlarmSettingSession(mWindowService,
						mSessionManagerHandler);
			} else if (SessionPreference.VALUE_TYPE_CONTACT_ADD.equals(mType)) {
				base = new ContactAddSession(mWindowService,
						mSessionManagerHandler);
			} else if (SessionPreference.VALUE_TYPE_APP_MUTIPLEP_SHOW
					.equals(mType)) {
				base = new MultipleApplicationShowSession(mWindowService,
						mSessionManagerHandler);
			} else if (SessionPreference.VALUE_TYPE_BROADCAST_SHOW
					.equals(mType)) {
				base = new BroadcastSession(mWindowService,
						mSessionManagerHandler);
			} else if (SessionPreference.VALUE_MUTIPLE_LOCATION.equals(mType)) {
				base = new MultipleLocationSession(mWindowService,
						mSessionManagerHandler);
			} else if (SessionPreference.VALUE_TYPE_UI_HANDLE_SHOW
					.equals(mType)) {
				base = new UIHandleShowSession(mWindowService,
						mSessionManagerHandler);

			}
		}

		if (base != null) {
			if (isNeedClear) {
				mSessionViewContainer.clearTemporaryViews();
			}
			if (mCurrentSession != null
					&& (mCurrentSession instanceof CallConfirmShowSession)) {
				mCurrentSession.release();
			}
			mCurrentSession = base;
			mCurrentSession.putProtocol(obj);
		}
	}

	public FunctionView getFunctionView() {
		LogUtil.d(TAG, "--getFunctionView--");
		// mFunctionView = new FunctionView(mWindowService);
		LayoutInflater layoutInflater = (LayoutInflater) mWindowService
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		/*
		 * FrameLayout functionLauout= (FrameLayout)
		 * layoutInflater.inflate(R.layout.funtion_main, null);
		 * if(functionLauout != null) { mFunctionView = (FunctionView)
		 * functionLauout.findViewById(R.id.main_rl); FunctionView view =
		 * (FunctionView) functionLauout.getChildAt(0); LogUtil.i(TAG ,
		 * "--functionLauout not null--" +functionLauout.getId() + " "
		 * +functionLauout.getChildCount()); if(view != null) { mFunctionView =
		 * view; } else { LogUtil.i(TAG , "--mFunctionView2 null--"); } }
		 */

		mFunctionView = (FunctionView) layoutInflater.inflate(
				R.layout.funtion_main, null);

		if (mFunctionView != null) {
			mFunctionView.setTextViews();
		} else {
			LogUtil.i(TAG, "--mFunctionView null--");
		}
		// mFunctionView.initFunctionViews();
		return mFunctionView;
	}

	/** 2014-12-3 yujun */
	public ReceiveSMSView getReceiveSMSView() {
		LogUtil.d(TAG, "--getReceiveSMSView--");
		if (mReveivedMessage != null) {
			LogUtil.d(TAG, "--mReveivedMessage != null--");
			mReceiveSMSView = new ReceiveSMSView(mWindowService,
					mReveivedMessage);
			mReceiveSMSView.initReceiveSMSView();
			return mReceiveSMSView;
		} else {
			LogUtil.d(TAG, "--mReveivedMessage == null--");
			return null;
		}

	}

	/** 2014-12-4 yujun */
	public ReceiveCallView getReceiveCALLView() {
		LogUtil.d(TAG, "--getReceiveCALLView--");
		mReceiveCallView = new ReceiveCallView(mWindowService, mName, mNumber,
				mSessionManagerHandler);
		mReceiveCallView.initReceiveCallView();
		return mReceiveCallView;
	}

	public void onFunctionClick() {
		onBackPressed();
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (mCurrentSession != null) {
			mCurrentSession.onActivityResult(requestCode, resultCode, data);
		}
	}

	public void replySms(String name, String phone) {
		LogUtil.d(TAG, "replySms : " + name + " phone : " + phone);
		String message = mWindowService.getString(R.string.sms_to, name);
		String format = "{\"rc\":0,\"text\":\"{0}\",\"service\":\"cn.yunzhisheng.sms\",\"code\":\"SMS_SEND\",\"semantic\":{\"intent\":{\"number\":\"{1}\",\"name\": \"{2}\"}},\"history\":\"cn.yunzhisheng.sms\"}";
		String replyMessage = DataTool.formatString(format, message, phone,
				name);
		Message msg = mSessionManagerHandler
				.obtainMessage(SessionPreference.MESSAGE_UI_OPERATE_PROTOCAL);
		msg.obj = replyMessage;
		mSessionManagerHandler.sendMessage(msg);
	}

	// public void resetAutoDismissTimer() {
	// mSessionManagerHandler.removeMessages(SessionPreference.MESSAGE_DISMISS_WINDOW);
	// mSessionManagerHandler.sendEmptyMessageDelayed(SessionPreference.MESSAGE_DISMISS_WINDOW,
	// 10000);
	// }
	//
	// private void cancelAutoDismissTimer() {
	// mSessionManagerHandler.removeMessages(SessionPreference.MESSAGE_DISMISS_WINDOW);
	// }

	private void onSessionDone() {
		LogUtil.d(TAG, "onSessionDone");
		// resetAutoDismissTimer();
		releaseWakeLock();
		releaseSessionCenterSession();
		// requestStartWakeup();
	}

	/**
	 * 设置静音
	 */
	private void systemaudio() {
		// TalkService.musiccurrent =
		// AudioUtil.getcurrentAudio(mWindowService,mAudioManager);
		// TalkService.ringcurrent =
		// AudioUtil.getcurrentRing(mWindowService,mAudioManager);
		// mAudioManager.setStreamVolume(AudioManager.STREAM_RING, 0, 0);
		// mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
		mAudioManager.setSpeakerphoneOn(false);
	}

	/**
	 * 打开音量
	 */
	private void addaudio() {
		// Log.d("way", "打开了声音" + TalkService.ringcurrent + ","
		// + TalkService.musiccurrent);
		// mAudioManager.setStreamVolume(AudioManager.STREAM_RING,
		// TalkService.ringcurrent, 0);
		// mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
		// TalkService.musiccurrent, 0);
		mAudioManager.setSpeakerphoneOn(true);
	}
}
