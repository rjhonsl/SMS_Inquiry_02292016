package com.santeh.inboxlistproject.util;

import java.util.List;

import com.santeh.inboxlistproject.main.MainInbox;
import com.santeh.inboxlistproject.R;
import com.santeh.inboxlistproject.DB.SMSDataSource;

import android.content.Context;
import android.graphics.Color;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Adapter_MainThread extends ArrayAdapter<MainInbox> {

	String tag = "INBOX";
	SMSDataSource db;
	
	Context context;
	LayoutInflater inflater;
	List<MainInbox> mainThreadList;
	ListView list;
	int positions;
	int finalpos;
	private SparseBooleanArray mSelectedItemsIds;
	
	public Adapter_MainThread(Context context, int resourceId,
			List<MainInbox> threadList) {
		super(context, resourceId, threadList);
		
		db = new SMSDataSource(context);
		db.open();
		
		
		Log.d(tag, "Context");
		mSelectedItemsIds = new SparseBooleanArray();
		this.context = context;
		this.mainThreadList = threadList;
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	
	private class ViewHolder {
		TextView sender;
		TextView content;
		TextView count;
		TextView date;
		RelativeLayout rel;
		
	}
	
	public View getView(int position, View view, ViewGroup parent) {
		final ViewHolder holder;
		positions = position;
		if (view == null) {
			Log.d(tag, "getview Null");
			
			holder = new ViewHolder();
			
			view = inflater.inflate(R.layout.item_listview_maininbox, null);
			
			holder.sender = (TextView) view.findViewById(R.id.TextView_main_Thread_number);
			holder.content = (TextView) view.findViewById(R.id.TextView_main_Thread_Content);
			holder.count = (TextView) view.findViewById(R.id.TextView_main_Thread_messageCount);
			holder.date = (TextView) view.findViewById(R.id.TextView_main_Thread_date);
			holder.rel = (RelativeLayout) view.findViewById(R.id.relative_layout_mainThread);
			view.setTag(holder);
		} 
		else 
		{
			Log.d("ADAPTER", "not null");
			holder = (ViewHolder) view.getTag();
		}
		
		Log.d("INBOX_adapter", "set views");
		// Capture position and set to the TextViews
		final MainInbox deliver = getItem(position);
		
		//gets number of messages on a single contact
		int cur = db.findAll_Single_MainInbox(mainThreadList.get(position).getMainSender());
		String count = String.valueOf(cur);
		
		
		//if date difference is 24hrs 
		long date = Long.parseLong(mainThreadList.get(position).getMainTimeReceived());
		long currentTime = System.currentTimeMillis();
		long dif = currentTime - date;
		CharSequence timeR = null;
		if (dif > 86400000) {
			timeR = DateFormat.format("MMM d", new java.util.Date(date));
		}
		else{
			timeR = DateFormat.format("h:mmAA", new java.util.Date(date));
		}
		
//		Log.d("INBOX_adapter", "set Values: "+ count + " x "+ mainThreadList.get(position).getMainSender()+" x "
//				+ mainThreadList.get(position).getMainContent() + " x "
//				+ mainThreadList.get(position).getMainTimeReceived());
		String sender = mainThreadList.get(position).getMainName();
		String readStat = mainThreadList.get(position).getMainRead();
		if (readStat.equals("1")) {
			holder.rel.setBackgroundColor(Color.parseColor("#CACACA"));
		}
		else{
			holder.rel.setBackgroundColor(Color.parseColor("#F2F2F2"));
		}
		
		holder.sender.setText(sender);
		holder.content.setText(mainThreadList.get(position).getMainContent());
		holder.count.setText(count);
		holder.date.setText(String.valueOf(timeR));
		Log.d("INBOX_adapter", "return view");
		return view;
	}//end public view
	

	@Override
	public void remove(MainInbox object) {
		mainThreadList.remove(object);
		notifyDataSetChanged();
	}

	public List<MainInbox> getAnswerList() {
		return mainThreadList;
	}

	public void toggleSelection(int position) {
		selectView(position, !mSelectedItemsIds.get(position));
	}

	public void removeSelection() {
		mSelectedItemsIds = new SparseBooleanArray();
		notifyDataSetChanged();
	}

	public void selectView(int position, boolean value) {
		if (value)
			mSelectedItemsIds.put(position, value);
		else
			mSelectedItemsIds.delete(position);
		notifyDataSetChanged();
	}

	public int getSelectedCount() {
		return mSelectedItemsIds.size();
	}

	public SparseBooleanArray getSelectedIds() {
		return mSelectedItemsIds;
	}
	

	public void notifyChange(){
		notifyDataSetChanged();
	}
}
