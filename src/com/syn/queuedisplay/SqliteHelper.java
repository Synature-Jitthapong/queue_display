package com.syn.queuedisplay;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SqliteHelper extends SQLiteOpenHelper {
	private static String dbName = "queue";

	private static final String createTbConfig = "CREATE TABLE config (" +
			" shop_id INTEGER DEFAULT 0, " +
			" server_ip TEXT, " + 
			" service_name TEXT, " +
			" update_interval INTEGER DEFAULT 0, " +
			" video_path TEXT, " +
			" logo_path TEXT, " +
			" is_enable_queue INTEGER DEFAULT 0, " +
			" is_enable_take INTEGER DEFAULT 1 );";

	public SqliteHelper(Context context) {
		super(context, dbName, null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(createTbConfig);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS config");
		onCreate(db);
	}

}
