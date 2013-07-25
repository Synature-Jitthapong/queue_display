package com.syn.queuedisplay;

import java.util.ArrayList;
import java.util.List;

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
				config.setServerIp(cursor.getString(cursor.getColumnIndex("server_ip")));
				config.setServiceName(cursor.getString(cursor.getColumnIndex("service_name")));
				config.setVideoPath(cursor.getString(cursor.getColumnIndex("video_path")));
				config.setLogoPath(cursor.getString(cursor.getColumnIndex("logo_path")));
			}while(cursor.moveToNext());
		}
		close();
		return config;
	}
	
	public void addConfig(String ip, String serviceName, String videoPath, String logoPath){
		ContentValues cv = new ContentValues();
		cv.put("server_ip", ip);
		cv.put("service_name", serviceName);
		cv.put("video_path", videoPath);
		cv.put("logo_path", logoPath);
		
		open();
		db.execSQL("DELETE FROM config");
		db.insert("config", null, cv);
		close();
	}
}
	