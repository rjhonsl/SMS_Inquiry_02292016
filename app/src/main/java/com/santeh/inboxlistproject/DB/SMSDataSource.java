package com.santeh.inboxlistproject.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.santeh.inboxlistproject.main.Answer;
import com.santeh.inboxlistproject.main.Branch;
import com.santeh.inboxlistproject.main.Contacts;
import com.santeh.inboxlistproject.main.Delivery;
import com.santeh.inboxlistproject.main.MainInbox;
import com.santeh.inboxlistproject.main.Message;

import java.util.ArrayList;
import java.util.List;


public class SMSDataSource {

	private static final String LOGTAG = "SMS";

	SQLiteOpenHelper dbhelper;
	SQLiteDatabase database;


	private static final String[] allColumns = {
		//String Array of all Columns Listed on OpenHelper
		//		ToursDBOpenHelper.COLUMN_DESC,
		SMSOpenHelper.COLUMN_ID,
		SMSOpenHelper.COLUMN_SENDER,
		SMSOpenHelper.COLUMN_Content,
		SMSOpenHelper.COLUMN_TIME_received

	};

	private static final String[] allColumns_replies = {
		//String Array of all Columns Listed on OpenHelper
		//		ToursDBOpenHelper.COLUMN_DESC,
		SMSOpenHelper.COLUMN_ID,
		SMSOpenHelper.COLUMN_SENDER,
		SMSOpenHelper.COLUMN_Content,
		SMSOpenHelper.COLUMN_TIME_sent,
		SMSOpenHelper.COLUMN_ANSWER
	};

	public SMSDataSource(Context context){
		//Log.d("DBSource", "database connect");
		dbhelper = new SMSOpenHelper(context);
		//opens the database connection
	}

	public void open(){
		//Log.d("DBSource", "database open");
		database = dbhelper.getWritableDatabase();
	}
	public void close(){
		//Log.d("DBSource", "database close");
		dbhelper.close();
	}

	//sets values to ContentValues
	public Message create(Message message){		
		ContentValues values = new ContentValues();
		values.put(SMSOpenHelper.COLUMN_SENDER, message.getMessageNumber());
		values.put(SMSOpenHelper.COLUMN_Content, message.getMessageContent());
		values.put(SMSOpenHelper.COLUMN_TIME_received, message.getMessageTimeReceived());
		//		values.put

		//id only accepts long
		long insertId = database.insert(SMSOpenHelper.TABLE_RECEIVED, null, values);
		message.setId(insertId);
		return message;
	}


	//after replied/yes or no
	public Answer create_replies(Answer answer){

		ContentValues values = new ContentValues();
		values.put(SMSOpenHelper.COLUMN_ANSWER, 		answer.getAnswerAnswer());
		values.put(SMSOpenHelper.COLUMN_SENDER, 		answer.getAnswerNumber());
		values.put(SMSOpenHelper.COLUMN_Content,	 	answer.getAnswerContent());
		values.put(SMSOpenHelper.COLUMN_TIME_sent, 		answer.getAnswerTimeReceived());
		values.put(SMSOpenHelper.COLUMN_ANSWER_Status, 	answer.getAnswerStatus());
		values.put(SMSOpenHelper.COLUMN_ANSWER_Status_TSR, answer.getAnswerStatusToTSR());

		//id only accepts long
		long insertId = database.insert(SMSOpenHelper.TABLE_REPLIES, null, values);
		answer.setId(insertId);
		return answer;
	}

	//create new queue
	public Answer create_queue(Answer queue){

		ContentValues values = new ContentValues();
		values.put(SMSOpenHelper.COLUMN_QUEUE_Sender, 		queue.getAnswerNumber());
		values.put(SMSOpenHelper.COLUMN_QUEUE_Content,	 	queue.getAnswerContent());
		values.put(SMSOpenHelper.COLUMN_QUEUE_TimeReceived, queue.getAnswerTimeReceived());
		values.put(SMSOpenHelper.COLUMN_QUEUE_answer, 		queue.getAnswerAnswer());
		values.put(SMSOpenHelper.COLUMN_QUEUE_status, 		queue.getAnswerStatus());

		//id only accepts long
		long insertId = database.insert(SMSOpenHelper.TABLE_QUEUE, null, values);
		queue.setId(insertId);
		return queue;
	}

	//after replied/yes or no
	public Delivery create_deliver_code(Delivery deliver){
		ContentValues values = new ContentValues();
		values.put(SMSOpenHelper.COLUMN_delivery_code_type, deliver.getDeliveryType());
		values.put(SMSOpenHelper.COLUMN_delivery_code_code, deliver.getDeliveryCode());
		long insertId = database.insert(SMSOpenHelper.TABLE_DELIVERY_CODE, null, values);
		deliver.setId(insertId);
		return deliver;
	}
	
	public Branch add_newBranch(Branch code){
		ContentValues values = new ContentValues();
		values.put(SMSOpenHelper.COLUMN_BRANCHES_code, code.getBranchcode());
		values.put(SMSOpenHelper.COLUMN_BRANCHES_name, code.getBranchname());
		long insertId = database.insert(SMSOpenHelper.TABLE_BRANCHES, null, values);
		code.setId(insertId);
		return code;
	}


	public Message create_inbox(Message inbox){

		ContentValues values = new ContentValues();
		values.put(SMSOpenHelper.COLUMN_Inbox_SENDER, inbox.getMessageNumber());
		values.put(SMSOpenHelper.COLUMN_Inbox_CONTENT, inbox.getMessageContent());
		values.put(SMSOpenHelper.COLUMN_Inbox_TIME_RECEIVED, inbox.getMessageTimeReceived());
		values.put(SMSOpenHelper.COLUMN_Inbox_STATUS, inbox.getMessageStatus());

		//id only accepts long
		long insertId = database.insert(SMSOpenHelper.TABLE_Inbox, null, values);
		inbox.setId(insertId);
		return inbox;
	}


	public MainInbox createMainInbox(MainInbox inbox){
		//Log.d("FINAL_ add to DB", String.valueOf(inbox.getMainTimeReceived()));
		ContentValues inboxx = new ContentValues();
		inboxx.put(SMSOpenHelper.COLUMN_MAIN_Sender_, 			inbox.getMainSender());
		inboxx.put(SMSOpenHelper.COLUMN_MAIN_Content, 			inbox.getMainContent());
		inboxx.put(SMSOpenHelper.COLUMN_MAIN_Time_Received, 	inbox.getMainTimeReceived());
		inboxx.put(SMSOpenHelper.COLUMN_MAIN_Time_Sent, 		inbox.getMainTimeSent());
		inboxx.put(SMSOpenHelper.COLUMN_MAIN_DELIVERY_STATUS, 	inbox.getMainDelivery());
		inboxx.put(SMSOpenHelper.COLUMN_MAIN_Read_STATUS,		inbox.getMainRead());
		inboxx.put(SMSOpenHelper.COLUMN_MAIN_name, 				inbox.getMainName());
		inboxx.put(SMSOpenHelper.COLUMN_MAIN_RegisterStatus, 				inbox.getMainUnregStat());
		//id only accepts long
		long insertId = database.insert(SMSOpenHelper.TABLE_main, null, inboxx);
		//		database.execSQL(sql)
		inbox.setId(insertId);
		return inbox;
	}

	//add new contact to dbs
	public Contacts create_new_contact(Contacts contact){

		ContentValues values = new ContentValues();
		Log.d("COUNTS", contact.getcontactName() + contact.getContactNumber());
		values.put(SMSOpenHelper.COLUMN_CONTACTS_contact_number, contact.getContactNumber());
		values.put(SMSOpenHelper.COLUMN_CONTACTS_contact_name, contact.getcontactName());

		//id only accepts long
		long insertId = database.insert(SMSOpenHelper.TABLE_CONTACTS, null, values);
		contact.setId(insertId);
		return contact;
	}


	//for querying back, returning values
	public List<Message> findAll() {
		//Log.d("DBFindAll", "FindAll");
		//1st is table name
		//2nd is column names
		//3rd is used to filter the data //Where
		//5th groupby
		//7th orderby

		Cursor cursor = database.query(SMSOpenHelper.TABLE_RECEIVED, allColumns,
				null, null, null, null, null);

		Log.i(LOGTAG, "Returned " + cursor.getCount() + " rows");

		List<Message> message = cursorToList(cursor);
		return message;
	}



	//FOR MEssage 
	public List<Message> findFiltered(String selection, String orderBy) {
		Cursor cursor = database.query(SMSOpenHelper.TABLE_RECEIVED, allColumns, 
				selection, null, null, null, orderBy);
		Log.i(LOGTAG, "Returned " + cursor.getCount() + " rows");
		List<Message> message = cursorToList(cursor);
		return message;
	}


	private List<Message> cursorToList(Cursor cursor) {
		//Log.d("DBFindAll", "cursor FindAll");
		List<Message> messages = new ArrayList<Message>();
		if (cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				Message message = new Message();
				message.setId(cursor.getLong(cursor.getColumnIndex(SMSOpenHelper.COLUMN_ID)));
				message.setMessageNumber(cursor.getString(cursor.getColumnIndex(SMSOpenHelper.COLUMN_SENDER)));
				message.setMessageContent(cursor.getString(cursor.getColumnIndex(SMSOpenHelper.COLUMN_Content)));
				message.setMessageTimeReceived(cursor.getString(cursor.getColumnIndex(SMSOpenHelper.COLUMN_TIME_received)));
			}
		}
		return messages;
	}


	//FIND ALL ANSWER
	public Cursor findAll_answers() {
		String where = null;
		String orderby = SMSOpenHelper.COLUMN_ANSWER_Status + " DESC";
		Cursor cursor = database.query(true, SMSOpenHelper.TABLE_REPLIES, SMSOpenHelper.ALL_KEYS_REPLIES, 
				where, null, null, null, orderby, null);
		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor;
	}

	//find all Queue Items
	public Cursor findAll_queue() {
		String where = null;
		String orderby = null;
		Log.d("Adapter","BEFORE CUR");
		Cursor cursor = database.query(true, SMSOpenHelper.TABLE_QUEUE, SMSOpenHelper.ALL_KEYS_QUEUE,
				///					   *orderby*
				where, null, null, null, orderby, null);
		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor;
	}

	public Cursor findAll_answers_filter_replies(String reply) {//.filter for answered queries. replies.
		String where = SMSOpenHelper.COLUMN_ANSWER + " = '"+reply+"'";
		String orderby = SMSOpenHelper.COLUMN_ANSWER_Status + " DESC";
		Cursor cursor = database.query(true, SMSOpenHelper.TABLE_REPLIES, SMSOpenHelper.ALL_KEYS_REPLIES, where, null, null, null, orderby, null);
		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor;
	}

	public void deleteAllTableRecord(String table){
		 database.execSQL("delete from " + table);
	}

	//FOR DELIVERY REQUEST
	public Cursor findAll_delivery() {
		String where = null;
		String orderby = SMSOpenHelper.COLUMN_delivery_code_type + " ASC";
		Cursor cursor = database.query(true, SMSOpenHelper.TABLE_DELIVERY_CODE, SMSOpenHelper.ALL_KEYS_DELIVERY, 
				///					   *orderby*
				where, null, null, null, orderby, null);
		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor;
	}


	//FOR MESSAGES
	public Cursor findAll_messages() {
		//Log.d("marker", "Start FInd all messages");
		//1st is table name
		//2nd is column names
		//3rd is used to filter the data //Where
		//5th groupby
		//7th orderby
		//			String order = orderBy;
		String where = null;
		Cursor cursor = database.query(true, SMSOpenHelper.TABLE_Inbox, SMSOpenHelper.ALL_KEYS_INBOX, 
				where, null, null, null, null, null);
		if (cursor != null) {
			cursor.moveToFirst();
		}

		return cursor;
	}

	public int findAll_Single_MainInbox(String sender) {
		//Log.d("INBOX_COUNTER_DB", sender);

		String where = SMSOpenHelper.COLUMN_MAIN_Sender_ + " = " + sender;
		Cursor cursor = database.query(true, SMSOpenHelper.TABLE_main, SMSOpenHelper.ALL_KEYS_MAIN, 
				null, null, null, null, null, null);
		int x = 0;
		String y = null, senderDB=null;

		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			//Log.d("INBOX_COUNTER", "Cursor Count > 0");
			do {
				senderDB = cursor.getString(cursor.getColumnIndex(SMSOpenHelper.COLUMN_MAIN_Sender_));

				if (sender.equals(senderDB)) {
					x++;
				}
			} while (cursor.moveToNext());
		}
		return x;
	}



	public Cursor findOneNum_SubThread(String sender) {
		String where = SMSOpenHelper.COLUMN_MAIN_Sender_ + "='" +sender+"'";
		String groupby = SMSOpenHelper.COLUMN_MAIN_Sender_;
		String orderby = SMSOpenHelper.COLUMN_MAIN_Time_Received + " DESC";
		Cursor cursor = database.query(true, 
				SMSOpenHelper.TABLE_main, //table name
				SMSOpenHelper.ALL_KEYS_MAIN, //columns
				where, //where
				null,
				null,//groupby 
				null,//having 
				orderby,//orderby
				null//limit
				);
		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor;
	}




	//query all mainInbox Grouped by Name
	public Cursor findAll_mainInbox_groupByNumber() {
		//Log.d("INBOX", "Start FInd all messages");
		//1st is table name
		//2nd is column names
		//3rd is used to filter the data //Where
		//5th groupby
		//7th orderby
		//			String order = orderBy;
		String where = null;
		String groupby = SMSOpenHelper.COLUMN_MAIN_Sender_;
		String orderby = SMSOpenHelper.COLUMN_MAIN_Time_Received + " ASC";
		Cursor cursor = database.query(true, 
				SMSOpenHelper.TABLE_main, //table name
				SMSOpenHelper.ALL_KEYS_MAIN, //columns
				where, //where
				null,
				groupby,//groupby 
				null,//having 
				orderby,//orderby
				null//limit
				);
		if (cursor != null) {
			cursor.moveToFirst();
		}
		//Log.d("INBOX", "Return Cursor");
		return cursor;
	}

	public Cursor findAll_mainInbox_unread_groupByNumber() {
		String where = SMSOpenHelper.COLUMN_MAIN_Read_STATUS + " = '1'";
		String groupby = SMSOpenHelper.COLUMN_MAIN_Sender_;
		String orderby = SMSOpenHelper.COLUMN_MAIN_Time_Received + " ASC";
		Cursor cursor = database.query(true, 
				SMSOpenHelper.TABLE_main, //table name
				SMSOpenHelper.ALL_KEYS_MAIN, //columns
				where, //where
				null,
				groupby,//groupby 
				null,//having 
				orderby,//orderby
				null//limit
				);
		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor;
	}

	//FOR ANSWERS
	public Cursor findAll_contacts() {
		Log.d("UNREG CONTACTLIST", "start COntact Query");
		String where = null;
		Cursor cursor = database.query(true, SMSOpenHelper.TABLE_CONTACTS, SMSOpenHelper.ALL_KEYS_CONTACTS, 
				where, null, null, null, null, null);
		if (cursor != null) {
			Log.d("UNREG CONTACTLIST", "COntact Query not null");
			cursor.moveToFirst();
		}
		return cursor;
	}
	
	public Cursor findAll_branches() {

		String where = null;
		Cursor cursor = database.query(true, SMSOpenHelper.TABLE_BRANCHES, SMSOpenHelper.ALL_KEYS_BRANCHES, 
				where, null, null, null, null, null);
		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor;
	}
	
	public Cursor findBracnhDuplicate(String code) {

		String where = SMSOpenHelper.COLUMN_BRANCHES_code + " = '"+ code +"'" ;
		Cursor cursor = database.query(true, SMSOpenHelper.TABLE_BRANCHES, SMSOpenHelper.ALL_KEYS_BRANCHES, 
				where, null, null, null, null, null);
		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor;
	}


	//Get get 1 Row from Contacts via ID
	public Cursor getRow_contacts(long rowId) {
		String where = SMSOpenHelper.COLUMN_ID + "=" + rowId;
		//Log.d("marker", "GetRowContacts " + rowId);
		Cursor c = 	database.query(true, SMSOpenHelper.TABLE_CONTACTS, SMSOpenHelper.ALL_KEYS_CONTACTS, 
				where, null, null, null, null, null);
		if (c != null) {
			c.moveToFirst();
		}
		//Log.d("marker", "Cursor " + String.valueOf(c));
		return c;
	}




	public Cursor searchRow_contacts(String keyword) {// LIKE '%" +key+"%' OR " + 
		String where = SMSOpenHelper.COLUMN_CONTACTS_contact_name + " LIKE '%"+keyword+"%' OR " + 
				SMSOpenHelper.COLUMN_CONTACTS_contact_number + " LIKE '%"+keyword+"%'"	;
		//Log.d("marker", "GetRowContacts " + rowId);
		Cursor c = 	database.query(true, SMSOpenHelper.TABLE_CONTACTS, SMSOpenHelper.ALL_KEYS_CONTACTS, 
				where, null, null, null, null, null);
		if (c != null) {
			c.moveToFirst();
		}
		//Log.d("marker", "Cursor " + String.valueOf(c));
		return c;
	}

	//check if number is existing in contacts
	public Cursor getRow_search_contacts(String number) {
		Log.d("SMSFILTER", "search contact");
		String where = SMSOpenHelper.COLUMN_CONTACTS_contact_number + "=" + number;
		Cursor c = 	database.query(true, SMSOpenHelper.TABLE_CONTACTS, SMSOpenHelper.ALL_KEYS_CONTACTS, 
				where, null, null, null, null, null);
		if (c != null) {
			c.moveToFirst();
		}
		return c;
	}

	//check if number is existing in contacts
	public Cursor getRow_search_queue_message(String message) {
		Log.d("SMSFILTER", "search contact");
		String where = SMSOpenHelper.COLUMN_QUEUE_Content + " LIKE '%" + message + "%' ";
		Cursor c = 	database.query(true, SMSOpenHelper.TABLE_QUEUE, SMSOpenHelper.ALL_KEYS_QUEUE,
				where, null, null, null, null, null);
		if (c != null) {
			c.moveToFirst();
		}
		return c;
	}

	//check if number is existing in contacts
	public Cursor getRow_search_deliveryRequest_message(String message) {
		Log.d("SMSFILTER", "search contact");
		String where = SMSOpenHelper.COLUMN_Inbox_CONTENT + " = '" + message + "' ";
		Cursor c = 	database.query(true, SMSOpenHelper.TABLE_Inbox, SMSOpenHelper.ALL_KEYS_INBOX,
				where, null, null, null, null, null);
		if (c != null) {
			c.moveToFirst();
		}
		return c;
	}
	
	public Cursor getRow_search_branch(String branchCode) {
		Log.d("SMSFILTER", "search Branch " + branchCode);
		String where = SMSOpenHelper.COLUMN_BRANCHES_code+ " = "  + "'"+branchCode+"'";
		Cursor c = 	database.query(true, SMSOpenHelper.TABLE_BRANCHES, SMSOpenHelper.ALL_KEYS_BRANCHES, 
				where, null, null, null, null, null);
		if (c != null) {
			c.moveToFirst();
		}
		return c;
	}

	//check if code is existing in db
	public Cursor getRow_search_deliveryCode(String code) {
		Log.d("SMSFILTER", "search delivery code");
		String where = null;
		//Log.d("CODE_search", "searching" + code);
		Cursor c = 	database.query(true, SMSOpenHelper.TABLE_DELIVERY_CODE, SMSOpenHelper.ALL_KEYS_DELIVERY, 
				where, null, null, null, null, null);
		if (c != null) {
			c.moveToFirst();
		}
		//Log.d("marker", "Cursor " + String.valueOf(c));
		return c;
	}

	public Cursor getRow_search_inbox(String id) {

		String where = 
				SMSOpenHelper.COLUMN_ID + " = " + id
				;
		Cursor c = 	database.query(true, SMSOpenHelper.TABLE_Inbox, SMSOpenHelper.ALL_KEYS_INBOX, 
				where, null, null, null, null, null);
		if (c != null) {
			c.moveToFirst();
		}
		//Log.d("INBOX_sucess", "Sucess " + String.valueOf(c));
		return c;
	}

	//Deletes row from Contacts
	public boolean deleteRow_contacts(long rowId) {
		String where = SMSOpenHelper.COLUMN_ID + "=" + rowId;
		return database.delete(SMSOpenHelper.TABLE_CONTACTS, where, null) != 0;
	}
	
	public boolean deleteRow_branch(long rowId) {
		String where = SMSOpenHelper.COLUMN_ID + "=" + rowId;
		return database.delete(SMSOpenHelper.TABLE_BRANCHES, where, null) != 0;
	}

	public boolean deleteRow_main(long rowId) {
		String where = SMSOpenHelper.COLUMN_ID + "=" + rowId;
		return database.delete(SMSOpenHelper.TABLE_main, where, null) != 0;
	}
	
	public boolean deleteThread_main(String number) {
		String where = SMSOpenHelper.COLUMN_MAIN_Sender_ + "= '" + number+"'";
		return database.delete(SMSOpenHelper.TABLE_main, where, null) != 0;
	}

	//Delete 1 Row from replies
	public boolean deleteRow_answers(long rowId) {
		String where = SMSOpenHelper.COLUMN_ID + " = " + rowId;
		return database.delete(SMSOpenHelper.TABLE_REPLIES, where, null) != 0;
	}


	//Delete 1 Row from queue
	public boolean deleteRow_queue(long rowId) {
		String where = SMSOpenHelper.COLUMN_ID + "=" + rowId;
		return database.delete(SMSOpenHelper.TABLE_QUEUE, where, null) != 0;
	}

	//Delete 1 Row from replies
	public boolean deleteRow_delivery(long rowId) {
		String where = SMSOpenHelper.COLUMN_ID + "=" + rowId;
		return database.delete(SMSOpenHelper.TABLE_DELIVERY_CODE, where, null) != 0;
	}

	//Deletes row from Inbox
	public boolean deleteRow_inbox(long rowId) {
		String where = SMSOpenHelper.COLUMN_ID + "=" + rowId;
		return database.delete(SMSOpenHelper.TABLE_Inbox, where, null) != 0;
	}

	//Update 1 Row on Contacts
	public boolean updateRow_contacts(long rowId, String contactNumber, String contactName) {
		String where = SMSOpenHelper.COLUMN_ID + "=" + rowId;
		ContentValues newValues = new ContentValues();
		newValues.put(SMSOpenHelper.COLUMN_CONTACTS_contact_number, contactNumber);
		newValues.put(SMSOpenHelper.COLUMN_CONTACTS_contact_name, contactName);
		// Insert it into the database.
		return database.update(SMSOpenHelper.TABLE_CONTACTS, newValues, where, null) != 0;
	}
	
	//Update 1 Row on branches
		public boolean updateRow_branch(long rowId, String branchName, String branchCode) {
			String where = SMSOpenHelper.COLUMN_ID + "=" + rowId;
			ContentValues newValues = new ContentValues();
			newValues.put(SMSOpenHelper.COLUMN_BRANCHES_name, branchName);
			newValues.put(SMSOpenHelper.COLUMN_BRANCHES_code, branchCode);
			// Insert it into the database.
			return database.update(SMSOpenHelper.TABLE_BRANCHES, newValues, where, null) != 0;
		}

	//Update 1 Row on replies
	public boolean updateRow_replies(long rowId, String sender, String content, String answer, String status, String time) {
		String where = SMSOpenHelper.COLUMN_ID + "=" + rowId;
		ContentValues newValues = new ContentValues();
		//Log.d("UPDATE_replies", status + ""+ String.valueOf(rowId));
		newValues.put(SMSOpenHelper.COLUMN_ANSWER, answer);
		newValues.put(SMSOpenHelper.COLUMN_SENDER, sender);
		newValues.put(SMSOpenHelper.COLUMN_Content, content);
		newValues.put(SMSOpenHelper.COLUMN_TIME_sent, time);
		newValues.put(SMSOpenHelper.COLUMN_ANSWER_Status, status);

		// Insert it into the database.
		return database.update(SMSOpenHelper.TABLE_REPLIES, newValues, where, null) != 0;
	}

	//Update 1 Row on replies
	public boolean updateRow_queueu(long rowId, String sender, String content, String answer, String status, String time) {
		String where = SMSOpenHelper.COLUMN_ID + "=" + rowId;
		ContentValues newValues = new ContentValues();
		//Log.d("UPDATE_replies", status + ""+ String.valueOf(rowId));
		newValues.put(SMSOpenHelper.COLUMN_ANSWER, answer);
		newValues.put(SMSOpenHelper.COLUMN_SENDER, sender);
		newValues.put(SMSOpenHelper.COLUMN_Content, content);
		newValues.put(SMSOpenHelper.COLUMN_TIME_sent, time);
		newValues.put(SMSOpenHelper.COLUMN_ANSWER_Status, status);

		// Insert it into the database.
		return database.update(SMSOpenHelper.TABLE_QUEUE, newValues, where, null) != 0;
	}

	//Update 1 Row on Main
	public boolean updateRow_ReadState_mainInbox(long rowId) {
		String where = SMSOpenHelper.COLUMN_ID + "=" + rowId;
		ContentValues newValues = new ContentValues();
		Log.d("UPDATE_Main", "make read " + ""+ String.valueOf(rowId));
		newValues.put(SMSOpenHelper.COLUMN_MAIN_Read_STATUS, "0");
		// Insert it into the database.
		return database.update(SMSOpenHelper.TABLE_main, newValues, where, null) != 0;
	}

	
	public boolean updateRow_ReadState_mainInbox_all(String number) {
		String where = SMSOpenHelper.COLUMN_MAIN_Sender_ + " = '" + number+"'";
		ContentValues newValues = new ContentValues();
		Log.d("UPDATE_Main_ALL", "make read " + ""+ String.valueOf(number));
		newValues.put(SMSOpenHelper.COLUMN_MAIN_Read_STATUS, "0");
		// Insert it into the database.
		return database.update(SMSOpenHelper.TABLE_main, newValues, where, null) != 0;
	}


	///Arrays for Answer
	public List<Answer> findFiltered_answers(String selection, String orderBy) {
		Cursor cursor = database.query(SMSOpenHelper.TABLE_RECEIVED, allColumns, 
				selection, null, null, null, orderBy);
		Log.i(LOGTAG, "Returned " + cursor.getCount() + " rows");
		List<Answer> answer = cursorToList_answer(cursor);
		return answer;
	}



	private List<Answer> cursorToList_answer(Cursor cursor) {
		//Log.d("DBFindAll", "cursor FindAll");
		List<Answer> answerss = new ArrayList<Answer>();
		if (cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				Answer answer = new Answer();
				answer.setId(cursor.getLong(cursor.getColumnIndex(SMSOpenHelper.COLUMN_ID)));
				answer.setAnswerNumber(cursor.getString(cursor.getColumnIndex(SMSOpenHelper.COLUMN_SENDER)));
				answer.setAnswerContent(cursor.getString(cursor.getColumnIndex(SMSOpenHelper.COLUMN_Content)));
				answer.setAnswerTimeReceived(cursor.getString(cursor.getColumnIndex(SMSOpenHelper.COLUMN_TIME_received)));
				answer.setAnswerAnswer(cursor.getString(cursor.getColumnIndex(SMSOpenHelper.COLUMN_ANSWER)));
				answer.setAnswerStatust(cursor.getString(cursor.getColumnIndex(SMSOpenHelper.COLUMN_ANSWER_Status)));
			}
		}
		return answerss;
	}
}
