package com.syn.queuedisplay.pizzahut;

import java.lang.reflect.Type;

import com.google.gson.reflect.TypeToken;
import com.j1tth4.mobile.util.JSONUtil;
import com.syn.pos.QueueDisplayInfo;

import android.content.Context;

public class QueueDisplayService extends QueueDisplayMainService {
	
	private Callback callback;
	
	public QueueDisplayService(Context c, Callback listener) {
		super(c,  GET_CURR_ALL_QUEUE_METHOD);
		
		callback = listener;
	}

	@Override
	protected void onPostExecute(String result) {
		JSONUtil jsonUtil = new JSONUtil();
		Type type = new TypeToken<QueueDisplayInfo>() {}.getType();
		
		try {
			QueueDisplayInfo queueDisplayInfo = 
					(QueueDisplayInfo) jsonUtil.toObject(type, result);
			
			callback.onSuccess(queueDisplayInfo);
		} catch (Exception e) {
			callback.onError(e.getMessage());
		}
	}

	@Override
	protected void onPreExecute() {
		callback.onProgress();
	}
	
	public static interface Callback{
		public void onSuccess(QueueDisplayInfo qInfo);
		public void onProgress();
		public void onError(String msg);
	}
}
