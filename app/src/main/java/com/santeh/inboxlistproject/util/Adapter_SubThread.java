package com.santeh.inboxlistproject.util;

import java.util.List;

import com.santeh.inboxlistproject.main.MainInbox;
import com.santeh.inboxlistproject.R;
import com.santeh.inboxlistproject.DB.SMSDataSource;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.provider.ContactsContract;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Adapter_SubThread extends ArrayAdapter<MainInbox> {

	String tag = "SUBTHREAD";
	SMSDataSource db;
	
	Context context;
	LayoutInflater inflater;
	List<MainInbox> SubThreadList;
	ListView list;
	int positions;
	int finalpos;
	private SparseBooleanArray mSelectedItemsIds;
	
	public Adapter_SubThread(Context context, int resourceId,
			List<MainInbox> threadList) {
		super(context, resourceId, threadList);
		
		db = new SMSDataSource(context);
		db.open();
		
		
		Log.d(tag, "Context");
		mSelectedItemsIds = new SparseBooleanArray();
		this.context = context;
		this.SubThreadList = threadList;
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	
	private class ViewHolder {
		TextView content;
		TextView status;
		TextView date;
		RelativeLayout rel;
		RelativeLayout rel_bottomCOntainer;
		
	}
	
	public View getView(int position, View view, ViewGroup parent) {
		final ViewHolder holder;
		positions = position;
		if (view == null) {
			Log.d(tag, "getview Null");
			
			holder = new ViewHolder();
			
			view = inflater.inflate(R.layout.item_listview_subthread, null);
			
			holder.content = (TextView) view.findViewById(R.id.textView_subthread_content);
			holder.status = (TextView) view.findViewById(R.id.textView_SubThread_Status);
			holder.date = (TextView) view.findViewById(R.id.textView_Subthread_time);
			holder.rel = (RelativeLayout) view.findViewById(R.id.rel_subthread_container);
			holder.rel_bottomCOntainer = (RelativeLayout) view.findViewById(R.id.rel_Subthread_Container_bottom);
			view.setTag(holder);
		} 
		else 
		{
			Log.d(tag+"ADAPTER", "not null");
			holder = (ViewHolder) view.getTag();
		}
		
		Log.d(tag+"ADAPTER", "set views");
		// Capture position and set to the TextViews
		final MainInbox deliver = getItem(position);
		
		//gets number of messages on a single contact
//		int cur = db.findAll_Single_MainInbox(SubThreadList.get(position).getMainSender());
//		String count = String.valueOf(cur);
		
		
		//if date difference is 24hrs 
		long date = Long.parseLong(SubThreadList.get(position).getMainTimeReceived());
		long currentTime = System.currentTimeMillis();
		long dif = currentTime - date;
		CharSequence timeR = null;
		if (dif > 86400000) {
			timeR = DateFormat.format("MMM d", new java.util.Date(date));
			
		}
		else{
			timeR = DateFormat.format("h:mm AA", new java.util.Date(date));
		}
		
		String me = SubThreadList.get(position).getMainTimeSent();
		
		if (me.equals("1")) {
			holder.content.setGravity(Gravity.LEFT);
			holder.rel_bottomCOntainer.setGravity(Gravity.RIGHT);
			holder.rel.setBackground(context.getResources().getDrawable(R.drawable.background_subthread_bubble_white));
		}
		else{
			holder.content.setGravity(Gravity.RIGHT);
			holder.rel_bottomCOntainer.setGravity(Gravity.LEFT);
			holder.rel.setBackground(context.getResources().getDrawable(R.drawable.background_subthread_bubble));
			holder.status.setVisibility(View.INVISIBLE);
		}
		
		holder.content.setText(SubThreadList.get(position).getMainContent());
		holder.date.setText(String.valueOf(timeR));//SubThreadList.get(position).getMainDelivery()
		if (SubThreadList.get(position).getMainDelivery().equalsIgnoreCase("SENT")) {
			holder.status.setTextColor(Color.parseColor("#ADAD85"));//darb brown
		}
		else
		{
			holder.status.setTextColor(Color.parseColor("#990000"));//red
		}
		holder.status.setText(SubThreadList.get(position).getMainDelivery());
		Log.d(tag, "return view");
		
		return view;
	}
	

	@Override
	public void remove(MainInbox object) {
		SubThreadList.remove(object);
		notifyDataSetChanged();
	}

	public List<MainInbox> getAnswerList() {
		return SubThreadList;
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

	//converts number to name
	public String numberToName(String senderNum){
		String matchedNum="";
		try {
			ContentResolver cr = context.getContentResolver();
			Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
					null, null, null, ContactsContract.Contacts.DISPLAY_NAME + 
					" COLLATE LOCALIZED ASC");
			if (cur.getCount() > 0) {
				while (cur.moveToNext()) {
					String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
					String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
					if (Integer.parseInt(cur.getString(
							cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
						Cursor pCur = cr.query(
								ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
								ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
								new String[]{id}, null);
						while (pCur.moveToNext()) {
							String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
							String type = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));

							phoneNo = phoneNo.replaceAll("[^0-9+]", "");//removes all non numeric character
							
							if(senderNum.equals(phoneNo)){
								Log.d("INBOX_adapter_numToName", senderNum + " = " + name);
								matchedNum = name;
							}
//							Log.d("TYPE", name + " " + type);
						}//end while
						pCur.close();
					}//end if
				}//end cursor loop
			}// end of if
		} catch (NumberFormatException e1) {
			e1.printStackTrace();
//			Toast.makeText(context, String.valueOf(e1), Toast.LENGTH_LONG).show();
		}
		return matchedNum;

		
	}

}