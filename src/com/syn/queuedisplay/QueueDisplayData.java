package com.syn.queuedisplay;

import com.j1tth4.mobile.core.sqlite.SqliteDatabase;

import android.content.Context;
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
	
	public void addConfig(String ip, String serviceName, String videoPath, String logoPath){
		
	}
}
