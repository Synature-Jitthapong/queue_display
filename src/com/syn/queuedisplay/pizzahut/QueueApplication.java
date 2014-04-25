package com.syn.queuedisplay.pizzahut;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;

public class QueueApplication extends Application{
	
	public static final String LOG_DIR = "pRoMiSeQueueLog";
	
	public static final String LOG_FILE_NAME = "pRoMiSeQueueLog";
	
	public static final String WEBSERVICE_NAME = "ws_mpos.asmx";
	
	public static Calendar sCalendar;
	
	public static Context sContext;
	
	@Override
	public void onCreate() {
		super.onCreate();
		sContext = getApplicationContext();
		sCalendar = Calendar.getInstance();
		try {
			com.j1tth4.mobile.util.FileManager fm = new 
					com.j1tth4.mobile.util.FileManager(sContext, LOG_DIR);
			fm.clear();
		} catch (Exception e) {
			
		}
	}
	
	public static String getFullUrl(){
		return getUrl() + "/" + WEBSERVICE_NAME;
	}
	
	private static String getUrl(){
		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(sContext);
		String url = sharedPref.getString(SettingActivity.PREF_URL, "");
		if(url.equals(""))
			return "";
		try {
			new URL(url);
		} catch (MalformedURLException e) {
			// not found protocal
			url = "http://" + url;
			e.printStackTrace();
		}
		return url;
	}

	public static boolean isEnableTb(){
		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(sContext);
		return sharedPref.getBoolean(SettingActivity.PREF_ENABLE_TB, false);
	}
	
	public static boolean isEnableTw(){
		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(sContext);
		return sharedPref.getBoolean(SettingActivity.PREF_ENABLE_TW, false);
	}
	
	public static long getRefresh(){
		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(sContext);
		long refresh = 60000;
		try {
			refresh = Long.parseLong(
					sharedPref.getString(SettingActivity.PREF_REFRESH, "60000"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return refresh;
	}
	
	public static String getInfoText(){
		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(sContext);
		return sharedPref.getString(SettingActivity.PREF_INFO_TEXT, "");
	}
	
	public static String getVDODir(){
		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(sContext);
		return sharedPref.getString(SettingActivity.PREF_VDO_DIR, "");
	}

	public static String getDeviceCode(){
		return Secure.getString(sContext.getContentResolver(),
				Secure.ANDROID_ID);
	}
	
	public static String getShopId(){
		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(sContext);
		return sharedPref.getString(SettingActivity.PREF_SHOP_ID, "");
	}
}
