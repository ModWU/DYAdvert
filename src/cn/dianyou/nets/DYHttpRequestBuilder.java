package cn.dianyou.nets;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class DYHttpRequestBuilder<T extends DYHttpRequestBuilder<T>> {
	
	protected Map<String, String> urlMap;
	protected Map<String, Map<String, String>> headerMap;
	protected Map<String, Map<String, String>> paramMap;
	
	protected String url;
	protected Object tag;
	protected Map<String, String> headers;
	protected Map<String, String> params;
	protected int paramType = DYIHasParamsable.PARAM_NOT_JSON_TYPE;
	protected int id;
	@SuppressWarnings("unchecked")
	public T id(int id) {
		this.id = id;
		return (T) this;
	}
	
	@SuppressWarnings("unchecked")
	public T url(String url) {
		this.url = url;
		return (T) this;
	}
	
	@SuppressWarnings("unchecked")
	public T paramJSONType() {
		this.paramType = DYIHasParamsable.PARAM_JSON_TYPE;
		return (T) this;
	}
	
	@SuppressWarnings("unchecked")
	public T paramNotJSONType() {
		this.paramType = DYIHasParamsable.PARAM_NOT_JSON_TYPE;
		return (T) this;
	}
	
	@SuppressWarnings("unchecked")
	public T urlMap(Map<String, String> urlMap) {
		if(urlMap != null && this.urlMap != null) 
			this.urlMap.putAll(urlMap);
		else
			this.urlMap = urlMap;
		return (T) this;
	}
	
	@SuppressWarnings("unchecked")
	public T url(String key, String url) {
		if(urlMap == null)
			urlMap = new HashMap<String, String>();
		
		urlMap.put(key, url);
		
		return (T) this;
	}
	
	@SuppressWarnings("unchecked")
	public T tag(Object tag) {
		this.tag = tag;
		return (T) this;
	}
	
	@SuppressWarnings("unchecked")
	public T headers(Map<String, String> headers) {
		this.headers = headers;
		return (T) this;
	}
	
	@SuppressWarnings("unchecked")
	public T headerMap(String key, Map<String, String> headers) {
		if(headers == null || headers.isEmpty()) 
			return (T) this;
		
		if(headerMap == null)
			headerMap = new HashMap<String, Map<String, String>>();
		
		headerMap.put(key, headers);
		
		return (T) this;
	}
	
	@SuppressWarnings("unchecked")
	public T addHeader(String urlKey, String key, String value) {
		if(value == null) {
			return (T) this;
		}
		if(headerMap == null) {
			headerMap = new HashMap<String, Map<String, String>>();
			headerMap.put(urlKey, new HashMap<String, String>());
		} else
			if(!headerMap.containsKey(urlKey) || headerMap.get(key) != null)
				headerMap.put(urlKey, new HashMap<String, String>());
		
		headerMap.get(key).put(key, value);
		return (T) this;
	}
	
	
	@SuppressWarnings("unchecked")
	public T addHeader(String key, String value) {
		if(this.headers == null) {
			headers = new LinkedHashMap<String, String>();
		}
		headers.put(key, value);
		return (T) this;
	}
	
	public abstract DYRequestCall build();
	public abstract DYRequestCall buildMap();
}
