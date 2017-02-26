package com.madrabbit.fragment;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.UrlQuerySanitizer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import com.madrabbit.BuildConfig;
import com.madrabbit.R;
import com.madrabbit.activity.VideoPlayerActivity;
import com.madrabbit.listener.OnKeyListener;
import com.madrabbit.listener.OnTouchListener;
import com.madrabbit.utility.NetworkUtility;
import com.madrabbit.view.StatefulLayout;

import im.delight.android.webview.AdvancedWebView;

public class MainFragment extends TaskFragment implements SwipeRefreshLayout.OnRefreshListener, AdvancedWebView.Listener {
	private boolean mActionBarProgress = false;
	private View mRootView;
	private StatefulLayout mStatefulLayout;
	private AdvancedWebView mWebView;
	private String mUrl;
	private boolean mLocal = false;
	private static final String ARGUMENT_URL = "url";
	public final static String VIDEO_URL = "com.madrabbit.com.VIDEO_URL";


	public static MainFragment newInstance(String url) {
		MainFragment fragment = new MainFragment();

		// arguments
		Bundle arguments = new Bundle();
		arguments.putString(ARGUMENT_URL, url);
		fragment.setArguments(arguments);

		return fragment;
	}

	@Override
	public void onAttach(Context context)
	{
		super.onAttach(context);
	}
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		setRetainInstance(true);

		// handle fragment arguments
		Bundle arguments = getArguments();
		if(arguments != null) {
			handleArguments(arguments);
		}
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_main, container, false);
		mWebView = (AdvancedWebView) mRootView.findViewById(R.id.fragment_main_webview);
		return mRootView;
	}
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// restore webview state
		if(savedInstanceState!=null) {
			mWebView.restoreState(savedInstanceState);
		}

		// setup webview
		bindData();

		// pull to refresh
		setupSwipeRefreshLayout();

		// setup stateful layout
		setupStatefulLayout(savedInstanceState);

		// load data
		if(mStatefulLayout.getState()==null) loadData();
		
		// progress in action bar
		showActionBarProgress(mActionBarProgress);
	}


	@Override
	public void onStart()
	{
		super.onStart();
	}
	
	
	@Override
	public void onResume() {
		super.onResume();
		mWebView.onResume();
	}
	
	
	@Override
	public void onPause() {
		super.onPause();
		mWebView.onPause();
	}

	@Override
	public void onStop()
	{
		super.onStop();
	}
	
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		mRootView = null;
	}
	
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		mWebView.onDestroy();
	}

	@Override
	public void onDetach()
	{
		super.onDetach();
	}


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		mWebView.onActivityResult(requestCode, resultCode, intent);
	}


	@Override
	public void onSaveInstanceState(Bundle outState) {
		// save current instance state
		super.onSaveInstanceState(outState);
		setUserVisibleHint(true);

		// stateful layout state
		if(mStatefulLayout!=null) mStatefulLayout.saveInstanceState(outState);

		// save webview state
		mWebView.saveState(outState);
	}
	
	@Override
	public void onRefresh()	{
		runTaskCallback(new Runnable()
		{
			@Override
			public void run()
			{
				refreshData();
			}
		});
	}


	@Override
	public void onPageStarted(String url, Bitmap favicon) {
	}


	@Override
	public void onPageFinished(String url) {
	}


	@Override
	public void onPageError(int errorCode, String description, String failingUrl) {
	}


	//@Override
	//public void onDownloadRequested(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
	//}

	@Override
	public void onDownloadRequested(String url, String suggestedFilename, String mimeType, long contentLength, String contentDisposition, String userAgent) { }

	private void handleArguments(Bundle arguments) {
		if(arguments.containsKey(ARGUMENT_URL)) {
			mUrl = arguments.getString(ARGUMENT_URL);
		}
	}

	@Override
	public void onExternalPageRequest(String url){
	}

	private void loadData() {
		if(NetworkUtility.isOnline(getActivity()) || mLocal) {
			// show progress
			mStatefulLayout.showProgress();

			// load web url
			mWebView.loadUrl(mUrl);
		}
		else {
			mStatefulLayout.showOffline();
		}
	}


	public void refreshData() {
		if(NetworkUtility.isOnline(getActivity()) || mLocal) {
			// show progress in action bar
			showActionBarProgress(true);

			// load web url
			String url = mWebView.getUrl();
			if(url == null || url.equals("")) url = mUrl;
			mWebView.loadUrl(url);
		}
		else {
			showActionBarProgress(false);
			Toast.makeText(getActivity(), R.string.global_offline_toast, Toast.LENGTH_LONG).show();
		}
	}


	private void showActionBarProgress(boolean visible) {
		// show pull to refresh progress bar
		SwipeRefreshLayout contentSwipeRefreshLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.container_content_swipeable);
		SwipeRefreshLayout offlineSwipeRefreshLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.container_offline_swipeable);
		SwipeRefreshLayout emptySwipeRefreshLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.container_empty_swipeable);

		contentSwipeRefreshLayout.setRefreshing(visible);
		offlineSwipeRefreshLayout.setRefreshing(visible);
		emptySwipeRefreshLayout.setRefreshing(visible);

		boolean enabled;
		enabled = !visible;

		contentSwipeRefreshLayout.setEnabled(enabled);
		offlineSwipeRefreshLayout.setEnabled(enabled);
		emptySwipeRefreshLayout.setEnabled(enabled);

		mActionBarProgress = visible;
	}
	

	private void showContent(final long delay) {
		final Handler timerHandler = new Handler();
		final Runnable timerRunnable = new Runnable()
		{
			@Override
			public void run()
			{
				runTaskCallback(new Runnable() {
					public void run() {
						if(getActivity()!=null && mRootView!=null) {
							mStatefulLayout.showContent();
						}
					}
				});
			}
		};
		timerHandler.postDelayed(timerRunnable, delay);
	}

	private void bindData()	{
		// webview settings
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setAppCacheEnabled(true);
		mWebView.getSettings().setAppCachePath(getActivity().getCacheDir().getAbsolutePath());
		mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
		mWebView.getSettings().setDomStorageEnabled(true);
		mWebView.getSettings().setDatabaseEnabled(true);
		mWebView.getSettings().setSupportZoom(true);
		mWebView.getSettings().setBuiltInZoomControls(false);
		mWebView.getSettings().setUserAgentString("MadRabbitVideosWebView" + " " + BuildConfig.VERSION_NAME);

		// advanced webview settings
		mWebView.setListener(getActivity(), this);

		// webview style
		mWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY); // fixes scrollbar on Froyo

		// webview hardware acceleration
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
		}
		else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}

		// webview client
		mWebView.setWebViewClient(new MyWebViewClient());

		// webview key listener
		mWebView.setOnKeyListener(new OnKeyListener());

		// webview touch listener
		mWebView.requestFocus(View.FOCUS_DOWN);
		mWebView.setOnTouchListener(new OnTouchListener());

		// webview javascriptinterface to load exoplayer
		mWebView.addJavascriptInterface(new WebAppInterface(getContext()), "Android");
	}

	private class WebAppInterface {
		Context mContext;

		WebAppInterface(Context c) {
			mContext = c;
		}

		@JavascriptInterface
		public void loadPlayer(String url) {
			Intent intent = new Intent(getActivity(), VideoPlayerActivity.class);
			intent.putExtra(VIDEO_URL, url);
			MainFragment.this.startActivity(intent);

		}
	}

	private void setupStatefulLayout(Bundle savedInstanceState)	{
		// reference
		mStatefulLayout = (StatefulLayout) mRootView;

		// restore state
		mStatefulLayout.restoreInstanceState(savedInstanceState);
	}

	private void setupSwipeRefreshLayout() {
		SwipeRefreshLayout contentSwipeRefreshLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.container_content_swipeable);
		SwipeRefreshLayout offlineSwipeRefreshLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.container_offline_swipeable);
		SwipeRefreshLayout emptySwipeRefreshLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.container_empty_swipeable);

		contentSwipeRefreshLayout.setOnRefreshListener(this);
		offlineSwipeRefreshLayout.setOnRefreshListener(this);
		emptySwipeRefreshLayout.setOnRefreshListener(this);
	}


	private class MyWebViewClient extends WebViewClient	{
		private boolean mSuccess = true;

		@Override
		public boolean shouldOverrideUrlLoading(final WebView view, final String url) {

			if (url.startsWith("whatsapp://")) {
				UrlQuerySanitizer sanitizer = new UrlQuerySanitizer(url);
				String share = sanitizer.getValue("text");
				final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
				Intent sendIntent = new Intent();
				sendIntent.setAction(Intent.ACTION_SEND);
				sendIntent.putExtra(Intent.EXTRA_TEXT, share);
				sendIntent.setType("text/plain");
				sendIntent.setPackage("com.whatsapp");
				startActivity(sendIntent);
				return true;
			}

			if(url.startsWith("mailto:")){
				Intent i = new Intent(Intent.ACTION_SENDTO, Uri.parse(url));
				startActivity(i);
				return true;
			}

			return false;
		}

		@Override
		public void onPageFinished(final WebView view, final String url) {
			runTaskCallback(new Runnable() {
				public void run() {
					if(getActivity() != null && mSuccess) {
						showContent(500); // hide progress bar with delay to show webview content smoothly
						showActionBarProgress(false);
					}
				}
			});
		}


		@SuppressWarnings("deprecation")
		@Override
		public void onReceivedError(final WebView view, final int errorCode, final String description, final String failingUrl) {
			runTaskCallback(new Runnable() {
				public void run() {
					if(getActivity() != null) {
						mSuccess = false;
						mStatefulLayout.showEmpty();
						showActionBarProgress(false);
					}
				}
			});
		}


		@TargetApi(Build.VERSION_CODES.M)
		@Override
		public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
			// forward to deprecated method
			onReceivedError(view, error.getErrorCode(), error.getDescription().toString(), request.getUrl().toString());
		}
	}
}
