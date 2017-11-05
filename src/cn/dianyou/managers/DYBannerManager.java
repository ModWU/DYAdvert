package cn.dianyou.managers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView.ScaleType;
import cn.dianyou.advert.R;
import cn.dianyou.beans.ImageInfo;
import cn.dianyou.managers.DYAdvertManager.IFindImageListener;
import cn.dianyou.utils.OtherUtils;
import cn.dianyou.views.BannerView;

public class DYBannerManager extends DYAdvertManager<DYBannerManager> {
	
	private static volatile DYBannerManager dyBannerManager;
	
	DYBannerManager(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	static DYBannerManager getInstance(Context context) {
		if(dyBannerManager == null) {
			synchronized (DYBannerManager.class) {
				if(dyBannerManager == null)
					dyBannerManager = new DYBannerManager(context);
				
			}
		}
		return dyBannerManager;
	}

	@Override
	public DYBannerManager loadEarlyAdvertData() {
		
		return this;
		
	}
	
	public void show(ViewGroup parent) {
		show(parent, null);
	}
	
	public void show(ViewGroup parent, List<Bitmap> failListBmp) {
		if(parent != null) {
			
			
			final ViewGroup tempParent = parent;
			
			final List<Bitmap> tempFailListBmp = failListBmp;
			
			findImage(new IFindImageListener() {

				@Override
				public void finished(List<ImageInfo> imgInfoList, List<Bitmap> failBmps, Map<String, Object> otherData, boolean isSuccess) {
					Context context = tempParent.getContext();
					Activity activity = getActivity();
					
					if(activity == null || activity.isFinishing())
						return;
					
					/*List<Bitmap> bmps = bmpMap.get("a");
					Bitmap bitmap = bmps != null && bmps.size() > 0? bmps.get(0) : null;
					if(bitmap != null) {
						
						BannerView bannerView = new BannerView(context);
						bannerView.setScaleType(ScaleType.FIT_XY);
						bannerView.setImageBitmap(bitmap);				
						tempParent.addView(bannerView);
						tempParent.setVisibility(View.VISIBLE);
						bannerView.go();
					}*/
				}

				@Override
				public List<Bitmap> getFailBitmap() {
					return tempFailListBmp;
				}

				@Override
				public Activity getActivity() {
					Activity activity = null;
					if(tempParent != null && tempParent.getContext() instanceof Activity)
						activity = (Activity) tempParent.getContext();
					return activity;
				}

				
			});
			
			
		}
	}

	@Override
	public void detroy() {
		dyBannerManager = null;
		super.detroy();
	}

	@Override
	protected void findImage(IFindImageListener listener) {
		Map<String, List<Bitmap>> bmpMap = new HashMap<String, List<Bitmap>>();
		List<Bitmap> listmap = Arrays.asList(new Bitmap[]{BitmapFactory.decodeResource(context.getResources(), R.drawable.c)});
		bmpMap.put("a", listmap);
		//listener.finished(bmpMap, null);
	}

	@Override
	protected void showWhenFail(List<ImageInfo> tempImgList, boolean isCensus, IFindImageListener listener) {
		// TODO Auto-generated method stub
		
	}
	
}
