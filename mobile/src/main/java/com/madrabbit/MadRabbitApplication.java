package com.madrabbit;

import android.app.Application;
import android.content.Context;
import com.onesignal.OneSignal;
import com.madrabbit.listener.OneSignalNotificationOpenedHandler;

public class MadRabbitApplication extends Application {
	private static MadRabbitApplication sInstance;
	public static Context getContext() {
		return sInstance;
	}
	public MadRabbitApplication() {
		sInstance = this;
	}


	@Override
	public void onCreate() {
		super.onCreate();
		
		try {
			Class.forName("android.os.AsyncTask");
		}
		catch(ClassNotFoundException e) {
			e.printStackTrace();
		}

		// initialize OneSignal
		OneSignal.startInit(this).setNotificationOpenedHandler(new OneSignalNotificationOpenedHandler()).init();
	}
}
