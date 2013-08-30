package com.syn.queuedisplay;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SqliteHelper extends SQLiteOpenHelper {
	private static String dbName = "queue";

	private static final String TB_CONFIG = "CREATE TABLE config (" +
			" shop_id INTEGER DEFAULT 0, " +
			" server_ip TEXT, " + 
			" service_name TEXT, " +
			" update_interval INTEGER DEFAULT 0, " +
			" video_path TEXT, " +
			" logo_path TEXT, " +
			" port INTEGER DEFAULT 5050, " + 
			" is_enable_queue INTEGER DEFAULT 0, " +
			" is_enable_take INTEGER DEFAULT 1 );";
	
	private static final String TB_MARQUEE = "CREATE TABLE marquee (" +
			" text_id INTEGER PRIMARY KEY AUTOINCREMENT DEFAULT 0, " +
			" text TEXT, " +
			" duration REAL DEFAULT 1000, " +
			" ordering INTEGER DEFAULT 0); ";

	public SqliteHelper(Context context) {
		super(context, dbName, null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(TB_CONFIG);
		db.execSQL(TB_MARQUEE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS config");
		onCreate(db);
	}

}
