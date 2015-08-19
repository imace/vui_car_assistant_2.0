/**
 * Copyright (c) 2012-2014 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * @FileName : BroadcastChannelInfo.java
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
public class BroadcastChannelInfo {
	public static final String TAG = "BroadcastChannelInfo";
	private String mChannel;
	private List<BroadcastFrequencyInfo> mFrequencyList = new ArrayList<BroadcastFrequencyInfo>();

	public String getChannel() {
		return mChannel;
	}

	public void setChannel(String channel) {
		this.mChannel = channel;
	}

	public void addFrequency(BroadcastFrequencyInfo frequencyInfo) {
		mFrequencyList.add(frequencyInfo);
	}

	public List<BroadcastFrequencyInfo> getFrequencyInfos() {
		return mFrequencyList;
	}

	public void clear() {
		mFrequencyList.clear();
		mFrequencyList = null;
	}
}
