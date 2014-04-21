package com.syn.queuedisplay.pizzahut;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import com.google.gson.reflect.TypeToken;
import com.j1tth4.mobile.connection.socket.ClientSocket;
import com.j1tth4.mobile.connection.socket.ISocketConnection;
import com.j1tth4.mobile.util.JSONUtil;
import com.j1tth4.mobile.util.MyMediaPlayer;
import com.syn.pos.QueueDisplayInfo;
import com.syn.queuedisplay.util.SystemUiHider;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class QueueDisplayActivity extends Activity{
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
	private String deviceCode = "";
	private String serviceUrl = "";
	private boolean isTakeRun = false;
	private boolean isQueueRun = false;
	private Handler handlerQueue;
	private Handler handlerTake;
	private Handler mHandlerWaitTake;
	private MyMediaPlayer myMediaPlayer;
	private boolean isPause = false;
	
	private QueueDisplayData config;
	//private ISocketConnection socketConn;
	
	private List<TakeAwayData> mTakeAwayLst;
	private TakeAwayQueueAdapter mTakeAwayAdapter;
	
	private QueueData queueData;
	private List<QueueData.MarqueeText> marqueeLst;
	private MarqueeAdapter marqueeAdapter;
	private SurfaceView surface;
	
	private WebView mWebView;
	private LinearLayout queueTakeLayout;
	private ListView mLvTakeAway;
	private LinearLayout queueLayout;
	private LinearLayout layoutA;
	private LinearLayout layoutB;
	private LinearLayout layoutC;
	private TextView tvCallA;
	private TextView tvCallB;
	private TextView tvCallC;
	private TextView tvCallASub;
	private TextView tvCallBSub;
	private TextView tvCallCSub;
	
	private TextView tvSumQA;
	private TextView tvSumQB;
	private TextView tvSumQC;
	private TextView tvPlaying;
	private TextView mTvVersion;
	
	LayoutInflater mInflater;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mInflater = LayoutInflater.from(QueueDisplayActivity.this);
		
		setContentView(R.layout.activity_queue_display);
		final View contentView = findViewById(R.id.headerLayout);
		surface = (SurfaceView) findViewById(R.id.surfaceView1);
		mWebView = (WebView) findViewById(R.id.webView1);
		mLvTakeAway = (ListView) findViewById(R.id.lvTakeAway);
		queueTakeLayout = (LinearLayout) findViewById(R.id.layoutQueueTake);
		queueLayout = (LinearLayout) findViewById(R.id.layoutQueue);
		layoutA = (LinearLayout) findViewById(R.id.queueALayout);
		layoutB = (LinearLayout) findViewById(R.id.queueBLayout);
		layoutC = (LinearLayout) findViewById(R.id.queueCLayout);
		tvCallA = (TextView) findViewById(R.id.textViewCallA);
		tvCallB = (TextView) findViewById(R.id.textViewCallB);
		tvCallC = (TextView) findViewById(R.id.textViewCallC);
		tvCallASub = (TextView) findViewById(R.id.tvCallASub);
		tvCallBSub = (TextView) findViewById(R.id.tvCallBSub);
		tvCallCSub = (TextView) findViewById(R.id.tvCallCSub);
		tvSumQB = (TextView) findViewById(R.id.textViewSumQB);
		tvSumQA = (TextView) findViewById(R.id.textViewSumQA);
		tvSumQC = (TextView) findViewById(R.id.textViewSumQC);
		tvPlaying = (TextView) findViewById(R.id.textViewPlaying);
		mTvVersion = (TextView) findViewById(R.id.tvVersion);
		
		PackageInfo pInfo;
		try {
			pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			mTvVersion.setText("v" + pInfo.versionName);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		deviceCode = Secure.getString(this.getContentResolver(),
				Secure.ANDROID_ID);
		
		readQueueData();
		readMarquee();

		myMediaPlayer = 
				new MyMediaPlayer(QueueDisplayActivity.this, surface, 
						queueData.getVideoPath(), new MyMediaPlayer.MediaPlayerStateListener(){

							@Override
							public void onError(Exception e) {
								try {
									myMediaPlayer.releaseMediaPlayer();
									myMediaPlayer.startPlayMedia();
								} catch (Exception e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
							}

							@Override
							public void onPlayedFileName(String fileName) {
								tvPlaying.setText(fileName);
							}
					
				});
		
		//new Thread(this).start();
		
		// update queue
		if(queueData.isEnableQueue()){
			queueLayout.setVisibility(View.VISIBLE);
			handlerQueue = new Handler();
			handlerQueue.post(updateQueue);
			isQueueRun = true;
		}else{
			queueLayout.setVisibility(View.GONE);
		}
		
		if(queueData.isEnableTake()){
			queueTakeLayout.setVisibility(View.VISIBLE);
			handlerTake = new Handler();
			handlerTake.post(updateQueueTake);
			mHandlerWaitTake = new Handler();
			mWaitTimeThread.start();
			isTakeRun = true;
		}else{
			queueTakeLayout.setVisibility(View.GONE);
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
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		// Trigger the initial hide() shortly after the activity has been
		// created, to briefly hint to the user that UI controls
		// are available.
		delayedHide(100);
	}

	private Runnable updateQueue = new Runnable() {

		@Override
		public void run() {
			if (isQueueRun) {
				try {
					createQueueFromService();
					handlerQueue.postDelayed(this, queueData.getUpdateInterval());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
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
			mHandlerWaitTake.postDelayed(this, 1000);
		}
		
	});
	
	private Runnable updateQueueTake = new Runnable() {

		@Override
		public void run() {
			if (isTakeRun) {
				try {
					createTakeAwayFromService();
					handlerTake.postDelayed(this,  queueData.getUpdateInterval());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	};
	
	private void readQueueData(){
		config = new QueueDisplayData(QueueDisplayActivity.this);
		queueData = config.readConfig();
		
		serviceUrl = "http://" + queueData.getServerIp() + "/" + queueData.getServiceName() + "/ws_mpos.asmx";
	}
	
	private void readMarquee(){
		marqueeLst = config.readMarquee();
		
		createMarqueeText();
	}
	
	private void createMarqueeText(){
		if (marqueeLst.size() > 0) {
			StringBuilder strHtml = new StringBuilder();
			StringBuilder strInfoText = new StringBuilder();
			for (QueueData.MarqueeText marquee : marqueeLst) {
				strInfoText.append(marquee.getTextVal());
				for (int i = 0; i < 10; i++) {
					strInfoText.append("\t");
				}
			}
			strHtml.append("<html><body style=\"background:#000;\"><FONT COLOR=\"#FFF\"><marquee direction=\"Left\" style=\"width: auto;\" >");
			strHtml.append(strInfoText);
			strHtml.append("</marquee></FONT></body></html>");
			mWebView.setVisibility(View.VISIBLE);
			mWebView.loadData(strHtml.toString(), "text/html", "UTF-8");
		} else {
			mWebView.setVisibility(View.GONE);
		}
	}
	
	private void popupSetting(){
		LayoutInflater inflater = LayoutInflater.from(QueueDisplayActivity.this);
		final View v = inflater.inflate(R.layout.activity_setting, null);
		final EditText txtShopId = (EditText) v.findViewById(R.id.editTextShopId);
		final EditText txtIp = (EditText) v.findViewById(R.id.editTextIp);
		final EditText txtPort = (EditText) v.findViewById(R.id.txtPort);
		final EditText txtService = (EditText) v.findViewById(R.id.editTextService);
		final EditText txtVideoDir = (EditText) v.findViewById(R.id.editTextVideoDir);
		final EditText txtMarquee = (EditText) v.findViewById(R.id.editTextMarquee);
		final CheckBox chkEnableQueue = (CheckBox) v.findViewById(R.id.checkBoxQueue);
		final CheckBox chkEnableTake = (CheckBox) v.findViewById(R.id.checkBoxTake);
		final EditText txtInterval = (EditText) v.findViewById(R.id.editTextInterval);
		final ListView lvMarquee = (ListView) v.findViewById(R.id.listViewMarquee);
		final Button btnIntervalMinus = (Button) v.findViewById(R.id.buttonIntervalMinus);
		final Button btnIntervalPlus = (Button) v.findViewById(R.id.buttonIntervalPlus);
		final Button btnCancel = (Button) v.findViewById(R.id.buttonCancel);
		final Button btnOk = (Button) v.findViewById(R.id.buttonOk);
		final Button btnAddMarquee = (Button) v.findViewById(R.id.btnAddMarquee);
		
		int shopId = queueData.getShopId();
		String strShopId = shopId != 0 ? Integer.toString(shopId) : "";
		
		txtShopId.setText(strShopId);
		txtIp.setText(queueData.getServerIp());
		txtPort.setText(Integer.toString(queueData.getPort()));
		txtService.setText(queueData.getServiceName());
		txtVideoDir.setText(queueData.getVideoPath());
		chkEnableQueue.setChecked(queueData.isEnableQueue());
		chkEnableTake.setChecked(queueData.isEnableTake());
		txtInterval.setText(Integer.toString((queueData.getUpdateInterval() == 0 ? 30000 : queueData.getUpdateInterval()) / 1000));
		
		marqueeAdapter = new MarqueeAdapter();
		lvMarquee.setAdapter(marqueeAdapter);
		
		btnIntervalMinus.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				int interval = Integer.parseInt(txtInterval.getText().toString());
				txtInterval.setText(Integer.toString(--interval));
			}
			
		});
		
		btnIntervalPlus.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				int interval = Integer.parseInt(txtInterval.getText().toString());
				txtInterval.setText(Integer.toString(++interval));
			}
			
		});
		
		btnAddMarquee.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View view) {
				String marquee = txtMarquee.getText().toString();
				config.addMarquee(marquee);
				txtMarquee.setText("");
				
				marqueeLst.add(config.getLastMarquee());
				marqueeAdapter.notifyDataSetChanged();
			}
			
		});
		
		final Dialog d = new Dialog(QueueDisplayActivity.this);
		d.setContentView(v);
		d.setTitle(R.string.title_activity_setting);
		d.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		d.show();
		
		btnCancel.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				d.dismiss();
			}
			
		});
		
		btnOk.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				String shopId = txtShopId.getText().toString();
				String ip = txtIp.getText().toString();
				int port = 5050;
				String service = txtService.getText().toString();
				String videoDir = txtVideoDir.getText().toString();
				int interval = 1;
				
				try {
					interval = Integer.parseInt(txtInterval.getText().toString()) * 1000;
					port = Integer.parseInt(txtPort.getText().toString());
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				if(!shopId.equals("") && !ip.equals("") && !service.equals("")){
					config.addConfig(Integer.parseInt(shopId), ip, port, service, interval, videoDir, "", 
							chkEnableQueue.isChecked(), chkEnableTake.isChecked());
					
					d.dismiss();
					
					QueueDisplayActivity.this.finish();
					Intent intent = 
							new Intent(QueueDisplayActivity.this, QueueDisplayActivity.class);
					QueueDisplayActivity.this.startActivity(intent);
				}else{
					String errMsg = "";
					
					if (shopId.equals(""))
						errMsg = "Please enter Shop ID.";
					else if(ip.equals(""))
						errMsg = "Please enter IP Address.";
					else
						errMsg = "Please enter Web service.";
					
					new AlertDialog.Builder(QueueDisplayActivity.this)
					.setTitle("Error")
					.setMessage(errMsg)
					.setNeutralButton("Close", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							
						}
					})
					.show();
				}
			}
			
		});
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
			popupSetting();
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
	
	private void drawTableQueue(QueueDisplayInfo qInfo){
		
		layoutA.removeAllViews();
		layoutB.removeAllViews();
		layoutC.removeAllViews();
		
		int totalQa = 0;
		int totalQb = 0;
		int totalQc = 0;
		
		for(QueueDisplayInfo.QueueInfo qData : qInfo.xListQueueInfo){
			if(qData.getiQueueGroupID() == 1){
				View vA = mInflater.inflate(R.layout.queue_template, null);
				TextView tvQ = (TextView) vA.findViewById(R.id.textViewQueue);
				TextView tvSub = (TextView) vA.findViewById(R.id.tvQueueSub);
				tvQ.setText(qData.getSzQueueName());
				tvSub.setText(qData.getSzCustomerName());
				layoutA.addView(vA);
				
				totalQa++;
			}
			
			if(qData.getiQueueGroupID() == 2){
				View vB = mInflater.inflate(R.layout.queue_template, null);
				TextView tvQ = (TextView) vB.findViewById(R.id.textViewQueue);
				TextView tvSub = (TextView) vB.findViewById(R.id.tvQueueSub);
				tvQ.setText(qData.getSzQueueName());
				tvSub.setText(qData.getSzCustomerName());
				layoutB.addView(vB);
				
				totalQb++;
			}
			
			if(qData.getiQueueGroupID() == 3){
				View vC = mInflater.inflate(R.layout.queue_template, null);
				TextView tvQ = (TextView) vC.findViewById(R.id.textViewQueue);
				TextView tvSub = (TextView) vC.findViewById(R.id.tvQueueSub);
				tvQ.setText(qData.getSzQueueName());
				tvSub.setText(qData.getSzCustomerName());
				layoutC.addView(vC);
				
				totalQc++;
			}
		}
		
		tvSumQA.setText(Integer.toString(totalQa));
		tvSumQB.setText(Integer.toString(totalQb));
		tvSumQC.setText(Integer.toString(totalQc));
		
		tvCallA.setText(qInfo.getSzCurQueueGroupA());
		tvCallB.setText(qInfo.getSzCurQueueGroupB());
		tvCallC.setText(qInfo.getSzCurQueueGroupC());
		tvCallASub.setText(qInfo.getSzCurQueueCustomerA());
		tvCallBSub.setText(qInfo.getSzCurQueueCustomerB());
		tvCallCSub.setText(qInfo.getSzCurQueueCustomerC());
	}
	
	private void createQueueFromService(){
		Log.i("queue", "call queue " + queueData.getUpdateInterval());
		
		// call service
		new QueueDisplayService(QueueDisplayActivity.this, queueData.getShopId(), deviceCode, 
				new QueueDisplayService.Callback() {
			
			@Override
			public void onSuccess(QueueDisplayInfo qInfo) {
//				JSONUtil jsonUtil = new JSONUtil();
//				Type type = new TypeToken<QueueDisplayInfo>() {}.getType();
//				String result = "{\"xListQueueInfo\":[{\"iQueueID\":8,\"iQueueIndex\":3,\"iQueueGroupID\":1,\"szQueueName\":\"A3\",\"szCustomerName\":\"testing\",\"iCustomerQty\":3,\"szStartQueueDate\":\"2013-09-24 15:01:29\",\"iWaitQueueMinTime\":23,\"iWaitQueueCurrentOfGroup\":0,\"iHasPreOrderList\":0},{\"iQueueID\":7,\"iQueueIndex\":1,\"iQueueGroupID\":2,\"szQueueName\":\"B1\",\"szCustomerName\":\"testing\",\"iCustomerQty\":3,\"szStartQueueDate\":\"2013-09-24 14:19:45\",\"iWaitQueueMinTime\":65,\"iWaitQueueCurrentOfGroup\":0,\"iHasPreOrderList\":0},{\"iQueueID\":5,\"iQueueIndex\":1,\"iQueueGroupID\":3,\"szQueueName\":\"C1\",\"szCustomerName\":\"jjjj\",\"iCustomerQty\":1,\"szStartQueueDate\":\"2013-09-24 13:50:48\",\"iWaitQueueMinTime\":94,\"iWaitQueueCurrentOfGroup\":0,\"iHasPreOrderList\":0},{\"iQueueID\":6,\"iQueueIndex\":2,\"iQueueGroupID\":3,\"szQueueName\":\"C2\",\"szCustomerName\":\"kkk\",\"iCustomerQty\":1,\"szStartQueueDate\":\"2013-09-24 14:12:30\",\"iWaitQueueMinTime\":72,\"iWaitQueueCurrentOfGroup\":0,\"iHasPreOrderList\":0},{\"iQueueID\":6,\"iQueueIndex\":2,\"iQueueGroupID\":3,\"szQueueName\":\"C2\",\"szCustomerName\":\"kkk\",\"iCustomerQty\":1,\"szStartQueueDate\":\"2013-09-24 14:12:30\",\"iWaitQueueMinTime\":72,\"iWaitQueueCurrentOfGroup\":0,\"iHasPreOrderList\":0}],\"szCurQueueGroupA\":\"A1\",\"szCurQueueCustomerA\":\"Customer name a\",\"szCurQueueGroupB\":\"B1\",\"szCurQueueCustomerB\":\"Customer name b\",\"szCurQueueGroupC\":\"C1\",\"szCurQueueCustomerC\":\"Customer name c\"}";
//				
//				qInfo = (QueueDisplayInfo) jsonUtil.toObject(type, result);

				drawTableQueue(qInfo);
			}
			
			@Override
			public void onProgress() {
				
			}
			
			@Override
			public void onError(String msg) {

			}
		}).execute(serviceUrl);
	}
	
	private void createTakeAwayFromService(){
//		String result = "[{\"szTransName\":\"Ibu Dina : 08765667777 xxxxxxyyyyyyyyyyyyyyzzzzzzzzzzzzzAAAAAAAA\",\"szQueueName\":\"(TA) : 24\",\"iKdsStatusID\":2,\"szKdsStatusName\":\"Pickup\",\"szStartDateTime\":\"44:47\",\"szFinishDateTime\":\"13-12-2556 10:15:15\"},{\"szTransName\":\"Bapak Jamhuri : 0976543678\",\"szQueueName\":\"(TA) : 30\",\"iKdsStatusID\":0,\"szKdsStatusName\":\"Waiting\",\"szStartDateTime\":\"08:07\",\"szFinishDateTime\":\"\"},{\"szTransName\":\"Ibu Susan : 098456787\",\"szQueueName\":\"(TA) : 32\",\"iKdsStatusID\":2,\"szKdsStatusName\":\"Pickup\",\"szStartDateTime\":\"56:16\",\"szFinishDateTime\":\"13-12-2556 10:15:15\"},{\"szTransName\":\"Ibu Ina : 09856789\",\"szQueueName\":\"(TA) : 36\",\"iKdsStatusID\":2,\"szKdsStatusName\":\"Pickup\",\"szStartDateTime\":\"46:47\",\"szFinishDateTime\":\"13-12-2556 10:15:15\"},{\"szTransName\":\"Bapak Koon : 0876890000\",\"szQueueName\":\"(TA) : 37\",\"iKdsStatusID\":2,\"szKdsStatusName\":\"Pickup\",\"szStartDateTime\":\"43:14\",\"szFinishDateTime\":\"13-12-2556 10:15:15\"},{\"szTransName\":\"Ibu Dina : 67899000\",\"szQueueName\":\"(TA) : 38\",\"iKdsStatusID\":2,\"szKdsStatusName\":\"Pickup\",\"szStartDateTime\":\"40:47\",\"szFinishDateTime\":\"13-12-2556 10:15:15\"},{\"szTransName\":\"1 : 4\",\"szQueueName\":\"(TA) : 39\",\"iKdsStatusID\":2,\"szKdsStatusName\":\"Pickup\",\"szStartDateTime\":\"59:01\",\"szFinishDateTime\":\"13-12-2556 10:15:15\"}]";
//		JSONUtil jsonUtil = new JSONUtil();
//		Type type = new TypeToken<List<TakeAwayData>>() {}.getType();
//		mTakeAwayLst = (List<TakeAwayData>) jsonUtil.toObject(type, result);
//		mTakeAwayAdapter = new TakeAwayQueueAdapter(this, mTakeAwayLst);
//		mLvTakeAway.setAdapter(mTakeAwayAdapter);
		
		new QueueTakeAwayService(QueueDisplayActivity.this, queueData.getShopId(), deviceCode, 
				new QueueTakeAwayService.Callback() {
			
			@Override
			public void onSuccess(List<TakeAwayData> takeAwayLst) {
				mTakeAwayLst = takeAwayLst;
				mTakeAwayAdapter = new TakeAwayQueueAdapter(QueueDisplayActivity.this, mTakeAwayLst);
				mLvTakeAway.setAdapter(mTakeAwayAdapter);
			}
			
			@Override
			public void onProgress() {
				
			}
			
			@Override
			public void onError(String msg) {

			}
		}).execute(serviceUrl);		
	}

	@Override
	protected void onResume() {
		super.onResume();
		try {
			myMediaPlayer.resume();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		try {
			myMediaPlayer.pause();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			isTakeRun = false;
			isQueueRun = false;
			handlerTake.removeCallbacks(updateQueueTake);
			handlerQueue.removeCallbacks(updateQueue);
			myMediaPlayer.releaseMediaPlayer();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void videoBackClicked(final View v){
		myMediaPlayer.back();
	}
	
	public void videoPauseClicked(final View v){
		if(!isPause){
			myMediaPlayer.pause();
			((ImageButton)v).setImageResource(android.R.drawable.ic_media_play);
			isPause = true;
		}
		else{
			myMediaPlayer.resume();
			((ImageButton)v).setImageResource(android.R.drawable.ic_media_pause);
			isPause = false;
		}
	}
	
	public void videoNextClicked(final View v){
		myMediaPlayer.next();
	}
	
//	@Override
//	public void run() {
//		try {
//			socketConn = new ClientSocket(queueData.getServerIp(), queueData.getPort());
//			String msg;
//			while ((msg = socketConn.receive()) != null) {
//				Log.d("msg", msg);
//				
//			}
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			new Thread(this).start();
//			e.printStackTrace();
//		}
//	}
	
	private class MarqueeAdapter extends BaseAdapter{
		private LayoutInflater inflater;
		
		public MarqueeAdapter(){
			inflater = LayoutInflater.from(QueueDisplayActivity.this);
		}
		
		@Override
		public int getCount() {
			return marqueeLst != null ? marqueeLst.size() : 0;
		}

		@Override
		public QueueData.MarqueeText getItem(int position) {
			return marqueeLst.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			final QueueData.MarqueeText marquee = marqueeLst.get(position);
			if(convertView == null){
				convertView = inflater.inflate(R.layout.marquee_template, null);
				holder = new ViewHolder();
				holder.tvText = (TextView) convertView.findViewById(R.id.textView1);
				holder.btnDel = (ImageButton) convertView.findViewById(R.id.imageButton1);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			
			holder.tvText.setText(marquee.getTextVal());
			holder.btnDel.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					config.removeMarquee(marquee.getTextId());
					marqueeLst.remove(position);
					notifyDataSetChanged();
				}
				
			});
			return convertView;
		}
		
		private class ViewHolder{
			TextView tvText;
			ImageButton btnDel;
		}
	}
}
