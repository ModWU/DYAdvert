package cn.dianyou.nets;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DYRequestCall {
	private DYRequest dyRequest;
	private Request request;
	private Map<String, Request> requestMap;
	private Map<String, Call> callMap;
	
	
	private Call call;
	
	private long readTimeOut;
	private long writeTimeOut;
	private long connTimeOut;
	
	private OkHttpClient clone;
	
	public DYRequestCall(DYRequest request) {
		this.dyRequest = request;
	}
	
	public DYRequestCall readTimeOut(long readTimeOut) {
		this.readTimeOut = readTimeOut;
		return this;
	}
	
	public DYRequestCall writeTimeOut(long writeTimeOut) {
		this.writeTimeOut = writeTimeOut;
		return this;
	}
	
	public DYRequestCall connTimeOut(long connTimeOut) {
		this.connTimeOut = connTimeOut;
		return this;
	}
	//callMap
	public Call buildCall(DYCallback<?> callback) {
		request = generateRequest(callback);
		if(readTimeOut > 0 || writeTimeOut > 0 || connTimeOut > 0) {
			readTimeOut = readTimeOut > 0 ? readTimeOut : DYHttpUtils.DEFAULT_MILLISECONDS;
			writeTimeOut = writeTimeOut > 0 ? writeTimeOut : DYHttpUtils.DEFAULT_MILLISECONDS;
			connTimeOut = connTimeOut > 0 ? connTimeOut : DYHttpUtils.DEFAULT_MILLISECONDS;
			clone = DYHttpUtils.getInstance().getOkHttpClient().newBuilder()
					.readTimeout(readTimeOut, TimeUnit.MILLISECONDS)
					.writeTimeout(writeTimeOut, TimeUnit.MILLISECONDS)
					.connectTimeout(connTimeOut, TimeUnit.MILLISECONDS)
					.build();
			call = clone.newCall(request);
		} else {
			call = DYHttpUtils.getInstance().getOkHttpClient().newCall(request);
		}
		return call;
	}
	
	public Map<String, Call> buildCallMap(DYCallback<?> callback) {
		requestMap = generateRequestMap(callback);
		
		if(callMap == null)
			callMap = new LinkedHashMap<String, Call>();
		
		Set<Entry<String, Request>> entrySet = requestMap.entrySet();
		
		if(readTimeOut > 0 || writeTimeOut > 0 || connTimeOut > 0) {
			readTimeOut = readTimeOut > 0 ? readTimeOut : DYHttpUtils.DEFAULT_MILLISECONDS;
			writeTimeOut = writeTimeOut > 0 ? writeTimeOut : DYHttpUtils.DEFAULT_MILLISECONDS;
			connTimeOut = connTimeOut > 0 ? connTimeOut : DYHttpUtils.DEFAULT_MILLISECONDS;
			clone = DYHttpUtils.getInstance().getOkHttpClient().newBuilder()
					.readTimeout(readTimeOut, TimeUnit.MILLISECONDS)
					.writeTimeout(writeTimeOut, TimeUnit.MILLISECONDS)
					.connectTimeout(connTimeOut, TimeUnit.MILLISECONDS)
					.build();
			
			
			for(Entry<String, Request> e : entrySet) {
				callMap.put(e.getKey(), clone.newCall(e.getValue()));
			}
			
		} else {
			
			for(Entry<String, Request> e : entrySet) {
				callMap.put(e.getKey(), DYHttpUtils.getInstance().getOkHttpClient().newCall(e.getValue()));
			}
		}
		return callMap;
	}
	
	private Request generateRequest(DYCallback<?> callback) {
		return dyRequest.generateRequest(callback);
	}
	
	private Map<String, Request> generateRequestMap(DYCallback<?> callback) {
		return dyRequest.generateRequestMap(callback);
	}
	
	public void execute(DYCallback<?> callback) {
		if(dyRequest.isOnlyOneUrl()) {
			buildCall(callback);
		    if(callback != null) {
		    	callback.onBefore(request, dyRequest.getId());
		    }
		    DYHttpUtils.getInstance().execute(this, callback);
		} else {
			buildCallMap(callback);
			if(callback != null)
				callback.onBefore(requestMap, dyRequest.getId());
			//////////////////////
			 DYHttpUtils.getInstance().executeMap(this, callback);
		}
	}
	
	public Call getCall() {
		return call;
	}
	
	public Map<String, Call> getCallMap() {
		return callMap;
	}
	
	public Request getRequest() {
		return request;
	}
	
	public Request getReqeust() {
		return request;
	}
	
	public DYRequest getHttpRequest() {
		return dyRequest;
	}
	
	public Response execute() throws IOException {
		buildCall(null);
		return call.execute();
	}
	
	public void cancel() {
		if(call != null)
			call.cancel();
	}
}
