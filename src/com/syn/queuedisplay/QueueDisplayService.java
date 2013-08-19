package com.syn.queuedisplay;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.j1tth4.mobile.util.JSONUtil;

import syn.pos.data.model.QueueDisplayInfo;

import android.content.Context;

public class QueueDisplayService extends QueueDisplayMainService {
	private Callback callback;
	public QueueDisplayService(Context c, int shopId, String deviceCode, Callback listener) {
		super(c, shopId, deviceCode, "WSiQueue_JSON_GetCurrentAllQueueDisplay");
		
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
