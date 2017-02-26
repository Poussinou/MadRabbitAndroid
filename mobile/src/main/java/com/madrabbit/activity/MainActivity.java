package com.madrabbit.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import com.madrabbit.R;
import com.madrabbit.fragment.MainFragment;

public class MainActivity extends FragmentActivity {

	public static final String EXTRA_URL = "url";
	private String mUrl = "https://mad-rabbit.com/";

	public static Intent newIntent(Context context)	{
		Intent intent = new Intent(context, MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		return intent;
	}

	public static Intent newIntent(Context context, String url)	{
		Intent intent = new Intent(context, MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra(EXTRA_URL, url);
		return intent;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		// handle intent extras
		Bundle extras = getIntent().getExtras();
		if(extras != null) {
			handleExtras(extras);
		}

		Intent intent = getIntent();
		Uri data = intent.getData();
		if(data != null) {
			mUrl = data.toString();
		}

		// Setup
		setupApp();
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onResume() {
		super.onResume();
	}
	
	@Override
	public void onPause() {
		super.onPause();
	}
	
	@Override
	public void onStop() {
		super.onStop();
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
	}


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);

		// forward activity result to fragment
		Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container);
		if(fragment != null) {
			fragment.onActivityResult(requestCode, resultCode, intent);
		}
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfiguration) {
		super.onConfigurationChanged(newConfiguration);
	}

	private void handleExtras(Bundle extras) {
		if(extras.containsKey(EXTRA_URL)) {
			mUrl = extras.getString(EXTRA_URL);
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	private void setupApp() {
		Fragment fragment = MainFragment.newInstance(mUrl);
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.container, fragment).commitAllowingStateLoss();
	}
}
