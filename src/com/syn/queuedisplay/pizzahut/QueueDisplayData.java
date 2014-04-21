package com.syn.queuedisplay.pizzahut;

import java.util.ArrayList;
import java.util.HashMap;
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
	
	public QueueData.MarqueeText getLastMarquee(){
		QueueData.MarqueeText marquee = 
				new QueueData.MarqueeText();
		
		String strSql = "SELECT * FROM marquee ORDER BY text_id DESC LIMIT 1";
		open();
		Cursor cursor = db.rawQuery(strSql, null);
		if(cursor.moveToFirst()){
			do{
				marquee.setTextId(cursor.getInt(cursor.getColumnIndex("text_id")));
				marquee.setTextVal(cursor.getString(cursor.getColumnIndex("text")));
			}while(cursor.moveToNext());
		}
		cursor.close();
		close();
		return marquee;
	}
	
	public List<QueueData.MarqueeText> readMarquee(){
		List<QueueData.MarqueeText> marqueeLst = 
				new ArrayList<QueueData.MarqueeText>();
		
		String strSql = "SELECT * FROM marquee ORDER BY ordering";
		open();
		Cursor cursor = db.rawQuery(strSql, null);
		if(cursor.moveToFirst()){
			do{
				QueueData.MarqueeText marquee = 
						new QueueData.MarqueeText();
				marquee.setTextId(cursor.getInt(cursor.getColumnIndex("text_id")));
				marquee.setTextVal(cursor.getString(cursor.getColumnIndex("text")));
				marqueeLst.add(marquee);
			}while(cursor.moveToNext());
		}
		cursor.close();
		close();
		return marqueeLst;
	}
	
	public void removeMarquee(int id){
		String strSql = "DELETE FROM marquee " +
				" WHERE text_id=" + id;
		open();
		db.execSQL(strSql);
		close();
	}
	
	public void addMarquee(String marquee){
		open();
		ContentValues cv = new ContentValues();
		cv.put("text", marquee);
		db.insert("marquee", null, cv);
		close();
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
				config.setPort(cursor.getInt(cursor.getColumnIndex("port")));
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
	
	public void addConfig(int shopId, String ip, int port, String serviceName, int updateInterval,
			String videoPath, String logoPath, boolean isEnableQueue, boolean isEnableTake){
		ContentValues cv = new ContentValues();
		cv.put("shop_id", shopId);
		cv.put("server_ip", ip);
		cv.put("service_name", serviceName);
		cv.put("update_interval", updateInterval);
		cv.put("video_path", videoPath);
		cv.put("logo_path", logoPath);
		cv.put("port", port);
		cv.put("is_enable_queue", isEnableQueue == true ? 1 : 0);
		cv.put("is_enable_take", isEnableTake == true ? 1 : 0);
		
		open();
		db.execSQL("DELETE FROM config");
		db.insert("config", null, cv);
		close();
	}
}
	