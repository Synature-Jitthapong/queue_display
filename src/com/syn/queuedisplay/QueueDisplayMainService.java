package com.syn.queuedisplay;

import org.ksoap2.serialization.PropertyInfo;

import android.content.Context;

import com.j1tth4.mobile.core.util.DotNetWebServiceTask;

public class QueueDisplayMainService extends DotNetWebServiceTask {

	public QueueDisplayMainService(Context c, String method) {
		super(c, method);
		
		property = new PropertyInfo();
		property.setName("iShopID");
		property.setValue(4);
		property.setType(int.class);
		soapRequest.addProperty(property);
		
		property = new PropertyInfo();
		property.setName("szDeviceCode");
		property.setValue("2e8752a898cb3c94");
		property.setType(String.class);
		soapRequest.addProperty(property);
	}

}
