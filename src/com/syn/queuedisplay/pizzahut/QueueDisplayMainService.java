package com.syn.queuedisplay.pizzahut;

import org.ksoap2.serialization.PropertyInfo;
import android.content.Context;
import com.j1tth4.mobile.util.DotNetWebServiceTask;

public class QueueDisplayMainService extends DotNetWebServiceTask {
	public QueueDisplayMainService(Context c, int shopId, String deviceCode, String method) {
		super(c, method);
		
		mProperty = new PropertyInfo();
		mProperty.setName("iShopID");
		mProperty.setValue(shopId);
		mProperty.setType(int.class);
		mSoapRequest.addProperty(mProperty);
		
		mProperty = new PropertyInfo();
		mProperty.setName("szDeviceCode");
		mProperty.setValue(deviceCode);
		mProperty.setType(String.class);
		mSoapRequest.addProperty(mProperty);
	}
}
