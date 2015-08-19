package cn.yunzhisheng.vui.assistant.widget.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.ilincar.voice.R;

public class MapNearbyAdapter extends BaseAdapter {

	private Context context;
	private int layoutResourceId;
	private String[] data;

	public MapNearbyAdapter(Context _context, int _layoutResourceId, String[] _data) {
		context = _context;
		layoutResourceId = _layoutResourceId;
		data = _data;
	}

	@Override
	public int getCount() {
		return data.length;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = View.inflate(context, layoutResourceId, null);
		TextView searchTip = (TextView) convertView.findViewById(R.id.textViewNearbyContent);
		searchTip.setText(data[position]);
		return convertView;
	}

}
