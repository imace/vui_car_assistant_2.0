package cn.yunzhisheng.vui.assistant.view;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cn.yunzhisheng.common.util.LogUtil;
import com.ilincar.voice.R;
import cn.yunzhisheng.vui.assistant.oem.RomContact;
import cn.yunzhisheng.vui.modes.ContactInfo;

public class PickPersonView extends PickBaseView {

	public PickPersonView(Context context) {
		super(context);
	}

	public void initView(ArrayList<ContactInfo> contactInfos) {
		Context context = getContext();

		for (int i = 0; i < contactInfos.size(); i++) {
			ContactInfo contactInfo = contactInfos.get(i);
			
			View view = mLayoutInflater.inflate(R.layout.pickview_item_contact, mContainer, false);
			
			TextView tvName = (TextView) view.findViewById(R.id.textViewName);
			tvName.setText(contactInfo.getDisplayName());
			
			TextView noText = (TextView) view.findViewById(R.id.textViewNo);
			noText.setText((i + 1) + "");
			
//			ImageView imageViewAvatar = (ImageView) view.findViewById(R.id.imageViewAvatar);
//			Drawable drawable = RomContact.loadContactDrawable(context, contactInfo.getPhotoId());
//			if (drawable != null) {
//				imageViewAvatar.setImageDrawable(drawable);
//			} else {
//				imageViewAvatar.setImageResource(R.drawable.ic_contact_avatar_new);
//			}

			View divider = view.findViewById(R.id.divider);
			if (getItemCount() == contactInfos.size() - 1) {
				divider.setVisibility(View.GONE);
			}
			addItem(view);
		}
	}
}
