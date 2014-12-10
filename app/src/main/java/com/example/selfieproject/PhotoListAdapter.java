package com.example.selfieproject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.example.selfieproject.provider.PhotoContract;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PhotoListAdapter extends CursorAdapter {
	
	private Map<String,Bitmap> mBitmap = new HashMap<String,Bitmap>();
	private ArrayList<PhotoRecord> mPhotoList = new ArrayList<PhotoRecord>();
	private static LayoutInflater pLayoutInflater = null;
	private Context mContext;

	
	public PhotoListAdapter(Context context) {
		super(context,null,0);
		mContext = context;
		pLayoutInflater = LayoutInflater.from(mContext);
		PhotoBitMapHelper.initStoragePath(mContext);
	}

	@Override
	public Object getItem(int position) {

		return mPhotoList.get(position);
	}



	

	static class ViewHolder {
		ImageView picture;
		TextView name;
	}


	public void add(PhotoRecord photorecord) {
		mPhotoList.add(photorecord);
		notifyDataSetChanged();

		
	}
	
	public void removeAllViews(){
		mPhotoList.clear();
		
		mContext.getContentResolver().delete(PhotoContract.PHOTO_URI, null, null);
        mContext.getContentResolver().notifyChange(PhotoContract.PHOTO_URI, null);
        
		this.notifyDataSetChanged();
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		ViewHolder holder = (ViewHolder) view.getTag();
		String path = cursor.getString(cursor.getColumnIndex(PhotoContract.PHOTO_COLUMN_THUMB));
		
			Bitmap bitmap = null;
			if(mBitmap.containsKey(path)) {
				bitmap  = mBitmap.get(path);
			} else {
				bitmap = PhotoBitMapHelper.getBitmapFromFile(path);
				mBitmap.put(path, bitmap);
			}
		
		holder.picture.setImageBitmap(bitmap);
		holder.name.setText(cursor.getString(cursor.getColumnIndex(PhotoContract.PHOTO_COLUMN_NAME)));
	
		
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View newView;
		ViewHolder holder = new ViewHolder();
		
		newView = pLayoutInflater.inflate(R.layout.photo_item_view, parent,false);
		holder.picture = (ImageView) newView.findViewById(R.id.photo_bitmap);
		holder.name = (TextView) newView.findViewById(R.id.photo_name);
		
		newView.setTag(holder);
		return newView;
	}
	
	public Cursor swapCursor(Cursor newCursor) {

		Cursor oldCursor = super.swapCursor(newCursor);
		mPhotoList.clear();
		
		
		if(null != newCursor) {
			newCursor.moveToFirst();
			while (!newCursor.isAfterLast()) {
				PhotoRecord photo = PhotoRecord.fromCursor(newCursor);
				mPhotoList.add(photo);
				newCursor.moveToNext();
			}
			
			
		}          
        
		return oldCursor;
	}

	public void addPhoto(PhotoRecord photo) {
		//adds the photo to the listview
		mPhotoList.add(photo);
		ContentValues values = new ContentValues();
		values.put(PhotoContract.PHOTO_COLUMN_NAME, photo.getName());
		values.put(PhotoContract.PHOTO_COLUMN_PATH, photo.getPath());
		values.put(PhotoContract.PHOTO_COLUMN_THUMB, photo.getThumb());
		
		mContext.getContentResolver().insert(PhotoContract.PHOTO_URI,values);
		
	}


}
