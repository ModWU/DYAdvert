package cn.dianyou.nets;

import java.util.Map;

public interface DYIHasParamsable {
	public static final int PARAM_JSON_TYPE = 0;
	public static final int PARAM_NOT_JSON_TYPE = 1;
	DYHttpRequestBuilder<? extends DYHttpRequestBuilder<?>> paramJSONType();
	DYHttpRequestBuilder<? extends DYHttpRequestBuilder<?>> paramNotJSONType();
	DYHttpRequestBuilder<? extends DYHttpRequestBuilder<?>> params(Map<String, String> params);
	DYHttpRequestBuilder<? extends DYHttpRequestBuilder<?>> addParams(String key, String value);
	
}
