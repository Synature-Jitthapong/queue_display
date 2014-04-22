package com.syn.queuedisplay.pizzahut;

import org.ksoap2.serialization.PropertyInfo;
import android.content.Context;
import com.j1tth4.mobile.util.DotNetWebServiceTask;

public class QueueDisplayMainService extends DotNetWebServiceTask {
	
	public static final String PARAM_SHOP_ID = "iShopID";
	public static final String PARAM_DEVICE_CODE = "szDeviceCode";
	public static final String GET_CURR_ALL_QUEUE_METHOD = "WSiQueue_JSON_GetCurrentAllQueueDisplay";
	public static final String GET_TAKEAWAY_QUEUE_METHOD = "WSiKDS_JSON_GetTakeAwayDisplayData";
	
	public QueueDisplayMainService(Context c, String method) {
		super(c, method);
		
		mProperty = new PropertyInfo();
		mProperty.setName(PARAM_SHOP_ID);
		mProperty.setValue(QueueApplication.getShopId());
		mProperty.setType(int.class);
		mSoapRequest.addProperty(mProperty);
		
		mProperty = new PropertyInfo();
		mProperty.setName(PARAM_DEVICE_CODE);
		mProperty.setValue(QueueApplication.getDeviceCode());
		mProperty.setType(String.class);
		mSoapRequest.addProperty(mProperty);
	}
}
