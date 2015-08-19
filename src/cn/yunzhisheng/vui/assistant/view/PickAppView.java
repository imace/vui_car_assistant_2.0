/**
 * Copyright (c) 2012-2012 Yunzhisheng(Shanghai) Co.Ltd. All right reserved.
 * @FileName : PickAppView.java
 * @ProjectName : iShuoShuo2
 * @PakageName : cn.yunzhisheng.vui.assistant.view
 * @Author : Brant
 * @CreateDate : 2012-11-15
 */
package cn.yunzhisheng.vui.assistant.view;

import java.util.ArrayList;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.ilincar.voice.R;
import cn.yunzhisheng.vui.modes.AppInfo;

public class PickAppView extends PickBaseView {
	public static final String TAG = "PickAppView";

	public PickAppView(Context context) {
		super(context);
		//mContainer.setBackgroundResource(R.drawable.function_bg);
		Resources res = getResources();
		int left = (int) (res.getDimension(R.dimen.pick_app_padding_left) + 0.5);
		int right = (int) (res.getDimension(R.dimen.pick_app_padding_right) + 0.5);
		int top = (int) (res.getDimension(R.dimen.pick_app_padding_top) + 0.5);
		int bottom = (int) (res.getDimension(R.dimen.pick_app_padding_bottom) + 0.5);
		mContainer.setPadding(left, top, right, bottom);
	}

	public void initView(ArrayList<AppInfo> appInfos) {
		Context context = getContext();
		View header = new View(context);
		header.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, context.getResources().getDimensionPixelSize(
			R.dimen.pick_app_content_view_header_height)));
		header.setBackgroundColor(context.getResources().getColor(R.color.title_bg));
		//setHeader(header);
		PackageManager packageManager = getContext().getPackageManager();

		for (int i = 0; i < appInfos.size(); i++) {
			AppInfo appInfo = appInfos.get(i);
			View view = mLayoutInflater.inflate(R.layout.pickview_item_app, this, false);
			TextView tvName = (TextView) view.findViewById(R.id.textViewAppName);
			tvName.setText(appInfo.mAppLabel);
			TextView noText = (TextView) view.findViewById(R.id.textViewNo);
			noText.setText((i + 1) + "");
			ImageView imageViewApp = (ImageView) view.findViewById(R.id.imageViewAppIcon);
			Drawable drawable = null;
			try {
				drawable = packageManager.getApplicationIcon(appInfo.mPackageName);
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}

			if (drawable != null) {
				imageViewApp.setImageDrawable(drawable);
			} else {
				imageViewApp.setImageResource(0);
			}

			View divider = view.findViewById(R.id.divider);
			if (getItemCount() == appInfos.size() - 1) {
				divider.setVisibility(View.GONE);
			}

			addItem(view);
		}
	}

}
