package com.syn.queuedisplay;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class QueueDisplayData {
	private SqliteHelper sqlite;
	private SQLiteDatabase db;
	
	public QueueDisplayData(Context c){
		sqlite = new SqliteHelper(c);
	}
	
	private void open() throws SQLException{
		db = sqlite.getWritableDatabase();
	}
	
	private void close() throws SQLException{
		db.close();
	}
	
	public QueueData readConfig(){
		QueueData config = new QueueData();
		String strSql = "SELECT * FROM config";
		open();
		Cursor cursor = db.rawQuery(strSql, null);
		if(cursor.moveToFirst()){
			do{
				config.setShopId(cursor.getInt(cursor.getColumnIndex("shop_id")));
				config.setServerIp(cursor.getString(cursor.getColumnIndex("server_ip")));
				config.setServiceName(cursor.getString(cursor.getColumnIndex("service_name")));
				config.setVideoPath(cursor.getString(cursor.getColumnIndex("video_path")));
				config.setLogoPath(cursor.getString(cursor.getColumnIndex("logo_path")));
				config.setEnableQueue(cursor.getInt(cursor.getColumnIndex("is_enable_queue")) == 1 ? true : false);
				config.setEnableTake(cursor.getInt(cursor.getColumnIndex("is_enable_take")) == 1 ? true : false);
				config.setUpdateInterval(cursor.getInt(cursor.getColumnIndex("update_interval")));
			}while(cursor.moveToNext());
		}
		close();
		return config;
	}
	
	public void addConfig(int shopId, String ip, String serviceName, int updateInterval,
			String videoPath, String logoPath, boolean isEnableQueue, boolean isEnableTake){
		ContentValues cv = new ContentValues();
		cv.put("shop_id", shopId);
		cv.put("server_ip", ip);
		cv.put("service_name", serviceName);
		cv.put("update_interval", updateInterval);
		cv.put("video_path", videoPath);
		cv.put("logo_path", logoPath);
		cv.put("is_enable_queue", isEnableQueue == true ? 1 : 0);
		cv.put("is_enable_take", isEnableTake == true ? 1 : 0);
		
		open();
		db.execSQL("DELETE FROM config");
		db.insert("config", null, cv);
		close();
	}
}
	