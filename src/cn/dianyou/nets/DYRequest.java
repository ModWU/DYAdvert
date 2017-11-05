package cn.dianyou.nets;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import okhttp3.Headers;
import okhttp3.Request;

public abstract class DYRequest {
	protected String url;
	protected Object tag;
	protected Map<String, String> params;
	protected Map<String, String> headers;
	protected Map<String, Request.Builder> builders;
	
	
	protected Map<String, String> urlMap;
	protected Map<String, Map<String, String>> paramMap;
	protected Map<String, Map<String, String>> headerMap;
	
	protected int paramType = DYIHasParamsable.PARAM_NOT_JSON_TYPE;
	
	protected int id;
	
	protected Request.Builder builder = new Request.Builder();
	
	public boolean isOnlyOneUrl() {
		if(urlMap != null && builders != null) {
			return false;
		}
		
		return true;
	}
	
	public Map<String, String> getUrlMap() {
		return urlMap;
	}
	
	protected DYRequest(String url, Object tag, int paramType, Map<String, String> params, Map<String, String> headers, int id) {
		this.url = url;
		this.tag = tag;
		this.paramType = paramType;
		this.params = params;
		this.headers = headers;
		this.id = id;
		if(url == null)
			Exceptions.illegalArgument("Url can not be null.");
		initBuilder();
	}
	
	protected DYRequest(Map<String, String> urlMap, Object tag, int paramType, Map<String, Map<String, String>> params, Map<String, Map<String, String>> headers, int id) {
		this.urlMap = urlMap;
		this.tag = tag;
		this.paramType = paramType;
		this.paramMap = params;
		this.headerMap = headers;
		this.id = id;
		if(urlMap == null || urlMap.isEmpty()) {
			Exceptions.illegalArgument("Url can not be null.");
		}
		initBuilderMap();
	}
	
	protected abstract Request buildRequest();
	protected abstract Map<String, Request> buildRequestMap();
	
	public DYRequestCall build() {
		return new DYRequestCall(this);
	}
	
	public Request generateRequest(DYCallback<?> callback) {
		if(this instanceof DYGetRequest) {
			return buildRequest();
		} else if(this instanceof DYPostRequest) {
			DYPostRequest dyPostRequest = (DYPostRequest) this;
			return dyPostRequest.buildReqeustBody().wrapListenerRequestBody(callback).buildRequest();
		}
		
		return null;
		
	}
	
	public Map<String, Request> generateRequestMap(DYCallback<?> callback) {
		if(this instanceof DYGetRequest) {
			return buildRequestMap();
		} else if(this instanceof DYPostRequest) {
			DYPostRequest dyPostRequest = (DYPostRequest) this;
			return dyPostRequest.buildRequestBodyMap().wrapListenerRequestBodyMap(callback).buildRequestMap();
		}
		return null;
	}
	
	private void initBuilder() {
		builder.url(url).tag(tag);
		appendHeaders();
	}
	
	private void initBuilderMap() {
		
		if(urlMap != null && !urlMap.isEmpty()) {
			builders = new HashMap<String, Request.Builder>();
			Set<Entry<String, String>> urlEntry = urlMap.entrySet();
			for(Entry<String, String> e : urlEntry) {
				Request.Builder b = new Request.Builder();
				b.url(e.getValue()).tag(tag);
				builders.put(e.getKey(), b);
			}
			
			appendHeaderMap();
		}
	}
	
	protected void appendHeaderMap() {
		if(headerMap == null || headerMap.isEmpty()) return;
		
		for(Entry<String, Request.Builder> e : builders.entrySet()) {
			String urlKey = e.getKey();
			Request.Builder builder = e.getValue();
			if(headerMap.containsKey(urlKey) && headerMap.get(urlKey) != null) {
				Headers.Builder headerBuilder = new Headers.Builder();
				for(String key : headerMap.get(urlKey).values()) {
					headerBuilder.add(key, headerMap.get(urlKey).get(key));
				}
				builder.headers(headerBuilder.build());
			}
		}
		
		
	}

	protected void appendHeaders() {
		Headers.Builder headerBuilder = new Headers.Builder();
		if(headers == null || headers.isEmpty()) return;
		
		for(String key : headers.keySet()) {
			headerBuilder.add(key, headers.get(key));
		}
		builder.headers(headerBuilder.build());
	}
	
	public int getId() {
		return id;
	}
}
