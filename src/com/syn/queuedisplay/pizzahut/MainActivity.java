package com.syn.queuedisplay.pizzahut;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.j1tth4.mediaplayer.VideoPlayer;
import com.j1tth4.mobile.util.Logger;
import com.syn.pos.QueueDisplayInfo;
import com.syn.queuedisplay.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class MainActivity extends Activity  implements 
	QueueServerSocket.ServerSocketListener, VideoPlayer.MediaPlayerStateListener{
	
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
	
	private Thread mSocketThread;
	
	private Timer mTwQTimer;
	private Timer mTbQTimer;
	
	private Handler mHandlerCountTwWaitTime;
	
	private List<TakeAwayData> mTakeAwayLst;
	
	private TakeAwayQueueAdapter mTakeAwayAdapter;
	
	private SurfaceView mSurface;
	
	private WebView mWebView;
	private LinearLayout mQueueTakeLayout;
	private LinearLayout mQueueLayout;
	private ListView mLvTakeAway;
	private LinearLayout mLayoutA;
	private LinearLayout mLayoutB;
	private LinearLayout mLayoutC;
	private TextView mTvCallA;
	private TextView mTvCallB;
	private TextView mTvCallC;
	private TextView mTvCallASub;
	private TextView mTvCallBSub;
	private TextView mTvCallCSub;
	private TextView mTvSumQA;
	private TextView mTvSumQB;
	private TextView mTvSumQC;
	private TextView mTvPlaying;
	private TextView mTvVersion;
	private LayoutInflater mInflater;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mInflater = LayoutInflater.from(MainActivity.this);
		
		setContentView(R.layout.main_activity);
		final View contentView = findViewById(R.id.headerLayout);
		mSurface = (SurfaceView) findViewById(R.id.surfaceView1);
		mWebView = (WebView) findViewById(R.id.webView1);
		mLvTakeAway = (ListView) findViewById(R.id.lvTakeAway);
		mQueueTakeLayout = (LinearLayout) findViewById(R.id.layoutQueueTake);
		mQueueLayout = (LinearLayout) findViewById(R.id.layoutQueue);
		mLayoutA = (LinearLayout) findViewById(R.id.queueALayout);
		mLayoutB = (LinearLayout) findViewById(R.id.queueBLayout);
		mLayoutC = (LinearLayout) findViewById(R.id.queueCLayout);
		mTvCallA = (TextView) findViewById(R.id.textViewCallA);
		mTvCallB = (TextView) findViewById(R.id.textViewCallB);
		mTvCallC = (TextView) findViewById(R.id.textViewCallC);
		mTvCallASub = (TextView) findViewById(R.id.tvCallASub);
		mTvCallBSub = (TextView) findViewById(R.id.tvCallBSub);
		mTvCallCSub = (TextView) findViewById(R.id.tvCallCSub);
		mTvSumQB = (TextView) findViewById(R.id.textViewSumQB);
		mTvSumQA = (TextView) findViewById(R.id.textViewSumQA);
		mTvSumQC = (TextView) findViewById(R.id.textViewSumQC);
		mTvPlaying = (TextView) findViewById(R.id.textViewPlaying);
		mTvVersion = (TextView) findViewById(R.id.tvVersion);
		
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
		
		if(QueueApplication.isEnableTb()){
			mQueueLayout.setVisibility(View.VISIBLE);
			mTbQTimer = new Timer();
			
			scheduleTb();
		}else{
			mQueueLayout.setVisibility(View.GONE);
		}
		
		if(QueueApplication.isEnableTw()){
			mQueueTakeLayout.setVisibility(View.VISIBLE);
			mTwQTimer = new Timer();
			
			scheduleTw();
			
			mHandlerCountTwWaitTime = new Handler();
			mWaitTimeThread.start();
		}else{
			mQueueTakeLayout.setVisibility(View.GONE);
		}
		
		// init socket thread
		try {
			mSocketThread = new Thread(new QueueServerSocket(this));
			mSocketThread.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// create marquee
		createMarqueeText();
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		// Trigger the initial hide() shortly after the activity has been
		// created, to briefly hint to the user that UI controls
		// are available.
		delayedHide(100);
	}
	
	private void scheduleTb(){
		UpdateTbTask tbTask = new UpdateTbTask();
		mTbQTimer.schedule(tbTask, 1000, QueueApplication.getRefresh());
	}
	
	private void scheduleTw(){
		UpdateTwTask twTask = new UpdateTwTask();
		mTwQTimer.schedule(twTask, 2000, QueueApplication.getRefresh());
	}
	
	class UpdateTwTask extends TimerTask{

		@Override
		public void run() {
			try {
				new QueueTakeAwayService(MainActivity.this, 
						mLoadTakeAwayQueueListener).execute(QueueApplication.getFullUrl());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	class UpdateTbTask extends TimerTask{

		@Override
		public void run() {
			try {
				new QueueDisplayService(MainActivity.this, 
						mLoadQueueListener).execute(QueueApplication.getFullUrl());
			} catch (Exception e) {
				Logger.appendLog(MainActivity.this, QueueApplication.LOG_DIR, 
						QueueApplication.LOG_FILE_NAME, e.getMessage());
				e.printStackTrace();
			}
		}
		
	}
	
	// listener load takeaway queue
	private QueueTakeAwayService.LoadTakeAwayQueueListener mLoadTakeAwayQueueListener = 
			new QueueTakeAwayService.LoadTakeAwayQueueListener() {
				
				@Override
				public void onPre() {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onPost() {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onError(String msg) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onPost(List<TakeAwayData> takeAwayLst) {
					updateTakeAwayQueue(takeAwayLst);
				}
	};
	
	// listener load table queue
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

	private final Thread mWaitTimeThread = new Thread(new Runnable(){

		@Override
		public void run() {
			Calendar calendar = Calendar.getInstance();
			SimpleDateFormat df = new SimpleDateFormat("mm:ss");
			if (mTakeAwayLst != null) {
				for (TakeAwayData takeData : mTakeAwayLst) {
					try {
						Date d = df.parse(takeData.getSzStartDateTime());
						calendar.setTime(d);
						calendar.add(Calendar.SECOND, 1);
						takeData.setSzStartDateTime(df.format(calendar
								.getTime()));
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				mTakeAwayAdapter.notifyDataSetChanged();
			}
			mHandlerCountTwWaitTime.postDelayed(this, 1000);
		}
		
	});
	


	private void createMarqueeText(){
		if (!QueueApplication.getInfoText().equals("")) {
			mWebView.setVisibility(View.VISIBLE);
			StringBuilder strHtml = new StringBuilder();
			strHtml.append("<html><head><meta charset=\"UTF-8\"></head>");
			strHtml.append("<body style=\"text-align:center;background:#0B0B0B; color:#F0F0F0 \">");
			strHtml.append("<marquee direction=\"left\" style=\"width: auto;\" >");
			strHtml.append(QueueApplication.getInfoText());
			strHtml.append("</marquee>");
			strHtml.append("</body></html>");
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
		switch (item.getItemId()) {
		case R.id.action_settings:
			Intent intent = new Intent(MainActivity.this, SettingActivity.class);
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
	
	private void updateQueueData(QueueDisplayInfo qInfo){
		
		mLayoutA.removeAllViews();
		mLayoutB.removeAllViews();
		mLayoutC.removeAllViews();
		
		int totalQa = 0;
		int totalQb = 0;
		int totalQc = 0;
		
		for(QueueDisplayInfo.QueueInfo qData : qInfo.xListQueueInfo){
			if(qData.getiQueueGroupID() == 1){
				View vA = mInflater.inflate(R.layout.queue_template, null);
				TextView tvQ = (TextView) vA.findViewById(R.id.tvQueueName);
				TextView tvSummary = (TextView) vA.findViewById(R.id.tvQueueSummary);
				tvQ.setText(qData.getSzQueueName());
				tvSummary.setText(qData.getSzCustomerName());
				mLayoutA.addView(vA);
				
				totalQa++;
			}
			
			if(qData.getiQueueGroupID() == 2){
				View vB = mInflater.inflate(R.layout.queue_template, null);
				TextView tvQ = (TextView) vB.findViewById(R.id.tvQueueName);
				TextView tvSummary = (TextView) vB.findViewById(R.id.tvQueueSummary);
				tvQ.setText(qData.getSzQueueName());
				tvSummary.setText(qData.getSzCustomerName());
				mLayoutB.addView(vB);
				
				totalQb++;
			}
			
			if(qData.getiQueueGroupID() == 3){
				View vC = mInflater.inflate(R.layout.queue_template, null);
				TextView tvQ = (TextView) vC.findViewById(R.id.tvQueueName);
				TextView tvSummary = (TextView) vC.findViewById(R.id.tvQueueSummary);
				tvQ.setText(qData.getSzQueueName());
				tvSummary.setText(qData.getSzCustomerName());
				mLayoutC.addView(vC);
				
				totalQc++;
			}
		}
		
		mTvSumQA.setText(Integer.toString(totalQa));
		mTvSumQB.setText(Integer.toString(totalQb));
		mTvSumQC.setText(Integer.toString(totalQc));
		
		mTvCallA.setText(qInfo.getSzCurQueueGroupA());
		mTvCallB.setText(qInfo.getSzCurQueueGroupB());
		mTvCallC.setText(qInfo.getSzCurQueueGroupC());
		mTvCallASub.setText(qInfo.getSzCurQueueCustomerA());
		mTvCallBSub.setText(qInfo.getSzCurQueueCustomerB());
		mTvCallCSub.setText(qInfo.getSzCurQueueCustomerC());
	}
	
	private void updateTakeAwayQueue(List<TakeAwayData> takeAwayLst){
//		String result = "[{\"szTransName\":\"Ibu Dina : 08765667777 xxxxxxyyyyyyyyyyyyyyzzzzzzzzzzzzzAAAAAAAA\",\"szQueueName\":\"(TA) : 24\",\"iKdsStatusID\":2,\"szKdsStatusName\":\"Pickup\",\"szStartDateTime\":\"44:47\",\"szFinishDateTime\":\"13-12-2556 10:15:15\"},{\"szTransName\":\"Bapak Jamhuri : 0976543678\",\"szQueueName\":\"(TA) : 30\",\"iKdsStatusID\":0,\"szKdsStatusName\":\"Waiting\",\"szStartDateTime\":\"08:07\",\"szFinishDateTime\":\"\"},{\"szTransName\":\"Ibu Susan : 098456787\",\"szQueueName\":\"(TA) : 32\",\"iKdsStatusID\":2,\"szKdsStatusName\":\"Pickup\",\"szStartDateTime\":\"56:16\",\"szFinishDateTime\":\"13-12-2556 10:15:15\"},{\"szTransName\":\"Ibu Ina : 09856789\",\"szQueueName\":\"(TA) : 36\",\"iKdsStatusID\":2,\"szKdsStatusName\":\"Pickup\",\"szStartDateTime\":\"46:47\",\"szFinishDateTime\":\"13-12-2556 10:15:15\"},{\"szTransName\":\"Bapak Koon : 0876890000\",\"szQueueName\":\"(TA) : 37\",\"iKdsStatusID\":2,\"szKdsStatusName\":\"Pickup\",\"szStartDateTime\":\"43:14\",\"szFinishDateTime\":\"13-12-2556 10:15:15\"},{\"szTransName\":\"Ibu Dina : 67899000\",\"szQueueName\":\"(TA) : 38\",\"iKdsStatusID\":2,\"szKdsStatusName\":\"Pickup\",\"szStartDateTime\":\"40:47\",\"szFinishDateTime\":\"13-12-2556 10:15:15\"},{\"szTransName\":\"1 : 4\",\"szQueueName\":\"(TA) : 39\",\"iKdsStatusID\":2,\"szKdsStatusName\":\"Pickup\",\"szStartDateTime\":\"59:01\",\"szFinishDateTime\":\"13-12-2556 10:15:15\"}]";
//		JSONUtil jsonUtil = new JSONUtil();
//		Type type = new TypeToken<List<TakeAwayData>>() {}.getType();
//		mTakeAwayLst = (List<TakeAwayData>) jsonUtil.toObject(type, result);
//		mTakeAwayAdapter = new TakeAwayQueueAdapter(this, mTakeAwayLst);
//		mLvTakeAway.setAdapter(mTakeAwayAdapter);
		
		mTakeAwayLst = takeAwayLst;
		mTakeAwayAdapter = new TakeAwayQueueAdapter(MainActivity.this, mTakeAwayLst);
		mLvTakeAway.setAdapter(mTakeAwayAdapter);		
	}
	
	@Override
	public void onPlayedFileName(String fileName) {
		mTvPlaying.setText(fileName);
	}

	@Override
	public void onError(Exception e) {
		releaseVideo();
	}

	@Override
	public void onReceipt(String msg) {
//		Gson gson = new Gson();
//		Type type = new TypeToken<QueueDisplayInfo>() {}.getType();
//		try {
//			final QueueDisplayInfo queueDisplayInfo = 
//					(QueueDisplayInfo) gson.fromJson(msg, type);
//			runOnUiThread(new Runnable(){
//
//				@Override
//				public void run() {
//					updateQueueData(queueDisplayInfo);
//				}
//				
//			});
//		} catch (Exception e) {
//		}
		
		String code = "0";
		String computerId = "0";
		try {
			String[] seq = msg.split("|");
			code = seq[0];
			computerId = seq[1];
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(Integer.parseInt(code) == UPDATE_CODE){
			if(QueueApplication.isEnableTb()){
				if(mTbQTimer != null)
					mTbQTimer.cancel();
				scheduleTb();
			}
			
			if(QueueApplication.isEnableTw()){
				if(mTwQTimer != null)
					mTwQTimer.cancel();
				scheduleTw();
			}
		}
	}

	@Override
	public void onAcceptErr(String msg) {
		// TODO Auto-generated method stub
		
	}
	
	private void releaseVideo(){
		try {
			mVideoPlayer.pause();
			mVideoPlayer.releaseMediaPlayer();
			mVideoPlayer.startPlayMedia();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	private void release(){
		releaseVideo();
		
		if(mTwQTimer != null)
			mTwQTimer.cancel();
		if(mTbQTimer != null)
			mTbQTimer.cancel();
		
		stopSocketThread();
	}
	
	private synchronized void stopSocketThread(){
		if(mSocketThread != null)
		{
			try {
				mSocketThread.interrupt();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void onPause() {
		release();
		super.onPause();
	}

}
