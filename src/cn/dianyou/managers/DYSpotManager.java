package cn.dianyou.managers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView.ScaleType;
import android.widget.Toast;
import cn.dianyou.advert.R;
import cn.dianyou.beans.ImageInfo;
import cn.dianyou.listeners.IOnDownloadListener;
import cn.dianyou.managers.DYAdvertManager.IFindImageListener;
import cn.dianyou.utils.OtherUtils;
import cn.dianyou.views.SpotSpreadView;
import cn.dianyou.views.SpotView;
import cn.dianyou.views.SpotView.AnimationBitmap;
import cn.dianyou.views.SpotView.IOnCloseListener;

public class DYSpotManager extends DYAdvertManager<DYSpotManager> {
	
	private static volatile DYSpotManager dySpotManager;
	
	public static final int NO_ANIMATION_TYPE = 0;
	
	public static final int SIMPLE_ANIMATION_TYPE = 1;
	
	
	private int animationType = SIMPLE_ANIMATION_TYPE;
	
	
	
	DYSpotManager(Context context) {
		super(context);
	}

	static DYSpotManager getInstance(Context context) {
		if(dySpotManager == null) {
			synchronized (DYSpotManager.class) {
				if(dySpotManager == null)
					dySpotManager = new DYSpotManager(context);
				
			}
		}
		return dySpotManager;
	}
	
	public DYSpotManager setAnimationType(int animationType) {
		if(animationType != NO_ANIMATION_TYPE && animationType != SIMPLE_ANIMATION_TYPE) {
			animationType = SIMPLE_ANIMATION_TYPE;
		}
		this.animationType = animationType;
		return this;
	}
	
	public void show(Context context) {
		show(context);
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
				Log.i("INFO", "插屏");
				
				Activity activity = getActivity();
				
				if(activity == null || activity.isFinishing())
					return;
				Log.i("INFO", "插屏..");
				List<Bitmap> bmps = null;
				List<Bitmap> animBmps = null;
				if(bmps == null || bmps.size() <= 0) {
					Log.i("INFO", "network is not enable or it's bitmap is null...");
					return;
				}
				
				Dialog dlg = cacheDialog.get(activity);
				if(dlg != null) {
					final ViewGroup viewP = (ViewGroup) dlg.getWindow().getDecorView();
					
					Object obj = viewP.getTag();
					if(obj instanceof LayoutAnimationController) {
						viewP.setLayoutAnimation((LayoutAnimationController)obj);
					}
					SpotView view = null;
					for(int i = viewP.getChildCount() - 1; i >= 0; i--) {
						View childView = viewP.getChildAt(i);
						if(childView instanceof SpotView) {
							view = (SpotView) childView;
							break;
						}
					}
					
					if(view != null) {
						view.setImageBitmap(bmps.get(0));
						view.newData();
						view.setAnimationBitmap(getAnimationByBitmap(view, animBmps));
						view.showPartAnimation();
						view.showClose(1000);
					}
					
					dlg.show();
					return;
				}
				
				final SpotView view = new SpotView(tempContext);
				
				
				
				view.setOnDownloadListener(new IOnDownloadListener() {
					
					@Override
					public boolean download() {
						Toast.makeText(DYSpotManager.this.context, "點擊一下下載按鈕", Toast.LENGTH_SHORT).show();
						return false;
					}
				});
				
				final Dialog dialog = new Dialog(tempContext) {
					@Override
					public void onBackPressed() {
						if(view.isShowCloseFinished()) {
							startCloseDialogAnimation(this, view);
						}
					}
				};
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialog.setCanceledOnTouchOutside(false);
				
				view.setScaleType(ScaleType.FIT_XY);
				ViewGroup.LayoutParams vlp = new ViewGroup.LayoutParams(dialogW, dialogH);
				view.setLayoutParams(vlp);
				view.setImageBitmap(bmps.get(0));
				Window window = dialog.getWindow();
				final ViewGroup viewP = (ViewGroup) window.getDecorView();
				
				final Animation animation = getAnimationEnter();
				if(animation != null) {
					LayoutAnimationController lac = new LayoutAnimationController(animation);
					lac.setOrder(LayoutAnimationController.ORDER_NORMAL);
					viewP.setLayoutAnimation(lac);
					viewP.setTag(lac);
				}
				
				viewP.setBackgroundResource(0);
				viewP.addView(view);
				
				AnimationBitmap[] abs = getAnimationByBitmap(view, animBmps);
				view.setAnimationBitmap(abs);
				
				boolean isShowPartAnimation = false;
				for(AnimationBitmap ab : abs) {
					if(ab.isAnimation) {
						isShowPartAnimation = true;
						break;
					}
				}
				if(isShowPartAnimation)
					view.showPartAnimation();
				else 
					Log.i("INFO", "not show part animation");
				
				view.showClose(1000);
				
				view.setOnCloseListener(new IOnCloseListener() {
					
					@Override
					public void close(SpotView view) {
						startCloseDialogAnimation(dialog, view);
					}
				}, 100);
				
				dialog.show();
				
				cacheDialog.put(tempContext, dialog);
				
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

	private void startCloseDialogAnimation(final Dialog dialog, final SpotView view) {
		if(dialog == null || view == null)
			return;
		if(!view.isClickEnable()) {
			view.setClickEnable(true);
			Animation animation = getAnimationExit();
			if(animation != null) {
				animation.setAnimationListener(new AnimationListener() {
	
					@Override
					public void onAnimationStart(Animation animation) {
					}
	
					@Override
					public void onAnimationEnd(Animation animation) {
						dialog.dismiss();
						view.setClickEnable(false);
						view.cancelPartAnimation();
						view.clear();
					}
	
					@Override
					public void onAnimationRepeat(Animation animation) {
					}
					
				});
				view.startAnimation(animation);
				
			} else {
				dialog.dismiss();
				view.setClickEnable(false);
			}
		}
	}

	private Animation getAnimationEnter() {
		AnimationSet animationSet = new AnimationSet(true); 
		switch(animationType) {
		
		case NO_ANIMATION_TYPE:
			animationSet = null;
			break;
		
		default:
		case SIMPLE_ANIMATION_TYPE:
			ScaleAnimation acale = new ScaleAnimation(1.0f, 1.0f, 0.0f, 1.0f, dialogW/2, dialogH/2);
			AlphaAnimation alpha=new AlphaAnimation(0.1f,1.0f); 
			animationSet.addAnimation(acale);
			animationSet.addAnimation(alpha);
			animationSet.setDuration(300);
			break;
		}
		
		return animationSet;
	}
	
	private Animation getAnimationExit() {
		AnimationSet animationSet = new AnimationSet(true); 
		switch(animationType) {
		
		case NO_ANIMATION_TYPE:
			animationSet = null;
			break;
		
		default:
		case SIMPLE_ANIMATION_TYPE:
			ScaleAnimation acale = new ScaleAnimation(1.0f, 1.0f, 1.0f, 0.0f, dialogW/2, dialogH/2); 
			animationSet.addAnimation(acale);
			animationSet.setDuration(300);
			break;
		}
		
		return animationSet;
	}

	@Override
	public DYSpotManager loadEarlyAdvertData() {
		return this;
	}

	@Override
	public void detroy() {
		super.detroy();
		dySpotManager = null;
		
	}

	@Override
	protected void findImage(IFindImageListener listener) {
		Map<String, List<Bitmap>> bmpMap = new HashMap<String, List<Bitmap>>();
		List<Bitmap> list1 = Arrays.asList(new Bitmap[]{BitmapFactory.decodeResource(context.getResources(), R.drawable.b)});
		List<Bitmap> list2 = Arrays.asList(new Bitmap[]{BitmapFactory.decodeResource(context.getResources(), R.drawable.a), BitmapFactory.decodeResource(context.getResources(), R.drawable.b)});
		bmpMap.put("a", list1);
		bmpMap.put("b", list2);
		//listener.finished(bmpMap, null);
		// 寻找广告图片
	}

	@Override
	protected void showWhenFail(List<ImageInfo> tempImgList, boolean isCensus, IFindImageListener listener) {
		// TODO Auto-generated method stub
		
	}

}
