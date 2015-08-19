package cn.yunzhisheng.vui.assistant.view;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.ilincar.voice.R;
import cn.yunzhisheng.vui.assistant.oem.RomContact;
import cn.yunzhisheng.vui.modes.PhoneNumberInfo;

public class PickPhoneNumberView extends PickBaseView {

	public PickPhoneNumberView(Context context) {
		super(context);
		// mContainer.setBackgroundResource(R.drawable.function_bg);
		Resources res = getResources();
		int left = (int) (res.getDimension(R.dimen.pick_phone_number_padding_left) + 0.5);
		int right = (int) (res.getDimension(R.dimen.pick_phone_number_padding_right) + 0.5);
		int top = (int) (res.getDimension(R.dimen.pick_phone_number_padding_top) + 0.5);
		int bottom = (int) (res.getDimension(R.dimen.pick_phone_number_padding_bottom) + 0.5);
		mContainer.setPadding(left, top, right, bottom);
	}

	public void initView(String contactName, long contactPhotoId, ArrayList<PhoneNumberInfo> phoneNumberInfos) {
		View header = mLayoutInflater.inflate(R.layout.pickview_header_contact, this, false);
		TextView tvName = (TextView) header.findViewById(R.id.textViewContactName);
		tvName.setText(contactName);
//		ImageView imageViewAvatar = (ImageView) header.findViewById(R.id.imageViewAvatar);

//		Drawable drawable = RomContact.loadContactDrawable(getContext(), contactPhotoId);
//
//		if (drawable != null) {
//			imageViewAvatar.setImageDrawable(drawable);
//		} else {
//			imageViewAvatar.setImageResource(R.drawable.ic_contact_avatar_new);
//		}

		setHeader(header);

		for (int i = 0; i < phoneNumberInfos.size(); i++) {
			PhoneNumberInfo phoneNumberInfo = phoneNumberInfos.get(i);

			View view = mLayoutInflater.inflate(R.layout.pickview_item_phone, mContainer, false);// 导入布局

			TextView tvPhoneNumber = (TextView) view.findViewById(R.id.textViewPhoneNumber_call);// 号码资源
			tvPhoneNumber.setText(phoneNumberInfo.getNumber());// 设置号码

			TextView noText = (TextView) view.findViewById(R.id.textViewNo_call);// 序号资源
			noText.setText((i + 1) + "");// 设置序号

			TextView tvPhoneNumberNote = (TextView) view.findViewById(R.id.textViewPhoneNumberNote_call);// 归属地资源
			String attribution = phoneNumberInfo.getAttribution();// 设置归属地
			tvPhoneNumberNote.setText(" (" + attribution + ")");

			if (TextUtils.isEmpty(attribution)) {
				tvPhoneNumberNote.setVisibility(View.GONE);
			} else {
				tvPhoneNumberNote.setVisibility(View.VISIBLE);
			}

			View divider = view.findViewById(R.id.divider);
			if (getItemCount() == phoneNumberInfos.size() - 1) {
				divider.setVisibility(View.GONE);
			}
			addItem(view);
		}
	}
}
