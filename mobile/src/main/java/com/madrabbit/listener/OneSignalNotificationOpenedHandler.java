package com.madrabbit.listener;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.onesignal.OSNotificationAction;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;
import com.madrabbit.MadRabbitApplication;
import com.madrabbit.activity.MainActivity;
import org.json.JSONObject;

public class OneSignalNotificationOpenedHandler implements OneSignal.NotificationOpenedHandler {

	@Override
	public void notificationOpened(OSNotificationOpenResult result) {
		OSNotificationAction.ActionType actionType = result.action.type;
		JSONObject data = result.notification.payload.additionalData;
		String url = null;
		Context context = MadRabbitApplication.getContext();

		if (data != null) {
			url = data.optString("permalink", null);
			if (url != null)
				Log.i("OneSignalExample", "customkey set with value: " + url);
		}
		// start activity
		Intent intent;
		if(url == null) intent = MainActivity.newIntent(context);
		else intent = MainActivity.newIntent(context, url);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}
}
