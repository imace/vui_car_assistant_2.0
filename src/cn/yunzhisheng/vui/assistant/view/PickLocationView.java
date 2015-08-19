/**
 * Copyright (c) 2012-2014 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * @FileName : PickLocationView.java
 * @ProjectName : vui_car_assistant
 * @PakageName : cn.yunzhisheng.vui.assistant.view
 * @Author : Brant
 * @CreateDate : 2014-10-29
 */
package cn.yunzhisheng.vui.assistant.view;

import java.util.ArrayList;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.ilincar.voice.R;
import cn.yunzhisheng.vui.modes.LocationInfo;

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
public class PickLocationView extends PickBaseView {
	public static final String TAG = "PickLocationView";
	private Context mContext = null;

	private int page_num = 0;
	private int page_content = 0;
	public PickLocationView(Context context) {
		super(context);
		mContext = context;
		//mContainer.setBackgroundResource(R.drawable.function_bg);
	}

	public void initView(ArrayList<LocationInfo> contactInfos) {
//		View header = mLayoutInflater.inflate(R.layout.pickview_header_location, this, false);
//		TextView pageTextView = (TextView) header.findViewById(R.id.textViewPageNum);
//		Button pageUpBtn = (Button) header.findViewById(R.id.pageUpBtn);
//		Button pageDownBtn = (Button) header.findViewById(R.id.pageDownBtn);
//		
//		setHeader(header);
		
		//add by tyz 20150330
		
		View view2 = mLayoutInflater.inflate(R.layout.location_number, mContainer,false);
		TextView number = (TextView)view2.findViewById(R.id.text_number);
		Button btn_next = (Button)view2.findViewById(R.id.btn_next);
		Button btn_pre = (Button)view2.findViewById(R.id.btn_pre);
		
		btn_next.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				mPickListener.onNext();
			}
		});
		btn_pre.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				mPickListener.onPre();
			}
		});
		number.setText("第"+ page_num +"页 (共" + page_content + "页)");
		
//		addItem(view2);
		setHeader(view2);
		
		for (int i = 0; i < contactInfos.size(); i++) {
			LocationInfo contactInfo = contactInfos.get(i);
			View view = mLayoutInflater.inflate(R.layout.pickview_item_location, mContainer, false);
			TextView tvName = (TextView) view.findViewById(R.id.textViewName);
			TextView tvAddress = (TextView) view.findViewById(R.id.textViewAddress);
			String name = contactInfo.getName();
			if (TextUtils.isEmpty(name)) {
				tvName.setVisibility(View.GONE);
				tvAddress.setTextColor(getContext().getResources().getColor(R.color.black));
			} else {
				tvName.setText(name);
			}
			if (TextUtils.isEmpty(contactInfo.getAddress())) {
				tvAddress.setVisibility(View.GONE);
			} else {
				tvAddress.setText(contactInfo.getAddress());
			}
			TextView noText = (TextView) view.findViewById(R.id.textViewNo);
			noText.setText(String.valueOf(i + 1));

			View divider = view.findViewById(R.id.divider);
			if (i == contactInfos.size() - 1) {
				divider.setVisibility(View.GONE);
			} else {
				divider.setVisibility(View.VISIBLE);
			}
			
			addItem(view);
		}
		updateLayoutParams();
	}
	

	public void setPageNum(int pageNum){
		page_num = pageNum;
	}
	public void setPageContent(int pageContent){
		page_content = pageContent;
	}
}
