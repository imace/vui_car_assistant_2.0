package cn.yunzhisheng.vui.assistant;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import com.ilincar.voice.R;
import cn.yunzhisheng.vui.assistant.oem.RomDevice;

public class AboutActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		
		TextView textViewVersion = (TextView) findViewById(R.id.tv_version);
		textViewVersion.setText(String.format(textViewVersion.getText().toString(), RomDevice.getAppVersionName(this)));
	}
}
