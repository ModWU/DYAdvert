package cn.dianyou.managers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView.ScaleType;
import android.widget.Toast;
import cn.dianyou.advert.R;
import cn.dianyou.beans.ImageInfo;
import cn.dianyou.listeners.IOnDownloadListener;
import cn.dianyou.managers.DYAdvertManager.IFindImageListener;
import cn.dianyou.utils.OtherUtils;
import cn.dianyou.views.SpotSpreadView;
import cn.dianyou.views.SpotView.AnimationBitmap;
import cn.dianyou.views.SpreadView;
import cn.dianyou.views.SpreadView.ISpreadStopListener;

public class DYSpreadManager extends  DYAdvertManager<DYSpreadManager> {
	
	private static volatile DYSpreadManager dySpreadManager;
	
	
	DYSpreadManager(Context context) {
		super(context);
		/*DisplayMetrics outMetriecs = context.getResources().getDisplayMetrics();
		screenH = outMetriecs.heightPixels;
		TypedValue tv = new TypedValue();  
		if (context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {  
			actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, outMetriecs);  
		} */ 
		dialogW = mScreenW;
		dialogH = mScreenH;
		
	}

	static DYSpreadManager getInstance(Context context) {
		if(dySpreadManager == null) {
			synchronized (DYSpreadManager.class) {
				if(dySpreadManager == null)
					dySpreadManager = new DYSpreadManager(context);
				
			}
		}
		return dySpreadManager;
	}
	
	public void show(Activity activity) {
		show(activity, null);
	}
	
	public void show() {
		List<Bitmap> failBmp = null;
		show(failBmp);
	}
	
	public void show(List<Bitmap> failListBmp) {
		Activity activity = null;
		if(context instanceof Activity) {
			activity = (Activity) context;
		} else {
			activity = OtherUtils.getActivity();
		}
		show(activity, failListBmp);
	}
	
	public void show(Activity activity, List<Bitmap> failListBmp) {
		
		final Activity tempActivity = activity;
		final List<Bitmap> tempFailListBmp = failListBmp;
		
		if(tempActivity != null) {
			
			findImage(new IFindImageListener() {
				@Override
				public void finished(List<ImageInfo> imgInfoList, List<Bitmap> failBmps, Map<String, Object> otherData, boolean isSuccess) {
					Activity temppActivity = getActivity();
					
					
					if(temppActivity == null || temppActivity.isFinishing())
						return;
					
					
					Bitmap bitmap = null;
					Bitmap containerImage = null;
					List<Bitmap> animBmps = null;
					
					Window window = tempActivity.getWindow();
					final ViewGroup parent = (ViewGroup) window.getDecorView();
					View contentView = parent.findViewById(android.R.id.content);
					
					SpreadView spreadView = new SpreadView(context);
					spreadView.setTextContainerColor(Color.parseColor("#292421"));
					spreadView.setScaleType(ScaleType.FIT_XY);
					
					if(containerImage != null) 
						spreadView.setTextContainerBitmap(containerImage);
					
					if(bitmap != null) {
						spreadView.setImageBitmap(bitmap);
						spreadView.setOnDownloadListener(new IOnDownloadListener() {
							
							@Override
							public boolean download() {
								Log.i("INFO", "想下载就下载吧...");
								Toast.makeText(context, "下载？", Toast.LENGTH_SHORT).show();
								return false;
							}
						});
						Log.i("INFO", "set bitmap");
					} else {
						Log.i("DY", "Please try again!");
						return;
					}
					
					
					if(contentView != null) {
						ViewGroup parent_1 = (ViewGroup) contentView;
						parent_1.addView(spreadView);
						dialogH = parent_1.getHeight();
						Log.i("INFO", "parent_1 add");
					} else {
						parent.addView(spreadView);
						dialogH = parent.getHeight();
						Log.i("INFO", "parent_2 add");
					}
					
					
					AnimationBitmap[] abs = getAnimationByBitmap(spreadView, animBmps);
					spreadView.setAnimationBitmap(abs);
					
					boolean isShowPartAnimation = false;
					for(AnimationBitmap ab : abs) {
						if(ab.isAnimation) {
							isShowPartAnimation = true;
							break;
						}
					}
					if(isShowPartAnimation)
						spreadView.showPartAnimation();
					
					spreadView.setStopListener(new ISpreadStopListener() {
						
						@Override
						public void onResponse(SpreadView view) {
							ViewGroup parent = (ViewGroup) view.getParent();
							if(parent != null)
								parent.removeView(view);
							view.clear();
						}
					}, 500);
					spreadView.go(1000);
					
					
				}

				@Override
				public List<Bitmap> getFailBitmap() {
					return tempFailListBmp;
				}

				@Override
				public Activity getActivity() {
					Activity temppActivity = tempActivity;
					
					if(temppActivity == null || temppActivity.isFinishing()) 
						temppActivity = OtherUtils.getActivity();
					return temppActivity;
				}
			});
			
		}
	}
	
	//可能需要其他参数
		private AnimationBitmap[] getAnimationByBitmap(SpotSpreadView view, List<Bitmap> bmps) {
			/*AnimationBitmap[] animationBitmaps = {new AnimationBitmap(bitmap, 20, 200, 120, 30, -30, false, false), new AnimationBitmap(bitmap2, 70, 200, 70, 30, -30, true, false),
					new AnimationBitmap(bitmap2, 20, 500, 120, 30, 30, false, false), new AnimationBitmap(bitmap, 70, 500, 70, 30, -30, true, false)*/	
			
			/*new AnimationBitmap(bmps.get(0), 100, 200, 120, 30, -30, new PointF(view.getGap() + 110, view.getGap() + 115), false, false), 
			new AnimationBitmap(bmps.get(1), 150, 200, 70, 30, -30, new PointF(view.getGap() + 110, view.getGap() + 115), true, false), 
			
			new AnimationBitmap(bmps.get(1), 100, 300, 120, 30, -30, new PointF(view.getGap() + 110, view.getGap() + 115), false, false), 
			new AnimationBitmap(bmps.get(0), 150, 300, 70, 30, -30, new PointF(view.getGap() + 110, view.getGap() + 115), true, false), */
			
	  //new AnimationBitmap(bmps.get(1), dialogW/2 - 60 - view.getGap(), dialogH - 120, 120, 30, 0, true, true),
			
			//x轴在中心点的动画
			AnimationBitmap[] animationBitmaps = {
												
												  new AnimationBitmap(bmps.get(1), dialogW/2 - 60 - view.getGap(), dialogH - 240, 120, 30, -45, new PointF(dialogW/2  - view.getGap(), dialogH - 240 + 15), true, true),
												  new AnimationBitmap(bmps.get(1), dialogW/2 - 60 - view.getGap(), dialogH - 240, 120, 30, -135, new PointF(dialogW/2  - view.getGap(), dialogH - 240 + 15), true, true)
												 
												 };
			//因为服务器传来的图片会缩小getGap的2倍，所以这些bitmap的突出动画图片的宽高要减去getGap的距离的2倍
			//动画时 顶点会自动加上getGap这么多的距离，所以直接用顶点left和top来做的话，不需要考虑getGap间隙
			return animationBitmaps;
		}

	@Override
	public DYSpreadManager loadEarlyAdvertData() {
		return this;
	}

	@Override
	public void detroy() {
		dySpreadManager = null;
		super.detroy();
	}

	@Override
	protected void findImage(IFindImageListener listener) {
		Map<String, List<Bitmap>> bmpMap = new HashMap<String, List<Bitmap>>();
		List<Bitmap> listbmp = Arrays.asList(new Bitmap[]{BitmapFactory.decodeResource(context.getResources(), R.drawable.b)});
		bmpMap.put("a", listbmp);
		
		bmpMap.put("b", null);
		
		List<Bitmap> list2 = Arrays.asList(new Bitmap[]{BitmapFactory.decodeResource(context.getResources(), R.drawable.a), BitmapFactory.decodeResource(context.getResources(), R.drawable.b)});
		bmpMap.put("c", list2);
		//listener.finished(bmpMap, null);
	}

	@Override
	protected void showWhenFail(List<ImageInfo> tempImgList, boolean isCensus, IFindImageListener listener) {
		// TODO Auto-generated method stub
		
	}
	
	/*@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private int getActionBarHeight(Activity activity) {  
		  
		 int height = 0;
	     ActionBar ab = activity.getActionBar();
	     if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
	    	 if(ab != null && ab.isShowing()) {
	    		 height = ab.getHeight();
	    	 }
	     } else {
	     }
	     Log.i("INFO", "....h:" + height);
	     return height;  
	}  */
}
