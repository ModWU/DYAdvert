package cn.dianyou.utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.os.Build;

public class BitmapUtils {
	
	public static Bitmap resizeImage(String path, int width, int height) {
	
		 BitmapFactory.Options options = new BitmapFactory.Options();  
	     options.inJustDecodeBounds = true;//不加载bitmap到内存中  
	     BitmapFactory.decodeFile(path,options);   
	     int outWidth = options.outWidth;  
	     int outHeight = options.outHeight;  
	     options.inPreferredConfig = Bitmap.Config.ARGB_8888;  
	     options.inSampleSize = 1;  
	       
	     if (outWidth != 0 && outHeight != 0 && width != 0 && height != 0)   
	     {  
	         int sampleSize=(outWidth/width+outHeight/height)/2;  
	         options.inSampleSize = sampleSize;  
	     }  
	   
	     
	     options.inJustDecodeBounds = false;  
	     return BitmapFactory.decodeFile(path, options);  
	}
	
	
	@TargetApi(Build.VERSION_CODES.FROYO)
	public static Bitmap resizeImage(Bitmap bitmap, int width, int height) {
		if(Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO) {
			return Bitmap.createScaledBitmap(bitmap, width, height, true);
		}
		return ThumbnailUtils.extractThumbnail(bitmap, width, height);
	}
	
	public static Bitmap drawableToBitmap(Drawable drawable) {
		if(drawable instanceof BitmapDrawable) {
			return ((BitmapDrawable) drawable).getBitmap();
		}
		Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
		drawable.draw(canvas);
		return bitmap;
	}
}
