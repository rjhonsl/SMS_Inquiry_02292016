package com.santeh.inboxlistproject.br;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class HeadlessSmsSendService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		Log.d("SMS_reply", "Service");
		return null;
	}

}
