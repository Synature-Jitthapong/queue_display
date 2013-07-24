package com.syn.queuedisplay;

import org.ksoap2.serialization.PropertyInfo;

import android.content.Context;

public class QueueDisplayService extends QueueDisplayMainService {

	public QueueDisplayService(Context c) {
		super(c, "");
		
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
		// TODO Auto-generated method stub
		super.onPostExecute(result);
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
	}
	
	public static interface Callback{
		public void onSuccess();
		public void onProgress();
		public void onError();
	}

}
