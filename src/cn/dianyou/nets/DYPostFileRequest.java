package cn.dianyou.nets;

import java.io.File;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

public class DYPostFileRequest extends DYPostRequest {
	
	private static MediaType MEDIA_TYPE_STREAM = MediaType.parse("application/octet-stream");
	
	private File file;
	private MediaType mediaType;
	
	protected DYPostFileRequest(String url, Object tag, Map<String, String> params, Map<String, String> headers,
			File file, MediaType mediaType, int id) {
		super(url, tag, DYIHasParamsable.PARAM_NOT_JSON_TYPE, params, headers, id);
		this.file = file;
        this.mediaType = mediaType;
		if(this.file == null)
			Exceptions.illegalArgument("the file can not be null !");
		if(this.mediaType == null)
			this.mediaType = MEDIA_TYPE_STREAM;
	}

	@Override
	protected DYPostFileRequest buildReqeustBody() {
		this.requestBody = RequestBody.create(mediaType, file);
		return this;
	}
	
	protected DYPostFileRequest wrapListenerRequestBody(final DYCallback<?> callback) {
		if(callback == null) return this;
		
		final DYListenerRequestBody listenerRequestBody = new DYListenerRequestBody(requestBody, new DYListenerRequestBody.Listener() {
			
			@Override
			public void onRequestProgress(long bytesWritten, long contentLength) {
				callback.inProgress(bytesWritten * 1.0f / contentLength, contentLength, id);
			}
		});
		
		requestBody = listenerRequestBody;
		return this;
	}

	@Override
	protected Request buildRequest() {
		// TODO Auto-generated method stub
		return builder.post(requestBody).build();
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
