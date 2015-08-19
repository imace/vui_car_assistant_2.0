package cn.yunzhisheng.vui.assistant.oem;

/**
 * 自定义命令处理类
 * @author chenhao
 *
 */
public class RomCustomSetting {
	private static final String TAG = "RomCustomSetting";
	//广播需求
	/*拨打电话*/
//	public static final String CALL = "cn.yunzhisheng.intent.action.call.call";
	public static final String CALL = "com.android.calls.liu";
	/* 通讯录同步完成*/
	public static final String PHONEBOOK_OK = "com.ilincar.voice.phonebook.OK";
	/*接听电话*/
	public static final String ANSWER = "cn.yunzhisheng.intent.action.call.answer";
	/*挂断电话*/
	public static final String HANGUP = "cn.yunzhisheng.intent.action.call.active.hangup";
	/*退出app*/
	public static final String EXIT_APP = "cn.yunzhisheng.intent.action.app.exit";
	/*返回主界面*/
	public static final String HOME_PAGE = "cn.yunzhisheng.intent.action.homepage";
	/*拍照*/
	public static final String PICTURING = "cn.yunzhisheng.intent.action.picturing";
}
