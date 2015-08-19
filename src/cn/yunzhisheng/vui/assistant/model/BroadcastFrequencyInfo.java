/**
 * Copyright (c) 2012-2014 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * @FileName : BroadcastFrequencyInfo.java
 * @ProjectName : CarPlay
 * @PakageName : cn.yunzhisheng.vui.assistant.model
 * @Author : Brant
 * @CreateDate : 2014-9-18
 */
package cn.yunzhisheng.vui.assistant.model;

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
public class BroadcastFrequencyInfo {
	public static final String TAG = "BroadcastFrequencyInfo";
	private double mFrequency;
	private String mType;
	private String mUnit;
	
	public String toString(){
		return "[Frequency : "+mFrequency+",Type : "+mType+",Unit : "+mUnit+"]";
	}

	public double getFrequency() {
		return mFrequency;
	}

	public void setFrequency(double frequency) {
		this.mFrequency = frequency;
	}

	public String getType() {
		return mType;
	}

	public void setType(String type) {
		this.mType = type;
	}

	public String getUnit() {
		return mUnit;
	}

	public void setUnit(String mUnit) {
		this.mUnit = mUnit;
	}
}
