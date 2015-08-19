/**
 * Copyright (c) 2012-2013 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * @FileName : PoiShowSession.java
 * @ProjectName : vui_assistant
 * @PakageName : cn.yunzhisheng.vui.assistant.session
 * @Author : Dancindream
 * @CreateDate : 2013-9-3
 */
package cn.yunzhisheng.vui.assistant.session;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Intents.Insert;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import cn.yunzhisheng.common.util.LogUtil;
import cn.yunzhisheng.preference.PrivatePreference;
import cn.yunzhisheng.vui.assistant.baidu.BaiduMap;
import com.ilincar.voice.R;
import cn.yunzhisheng.vui.assistant.gaode.GaodeMap;
import cn.yunzhisheng.vui.assistant.preference.SessionPreference;
import cn.yunzhisheng.vui.assistant.util.Util;
import cn.yunzhisheng.vui.assistant.view.ELinearLayout.OnItemClickListener;
import cn.yunzhisheng.vui.assistant.view.ELinearLayout.ViewBinder;
import cn.yunzhisheng.vui.assistant.view.PoiContentView;
import cn.yunzhisheng.vui.assistant.view.PoiHeaderView;
import cn.yunzhisheng.vui.assistant.view.PoiHeaderView.OnSortChangedLisener;
import cn.yunzhisheng.vui.assistant.view.PoiHeaderView.SortItem;
import cn.yunzhisheng.vui.modes.LocationInfo;
import cn.yunzhisheng.vui.modes.PoiInfo;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Dancindream
 * @CreateDate : 2013-9-3
 * @ModifiedBy : Dancindream
 * @ModifiedDate: 2013-9-3
 * @Modified:
 * 2013-9-3: 实现基本功能
 */
public class PoiShowSession extends BaseSession {
	public static final String TAG = "PoiShowSession";
	private SortItem[] SORT_ITEMS = null;
	private PoiContentView mPoiContentView = null;
	private List<PoiInfo> mPOIInfos = null;
	private boolean sortFocus = false;
	private PoiHeaderView mContentViewHeader = null;
	private LocationInfo mCurrentLocation;

	private OnClickListener mPoiItemExtendClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Context context = v.getContext();
			Map<String, ?> data = mPoiContentView.getData(mPoiContentView.getExpandItemIndex());
			String name = (String) data.get("name");
			String branchName = (String) data.get("branch_name");
			String phone = (String) data.get("phone");
			String address = (String) data.get("address");
			double latitude = (Double) data.get("latitude");
			double longtitude = (Double) data.get("longtitude");

			switch (v.getId()) {
			case R.id.textViewPoiAddr: {
				GaodeMap.showRoute(
					context,
					BaiduMap.ROUTE_MODE_DRIVING,
					mCurrentLocation.getLatitude(),
					mCurrentLocation.getLongitude(),
					"",
					"",
					"",
					latitude,
					longtitude,
					"",
					name);
			}
				break;
			case R.id.textViewPoiPhone: {
				Uri uri = Uri.parse("tel:" + phone);
				Intent it = new Intent(Intent.ACTION_DIAL, uri);
				it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(it);
			}
				break;
			case R.id.textViewPoiPhoneSave:
				Intent i = new Intent(Intent.ACTION_INSERT_OR_EDIT);
				i.setType(ContactsContract.Contacts.CONTENT_ITEM_TYPE);
				i.putExtra(Insert.NAME, name + (TextUtils.isEmpty(branchName) ? "" : "(" + branchName + ")"));
				i.putExtra(Insert.PHONE, phone);
				i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(i);
				break;
			case R.id.textViewPoiPhoneForward:
				Uri uri = Uri.parse("smsto:");
				Intent it = new Intent(Intent.ACTION_SENDTO, uri);
				it.putExtra("sms_body", getFormatPoiInfo(context, name, branchName, address, phone));
				it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				try {
					context.startActivity(it);
				} catch (Exception e) {
					e.printStackTrace();
					Toast
						.makeText(mContext, mContext.getString(R.string.sms_activity_not_found), Toast.LENGTH_SHORT)
						.show();
				}
				break;
			case R.id.textViewPoiMore:
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse((String) data.get("url")));
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent);
				break;
			}
		}
	};

	/**
	 * @Author : Dancindream
	 * @CreateDate : 2013-9-3
	 * @param context
	 * @param sessionManagerHandler
	 */
	public PoiShowSession(Context context, Handler sessionManagerHandler) {
		super(context, sessionManagerHandler);
		mCurrentLocation = new LocationInfo();
	}

	public void putProtocol(JSONObject jsonProtocol) {
		super.putProtocol(jsonProtocol);
		addQuestionViewText(mQuestion);

		mPOIInfos = new ArrayList<PoiInfo>();
		JSONObject jsonResultObj = getJSONObject(mDataObject, "result");
		mCurrentLocation.setLatitude(getJsonValue(jsonResultObj, "current_latitude", 0d));
		mCurrentLocation.setLongitude(getJsonValue(jsonResultObj, "current_longtitude", 0d));
		mCurrentLocation.setAddress(getJsonValue(jsonResultObj, "current_address"));
		if (jsonResultObj != null) {
			JSONArray resultShopObject = getJsonArray(jsonResultObj, "shops");
			if (resultShopObject != null) {
				for (int i = 0; i < resultShopObject.length(); i++) {
					JSONObject item = getJSONObject(resultShopObject, i);
					PoiInfo poiItem = new PoiInfo();
					poiItem.setId(getJsonValue(item, "id"));
					poiItem.setName(getJsonValue(item, "name"));
					poiItem.setBranchName(getJsonValue(item, "branch_name"));
					poiItem.setTel(getJsonValue(item, "tel"));
					poiItem.setPostCode(getJsonValue(item, "postCode"));
					poiItem.setTypeDes(getJsonValue(item, "typeDes"));
					poiItem.setDistance(getJsonValue(item, "distance", 0));
					poiItem.setRating(Float.parseFloat(getJsonValue(item, "rate", "0.0")));
					poiItem.setUrl(getJsonValue(item, "url"));

					poiItem.setName(getJsonValue(item, "name"));
					JSONArray ar = getJsonArray(item, "category");
					if (ar != null && ar.length() > 0) {
						String[] categories = new String[ar.length()];
						for (int j = 0; j < ar.length(); j++) {
							try {
								categories[j] = ar.getString(j);
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						poiItem.setCategories(categories);
					}
					ar = getJsonArray(item, "region");
					if (ar != null && ar.length() > 0) {
						String[] regions = new String[ar.length()];
						for (int j = 0; j < ar.length(); j++) {
							try {
								regions[j] = ar.getString(j);
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						poiItem.setRegions(regions);
					}

					JSONObject jsonLocationObj = getJSONObject(item, "location");
					LocationInfo locationInfo = new LocationInfo();
					locationInfo.setType(getJsonValue(jsonLocationObj, "type", 0));
					locationInfo.setProvider(getJsonValue(jsonLocationObj, "provider"));
					try {
						locationInfo.setAccuracy(Float.parseFloat(getJsonValue(jsonLocationObj, "accuracy")));
					} catch (Exception e) {
					}
					locationInfo.setAltitude(getJsonValue(jsonLocationObj, "altitude", 0d));
					locationInfo.setTime(getJsonValue(jsonLocationObj, "time", 0));
					locationInfo.setBearing(getJsonValue(jsonLocationObj, "bearing", 0));
					locationInfo.setSpeed(getJsonValue(jsonLocationObj, "speed", 0));
					locationInfo.setName(getJsonValue(jsonLocationObj, "name"));
					locationInfo.setProvince(getJsonValue(jsonLocationObj, "province"));
					locationInfo.setCity(getJsonValue(jsonLocationObj, "city"));
					locationInfo.setCityCode(getJsonValue(jsonLocationObj, "cityCode"));
					locationInfo.setDistrict(getJsonValue(jsonLocationObj, "destrict"));
					locationInfo.setStreet(getJsonValue(jsonLocationObj, "street"));
					locationInfo.setAddress(getJsonValue(jsonLocationObj, "address"));
					locationInfo.setAddressDetail(getJsonValue(jsonLocationObj, "addressDetail"));
					locationInfo.setLatitude(getJsonValue(jsonLocationObj, "lat", 0d));
					locationInfo.setLongitude(getJsonValue(jsonLocationObj, "lng", 0d));

					poiItem.setLocationInfo(locationInfo);
					mPOIInfos.add(poiItem);
				}
			}

			JSONArray resultactionObject = getJsonArray(jsonResultObj, "actions");
			if (resultactionObject != null) {
				SORT_ITEMS = new SortItem[resultactionObject.length()];
				for (int i = 0; i < resultactionObject.length(); i++) {
					JSONObject item = getJSONObject(resultactionObject, i);
					if (item != null) {
						String name = getJsonValue(item, "title", "");
						boolean focus = getJsonValue(item, "focus", false);
						String onSelected = getJsonValue(item, "onSelected", "");
						SortItem sortItem = new SortItem(name, "" + i, focus, onSelected);
						SORT_ITEMS[i] = sortItem;
					}
				}
			}
		}

		if (mPoiContentView == null) {
			LogUtil.d(TAG, "--PoiShowSession mAnswer : " + mAnswer + "--");
			playTTS(mAnswer);
			//playTTS(mTTS);
			mPoiContentView = new PoiContentView(mContext, R.layout.poi_list_item, R.drawable.horizontal_divider);
			String poiProvider = PrivatePreference.getValue("poi_vendor", "");
			Drawable icon = null;
			String source = null;
			if ("AMAP".equals(poiProvider) || "GAODE".equals(poiProvider)) {
				icon = mContext.getResources().getDrawable(R.drawable.ic_amap);
			} else if ("BAIDU".equals(poiProvider)) {
				icon = mContext.getResources().getDrawable(R.drawable.ic_baidu);
			} else if ("DIANPING".equals(poiProvider)) {
				icon = mContext.getResources().getDrawable(R.drawable.ic_dianping_logo);
			}
			if (icon != null || !TextUtils.isEmpty(source)) {
				mPoiContentView.setDataSource(icon, source);
			}
			mContentViewHeader = new PoiHeaderView(mContext);
			// mPoiContentView.addHeader(mContentViewHeader);
			addAnswerView(mPoiContentView);
		}

		mContentViewHeader.setSortItems(SORT_ITEMS);
		// header.setParams(params);
		for (int i = 0; i < SORT_ITEMS.length; i++) {
			if (SORT_ITEMS[i].focus == true) {
				sortFocus = true;
				mContentViewHeader.setSortSelectedItem(i);
			}
		}
		if (sortFocus == false) {
			mContentViewHeader.setSortSelectedItem(0);
		}
		mContentViewHeader.setTitle(mAnswer);
		mContentViewHeader.setDividerHeight(mContext.getResources().getDimensionPixelSize(
			R.dimen.poi_sort_list_divider_height));
		mContentViewHeader.setDividerColor(mContext.getResources().getColor(R.color.page_memo_management_header));
		mContentViewHeader.setSortChangedListener(new OnSortChangedLisener() {

			@Override
			public void onChanged(int index) {
				mContentViewHeader.onLoading();
				String onSelected = SORT_ITEMS[index].onSelected;
				onUiProtocal(onSelected);
			}
		});

		mPoiContentView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClicked(int position, Map<String, ?> data) {
				if (position == mPoiContentView.getExpandItemIndex()) {
					View item = mPoiContentView.getItemViewAt(position);
					View itemExpand = item.findViewById(R.id.linearLayoutPoiItemExtend);
					itemExpand.setVisibility(View.GONE);
					mPoiContentView.setExpandItemIndex(-1);
				} else {
					if (mPoiContentView.getExpandItemIndex() != -1) {
						View itemOld = mPoiContentView.getItemViewAt(mPoiContentView.getExpandItemIndex());
						View itemExpandOld = itemOld.findViewById(R.id.linearLayoutPoiItemExtend);
						itemExpandOld.setVisibility(View.GONE);
					}
					View item = mPoiContentView.getItemViewAt(position);
					View itemExpand = item.findViewById(R.id.linearLayoutPoiItemExtend);
					itemExpand.setVisibility(View.VISIBLE);
					mPoiContentView.setExpandItemIndex(position);
				}
			}
		});
		mPoiContentView.setPoiListViewBinder(new ViewBinder() {

			@Override
			public void bindViewData(int position, View view, Map<String, ?> data) {
				String dataEmpty = mContext.getString(R.string.data_empty);
				TextView tvPoiName = (TextView) view.findViewById(R.id.textViewPoiName);
				tvPoiName.setText((String) data.get("name"));
				TextView tvBranchName = (TextView) view.findViewById(R.id.textViewPoiBranchName);
				String branchName = (String) data.get("branch_name");

				if (TextUtils.isEmpty(branchName)) {
					tvBranchName.setText("");
				} else {
					tvBranchName.setText("(" + branchName + ")");
				}
				TextView tvPoiCategory = (TextView) view.findViewById(R.id.textViewPoiCategory);
				tvPoiCategory.setText((String) data.get("category"));
				TextView tvPoiRegion = (TextView) view.findViewById(R.id.textViewPoiRegion);
				tvPoiRegion.setText((String) data.get("region"));

				String price = (String) data.get("price");
				TextView tvPrice = (TextView) view.findViewById(R.id.textViewPoiAvgPrice);
				if (TextUtils.isEmpty(price)) {
					tvPrice.setText("");
				} else {
					tvPrice.setText(mContext.getString(R.string.per_capita) + price);
				}

				RatingBar ratingBar = (RatingBar) view.findViewById(R.id.ratingBarPoi);
				ratingBar.setRating((Float) data.get("rating"));

				TextView tvDistance = (TextView) view.findViewById(R.id.textViewPoiDistance);
				Object distanceObj = data.get("distance");
				if (distanceObj != null) {
					tvDistance.setText(Util.trans2Length((Integer) distanceObj));
				} else {
					tvDistance.setText(dataEmpty);
				}

				TextView tvAddr = (TextView) view.findViewById(R.id.textViewPoiAddr);
				String address = (String) data.get("address");
				if (TextUtils.isEmpty(address)) {
					address = dataEmpty;
				}
				tvAddr.setText(address);
				tvAddr.setOnClickListener(mPoiItemExtendClickListener);

				TextView tvPhone = (TextView) view.findViewById(R.id.textViewPoiPhone);
				TextView tvSave = (TextView) view.findViewById(R.id.textViewPoiPhoneSave);
				String phone = (String) data.get("phone");
				if (TextUtils.isEmpty(phone)) {
					tvPhone.setVisibility(View.GONE);
					tvSave.setVisibility(View.GONE);
				} else {
					if (tvPhone.getVisibility() != View.VISIBLE) {
						tvPhone.setVisibility(View.VISIBLE);
					}
					if (tvSave.getVisibility() != View.VISIBLE) {
						tvSave.setVisibility(View.VISIBLE);
					}
					tvPhone.setText(phone);
					tvPhone.setOnClickListener(mPoiItemExtendClickListener);
					tvSave.setOnClickListener(mPoiItemExtendClickListener);
				}

				TextView tvForward = (TextView) view.findViewById(R.id.textViewPoiPhoneForward);
				tvForward.setOnClickListener(mPoiItemExtendClickListener);

				TextView tvMore = (TextView) view.findViewById(R.id.textViewPoiMore);
				String url = (String) data.get("url");
				if (TextUtils.isEmpty(url)) {
					tvMore.setVisibility(View.GONE);
				} else {
					if (tvMore.getVisibility() != View.VISIBLE) {
						tvMore.setVisibility(View.VISIBLE);
					}
					tvMore.setVisibility(View.VISIBLE);
					tvMore.setOnClickListener(mPoiItemExtendClickListener);
				}

				View expand = view.findViewById(R.id.linearLayoutPoiItemExtend);
				expand.setVisibility(View.GONE);
			}
		});
		mPoiContentView.setPoiData(getPoiData(mPOIInfos));
		mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_DONE);
	}

	private List<HashMap<String, Object>> getPoiData(List<PoiInfo> result) {
		List<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
		for (PoiInfo info : result) {
			HashMap<String, Object> item = new HashMap<String, Object>();
			item.put("name", info.getName());
			item.put("branch_name", info.getBranchName());
			if (info.getCategories() != null && info.getCategories().length > 0) {
				item.put("category", info.getCategories().toString());
			} else if (!TextUtils.isEmpty(info.getTypeDes())) {
				item.put("category", info.getTypeDes());
			}

			if (info.getRegions() != null && info.getRegions().length > 0) {
				item.put("region", info.getRegions().toString());
			}
			item.put("rating", info.getRating());

			item.put("distance", info.getDistance());

			item.put("address", info.getAddress());
			item.put("phone", info.getTel());
			item.put("url", getWapUrl(info.getUrl()));
			item.put("city", info.getCity());

			item.put("latitude", info.getLatitude());
			item.put("longtitude", info.getLongitude());
			data.add(item);
		}

		return data;
	}

	private String getWapUrl(String url) {
		if (TextUtils.isEmpty(url)) {
			return url;
		} else {
			return url.replaceFirst("www.dianping.com", "m.dianping.com");
		}
	}

	private String getFormatPoiInfo(Context context, String name, String branchName, String address, String phone) {
		StringBuilder builder = new StringBuilder();
		builder.append(name);
		if (!TextUtils.isEmpty(branchName)) {
			builder.append(context.getString(R.string.left_parentheses));
			builder.append(branchName);
			builder.append(context.getString(R.string.right_parentheses));
		}
		if (!TextUtils.isEmpty(address)) {
			builder.append("\n");
			builder.append(context.getString(R.string.address));
			builder.append(address);
		}

		if (!TextUtils.isEmpty(phone)) {
			builder.append("\n");
			builder.append(context.getString(R.string.phone));
			builder.append(phone);
		}
		return builder.toString();
	}

	@Override
	public void release() {
		super.release();
		if (mPOIInfos != null) {
			mPOIInfos.clear();
			mPOIInfos = null;
		}
		mCurrentLocation = null;
		SORT_ITEMS = null;
		if (mPoiContentView != null) {
			mPoiContentView.setOnItemClickListener(null);
			mPoiContentView.setPoiListViewBinder(null);
			mPoiContentView = null;
		}
		if (mContentViewHeader != null) {
			mContentViewHeader.setSortChangedListener(null);
			mContentViewHeader = null;
		}
	}

	@Override
	public void onTTSEnd() {
		super.onTTSEnd();
		// mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_DISMISS_WINDOW);
	}
}
