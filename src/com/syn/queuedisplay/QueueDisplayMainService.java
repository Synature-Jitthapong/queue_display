package com.syn.queuedisplay;

import org.ksoap2.serialization.PropertyInfo;

import syn.pos.data.model.QueueDisplayInfo;

import android.content.Context;

import com.j1tth4.mobile.util.DotNetWebServiceTask;

public class QueueDisplayMainService extends DotNetWebServiceTask {
	public QueueDisplayMainService(Context c, int shopId, String deviceCode, String method) {
		super(c, method);
		
		property = new PropertyInfo();
		property.setName("iShopID");
		property.setValue(shopId);
		property.setType(int.class);
		soapRequest.addProperty(property);
		
		property = new PropertyInfo();
		property.setName("szDeviceCode");
		property.setValue(deviceCode);
		property.setType(String.class);
		soapRequest.addProperty(property);
	}
}
