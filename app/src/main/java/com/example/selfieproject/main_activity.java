package com.example.selfieproject;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;



public class main_activity extends Activity {
	
	
	public static final String EXTRA_NAME = "name";
	public static final String EXTRA_PATH = "path";
	
	private ImageView mBitmap;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.main);
		
		
		mBitmap = (ImageView)findViewById(R.id.photo_bitmap);
		String photoName = getIntent().getStringExtra(EXTRA_NAME);
		String filePath = getIntent().getStringExtra(EXTRA_PATH);
		setTitle(photoName);
		new LoadBitmapTask(this,mBitmap).execute(filePath);
		setProgressBarIndeterminateVisibility(true);
	}
	
	static class LoadBitmapTask extends AsyncTask<String, String, Bitmap> {

		private ImageView mImageView;
		private Activity mActivity;
		
		public LoadBitmapTask(Activity activity,ImageView imageView) {
			mImageView = imageView;
			mActivity = activity;
		}
		
		@Override
		protected Bitmap doInBackground(String... params) {
			String photoPath = params[0];
			
			return PhotoBitMapHelper.getBitmapFromFile(photoPath);
		}
		
		@Override
		protected void onPostExecute(Bitmap result) {
			mImageView.setImageBitmap(result);
			mActivity.setProgressBarIndeterminateVisibility(false);
			super.onPostExecute(result);
		}
	}
}