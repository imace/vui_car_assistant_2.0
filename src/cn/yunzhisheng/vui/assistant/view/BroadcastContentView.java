/**
 * Copyright (c) 2012-2014 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * @FileName : BroadcastContentView.java
 * @ProjectName : CarPlay
 * @PakageName : cn.yunzhisheng.vui.assistant.view
 * @Author : Brant
 * @CreateDate : 2014-9-19
 */
package cn.yunzhisheng.vui.assistant.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.ilincar.voice.R;
import cn.yunzhisheng.vui.assistant.model.BroadcastChannelInfo;
import cn.yunzhisheng.vui.assistant.model.BroadcastFrequencyInfo;
import cn.yunzhisheng.vui.assistant.model.BroadcastStationInfo;
import cn.yunzhisheng.vui.assistant.view.ELinearLayout.ViewBinder;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Brant
 * @CreateDate : 2014-9-19
 * @ModifiedBy : Brant
 * @ModifiedDate: 2014-9-19
 * @Modified:
 * 2014-9-19: 实现基本功能
 */
public class BroadcastContentView extends FrameLayout implements ISessionView {
	public static final String TAG = "BroadcastContentView";

	private LayoutInflater mInflater;
	private TextView mTextViewStation;
	private ELinearLayout mList;

	public BroadcastContentView(Context context) {
		super(context);
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mInflater.inflate(R.layout.broadcast_content_view, this, true);
		findViews();
		setListener();
		init();
	}

	private void findViews() {
		mTextViewStation = (TextView) findViewById(R.id.textViewStation);
		mList = (ELinearLayout) findViewById(android.R.id.list);
	}

	private void setListener() {

	}

	private void init() {
		mList.setBindResource(R.layout.broadcast_list_item);
		mList.setDividerDrawable(R.drawable.horizontal_divider);
		mList.setViewBinder(new ViewBinder() {

			@Override
			public void bindViewData(int position, View view, Map<String, ?> data) {
				TextView tvStation = (TextView) view.findViewById(R.id.textViewStation);
				tvStation.setText((String) data.get("channel"));
				TextView tvFrequency = (TextView) view.findViewById(R.id.textViewFrequency);
				tvFrequency.setText((String) data.get("frequency"));
			}
		});
	}

	@Override
	public boolean isTemporary() {
		return false;
	}

	public void setBroadcastResult(BroadcastStationInfo result) {
		if (result == null) {
			return;
		}
		mTextViewStation.setText(result.getStation());
		List<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
		StringBuilder frequecyBuilder = new StringBuilder();
		for (BroadcastChannelInfo channel : result.getChannelInfos()) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("channel", channel.getChannel());
			frequecyBuilder.setLength(0);
			String split = "";
			for (BroadcastFrequencyInfo frequency : channel.getFrequencyInfos()) {
				frequecyBuilder.append(split + frequency.getType() + " " + frequency.getFrequency()
										+ frequency.getUnit());
				split = "|";
			}
			map.put("frequency", frequecyBuilder.toString());
			data.add(map);
		}
		mList.resetPageIndex();
		mList.setData(data);
	}

	@Override
	public void release() {

	}

}
