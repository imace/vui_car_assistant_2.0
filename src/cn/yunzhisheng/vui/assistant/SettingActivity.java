package cn.yunzhisheng.vui.assistant;

import cn.yunzhisheng.common.util.LogUtil;
import cn.yunzhisheng.vui.assistant.SettingFragment.SettingFragmentListener;
import com.ilincar.voice.R;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

public class SettingActivity extends FragmentActivity {
	
	private String TAG = "SettingActivity";

	public SettingActivity() {
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_setting);
				
		FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment, new SettingFragment(mSettingFragmentListener));
		transaction.addToBackStack(null);
		transaction.commit();
	}
	
	SettingFragmentListener mSettingFragmentListener = new SettingFragmentListener() {
		
		@Override
		public void onClickBtn(int btnId) {
			LogUtil.d(TAG, "onClickReturnBtn");
			switch (btnId) {
			case R.id.setting_return:
				finish();
				break;
				
			case R.id.setting_help:
                Intent intent = new Intent(SettingActivity.this,
                        HelpActivity.class);
                startActivity(intent);
				break;

			default:
				break;
			}
		};
	};

    @Override
    public void onBackPressed() {
        finish();
    }
}
