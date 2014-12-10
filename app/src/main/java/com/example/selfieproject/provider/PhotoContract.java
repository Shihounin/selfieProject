package com.example.selfieproject.provider;

import android.net.Uri;

public class PhotoContract {
	
	public static final String AUTHORITY = "com.example.selfieproject.provider";
	public static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY + "/");
	
	public static final String PHOTO_TABLE_NAME = "photo";

	// The URI for this table.
	public static final Uri PHOTO_URI = Uri.withAppendedPath(BASE_URI, PHOTO_TABLE_NAME);
	
	
	//Columns for the photo table
	public static final String PHOTO = "_id";
	public static final String PHOTO_COLUMN_NAME = "name";
	public static final String PHOTO_COLUMN_PATH = "path";
	public static final String PHOTO_COLUMN_THUMB = "thumb";

}