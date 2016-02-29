package com.santeh.inboxlistproject.util;

import java.util.List;

import com.santeh.inboxlistproject.R;
import android.content.Context;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class Adapter_NewMessage_Contacts extends ArrayAdapter<NewMessage_Contacts> {

	Context context;
	LayoutInflater inflater;
	List<NewMessage_Contacts> contactList;
	ListView list;
	int positions;
	int finalpos;
	private SparseBooleanArray mSelectedItemsIds;
	
	public Adapter_NewMessage_Contacts(Context context, int resourceId,
			List<NewMessage_Contacts> answerList) {
		super(context, resourceId, answerList);
		mSelectedItemsIds = new SparseBooleanArray();
		this.context = context;
		this.contactList = answerList;
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//		Log.d("ADAPTER", "context" +"\n ------" + String.valueOf(context) +"\n ------"+ 
//		String.valueOf(answerList)+"\n ------"+String.valueOf(inflater));
	}
	
	
	private class ViewHolder {
		TextView name;
		TextView type;
		TextView number;
		EditText search;
		
	}
	
	public View getView(int position, View view, ViewGroup parent) {
		final ViewHolder holder;
		positions = position;
		if (view == null) {
			
			holder = new ViewHolder();
			
			view = inflater.inflate(R.layout.item_listview_maincontacts, null);
			
			holder.type  = (TextView) view.findViewById(R.id.textView_mainInbox_contactType);
			holder.name = (TextView) view.findViewById(R.id.textView_mainInbox_contactName);
			holder.number = (TextView) view.findViewById(R.id.textView_mainInbox_contactNumber);
//			holder.number = (TextView) view.findViewById(R.id.editText_new_messageNumber);
			// Locate the ImageView in listview_item.xml
			view.setTag(holder);
		} 
		else 
		{
			Log.d("ADAPTER", "not null");
			holder = (ViewHolder) view.getTag();
		}
		
		Log.d("ADAPTER", "set views");
		// Capture position and set to the TextViews
		final NewMessage_Contacts deliver = getItem(position);
		
		
		holder.name.setText(contactList.get(position).getcontactName());
		holder.number.setText(contactList.get(position).getcontactNumber());
		holder.type.setText("<"+contactList.get(position).getcontactType()+">");
		
		
		
		Log.d("ADAPTER", "return view");
		return view;
	}
	

	@Override
	public void remove(NewMessage_Contacts object) {
		contactList.remove(object);
		notifyDataSetChanged();
	}

	public List<NewMessage_Contacts> getAnswerList() {
		return contactList;
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
