package com.santeh.inboxlistproject.br;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;

public class BackupDbReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) 
	{   
		Date d1 = new Date();
		CharSequence s1  = DateFormat.format("hh:mm:ss", d1.getTime());
		Log.d("Backup", "BroadcastReceiver, in onReceive:");
		//		 UIHelper.toaster(activity, "Backup", Toast.LENGTH_LONG).show();

		Boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
		if(isSDPresent){
			final String inFileName = "/data/data/com.example.inboxlistproject/databases/db_sms.db";
			File dbFile = new File(inFileName);
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(dbFile);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

			Date d = new Date();
			CharSequence s  = DateFormat.format("MMMM-dd-yyyy hhxmmAA", d.getTime());
			Log.d("time", String.valueOf(s));

			String curDate = String.valueOf(s);


			String outFileName = Environment.getExternalStorageDirectory()+"/SMSInquiry/backup/" + curDate+".db";

			// Open the empty db as the output stream
			OutputStream output = null;
			try {
				output = new FileOutputStream(outFileName);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// Transfer bytes from the inputfile to the outputfile
			byte[] buffer = new byte[1024];
			int length;
			try {
				while ((length = fis.read(buffer))>0){
					output.write(buffer, 0, length);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// Close the streams
			try {
				output.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				output.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				fis.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Toast.makeText(context, "Back up Created: " + curDate, Toast.LENGTH_LONG).show();
		}
		else{

			Toast.makeText(context, "SDcard Not Available", Toast.LENGTH_LONG).show();

		}

	}

}
