package cn.dianyou.nets;

import java.util.Map;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

public class DYPostStringRequest extends DYPostRequest {
	
	private static MediaType MEDIA_TYPE_PLAIN = MediaType.parse("text/plain;charset=utf-8");
	
	private String content;
	private MediaType mediaType;

	protected DYPostStringRequest(String url, Object tag, Map<String, String> params, Map<String, String> headers, String content, MediaType mediaType, int id) {
		super(url, tag, DYIHasParamsable.PARAM_NOT_JSON_TYPE, params, headers, id);
		this.content = content;
		this.mediaType = mediaType;
		
		if(this.content == null)
			Exceptions.illegalArgument("this content can not be null!");
		if(this.mediaType == null)
			this.mediaType = MEDIA_TYPE_PLAIN;
	}

	@Override
	protected Request buildRequest() {
		return builder.post(requestBody).build();
	}

	@Override
	protected DYPostStringRequest buildReqeustBody() {
		this.requestBody = RequestBody.create(mediaType, content);
		return this;
	}

	@Override
	protected Map<String, Request> buildRequestMap() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected DYPostRequest buildRequestBodyMap() {
		// TODO Auto-generated method stub
		return null;
	}

	

}
