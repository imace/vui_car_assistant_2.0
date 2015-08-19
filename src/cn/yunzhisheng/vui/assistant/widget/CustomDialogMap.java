package cn.yunzhisheng.vui.assistant.widget;

import cn.yunzhisheng.common.util.LogUtil;
import com.ilincar.voice.R;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class CustomDialogMap extends Dialog {
	private int layoutRes;// 布局文件
	private Context context;
	private LayoutInflater inflater;
	private View layout;
	
	private RelativeLayout amapLy, baiduLy;
	private ImageView amapImageView, baiduImageView;
	private Button cancelBtn;
	
	

	public CustomDialogMap(Context context) {
		super(context);
		this.context = context;
	}

	/**
	 * 自定义布局的构造方法
	 * 
	 * @param context
	 * @param resLayout
	 */
	public CustomDialogMap(Context context, int resLayout) {
		super(context);
		this.context = context;
		this.layoutRes = resLayout;
	}

	/**
	 * 自定义主题及布局的构造方法
	 * 
	 * @param context
	 * @param theme
	 * @param resLayout
	 */
	public CustomDialogMap(Context context, int theme, int resLayout) {
		super(context, theme);
		this.context = context;
		this.layoutRes = resLayout;
		this.inflater = LayoutInflater.from(context);
		this.layout = inflater.inflate(resLayout, null);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		amapImageView = (ImageView) layout.findViewById(R.id.amap_select);
		baiduImageView = (ImageView) layout.findViewById(R.id.baidu_select);
		amapLy = (RelativeLayout) layout.findViewById(R.id.amapPannel);
		baiduLy = (RelativeLayout) layout.findViewById(R.id.baiduPannel);
		cancelBtn = (Button) layout.findViewById(R.id.map_setting_cancel);
		
		amapLy.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				LogUtil.d("custom dialog map", "1231------");
			}
			
		});
		
		//cancelBtn.setOnClickListener(this);
	}
	
	public void setChoice(int which) {
		if (which == 0) {
			
		}
	}
}