package cn.yunzhisheng.vui.assistant.view;

import com.ilincar.voice.R;
import cn.yunzhisheng.vui.assistant.model.KnowledgeMode;
import cn.yunzhisheng.vui.assistant.sms.SmsItem;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ReceiveSMSView extends LinearLayout implements ISessionView{
	public static final String TAG = "ReceiveSMSView";
	private LayoutInflater mLayoutInflater;
	private String mName = "",mNumber = "",mMessage = "";
	
	@SuppressLint("NewApi")
	public ReceiveSMSView(Context context,SmsItem mSmsItem) {
		super(context);
		if(mSmsItem != null){
			if(mSmsItem.getName() != null && !mSmsItem.getName().isEmpty() && !mSmsItem.getName().equals("0")){
				mName = mSmsItem.getName();
			}
			mNumber = mSmsItem.getNumber();
			mMessage = mSmsItem.getMessage();
		}
		mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		setOrientation(VERTICAL);
	}
	
	public void initReceiveSMSView(){
		removeAllViews();
		
		addContect();
	}
	
	public void addContect(){
		View view = mLayoutInflater.inflate(R.layout.sms_whole, this, false);
		LinearLayout ll = (LinearLayout) view.findViewById(R.id.linearLayoutSMSOKItem);
		LinearLayout lll = (LinearLayout) ll.findViewById(R.id.linearLayoutSMSOKItem2);
		LinearLayout V = (LinearLayout) ll.findViewById(R.id.linearLayoutSMSOKItem4);
		LinearLayout lV = (LinearLayout) lll.findViewById(R.id.linearLayoutSMSOKItem3);
		TextView textViewHead = (TextView) lV.findViewById(R.id.contectViewHead);
		TextView edit= (TextView) ll.findViewById(R.id.message);
		Button btnSms = (Button) V.findViewById(R.id.btnSms);
		Button btnCancelSms = (Button) V.findViewById(R.id.btnCancelSms);
		textViewHead.setText(mName+" "+mNumber+":");
		edit.setText(mMessage);
		addView(view);
	}

	@Override
	public boolean isTemporary() {
		return true;
	}

	@Override
	public void release() {
		removeAllViews();
	}

}
