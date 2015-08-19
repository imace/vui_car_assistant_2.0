package cn.yunzhisheng.vui.assistant;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import cn.yunzhisheng.common.util.LogUtil;
import cn.yunzhisheng.vui.assistant.talk.TalkService;

public class MessageReceiver extends BroadcastReceiver {
	private static final String TAG = "MessageReceiver";
	public static final String ACTION_START_TALK = "cn.yunzhisheng.intent.action.START_TALK";
	public static final String ACTION_STOP_TALK = "cn.yunzhisheng.intent.action.STOP_TALK";
	public static final String ACTION_CANCEL_TALK = "cn.yunzhisheng.intent.action.CANCEL_TALK";

	public static final String ACTION_COMPILE_GRAMMER = "cn.yunzhisheng.intent.action.COMPILE_GRAMMER";

	@Override
	public void onReceive(Context context, Intent intent) {
		LogUtil.d(TAG, "onReceive:intent " + intent);
		String action = intent.getAction();
		if (Intent.ACTION_BOOT_COMPLETED.equals(action)) {
			Intent windowService = new Intent(context, WindowService.class);
			context.startService(windowService);
		} else if (ACTION_COMPILE_GRAMMER.equals(action)) {
			intent.setClass(context, TalkService.class);
			context.startService(intent);
		} else if (ACTION_START_TALK.equals(action)) {
			intent.setClass(context, WindowService.class);
			intent.setAction(WindowService.ACTION_START_WAKEUP);
			intent.setAction(MessageReceiver.ACTION_START_TALK);
			intent.putExtra(WindowService.EXTRA_KEY_START_TALK_FROM, WindowService.START_TALK_FROM_HARDWARE);
			context.startService(intent);
			abortBroadcast();
		} else if (ACTION_STOP_TALK.equals(action)) {
			intent.setClass(context, WindowService.class);
			context.startService(intent);
			abortBroadcast();
		} else if (ACTION_CANCEL_TALK.equals(action)) {
			intent.setClass(context, WindowService.class);
			context.startService(intent);
		}
	}
}
