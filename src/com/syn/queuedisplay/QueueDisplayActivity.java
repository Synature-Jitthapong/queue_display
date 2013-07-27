package com.syn.queuedisplay;

import java.util.List;

import syn.pos.data.model.QueueDisplayInfo;

import com.j1tth4.mobile.core.util.MyMediaPlayer;
import com.syn.queuedisplay.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings.Secure;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
	private boolean isRun = true;
	private Handler handler;
	private Handler handler2;

	private QueueData queueData;
	private SurfaceView surface;
	private SurfaceHolder surfaceHolder;
	private TextView tvMarquee;
	

	private LinearLayout takeAwayLayout;
	private LinearLayout layoutA;
	private LinearLayout layoutB;
	private LinearLayout layoutC;
	private TextView tvCallA;
	private TextView tvCallB;
	private TextView tvCallC;
	
	private TextView tvSumQA;
	private TextView tvSumQB;
	private TextView tvSumQC;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_queue_display);
		final View contentView = findViewById(R.id.queue_layout);
		surface = (SurfaceView) findViewById(R.id.surfaceView1);
		tvMarquee = (TextView) findViewById(R.id.textViewMarquee);
		takeAwayLayout = (LinearLayout) findViewById(R.id.takeAwayLayout);
		layoutA = (LinearLayout) findViewById(R.id.queueALayout);
		layoutB = (LinearLayout) findViewById(R.id.queueBLayout);
		layoutC = (LinearLayout) findViewById(R.id.queueCLayout);
		tvCallA = (TextView) findViewById(R.id.textViewTakeNo);
		tvCallB = (TextView) findViewById(R.id.textViewCallB);
		tvCallC = (TextView) findViewById(R.id.textViewCallC);
		tvSumQB = (TextView) findViewById(R.id.textViewSumQB);
		tvSumQA = (TextView) findViewById(R.id.textViewSumQA);
		tvSumQC = (TextView) findViewById(R.id.textViewSumQC);
		
		tvMarquee.setSelected(true);
		
		surfaceHolder = surface.getHolder();
		
		deviceCode = Secure.getString(this.getContentResolver(),
				Secure.ANDROID_ID);
		
		readQueueData();
		
		MyMediaPlayer mMediaPlayer = 
				new MyMediaPlayer(QueueDisplayActivity.this, surface, surfaceHolder, queueData.getVideoPath());
		

		// update queue
		handler = new Handler();
		//handler.post(updateQueue);
		handler2 = new Handler();
		handler2.post(updateQueueTake);
		
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

	// thread update queue
	private Runnable updateQueue = new Runnable() {

		@Override
		public void run() {
			if (isRun) {
				try {
					createQueue();
					handler.postDelayed(this, 5000);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	};
	
	// thread update queue take
	private Runnable updateQueueTake = new Runnable() {

		@Override
		public void run() {
			if (isRun) {
				try {
					createTakeAway();
					handler2.postDelayed(this, 5000);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	};
	
	private void readQueueData(){
		QueueDisplayData config = 
				new QueueDisplayData(QueueDisplayActivity.this);
		queueData = config.readConfig();
		
		serviceUrl = "http://" + queueData.getServerIp() + "/" + queueData.getServiceName() + "/ws_mpos.asmx";
	}
	
	private void popupSetting(){
		LayoutInflater inflater = LayoutInflater.from(QueueDisplayActivity.this);
		final View v = inflater.inflate(R.layout.activity_setting, null);
		final EditText txtShopId = (EditText) v.findViewById(R.id.editText4);
		final EditText txtIp = (EditText) v.findViewById(R.id.editText1);
		final EditText txtService = (EditText) v.findViewById(R.id.editText2);
		final EditText txtVideoDir = (EditText) v.findViewById(R.id.editText3);
		final Button btnCancel = (Button) v.findViewById(R.id.button1);
		final Button btnOk = (Button) v.findViewById(R.id.button2);
		
		txtShopId.setText(Integer.toString(queueData.getShopId()));
		txtIp.setText(queueData.getServerIp());
		txtService.setText(queueData.getServiceName());
		txtVideoDir.setText(queueData.getVideoPath());
		
		final Dialog d = new Dialog(QueueDisplayActivity.this);
		d.setContentView(v);
		d.setTitle("Setting");
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
				String service = txtService.getText().toString();
				String videoDir = txtVideoDir.getText().toString();
				
				if(!shopId.equals("") && !ip.equals("") && !service.equals("")){
					QueueDisplayData config = 
							new QueueDisplayData(QueueDisplayActivity.this);
					
					config.addConfig(Integer.parseInt(shopId), ip, service, videoDir, "");
					
					d.dismiss();
					
					QueueDisplayActivity.this.finish();
					Intent intent = 
							new Intent(QueueDisplayActivity.this, QueueDisplayActivity.class);
					QueueDisplayActivity.this.startActivity(intent);
				}else{
					String errMsg = "";
					if(ip.equals(""))
						errMsg = "Please enter IP Address.";
					else if (shopId.equals(""))
						errMsg = "Please enter Shop ID.";
					else
						errMsg = "Please enter Web service.";
					
					new AlertDialog.Builder(QueueDisplayActivity.this)
					.setTitle("Error")
					.setMessage(errMsg)
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
	
	private void createQueue(){
		final LayoutInflater inflater = LayoutInflater.from(QueueDisplayActivity.this);
		
		// call service
		new QueueDisplayService(QueueDisplayActivity.this, queueData.getShopId(), deviceCode, 
				new QueueDisplayService.Callback() {
			
			@Override
			public void onSuccess(QueueDisplayInfo qInfo) {

				layoutA.removeAllViews();
				layoutB.removeAllViews();
				layoutC.removeAllViews();
				
				int totalQa = 0;
				int totalQb = 0;
				int totalQc = 0;
				
				for(QueueDisplayInfo.QueueInfo qData : qInfo.xListQueueInfo){
					if(qData.getiQueueGroupID() == 1){
						View vA = inflater.inflate(R.layout.queue_template, null);
						TextView tvQ = (TextView) vA.findViewById(R.id.textViewQueue);
						tvQ.setText(qData.getSzQueueName());
						layoutA.addView(vA);
						
						totalQa++;
					}
					
					if(qData.getiQueueGroupID() == 2){
						View vB = inflater.inflate(R.layout.queue_template, null);
						TextView tvQ = (TextView) vB.findViewById(R.id.textViewQueue);
						tvQ.setText(qData.getSzQueueName());
						layoutB.addView(vB);
						
						totalQb++;
					}
					
					if(qData.getiQueueGroupID() == 3){
						View vC = inflater.inflate(R.layout.queue_template, null);
						TextView tvQ = (TextView) vC.findViewById(R.id.textViewQueue);
						tvQ.setText(qData.getSzQueueName());
						layoutC.addView(vC);
						
						totalQc++;
					}
				}
				
				tvSumQA.setText("A=" + Integer.toString(totalQa));
				tvSumQB.setText("B=" + Integer.toString(totalQb));
				tvSumQC.setText("C=" + Integer.toString(totalQc));
				
				tvCallA.setText(qInfo.getSzCurQueueGroupA());
				tvCallB.setText(qInfo.getSzCurQueueGroupB());
				tvCallC.setText(qInfo.getSzCurQueueGroupC());
			}
			
			@Override
			public void onProgress() {
				
			}
			
			@Override
			public void onError(String msg) {
//				isRun = false;
//				popup("Error", msg);
			}
		}).execute(serviceUrl);
	}
	
	private void createTakeAway(){
		
		new QueueTakeAwayService(QueueDisplayActivity.this, queueData.getShopId(), deviceCode, 
				new QueueTakeAwayService.Callback() {
			
			@Override
			public void onSuccess(List<TakeAwayData> takeAwayLst) {

				takeAwayLayout.removeAllViews();
				
				LayoutInflater inflater = LayoutInflater.from(QueueDisplayActivity.this);
				
				int i = 0;
				for(TakeAwayData takeAwayData : takeAwayLst){
					View v = inflater.inflate(R.layout.take_away_template, null);
					TextView tvName = (TextView) v.findViewById(R.id.textViewTakeName);
					TextView tvTimeIn = (TextView) v.findViewById(R.id.textViewTakeTimeIn);
					TextView tvStatus = (TextView) v.findViewById(R.id.textViewTakeStatus);
					TextView tvNo = (TextView) v.findViewById(R.id.textViewTakeNo);
					
					tvNo.setText(takeAwayData.getSzQueueName());
					tvNo.setSelected(true);
					tvName.setText(takeAwayData.getSzTransName());
					tvName.setSelected(true);
					tvTimeIn.setText(takeAwayData.getSzStartDateTime());
					tvTimeIn.setSelected(true);
					tvStatus.setText(takeAwayData.getSzKdsStatusName());
					tvStatus.setSelected(true);

					takeAwayLayout.addView(v);
					i++;
				}
			}
			
			@Override
			public void onProgress() {
				
			}
			
			@Override
			public void onError(String msg) {
//				isRun = false;
//				popup("Error", msg);
			}
		}).execute(serviceUrl);
		
	}
	
	private void popup(String title, String msg){
		new AlertDialog.Builder(QueueDisplayActivity.this)
		.setTitle(title)
		.setMessage(msg)
		.show();
	}
}
