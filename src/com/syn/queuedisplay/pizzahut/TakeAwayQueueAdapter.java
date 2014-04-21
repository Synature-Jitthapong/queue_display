package com.syn.queuedisplay.pizzahut;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class TakeAwayQueueAdapter extends BaseAdapter{
	private Context mContext;
	private List<TakeAwayData> mTakeAwayLst;
	
	public TakeAwayQueueAdapter(Context c, List<TakeAwayData> takeLst){
		mContext = c;
		mTakeAwayLst = takeLst;
	}
	
	@Override
	public int getCount() {
		return mTakeAwayLst != null ? mTakeAwayLst.size() : 0;
	}

	@Override
	public TakeAwayData getItem(int position) {
		return mTakeAwayLst.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater)
				mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				
		convertView = inflater.inflate(R.layout.take_away_template, null);
		TextView tvName = (TextView) convertView.findViewById(R.id.textViewTakeName);
		TextView tvWait = (TextView) convertView.findViewById(R.id.textViewWaitingTime);
		TextView tvStatus = (TextView) convertView.findViewById(R.id.textViewTakeStatus);
		TextView tvNo = (TextView) convertView.findViewById(R.id.textViewTakeNo);
		
		TakeAwayData takeData = mTakeAwayLst.get(position);
		tvNo.setText(takeData.getSzQueueName());
		tvName.setText(takeData.getSzTransName());
		tvStatus.setText(takeData.getSzKdsStatusName());
		tvWait.setText(takeData.getSzStartDateTime());
		tvNo.setSelected(true);
		tvName.setSelected(true);
		tvStatus.setSelected(true);
		
		return convertView;
	}

}
