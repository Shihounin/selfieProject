package com.example.selfieproject.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PhotoDatabase extends SQLiteOpenHelper {
	
	private static final String DATABASE_NAME = "selfie_bdd";
	private static final int DATABASE_VERSION = 1; 
	
	private static final String CREATE_STATEMENT = 
			"CREATE TABLE "+PhotoContract.PHOTO_TABLE_NAME+ " ("+
			PhotoContract.PHOTO + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
			PhotoContract.PHOTO_COLUMN_NAME+ " TEXT, "+
			PhotoContract.PHOTO_COLUMN_PATH+ " TEXT, "+
			PhotoContract.PHOTO_COLUMN_THUMB+ " TEXT" +
			")";
			
	
	
	public PhotoDatabase(Context context) {
		super(context,DATABASE_NAME,null,DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_STATEMENT);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS "+PhotoContract.PHOTO_TABLE_NAME);
		onCreate(db);
	}

}