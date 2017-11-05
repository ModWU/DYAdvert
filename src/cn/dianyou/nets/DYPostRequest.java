package cn.dianyou.nets;

import java.util.Map;

import okhttp3.RequestBody;

public abstract class DYPostRequest extends DYRequest {
	
	protected RequestBody requestBody;
	protected Map<String, RequestBody> requestBodyMap;

	protected DYPostRequest(String url, Object tag, int paramType, Map<String, String> params, Map<String, String> headers, int id) {
		super(url, tag, paramType, params, headers, id);
	}
	
	protected DYPostRequest(Map<String, String> urlMap, Object tag, int paramType, Map<String, Map<String, String>> params, Map<String, Map<String, String>> headers, int id) {
		super(urlMap, tag, paramType, params, headers, id);
	}

	protected abstract DYPostRequest buildReqeustBody();
	protected abstract DYPostRequest buildRequestBodyMap();
	
	protected DYPostRequest wrapListenerRequestBody(final DYCallback<?> callback) {
		return this;
	}
	
	protected DYPostRequest wrapListenerRequestBodyMap(final DYCallback<?> callback) {
		return this;
	}

}
