/**
 * Copyright (c) 2012-2012 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * @FileName : ITalkServicePresentorListener.java
 * @ProjectName : V Plus 1.0
 * @PakageName : cn.yunzhisheng.vui.assistant.talk
 * @Author : Dancindream
 * @CreateDate : 2012-5-22
 */
package cn.yunzhisheng.vui.assistant.talk;

public interface ITalkServicePresentorListener {
	public static final String TAG = "ITalkServicePresentorListener";

	public static final String SERVICEPRESENTOR_SERVERNAME = "TALKSERVICE";
	public static final String SERVICEPRESENTOR_ERROR_INITFAIL = SERVICEPRESENTOR_SERVERNAME
																	+ "_SERVICEPRESENTOR_ERROR_INITFAIL";
	public static final String SERVICEPRESENTOR_ERROR_SERVICEISNULL = SERVICEPRESENTOR_SERVERNAME
																		+ "_SERVICEPRESENTOR_ERROR_SERVICEISNULL";

	// Talk
	public void onTalkInitDone();

	public void onTalkStart();

	public void onTalkStop();

	public void onTalkRecordingStart();

	public void onTalkRecordingStop();

	public void onTalkCancel();

	public void onTalkResult(String result);

	public void onSessionProtocal(String protocal);

	public void onTalkDataDone();

	public void onActiveStatusChanged(int flag);
	
	public void isActive(boolean status);
}
