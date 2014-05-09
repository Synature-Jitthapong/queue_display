package com.syn.queuedisplay.pizzahut;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.j1tth4.util.Logger;

import android.content.Context;

public class UpdateServerTimeService extends QueueDisplayMainService{
	
	public static final String METHOD = "WSmPOS_JSON_GetCurrentServerDateTime";
	
	public static final String SERVER_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	
	private WebServiceProgressListener mListener;
	
	public UpdateServerTimeService(Context c, WebServiceProgressListener listener) {
		super(c, METHOD);
		mListener = listener;
	}

	@Override
	protected void onPreExecute() {
		mListener.onPre();
	}

	@Override
	protected void onPostExecute(String result) {
		if(result != null && !result.equals("")){
			try {
				SimpleDateFormat df = new SimpleDateFormat(SERVER_DATE_TIME_FORMAT);
				QueueApplication.sCalendar.setTime(df.parse(result));
				mListener.onPost();
			} catch (ParseException e) {
				Logger.appendLog(mContext, QueueApplication.LOG_DIR, 
						QueueApplication.LOG_FILE_NAME, "Error, Parse date time from server : " + e.getMessage());
			}
		}
	}

}
