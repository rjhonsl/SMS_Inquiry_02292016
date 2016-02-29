package com.santeh.inboxlistproject.util;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.format.DateFormat;
import android.util.Log;

import com.santeh.inboxlistproject.main.Message;

public class SMSFetchCommands {

	
	//theses gets the values from inbox
		public ArrayList<Message> fetchInboxSms(int type, Context context) {
			Log.d("VAR_Start_ fetchInboxSms", "fetchInboxSms");
			
			ArrayList<Message> smsInbox = new ArrayList<Message>();

			Uri uriSms = Uri.parse("content://sms");
		
			Cursor cursor = context.getContentResolver()
					.query(uriSms,
							new String[] { "_id", "address", "date", "body",
									"type", "read" }, "type=" + type, null,
							"date" + " COLLATE LOCALIZED ASC");
			if (cursor != null) {
				cursor.moveToLast();
				if (cursor.getCount() > 0) {

					do {

						Message message = new Message();
						message.messageNumber = cursor.getString(cursor.getColumnIndex("address"));
						message.messageContent = cursor.getString(cursor.getColumnIndex("body"));
						
						String date =  cursor.getString(cursor.getColumnIndex("date"));
						Long ms = Long.parseLong(date);
//						CharSequence timeR = DateFormat.format("EEEE, MMMM dd,yyyy h:mm:ssaa", new Date(ms));
						CharSequence timeR = DateFormat.format("MM/dd/yyyy  h:mm:ssaa", new Date(ms));
						Calendar calendar = Calendar.getInstance();
					    calendar.setTimeInMillis(ms);
					    //Date date = calendar.getTime();			 
					    
						message.messageTimeReceived = timeR.toString();
						smsInbox.add(message);
						
						
	//adds data to database					
//						message.setImage("nulls");
//						message.setMessageContent(message.messageContent);
//						message.setMessageTimeReceived(message.messageTimeReceived);
//						message.setMessageNumber(message.messageNumber);
//						message = datasource.create(message);
					} while (cursor.moveToPrevious());
				}
			}

			return smsInbox;

		}
}
