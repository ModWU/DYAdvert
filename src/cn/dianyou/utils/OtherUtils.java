package cn.dianyou.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;

public class OtherUtils {
	public static Activity getActivity() {
	    Class<?> activityThreadClass = null;
	    try {
	        activityThreadClass = Class.forName("android.app.ActivityThread");
	        Object activityThread = activityThreadClass.getMethod("currentActivityThread").invoke(null);
	        Field activitiesField = activityThreadClass.getDeclaredField("mActivities");
	        activitiesField.setAccessible(true);
	        Map<?, ?> activities = (Map<?, ?>) activitiesField.get(activityThread);
	        for (Object activityRecord : activities.values()) {
	            Class<?> activityRecordClass = activityRecord.getClass();
	            Field pausedField = activityRecordClass.getDeclaredField("paused");
	            pausedField.setAccessible(true);
	            if (!pausedField.getBoolean(activityRecord)) {
	                Field activityField = activityRecordClass.getDeclaredField("activity");
	                activityField.setAccessible(true);
	                Activity activity = (Activity) activityField.get(activityRecord);
	                return activity;
	            }
	        }
	    } catch (ClassNotFoundException e) {
	        e.printStackTrace();
	    } catch (NoSuchMethodException e) {
	        e.printStackTrace();
	    } catch (IllegalAccessException e) {
	        e.printStackTrace();
	    } catch (InvocationTargetException e) {
	        e.printStackTrace();
	    } catch (NoSuchFieldException e) {
	        e.printStackTrace();
	    }
	    return null;
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static void manageLayer(View v, boolean enableHardware)
	{
		if (Build.VERSION.SDK_INT < 11)
			return;
		
		//从api11开始支持硬件加速，充分利用cpu，使得绘制更加平滑，但是会多消耗一定内存
		int layerType = enableHardware ? View.LAYER_TYPE_HARDWARE
				: View.LAYER_TYPE_NONE;
		if (layerType != v.getLayerType())
			v.setLayerType(layerType, null);
	}
	
	public static <T>Map<String, T> sortMapByKey(Map<String, T> oriMap) {  
	    if (oriMap == null || oriMap.isEmpty()) {  
	        return null;  
	    }  
	    TreeMap<String, T> sortedMap = new TreeMap<String, T>(new Comparator<String>() {  
	        public int compare(String key1, String key2) {  
	           return key1.compareTo(key2);
	        }});  
	    sortedMap.putAll(oriMap);  
	    
	    return sortedMap;  
	}  
	
	public static <T> List<T> listMapValues(Map<String, T> map) {
		if(map == null || map.isEmpty())
			return null;
		map = sortMapByKey(map);
		Log.i("INFO", "map key:" + map.keySet());
	   return new ArrayList<T>(map.values());
	}
	
	/**
	 * 不为null
	 * @param map
	 * @param v
	 * @return - List<K> 不为null
	 */
	public static <K,V> List<K> getKeysByValue(Map<K,V> map, V v) {
		List<K> listKey = new ArrayList<K>();
		if(map == null || map.isEmpty()) return listKey;
		
		Set<Entry<K, V>> entryKey = map.entrySet();
		for(Entry<K, V> e : entryKey) {
			if(e.getValue().equals(v)) {
				listKey.add(e.getKey());
			}
		}
		
		return listKey;
	}
	
	public static String getHex_13() {
		StringBuffer result_buf = new StringBuffer();
		final char[] dataSet = {'a', 'b', 'c', 'd', 'e', 'f', '0', '1', '2', '3', '4', '5','6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
		for(int i = 0; i < 13; i++) {
			result_buf.append(dataSet[(int) (Math.random() * dataSet.length)]);
		}
		return result_buf.toString();
	}
	
	
	@TargetApi(Build.VERSION_CODES.DONUT)
	public static void releaseAllWebViewCallback() {
		int currentApiLevel = 1;
		try {
			currentApiLevel = android.os.Build.VERSION.SDK_INT;
		} catch(Exception e) {
		} catch(Error e) {}
		
		
		if (currentApiLevel < 16) {
			try {
				Field field = WebView.class.getDeclaredField("mWebViewCore");
				field = field.getType().getDeclaredField("mBrowserFrame");
				field = field.getType().getDeclaredField("sConfigCallback");
				field.setAccessible(true);
				field.set(null, null);
			} catch (NoSuchFieldException e) {
			} catch (IllegalAccessException e) {
			}
		} else {
			try {
				Field sConfigCallback = Class.forName("android.webkit.BrowserFrame")
						.getDeclaredField("sConfigCallback");
				if (sConfigCallback != null) {
					sConfigCallback.setAccessible(true);
					sConfigCallback.set(null, null);
				}
			} catch (NoSuchFieldException e) {
			} catch (ClassNotFoundException e) {
			} catch (IllegalAccessException e) {
			}
		}
	}

}
