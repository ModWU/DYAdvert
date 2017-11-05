package cn.dianyou.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import cn.dianyou.beans.ImageInfo;
import cn.dianyou.utils.ContantsUtils;
import cn.dianyou.utils.FileManageUtils;

public abstract class DYAdvertManager<T extends DYAdvertManager<T>> {
	
	//全局上下文，不可以用來構建window或view
	protected Context context;
	
	protected HashMap<Context, Dialog> cacheDialog;
	
	protected int mScreenW, mScreenH;
	
	protected int dialogW, dialogH;
	
	protected float mDensity;
	
	protected static final String ALL_ADS_IMG = "ads_all_data_image";
	
	protected static final int DELETE_LIMIT_COUNT = 30;
	protected static final int DELETE_COUNT = 5; 
	
	protected DYAdvertManager(Context context) {
		this.context = context;
		cacheDialog = new HashMap<Context, Dialog>();
		obtainScreenSize();
		
		if(mScreenW - mScreenH < 0) {
			dialogW = (int) (mScreenW / 8.0f * 7);
			dialogH = (int) (mScreenH / 5.0f * 4);
		} else {
			dialogW = (int) (mScreenW / 5.0f * 4);
			dialogH = (int) (mScreenH / 8.0f * 7);
		}
		
	
	}
	
	
	private void obtainScreenSize() {
		DisplayMetrics outMetrics = context.getResources().getDisplayMetrics();
		mScreenW = outMetrics.widthPixels;
		mScreenH = outMetrics.heightPixels;
		mDensity = outMetrics.density;
	}

	@SuppressWarnings("unchecked")
	public T resetContext(Context context) {
		this.context = context;
		return (T) this;
	}
	
	/**
	 * 此接口为预留接口，暂时不做任何处理
	 * @return 本身
	 */
	protected abstract DYAdvertManager<T> loadEarlyAdvertData();
	protected abstract void findImage(IFindImageListener listener);
	protected abstract void showWhenFail(List<ImageInfo> tempImgList, boolean isCensus, IFindImageListener listener);
	
	protected void detroy() {
		if(cacheDialog != null) {
			cacheDialog.clear();
			cacheDialog = null;
		}
	};
	
	protected void doListFilter(List<ImageInfo> tempImgList, Map<String, Bitmap> map) {
		for(int i = 0; i < tempImgList.size(); i++) {
			String urlKey = tempImgList.get(i).getUrlKey();
			if(!map.containsKey(urlKey) || map.get(urlKey) == null) {
				if(!checkImageExits(tempImgList.get(i).getId(), tempImgList.get(i).getUrl())) {
					tempImgList.remove(i);
					i--;
				} else {
					tempImgList.get(i).setBitmap(obtainImageFromPhone(tempImgList.get(i).getId(), tempImgList.get(i).getUrl()));
				}
			} else {
				tempImgList.get(i).setBitmap(map.get(urlKey));
			}
		}
	}
	
	protected List<Bitmap> getAllBitmap(List<ImageInfo> imgInfoList) {
		if(imgInfoList == null || imgInfoList.isEmpty()) return null;
		List<Bitmap> bmpList = new ArrayList<Bitmap>();
		List<ImageInfo> tempInfoList = new LinkedList<ImageInfo>(imgInfoList);
		
		for(int i = 0; i < tempInfoList.size(); i++) {
			ImageInfo imgInfo = tempInfoList.get(i);
			Bitmap bitmap = imgInfo.getBitmap();
			if(bitmap == null) {
				bitmap = obtainImageFromPhone(imgInfo.getId(), imgInfo.getUrl());
			}
			
			if(bitmap != null) {
				bmpList.add(bitmap);
			} else {
				imgInfoList.remove(imgInfo);
			}
		}
		return bmpList;
	}
	
	protected boolean checkImageExits(String imageName) {
		return FileManageUtils.isExistsFile_b2(context, ContantsUtils.DianYouKey.PRE_ADVERT_IMAGE_DIR, imageName);
	}
	
	protected boolean checkImageExits(String imageId, String imageUri) {
		String imageName = FileManageUtils.getFileName(imageUri) + imageId;
		return FileManageUtils.isExistsFile_b2(context, ContantsUtils.DianYouKey.PRE_ADVERT_IMAGE_DIR, imageName);
	}
	
	protected Bitmap obtainImageFromPhone(String imageName) {
		return FileManageUtils.obtainBitmap(context, ContantsUtils.DianYouKey.PRE_ADVERT_IMAGE_DIR, imageName);
	}
	
	protected Bitmap obtainImageFromPhone(String imageId, String imageUri) {
		String imageName = FileManageUtils.getFileName(imageUri) + imageId;
		return FileManageUtils.obtainBitmap(context, ContantsUtils.DianYouKey.PRE_ADVERT_IMAGE_DIR, imageName);
	}
	
	protected String saveImageToPhone(Bitmap bmp, String imageId, String imageUri) {
		//保存圖片之前先清理圖片
		FileManageUtils.deleteFilesAtDir(context, ContantsUtils.DianYouKey.PRE_ADVERT_IMAGE_DIR, DELETE_LIMIT_COUNT, DELETE_COUNT);
		String imageName = FileManageUtils.getFileName(imageUri) + imageId;
		return FileManageUtils.saveBitmap(context, bmp, ContantsUtils.DianYouKey.PRE_ADVERT_IMAGE_DIR, imageName);
	}
	
	public void saveBitmapToPhone(List<ImageInfo> imageInfo) {
		if(imageInfo == null || imageInfo.isEmpty()) return;
		//保存圖片之前先清理圖片
		FileManageUtils.deleteFilesAtDir(context, ContantsUtils.DianYouKey.PRE_ADVERT_IMAGE_DIR, DELETE_LIMIT_COUNT, DELETE_COUNT);
		for(int i = 0; i < imageInfo.size(); i++) {
			Bitmap bmp = imageInfo.get(i).getBitmap();
			if(bmp != null) {
				saveImageToPhone(bmp, imageInfo.get(i).getId(), imageInfo.get(i).getUrl());
			}
		}
		
	}
	
	public interface IFindImageListener {
		void finished(List<ImageInfo> imgInfoList, List<Bitmap> failBmps, Map<String, Object> otherData, boolean isSuccess);
		List<Bitmap> getFailBitmap();
		Activity getActivity();
	}
	
	
	

}
