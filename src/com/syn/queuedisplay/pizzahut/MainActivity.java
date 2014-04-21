package com.syn.queuedisplay.custom;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.j1tth4.mediaplayer.VideoPlayer;
import com.j1tth4.mobile.util.Logger;
import com.syn.pos.QueueDisplayInfo;
import com.syn.queuedisplay.custom.QueueDatabase.CallingQueueData;
import com.syn.queuedisplay.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class MainActivity extends Activity implements Runnable, QueueServerSocket.ServerSocketListener,
	SpeakCallingQueue.OnPlaySoundListener, VideoPlayer.MediaPlayerStateListener{
	/**
	 * Whether or not the system UI should be auto-hidden after
	 * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
	 */
	private static final boolean AUTO_HIDE = true;

	/**
	 * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
	 * user interaction before hiding the system UI.
	 */
	private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

	/**
	 * If set, will toggle the system UI visibility upon interaction. Otherwise,
	 * will show the system UI visibility upon interaction.
	 */
	private static final boolean TOGGLE_ON_CLICK = true;

	/**
	 * The flags to pass to {@link SystemUiHider#getInstance}.
	 */
	private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

	/**
	 * The instance of the {@link SystemUiHider} for this activity.
	 */
	private static final int SPEAK_DELAY = 2500;
	
	/**
	 * index of playing sound
	 */
	private int mQueueIdx = -1;
	
	private boolean mIsPause = false;
	
	private SQLiteHelper mSqliteHelper;
	
	private SQLiteDatabase mSqlite;
	
	private Fragment mQueueFragment;
	private QueueDatabase mQueueDatabase;
	private SystemUiHider mSystemUiHider;
	private Handler mHandlerQueue;
	private Thread mConnThread;
	private Thread mLoadCallingQueueThread;
	private Handler mHandlerSpeakQueue;
	private VideoPlayer mVideoPlayer;
	private List<CallingQueueData> mCallingQueueLst;
	private SpeakCallingQueue mSpeakCallingQueue;
	private SurfaceView mSurface;
	private WebView mWebView;
	private TextView mTvPlaying;
	private TextView mTvVersion;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mSurface = (SurfaceView) findViewById(R.id.surfaceView1);
		mWebView = (WebView) findViewById(R.id.webView1);
		mTvPlaying = (TextView) findViewById(R.id.textViewPlaying);
		mTvVersion = (TextView) findViewById(R.id.tvVersion);
		
//		PackageInfo pInfo;
//		try {
//			pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
//			mTvVersion.setText("v" + pInfo.versionName);
//		} catch (NameNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		//new Thread(this).start();
		
		// Set up an instance of SystemUiHider to control the system UI for
		// this activity.
		final View head = findViewById(R.id.head);
		mSystemUiHider = SystemUiHider.getInstance(this, head,
				HIDER_FLAGS);
		mSystemUiHider.setup();
		mSystemUiHider
				.setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
					@Override
					@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
					public void onVisibilityChange(boolean visible) {
						if (visible && AUTO_HIDE) {
							// Schedule a hide().
							delayedHide(AUTO_HIDE_DELAY_MILLIS);
						}
					}
				});

		// Set up the user interaction to manually show or hide the system UI.
		head.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (TOGGLE_ON_CLICK) {
					mSystemUiHider.toggle();
				} else {
					mSystemUiHider.show();
				}
			}
		});
		// init object
		mHandlerQueue = new Handler();
		mHandlerQueue.post(mUpdateQueue);
		
		mSqliteHelper = new SQLiteHelper(this);
		mSqlite = mSqliteHelper.getWritableDatabase();
		mQueueDatabase = new QueueDatabase(mSqlite);
		mQueueDatabase.deleteQueue();
		mHandlerSpeakQueue = new Handler();
		mSpeakCallingQueue = new SpeakCallingQueue(this);
		mLoadCallingQueueThread = new Thread(this);
		mLoadCallingQueueThread.start();
		try {
			mConnThread = new Thread(new QueueServerSocket(this));
			mConnThread.start();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		// init media player
		mVideoPlayer = new VideoPlayer(QueueApplication.sContext, mSurface, 
				QueueApplication.getVDODir(), this);
		setupQueueColumn();
		createMarqueeText();
	}

	public SQLiteDatabase getDatabase(){
		return mSqlite;
	}
	
	private void setupQueueColumn(){
		if(QueueApplication.getColumns().equals("1"))
			mQueueFragment = QueueColumnFragment.newInstance();
		else if(QueueApplication.getColumns().equals("2"))
			mQueueFragment = Queue2ColumnFragment.newInstance();
		else if(QueueApplication.getColumns().equals("3"))
			mQueueFragment = Queue3ColumnFragment.newInstance();
		else if(QueueApplication.getColumns().equals("4"))
			mQueueFragment = Queue4ColumnFragment.newInstance();
		getFragmentManager().beginTransaction().replace(R.id.queueContainer, mQueueFragment).commit();
	}
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		// Trigger the initial hide() shortly after the activity has been
		// created, to briefly hint to the user that UI controls
		// are available.
		delayedHide(100);
	}

	private void createMarqueeText(){
		if (!QueueApplication.getInfoText().equals("")) {
			mWebView.setVisibility(View.VISIBLE);
			StringBuilder strHtml = new StringBuilder();
			strHtml.append("<body style=\"text-align:center;background:#BEBEBE; \">");
			strHtml.append("<marquee direction=\"left\" style=\"width: auto;\" >");
			strHtml.append(QueueApplication.getInfoText());
			strHtml.append("</marquee>");
			strHtml.append("</body>");
			mWebView.setVisibility(View.VISIBLE);
			mWebView.loadData(strHtml.toString(), "text/html", "UTF-8");
		} else {
			mWebView.setVisibility(View.GONE);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.setting, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		switch (item.getItemId()) {
		case R.id.action_settings:
			intent = new Intent(MainActivity.this, SettingActivity.class);
			startActivity(intent);
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Touch listener to use for in-layout UI controls to delay hiding the
	 * system UI. This is to prevent the jarring behavior of controls going away
	 * while interacting with activity UI.
	 */
	View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View view, MotionEvent motionEvent) {
			if (AUTO_HIDE) {
				delayedHide(AUTO_HIDE_DELAY_MILLIS);
			}
			return false;
		}
	};

	Handler mHideHandler = new Handler();
	Runnable mHideRunnable = new Runnable() {
		@Override
		public void run() {
			mSystemUiHider.hide();
		}
	};

	/**
	 * Schedules a call to hide() in [delay] milliseconds, canceling any
	 * previously scheduled calls.
	 */
	private void delayedHide(int delayMillis) {
		mHideHandler.removeCallbacks(mHideRunnable);
		mHideHandler.postDelayed(mHideRunnable, delayMillis);
	}
	
	/**
	 * Play calling sound
	 */
	private Runnable mSpeakQueueRunnable = new Runnable(){

		@Override
		public void run() {
			if(mCallingQueueLst.size() > 0){
				CallingQueueData q = mCallingQueueLst.get(mQueueIdx);
				int callingTime = q.getCallingTime();
				try {
					mSpeakCallingQueue.speak(q.getQueueName());
					mQueueDatabase.updateCallingQueue(q.getQueueName(), ++callingTime);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	};
	
	private QueueDisplayService.LoadQueueListener mLoadQueueListener = 
			new QueueDisplayService.LoadQueueListener() {
				
				@Override
				public void onPre() {
				}
				
				@Override
				public void onPost() {
				}
				
				@Override
				public void onError(String msg) {
				}
				
				@Override
				public void onPost(QueueDisplayInfo queueInfo) {
					updateQueueData(queueInfo);
				}
	};
	
	private void updateQueueData(QueueDisplayInfo queueInfo){
		if(mQueueFragment instanceof QueueColumnFragment){
			((QueueColumnFragment) mQueueFragment).setQueueData(queueInfo);
		}else if(mQueueFragment instanceof Queue2ColumnFragment){
			((Queue2ColumnFragment) mQueueFragment).setQueueData(queueInfo);
		}else if(mQueueFragment instanceof Queue3ColumnFragment){
			((Queue3ColumnFragment) mQueueFragment).setQueueData(queueInfo);
		}else if(mQueueFragment instanceof Queue4ColumnFragment){
			((Queue4ColumnFragment) mQueueFragment).setQueueData(queueInfo);
		}
	}
	
	private Runnable mUpdateQueue = new Runnable() {

		@Override
		public void run() {
			try {
				new QueueDisplayService(QueueApplication.sContext, 
						mLoadQueueListener).execute(QueueApplication.getFullUrl());
				mHandlerQueue.postDelayed(this, Long.parseLong(QueueApplication.getRefresh()));
			} catch (Exception e) {
				Logger.appendLog(MainActivity.this, QueueApplication.LOG_DIR, 
						QueueApplication.LOG_FILE_NAME, e.getMessage());
				e.printStackTrace();
			}
		}

	};

	private synchronized void stopConnThread(){
		if(mConnThread != null)
		{
			try {
				mConnThread.interrupt();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private synchronized void stopLoadCallingThread(){
		if(mLoadCallingQueueThread != null)
		{
			try {
				mLoadCallingQueueThread.interrupt();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void release(){
		try {
			stopLoadCallingThread();
			stopConnThread();
			mHandlerQueue.removeCallbacks(mUpdateQueue);
			mHandlerSpeakQueue.removeCallbacks(mSpeakQueueRunnable);
			mVideoPlayer.releaseMediaPlayer();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}

	@Override
	protected void onDestroy() {
		release();
		super.onDestroy();
	}

	public void videoBackClicked(final View v){
		mVideoPlayer.back();
	}
	
	public void videoPauseClicked(final View v){
		if(!mIsPause){
			mVideoPlayer.pause();
			((ImageButton)v).setImageResource(android.R.drawable.ic_media_play);
			mIsPause = true;
		}
		else{
			mVideoPlayer.resume();
			((ImageButton)v).setImageResource(android.R.drawable.ic_media_pause);
			mIsPause = false;
		}
	}
	
	public void videoNextClicked(final View v){
		mVideoPlayer.next();
	}

	@Override
	public void onReceipt(String msg) {
		Gson gson = new Gson();
		Type type = new TypeToken<QueueDisplayInfo>() {}.getType();
		try {
			final QueueDisplayInfo queueDisplayInfo = 
					(QueueDisplayInfo) gson.fromJson(msg, type);
			runOnUiThread(new Runnable(){

				@Override
				public void run() {
					updateQueueData(queueDisplayInfo);
				}
				
			});
		} catch (Exception e) {
		}
	}

	@Override
	public void onAcceptErr(String msg) {
		
	}

	@Override
	public void onSpeaking() {
		try {
			mVideoPlayer.setSoundVolumn(0.0f, 0.0f);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onSpeakComplete() {
		try {
			mVideoPlayer.setSoundVolumn(1.0f, 1.0f);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(mQueueIdx < mCallingQueueLst.size() - 1){
			mHandlerSpeakQueue.postDelayed(mSpeakQueueRunnable, SPEAK_DELAY);
			mQueueIdx++;
		}else{
			mQueueIdx = -1;
		}
	}

	@Override
	public void onPlayedFileName(String fileName) {
		mTvPlaying.setText(fileName);
	}

	@Override
	public void onError(Exception e) {
		try {
			mVideoPlayer.releaseMediaPlayer();
			mVideoPlayer.startPlayMedia();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	@Override
	public void run() {
		while(!mLoadCallingQueueThread.isInterrupted()){
			if(mQueueIdx == -1){
				mCallingQueueLst = mQueueDatabase.listCallingQueueName(
						Integer.parseInt(QueueApplication.getSpeakTimes()));
				if(mCallingQueueLst.size() > 0){
					mQueueIdx = 0;
					mHandlerSpeakQueue.postDelayed(mSpeakQueueRunnable, SPEAK_DELAY);
				}
			}
		}
	}
}
