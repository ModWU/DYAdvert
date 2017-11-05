package cn.dianyou.nets;

import okhttp3.MediaType;

public class DYPostStringBuilder extends DYHttpRequestBuilder<DYPostStringBuilder> {
	
	private String content;
	private MediaType mediaType;
	
	public DYPostStringBuilder content(String content) {
		this.content = content;
		return this;
	}
	
	public DYPostStringBuilder mediaType(MediaType mediaType) {
		this.mediaType = mediaType;
		return this;
	}

	@Override
	public DYRequestCall build() {
		return new DYPostStringRequest(url, tag, params, headers, content, mediaType, id).build();
	}

	@Override
	public DYRequestCall buildMap() {
		// TODO Auto-generated method stub
		return null;
	}

}
