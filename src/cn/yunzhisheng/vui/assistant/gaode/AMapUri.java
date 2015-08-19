/**
 * Copyright (c) 2012-2015 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * @FileName : AMapUriData.java
 * @ProjectName : vui_car_assistant
 * @PakageName : cn.yunzhisheng.vui.assistant.gaode
 * @Author : Brant
 * @CreateDate : 2015-1-16
 */
package cn.yunzhisheng.vui.assistant.gaode;

import java.util.HashMap;
import java.util.Set;

import cn.yunzhisheng.common.util.LogUtil;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Brant
 * @CreateDate : 2015-1-16
 * @ModifiedBy : Brant
 * @ModifiedDate: 2015-1-16
 * @Modified:
 * 2015-1-16: 实现基本功能
 */
public class AMapUri {
	public static final String TAG = "AMapUriData";

	private static final String TEMPLATE = "androidamap://%1$s";

	private String mService;
	private HashMap<String, Object> mParams;

	public AMapUri(String service) {
		mService = service;
		mParams = new HashMap<String, Object>();
	}

	public void addParam(String key, Object value) {
		LogUtil.d(TAG, "addParam:key " + key + ",value " + value);
		if (mParams.containsKey(key)) {
			LogUtil.w(TAG, "mParams have exist (key='" + key + "',value='" + mParams.get(key) + "')"
							+ ",will be overrided!");
		}
		mParams.put(key, value);
	}

	public String getDatString() {
		StringBuilder builder = new StringBuilder();
		builder.append(String.format(TEMPLATE, mService));

		String split = "?";
		Set<String> keys = mParams.keySet();
		for (String key : keys) {
			builder.append(split);
			builder.append(key);
			builder.append("=");
			builder.append(mParams.get(key));
			split = "&";
		}

		return builder.toString();
	}
}
