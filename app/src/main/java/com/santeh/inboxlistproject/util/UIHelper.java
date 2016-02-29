package com.santeh.inboxlistproject.util;


import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Build;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.santeh.inboxlistproject.DB.SMSDataSource;
import com.santeh.inboxlistproject.R;

public class UIHelper {



	public static String getText(Activity activity, int id) {
		EditText et = (EditText) activity.findViewById(id);
		return et.getText().toString();
	}
	
	public static Boolean checkSD(Activity activity) {
		Boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
		if(isSDPresent)
		{
//			toaster_long(activity, "SDcard available");
		}
		else
		{
//			toaster_long(activity, "SDcard not available");
		}
		return isSDPresent;
	}
	
	public static boolean getCBChecked(Activity activity, int id) {
		CheckBox cb = (CheckBox) activity.findViewById(id);
		return cb.isChecked();
	}

	public static void setCBChecked(Activity activity, int id, boolean value) {
		CheckBox cb = (CheckBox) activity.findViewById(id);
		cb.setChecked(value);
	}

	public static void toaster(Activity context, String msg) {

		LayoutInflater inflater = context.getLayoutInflater();
		View layout = inflater.inflate(R.layout.toast,
				(ViewGroup) context.findViewById(R.id.toast_layout_root));

		TextView text = (TextView) layout.findViewById(R.id.text);
		Typeface font = Typeface.createFromAsset(context.getAssets(), "Raleway-Regular.ttf");
		text.setTypeface(font);
		text.setText(msg);

		Toast toast = new Toast(context.getApplicationContext());
		toast.setGravity(Gravity.BOTTOM, 0, 100);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setView(layout);
		toast.show();
	}
	
	public static void toaster_long(Activity context, String msg) {

		LayoutInflater inflater = context.getLayoutInflater();
		View layout = inflater.inflate(R.layout.toast,
				(ViewGroup) context.findViewById(R.id.toast_layout_root));

		TextView text = (TextView) layout.findViewById(R.id.text);
		Typeface font = Typeface.createFromAsset(context.getAssets(), "Raleway-Regular.ttf");
		text.setTypeface(font);
		text.setText(msg);

		Toast toast = new Toast(context.getApplicationContext());
		toast.setGravity(Gravity.BOTTOM, 0, 100);
		toast.setDuration(Toast.LENGTH_LONG);
		toast.setView(layout);
		toast.show();
	}

	public static String convertMillisToDate(String time){

		//if date difference is 24hrs
		//		CharSequence timeR = DateFormat.format("MMM-dd-yyyy h:mm:ssAA", new java.util.Date(date));

		long date = Long.parseLong(time);
		long currentTime = System.currentTimeMillis();
		long dif = currentTime - date;
		CharSequence timeR = null;
		if (dif > 86400000) {
			timeR = DateFormat.format("MMM d", new java.util.Date(date));
		}
		else{
			timeR = DateFormat.format("h:mmAA", new java.util.Date(date));
		}
		return String.valueOf(timeR);
	}

	public static String extract_branch(String content){
		String branch="";
		int commaCounter = 0, end=0, x=0;

		do{
			char str = content.charAt(x);
			if (commaCounter == 1){
				branch=branch+str;
			}

			if (str == ':'){
				commaCounter++;
			}

			if(branch.length()==3){
				end=1;
			}
			x++;

		}while(end==0);
		return branch;
	}

	public static void animateExit_slide(Activity activity){

		activity.finish();
		activity.overridePendingTransition(R.anim.right_to_left, R.anim.left_to_right);
	}

	public static void startActivity_slidetoLeft(Activity currentClass, Class nextclass){

		Intent intent = new Intent(currentClass.getApplicationContext(), nextclass);
		currentClass.startActivity(intent);
		currentClass.overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);

	}
	public static void startActivity_slidetoLeftDim(Activity currentClass, Class nextclass){

		Intent intent = new Intent(currentClass.getApplicationContext(), nextclass);
		currentClass.startActivity(intent);
		currentClass.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

	}

	public static void finishAll(Activity activity){
		activity.finishAffinity();
	}

	public static void startActivity_clearStack(Activity currentClass, Class nextclass){
		Intent intent = new Intent(currentClass.getApplicationContext(), nextclass);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		currentClass.startActivity(intent);
		currentClass.finish();
	}

	public static void startActivity_defaultAnim(Activity currentClass, Class nextclass){
		Intent intent = new Intent(currentClass.getApplicationContext(), nextclass);
		currentClass.startActivity(intent);
	}

	public static boolean checkIfNewInstalled(SMSDataSource db){

		boolean isNewInstalled = false;
		Cursor cur_branches = db.findAll_branches();
		int branchCount = cur_branches.getCount();

		Cursor  cur_Contacts = db.findAll_contacts();
		int contactCount = cur_Contacts.getCount();

		Cursor cur_deliveryCode = db.findAll_delivery();
		int delvieryCodeCount = cur_deliveryCode.getCount();

		if (branchCount<1 || contactCount<1 || delvieryCodeCount<1){
			isNewInstalled = false;

		}
		else{
			isNewInstalled = true;
		}

		return isNewInstalled;
	}

	public static boolean checkHardwareIfMtk(){
		String cpuManufacturer = Build.HARDWARE, mnftr = cpuManufacturer.substring(0, 2);
		Log.d("BUILD", "HW: " + cpuManufacturer + " " + mnftr);
		boolean isMTK = false;
		if(mnftr.equalsIgnoreCase("mt")){
			isMTK = true;
		}
		return isMTK;
	}

	public static String removeDoublePlus(String number) {
		int plusCount = 0; String trimmed = ""; int ctr = 0;
		for (int i = 0; i < number.length(); i++) {
			char c = number.charAt(i);
			if (c=='+'){
				plusCount++;
			}

			if(plusCount>1){
				trimmed = trimmed + c;
			}
		}
		return trimmed;
	}

	public static String getOrderNumber(String message){
		String ORnumber="";

		char x = 'a';
		int index = 0;
		do {
			ORnumber = ORnumber + message.charAt(index);
			x = message.charAt(index);
			index++;
		} while (x != ';');

		return ORnumber;
	}

}

