package com.syn.queuedisplay;

import syn.pos.data.json.GsonDeserialze;
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
		GsonDeserialze gdz = new GsonDeserialze();
		
		try {
			QueueDisplayInfo queueDisplayInfo = gdz
					.deserializeQueueDisplayInfoJSON(result);
			
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
