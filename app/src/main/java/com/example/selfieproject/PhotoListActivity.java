package com.example.selfieproject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.example.selfieproject.provider.PhotoContract;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.app.LoaderManager.LoaderCallbacks;

/**
 * An activity representing a list of Photos.
 * @author Graham Aro
 */

public class PhotoListActivity extends ListActivity implements LoaderCallbacks<Cursor>{

	
	public String mCurrentPhotoPath;

	private PhotoListAdapter mAdapter;
	
	private String mPhotoRecordPath;
	
	private static final String PHOTO_PATH = "DailyPhotos/Photos";
	private static final int REQUEST_TAKE_PHOTO = 0;
	private static int ALARM_TOGGLE = 1;
	
	private AlarmManager mAlarmManager;
	private Intent mNotificationReceiverIntent, mLoggerReceiverIntent;
	private PendingIntent mNotificationReceiverPendingIntent,mLoggerReceiverPendingIntent;
	
	private static final long INITIAL_ALARM_DELAY = 2 * 60 * 1000;
	protected static final long JITTER = 2 * 60 * 1000;
	//private static String mBitmapStoragePath;

	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		if (savedInstanceState != null) {
			mCurrentPhotoPath = savedInstanceState.getString(PHOTO_PATH);
		}
		
		mAdapter = new PhotoListAdapter(this);
		
		getListView().setAdapter(mAdapter);
		getLoaderManager().initLoader(0,null,this);
		//initalize alarm on when app starts
		if (ALARM_TOGGLE == 1) {
			ALARM_TOGGLE++;
			toggleAlarm();
		}
		
		getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> arg0, View v,int index, long arg3) {
            	// TODO delete individual view on mAdapter and delete file in /Photos
            	
            	Toast.makeText(getApplicationContext(),"LongClick Successful", Toast.LENGTH_LONG).show();
     	        return false;
            }
		});

		
	}


	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		PhotoRecord photo = (PhotoRecord)mAdapter.getItem(position);
		Intent intent = new Intent(PhotoListActivity.this,main_activity.class);
		intent.putExtra(main_activity.EXTRA_NAME,photo.getName());
		intent.putExtra(main_activity.EXTRA_PATH,photo.getPath());

		startActivity(intent);
	}

	
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}
	
	
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.camera:
			dispatchTakePictureIntent();
			return true;
		case R.id.delete:
			try {
				deleteAll();
				return true;
			} catch (IOException e) {
				Toast.makeText(getApplicationContext(), "Error occured while deleting the File", Toast.LENGTH_SHORT).show();
			}
		case R.id.alarm:
			toggleAlarm();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void toggleAlarm() {
		
			// Get the AlarmManager Service
			mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

			// Create an Intent to broadcast to the AlarmNotificationReceiver
			mNotificationReceiverIntent = new Intent(PhotoListActivity.this,
							PhotoAlarmReceiver.class);

			// Create an PendingIntent that holds the NotificationReceiverIntent
			mNotificationReceiverPendingIntent = PendingIntent.getBroadcast(
							PhotoListActivity.this, 0, mNotificationReceiverIntent, 0);

			// Create an Intent to broadcast to the AlarmLoggerReceiver
			mLoggerReceiverIntent = new Intent(PhotoListActivity.this,
							AlarmLoggerReceiver.class);

			// Create PendingIntent that holds the mLoggerReceiverPendingIntent
			mLoggerReceiverPendingIntent = PendingIntent.getBroadcast(
							PhotoListActivity.this, 0, mLoggerReceiverIntent, 0);
		
		if((ALARM_TOGGLE & 1) == 0 ) {
		
			// Set repeating alarm
			mAlarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME,
					SystemClock.elapsedRealtime() + INITIAL_ALARM_DELAY,
					JITTER,
					mNotificationReceiverPendingIntent);

			// Set repeating alarm to fire shortly after previous alarm
			mAlarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME,
					SystemClock.elapsedRealtime() + INITIAL_ALARM_DELAY
					+ JITTER,
					JITTER,
					mLoggerReceiverPendingIntent);

			// Show Toast message
			Toast.makeText(getApplicationContext(), "Alarms Activated",
					Toast.LENGTH_LONG).show();
			//toggle the alarm_toggle to be an odd number, allowing the cancel toggle to take place
			ALARM_TOGGLE++;
		} else {
			// Cancel all alarms using mNotificationReceiverPendingIntent
			mAlarmManager.cancel(mNotificationReceiverPendingIntent);

			// Cancel all alarms using mLoggerReceiverPendingIntent
			mAlarmManager.cancel(mLoggerReceiverPendingIntent);

			// Show Toast message
			Toast.makeText(getApplicationContext(),
					"Alarms Disengaged", Toast.LENGTH_LONG).show();
			//toggle back on alarm 
			ALARM_TOGGLE++;
		}
	}
		



	private void deleteAll() throws IOException {
	
		File dir = getExternalFilesDir("DailyPhotos/Photos");
        if (dir.isDirectory()) {
            String[] children = dir.list();

            for (int i = 0; i < children.length; i++) {
                new File(dir, children[i]).delete();
            }
        }
        mAdapter.removeAllViews();
		
	}
	



	private void dispatchTakePictureIntent() {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// Ensure that there's a camera activity to handle the intent
		if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
			// Create the File where the photo should go
			File photoFile = null;
	        try {
	            photoFile = PhotoBitMapHelper.createImageFile();
	            mPhotoRecordPath = photoFile.getAbsolutePath();
	        } catch (IOException ex) {
	            // Error occurred while creating the File
	        	Toast.makeText(getApplicationContext(), "Error occured while creating the File", Toast.LENGTH_SHORT).show();
	        }
	        // Continue only if the File was successfully created
	        if (photoFile != null) {
	            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
	                    Uri.fromFile(photoFile));
	            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
	        }

		}
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (REQUEST_TAKE_PHOTO == requestCode) {
			if(resultCode == RESULT_CANCELED) {
				new File(mPhotoRecordPath).delete();
			}
			if(resultCode == RESULT_OK){
	    	    	
				PhotoRecord photo = new PhotoRecord();
	    		photo.setName(new File(mPhotoRecordPath).getName());
	    		photo.setPath(mPhotoRecordPath);
	    		
	    		Bitmap largePhoto = PhotoBitMapHelper.getBitmapFromFile(mPhotoRecordPath);
				Bitmap scaledPhoto = PhotoBitMapHelper.getScaledBitmapFromFile(mPhotoRecordPath);
				//Bitmap scaledPhoto = Bitmap.createScaledBitmap(largePhoto, 80, 80, true);

				String thumbpath = PhotoBitMapHelper.getThumbPath(mPhotoRecordPath);
				photo.setThumbPath(thumbpath);
				PhotoBitMapHelper.storeBitmapToFile(scaledPhoto, thumbpath);
			
				largePhoto.recycle();
				scaledPhoto.recycle();
				mPhotoRecordPath = null;
			
				mAdapter.addPhoto(photo);
			}
	    }
	}
	
	
	
	//moved to separate class file (NOT USED)
	@SuppressLint("SimpleDateFormat")
	private File createImageFile() throws IOException {
	    // Create an image file name
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    String imageFileName = "JPEG_" + timeStamp + "_";
	    File storageDir = Environment.getExternalStoragePublicDirectory(
	            Environment.DIRECTORY_PICTURES);
	    File image = File.createTempFile(
	        imageFileName,  /* prefix */
	        ".jpg",         /* suffix */
	        storageDir      /* directory */
	    );

	    // Save a file: path for use with ACTION_VIEW intents
	    mCurrentPhotoPath = "file:" + image.getAbsolutePath();
	    return image;
	}
	


	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		CursorLoader loader = new CursorLoader(this, PhotoContract.PHOTO_URI, null, null,null,null);
		return loader;
	}


	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
		mAdapter.swapCursor(arg1);
		
	}


	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		mAdapter.swapCursor(null);
		
	}
	
	
}
