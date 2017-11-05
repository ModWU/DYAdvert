package cn.dianyou.nets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import cn.dianyou.utils.DEncodingUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;


public class DYHttpUtils {
	
	public static final long DEFAULT_MILLISECONDS = 10000L;
	private volatile static DYHttpUtils mInstance;
	private OkHttpClient mOkHttpClient;
	private Handler mHandler = null;
	
	
	public DYHttpUtils(OkHttpClient okHttpClient) {
		if(okHttpClient == null) {
			mOkHttpClient = new OkHttpClient();
		} else {
			mOkHttpClient = okHttpClient;
		}
		
		if(mHandler == null)
			mHandler = new Handler(Looper.getMainLooper());
		
	}
	
	public static DYHttpUtils initClient(OkHttpClient okHttpClient) {
		if(mInstance == null) {
			synchronized(DYHttpUtils.class) {
				if(mInstance == null) {
					mInstance = new DYHttpUtils(okHttpClient);
				}
			}
		}
		return mInstance;
	}
	
	
	public static DYHttpUtils getInstance() {
		return initClient(null);
	}
	
	private String charsetName = "UTF-8";
	
	public DYHttpUtils encoding(String charsetName) {
		this.charsetName = charsetName;
		return this;
	}
	
	
	public OkHttpClient getOkHttpClient() {
		return mOkHttpClient;
	}
	
	public DYGetBuilder get() {
		return new DYGetBuilder();
	}
	
	public DYPostStringBuilder postString() {
		return new DYPostStringBuilder();
	}
	
	public DYPostFileBuilder postFile() {
		return new DYPostFileBuilder();
	}
	
	public DYPostFormBuilder post() {
		return new DYPostFormBuilder();
	}
	
	public void execute(final DYRequestCall requestCall, DYCallback<?> callback) {
		if(callback == null) 
			callback = DYCallback.CALLBACK_DEFAULT;
		@SuppressWarnings("unchecked")
		final DYCallback<Object> finalCallback = (DYCallback<Object>) callback;
		final int id = requestCall.getHttpRequest().getId();
		requestCall.getCall().enqueue(new Callback() {
			
			@Override
			public void onResponse(Call call, Response response) throws IOException {
				try {
					if(call.isCanceled()) {
						sendFailResultCallback(call, new IOException("Canceled!"), finalCallback, id);
						return;
					}
					
					if(!finalCallback.validateReponse(response, id)) {
						sendFailResultCallback(call, new IOException("request failed, reponse's code is : " + response.code()), finalCallback, id);
						return;
					}
					
					Object o = finalCallback.parseNetworkResponse(response, id);
					if(o != null)
						sendSuccessResultCallback(o, finalCallback, id);
					else 
						sendFailResultCallback(call, new IOException("parseNetworkResponse obj fail. "), finalCallback, id);
				} catch(Exception e) {
					sendFailResultCallback(call, e, finalCallback, id);
				} finally {
					if(response.body() != null) 
						response.body().close();
				}
			}
			
			@Override
			public void onFailure(Call call, IOException e) {
				sendFailResultCallback(call, e, finalCallback, id);
			}
		});
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
		Set<Entry<K, V>> tempSet = null;
		
		try {
			tempSet = map.entrySet();
		} catch(Exception e) {
			Log.i("sort", "getKeysByValue.............................................................................exception:" + e.toString());
			return listKey;
		}
		Set<Entry<K, V>> entryKey = new HashSet<Entry<K, V>>(tempSet);
		for(Entry<K, V> e : entryKey) {
			if(e.getValue().equals(v)) {
				listKey.add(e.getKey());
			}
		}
		
		return listKey;
	}
	
	
	
	private final class MyCallback implements Callback {
		
		private int id;
		private int totalCount;
		private int failCount;
		private int successCount;
		private Map<String, Object> successObj = Collections.synchronizedMap(new HashMap<String, Object>());
		private Map<String, Call> errorObj = Collections.synchronizedMap(new HashMap<String, Call>());
		private Map<String, String> urlMap = Collections.synchronizedMap(new HashMap<String, String>());
		private  DYCallback<Object> finalCallback;
		
		public MyCallback(int id, int totalCount, Map<String, String> urlMap, DYCallback<Object> finalCallback) {
			this.finalCallback = finalCallback;
			this.totalCount = totalCount;
			this.finalCallback = finalCallback;
			if(urlMap != null)
				this.urlMap.putAll(urlMap);
		}
		

		@Override
		public void onResponse(Call call, Response response) throws IOException {
			String tempUrlKey = call.request().url().toString();
			String urlKey = charsetName == null || charsetName.trim().equals("") ? tempUrlKey : DEncodingUtils.decoding(tempUrlKey, charsetName);
			synchronized (this) {
				List<String> urlKeys = getKeysByValue(urlMap, urlKey);
				urlKey = urlKeys.isEmpty() ? urlKey : urlKeys.get(0);
				Log.i("INFO", "onResponse:" + urlKey);
				urlMap.remove(urlKey);
			}
			
			Log.i("INFO", "............." + Thread.currentThread().getId());
			boolean isFailAdd = false;
			try {
				synchronized (this) {
					if(call.isCanceled()) {
						Log.i("INFO", ".....1........");
						failCount++;
						isFailAdd = true;
						errorObj.put(urlKey, call);
						sendFailResultCallback(call, new IOException("[urlKey:" + urlKey + "] Canceled!"), finalCallback, id);
						return;
					}
					
					if(!finalCallback.validateReponse(response, id)) {
						Log.i("INFO", ".....2........");
						failCount++;
						isFailAdd = true;
						errorObj.put(urlKey, call);
						sendFailResultCallback(call, new IOException("[urlKey:" + urlKey + "] request failed, reponse's code is : " + response.code()), finalCallback, id);
						return;
					}
					Log.i("INFO", ".....3........");
				}
				
				Object o = finalCallback.parseNetworkResponse(response, id);
				
				synchronized (this) {
					if(call.isCanceled()) {
						failCount++;
						isFailAdd = true;
						errorObj.put(urlKey, call);
						sendFailResultCallback(call, new IOException("[urlKey:" + urlKey + "] Canceled!"), finalCallback, id);
						return;
					}
					Log.i("INFO", "urlKey--wuchacaho:" + urlKey);
					if(o != null) {
						sendSuccessResultCallback(o, finalCallback, id);
						successObj.put(urlKey, o);
						successCount++;
					} else {
						failCount++;
						isFailAdd = true;
						errorObj.put(urlKey, call);
						sendFailResultCallback(call, new IOException("[urlKey:" + urlKey + "] parseNetworkResponse obj fail."), finalCallback, id);
					}
				}
				
			} catch(Exception e) {
				synchronized (this) {
					if(!isFailAdd) {
						failCount++;
					}
					errorObj.put(urlKey, call);
					sendFailResultCallback(call, e, finalCallback, id);
				}
			} finally {
				synchronized (this) {
					Log.i("INFO", "failCount:" + failCount);
					Log.i("INFO", "totalCount:" + totalCount);
					Log.i("INFO", "successCount:" + successCount);
					if(failCount < totalCount) {
						if(successCount + failCount >= totalCount)
							sendSuccessResultCallbackMap(call, finalCallback, successObj, id);
					} else {
						sendFailResultCallbackMap(call, finalCallback, errorObj, id);
					}
				}
				if(response.body() != null) 
					response.body().close();
			}
		}
		
		@Override
		public void onFailure(Call call, IOException e) {
			List<String> urlKeys = getKeysByValue(urlMap, call.request().url().toString());
			String urlKey = urlKeys.isEmpty() ? call.request().url().toString() : urlKeys.get(0);
			
			synchronized (this) {
				failCount++;
				e = new IOException("[urlKey:" + urlKey + "] " + e.getMessage());
				sendFailResultCallback(call, e, finalCallback, id);
				errorObj.put(urlKey, call);
				if(failCount < totalCount) {
					if(successCount + failCount >= totalCount)
						sendSuccessResultCallbackMap(call, finalCallback, successObj, id);
				} else {
					sendFailResultCallbackMap(call, finalCallback, errorObj, id);
				}
			}
			
		}
		
	}
	
	public void executeMap(final DYRequestCall requestCall, DYCallback<?> callback) {
		if(callback == null) 
			callback = DYCallback.CALLBACK_DEFAULT;
		//cancelTag(id);
		@SuppressWarnings("unchecked")
		final DYCallback<Object> finalCallback = (DYCallback<Object>) callback;
		final Map<String, Call> callMap = requestCall.getCallMap();
		int id = requestCall.getHttpRequest().getId();
		
		Set<Entry<String, Call>> callSet = callMap.entrySet();
		
		int totalCount = callSet.size();
		
		Map<String, String> urlMap = requestCall.getHttpRequest().getUrlMap();
		
		MyCallback callback_ = new MyCallback(id, totalCount, urlMap, finalCallback);
		
		
		for(Entry<String, Call> e : callSet) {
			Call call = e.getValue();
			call.enqueue(callback_);
		}
		
		
	}
	
	private void sendFailResultCallback(final Call call, final Exception e, final DYCallback<?> callback, final int id) {
		if(callback == null) return;
		mHandler.post(new Runnable() {

			@Override
			public void run() {
				callback.onError(call, e, id);
				callback.onAfter(id);
			}
			
		});
		
	}
	
	private <T> void sendSuccessResultCallback(final Object object, final DYCallback<Object> callback, final int id) {
		if(callback == null) return;
		
		mHandler.post(new Runnable() {

			@Override
			public void run() {
				callback.onResponse(object, id);
				callback.onAfter(id);
			}
			
		});
		
	}
	
	private <T> void sendSuccessResultCallbackMap(final Object object, final DYCallback<Object> callback, final Map<String, Object> map, final int id) {
		if(callback == null) return;
		
		mHandler.post(new Runnable() {

			@Override
			public void run() {
				callback.onResponseMap(map);
			}
			
		});
		
	}
	
	private void sendFailResultCallbackMap(final Call call, final DYCallback<?> callback, final Map<String, Call> map, final int id) {
		if(callback == null) return;
		mHandler.post(new Runnable() {

			@Override
			public void run() {
				callback.onErrorMap(map);
			}
			
		});
		
	}
	
	public void cancelTag(Object tag) {
		for(Call call : mOkHttpClient.dispatcher().queuedCalls()) {
			if(tag.equals(call.request().tag()))
				call.cancel();
		}
		
		for(Call call : mOkHttpClient.dispatcher().runningCalls())
			call.cancel();
	}
	
}
