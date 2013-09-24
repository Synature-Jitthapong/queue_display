package com.syn.queuedisplay;

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.reflect.TypeToken;
import com.j1tth4.mobile.util.JSONUtil;
import com.syn.pos.WebServiceResult;

import android.content.Context;

public class QueueTakeAwayService extends QueueDisplayMainService {
	private Callback callback;
	
	public QueueTakeAwayService(Context c, int shopId, String deviceCode, Callback listener) {
		super(c, shopId, deviceCode, "WSiKDS_JSON_GetTakeAwayDisplayData");
		
		callback = listener;
	}

	@Override
	protected void onPostExecute(String result) {
		JSONUtil jsonUtil = new JSONUtil();
		Type type = new TypeToken<WebServiceResult>() {}.getType();
		try {
			WebServiceResult wsResult = (WebServiceResult) jsonUtil.toObject(type, result);
			
			if(wsResult.getiResultID() == 0){
				try {
					jsonUtil = new JSONUtil();
					type = new TypeToken<List<TakeAwayData>>() {}.getType();
					List<TakeAwayData> takeAwayLst = 
							(List<TakeAwayData>) jsonUtil.toObject(type, wsResult.getSzResultData());
					
					callback.onSuccess(takeAwayLst);
				} catch (Exception e) {
					callback.onError(e.getMessage());
				}
			}else{
				callback.onError(result);
			}
		} catch (Exception e) {
			callback.onError(result);
		}
	}

	@Override
	protected void onPreExecute() {
		callback.onProgress();
	}

	public static interface Callback{
		public void onSuccess(List<TakeAwayData> takeAwayLst);
		public void onProgress();
		public void onError(String msg);
	}
}
