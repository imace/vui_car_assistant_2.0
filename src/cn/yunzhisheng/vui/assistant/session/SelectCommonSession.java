/**
 * Copyright (c) 2012-2014 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * @FileName : MutipleLocationSession.java
 * @ProjectName : vui_car_assistant
 * @PakageName : cn.yunzhisheng.vui.assistant.session
 * @Author : Brant
 * @CreateDate : 2014-10-29
 */
package cn.yunzhisheng.vui.assistant.session;

import java.util.ArrayList;

import android.content.Context;
import android.os.Handler;
import cn.yunzhisheng.common.util.LogUtil;
import com.ilincar.voice.R;
import cn.yunzhisheng.vui.assistant.view.PickBaseView.IPickListener;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Brant
 * @CreateDate : 2014-10-29
 * @ModifiedBy : Brant
 * @ModifiedDate: 2014-10-29
 * @Modified:
 * 2014-10-29: 实现基本功能
 */
public class SelectCommonSession extends CommBaseSession {
	public static final String TAG = "SelectCommonSession";
	protected ArrayList<String> mDataItemProtocalList = new ArrayList<String>();
	protected IPickListener mPickViewListener = new IPickListener() {
		@Override
		public void onItemPicked(int position) {
			LogUtil.d(TAG, "onItemPicked");
			cancelTTS();
			if (mDataItemProtocalList != null && mDataItemProtocalList.size() > 0) {
				String selectedItem = mDataItemProtocalList.get(position);
				onUiProtocal(selectedItem);
			}
		}

		@Override
		public void onPickCancel() {
			onUiProtocal("");
			LogUtil.d(TAG, "onItemPicked");
		}

		@Override
		public void onNext() {
			// TODO Auto-generated method stub
			onUiProtocal(mContext.getString(R.string.next_page_protocal));
			LogUtil.d(TAG, "onNext");
		}

		@Override
		public void onPre() {
			// TODO Auto-generated method stub
			onUiProtocal(mContext.getString(R.string.up_page_protocal));
			LogUtil.d(TAG, "onPre");
		}
	};

	public SelectCommonSession(Context context, Handler sessionManagerHandler) {
		super(context, sessionManagerHandler);
	}

	@Override
	public void release() {
		super.release();

		mDataItemProtocalList.clear();
		mDataItemProtocalList = null;
		mPickViewListener = null;
	}

}
