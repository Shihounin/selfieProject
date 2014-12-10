package com.example.selfieproject;

import com.example.selfieproject.provider.PhotoContract;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;

public class PhotoRecord {
		
	private Uri mFilepath;
	private String name;
	private String path;
	private String thumb;
	private Bitmap photo;
	private int _id;
	


	public static PhotoRecord fromCursor(Cursor cursor) {
		PhotoRecord photoRecord = new PhotoRecord();
			
		photoRecord.setId(cursor.getInt(cursor.getColumnIndex(PhotoContract.PHOTO)));
		photoRecord.setPath(cursor.getString(cursor.getColumnIndex(PhotoContract.PHOTO_COLUMN_PATH)));
		photoRecord.setName(cursor.getString(cursor.getColumnIndex(PhotoContract.PHOTO_COLUMN_NAME)));
		
		return photoRecord;		
	}
	
	public Uri getPhotoUri () {
		return mFilepath;
	}
	
	public void setPhotoUri (Uri uri) {
		this.mFilepath = uri;
	}
	
	public int getId() {
		return _id;
	}
	
	public void setId(int id) {
		this._id = id;
	}
	
	public String getName () {
		return name;
	}
	
	public void setName (String name) {
		this.name = name;
	}
	
	public String getPath () {
		return path;
	}
	
	public void setPath(String path) {
		this.path = path;
	}
		
	public String getThumb () {
		return thumb;
	}
	
	public void setThumbPath(String thumb) {
		this.thumb = thumb;
	}
	
	public Bitmap getPhoto() {
		return photo;
	}
	
	public void setBmp(Bitmap photo) {
		this.photo = photo;
	}
		
}
