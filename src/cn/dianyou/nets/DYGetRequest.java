package cn.dianyou.nets;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import okhttp3.Request;
import okhttp3.Request.Builder;

public class DYGetRequest extends DYRequest {

	protected DYGetRequest(String url, Object tag, int paramType, Map<String, String> params, Map<String, String> headers, int id) {
		super(url, tag, paramType, params, headers, id);
	}
	
	protected DYGetRequest(Map<String, String> urlMap, Object tag, int paramType, Map<String, Map<String, String>> params, Map<String, Map<String, String>> headers, int id) {
		super(urlMap, tag, paramType, params, headers, id);
	}

	@Override
	protected Request buildRequest() {
		return builder.get().build();
	}

	@Override
	protected Map<String, Request> buildRequestMap() {
		Map<String, Request> requestMap = new LinkedHashMap<String, Request>();
		
		Set<Entry<String, Builder>> entrySet = builders.entrySet();
		
		for(Entry<String, Builder> e : entrySet) {
			String urlKey = e.getKey();
			Builder builder = e.getValue();
			requestMap.put(urlKey, builder.get().build());
		}
		
		return requestMap;
	}

}
