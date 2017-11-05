package cn.dianyou.nets;

import java.util.Map;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

public abstract class DYCallback<T> {
	public void onBefore(Request request, int id) {
		
	}
	
	public void onBefore(Map<String, Request> requestMap, int id) {
		
	}
	
	
	public void onAfter(int id) {
		
	}
	
	public void inProgress(float progress, long total, int id) {
		
	}
	
	public void inProgress(String urlKey, float progress, long total, int id) {
		
	}
	
	public boolean validateReponse(Response response, int id) {
		return response.isSuccessful();
	}
	
	public void onResponseMap(Map<String, T> map) {
		
	}
	
	public void onErrorMap(Map<String, Call> map) {
		
	}
	
	public abstract T parseNetworkResponse(Response response, int id) throws Exception;
	
	public abstract void onError(Call call, Exception e, int id);
	
	public abstract void onResponse(T response, int id);
	
	public static DYCallback<Object> CALLBACK_DEFAULT = new DYCallback<Object>() {

		@Override
		public Object parseNetworkResponse(Response response, int id) throws Exception {
			return null;
		}

		@Override
		public void onError(Call call, Exception e, int id) {
			
		}

		@Override
		public void onResponse(Object response, int id) {
			
		}

		
		
	};
}
