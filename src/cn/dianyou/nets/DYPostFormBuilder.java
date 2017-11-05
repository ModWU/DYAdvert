package cn.dianyou.nets;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class DYPostFormBuilder extends DYHttpRequestBuilder<DYPostFormBuilder> implements DYIHasParamsable {
	
	
	private List<FileInput> files = new ArrayList<FileInput>();
	
	private Map<String, List<FileInput>> fileMap = new HashMap<String, List<FileInput>>();
	
	@Override
	public DYRequestCall build() {
		return new DYPostFormRequest(url, tag, paramType, params, headers, files, id).build();
	}
	

	@Override
	public DYPostFormBuilder params(Map<String, String> params) {
		if(this.params != null && params != null && !params.isEmpty()) {
			Set<Entry<String, String>> paramSet = params.entrySet();
			for(Entry<String, String> p : paramSet) 
				this.params.put(p.getKey(), p.getValue());
			return this;
		}
		this.params = params;
		return this;
	}

	@Override
	public DYPostFormBuilder addParams(String key, String value) {
		if(this.params == null)
			params = new LinkedHashMap<String, String>();
		params.put(key, value);
		return this;
	}
	
	public DYPostFormBuilder paramMap(String urlKey, Map<String, String> params) {
		if(paramMap == null) {
			paramMap = new HashMap<String, Map<String, String>>();
		}
		
		paramMap.put(urlKey, params);
		
		return this;
		
	}
	
	public DYPostFormBuilder addParams(String urlKey, String key, String value) {
		if(value == null)
			value = "";
		
		if(paramMap == null) {
			paramMap = new HashMap<String, Map<String, String>>();
			paramMap.put(urlKey, new HashMap<String, String>());
		} else {
			if(!paramMap.containsKey(urlKey) || paramMap.get(urlKey) == null)
				paramMap.put(urlKey, new HashMap<String, String>());
		}
		
		paramMap.get(urlKey).put(key, value);
		
		return this;
	}
	
	public DYPostFormBuilder files(String key, Map<String, File> files) {
		if(files == null || files.isEmpty()) {
			return this;
		}
		
		for(String filename : files.keySet()) {
			this.files.add(new FileInput(key, filename, files.get(filename)));
		}
		return this;
	}
	
	public DYPostFormBuilder fileMap(String urlKey, String key, Map<String, File> files) {
		if(fileMap == null || files == null || files.isEmpty()) {
			return this;
		}
		
		List<FileInput> listFile = fileMap.get(urlKey);
		if(listFile == null) {
			listFile = new ArrayList<FileInput>();
		}
		for(String filename : files.keySet()) {
			listFile.add(new FileInput(key, filename, files.get(filename)));
		}
		fileMap.put(urlKey, listFile);
		return this;
	}
	
	public DYPostFormBuilder addFile(String name, String filename, File file) {
		if(files == null)
			return this;
		files.add(new FileInput(name, filename, file));
		return this;
	}
	
	public DYPostFormBuilder addFile(String urlKey, String name, String filename, File file) {
		if(fileMap == null)
			return this;
		List<FileInput> listFile = fileMap.get(urlKey);
		if(listFile == null) {
			listFile = new ArrayList<FileInput>();
		}
		listFile.add(new FileInput(name, filename, file));
		fileMap.put(urlKey, listFile);
		return this;
	}
	
	
	public static class FileInput {
		public String key;
		public String filename;
		public File file;
		
		public FileInput(String name, String filename, File file) {
			this.key = name;
			this.filename = filename;
			this.file = file;
		}
		
		@Override
		public String toString() {
			return "FileInput{" +
                    "key='" + key + '\'' +
                    ", filename='" + filename + '\'' +
                    ", file=" + file +
                    '}';
		}
	}


	@Override
	public DYRequestCall buildMap() {
		return new DYPostFormRequest(urlMap, tag, paramType, paramMap, headerMap, fileMap, id).build();
	}


}
