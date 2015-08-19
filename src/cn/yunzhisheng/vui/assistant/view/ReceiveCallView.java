package cn.yunzhisheng.vui.assistant.view;

import cn.yunzhisheng.phone.Telephony;
import com.ilincar.voice.R;
import cn.yunzhisheng.vui.assistant.preference.SessionPreference;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ReceiveCallView extends RelativeLayout implements ISessionView{
	public static final String TAG = "ReceiveSMSView";
	private Handler mSessionManagerHandler = null;
	private LayoutInflater mLayoutInflater;
	private Context mContext;
	private String mName,mNumber;
	private Button mHangUpBtn, mAnswerBtn;
	
	
	private OnClickListener mOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.hangupBtn:
				Telephony.endCall(mContext);
				break;
			case R.id.answerBtn:
				Telephony.answerRingingCall(mContext);
				break;
			default:
				break;
			}
			mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_ANSWER_CALL);
		}
	};
	
	public ReceiveCallView(Context context,String name,String number, Handler sessionManagerHandler) {
		super(context);
		mName = name;
		mNumber = number;
		mContext = context;
		mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.mSessionManagerHandler = sessionManagerHandler;
	}
	
	public void addCall(){
		View view = mLayoutInflater.inflate(R.layout.call_txt, this, false);
		RelativeLayout ll = (RelativeLayout) view.findViewById(R.id.linearLayoutCALLTxtItem);
		TextView textViewHead = (TextView) ll.findViewById(R.id.callViewHead);
		mHangUpBtn = (Button) ll.findViewById(R.id.answerBtn);
		mAnswerBtn = (Button) ll.findViewById(R.id.hangupBtn);
		mHangUpBtn.setOnClickListener(mOnClickListener);
		mAnswerBtn.setOnClickListener(mOnClickListener);
		textViewHead.setText(mName+" "+mNumber);
		addView(view);
	}
	
	public void initReceiveCallView(){
		removeAllViews();
		addCall();
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
