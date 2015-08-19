/**
 * Copyright (c) 2012-2013 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * @FileName	: ITalkServiceWakeupListener.java
 * @ProjectName	: vui_assistant
 * @PakageName	: cn.yunzhisheng.vui.assistant.talk
 * @Author		: Dancindream
 * @CreateDate	: 2013-11-14
 */
package cn.yunzhisheng.vui.assistant.talk;

/**
 * @Module		: 隶属模块名
 * @Comments	: 描述
 * @Author		: Dancindream
 * @CreateDate	: 2013-11-14
 * @ModifiedBy	: Dancindream
 * @ModifiedDate: 2013-11-14
 * @Modified: 
 * 2013-11-14: 实现基本功能
 */
public interface ITalkServiceWakeupListener {
	public void onInitDone();
	public void onStart();
	public void onStop();
	public void onSuccess();
	public void onError(int code, String message);
}
