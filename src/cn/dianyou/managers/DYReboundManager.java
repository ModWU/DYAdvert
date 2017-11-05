package cn.dianyou.managers;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;
import com.nineoldandroids.view.ViewHelper;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView.ScaleType;
import android.widget.Toast;
import cn.dianyou.beans.ImageInfo;
import cn.dianyou.managers.DYAdvertManager.IFindImageListener;
import cn.dianyou.nets.DYBitmapCallback;
import cn.dianyou.nets.DYCallback;
import cn.dianyou.nets.DYFileCallBack;
import cn.dianyou.nets.DYHttpUtils;
import cn.dianyou.utils.OtherUtils;
import cn.dianyou.views.ReboundViewPager;
import cn.dianyou.views.ReboundViewPager.TransitionEffect;
import cn.dianyou.views.RoundCornerImageView;
import cn.dianyou.views.WrapReboundViewPager;
import cn.dianyou.views.WrapReboundViewPager.IOnCloseListener;
import okhttp3.Call;
import okhttp3.Request;

public class DYReboundManager extends DYAdvertManager<DYReboundManager> {
	
	private static volatile DYReboundManager dyReboundManager;
	
	private final SpringSystem mSpringSystem = SpringSystem.create();
	
	
	private String url[] = {"https://raw.githubusercontent.com/yipianfengye/android-adDialog/master/images/testImage1.png", "https://raw.githubusercontent.com/yipianfengye/android-adDialog/master/images/testImage2.png","https://raw.githubusercontent.com/yipianfengye/android-adDialog/master/images/testImage1.png", "https://raw.githubusercontent.com/yipianfengye/android-adDialog/master/images/testImage2.png", "https://raw.githubusercontent.com/yipianfengye/android-adDialog/master/images/testImage1.png", "https://raw.githubusercontent.com/yipianfengye/android-adDialog/master/images/testImage2.png","https://raw.githubusercontent.com/yipianfengye/android-adDialog/master/images/testImage1.png", "https://raw.githubusercontent.com/yipianfengye/android-adDialog/master/images/testImage2.png", "https://raw.githubusercontent.com/yipianfengye/android-adDialog/master/images/testImage2.png", "https://raw.githubusercontent.com/yipianfengye/android-adDialog/master/images/testImage2.png"};

	DYReboundManager(Context context) {
		super(context);
		dialogW *= 7/8.0f;
	}
	
	static DYReboundManager getInstance(Context context) {
		if(dyReboundManager == null) {
			synchronized (DYBannerManager.class) {
				if(dyReboundManager == null)
					dyReboundManager = new DYReboundManager(context);
				
			}
		}
		return dyReboundManager;
	}

	public void show(Context context) {
		show(context, null);
	}
	
	public void show(Context context, List<Bitmap> failListBmp) {
		if(context == null) {
			context = this.context;
		}
		
		final Context tempContext = context;
		final List<Bitmap> tempFailListBmp = failListBmp;
		findImage(new IFindImageListener() {

			@Override
			public void finished(List<ImageInfo> imgInfoList, List<Bitmap> failBmps, Map<String, Object> otherData, boolean isSuccess) {
				/*List<Bitmap> bmps = bmpMap.get(ALL_ADS_IMG);
				List<View> listImgData = getAllAdViews(bmps);
				
				Activity activity = getActivity();
				
				if(activity != null && !activity.isFinishing())
					showDialog(activity, listImgData);*/
			}

			@Override
			public List<Bitmap> getFailBitmap() {
				return tempFailListBmp;
			}

			@Override
			public Activity getActivity() {
				Activity activity = null;
				if(tempContext instanceof Activity) {
					activity = (Activity) tempContext;
				} 
				
				if(activity == null)
					activity = OtherUtils.getActivity();
				return activity;
			}

		});
	}
	
	

	private void showDialog(Context context, List<View> listImgData) {
		final Context tempContext = context;
		
		if(listImgData == null || listImgData.isEmpty()) {
			Log.i("INFO", "network is not enable or it's bitmap is null...");
			return;
		}
		
		final WrapReboundViewPager wrapViewPager = buildWrapView();
		
		final Dialog dialog = new Dialog(context) {
			@Override
			public void onBackPressed() {
				dismiss();
				wrapViewPager.clear();
			}
		};
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setCanceledOnTouchOutside(false);
			
		
			
		wrapViewPager.setOnCloseListener(new IOnCloseListener() {
				
			@Override
			public void close() {
				Toast.makeText(tempContext, "¹Ø±Õ!", Toast.LENGTH_LONG).show();
				dialog.dismiss();
				wrapViewPager.clear();
			}
		});
			
		ReboundViewPager viewPager = wrapViewPager.getReboundViewPager();
		
		
		viewPager.setTransitionEffect(TransitionEffect.Stack);
		
		viewPager.setFadeEnabled(true);
		viewPager.setLeftRightAlpha(false, true);
		viewPager.setIndexDot(true);
			
		viewPager.setPagerViews(listImgData);
			
			
		Window window = dialog.getWindow();
		ViewGroup parentView = (ViewGroup) window.getDecorView();
		parentView.setBackgroundResource(0);
		parentView.addView(wrapViewPager);
			
		addReboundAnimatin(parentView);
			
	
		dialog.show();
		
	}

	private void addReboundAnimatin(View view) {
		
		final View tempView = view;
		if(tempView == null)
			return;
		
		Spring spring = mSpringSystem.createSpring();
		
		spring.setSpringConfig(SpringConfig.fromOrigamiTensionAndFriction(50, 4));
		
		spring.setCurrentValue(1.0f);
		
		spring.addListener(new SimpleSpringListener() {
			@Override
			public void onSpringUpdate(Spring spring) {
				ViewHelper.setTranslationY(tempView, (float)(spring.getCurrentValue() * mScreenH));
				
				//ViewHelper.setRotationX(tempView, (float)(50 * spring.getCurrentValue()));
				//ViewHelper.setRotationY(tempView, (float)(360 * spring.getCurrentValue()));
				//ViewHelper.setScaleX(tempView, (float)(-0.1f * spring.getCurrentValue() + 1.0f));
				//ViewHelper.setScaleY(tempView, (float)(-0.1f * spring.getCurrentValue() + 1.0f));
			}
		});
		
		spring.setEndValue(0);
		
	}

	private List<View> getAllAdViews(List<Bitmap> bmps) {
		
		if(bmps == null || bmps.size() <= 0) {
			return null;
		}
		
		List<View> listViews = new ArrayList<View>();
		for(Bitmap bmp : bmps) {
			RoundCornerImageView imageView = new RoundCornerImageView(context);
			imageView.setRoundDegree(2 * mDensity);
			imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
			imageView.setImageBitmap(bmp);
			imageView.setScaleType(ScaleType.FIT_XY);
			listViews.add(imageView);
		}
		
		return listViews;
	}

	private WrapReboundViewPager buildWrapView() {
		WrapReboundViewPager wrapViewPager = new WrapReboundViewPager(context);
		ViewGroup.LayoutParams vlp_wrap = new ViewGroup.LayoutParams(dialogW, dialogH);
		wrapViewPager.setLayoutParams(vlp_wrap);
		
		ReboundViewPager reboundViewPager = new ReboundViewPager(context);
		ViewGroup.LayoutParams vlp_viewpager = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		reboundViewPager.setLayoutParams(vlp_viewpager);
		
		wrapViewPager.addReboundViewPager(reboundViewPager);
		return wrapViewPager;
	}
	
	@Override
	protected DYReboundManager loadEarlyAdvertData() {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	protected void findImage(IFindImageListener listener) {
		
		final IFindImageListener tempListener = listener;
		
		
		Map<String, String> urlMap = new HashMap<String, String>();
		for(int i = 0; i < url.length; i++) {
			urlMap.put(i + "", url[i]);
		}
		
		
		
		DYHttpUtils.getInstance().post().urlMap(urlMap).buildMap().execute(new DYBitmapCallback() {
			
			@Override
			public void onResponse(Bitmap response, int id) {
				Log.i("INFO", "onResponse..");
				Activity activity = tempListener.getActivity();
				if(activity != null && activity.isFinishing()) {
					DYHttpUtils.getInstance().cancelTag(id);
					Log.i("INFO", "onResponse cancel:" + id);
				} else {
					Log.i("INFO", "onResponse not cancel:" + id);
				}

			}
			
			@Override
			public void onError(Call call, Exception e, int id) {
				Log.i("INFO", "fail..");
				Activity activity = tempListener.getActivity();
				if(activity != null && activity.isFinishing()) {
					DYHttpUtils.getInstance().cancelTag(id);
					Log.i("INFO", "onError cancel:" + id);
				} else {
					Log.i("INFO", "onError not cancel:" + id);
				}

			}
			
			@Override
			public void onResponseMap(Map<String, Bitmap> map) {
				List<Bitmap> listMap = OtherUtils.listMapValues(map); 
				Map<String, List<Bitmap>> bmpMap = new HashMap<String, List<Bitmap>>();
				bmpMap.put(ALL_ADS_IMG, listMap);
				//tempListener.finished(bmpMap, null);
				
				Log.i("INFO", "onResponseMap-->" + map);
				Log.i("INFO", "success");
				
			}
			
			@Override
			public void onErrorMap(Map<String, Call> map) {
				Log.i("INFO", "onErrorMap..");
				List<Bitmap> failListBmp = tempListener.getFailBitmap();
				if(failListBmp != null && !failListBmp.isEmpty()) {
					Map<String, List<Bitmap>> bmpMap = new HashMap<String, List<Bitmap>>();
					bmpMap.put(ALL_ADS_IMG, failListBmp);
					//tempListener.finished(bmpMap, null);
				}
			}
		});
		
		/*List<Bitmap> list = Arrays.asList(new Bitmap[]{BitmapFactory.decodeResource(context.getResources(), R.drawable.a), BitmapFactory.decodeResource(context.getResources(), R.drawable.b), BitmapFactory.decodeResource(context.getResources(), R.drawable.b), BitmapFactory.decodeResource(context.getResources(), R.drawable.a), BitmapFactory.decodeResource(context.getResources(), R.drawable.b)});
		Map<String, List<Bitmap>> bmpMap = new HashMap<String, List<Bitmap>>();
		bmpMap.put(ALL_ADS_IMG, list);
		tempListener.finished(bmpMap);
		Log.i("INFO", "findImage...");*/
	}

	@Override
	public void detroy() {
		mSpringSystem.removeAllListeners();
		dyReboundManager = null;
		super.detroy();
	}

	@Override
	protected void showWhenFail(List<ImageInfo> tempImgList, boolean isCensus, IFindImageListener listener) {
		// TODO Auto-generated method stub
		
	}

}
