package com.santeh.inboxlistproject.br;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.santeh.inboxlistproject.DB.SMSDataSource;
import com.santeh.inboxlistproject.DB.SMSOpenHelper;
import com.santeh.inboxlistproject.main.Activity_Answered_Queries;
import com.santeh.inboxlistproject.main.Activity_Inbox;
import com.santeh.inboxlistproject.main.Activity_MainInbox_SubThread;
import com.santeh.inboxlistproject.main.Activity_Queue;
import com.santeh.inboxlistproject.main.Activity_UnregInbox_MainThread;
import com.santeh.inboxlistproject.main.Answer;
import com.santeh.inboxlistproject.main.MainInbox;
import com.santeh.inboxlistproject.util.Aswered_Queries_ListAdapter;
import com.santeh.inboxlistproject.util.SmsInquiryApp;
import com.santeh.inboxlistproject.util.UIHelper;

import java.util.ArrayList;
import java.util.List;

public class SMS_delivery extends BroadcastReceiver {

	private final String DEBUG_TAG = getClass().getSimpleName().toString();
	private static final String ACTION_SMS_SENT = "SMS_SENT";
	public static boolean stillsending = false;
	Context ctx;
	Aswered_Queries_ListAdapter listviewAdapter;
	ListView list ;
	List<Answer> worldpopulationlist = new ArrayList<Answer>();
	SMSDataSource db;

	// When the SMS has been sent
	public void onReceive(Context context, Intent intent) {
		ctx=context;
		db = new SMSDataSource(context);
		db.open();
		Log.d("NewMessage", "Broadcast Receiver");

		String action = intent.getAction();
		String answer1 = intent.getStringExtra("answer");
		String content = intent.getStringExtra("content");
		String number = intent.getStringExtra("number");
		String time = intent.getStringExtra("time");
		String fromActivity = intent.getStringExtra("fromActivity");
		String id = intent.getStringExtra("id");
		String name = numberToName(number);
		String reg = intent.getStringExtra("reg");
		String curname = intent.getStringExtra("name");
		
		if (curname == null) {
			 curname = number;
		}
		
		Answer answer = new Answer();
		MainInbox mainInbox = new MainInbox();

		int countparts = ((SmsInquiryApp) (ctx).getApplicationContext()).getCountParts();
		int loopcount = ((SmsInquiryApp) (ctx).getApplicationContext()).getLoopCounter() + 1;

		String status = "";
		String status_tsr = "";

		//checks if intent action passed is equals to sms sent
		if (action.equals(ACTION_SMS_SENT)) {
			((SmsInquiryApp) (ctx).getApplicationContext()).stillsending = true;
			Log.d("STILL sending", String.valueOf(((SmsInquiryApp) (ctx).getApplicationContext()).stillsending));
			
			switch (getResultCode()) {
			case Activity.RESULT_OK:
				Toast.makeText(context, "SENT", Toast.LENGTH_LONG).show();
				status="SENT";
				Bundle b = intent.getExtras();
				Log.d(DEBUG_TAG, "sendBroadcastReceiver : b is " + b);
				if (b != null) {
					String value = b.getString("extra_key");
					Log.d("SMS_DEL_ID", "sendBroadcastReceiver : value is " + value);
				}
				break;
			case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
				Toast.makeText(context, "SENDING FAILED! Check Operator Service!", Toast.LENGTH_LONG)
				.show();
				status="FAILED";
				break;
			case SmsManager.RESULT_ERROR_NO_SERVICE:
				Toast.makeText(context, "SENDING FAILED! No Network Service!", Toast.LENGTH_LONG)
				.show();
				status="FAILED";
				break;
			case SmsManager.RESULT_ERROR_NULL_PDU:
				Toast.makeText(context, "Null PDU", Toast.LENGTH_LONG).show();
				status="FAILED";
				break;
			case SmsManager.RESULT_ERROR_RADIO_OFF:
				Toast.makeText(context, "SENDING FAILED! Connectivity is Off!", Toast.LENGTH_LONG).show();
				status="FAILED";
				break;
				
			}


			Log.d("NewMessage", "loop count parts");
			if (loopcount==countparts) {
				Log.d("NewMessage", "if ID is not empty");
				if (id != null && !id.isEmpty()) {//if iD is not empty then UPDATE DATA that matches CURRENT ID
					Log.d("DELIVERY", "id is not empty");
					if(fromActivity.equals("mainInbox")){//main inbox/ currnetly unused
						long rowId = Long.parseLong(id);
					}
					else if(fromActivity.equals("answeredQeuryInbox")){
//						Log.d("STILL sending on answeredQuery Resend", String.valueOf(((SmsInquiryApp) (ctx).getApplicationContext()).stillsending));

						db.updateRow_replies(Long.parseLong(id), number, content, answer1, status, time);
						
						((SmsInquiryApp) (ctx).getApplicationContext()).stillsending = false; //check if still sending
						Intent startBroadcastIntent = new Intent(context, Activity_Answered_Queries.class);
						startBroadcastIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						context.startActivity(startBroadcastIntent);
					}
					else if(fromActivity.equals("queueList")){
//						Log.d("STILL sending on answeredQuery Resend", String.valueOf(((SmsInquiryApp) (ctx).getApplicationContext()).stillsending));

						db.updateRow_queueu(Long.parseLong(id), number, content, answer1, status, time);

						((SmsInquiryApp) (ctx).getApplicationContext()).stillsending = false; //check if still sending
						Intent startBroadcastIntent = new Intent(context, Activity_Queue.class);
						startBroadcastIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						context.startActivity(startBroadcastIntent);
					}
				}
				else//else if id is nul then create new record
				{
					if (fromActivity.equals("mainInbox")) { //if from main inbox (unused)
						Log.d("DELIVERY ", "intent From Main");
						Log.d("STILL sending MainInbox", String.valueOf(((SmsInquiryApp) (ctx).getApplicationContext()).stillsending));
						final MainInbox main = new MainInbox();
						main.setMainSender(number);
						main.setMainContent(content);
						main.setMainTimeReceived(time);
						main.setMainTimeSent("1");//this is really a marker if thender is me or not
						main.setMainDelivery(status); 
						main.setMainRead("0");
						main.setMainName(curname);
						main.setMainUnregStat(reg);
						db.createMainInbox(main);
						
						((SmsInquiryApp) (ctx).getApplicationContext()).stillsending = false; //check if still sending
						
//						Log.d("DELIVERY Check if active", "intent From Main" + number + " " + content + " " + time + " "  + status + " "  + curname + " "  + reg);
						Log.d("STILL sending on answeredQuery Resend", String.valueOf(((SmsInquiryApp) (ctx).getApplicationContext()).stillsending));
						refreshViews(context, number);//refreshes view of current active Activity
					}
					else if(fromActivity.equals("deliveryInbox")) {
						Log.d("DELIVERY FROM request", "intent From DELIVERY");
						answer.setAnswerAnswer(answer1);
						answer.setAnswerContent(content);
						answer.setAnswerTimeReceived(time);
						answer.setAnswerNumber(number);
						answer.setAnswerStatust(status);
						db.create_queue(answer);
						((SmsInquiryApp) (ctx).getApplicationContext()).stillsending = false; //check if still sending
					}

//					else{
//						Log.d("DELIVERY ", "intent From Delivery or Answered Query");
//						answer.setAnswerAnswer(answer1);
//						answer.setAnswerContent(content);
//						answer.setAnswerTimeReceived(time);
//						answer.setAnswerNumber(number);
//						answer.setAnswerStatusToTSR("SENT");
//						answer.setAnswerStatust(status);
//						db.create_replies(answer);
//						((SmsInquiryApp) (ctx).getApplicationContext()).stillsending = false; //check if still sending
//					}


				}//end of if null
			}// end of LongCounter
			((SmsInquiryApp) (ctx).getApplicationContext()).longmessage_Delivery_LoopCounter++;
			Log.d("STILL SENDING end of Loop ", String.valueOf(((SmsInquiryApp) (ctx).getApplicationContext()).stillsending));
		}
	}

	private void refreshViews(Context context, String number) {
		Log.d("DELIVERY ", "if main");


		if (Activity_Inbox.active) {
            Log.d("DELIVERY ", "main");
            Intent refresh = new Intent(context, Activity_Inbox.class);
            refresh.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
            context.startActivity(refresh);
        }
		Log.d("DELIVERY ", "if unreg main");

		if (Activity_UnregInbox_MainThread.active) {
            Log.d("DELIVERY ", "unReg");
            Intent refresh = new Intent(context, Activity_UnregInbox_MainThread.class);
            refresh.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
            context.startActivity(refresh);
        }

		Log.d("DELIVERY ", "if subthread");
		if (Activity_MainInbox_SubThread.active && Activity_MainInbox_SubThread.activeNum.equals(number)) {
            Intent refresh = new Intent(context, Activity_MainInbox_SubThread.class);
            refresh.putExtra("sender", number);
            refresh.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
            context.startActivity(refresh);
        }
		Log.d("DELIVERY ", "END");
	}


	public List<Answer> fetchInboxSms() {
		Log.d("VAR_Start_ fetchInboxSms", "fetchInboxSms");

		List<Answer> answerDB = new ArrayList<Answer>();

		Cursor cursor = db.findAll_answers();
		if (cursor != null) {
			cursor.moveToLast();
			if (cursor.getCount() > 0) {
				do {
					Answer answer = new Answer();
					answer.answerID = cursor.getString(cursor.getColumnIndex(SMSOpenHelper.COLUMN_ID));
					answer.answerNumber = cursor.getString(cursor.getColumnIndex(SMSOpenHelper.COLUMN_SENDER));
					answer.answerContent = cursor.getString(cursor.getColumnIndex(SMSOpenHelper.COLUMN_Content));
					answer.answerTimeReceived = cursor.getString(cursor.getColumnIndex(SMSOpenHelper.COLUMN_TIME_received));
					answer.answerAnswer = cursor.getString(cursor.getColumnIndex(SMSOpenHelper.COLUMN_ANSWER));
					answer.answerStatus = cursor.getString(cursor.getColumnIndex(SMSOpenHelper.COLUMN_ANSWER_Status));
					Log.d("UPDATE_SHOW", answer.answerID +" " +answer.answerNumber + answer.answerContent + 
							answer.answerTimeReceived + answer.answerAnswer + answer.answerStatus);
					worldpopulationlist.add(answer);
				} while (cursor.moveToPrevious());
			}
		}

		return answerDB;

	}


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

	}


}
