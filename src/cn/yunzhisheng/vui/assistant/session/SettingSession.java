/**
 * Copyright (c) 2012-2013 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * @FileName : SettingSession.java
 * @ProjectName : vui_assistant
 * @PakageName : cn.yunzhisheng.vui.assistant.session
 * @Author : Dancindream
 * @CreateDate : 2013-9-6
 */
package cn.yunzhisheng.vui.assistant.session;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import cn.yunzhisheng.common.util.LogUtil;
import cn.yunzhisheng.phone.Telephony;
import cn.yunzhisheng.vui.assistant.MessageSender;
import com.ilincar.voice.R;
import cn.yunzhisheng.vui.assistant.oem.RomControl;
import cn.yunzhisheng.vui.assistant.oem.RomCustomSetting;
import cn.yunzhisheng.vui.assistant.oem.RomSystemSetting;
import cn.yunzhisheng.vui.assistant.preference.CommandPreference;
import cn.yunzhisheng.vui.assistant.preference.SessionPreference;
import cn.yunzhisheng.vui.assistant.view.NoPerSonContentView;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Dancindream
 * @CreateDate : 2013-9-6
 * @ModifiedBy : Dancindream
 * @ModifiedDate: 2013-9-6
 * @Modified: 2013-9-6: 实现基本功能
 */
public class SettingSession extends BaseSession {
	public static final String TAG = "SettingSession";

	private MessageSender mMessageSender;

	/**
	 * @Author : Dancindream
	 * @CreateDate : 2013-9-6
	 * @param context
	 * @param sessionManagerHandler
	 */
	public SettingSession(Context context, Handler sessionManagerHandler) {
		super(context, sessionManagerHandler);
		mMessageSender = new MessageSender(context);
	}

	public void putProtocol(JSONObject jsonProtocol) {
		super.putProtocol(jsonProtocol);

		String operator = getJsonValue(mDataObject, "operator", "");
		String operands = getJsonValue(mDataObject, "operands", "");
Log.d("mg", "=="+jsonProtocol);
		boolean playTTS = true;
		if (TextUtils.isEmpty(mAnswer)) {
			mAnswer = mQuestion;
		}
		if (SessionPreference.VALUE_SETTING_OBJ_3G.equals(operands)) {
			if (SessionPreference.VALUE_SETTING_ACT_OPEN.equals(operator)) {
				mAnswer = mContext.getString(R.string.open_3g);
				RomControl.enterControl(mContext, RomControl.ROM_OPEN_3G);
			} else if (SessionPreference.VALUE_SETTING_ACT_CLOSE.equals(operator)) {
				mAnswer = mContext.getString(R.string.close_3g);
				RomControl.enterControl(mContext, RomControl.ROM_CLOSE_3G);
			}
		} else if (SessionPreference.VALUE_SETTING_OBJ_AUTOLIGHT.equals(operands)) {
			if (SessionPreference.VALUE_SETTING_ACT_OPEN.equals(operator)) {
			} else if (SessionPreference.VALUE_SETTING_ACT_CLOSE.equals(operator)) {
			}
			mAnswer = mContext.getString(R.string.open_display_settings);
			RomControl.enterControl(mContext, RomControl.ROM_OPEN_DISPLAY_SETTINGS);
		} else if (SessionPreference.VALUE_SETTING_OBJ_BLUETOOTH.equals(operands)) {
			if (SessionPreference.VALUE_SETTING_ACT_OPEN.equals(operator)) {
				mAnswer = mContext.getString(R.string.open_bluetooth);
				RomControl.enterControl(mContext, RomControl.ROM_OPEN_BLUETOOTH);
			} else if (SessionPreference.VALUE_SETTING_ACT_CLOSE.equals(operator)) {
				mAnswer = mContext.getString(R.string.close_bluetooth);
				RomControl.enterControl(mContext, RomControl.ROM_CLOSE_BLUETOOTH);
			}
		} else if (SessionPreference.VALUE_SETTING_OBJ_TIME.equals(operands)) {
			if (SessionPreference.VALUE_SETTING_ACT_SET.equals(operator)) {
				mAnswer = mContext.getString(R.string.open_time_settings);
				RomControl.enterControl(mContext, RomControl.ROM_OPEN_TIME_SETTINGS);
			}
		} else if (SessionPreference.VALUE_SETTING_OBJ_LIGHT.equals(operands)) {
			if (SessionPreference.VALUE_SETTING_ACT_INCREASE.equals(operator)) {
			} else if (SessionPreference.VALUE_SETTING_ACT_DECREASE.equals(operator)) {
			} else if (SessionPreference.VALUE_SETTING_ACT_MAX.equals(operator)) {
			} else if (SessionPreference.VALUE_SETTING_ACT_MIN.equals(operator)) {
			}
			mAnswer = mContext.getString(R.string.open_display_settings);
			RomControl.enterControl(mContext, RomControl.ROM_OPEN_DISPLAY_SETTINGS);
		} else if (SessionPreference.VALUE_SETTING_OBJ_VOLUMN.equals(operands)) {
			if (SessionPreference.VALUE_SETTING_ACT_INCREASE.equals(operator)) {
				mAnswer = mContext.getString(R.string.increase_volumne);
				RomControl.enterControl(mContext, RomControl.ROM_INCREASE_VOLUMNE, 2);
			} else if (SessionPreference.VALUE_SETTING_ACT_DECREASE.equals(operator)) {
				mAnswer = mContext.getString(R.string.decrease_volumne);
				RomControl.enterControl(mContext, RomControl.ROM_DECREASE_VOLUMNE, 2);
			} else if (SessionPreference.VALUE_SETTING_ACT_MAX.equals(operator)) {
				mAnswer = mContext.getString(R.string.max_volumne);
				RomControl.enterControl(mContext, RomControl.ROM_VOLUMNE_MAX);
			} else if (SessionPreference.VALUE_SETTING_ACT_MIN.equals(operator)) {
				mAnswer = mContext.getString(R.string.min_volumne);
				RomControl.enterControl(mContext, RomControl.ROM_VOLUMNE_MIN);
			}
		} else if (SessionPreference.VALUE_SETTING_OBJ_MODEL_INAIR.equals(operands)) {
			if (SessionPreference.VALUE_SETTING_ACT_OPEN.equals(operator)) {
				mAnswer = mContext.getString(R.string.wait_open_setting_inair);
				RomControl.enterControl(mContext, RomControl.ROM_OPEN_MODEL_INAIR);
			} else if (SessionPreference.VALUE_SETTING_ACT_CLOSE.equals(operator)) {
				mAnswer = mContext.getString(R.string.wait_close_setting_inair);
				RomControl.enterControl(mContext, RomControl.ROM_CLOSE_MODEL_INAIR);
			}
		} else if (SessionPreference.VALUE_SETTING_OBJ_MODEL_MUTE.equals(operands)) {
			if (SessionPreference.VALUE_SETTING_ACT_OPEN.equals(operator)) {
				mAnswer = mContext.getString(R.string.open_model_mute);
				RomControl.enterControl(mContext, RomControl.ROM_OPEN_MODEL_MUTE);
			} else if (SessionPreference.VALUE_SETTING_ACT_CLOSE.equals(operator)) {
				mAnswer = mContext.getString(R.string.close_model_mute);
				RomControl.enterControl(mContext, RomControl.ROM_CLOSE_MODEL_MUTE);
			}
		} else if (SessionPreference.VALUE_SETTING_OBJ_MODEL_VIBRA.equals(operands)) {
			if (SessionPreference.VALUE_SETTING_ACT_OPEN.equals(operator)) {
				mAnswer = mContext.getString(R.string.open_model_vibra);
				RomControl.enterControl(mContext, RomControl.ROM_OPEN_MODEL_VIBRA);
			} else if (SessionPreference.VALUE_SETTING_ACT_CLOSE.equals(operator)) {
				mAnswer = mContext.getString(R.string.close_model_vibra);
				RomControl.enterControl(mContext, RomControl.ROM_CLOSE_MODEL_VIBRA);
			}
		} else if (SessionPreference.VALUE_SETTING_OBJ_RINGTONE.equals(operands)) {
			if (SessionPreference.VALUE_SETTING_ACT_SET.equals(operator)) {
				mAnswer = mContext.getString(R.string.open_sound_setting);
				RomControl.enterControl(mContext, RomControl.ROM_OPEN_SOUND_SETTINGS);
			}
		} else if (SessionPreference.VALUE_SETTING_OBJ_ROTATION.equals(operands)) {
			if (SessionPreference.VALUE_SETTING_ACT_OPEN.equals(operator)) {
				mAnswer = mContext.getString(R.string.open_rotation);
				RomControl.enterControl(mContext, RomControl.ROM_OPEN_ROTATION);
			} else if (SessionPreference.VALUE_SETTING_ACT_CLOSE.equals(operator)) {
				mAnswer = mContext.getString(R.string.close_rotation);
				RomControl.enterControl(mContext, RomControl.ROM_CLOSE_ROTATION);
			}
		} else if (SessionPreference.VALUE_SETTING_OBJ_WALLPAPER.equals(operands)) {
			if (SessionPreference.VALUE_SETTING_ACT_SET.equals(operator)) {
				mAnswer = mContext.getString(R.string.open_wallpaper_setting);
				RomControl.enterControl(mContext, RomControl.ROM_OPEN_WALLPAPER_SETTINGS);
			}
		} else if (SessionPreference.VALUE_SETTING_OBJ_WIFI.equals(operands)) {
			if (SessionPreference.VALUE_SETTING_ACT_OPEN.equals(operator)) {
				mAnswer = mContext.getString(R.string.open_wifi);
				RomControl.enterControl(mContext, RomControl.ROM_OPEN_WIFI);
			} else if (SessionPreference.VALUE_SETTING_ACT_CLOSE.equals(operator)) {
				mAnswer = mContext.getString(R.string.close_wifi);
				RomControl.enterControl(mContext, RomControl.ROM_CLOSE_WIFI);
			}
		} else if (SessionPreference.VALUE_SETTING_OBJ_WIFI_SPOT.equals(operands)) {
			if (SessionPreference.VALUE_SETTING_ACT_OPEN.equals(operator)) {
				mAnswer = mContext.getString(R.string.open_wifi_spot);
				RomControl.enterControl(mContext, RomControl.ROM_OPEN_WIFI_SPOT);
			} else if (SessionPreference.VALUE_SETTING_ACT_CLOSE.equals(operator)) {
				mAnswer = mContext.getString(R.string.close_wifi_spot);
				RomControl.enterControl(mContext, RomControl.ROM_CLOSE_WIFI_SPOT);
			}
		}
		//TODO 自定义命令添加
		else if (SessionPreference.VALUE_SETTING_OBJ_CALL.equals(operands)) {
			if (SessionPreference.VALUE_SETTING_ACT_ANSWER.equals(operator)) {
				mAnswer = mContext.getString(R.string.answer_call);
				//TODO 接听电话
				Intent intent = new Intent(RomCustomSetting.ANSWER);
				mMessageSender.sendMessage(intent, null);
				LogUtil.d("YunZhiSheng", " 接听电话 ："+ intent.toString());
//				Telephony.answerRingingCall(mContext);
			} else if (SessionPreference.VALUE_SETTING_ACT_HANG_UP.equals(operator)) {
				mAnswer = mContext.getString(R.string.end_call);
			    //TODO 挂断电话
				Intent intent = new Intent(RomCustomSetting.HANGUP);
				mMessageSender.sendMessage(intent, null);
				LogUtil.d("YunZhiSheng", " 挂断电话 ："+ intent.toString());
//				Telephony.endCall(mContext);
			}
		}
		
		//TODO 拍照
		else if (SessionPreference.VALUE_SETTING_OBJ_PICTURE.equals(operands)) {
			if (SessionPreference.VALUE_SETTING_ACT_PICTURE.equals(operator)) {
				mAnswer = mContext.getString(R.string.take_picture);
				Intent intent = new Intent(RomCustomSetting.PICTURING);
				mMessageSender.sendMessage(intent, null);
				LogUtil.d("YunZhiSheng", " 拍照 ："+ intent.toString());
//				Telephony.answerRingingCall(mContext);
			} 
		}
		//TODO 返回主界面
		else if (SessionPreference.VALUE_SETTING_OBJ_HOMEPAGE.equals(operands)) {
			mAnswer = mContext.getString(R.string.home_page);
			Intent intent = new Intent(RomCustomSetting.HOME_PAGE);
			mMessageSender.sendMessage(intent, null);
			
		    Intent mHomeIntent = new Intent(Intent.ACTION_MAIN);  
		      
		    mHomeIntent.addCategory(Intent.CATEGORY_HOME);  
		    mHomeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK  
		                    | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);  
		    mContext.startActivity(mHomeIntent);  
			
			LogUtil.d("YunZhiSheng", " 返回主界面 ："+ intent.toString());
			//				Telephony.answerRingingCall(mContext);
		} 
		else if (SessionPreference.VALUE_SETTING_OBJ_MUSIC_SHUFFLE_PLAYBACK.equals(operands)) {
			if (SessionPreference.VALUE_SETTING_ACT_CANCEL.equals(operator)) {
				mAnswer = mContext.getString(R.string.cancel_random_play);
			} else if (SessionPreference.VALUE_SETTING_ACT_PLAY.equals(operator)) {
				mAnswer = mContext.getString(R.string.random_play);
				Intent intent = new Intent(CommandPreference.SERVICECMD);
				intent.putExtra(CommandPreference.CMDNAME, CommandPreference.CMDSHUFFLE_PLAYBACK);
				mMessageSender.sendMessage(intent, null);
			}
		} else if (SessionPreference.VALUE_SETTING_OBJ_MUSIC_ORDER_PLAYBACK.equals(operands)) {
			if (SessionPreference.VALUE_SETTING_ACT_PLAY.equals(operator)) {
				mAnswer = mContext.getString(R.string.sequence_play);
				Intent intent = new Intent(CommandPreference.SERVICECMD);
				intent.putExtra(CommandPreference.CMDNAME, CommandPreference.CMDORDER_PLAYBACK);
				mMessageSender.sendMessage(intent, null);
			}
		} else if (SessionPreference.VALUE_SETTING_OBJ_MUSIC_SINGLE_CYCLE.equals(operands)) {
			if (SessionPreference.VALUE_SETTING_ACT_PLAY.equals(operator)) {
				mAnswer = mContext.getString(R.string.single_cycle);
				Intent intent = new Intent(CommandPreference.SERVICECMD);
				intent.putExtra(CommandPreference.CMDNAME, CommandPreference.CMDSINGLE_CYCLE);
				mMessageSender.sendMessage(intent, null);

			}
		} else if (SessionPreference.VALUE_SETTING_OBJ_MUSIC_LIST_CYCLE.equals(operands)) {
			if (SessionPreference.VALUE_SETTING_ACT_PLAY.equals(operator)) {
				mAnswer = mContext.getString(R.string.list_cycle);
				Intent intent = new Intent(CommandPreference.SERVICECMD);
				intent.putExtra(CommandPreference.CMDNAME, CommandPreference.CMDFULL_CYCLE);
				mMessageSender.sendMessage(intent, null);
			}
		} else if (SessionPreference.VALUE_SETTING_OBJ_MUSIC_FULL_CYCLE.equals(operands)) {
			if (SessionPreference.VALUE_SETTING_ACT_PLAY.equals(operator)) {
				mAnswer = mContext.getString(R.string.full_cycle);
				Intent intent = new Intent(CommandPreference.SERVICECMD);
				intent.putExtra(CommandPreference.CMDNAME, CommandPreference.CMDFULL_CYCLE);
				mMessageSender.sendMessage(intent, null);
			}
		} else if (SessionPreference.VALUE_SETTING_ACT_CLOSE_DESK_LYRIC.equals(operator)) {
			mAnswer = mContext.getString(R.string.close_desk_lyric);
			Intent intent = new Intent(CommandPreference.SERVICECMD);
			intent.putExtra(CommandPreference.CMDNAME, CommandPreference.CMDCLOSE_DESK_LYRIC);
			mMessageSender.sendMessage(intent, null);
		} else if (SessionPreference.VALUE_SETTING_ACT_OPEN_DESK_LYRIC.equals(operator)) {
			mAnswer = mContext.getString(R.string.open_desk_lyric);
			Intent intent = new Intent(CommandPreference.SERVICECMD);
			intent.putExtra(CommandPreference.CMDNAME, CommandPreference.CMDOPEN_DESK_LYRIC);
			mMessageSender.sendMessage(intent, null);
		} else if (SessionPreference.VALUE_SETTING_OBJ_MUSIC_SHUFFLE_PLAYBACK
				.equals(operands)) {
			if (SessionPreference.VALUE_SETTING_ACT_CANCEL.equals(operator)) {
				mAnswer = "已取消随机播放";
			} else if (SessionPreference.VALUE_SETTING_ACT_PLAY
					.equals(operator)) {
				mAnswer = "已为您随机播放";
				Intent intent = new Intent(CommandPreference.SERVICECMD);
				intent.putExtra(CommandPreference.CMDNAME,
						CommandPreference.CMDSHUFFLE_PLAYBACK);
				mMessageSender.sendMessage(intent, null);

			}
		} else if (SessionPreference.VALUE_SETTING_OBJ_MUSIC_ORDER_PLAYBACK
				.equals(operands)) {
			if (SessionPreference.VALUE_SETTING_ACT_PLAY.equals(operator)) {
				mAnswer = mContext.getString(R.string.sequence_play);
				Intent intent = new Intent(CommandPreference.SERVICECMD);
				intent.putExtra(CommandPreference.CMDNAME,
						CommandPreference.CMDORDER_PLAYBACK);
				mMessageSender.sendMessage(intent, null);
			}
		} else if (SessionPreference.VALUE_SETTING_OBJ_MUSIC_SINGLE_CYCLE
				.equals(operands)) {
			if (SessionPreference.VALUE_SETTING_ACT_PLAY.equals(operator)) {
				mAnswer = mContext.getString(R.string.single_cycle);
				Intent intent = new Intent(CommandPreference.SERVICECMD);
				intent.putExtra(CommandPreference.CMDNAME,
						CommandPreference.CMDSINGLE_CYCLE);
				mMessageSender.sendMessage(intent, null);

			}
		} else if (SessionPreference.VALUE_SETTING_OBJ_MUSIC_LIST_CYCLE
				.equals(operands)) {
			if (SessionPreference.VALUE_SETTING_ACT_PLAY.equals(operator)) {
				mAnswer = mContext.getString(R.string.list_cycle);
				Intent intent = new Intent(CommandPreference.SERVICECMD);
				intent.putExtra(CommandPreference.CMDNAME,
						CommandPreference.CMDFULL_CYCLE);
				mMessageSender.sendMessage(intent, null);
			}
		} else if (SessionPreference.VALUE_SETTING_OBJ_MUSIC_FULL_CYCLE
				.equals(operands)) {
			if (SessionPreference.VALUE_SETTING_ACT_PLAY.equals(operator)) {
				mAnswer = mContext.getString(R.string.full_cycle);
				Intent intent = new Intent(CommandPreference.SERVICECMD);
				intent.putExtra(CommandPreference.CMDNAME,
						CommandPreference.CMDFULL_CYCLE);
				mMessageSender.sendMessage(intent, null);
			}
		} else if (SessionPreference.VALUE_SETTING_ACT_CLOSE_DESK_LYRIC
				.equals(operator)) {
			mAnswer = mContext.getString(R.string.close_desk_lyric);
			Intent intent = new Intent(CommandPreference.SERVICECMD);
			intent.putExtra(CommandPreference.CMDNAME,
					CommandPreference.CMDCLOSE_DESK_LYRIC);
			mMessageSender.sendMessage(intent, null);
		} else if (SessionPreference.VALUE_SETTING_ACT_OPEN_DESK_LYRIC
				.equals(operator)) {
			mAnswer = mContext.getString(R.string.close_desk_lyric);
			Intent intent = new Intent(CommandPreference.SERVICECMD);
			intent.putExtra(CommandPreference.CMDNAME,
					CommandPreference.CMDOPEN_DESK_LYRIC);
			mMessageSender.sendMessage(intent, null);
		}

		// 如果协议中包含OBJ_XXX,请在这里添加
		else {
			if (operator.equals(SessionPreference.VALUE_SETTING_ACT_OPEN_CHANNEL)) {
			} else if (SessionPreference.VALUE_SETTING_ACT_PREV.equals(operator)) {
				mAnswer = mContext.getString(R.string.previous);
				Intent intent = new Intent(CommandPreference.SERVICECMD);
				intent.putExtra(CommandPreference.CMDNAME, CommandPreference.CMDPREVIOUS);
				mMessageSender.sendMessage(intent, null);
			} else if (SessionPreference.VALUE_SETTING_ACT_NEXT.equals(operator)) {
				mAnswer = mContext.getString(R.string.next);
				Intent intent = new Intent(CommandPreference.SERVICECMD);
				intent.putExtra(CommandPreference.CMDNAME, CommandPreference.CMDNEXT);
				mMessageSender.sendMessage(intent, null);
			} else if (SessionPreference.VALUE_SETTING_ACT_PLAY.equals(operator)) {
				mAnswer = mContext.getString(R.string.play);
				Intent intent = new Intent(CommandPreference.SERVICECMD);
				intent.putExtra(CommandPreference.CMDNAME, CommandPreference.CMDPLAY);
				mMessageSender.sendMessage(intent, null);
			} else if (SessionPreference.VALUE_SETTING_ACT_PAUSE.equals(operator)) {
				mAnswer = mContext.getString(R.string.pause);
				Intent intent = new Intent(CommandPreference.SERVICECMD);
				intent.putExtra(CommandPreference.CMDNAME, CommandPreference.CMDPAUSE);
				mMessageSender.sendMessage(intent, null);
			} else if (SessionPreference.VALUE_SETTING_ACT_RESUME.equals(operator)) {
				mAnswer = mContext.getString(R.string.recovery);
				Intent intent = new Intent(CommandPreference.SERVICECMD);
				intent.putExtra(CommandPreference.CMDNAME, CommandPreference.CMDPLAY);
				mMessageSender.sendMessage(intent, null);
			} else if (SessionPreference.VALUE_SETTING_ACT_STOP.equals(operator)) {
				mAnswer = mContext.getString(R.string.stop);
				Intent intent = new Intent(CommandPreference.SERVICECMD);
				intent.putExtra(CommandPreference.CMDNAME, CommandPreference.CMDSTOP);
				mMessageSender.sendMessage(intent, null);
			} 
			//云端的关闭既然不处理就删掉判断
/*			else if (SessionPreference.VALUE_SETTING_ACT_CLOSE.equals(operator)) {
				mAnswer = "已执行关闭";
				Intent intent = new Intent(CommandPreference.SERVICECMD);
				intent.putExtra(CommandPreference.CMDNAME, CommandPreference.CMDSTOP);
				mMessageSender.sendMessage(intent, null);
			}*/
			else if (SessionPreference.VALUE_SETTING_OBJ_MUSIC_PREVIOUS_ITEM.equals(operands)) {
				if (SessionPreference.VALUE_SETTING_ACT_PREV.equals(operator)) {
					mAnswer = mContext.getString(R.string.play_previous);
					// Toast.makeText(mContext, jsonProtocol.toString(),
					// Toast.LENGTH_SHORT).show();
				}
			} else if (SessionPreference.VALUE_SETTING_ACT_PLAY_MESSAGE.equals(operator)) {
				LogUtil.d(TAG, "--VALUE_SETTING_ACT_PLAY_MESSAGE Execution Broadcast--");
				mAnswer = mContext.getString(R.string.sms_content);
				playTTS = false;
				mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_PLAY_REVEIVED_MESSAGE);

			} else if (SessionPreference.VALUE_SETTING_ACT_CANCEL.equals(operator)) {
				mAnswer = mContext.getString(R.string.cancal);
				playTTS = false;
			} else{
				addQuestionViewText(mQuestion);
				addAnswerViewText(mQuestion);
				NoPerSonContentView view = new NoPerSonContentView(mContext);
				view.setShowText(mContext.getString(R.string.no_support_cmd, mQuestion));
				addAnswerView(view, true);
				if (playTTS) {
					playTTS(mContext.getString(R.string.no_support_cmd, mQuestion));
				} else {
					mSessionManagerHandler
							.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_DONE);
				}
				return;
			}
			// 如果协议中仅包含ACT_XXX，请在此添加
		}
		addQuestionViewText(mQuestion);
		addAnswerViewText(mAnswer);
        NoPerSonContentView view = new NoPerSonContentView(mContext);
        view.setShowText(mContext.getString(R.string.executed)+ mQuestion);
        addAnswerView(view, true);
		if (playTTS) {
			playTTS(mAnswer);
		} else {
			mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_DONE);
		}
	}

	@Override
	public void onTTSEnd() {
		LogUtil.d(TAG, "onTTSEnd");
		super.onTTSEnd();
		mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_DONE);
	}
}
