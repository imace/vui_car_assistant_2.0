/**
 * Copyright (c) 2012-2014 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * @FileName : IMusicListener.java
 * @ProjectName : CarPlay
 * @PakageName : cn.yunzhisheng.vui.assistant.media
 * @Author : Brant
 * @CreateDate : 2014-9-16
 */
package cn.yunzhisheng.vui.assistant.media;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Brant
 * @CreateDate : 2014-9-16
 * @ModifiedBy : Brant
 * @ModifiedDate: 2014-9-16
 * @Modified:
 * 2014-9-16: 实现基本功能
 */
public interface IMusicListener extends PlayerEngineListener {
	void onBinderError(int errorCode);

	void onServiceConnected();

	void onServiceDisconnected();
}
