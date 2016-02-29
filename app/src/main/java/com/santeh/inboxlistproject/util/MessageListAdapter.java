package com.santeh.inboxlistproject.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.telephony.gsm.SmsManager;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.santeh.inboxlistproject.DB.SMSDataSource;
import com.santeh.inboxlistproject.DB.SMSOpenHelper;
import com.santeh.inboxlistproject.R;
import com.santeh.inboxlistproject.br.SMS_delivery;
import com.santeh.inboxlistproject.main.MainActivity;
import com.santeh.inboxlistproject.main.Message;

import java.util.ArrayList;

public class MessageListAdapter extends ArrayAdapter<Message> {

	private Context ctx;
	public ArrayList<Message> messageListArray;
	public static String ACTION_SMS_SENT = "SMS_SENT";
	public static String ACTION_SMS_DELIVERED = "SMS_DELIVERED";
	SMSDataSource datasource ;

	String dbSender, contactName, dbContent, dbTime;
	String branchCode="", unreg_status="", branchName="";
	public  String holder_number = "";
	public  String holder_content = "";
	public  String holder_timereceived = "";				
	public  String holder_answer = "";
	public 	String holder_id = "";
	int indexPos;

	Activity activity = null;
	private SparseBooleanArray mSelectedItemsIds;
	

	//STEP 11 from MainActivity
	//this ArrayList<Message> messageListArray contains recordsStored
	public MessageListAdapter(Context context, int textViewResourceId, ArrayList<Message> messageListArray) {
		super(context, textViewResourceId);
		this.messageListArray = messageListArray;
		this.ctx = context;
		activity = (Activity) ctx;
		mSelectedItemsIds = new SparseBooleanArray();
//		Log.d("INT", holder_number+ "int");
//		Log.d("INT", holder_content+ "int");
//		Log.d("INT", holder_timereceived+ "int");
//		Log.d("INT", holder_answer+ "int");
//		Log.d("INT", holder_id + "int");
		datasource = new SMSDataSource(ctx);
		datasource.open();
	}

	@Override
	//THIS Creates the VIEW 
	public View getView(int position, View convertView, ViewGroup parent) {

//		Log.d("POS", String.valueOf(position));
		Holder holder;
		View convertView1 = convertView;

		if (convertView1 == null) {

			//holder holds Message Class Values
			holder = new Holder();

//			Log.d("VAR_FROM_MessageListAdapter ctx", String.valueOf(ctx));
			//Adds cast to LayoutInflater
			//Create new LayoutInflater vi = (LayoutInflater)//context Class
			LayoutInflater vi = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			//ConvertView is a new View
			convertView1 = vi.inflate(R.layout.message_list_item, null);

			//Here is where textView layout are set before they are created/showed
			holder.messageFrom = (TextView) convertView1.findViewById(R.id.txt_msgTO);
			holder.messageContent = (TextView) convertView1.findViewById(R.id.txt_messageContent);
			holder.buttonYes = (TextView) convertView1.findViewById(R.id.cmdYes);
			holder.buttonNo = (TextView) convertView1.findViewById(R.id.cmdNo);
			holder.messagetimeReceived = (TextView) convertView1.findViewById(R.id.txt_time);
			holder.buttonCall = (TextView) convertView1.findViewById(R.id.imageButton1);
//			Log.d("VAR_Start_FROM_MessageListAdapter _getTagConvertView1", String.valueOf(convertView1.getTag()));
			convertView1.setTag(holder);
		}
		else 
		{
			holder = (Holder) convertView1.getTag();
		}



		final Message message = getItem(position);
		

		//holder.messageContent is from class Holder
		//message.MessageContent came from Message.java
		int commaCounter = 0;
		String deliveryCode = "";
		String branchCode1 = "", branchCode2 = "";
		for(int x = 0, n = message.messageContent.length() ; x < n ; x++) { 
//			Log.d("LOOP","loop");
			char c = message.messageContent.charAt(x);
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

		holder.messageFrom.setText(checkBranch(branchCode2));

		holder.messageContent.setText(message.messageContent);

//		CharSequence timeR = DateFormat.format("MMM-dd-yyyy h:mm:ssAA", new java.util.Date(date));
		holder.messagetimeReceived.setText(UIHelper.convertMillisToDate(message.messageTimeReceived));

		Log.d("ID", message.messageID);

		//ACtion if YES button is clicked
		holder.buttonYes.setText("YES");
		holder.buttonYes.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (((SmsInquiryApp) (ctx).getApplicationContext()).stillsending) {

					UIHelper.toaster( activity , "Still sending previous message. Please Wait.");
				}
				else
				{
				new AlertDialog.Builder(ctx)
    		    .setTitle("Send Reply")
    		    .setMessage("Are you sure you want to reply YES?")
    		    .setPositiveButton("SEND YES", new DialogInterface.OnClickListener() {
    		    	
    		        public void onClick(DialogInterface dialog, int which) { 
    		        	Intent sendIntent = new Intent(ACTION_SMS_SENT);
    					Intent deliverIntent = new Intent(ACTION_SMS_DELIVERED);
    					sendIntent.putExtra("extra_key", "extra_value");
    					sendIntent.putExtra("answer", "YES");
    					sendIntent.putExtra("content", message.messageContent);
    					sendIntent.putExtra("time", message.messageTimeReceived);
    					sendIntent.putExtra("number", message.messageNumber);
    					sendIntent.putExtra("fromActivity", "deliveryInbox");


    					PendingIntent piSent = PendingIntent.getBroadcast(getContext(), 0, sendIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    					PendingIntent piDelivered = PendingIntent.getBroadcast(getContext(), 0, deliverIntent, 0);

						SmsManager sms = SmsManager.getDefault();

    					ArrayList<String> messageParts = sms.divideMessage(message.messageContent);
    					((SmsInquiryApp) (ctx).getApplicationContext()).setCountParts(1);
    					((SmsInquiryApp) (ctx).getApplicationContext()).setLoopCounter(0);
    					try {
							UIHelper.toaster( activity , "Sending YES to "+ message.messageNumber );
//    						Toast.makeText(ctx, "Sending YES to "+ message.messageNumber +"", Toast.LENGTH_SHORT).show();

    						String sendTO ="+63"+message.messageNumber;
							Log.d("MTK",sendTO);
							sms.sendTextMessage(sendTO, null, message.messageContent+"YES", piSent, piDelivered);

    						deleteSMS(ctx, message.messageID);
							Intent xtent = new Intent(ctx, MainActivity.class);
							xtent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							ctx.startActivity(xtent);

    					}
    					catch (Exception e) {
							UIHelper.toaster_long( activity , "SMS to "+message.messageNumber+" failed! Please try again later!" );
//    						Toast.makeText(ctx, "SMS to "+message.messageNumber+" failed! Please try again later!", Toast.LENGTH_LONG).show();
    						e.printStackTrace();
    					}	
    		        	
    		        }
    		        
    		     })
    		    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
    		    	
    		        public void onClick(DialogInterface dialog, int which) { 
    		        }
    		        
    		     })
    		    .setIcon(android.R.drawable.ic_dialog_alert)
    		    .show();
				
				}//end of stillsending
				
			}
		});


		//ACTION if button No is clicked;
		holder.buttonNo.setText("NO");
		holder.buttonNo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
//				Log.d("STILL sending on ListAdapter for Delivery Request", String.valueOf(SMS_delivery.stillsending));
				if (SMS_delivery.stillsending) {
//					Toast.makeText(ctx, "Still sending previous message. Please Wait.", Toast.LENGTH_SHORT).show();
					UIHelper.toaster(activity,  "Still sending previous message. Please Wait.");
				}
				else
				{
				
				new AlertDialog.Builder(ctx)
    		    .setTitle("Send Reply")
    		    .setMessage("Are you sure you want to reply NO?")
    		    .setPositiveButton("SEND NO", new DialogInterface.OnClickListener() {
    		    	
    		        public void onClick(DialogInterface dialog, int which) { 

    					Intent sendIntent = new Intent(ACTION_SMS_SENT);
    					Intent deliverIntent = new Intent(ACTION_SMS_DELIVERED);
    					sendIntent.putExtra("extra_key", "extra_value");

    					sendIntent.putExtra("extra_key", "extra_value");
    					sendIntent.putExtra("answer", "NO");
    					sendIntent.putExtra("content", message.messageContent);
    					sendIntent.putExtra("time", message.messageTimeReceived);
    					sendIntent.putExtra("number", message.messageNumber);
    					sendIntent.putExtra("fromActivity", "deliveryInbox");

    					PendingIntent piSent = PendingIntent.getBroadcast(getContext(), 0, sendIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    					PendingIntent piDelivered = PendingIntent.getBroadcast(getContext(), 0, deliverIntent, 0);
    					SmsManager smsManager = SmsManager.getDefault();

    					ArrayList<String> messageParts = smsManager.divideMessage(message.messageContent);
    					int nMsgParts = messageParts.size();
    					((SmsInquiryApp) (ctx).getApplicationContext()).setCountParts(1);
    					((SmsInquiryApp) (ctx).getApplicationContext()).setLoopCounter(0);

    					try {

    						Log.d("ID_NO", message.messageID);
//    						Toast.makeText(ctx, "Sending NO to "+ message.messageNumber +"", Toast.LENGTH_SHORT).show();
    						UIHelper.toaster(activity,  "Sending NO to +63"+ message.messageNumber +"");

							SmsManager sms = SmsManager.getDefault();
    						String sendTO ="+63" +message.messageNumber;
    						sms.sendTextMessage(sendTO, null, message.messageContent+"NO", piSent, piDelivered);
    						deleteSMS(ctx, message.messageID);
    						Intent xtent = new Intent(ctx, MainActivity.class);
    						xtent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    						ctx.startActivity(xtent);
    					}
    					catch (Exception e) {
    						//has an error when delete fails
							UIHelper.toaster_long(activity, "SMS to " + message.messageNumber + " failed! INVALID NUMBER!");
//    						Toast.makeText(ctx, "SMS to "+message.messageNumber + " failed! INVALID NUMBER!" + e, Toast.LENGTH_LONG).show();
    						e.printStackTrace();
    					}
    		        }
    		        
    		     })
    		    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
    		        public void onClick(DialogInterface dialog, int which) {
    		        }
    		        
    		     })
    		    .setIcon(android.R.drawable.ic_dialog_alert)
    		    .show();
			
				}//end of still sending
			}
		});


		//WHEN CAll Button isClicked
		holder.buttonCall.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_DIAL);
				intent.setData(Uri.parse("tel:+" + message.messageNumber));
				ctx.startActivity(intent);

			}
		});

		return convertView1;
	}



	//deletes SMS
	public void deleteSMS(Context context, String id) {

		String delete_id = checkInbox(id);
		datasource.deleteRow_inbox(Long.parseLong(delete_id));
		//		UIHelper.toaster(context.this, " " + message + " " + number);
	}
	
	public String matchContact(String number){
		Cursor cur = datasource.getRow_search_contacts(number);
		
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

	//returns Contact	
	public String checkInbox(String id) {

		Log.d("INBOX to qeury", id);
		Cursor cursor = datasource.getRow_search_inbox(id);
		String dbID = null;
		if (cursor.moveToFirst()) {

			dbID = cursor.getString(SMSOpenHelper.C_no_Inbox_ID);
			dbSender = cursor.getString(SMSOpenHelper.C_no_Inbox_SENDER);
			dbContent = cursor.getString(SMSOpenHelper.C_no_Inbox_CONTENT);
			dbTime = cursor.getString(SMSOpenHelper.C_no_Inbox_TIME_RECEIVED);
		}
		else
		{
			//do nothing meh...
		}
		cursor.close();
		Log.d("INBOX_return", dbSender + dbContent + dbTime);
	
		return dbID;
	}

	@Override
	////STEP 12  MessageListAdapter jumps to here unknowingly
	public int getCount() {
		return messageListArray.size();
	}

	@Override
	public Message getItem(int position) {
		return messageListArray.get(position);
	}

	public void setArrayList(ArrayList<Message> messageList) {
		this.messageListArray = messageList;
		notifyDataSetChanged();
	}
	
	private class Holder {
		public TextView messageFrom, messageContent, messagetimeReceived;
		public TextView buttonYes, buttonNo;
		public TextView buttonCall;
	}
	public String checkBranch(String branch) {
		Cursor cursor = datasource.getRow_search_branch(branch);
		if (cursor.moveToFirst()) {
			int id = cursor.getInt(SMSOpenHelper.C_no_CONTACTS_ID);
			branchCode = cursor.getString(cursor.getColumnIndex(SMSOpenHelper.COLUMN_BRANCHES_code));
			branchName = cursor.getString(cursor.getColumnIndex(SMSOpenHelper.COLUMN_BRANCHES_name));
		}
		else
		{
			branchCode ="";
			branchName = "";
		}
		cursor.close();
		return branchName;
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
