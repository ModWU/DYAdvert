package cn.dianyou.beans;

import java.util.List;

import android.graphics.Bitmap;

public class ImageInfo implements Comparable<ImageInfo> {
	private String id;
	private String title;
	private String url;
	private String urlKey;
	private Bitmap bitmap;
	private int sort;
	private boolean isClickable;
	private String clickUrl;
	private List<PartImageInfo> partImageInfoList;
	
	
	public ImageInfo() {
	}
	
	

	public ImageInfo(String id, String title, String url, String urlKey, int sort, boolean isClickable, String clickUrl,
			List<PartImageInfo> partImageInfoList) {
		this.id = id;
		this.title = title;
		this.url = url;
		this.urlKey = urlKey;
		this.sort = sort;
		this.isClickable = isClickable;
		this.clickUrl = clickUrl;
		this.partImageInfoList = partImageInfoList;
	}
	
	public ImageInfo(ImageInfo otherImageInfo) {
		this.id = otherImageInfo.id;
		this.title = otherImageInfo.title;
		this.url = otherImageInfo.url;
		this.urlKey = otherImageInfo.urlKey;
		this.sort = otherImageInfo.sort;
		this.isClickable = otherImageInfo.isClickable;
		this.clickUrl = otherImageInfo.clickUrl;
		this.partImageInfoList = otherImageInfo.partImageInfoList;
	}
	
	public void set(ImageInfo otherImageInfo) {
		this.id = otherImageInfo.id;
		this.title = otherImageInfo.title;
		this.url = otherImageInfo.url;
		this.urlKey = otherImageInfo.urlKey;
		this.sort = otherImageInfo.sort;
		this.isClickable = otherImageInfo.isClickable;
		this.clickUrl = otherImageInfo.clickUrl;
		this.partImageInfoList = otherImageInfo.partImageInfoList;
	}
	
	public Bitmap getBitmap() {
		return bitmap;
	}


	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}



	public List<PartImageInfo> getPartImageInfoList() {
		return partImageInfoList;
	}

	public void setPartImageInfoList(List<PartImageInfo> partImageInfoList) {
		this.partImageInfoList = partImageInfoList;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public int getSort() {
		return sort;
	}
	public void setSort(int sort) {
		this.sort = sort;
	}
	public boolean isClickable() {
		return isClickable;
	}
	public void setClickable(boolean isClickable) {
		this.isClickable = isClickable;
	}
	public String getClickUrl() {
		return clickUrl;
	}
	public void setClickUrl(String clickUrl) {
		this.clickUrl = clickUrl;
	}



	public String getUrlKey() {
		return urlKey;
	}



	public void setUrlKey(String urlKey) {
		this.urlKey = urlKey;
	}



	@Override
	public String toString() {
		return "ImageInfo [id=" + id + ", title=" + title + ", url=" + url + ", urlKey=" + urlKey + ", sort=" + sort
				+ ", isClickable=" + isClickable + ", clickUrl=" + clickUrl + ", partImageInfoList=" + partImageInfoList
				+ "]";
	}



	@Override
	public int compareTo(ImageInfo another) {
		
		if(sort == another.sort) {
			return 0;
		}
		
		return (sort < another.sort ? -1 : 1);
	}

	
}
