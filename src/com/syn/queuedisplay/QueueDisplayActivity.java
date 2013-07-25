package com.syn.queuedisplay;

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

	private QueueData queueData;
	private SurfaceView surface;
	private SurfaceHolder surfaceHolder;
	private TextView tvMarquee;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_queue_display);
		final View contentView = findViewById(R.id.queue_layout);
		surface = (SurfaceView) findViewById(R.id.surfaceView1);
		tvMarquee = (TextView) findViewById(R.id.textViewMarquee);
		tvMarquee.setSelected(true);
		
		surfaceHolder = surface.getHolder();

		readQueueData();
		
		MyMediaPlayer mMediaPlayer = 
				new MyMediaPlayer(QueueDisplayActivity.this, surface, surfaceHolder, queueData.getVideoPath());
		
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

	private void readQueueData(){
		QueueDisplayData config = 
				new QueueDisplayData(QueueDisplayActivity.this);
		queueData = config.readConfig();
	}
	
	private void popupSetting(){
		LayoutInflater inflater = LayoutInflater.from(QueueDisplayActivity.this);
		final View v = inflater.inflate(R.layout.activity_setting, null);
		final EditText txtIp = (EditText) v.findViewById(R.id.editText1);
		final EditText txtService = (EditText) v.findViewById(R.id.editText2);
		final EditText txtVideoDir = (EditText) v.findViewById(R.id.editText3);
		final Button btnCancel = (Button) v.findViewById(R.id.button1);
		final Button btnOk = (Button) v.findViewById(R.id.button2);
		
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
				String ip = txtIp.getText().toString();
				String service = txtService.getText().toString();
				String videoDir = txtVideoDir.getText().toString();
				
				if(!ip.equals("") && !service.equals("")){
					QueueDisplayData config = 
							new QueueDisplayData(QueueDisplayActivity.this);
					
					config.addConfig(ip, service, videoDir, "");
					
					d.dismiss();
					
					Intent intent = 
							new Intent(QueueDisplayActivity.this, QueueDisplayActivity.class);
					QueueDisplayActivity.this.startActivity(intent);
				}else{
					String errMsg = "";
					if(ip.equals(""))
						errMsg = "Please enter IP Address";
					else
						errMsg = "Please enter Web service";
					
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
}
