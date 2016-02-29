package com.santeh.inboxlistproject.util;

import android.content.Context;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.santeh.inboxlistproject.DB.SMSDataSource;
import com.santeh.inboxlistproject.R;
import com.santeh.inboxlistproject.main.Branch;

import java.util.List;

public class Adapter_Branch extends ArrayAdapter<Branch> {

	SMSDataSource db;
	Context context;
	LayoutInflater inflater;
	List<Branch> ItemList;
	ListView listViewItem;
	int positions = 0;
	String tag = "CreateNew ArrayAdapter";
	private SparseBooleanArray mSelectedItemsIds;

	public Adapter_Branch(Context context, int resourceId, List<Branch> items) {
		super(context, resourceId, items);
		db = new SMSDataSource(context);
		db.open();
		mSelectedItemsIds = new SparseBooleanArray();
		this.context = context;
		this.ItemList = items;
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		Log.d(tag, "Adapter Context");
	}

	private class ViewHolder {
		TextView name;
		TextView code;
	}

	public View getView(int position, View view, ViewGroup parent) {
		final ViewHolder holder;
		positions = position;

		Log.d(tag, "Adapter Getview");
		if (view == null) {

			Log.d(tag, "if null");
			holder = new ViewHolder();

			view = inflater.inflate(R.layout.item_listview_branch, null);

			holder.code = (TextView) view.findViewById(R.id.lbl_listview_branch_code);
			holder.name = (TextView) view.findViewById(R.id.lbl_listview_branch_name);
			view.setTag(holder);
		}
		else
		{
			Log.d(tag, "if not null");
			holder = (ViewHolder) view.getTag();
		}

		// Capture position and set to the TextViews
		holder.code.setText(ItemList.get(position).getBranchcode());//reversed this//
		holder.name.setText(ItemList.get(position).getBranchname());


		return view;
	}

	@Override
	public void remove(Branch object) {
		ItemList.remove(object);
		notifyDataSetChanged();
	}

	public List<Branch> getAnswerList() {
		return ItemList;
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

}
