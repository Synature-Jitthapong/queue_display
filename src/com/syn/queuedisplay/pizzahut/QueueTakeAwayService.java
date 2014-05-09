package com.syn.queuedisplay.pizzahut;

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.reflect.TypeToken;
import com.j1tth4.util.JSONUtil;
import com.j1tth4.util.Logger;
import com.syn.pos.WebServiceResult;

import android.content.Context;

public class QueueTakeAwayService extends QueueDisplayMainService {
	
	private LoadTakeAwayQueueListener mListener;
	
	public QueueTakeAwayService(Context c, LoadTakeAwayQueueListener listener) {
		super(c, GET_TAKEAWAY_QUEUE_METHOD);
		
		mListener = listener;
	}

	@Override
	protected void onPostExecute(String result) {
		Logger.appendLog(mContext, QueueApplication.LOG_DIR, 
				QueueApplication.LOG_FILE_NAME, "Data From WebService : " + result);	
		
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
					
					mListener.onPost(takeAwayLst);
				} catch (Exception e) {
					mListener.onError(e.getMessage());
				}
			}else{
				mListener.onError(result);
			}
		} catch (Exception e) {
			mListener.onError(result);
		}
	}

	@Override
	protected void onPreExecute() {
		mListener.onPre();
	}

	public static interface LoadTakeAwayQueueListener extends WebServiceProgressListener{
		public void onPost(List<TakeAwayData> takeAwayLst);
	}
}
