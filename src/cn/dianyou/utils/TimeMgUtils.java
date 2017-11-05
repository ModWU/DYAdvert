package cn.dianyou.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.util.Log;

public class TimeMgUtils {
	
	@SuppressLint("SimpleDateFormat")
	private static final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
	
	public static String getTimeStrByMillis(long millis) {
		return df.format(new Date(millis));
	}
	
	private static long handleTime(String timeOne, String timeTwo) {
		try {
			Date d1 = df.parse(timeOne);  
			Date d2 = df.parse(timeTwo); 
			long diff = d1.getTime() - d2.getTime();
			return diff;
		} catch(Exception e) {
			Log.i("INFO", "TimeUtils ex:" + e.toString());
		}
		return 0;
	}
	
	
	public static long getMillisDistance(String timeOne, String timeTwo) {
		return handleTime(timeOne, timeTwo);
	}
	
	public static long getSecondsDistance(String timeOne, String timeTwo) {
		return handleTime(timeOne, timeTwo) / 1000;
	}
	
	public static long getMinutesDistance(String timeOne, String timeTwo) {
		return handleTime(timeOne, timeTwo) / 1000 / 60;
	}
	
	public static long getHoursDistance(String timeOne, String timeTwo) {
		return handleTime(timeOne, timeTwo) / 1000 / 60 / 60;
	}
	
	public static long getDaysDistance(String timeOne, String timeTwo) {
		return handleTime(timeOne, timeTwo) / 1000 / 60 / 60 / 24;
	}
	
	/**
	 * 判断上午还是下午
	 * @return int - 上午:0,下午:1
	 */
	public static int isAmOrPm() {
		final Calendar mCalendar = Calendar.getInstance();
		mCalendar.setTimeInMillis(System.currentTimeMillis());
		return mCalendar.get(Calendar.AM_PM);
	}

}
