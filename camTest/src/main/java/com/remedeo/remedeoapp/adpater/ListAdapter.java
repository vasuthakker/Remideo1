package com.remedeo.remedeoapp.adpater;

import java.util.List;

import com.remedeo.remedeoapp.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListAdapter extends BaseAdapter {

	private Activity activity;
	private LayoutInflater inflater;
	private List<String> values;
	private int pos = 0;

	public ListAdapter(Activity activity, List<String> values, int position) {
		this.values = values;
		this.activity = activity;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		pos = position;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return values.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.listitem, null);
		}

		ImageView tickMark = (ImageView) convertView
				.findViewById(R.id.listitem_tick);
		TextView text = (TextView) convertView.findViewById(R.id.listitem_text);

		if (pos == position) {
			tickMark.setVisibility(View.VISIBLE);
		} else {
			tickMark.setVisibility(View.INVISIBLE);
		}
		
		text.setText(values.get(position));

		return convertView;
	}

}
