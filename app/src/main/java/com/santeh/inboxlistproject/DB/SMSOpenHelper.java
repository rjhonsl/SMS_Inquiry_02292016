package com.santeh.inboxlistproject.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SMSOpenHelper extends SQLiteOpenHelper {
	
	private static final String LOGTAG = "SMS";
	private static final String DATABASE_NAME = "db_sms.db";
	//each time you change data structure, you must increment this by 1
	private static final int DATABASE_VERSION = 19;
	
	public int longcount = 0;
	//Reference for tblSMS
	public static final String TABLE_RECEIVED = "tblsms";
	public static final int C_no_TSMS_ID 		= 0;
	public static final int C_no_TSMS_SENDER 	= 1;
	public static final int C_no_TSMS_CONTENT 	= 2;
	public static final int C_no_TSMS_RECEIVED 	= 3;
	public static final String COLUMN_ID 		= "_id"; //Column name for all ID in every table
	public static final String COLUMN_SENDER 	= "sender";
	public static final String COLUMN_Content 	= "content";
	public static final String COLUMN_TIME_received = "time_received";
	
	//table for TrueInbox
	public static final String TABLE_main="tblmain";
	public static final int C_no_MAIN_ID	 		= 0;
	public static final int C_no_MAIN_SENDER 		= 1;
	public static final int C_no_MAIN_Content		= 2;
	public static final int C_no_MAIN_Time_Received	= 3;
	public static final int C_no_MAIN_Time_sent 	= 4;
	public static final int C_no_MAIN_Read_status 	= 5;
	public static final int C_no_MAIN_delivery_status =6;
	public static final int C_no_MAIN_Sender 		  =7;
	
	public static final String COLUMN_MAIN_Sender_			= "sender";
	public static final String COLUMN_MAIN_Content			= "content";
	public static final String COLUMN_MAIN_Time_Received	= "time_received";
	public static final String COLUMN_MAIN_Time_Sent		= "time_sent";
	public static final String COLUMN_MAIN_Read_STATUS		= "read_status";
	public static final String COLUMN_MAIN_DELIVERY_STATUS	= "delivery_status";
	public static final String COLUMN_MAIN_name 			= "name";
	public static final String COLUMN_MAIN_RegisterStatus 	= "register_status";
	public static final String[] ALL_KEYS_MAIN = new String[] {
		COLUMN_ID,
		COLUMN_MAIN_Sender_, 
		COLUMN_MAIN_Content, 
		COLUMN_MAIN_Time_Received, 
		COLUMN_MAIN_Time_Sent, 
		COLUMN_MAIN_Read_STATUS, 
		COLUMN_MAIN_DELIVERY_STATUS, 
		COLUMN_MAIN_name,
		COLUMN_MAIN_RegisterStatus};
	
	//Table for inbox
	public static final String TABLE_Inbox= "tblinbox";
	public static final int C_no_Inbox_ID 				= 0;
	public static final int C_no_Inbox_SENDER 			= 1;
	public static final int C_no_Inbox_CONTENT 			= 2;
	public static final int C_no_Inbox_TIME_RECEIVED	= 3;
	public static final int C_no_Inbox_STATUS			= 4;
	public static final String COLUMN_Inbox_SENDER 			= "inbox_sender";
	public static final String COLUMN_Inbox_CONTENT 		= "inbox_content";
	public static final String COLUMN_Inbox_TIME_RECEIVED 	= "inbox_time_received";
	public static final String COLUMN_Inbox_STATUS 			= "inbox_status";
	public static final String[] ALL_KEYS_INBOX = new String[] {COLUMN_ID, COLUMN_Inbox_SENDER, COLUMN_Inbox_CONTENT, 
		COLUMN_Inbox_TIME_RECEIVED, COLUMN_Inbox_STATUS};

	
	//Table for delivery Code
	public static final String TABLE_DELIVERY_CODE = "tbldelivery_code";
	public static final int C_no_delivery_code_ID 			= 0;
	public static final int C_no_delivery_code_type 		= 1;
	public static final int C_no_delivery_code_code			= 2;
	
	public static final String COLUMN_delivery_code_ID 		= "_id";
	public static final String COLUMN_delivery_code_type	= "type";
	public static final String COLUMN_delivery_code_code	= "code";
	public static final String[] ALL_KEYS_DELIVERY= new String[] {COLUMN_delivery_code_ID,COLUMN_delivery_code_type,COLUMN_delivery_code_code};
	
	//reference for table tblcontacts
	public static final String TABLE_CONTACTS					= "tblContacts";
	public static final int C_no_CONTACTS_ID 				= 0;
	public static final int C_no_CONTACTS_contact_number	= 1;
	public static final int C_no_CONTACTS_contact_name 		= 2;
	public static final String COLUMN_CONTACTS_contact_number 	= "contact_number";
	public static final String COLUMN_CONTACTS_contact_name 	= "contact_name";
	//key for all column name in cotacts
	public static final String[] ALL_KEYS_CONTACTS = new String[] {COLUMN_ID, COLUMN_CONTACTS_contact_number, COLUMN_CONTACTS_contact_name};
	
	
	//reference for table tblreplies
	public static final String TABLE_REPLIES			= "tblreplies";
	public static final int C_no_TREPLIES_ID 			= 0;
	public static final int C_no_TREPLIES_sender 		= 1;
	public static final int C_no_TREPLIES_content 		= 2;
	public static final int C_no_TREPLIES_timeReceived 	= 3;
	public static final int C_no_TREPLIES_answer 		= 4;
	public static final int C_no_TREPLIES_status 		= 5;
	public static final int C_no_TREPLIES_status_TSR 	= 6;
	public static final String COLUMN_TIME_sent 		= "time_received";
	public static final String COLUMN_ANSWER 			= "answer";
	public static final String COLUMN_ANSWER_Status 	= "status";
	public static final String COLUMN_ANSWER_Status_TSR 	= "statusTSR";

	//key for all column name in replies
	public static final String[] ALL_KEYS_REPLIES = new String[] {COLUMN_ID, COLUMN_SENDER, COLUMN_Content, COLUMN_TIME_received, COLUMN_ANSWER, COLUMN_ANSWER_Status, COLUMN_ANSWER_Status_TSR};

	//reference for tblbranches
	public static final String TABLE_BRANCHES			= "tblbranches";
	public static final int C_no_BRANCHES_ID 			= 0;
	public static final int C_no_BRANCHES_NAME 			= 1;
	public static final int C_no_BRANCHES_CODE			= 2;
	public static final String COLUMN_BRANCHES_name		= "bname";
	public static final String COLUMN_BRANCHES_code		= "bcode";
	
	public static final String[] ALL_KEYS_BRANCHES= new String[] {COLUMN_ID,COLUMN_BRANCHES_name,COLUMN_BRANCHES_code};

	//referece for tbl_queue
	public static final String TABLE_QUEUE = "tblqueue";
	public static final String COLUMN_QUEUE_Sender = "sender";
	public static final String COLUMN_QUEUE_Content = "content";
	public static final String COLUMN_QUEUE_TimeReceived= "time_received";
	public static final String COLUMN_QUEUE_answer = "answer";
	public static final String COLUMN_QUEUE_status = "status";

	public static final int C_no_Q_ID 			= 0;
	public static final int C_no_Q_sender 		= 1;
	public static final int C_no_Q_content 		= 2;
	public static final int C_no_Q_timeReceived = 3;
	public static final int C_no_Q_answer 		= 4;
	public static final int C_no_Q_status 		= 5;

	public static final String[] ALL_KEYS_QUEUE = new String[] {COLUMN_ID, COLUMN_QUEUE_Sender, COLUMN_QUEUE_Content, COLUMN_QUEUE_TimeReceived, COLUMN_QUEUE_answer, COLUMN_QUEUE_status};



	
	//Query to create main inbox table
		private static final String TABLE_CREATE_MAIN = 
				"CREATE TABLE " + TABLE_main + " " +
				"(" +
				COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
				COLUMN_MAIN_Sender_ 		+ " TEXT, " +
				COLUMN_MAIN_Content 		+ " TEXT, " +
				COLUMN_MAIN_Time_Received 	+ " TEXT, " +
				COLUMN_MAIN_Time_Sent 		+ " TEXT, " +
				COLUMN_MAIN_Read_STATUS 	+ " TEXT, " +
				COLUMN_MAIN_DELIVERY_STATUS + " TEXT, " +
				COLUMN_MAIN_name + " TEXT, " +
				COLUMN_MAIN_RegisterStatus + " TEXT " +
				")";
	
	//Query to create tables
	private static final String TABLE_CREATE = 
			"CREATE TABLE " + TABLE_RECEIVED + " " +
			"(" +
			COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
			COLUMN_SENDER + " TEXT, " +
			COLUMN_Content + " TEXT, " +
			COLUMN_TIME_received + " INTEGER " +
			
			")";
	
	//Query to create tables
		private static final String TABLE_CREATE_DELIVERY = 
				"CREATE TABLE " + TABLE_DELIVERY_CODE  + " " +
				"(" +
				COLUMN_delivery_code_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
				COLUMN_delivery_code_type + " TEXT, " +
				COLUMN_delivery_code_code + " TEXT " +
				")";
	
	//Query to create INBOX
	private static final String TABLE_CREATE_INBOX = 
			"CREATE TABLE " + TABLE_Inbox + " " +
			"(" +
			COLUMN_ID 				+ " INTEGER PRIMARY KEY AUTOINCREMENT, " +
			COLUMN_Inbox_SENDER 	+ " TEXT, " +
			COLUMN_Inbox_CONTENT 	+ " TEXT, " +
			COLUMN_Inbox_TIME_RECEIVED + " INTEGER, " +
			COLUMN_Inbox_STATUS 	+ " TEXT " +
			")";
	
	//create tblreplies
	private static final String TABLE_CREATE_REPLIES = 
			"CREATE TABLE " + TABLE_REPLIES + " " +
			"(" +
			COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
			COLUMN_SENDER + " TEXT, " +
			COLUMN_Content + " TEXT, " +
			COLUMN_TIME_sent + " INTEGER, " +
			COLUMN_ANSWER + " TEXT, " +
			COLUMN_ANSWER_Status + " TEXT, "+
			COLUMN_ANSWER_Status_TSR + " TEXT "+
			")";
	
	//create tblcontacts
	private static final String TABLE_CREATE_CONTACTS = 
			"CREATE TABLE " + TABLE_CONTACTS + " " +
					"(" +
					COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
					COLUMN_CONTACTS_contact_number + " TEXT, " +
					COLUMN_CONTACTS_contact_name + " TEXT " +
					")";

	private static final String TABLE_CREATE_BRANCHES = 
			"CREATE TABLE " + TABLE_BRANCHES  + " " +
			"(" +
			COLUMN_ID+ " INTEGER PRIMARY KEY AUTOINCREMENT, " +
			COLUMN_BRANCHES_name + " TEXT, " +
			COLUMN_BRANCHES_code + " TEXT " +
			")";


	//create tblqueue
	private static final String TABLE_CREATE_QUEUE =
			"CREATE TABLE " + TABLE_QUEUE + " " +
					"(" +
					COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
					COLUMN_QUEUE_Sender + " TEXT, " +
					COLUMN_QUEUE_Content + " TEXT, " +
					COLUMN_QUEUE_TimeReceived+ " INTEGER, " +
					COLUMN_QUEUE_answer+ " TEXT, " +
					COLUMN_QUEUE_status+ " TEXT "+
					")";


	//connects db
	public SMSOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		Log.d(LOGTAG, "table "+DATABASE_NAME+" has been opened: "+String.valueOf(context));
	}

	@Override
	//creates tb
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(TABLE_CREATE);
		db.execSQL(TABLE_CREATE_INBOX);
		db.execSQL(TABLE_CREATE_REPLIES);
		db.execSQL(TABLE_CREATE_CONTACTS);
		db.execSQL(TABLE_CREATE_DELIVERY);
		db.execSQL(TABLE_CREATE_MAIN);
		db.execSQL(TABLE_CREATE_BRANCHES);
		db.execSQL(TABLE_CREATE_QUEUE);
		Log.d(LOGTAG, "tables has been created: " +String.valueOf(db));

	}

	@Override
	//on update version renew tb
	public void onUpgrade(SQLiteDatabase _db, int oldVersion, int newVersion) {
		_db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECEIVED);
		_db.execSQL("DROP TABLE IF EXISTS " + TABLE_REPLIES);
		_db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
		_db.execSQL("DROP TABLE IF EXISTS " + TABLE_Inbox);
		_db.execSQL("DROP TABLE IF EXISTS " + TABLE_DELIVERY_CODE);
		_db.execSQL("DROP TABLE IF EXISTS " + TABLE_main);
		_db.execSQL("DROP TABLE IF EXISTS " + TABLE_BRANCHES);
		_db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUEUE);
		Log.d(LOGTAG, "table has been deleted: "+String.valueOf(_db));
		onCreate(_db);

	}

}
