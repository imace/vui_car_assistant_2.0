/**
 * Copyright (c) 2012-2014 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * @FileName : BroadcastStationInfo.java
 * @ProjectName : CarPlay
 * @PakageName : cn.yunzhisheng.vui.assistant.model
 * @Author : Brant
 * @CreateDate : 2014-9-18
 */
package cn.yunzhisheng.vui.assistant.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Brant
 * @CreateDate : 2014-9-18
 * @ModifiedBy : Brant
 * @ModifiedDate: 2014-9-18
 * @Modified:
 * 2014-9-18: 实现基本功能
 */
public class BroadcastStationInfo {
	public static final String TAG = "BroadcastStationInfo";

	private String mStation;
	private String mMsg;
	private List<BroadcastChannelInfo> mChannelList = new ArrayList<BroadcastChannelInfo>();

	public String getStation() {
		return mStation;
	}

	public void setStation(String station) {
		this.mStation = station;
	}

	public String getMsg() {
		return mMsg;
	}

	public void setMsg(String mMsg) {
		this.mMsg = mMsg;
	}

	public void addChannel(BroadcastChannelInfo broadcastChannelInfo) {
		mChannelList.add(broadcastChannelInfo);
	}

	public List<BroadcastChannelInfo> getChannelInfos() {
		return mChannelList;
	}

	public void clear() {
		for (BroadcastChannelInfo channel : mChannelList) {
			channel.clear();
		}
		mChannelList.clear();
		mChannelList = null;
	}

}
