package cn.dianyou.nets;

import java.io.UnsupportedEncodingException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.util.Log;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

public class DYPostFormRequest extends DYPostRequest {
	
	private List<DYPostFormBuilder.FileInput> files;
	
	private Map<String, List<DYPostFormBuilder.FileInput>> fileMap;
	
	
	protected DYPostFormRequest(String url, Object tag, int paramType, Map<String, String> params, Map<String, String> headers,
			List<DYPostFormBuilder.FileInput> files,int id) {
		super(url, tag, paramType, params, headers, id);
		this.files = files;
	}
	
	protected DYPostFormRequest(Map<String, String> urlMap, Object tag, int paramType, Map<String, Map<String, String>> params, Map<String, Map<String, String>> headers,  Map<String, List<DYPostFormBuilder.FileInput>> fileMap, int id) {
		super(urlMap, tag, paramType, params, headers, id);
		this.fileMap = fileMap;
	}

	
	
	@Override
	protected DYPostRequest wrapListenerRequestBody(final DYCallback<?> callback) {
		if(callback == null) return this;
		final DYListenerRequestBody listenerRequestBody = new DYListenerRequestBody(requestBody, new DYListenerRequestBody.Listener() {
			
			@Override
			public void onRequestProgress(long bytesWritten, long contentLength) {
				callback.inProgress(bytesWritten * 1.0f / contentLength,contentLength,id);
			}
		});
		
		requestBody = listenerRequestBody;
		
		return this;
	}
	
	@Override
	protected DYPostRequest wrapListenerRequestBodyMap(final DYCallback<?> callback) {
		if(callback == null || requestBodyMap == null) return this;
		
		
		Set<Entry<String, RequestBody>> urlSet = requestBodyMap.entrySet();
		for(Entry<String, RequestBody> e : urlSet) {
			final String urlKey = e.getKey();
			
			Log.i("INFO", "wrapListenerRequestBodyMap.." + urlKey);
			Log.i("INFO", "wrapListenerRequestBodyMap.." + e.getValue());
			final DYListenerRequestBody listenerRequestBody = new DYListenerRequestBody(e.getValue(), new DYListenerRequestBody.Listener() {
				
				@Override
				public void onRequestProgress(long bytesWritten, long contentLength) {
					callback.inProgress(urlKey, bytesWritten * 1.0f / contentLength,contentLength,id);
					Log.i("INFO", "onRequestProgress.." + bytesWritten);
					Log.i("INFO", "contentLength.." + contentLength);
				}
			});
			
			
			requestBodyMap.put(urlKey, listenerRequestBody);
		}
		
		
		return this;
	}

	@Override
	protected DYPostFormRequest buildReqeustBody() {
		if(files == null || files.isEmpty()) {
			if(paramType == DYIHasParamsable.PARAM_JSON_TYPE) {
				 RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), getJSONByMap(params));
				 this.requestBody = requestBody;
			} else if(paramType == DYIHasParamsable.PARAM_NOT_JSON_TYPE) {
				FormBody.Builder builder = new FormBody.Builder();
				addParams(builder);
				FormBody formBody = builder.build();
				this.requestBody = formBody;
			}
			
			return this;
		} else {
			MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
			addParams(builder);
			
			for(int i = 0; i < files.size(); i++) {
				DYPostFormBuilder.FileInput fileInput = files.get(i);
				RequestBody fileBody = RequestBody.create(MediaType.parse(guessMimeType(fileInput.filename)), fileInput.file);
				builder.addFormDataPart(fileInput.key, fileInput.filename, fileBody);
			}
			this.requestBody = builder.build();
			return this;
		}
	}
	
	

	
	
	private void addParams(MultipartBody.Builder builder) {
		if(params != null && !params.isEmpty()) {
			for(String key : params.keySet()) {
				builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + key + "\""), RequestBody.create(null, params.get(key)));
			}
		}
	}
	
	private void addParamMap(MultipartBody.Builder builder, Map<String, String> params) {
		if(builder != null && params != null && !params.isEmpty()) {
			for(String key : params.keySet()) {
				builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + key + "\""), RequestBody.create(null, params.get(key)));
			}
		}
	}
	
	
	private void addParams(FormBody.Builder builder) {
		if(params != null) {
			for(String key : params.keySet()) {
				builder.add(key, params.get(key));
			}
		}
	}
	
	private void addParamMap(FormBody.Builder builder, Map<String, String> params) {
		if(builder != null && params != null) {
			for(String key : params.keySet()) {
				builder.add(key, params.get(key));
			}
		}
	}
	
	private String getJSONByMap(Map<String, String> map) {
		if(map == null) 
			return "{}";
		StringBuffer buffer = new StringBuffer();
		buffer.append("{");
		Set<Entry<String, String>> entrySet = map.entrySet();
		for(Entry<String, String> e : entrySet) {
			buffer.append("\"").append(e.getKey())
			.append("\": \"").append(e.getValue())
			.append("\"").append(",");
			
		}
		buffer.deleteCharAt(buffer.length() - 1);
		buffer.append("}");
		Log.i("INFO", "buffer:" + buffer.toString());
		return buffer.toString();
	}
	
	
	private String guessMimeType(String path) {
		FileNameMap fileNameMap = URLConnection.getFileNameMap();
		String contentTypeFor = null;
		try {
			contentTypeFor = fileNameMap.getContentTypeFor(URLEncoder.encode(path, "UTF-8"));
		} catch(UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		if(contentTypeFor == null) {
			contentTypeFor = "application/octet-stream";
		}
		
		return contentTypeFor;
	}

	@Override
	protected Request buildRequest() {
		return builder.post(requestBody).build();
	}

	@Override
	protected Map<String, Request> buildRequestMap() {
		Map<String, Request> mapRequest = new HashMap<String, Request>();
		Set<Entry<String, Request.Builder>> entrySet = builders.entrySet();
		for(Entry<String, Request.Builder> e : entrySet) {
			String urlKey = e.getKey();
			Request.Builder builder = e.getValue();
			Request request = null;
			if(requestBodyMap == null || requestBodyMap.isEmpty()) {
				request = builder.build();
				Log.i("INFO", "buildRequestMap..null");
			} else {
				if(requestBodyMap.get(urlKey) == null) {
					request = builder.build();
				} else {
					request = builder.post(requestBodyMap.get(urlKey)).build();
				}
				
				Log.i("INFO", "buildRequestMap..has:" + requestBodyMap.get(urlKey));
			}
			mapRequest.put(urlKey, request);
		}

		return mapRequest;
	}

	@Override
	protected DYPostRequest buildRequestBodyMap() {
		requestBodyMap = new HashMap<String, RequestBody>();
		if(fileMap == null || fileMap.isEmpty()) {
			
			if(paramType == DYIHasParamsable.PARAM_JSON_TYPE) {
				 
				 Set<Entry<String, Map<String, String>>> entrySet = (paramMap == null? new HashSet<Entry<String, Map<String, String>>>() : paramMap.entrySet());
				 for(Entry<String, Map<String, String>> e : entrySet) {
					 String urlKey = e.getKey();
					 Map<String, String> param_ = e.getValue();
					 RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), getJSONByMap(param_));
					 requestBodyMap.put(urlKey, requestBody);
				 }
				 
			} else if(paramType == DYIHasParamsable.PARAM_NOT_JSON_TYPE) {
				
				Set<Entry<String, Map<String, String>>> entrySet = (paramMap == null? new HashSet<Entry<String, Map<String, String>>>() : paramMap.entrySet());
				 for(Entry<String, Map<String, String>> e : entrySet) {
					 FormBody.Builder builder = new FormBody.Builder();
					 addParamMap(builder, e.getValue());
					 FormBody formBody = builder.build();
					 requestBodyMap.put(e.getKey(), formBody);
				 }
				
			}
			
			return this;
		} else {
			
			final Map<String, MultipartBody.Builder> builders = new HashMap<String, MultipartBody.Builder>();
			Set<Entry<String, Map<String, String>>> entrySetBuild = (paramMap == null? new HashSet<Entry<String, Map<String, String>>>() : paramMap.entrySet());
			 for(Entry<String, Map<String, String>> e : entrySetBuild) {
				String urlKey = e.getKey();
				Map<String, String> param_ = e.getValue();
				MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
				addParamMap(builder, param_);
				builders.put(urlKey, builder);
			 }
			 
			 Set<Entry<String, List<DYPostFormBuilder.FileInput>>> entrySetFile = (fileMap == null ? new HashSet<Entry<String, List<DYPostFormBuilder.FileInput>>>() : new HashSet<Entry<String, List<DYPostFormBuilder.FileInput>>>(fileMap.entrySet()));
			
			 for(Entry<String, List<DYPostFormBuilder.FileInput>> e : entrySetFile) {
				 String urlKey = e.getKey();
				 MultipartBody.Builder builder = null;
				 if(builders.get(urlKey) == null) {
					 builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
				 } else {
					 builder = builders.get(urlKey);
				 }
				 
				 List<DYPostFormBuilder.FileInput> fileList = e.getValue();
				 if(fileList != null) {
					 for(int i = 0; i < fileList.size(); i++) {
						 DYPostFormBuilder.FileInput fileInput = fileList.get(i);
						 RequestBody fileBody = RequestBody.create(MediaType.parse(guessMimeType(fileInput.filename)), fileInput.file);
						 builder.addFormDataPart(fileInput.key, fileInput.filename, fileBody);
					 }
				 }
				 requestBodyMap.put(urlKey, builder.build());
			 }
			
			return this;
		}
	}



	
}
