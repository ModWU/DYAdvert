package cn.dianyou.nets;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.net.Uri;

public class DYGetBuilder extends DYHttpRequestBuilder<DYGetBuilder> implements DYIHasParamsable{

	@Override
	public DYGetBuilder params(Map<String, String> params) {
		if(this.params != null && params != null && !params.isEmpty()) {
			Set<Entry<String, String>> paramSet = params.entrySet();
			for(Entry<String, String> p : paramSet) 
				this.params.put(p.getKey(), p.getValue());
			return this;
		}
		this.params = params;
		return this;
	}
	
	public DYGetBuilder paramMap(String urlKey, Map<String, String> params) {
		if(params == null || params.isEmpty()) 
			return this;
		if(paramMap == null) {
			paramMap = new HashMap<String, Map<String, String>>();
		}
		
		paramMap.put(urlKey, params);
		
		return this;
		
	}

	@Override
	public DYGetBuilder addParams(String key, String value) {
		if(this.params == null)
			params = new LinkedHashMap<String, String>();
		params.put(key, value);
		return this;
	}
	
	public DYGetBuilder addParams(String urlKey, String key, String value) {
		if(value == null)
			return this;
		
		if(paramMap == null) {
			paramMap = new HashMap<String, Map<String, String>>();
			paramMap.put(urlKey, new LinkedHashMap<String, String>());
		} else 
			if(!paramMap.containsKey(urlKey) || paramMap.get(urlKey) == null)
				paramMap.put(urlKey, new LinkedHashMap<String, String>());
		
		paramMap.get(urlKey).put(key, value);
		
		return this;
	}

	@Override
	public DYRequestCall build() {
		if(params != null) {
			url = appendParams(url, params);
		}
		return new DYGetRequest(url, tag, DYIHasParamsable.PARAM_NOT_JSON_TYPE, params, headers, id).build();
	}
	
	protected String appendParams(String url, Map<String, String> params) {
		if(url == null || params == null || params.isEmpty()) {
			return url;
		}
		
		Uri.Builder builder = Uri.parse(url).buildUpon();
		Set<String> keys = params.keySet();
		Iterator<String> iterator = keys.iterator();
		while(iterator.hasNext()) {
			String key = iterator.next();
			builder.appendQueryParameter(key, params.get(key));
		}
		return builder.build().toString();
	}
	

	@Override
	public DYRequestCall buildMap() {
		if(urlMap == null || paramMap == null || paramMap.isEmpty()) 
			return new DYGetRequest(urlMap, tag, paramType, paramMap, headerMap, id).build();
		Set<Entry<String, String>> urlEntry = new HashSet<Entry<String, String>>(urlMap.entrySet());
		for(Entry<String, String> e : urlEntry) {
			String urlKey = e.getKey();
			String url = e.getValue();
			if(paramMap.containsKey(urlKey)) {
				Map<String, String> urlParams = paramMap.get(urlKey);
				url = appendParams(url, urlParams);
			}
			urlMap.put(urlKey, url);
		}
		return new DYGetRequest(urlMap, tag, paramType, paramMap, headerMap, id).build();
	}

	@Override
	public DYGetBuilder paramJSONType() {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public DYGetBuilder paramNotJSONType() {
		// TODO Auto-generated method stub
		return this;
	}
	

}
