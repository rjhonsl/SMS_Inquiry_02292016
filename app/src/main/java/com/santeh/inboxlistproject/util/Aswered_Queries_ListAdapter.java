package com.santeh.inboxlistproject.util;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.telephony.SmsManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.santeh.inboxlistproject.DB.SMSDataSource;
import com.santeh.inboxlistproject.DB.SMSOpenHelper;
import com.santeh.inboxlistproject.R;
import com.santeh.inboxlistproject.br.SMS_delivery;
import com.santeh.inboxlistproject.main.Answer;

import java.util.ArrayList;
import java.util.List;


public class Aswered_Queries_ListAdapter extends ArrayAdapter<Answer> {
	// Declare Variables
	private static String ACTION_SMS_SENT = "SMS_SENT";
	private static String ACTION_SMS_DELIVERED = "SMS_DELIVERED";
	Context context;
	LayoutInflater inflater;
	List<Answer> answerList;
	ListView list;
	SMSDataSource db;
	int finalpos;
	private SparseBooleanArray mSelectedItemsIds;

	public Aswered_Queries_ListAdapter(Context context, int resourceId,
			List<Answer> answerList) {
		super(context, resourceId, answerList);
		mSelectedItemsIds = new SparseBooleanArray();
		this.context = context;
		this.answerList = answerList;
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		db = new SMSDataSource(context);
		db.open();
	}

	private class ViewHolder {
		TextView number;
		TextView content;
		TextView answer;
		TextView status;
		TextView time_received;
		TextView resend;
		ProgressBar progress;
	}

	public View getView(int position, View view, ViewGroup parent) {
		final ViewHolder holder;
		if (view == null) {

			holder = new ViewHolder();
			view = inflater.inflate(R.layout.item_listview_answered_queries, null);
			holder.number = (TextView) view.findViewById(R.id.textView_answer_SENDER);
			holder.content = (TextView) view.findViewById(R.id.textView_answer_CONTENT);
			holder.answer = (TextView) view.findViewById(R.id.textView_answer_ANSWER);
			holder.time_received = (TextView) view.findViewById(R.id.textView_answer_TIME_RECEIVED);
			holder.status = (TextView) view.findViewById(R.id.textView_answer_STATUS);
			holder.resend = (TextView) view.findViewById(R.id.textView_answer_STATUS_TSR);
			holder.progress = (ProgressBar) view.findViewById(R.id.progressBar1);

			// Locate the View in listview_item.xml
			view.setTag(holder);
		} 
		else 
		{
			holder = (ViewHolder) view.getTag();
		}


		// Capture position and set to the TextViews
		final Answer answer = getItem(position);
		String number = answerList.get(position).getAnswerNumber();
		String matchToName = matchContact(number);
		
		Log.d("MATCH Name", matchToName + number);

		holder.number.setText(matchToName);
		holder.content.setText(answerList.get(position).getAnswerContent());

		String branchCode2 = getbranchCode(position);

		holder.number.setText(checkBranch(branchCode2));

		holder.answer.setText(answerList.get(position).getAnswerAnswer());
		if(!answerList.get(position).getAnswerAnswer().equalsIgnoreCase("yes")) {
			holder.answer.setBackgroundResource(R.drawable.bg_replied_answer_no);
		}
		else{
			holder.answer.setBackgroundResource(R.drawable.bg_replied_answer);
		}

		long date = Long.parseLong(answerList.get(position).getAnswerTimeReceived());
		CharSequence timeR = DateFormat.format("MMM-dd-yyyy h:mmAA", new java.util.Date(date));
		holder.time_received.setText("Received: " + (String.valueOf(timeR)));

		holder.status.setText(answerList.get(position).getAnswerStatus());
		String stat = answerList.get(position).getAnswerStatus();

		
		Log.d("MATCH Name", "before resend visibility-progress");

		holder.resend.setVisibility(View.VISIBLE);
		holder.progress.setVisibility(View.GONE);

		Log.d("MATCH Name", "before if" + stat);
		if (stat.equals("SENT")) {
			holder.status.setTextColor(Color.parseColor("#388e3c"));
			holder.progress.setVisibility(View.GONE);
			holder.resend.setVisibility(View.VISIBLE);
		}
		else if (stat.equals("FAILED")) {
			holder.status.setTextColor(Color.parseColor("#ef5350"));
			holder.resend.setVisibility(View.VISIBLE);
		}
		
		Log.d("MATCH Name", "before resend");
		holder.resend.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				Log.d("STILL sending on ListAdapter for Delivery Request", String.valueOf(SMS_delivery.stillsending));
				if (SMS_delivery.stillsending) {
					Toast.makeText(context, "Still sending previous message. Please Wait.", Toast.LENGTH_SHORT).show();
				}
				else
				{
					Intent sendIntent = new Intent(ACTION_SMS_SENT);
					Intent deliverIntent = new Intent(ACTION_SMS_DELIVERED);
					sendIntent.putExtra("extra_key", "extra_value");
					sendIntent.putExtra("answer", answer.getAnswerAnswer());
					sendIntent.putExtra("content", answer.getAnswerContent());
					sendIntent.putExtra("time", answer.getAnswerTimeReceived());
					sendIntent.putExtra("number",answer.getAnswerNumber());
					sendIntent.putExtra("id", answer.getAnswerID());
					sendIntent.putExtra("fromActivity", "answeredQeuryInbox");
					//					Toast.makeText(context,  "Answer: "+ answer.getAnswerAnswer() +" Content: " +answer.getAnswerContent()+" time: "
					//									+answer.getAnswerTimeReceived()+" number: " +answer.getAnswerNumber()+" ID: " + answer.getAnswerID(), Toast.LENGTH_LONG).show();
					PendingIntent piSent = PendingIntent.getBroadcast(getContext(), 0, sendIntent, PendingIntent.FLAG_UPDATE_CURRENT);
					PendingIntent piDelivered = PendingIntent.getBroadcast(getContext(), 0, deliverIntent, 0);
					SmsManager smsManager = SmsManager.getDefault();
					ArrayList<String> messageParts = smsManager.divideMessage(answer.getAnswerContent());
					int nMsgParts = messageParts.size();
					((SmsInquiryApp) (context).getApplicationContext()).setCountParts(1);
					((SmsInquiryApp) (context).getApplicationContext()).setLoopCounter(0);

					Toast.makeText(context, answer.getAnswerContent()+answer.getAnswerAnswer(), Toast.LENGTH_LONG).show();
					try {

						SmsManager sms = SmsManager.getDefault();
						String sendTO ="+63" +answer.getAnswerNumber();
						String TSR = null;
						sms.sendTextMessage(sendTO, null, answer.getAnswerContent()+answer.getAnswerAnswer(), piSent, piDelivered);
						holder.progress.setVisibility(View.VISIBLE);

					}
					catch (Exception e) {
						Toast.makeText(context, "RESENDING failed! Please try again later!", Toast.LENGTH_LONG).show();
						e.printStackTrace();
					}
				}
			}
		});

		return view;
	}

	@NonNull
	private String getbranchCode(int position) {
		int commaCounter = 0;
		String branchCode1 = "", branchCode2 = "", deliveryCode="";
		for(int x = 0, n = answerList.get(position).getAnswerContent().length() ; x < n ; x++) {
//			Log.d("LOOP","loop");
			char c = answerList.get(position).getAnswerContent().charAt(x);
			//OrderCode
			if (x<3) {
				deliveryCode = deliveryCode + c;
			}
			//Format ; and BranchCode
			if (c==';') {
				commaCounter++;
			}
			if (commaCounter==1) {
				branchCode1 = branchCode1 + c;
			}
		}

		//final Brancode!
		if (commaCounter!=0) {
			branchCode2 = branchCode1.substring(5, 8);
		}
		//final Brancode!
		return branchCode2;
	}

	public String checkBranch(String branch) {
		String branchName="";
		Cursor cursor = db.getRow_search_branch(branch);
		if (cursor.moveToFirst()) {
			branchName = cursor.getString(cursor.getColumnIndex(SMSOpenHelper.COLUMN_BRANCHES_name));
		}
		else
		{
			branchName = "Unregistered";
		}
		cursor.close();
		return branchName;
	}



	@Override
	public void remove(Answer object) {
		answerList.remove(object);
		notifyDataSetChanged();
	}



	public List<Answer> getAnswerList() {
		return answerList;
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



	public String matchContact(String number){
		Log.d("MATCH matchContact", number);
		Cursor cur = db.getRow_search_contacts(number);

		String match = null;
		if (cur.moveToFirst()) {

			match = cur.getString(SMSOpenHelper.C_no_CONTACTS_contact_name);
		}
		else
		{
			//do nothing meh...
		}
		cur.close();

		return match;
	}


}
