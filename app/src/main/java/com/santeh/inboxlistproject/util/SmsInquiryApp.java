package com.santeh.inboxlistproject.util;

import android.app.Application;

public class SmsInquiryApp extends Application {

	public String someVariable;
	public int longmessage_Delivery_LoopCounter = 0;
	public int longmessage_Countparts = 0;
	public boolean stillsending = false;

	
	public int getLoopCounter() {
		return longmessage_Delivery_LoopCounter;
	}

	public void setLoopCounter(int loopcount) {
		this.longmessage_Delivery_LoopCounter = loopcount;
	}
	
	public int getCountParts() {
		return  longmessage_Countparts;
	}

	public void setCountParts(int longparts) {
		this.longmessage_Countparts = longparts;
	}
	
	
	public String getSomeVariable() {
		return someVariable;
	}

	public void setSomeVariable(String someVariable) {
		this.someVariable = someVariable;
	}
	
	
	public boolean getStillSending() {
		return stillsending;
	}

	public void setStillSending(boolean stillsending1) {
		this.stillsending = stillsending1;
	}
	
}
