package com.santeh.inboxlistproject.br;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.santeh.inboxlistproject.DB.SMSDataSource;
import com.santeh.inboxlistproject.DB.SMSOpenHelper;
import com.santeh.inboxlistproject.R;
import com.santeh.inboxlistproject.main.Activity_Home;
import com.santeh.inboxlistproject.main.Activity_Inbox;
import com.santeh.inboxlistproject.main.Activity_MainInbox_SubThread;
import com.santeh.inboxlistproject.main.Activity_UnregInbox_MainThread;
import com.santeh.inboxlistproject.main.Answer;
import com.santeh.inboxlistproject.main.MainActivity;
import com.santeh.inboxlistproject.main.Message;

import java.util.Calendar;
import java.util.List;

public class SMS_BroadcastReceiver extends BroadcastReceiver {
	// Get the object of SmsManager
	SMSDataSource db;
	public static String contactNumber="", contactName="" , branchName="";
	String branchCode="";
	Context ctx;
	Activity activity;
	int counter = 0;
	private SharedPreferences settings;

	public void onReceive(Context context, Intent intent) {
		ctx = context;
		db = new SMSDataSource(context);
		db.open();
		int commaCounter = 0;
		String deliveryCode = "";
		String branchCode1 = "", branchCode2 = "";
		ActivityManager am = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);
		List< ActivityManager.RunningTaskInfo > taskInfo = am.getRunningTasks(1); 

		// Retrieves a map of extended data from the intent.
		final Bundle extras = intent.getExtras();

		try {
			
			if (extras != null) {

				Object[] pdusObj = (Object[]) extras.get("pdus");
				SmsMessage[] messages = new SmsMessage[pdusObj.length];
				
				for (int i = 0; i < pdusObj.length; i++){
					messages[i] = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
				}
				//gets all values from new messages and puts it to variables for storage
				String finalmessage, finalNumber, finalBody;
				long finalDate; int finalStatus;
				finalNumber = messages[0].getDisplayOriginatingAddress();
				finalBody =  messages[0].getMessageBody();
				finalDate = messages[0].getTimestampMillis();
				finalStatus = messages[0].getStatus();
				
//				check if message is multipart or not
				if (messages.length == 1) {
					finalmessage = messages[0].getDisplayMessageBody();
				}
				//appends if message is multipart
				else{
					StringBuilder bodyText = new StringBuilder();
					for (int a = 0; a < messages.length; a++) {
					      bodyText.append(messages[a].getDisplayMessageBody());
					    }
					finalmessage = String.valueOf(bodyText);
				}
				
				String phoneNumber = finalNumber;
				String senderNum = phoneNumber;
				String message = finalmessage;
				String body = finalBody;
				long date = finalDate;
				int status = finalStatus;
				
				contactName = phoneNumber;
//				CharSequence timeR = DateFormat.format("MM/dd/yyyy  h:mm:ssAA", new Date(date));
				
				Calendar calendar = Calendar.getInstance();
				calendar.setTimeInMillis(date);

				for(int x = 0, n = body.length() ; x < n ; x++) { 
//					Log.d("LOOP","loop");
					char c = body.charAt(x);
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
				
				String codeDB = check_deliveryCode(deliveryCode);
				// Log.d("SMSReceiver_Delivery Code", deliveryCode + " "+ codeDB);
				if (codeDB.equals(null) || codeDB.isEmpty()) {
					// Log.d("SMSReceiverCHECK if null", codeDB + " L: " + codeDB.length());
				}
				String sender_num_six = senderNum.substring(0, 3);
				String sender_num_zero = senderNum.substring(0, 1);
				
				// Log.d("Phone", sender_num_six + " " +sender_num_zero);
				if (sender_num_six.equals("+63") || sender_num_zero.equals("0")) {
					String finalNum, Num=null;
					if (sender_num_six.equals("+63")) {
						Num = senderNum.substring(3, senderNum.length());
					}
					if (sender_num_zero.equals("0")) {
						Num = senderNum.substring(1, senderNum.length());
					}
					finalNum = checkContacts(Num);
					branchCode = checkBranch(branchCode2);
					
					//if num is registered and delivery code is matched and branch code match
					if (Num.equalsIgnoreCase(finalNum) && !codeDB.isEmpty() && branchCode2.equals(branchCode)) {

						Log.d("CONFIRMATION", "Confirmed");
						/***
						 * BELOW IS THE FUNCTION USED TO INSTANT REPLY FOR DELIVERY REQUEST CONFIRMATION OR RECEIVING CONFIRMATION
						 ***/
						String key;
						if (message.length()>3){
							key = message.substring(message.length()-3, message.length());
						}
						else{
							key="";
						}

						//if existing on Queue --- RECEIVED TEXT SHOULD HAVE 'SNT' AT THE END //////////////
						Cursor cursor = compareMessageToQueue(message.substring(0, message.length() - 3));
						Answer queue = new Answer();
						if (cursor.moveToFirst() && key.equalsIgnoreCase("SNT")) {
							queue.setAnswerID(cursor.getString(cursor.getColumnIndex(SMSOpenHelper.COLUMN_ID)));
							queue.setAnswerAnswer(cursor.getString(cursor.getColumnIndex(SMSOpenHelper.COLUMN_QUEUE_answer)));
							queue.setAnswerContent(cursor.getString(cursor.getColumnIndex(SMSOpenHelper.COLUMN_QUEUE_Content)));
							queue.setAnswerTimeReceived(cursor.getString(cursor.getColumnIndex(SMSOpenHelper.COLUMN_QUEUE_TimeReceived)));
							queue.setAnswerNumber(cursor.getString(cursor.getColumnIndex(SMSOpenHelper.COLUMN_QUEUE_Sender)));
							queue.setAnswerStatust(cursor.getString(cursor.getColumnIndex(SMSOpenHelper.COLUMN_QUEUE_status)));
							Log.d("CONFIRMATION", "Not null");
							if(cursor.getCount()>0) {
								if (db.deleteRow_queue(Long.parseLong(queue.getAnswerID()))){
									db.create_replies(queue);
								}
							}
						}
						else{

							Log.d("CONFIRMATION", "AutoReply - before boolean");
							boolean isExistingInRequest = compareMessageTDeliveryRequest(message);
							Log.d("CONFIRMATION", "AutoReply - after boolean");
							if(isExistingInRequest /*&& cursor.moveToFirst()*/){
								Log.d("CONFIRMATION", "AutoReply - TRUE existing in the del");
								Toast.makeText(ctx, "Request already exist in list.",Toast.LENGTH_SHORT).show();
							}
							else{
								Log.d("CONFIRMATION", "AutoReply - FALSE new request");
								final Message inbox = new Message();
								inbox.setMessageNumber(finalNum);
								inbox.setMessageContent(message);
								inbox.setMessageTimeReceived(String.valueOf(date));
								inbox.setMessagestatus(String.valueOf(status));
								db.create_inbox(inbox);

								SmsManager sms = SmsManager.getDefault();
								String sendTO = finalNumber;
								Log.d("AUTOCONFIRM", finalNum);
								sms.sendTextMessage(finalNumber, null, message + "RVD", null, null);
								showNotification(contactName, message);//notify

							}

						}
						cursor.close();
						refreshView(ctx, finalNum);
					}

					/**
					*
					* BELOW ARE UNUSED METHODS!
					*
					 * **/
					//if only matched by number//for registered
					else if(Num.equalsIgnoreCase(finalNum))
					{
						Log.d("SMSReceiver insertoDB", "Registered");

						refreshView(context, senderNum);
					}
					else{
						Log.d("SMSReceiver insertoDB", "unRegistered");

						refreshView(context, senderNum);
					}
				}
				else
				{
//					Log.d("SMSReceiver insertoDB", "unRegistered");
//					String timeReceived = String.valueOf(date);
//					final MainInbox main = new MainInbox();
//					main.setMainSender(senderNum);
//					main.setMainContent(message);
//					main.setMainTimeReceived(timeReceived);
//					main.setMainTimeSent("-");
//					main.setMainDelivery("-");
//					main.setMainRead("1");
//					main.setMainName(name);
//					main.setMainUnregStat("0");
//					db.createMainInbox(main);
//
//					showNotificationMain_unreg(contactName, "not FIltered");
					refreshView(ctx,senderNum);
				}
			} // bundle is null
		} catch (Exception e) {
			// Log.d("SmsReceiver", "Exception smsReceiver: " +e);
		}//end of Try/Catch
		
	}   //end of onreceive

	private void refreshView(Context context, String senderNum) {
		if (Activity_Home.active) {
            Intent refresh = new Intent(context, Activity_Home.class);
            refresh.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
            context.startActivity(refresh);
        }

		if (Activity_Inbox.active) {
            Intent refresh = new Intent(context, Activity_Inbox.class);
            refresh.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
            context.startActivity(refresh);
        }

		if (Activity_UnregInbox_MainThread.active) {
            Intent refresh = new Intent(context, Activity_UnregInbox_MainThread.class);
            refresh.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
            context.startActivity(refresh);
        }

		if (Activity_MainInbox_SubThread.active && Activity_MainInbox_SubThread.activeNum.equals(senderNum)) {
            Intent refresh = new Intent(context, Activity_MainInbox_SubThread.class);
            refresh.putExtra("sender", senderNum);
            refresh.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
            context.startActivity(refresh);
        }
	}


	public String checkContacts(String sender) {
		String contact = null;
		Cursor cursor = db.getRow_search_contacts(sender);
		if (cursor.moveToFirst()) {
			int id = cursor.getInt(SMSOpenHelper.C_no_CONTACTS_ID);
			contact = cursor.getString(SMSOpenHelper.C_no_CONTACTS_contact_number);
			contactNumber = cursor.getString(SMSOpenHelper.C_no_CONTACTS_contact_number);
			contactName = cursor.getString(SMSOpenHelper.C_no_CONTACTS_contact_name);
		}
		else
		{
			contact = "";
			contactNumber = "";
			contactName = "";
		}
		cursor.close();
		return contactNumber;
	}

	public Cursor compareMessageToQueue(String message) {
		Log.d("CONFIRMATION", "compare message to Queue");
		String idinDB = "null";
		Cursor cursor = db.getRow_search_queue_message(message);

		return cursor;
	}
	public boolean compareMessageTDeliveryRequest(String message) {
		boolean isExisting = false;
		Log.d("CONFIRMATION", "compare message to Request");
		String idinDB = "null";
		Cursor cursor = db.getRow_search_deliveryRequest_message(message);
		Log.d("CONFIRMATION", "count: "+cursor.getCount());
		if (cursor.getCount()>0){
			isExisting = true;
		}
		return isExisting;
	}
	
	
	public String checkBranch(String branch) {
		Cursor cursor = db.getRow_search_branch(branch);
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
		return branchCode;
	}
	

	public String check_deliveryCode(String delivery_code) {
		String search_result="";
		// Log.d("CODE", delivery_code);
		String code = "", type = "";
		Cursor cursor = db.getRow_search_deliveryCode(delivery_code);
		
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			
			int id = cursor.getInt(SMSOpenHelper.C_no_delivery_code_ID);
			code = cursor.getString(SMSOpenHelper.C_no_delivery_code_code);
			type = cursor.getString(SMSOpenHelper.C_no_delivery_code_type);
	
			if (code==delivery_code || code.equals(delivery_code)) {
				search_result = code;
			}
		}
		cursor.close();
		return search_result;
	}
	
	
	
	

/**
 * The notification is the icon and associated expanded entry in the status
 * bar.
 */
	protected void showNotification(String contactId, String message) {
		
		// Log.d("SMS", String.valueOf("NOTIF"));
		settings = PreferenceManager.getDefaultSharedPreferences(ctx);
		String prefValue = settings.getString("pref_tone", "Silent");
		boolean bool_vibrate = settings.getBoolean("pref_vibrate", false);
		boolean bool_silent = settings.getBoolean("pref_silent_mode", false);
		boolean bool_showNotif = settings.getBoolean("pref_show_notif", false);
		Vibrator v = (Vibrator) this.ctx.getSystemService(Context.VIBRATOR_SERVICE);
		
		Uri sound = Uri.parse(prefValue);
		
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(ctx);
		if (bool_showNotif) {
		mBuilder.setSmallIcon(R.drawable.white_mail);
		mBuilder.setContentTitle("SMS Inquiry");
		mBuilder.setContentText("You have new Delivery Request!");
		}
		
		mBuilder.setAutoCancel(true);
		mBuilder.setNumber(counter);
		// Log.d("TONE_notif", String.valueOf(prefValue));
		
		if (!bool_silent) {
			mBuilder.setSound(sound);
		}
		if (bool_vibrate) {
			long[] pattern = {1000, 500, 1000, 500}; 
			mBuilder.setVibrate(pattern);
		}
	
//		int numMessages = 0;
			    
		Intent resultIntent = new Intent(ctx, MainActivity.class);
		// Because clicking the notification opens a new ("special") activity, there's
		// no need to create an artificial back stack.
		PendingIntent resultPendingIntent =
		    PendingIntent.getActivity(
		    ctx,
		    0,
		    resultIntent,
		    PendingIntent.FLAG_UPDATE_CURRENT
		);
		mBuilder.setContentIntent(resultPendingIntent);
		int mNotificationId = 001;
		// Gets an instance of the NotificationManager service
		NotificationManager mNotifyMgr = 
				(NotificationManager) ctx.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
		// Builds the notification and issues it.
		mNotifyMgr.notify(mNotificationId, mBuilder.build());
		
		
	}//end of show Notification


protected void showNotificationMain_unreg(String contactId, String message) {
		
		settings = PreferenceManager.getDefaultSharedPreferences(ctx);
		String prefValue = settings.getString("pref_tone", "Silent");
		boolean bool_vibrate = settings.getBoolean("pref_vibrate", false);
		boolean bool_silent = settings.getBoolean("pref_silent_mode", false);
		boolean bool_showNotif = settings.getBoolean("pref_show_notif", false);
		
		Uri sound = Uri.parse(prefValue);
		
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(ctx);
		
		if (bool_showNotif) {
			mBuilder.setSmallIcon(R.drawable.ic_unregistered);
			mBuilder.setContentTitle("New Message");
			mBuilder.setContentText("You haven a new Message!");
		}
		
		mBuilder.setAutoCancel(true);
		mBuilder.setNumber(counter);
		// Log.d("TONE_notif", String.valueOf(prefValue));
		
		if (!bool_silent) {
			mBuilder.setSound(sound);
		}
		if (bool_vibrate) {
			long[] pattern = {1000, 500, 1000, 500}; 
			mBuilder.setVibrate(pattern);
		}
	
//		int numMessages = 0;
			    
		Intent resultIntent = new Intent(ctx, Activity_UnregInbox_MainThread.class);
		// Because clicking the notification opens a new ("special") activity, there's
		// no need to create an artificial back stack.
		PendingIntent resultPendingIntent =
		    PendingIntent.getActivity(
		    ctx,
		    0,
		    resultIntent,
		    PendingIntent.FLAG_UPDATE_CURRENT
		);
		mBuilder.setContentIntent(resultPendingIntent);
		int mNotificationId = 003;
		// Gets an instance of the NotificationManager service
		NotificationManager mNotifyMgr = 
				(NotificationManager) ctx.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
		// Builds the notification and issues it.
		mNotifyMgr.notify(mNotificationId, mBuilder.build());
		
		
	}//end of show Notification
	
	
	
protected void showNotificationMain(String contactId, String message) {
		
		// Log.d("SMS", String.valueOf("NOTIF_MAIN"));
		settings = PreferenceManager.getDefaultSharedPreferences(ctx);
		String prefValue = settings.getString("pref_tone", "Silent");
		boolean bool_vibrate = settings.getBoolean("pref_vibrate", false);
		boolean bool_silent = settings.getBoolean("pref_silent_mode", false);
		boolean bool_showNotif = settings.getBoolean("pref_show_notif", false);
		Vibrator v = (Vibrator) this.ctx.getSystemService(Context.VIBRATOR_SERVICE);
		
		
		Uri sound = Uri.parse(prefValue);
		
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(ctx);
		if (bool_showNotif) {
		mBuilder.setSmallIcon(R.drawable.ic_white_inbox);
		mBuilder.setContentTitle("New Message");
		mBuilder.setContentText("You haven a new Message");
		}
		
		mBuilder.setAutoCancel(true);
		mBuilder.setNumber(counter);
		// Log.d("TONE_notif", String.valueOf(prefValue));
		
		if (!bool_silent) {
			mBuilder.setSound(sound);
		}
		if (bool_vibrate) {
			long[] pattern = {1000, 500, 1000, 500}; 
			mBuilder.setVibrate(pattern);
		}
	
//		int numMessages = 0;
			    
		Intent resultIntent = new Intent(ctx, Activity_Inbox.class);
		// Because clicking the notification opens a new ("special") activity, there's
		// no need to create an artificial back stack.
		PendingIntent resultPendingIntent =
		    PendingIntent.getActivity(
		    ctx,
		    0,
		    resultIntent,
		    PendingIntent.FLAG_UPDATE_CURRENT
		);
		mBuilder.setContentIntent(resultPendingIntent);
		int mNotificationId = 002;
		// Gets an instance of the NotificationManager service
		NotificationManager mNotifyMgr = 
				(NotificationManager) ctx.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
		// Builds the notification and issues it.
		mNotifyMgr.notify(mNotificationId, mBuilder.build());
		
		
	}//end of show Notification


//converts number to name
	public String numberToName(String senderNum){
		String matchedNum=senderNum;
		try {
			ContentResolver cr = ctx.getContentResolver();
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
								matchedNum = name;
							}
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
	}//end of matched num

}
