package cn.dianyou.nets;

import java.io.File;

import okhttp3.MediaType;

public class DYPostFileBuilder extends DYHttpRequestBuilder<DYPostFileBuilder> {

	private File file;
	private MediaType mediaType;
	
	public DYPostFileBuilder file(File file) {
		this.file = file;
		return this;
	}
	
	public DYPostFileBuilder mediaType(MediaType mediaType) {
		this.mediaType = mediaType;
		return this;
	}
	
	@Override
	public DYRequestCall build() {
		return new DYPostFileRequest(url, file, params, headers, file, mediaType, id).build();
	}

	@Override
	public DYRequestCall buildMap() {
		return null;
	}

}
