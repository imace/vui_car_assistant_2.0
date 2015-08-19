package cn.yunzhisheng.vui.assistant.view;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.ilincar.voice.R;
import cn.yunzhisheng.vui.assistant.preference.AssistantPreference;
import cn.yunzhisheng.vui.assistant.util.ImageDownloader;
import cn.yunzhisheng.vui.modes.StockInfo;

public class NoPerSonContentView extends FrameLayout implements ISessionView {
	public static final String TAG = "StockContentView";

	private TextView mTextViewName;
	
	
	public NoPerSonContentView(Context context) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.view_no_person, this, true);
		findViews();
		
	}

	private void findViews() {
		mTextViewName = (TextView) findViewById(R.id.noperson_text);
	}
	
	public void setShowText(String s) {
		mTextViewName.setText(s);
	}
	
	public void setShowText(int id) {
		mTextViewName.setText(id);
	}

	private void setListener() {
	}

	@Override
	public boolean isTemporary() {
		return false;
	}

	@Override
	public void release() {

	}

   
}
