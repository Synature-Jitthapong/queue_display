package com.syn.queuedisplay;

import org.ksoap2.serialization.PropertyInfo;

import android.content.Context;

public class QueueDisplayService extends QueueDisplayMainService {
	Callback callback;
	public QueueDisplayService(Context c, Callback listener) {
		super(c, "");
		
		callback = listener;
		
//		property = new PropertyInfo();
//		property.setName("iShopID");
//		property.setValue(4);
//		property.setType(int.class);
//		soapRequest.addProperty(property);
//		
//		property = new PropertyInfo();
//		property.setName("szDeviceCode");
//		property.setValue("2e8752a898cb3c94");
//		property.setType(String.class);
//		soapRequest.addProperty(property);
	}

	@Override
	protected void onPostExecute(String result) {
		callback.onSuccess();
	}

	@Override
	protected void onPreExecute() {
		callback.onProgress();
	}
	
	public static interface Callback{
		public void onSuccess();
		public void onProgress();
		public void onError();
	}

}
