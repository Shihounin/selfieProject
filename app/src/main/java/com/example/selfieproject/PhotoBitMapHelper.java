package com.example.selfieproject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;


 // A class for handling the bitmap operations


public class PhotoBitMapHelper {
	
	public static final String APP_DIR = "DailyPhotos/Photos";
	public static String mBitmapStoragePath;

	
	
	 // Creates and set the application storage path for the photos
	 
	public static void initStoragePath(Context context) {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			try {
				String root = context.getExternalFilesDir(null)
						.getCanonicalPath();
				if (null != root) {
					File bitmapStorageDir = new File(root, PhotoBitMapHelper.APP_DIR);
					bitmapStorageDir.mkdirs();
					mBitmapStoragePath = bitmapStorageDir.getCanonicalPath();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	
	/**
	 * Return a bitmap from a file
	 */
	public static Bitmap getBitmapFromFile(String filePath) {
		Matrix matrix = new Matrix();
        matrix.postRotate(270);
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap , 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
  		return rotatedBitmap;
	}
	/**
	 * Read and return a scaled bitmap
	 */
	public static Bitmap getScaledBitmapFromFile(String filePath) {
		Matrix matrix = new Matrix();
        matrix.postRotate(360);
        
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
		
        Bitmap scaledBitmap = Bitmap.createBitmap(bitmap , 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
  		return scaledBitmap;
	}

	/**
	 * Saves a bitmap to a file
	 * Source comes from Adam Porter ContentProviderLab
	 * @param bitmap the bitmap to save
	 * @param filePath the full path of the file
	 * @return true if save is successful, false otherwise
	 */
	public static boolean storeBitmapToFile(Bitmap bitmap, String filePath) {

		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {

			try {

				BufferedOutputStream bos = new BufferedOutputStream(
						new FileOutputStream(filePath));
				bitmap.compress(CompressFormat.PNG, 100, bos);
				bos.flush();
				bos.close();
			} catch (FileNotFoundException e) {
				return false;
			} catch (IOException e) {
				return false;
			}
			return true;
		}
		return false;
	}
	
	/**
	 * Creates a path for storing the image thumbnail based on full sized picture
	 * Basically the output is </full/path/to/image/filename>_thumb.jpg
	 * @param imagePath the path to the full sized bitmap (/full/path/to/image/filename.jpg)
	 * @return the full path to the thumb bitmap
	 */
	public static String getThumbPath(String imagePath) {
		File image = new File(imagePath);
		String path = image.getAbsolutePath();
		
		String fileWithoutExt = image.getName().split("\\.")[0];
		String newPath = path+fileWithoutExt+"_thumb.jpg";
		return newPath;
	}
	
	/**
	 * Creates a temporary empty image file in the photo folder
	 * It is used for the camera intent
	 * Source comes from Android Developer site
	 */
	
	@SuppressLint("SimpleDateFormat")
	public static File createImageFile() throws IOException {
	    // Create an image file name
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
	    String imageFileName = "photo_" + timeStamp;
	    
	    File image = new File(mBitmapStoragePath+"/"+imageFileName+".jpg");
	    image.createNewFile();
	        
	    return image;
	}
}