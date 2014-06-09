package com.synature.queuedisplay;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import com.syn.synaturequeue.SynatureQueue;
import com.synature.queuedisplay.util.SystemUiHider;
import com.synature.util.FileManager;
import com.synature.videoplayer.VideoPlayer;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class MainActivity extends Activity  implements VideoPlayer.MediaPlayerStateListener{
	
	/**
	 * code that send from pRoMiSe Front Program over socket 
	 * it means let's update data from webservice 
	 */
	public static final int UPDATE_CODE = 601;
	
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
	
	private SystemUiHider mSystemUiHider;
	
	private VideoPlayer mVideoPlayer;
	
	/*
	 * clock timer
	 */
	private Timer mClockTimer;
	
	private Calendar mCalendar;
	
	private SurfaceView mSurface;
	private FrameLayout mQueueContainer;
	private WebView mWebView;
	private TextView mTvPlaying;
	private TextView mTvVersion;
	private TextView mTvClock;
	private ImageView mImgLogo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
		final View contentView = findViewById(R.id.headerLayout);
		mSurface = (SurfaceView) findViewById(R.id.surfaceView1);
		mQueueContainer = (FrameLayout) findViewById(R.id.queueContainer);
		mWebView = (WebView) findViewById(R.id.webView1);
		mTvPlaying = (TextView) findViewById(R.id.textViewPlaying);
		mTvVersion = (TextView) findViewById(R.id.tvVersion);
		mTvClock = (TextView) findViewById(R.id.tvClock);
		mImgLogo = (ImageView) findViewById(R.id.imageView1);

		PackageInfo pInfo;
		try {
			pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			mTvVersion.setText("v" + pInfo.versionName);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Set up an instance of SystemUiHider to control the system UI for
		// this activity.
		mSystemUiHider = SystemUiHider.getInstance(this, contentView,
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
		contentView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (TOGGLE_ON_CLICK) {
					mSystemUiHider.toggle();
				} else {
					mSystemUiHider.show();
				}
			}
		});
		
		// init media player
		mVideoPlayer = new VideoPlayer(this, mSurface, 
				QueueApplication.getVDODir(), this);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		// Trigger the initial hide() shortly after the activity has been
		// created, to briefly hint to the user that UI controls
		// are available.
		delayedHide(100);
	}
	
	private void setupQueueView(){
		// init the queue
		SynatureQueue view = new SynatureQueue(this, QueueApplication.getShopId(),
				QueueApplication.getFullUrl(), "Sound");
		if(mQueueContainer.getChildCount() > 0)
			mQueueContainer.removeAllViews();
		mQueueContainer.addView(view);
	}
	
	private void configurationChange(){
		startClock();
		loadLogo();
	}
	
	private void startClock(){
		mCalendar = QueueApplication.sCalendar;
		if(mClockTimer != null){
			mClockTimer.cancel();
			mClockTimer.purge();
		}
		mClockTimer = new Timer();
		SystemClock clock = new SystemClock();
		mClockTimer.schedule(clock, 0, 1000);
	}
	
	private void loadLogo(){
		FileManager fm = new FileManager(this, ManageLogoFragment.LOGO_DIR);
		Bitmap bitmap = BitmapFactory.decodeFile(fm.getFile(ManageLogoFragment.FILE_NAME).getPath());
		mImgLogo.setImageBitmap(bitmap);
	}
	
	class SystemClock extends TimerTask{

		@Override
		public void run() {
			mCalendar.add(Calendar.SECOND, 1);
			
			final SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss", Locale.US);
			runOnUiThread(new Runnable(){

				@Override
				public void run() {
					mTvClock.setText(df.format(mCalendar.getTime()));
				}
				
			});
		}
		
	}
	
//	private final Thread mHidePickupThread = new Thread(new Runnable(){
//
//		@Override
//		public void run() {
//			if(mTakeAwayLst != null){
//				Iterator<TakeAwayData> i = mTakeAwayLst.iterator();
//				while(i.hasNext()){
//					TakeAwayData twData = i.next();
//					if(twData.getiKdsStatusID() == 2){
//						Calendar currTime = (Calendar) mCalendar.clone();
//						String strFinishDateTime = twData.getSzFinishDateTime();
//						if(!strFinishDateTime.equals("")){
//							SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.US);
//							int minuteToHide = twData.getiMinuteTimeAfterPickup();
//							try {
//								Calendar cFinish = (Calendar) mCalendar.clone();
//								cFinish.setTime(df.parse(strFinishDateTime));
//								cFinish.add(Calendar.MINUTE, minuteToHide);
//								
////								Logger.appendLog(MainActivity.this, QueueApplication.LOG_DIR, 
////										QueueApplication.LOG_FILE_NAME, "CurrTime : " + df.format(currTime.getTime()) +
////										" FinishTime : " + df.format(cFinish.getTime()));	
//								
//								if(currTime.compareTo(cFinish) >= 0){
//									i.remove();
//									mTakeAwayAdapter.notifyDataSetChanged();
//
//									Logger.appendLog(MainActivity.this, QueueApplication.LOG_DIR, 
//											QueueApplication.LOG_FILE_NAME, "CurrTime : " + df.format(currTime.getTime()) +
//											" FinishTime : " + df.format(cFinish.getTime()) + "\n" + 
//											" Remove  " + twData.getSzQueueName());									
//								}
//							} catch (ParseException e) {
//								Logger.appendLog(MainActivity.this, QueueApplication.LOG_DIR, 
//										QueueApplication.LOG_FILE_NAME, " Error when remove pickup : " + e.getMessage());
//							}
//						}
//					}
//				}
//			}
//			mHandlerHidePickup.postDelayed(this, 5000);
//		}
//		
//	});
	
//	private final Thread mWaitTimeThread = new Thread(new Runnable(){
//
//		@Override
//		public void run() {
//			Calendar calendar = Calendar.getInstance();
//			SimpleDateFormat df = new SimpleDateFormat("mm:ss");
//			if (mTakeAwayLst != null) {
//				for (TakeAwayData takeData : mTakeAwayLst) {
//					try {
//						Date d = df.parse(takeData.getSzStartDateTime());
//						calendar.setTime(d);
//						calendar.add(Calendar.SECOND, 1);
//						takeData.setSzStartDateTime(df.format(calendar
//								.getTime()));
//					} catch (ParseException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
//				mTakeAwayAdapter.notifyDataSetChanged();
//			}
//			mHandlerCountTwWaitTime.postDelayed(this, 1000);
//		}
//		
//	});
	
	private void createMarqueeText(){
		if (!QueueApplication.getInfoText().equals("")) {
			mWebView.setVisibility(View.VISIBLE);
			StringBuilder strHtml = new StringBuilder();
			strHtml.append("<body style=\"text-align:center;background:#0B0B0B; color:#F0F0F0 \">");
			strHtml.append("<marquee direction=\"left\" style=\"width: auto;\" >");
			strHtml.append(QueueApplication.getInfoText());
			strHtml.append("</marquee>");
			strHtml.append("</body>");
			mWebView.setVisibility(View.VISIBLE);
			mWebView.loadData(strHtml.toString(), "text/html; charset=UTF-8", null);
		} else {
			mWebView.setVisibility(View.GONE);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_activity, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_settings:
			startActivity(new Intent(MainActivity.this, SettingActivity.class));
			return true;
		case R.id.action_about:
			startActivity(new Intent(MainActivity.this, AboutActivity.class));
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
	
	@Override
	public void onPlayedFileName(String fileName) {
		mTvPlaying.setText(fileName);
	}

	@Override
	public void onError(Exception e) {
		mVideoPlayer.pause();
		mVideoPlayer.releaseMediaPlayer();
		mVideoPlayer.startPlayMedia();
	}

	private void releaseVideoPlayer(){
		mVideoPlayer.pause();
		mVideoPlayer.releaseMediaPlayer();
	}
	
	private void release(){	
		releaseVideoPlayer();
	}

	@Override
	protected void onResume() {
		setupQueueView();
		createMarqueeText();
		configurationChange();
		delayedHide(100);
		super.onResume();
		if(mVideoPlayer.isPause())
			mVideoPlayer.resume();
	}

	@Override
	protected void onPause() {
		mVideoPlayer.pause();
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		release();
		super.onDestroy();
	}

}
