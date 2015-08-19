/**
 * Copyright (c) 2012-2013 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * @FileName : MusicShowSession.java
 * @ProjectName : vui_assistant
 * @PakageName : cn.yunzhisheng.vui.assistant.session
 * @Author : Dancindream
 * @CreateDate : 2013-9-3
 */
package cn.yunzhisheng.vui.assistant.session;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import cn.yunzhisheng.common.DataTool;
import cn.yunzhisheng.common.JsonTool;
import cn.yunzhisheng.common.util.LogUtil;
import cn.yunzhisheng.preference.PrivatePreference;
import cn.yunzhisheng.vui.assistant.MessageSender;
import com.ilincar.voice.R;
import cn.yunzhisheng.vui.assistant.media.TrackInfo;
import cn.yunzhisheng.vui.assistant.preference.CommandPreference;
import cn.yunzhisheng.vui.assistant.preference.SessionPreference;
import cn.yunzhisheng.vui.assistant.preference.UserPreference;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Dancindream
 * @CreateDate : 2013-9-3
 * @ModifiedBy : Dancindream
 * @ModifiedDate: 2013-9-3
 * @Modified:
 * 2013-9-3: 实现基本功能
 */
public class UIHandleShowSession extends BaseSession {
	public static final String TAG = "MusicShowSession";
	private final String FATVORITE_ROUTE_FORMAT;

	/**
	 * @Author : Dancindream
	 * @CreateDate : 2013-9-3
	 * @param context
	 * @param sessionManagerHandler
	 */
	public UIHandleShowSession(Context context, Handler sessionManagerHandler) {
		super(context, sessionManagerHandler);
		FATVORITE_ROUTE_FORMAT = context.getString(R.string.onfind_place);
	}

	@Override
	public void putProtocol(JSONObject jsonProtocol) {
		super.putProtocol(jsonProtocol);
		addQuestionViewText(mQuestion);

		if (SessionPreference.DOMAIN_ROUTE.equals(mOriginType)
			&& SessionPreference.DOMAIN_CODE_FAVORITE_ROUTE.equals(mOriginCode)) {
			String poiVendor = PrivatePreference.getValue("poi_vendor", UserPreference.MAP_VALUE_AMAP);
			JSONObject resultObject = getJSONObject(mDataObject, "result");
			String toPoi = getJsonValue(resultObject, "toPoi");
			if ("AMAP".equals(poiVendor) || "GAODE".equals(poiVendor)) {
				mAnswer = mTTS = DataTool.formatString(FATVORITE_ROUTE_FORMAT, mContext.getString(R.string.gaodemap_or_navigation), toPoi);
			} else if ("BAIDU".equals(poiVendor)) {
				mAnswer = mTTS = DataTool.formatString(FATVORITE_ROUTE_FORMAT, mContext.getString(R.string.baidumap_or_navigation), toPoi);
			} else {
				mAnswer = mTTS = DataTool.formatString(FATVORITE_ROUTE_FORMAT, mContext.getString(R.string.map_or_navigation), toPoi);
			}

			addAnswerViewText(mAnswer);
			playTTS(mTTS);
		} else {
			String originType = JsonTool.getJsonValue(jsonProtocol, SessionPreference.KEY_ORIGIN_TYPE, "");
			if (originType.equals(SessionPreference.DOMAIN_MUSIC)) {
				MessageSender messageSender = new MessageSender(mContext);
				JSONObject dataObject = JsonTool.getJSONObject(jsonProtocol, SessionPreference.KEY_DATA);
				JSONObject result = JsonTool.getJSONObject(dataObject, "result");
				if (result != null) {
					String title = JsonTool.getJsonValue(result, "song");
					String artist = JsonTool.getJsonValue(result, "artist");
					String album = JsonTool.getJsonValue(result, "album");

					TrackInfo track = new TrackInfo();
					track.setTitle(title);
					track.setArtist(artist);
					track.setAlbum(album);
					mAnswer = mContext.getString(R.string.for_find) + JsonTool.getJsonValue(result, "keyword");
					Bundle bundle = new Bundle();
					bundle.putParcelable("track", track);
					Intent intent = new Intent(CommandPreference.ACTION_MUSIC_DATA);
					intent.putExtras(bundle);
					messageSender.sendOrderedMessage(intent, null);
				} else {
					mAnswer = mContext.getString(R.string.open_music);
					Intent intent = new Intent(CommandPreference.SERVICECMD);
					intent.putExtra(CommandPreference.CMDNAME, CommandPreference.CMDOPEN_MUSIC);
					messageSender.sendMessage(intent, null);
				}
			}

			addAnswerViewText(mAnswer);
			playTTS(mAnswer);
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
