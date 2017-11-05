package cn.dianyou.advert.adEntry;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.Toast;
import cn.dianyou.advert.adAbstarct.AbsSDKAdvert;
import cn.dianyou.advert.adAbstarct.IAdvertModel;
import cn.dianyou.advert.adConfig.YouMiConfig;
import cn.dianyou.advert.adEntry.YouMiAdvert.SpotAds.SpotStateListener;
import cn.dianyou.advert.adIntegral.IIntegral;
import cn.dianyou.advert.adIntegral.YouMiIntegral;
import cn.dianyou.advert.adIntegral.YouMiIntegral.YouMiIntegralClient;
import cn.dianyou.advert.adIntegral.YouMiIntegral.YouMiIntegralServer;
import net.youmi.android.AdManager;
import net.youmi.android.banner.AdSize;
import net.youmi.android.banner.AdView;
import net.youmi.android.banner.AdViewListener;
import net.youmi.android.listener.Interface_ActivityListener;
import net.youmi.android.offers.OffersBrowserConfig;
import net.youmi.android.offers.OffersManager;
import net.youmi.android.offers.OffersWallDialogListener;
import net.youmi.android.spot.CustomerSpotView;
import net.youmi.android.spot.SplashView;
import net.youmi.android.spot.SpotDialogListener;
import net.youmi.android.spot.SpotManager;

/**
 * 有米sdk版本6.1.0 不支持2.3以下版本 minSdkVersion提高至9 不支持平板
 * @author Mod_J
 *
 */

public final class YouMiAdvert extends AbsSDKAdvert<YouMiConfig> {
	
	private AdManager adManager;
	
	/**
	 * 插屏廣告
	 */
	private SpotAds spotAds;
	
	/**
	 * 廣告條
	 */
	private BannerAds bannerAds;
	
	/**
	 * 开屏广告
	 */
	private SpreadAds spreadAds;
	
	/**
	 * 積分墻
	 */
	private IntegralWallAds integralWallAds;
	
	private YouMiAdvert(Context context) {
		super(context);
		adManager = AdManager.getInstance(context);
	}
	
	private YouMiAdvert(Context context, YouMiConfig youMiConfig, boolean isTestMode) {
		super(context, youMiConfig);
		this.isTestMode = isTestMode;
		adManager = AdManager.getInstance(context);
	}


	@Override
	public YouMiAdvert init() {
		if(config != null) {
			init(config.getAppId(), config.getAppSecret(), isTestMode);
		}
		return this;
	}
	
	public void init(String appId, String appSecret) {
		init(appId, appSecret, isTestMode);
	}
	
	public void init(YouMiConfig config) {
		if(config != null)
			init(config.getAppId(), config.getAppSecret(), isTestMode);
	}
	
	public void init(String appId, String appSecret, boolean isTestMode) {
		if(appId != null && appSecret != null) {
			config = YouMiConfig.Builder.create().setAppId(appId).setAppSecret(appSecret).build();
			adManager.init(appId, appSecret, isTestMode);
			isInitFinished = true;
		}
	}
	
	SpotAds getSpotAds() {
		return spotAds;
	}
	
	BannerAds getBannerAds() {
		return bannerAds;
	}
	
	SpreadAds getSpreadAds() {
		return spreadAds;
	}
	

	@Override
	public void show() {
		if(spotAds != null)
			spotAds.show();
		
		if(bannerAds != null)
			bannerAds.show();
		
		if(spreadAds != null) 
			spreadAds.show();
		
		if(integralWallAds != null)
			integralWallAds.show();
	}
	
	/**
	 * 創建插屏廣告
	 * @return SpotAds
	 */
	public SpotAds createSpotAds() {
		spotAds = new SpotAds(this);
		isDestroy = false;
		return spotAds;
	}
	
	/**
	 * 創建廣告條
	 */
	public BannerAds createBannerAds() {
		bannerAds =  createBannerAds(null);
		isDestroy = false;
		return bannerAds;
	}
	
	/**
	 * 创建开屏广告
	 */
	public SpreadAds createSpreadAds(Class<? extends Activity> targetActivity) {
		spreadAds = new SpreadAds(this, targetActivity);
		isDestroy = false;
		return spreadAds;
	}
	
	public BannerAds createBannerAds(ViewGroup parentView) {
		bannerAds = new BannerAds(this, parentView);
		isDestroy = false;
		return bannerAds;
	}
	
	public IntegralWallAds createIntegralWallAds() {
		integralWallAds = new IntegralWallAds(this);
		isDestroy = false;
		return integralWallAds;
	}
	
	/**
	 * 銷毀
	 */
	@Override
	public void destroy() {
		super.destroy();
		if(!isDestroy) {
			if(spotAds != null) {
				spotAds.destroy();
				spotAds = null;
			}
			
			if(bannerAds != null) {
				bannerAds.destroy();
				bannerAds = null;
			}
			
			if(spreadAds != null) {
				spreadAds.destroy();
				spreadAds = null;
			}
			
			if(integralWallAds != null) {
				integralWallAds.destroy();
				integralWallAds = null;
			}
			adManager = null;
			isDestroy = true;
		}
	}
	
	@Override
	public void recovery() {
		if(spotAds != null)
			spotAds.recovery();
		if(bannerAds != null)
			bannerAds.recovery();
		if(spreadAds != null)
			spreadAds.recovery();
		if(integralWallAds != null) 
			integralWallAds.recovery();
		
	}
	
	/**
	 * <ul>
	 * 		<li><a href="http://www.baidu.com" title="百度一下">SpreadAds</a> - 开屏</li>
	 * 		<li>SpotAds - 插屏</li>
	 * </ul>
	 * <strong>{@linkplain SpreadAds <font style="color:red;">开屏</font>}</strong>和<strong>{@linkplain SpotAds <font style="color:#7c7c7c;">插屏</font>}</strong>的<code>共同接口</code>
	 * @author Mod_J
	 */
	public static abstract class SpreadSpotAds<T extends SpreadSpotAds<T>> implements IAdvertModel {
		protected boolean isDestroy = false;
		protected final YouMiAdvert youMiAdvert;
		protected boolean isEarlyLoading = false;
		protected static boolean isShowedSpreadSpotAds = false;
		protected static SpotStateListener spotStateListener;
		/**
		 * 插屏廣告的方向
		 */
		public static final int SPOT_ORIENTATION_PORTRAIT = SpotManager.ORIENTATION_PORTRAIT;
		public static final int SPOT_ORIENTATION_LANDSCAPE = SpotManager.ORIENTATION_PORTRAIT;
		
		/**
		 * 插屏廣告動畫類型
		 */
		public static final int SPOT_ANIM_NONE = SpotManager.ANIM_NONE;
		public static final int SPOT_ANIM_SIMPLE = SpotManager.ANIM_SIMPLE;
		public static final int SPOT_ANIM_ADVANCE = SpotManager.ANIM_ADVANCE;
		protected SpreadSpotAds(YouMiAdvert youMiAdvert) {
			this.youMiAdvert = youMiAdvert;
		}
		
		/**
		 * 無積分廣告調用 插屏廣告 1
		 * 預加載开屏插屏廣告數據，必須在init之後調用
		 */
		@SuppressWarnings("unchecked")
		public T EarlyLoadingSpotAdsData() {
			if(!youMiAdvert.isInitFinished)
				youMiAdvert.init();
			if(youMiAdvert.isInitFinished && !isEarlyLoading) {
				SpotManager.getInstance(youMiAdvert.context).loadSpotAds();
				isEarlyLoading = true;	
			}
			return (T) this;
		}
		
		
		@SuppressWarnings("unchecked")
		@Override
		public T init() {
			return EarlyLoadingSpotAdsData();
		}
		
		/**
		 * 無積分廣告調用 开屏插屏廣告 2
		 * 設置插屏橫屏展示與展示動畫設置
		 * 如果横屏广告资源还没齐全。设置成横屏模式可能会依然使用竖屏广告，在广告之后补充后，会逐渐变成横屏广告数据。
		 * @param orientation 橫豎屏方向
		 */
		@SuppressWarnings("unchecked")
		public T setSpotOrientation(int orientation) {
			SpotManager.getInstance(youMiAdvert.context).setSpotOrientation(orientation);
			return (T) this;
		}
		/**
		 * 無積分廣告調用 开屏插屏廣告 3
		 * 設置开屏插屏橫屏展示與展示動畫設置
		 * @param animationType 動畫效果類型 默認值是ANIM_ADVANCE 4.0及其以上
		 */
		@SuppressWarnings("unchecked")
		public T setSpotAnimationType(int animationType) {
			SpotManager.getInstance(youMiAdvert.context).setAnimationType(animationType);
			return (T) this;
		}
		
		public T setSpotAdsStyle(int orientation, int animationType) {
			return setSpotOrientation(orientation).setSpotAnimationType(animationType);
		}
		
		protected void recoverySimpleSpreadSpotAds() {
			if(isShowedSpreadSpotAds) {
				if(spotStateListener != null) {
					show(spotStateListener);
				} else {
					show();
				}
			}
		}
		
		protected abstract void show(SpotStateListener spotStateListener);
		
		protected SpotDialogListener readyShow(SpotStateListener spotStateListener) {
			final SpotStateListener tempSpotStateListener = spotStateListener;
			if(tempSpotStateListener != null)
				SpotAds.spotStateListener = tempSpotStateListener;
			else
				SpotAds.spotStateListener = null;
			
			return new SpotDialogListener() {
				
					@Override
					public void onSpotClosed() {
						Log.i("INFO", "關閉");
						isShowedSpreadSpotAds = false;
						if(tempSpotStateListener != null) 
							tempSpotStateListener.onSpotClosed();
					}
				
					@Override
					public void onSpotClick(boolean arg0) {
						Log.i("INFO", "點擊：" + arg0);
						if(tempSpotStateListener != null)
							tempSpotStateListener.onSpotClick(arg0);
					}
				
					@Override
					public void onShowSuccess() {
						Log.i("INFO", "展示");
						isShowedSpreadSpotAds = true;
						if(tempSpotStateListener != null)
							tempSpotStateListener.onShowSuccess();
					}
				
					@Override
					public void onShowFailed() {
						Log.i("INFO", "展示失敗");
						if(tempSpotStateListener != null)
							tempSpotStateListener.onShowFailed();
					}
				};
		}
	}
	
	
	/**
	 * 1</p>
	 * 此類為<font style="color:red">插屏廣告</font> 半屏  无积分
	 * @author Mod_J-2016/8
	 *@see  IntegralWallAds
	 *有积分 - IntegralWallAds
	 */
	public static final class SpotAds extends SpreadSpotAds<SpotAds>  {
		
		private static HashMap<CustomerSpotView, ViewGroup> allShowView;
		private static Toast toast;
		
		private static class HolderParams {
			HolderParams(SpotStateListener spotStateListener, ViewGroup.LayoutParams parmas) {
				this.spotStateListener = spotStateListener;
				this.parmas = parmas;
			}
			SpotStateListener spotStateListener;
			ViewGroup.LayoutParams parmas;
		}
		
		/**
		 * onShowSuccess: 展示成功<br>
		 * onShowFailed: 展示失败<br>
		 * onSpotClick: 插屏被点击  - 參數(isWebPath:是否是网页广告)<br>
		 * onSpotClosed: 插屏被关闭
		 */
		public interface SpotStateListener extends SpotDialogListener {
			void ObtainSpotView(CustomerSpotView spotView);
		}
		
		private SpotAds(YouMiAdvert youMiAdvert) {
			super(youMiAdvert);
		}
		
		private void tryCreateMap() {
			if(allShowView == null)
				allShowView = new HashMap<CustomerSpotView, ViewGroup>();
		}
		
		
		
		/**
		 * @param spotStateListener
		 * @see SpotStateListener
		 * 点击查看spotStateListener的回调方法描述
		 */
		private void showSpotAds(SpotStateListener spotStateListener) {
			final SpotDialogListener spotDialogListener = readyShow(spotStateListener);
			SpotManager.getInstance(youMiAdvert.context).showSpotAds(youMiAdvert.context, spotDialogListener);
		}
		
		/**
		 * 如果有需要，可以点击后退关闭插播广告
		 * @return boolean - 判斷對話框形式的插屏廣告是否还在屏幕上
		 */
		public boolean disMissSpotAds() {
			return SpotManager.getInstance(youMiAdvert.context).disMiss();
		}
		
		/**
		 * 可能过时 -- 格式<strong>[@deprecated deprecated-text]</strong>
		 * <hr>
		 * 可以查看 {@link #showToViewGroup(ViewGroup)}
		 * @param viewGroup - 省略...
		 * @param param - 省略...
		 */
		public void showToViewGroup(ViewGroup viewGroup, ViewGroup.LayoutParams param) {
			showToViewGroup(viewGroup, param, null);
		}
		
		/**
		 * 展示在父视图上
		 * @param viewGroup - 父视图
		 */
		public void showToViewGroup(ViewGroup viewGroup) {
			showToViewGroup(viewGroup, null);
		}
		
		/**
		 * -- 展示插屏廣告 以靜態view添加的形式 --
		 * @param viewGroup - 父视图
		 * @param param - 布局参数
		 * @param spotStateListener - {@linkplain SpotStateListener <font style="color:#a800a8">监听器</font>}
		 * @return 无任何返回值
		 */
		public void showToViewGroup(ViewGroup viewGroup, ViewGroup.LayoutParams param, SpotStateListener spotStateListener) {
			tryCreateMap();
			if(viewGroup == null)
				return;
			
			final SpotStateListener tempSpotStateListener = spotStateListener;
			final ViewGroup tempViewGroup = viewGroup;
			final ViewGroup.LayoutParams tempParam = param;
			tempViewGroup.setTag(new HolderParams(tempSpotStateListener, tempParam));
			
			new Thread(new Runnable() {

				@Override
				public void run() {
					
					final CustomerSpotView customerSpotView = SpotManager.getInstance(youMiAdvert.context).cacheCustomerSpot(youMiAdvert.context, tempSpotStateListener);
					
					if(tempSpotStateListener != null)
						tempSpotStateListener.ObtainSpotView(customerSpotView);
					
					if(customerSpotView != null) {
						new Handler(Looper.getMainLooper()).post(new Runnable() {

							@Override
							public void run() {
								if(tempParam == null)
									tempViewGroup.addView(customerSpotView);
								else 
									tempViewGroup.addView(customerSpotView, tempParam);
								allShowView.put(customerSpotView, tempViewGroup);
							}
							
						});
					} else {
						new Handler(Looper.getMainLooper()).post(new Runnable() {

							@Override
							public void run() {
								if(toast == null)
									toast = Toast.makeText(youMiAdvert.context, "Please try again later.", Toast.LENGTH_SHORT);
								toast.show();
							}
							
						});
					}
						
				}
				
			}).start();
			
		}
		
		
		@Override
		public void show() {
			showSpotAds(null);
		}
		
		
		public void show(SpotStateListener spotStateListener) {
			showSpotAds(spotStateListener);
		}

		/**
		 * 銷毀插屏
		 */
		@Override
		public void destroy() {
			if(!isDestroy) {
				 if(allShowView != null) {
					 Set<Entry<CustomerSpotView, ViewGroup>> entrySet = allShowView.entrySet();
					 for(Entry<CustomerSpotView, ViewGroup> entry : entrySet) 
						 entry.getValue().removeView(entry.getKey());
					 allShowView.clear();
					 allShowView = null;
				 }
				 if(youMiAdvert.getSpreadAds() != null) {
					if(youMiAdvert.getSpreadAds().isDestroy) 
						SpotManager.getInstance(youMiAdvert.context).onDestroy();
				 } else {
					 SpotManager.getInstance(youMiAdvert.context).onDestroy();
				 }
				isShowedSpreadSpotAds = false;
				isDestroy = true;
				toast = null;
				spotStateListener = null;
				
			}
		}

		@Override
		public void recovery() {
			recovery(new ViewGroup[]{});
		}
		
		
		
		public void recovery(ViewGroup...viewGroups) {
			tryCreateMap();
			
			recoverySimpleSpreadSpotAds();
			
			 Set<Entry<CustomerSpotView, ViewGroup>> entrySet = new HashSet<Entry<CustomerSpotView, ViewGroup>>(allShowView.entrySet());
			 for(Entry<CustomerSpotView, ViewGroup> entry : entrySet) {
				 final CustomerSpotView customerSpotView = entry.getKey();
				 if(customerSpotView != null) {
					final ViewGroup oldParent = (ViewGroup) customerSpotView.getParent();
					if(oldParent != null)
						oldParent.removeView(customerSpotView);
					
					allShowView.remove(customerSpotView);
					
				 }
				 
			 }
			 
			 Iterator<Entry<CustomerSpotView, ViewGroup>> iterator = entrySet.iterator();
			 
			 if(viewGroups != null) {
				 for(int i = 0; i < viewGroups.length; i++) {
					 
					 if(entrySet.size() != viewGroups.length) {
						 //showToViewGroup(viewGroups[0], null, null);
					 } else {
						 final Entry<CustomerSpotView, ViewGroup> entry = iterator.next();
						 final ViewGroup oldViewGroup = entry.getValue();
						 final HolderParams holderParams = (HolderParams) oldViewGroup.getTag();
						 if(oldViewGroup != null && holderParams != null) {
							 showToViewGroup(viewGroups[i], holderParams.parmas, holderParams.spotStateListener);
							 Log.i("INFO", "i:" + i);
						 } else {
							 showToViewGroup(viewGroups[i], null, null);
						 }
					 }
				 }
			 }
				
		}

		
		
	}
	
	/**
	 * <b>2、</b> <br>
	 * <code>{@literal}</code> 
	 * You can <a href="{@docRoot}/cn/dianyou/advert/adEntry/YouMiAdvert.java">进入当前java文件</a>!<br>
	 * &lt;&lt;此類為廣告條 无积分 - <strong>see</strong>{@link IntegralWallAds 有积分类}&gt;&gt;
	 * @version 无版本限制 - {@code 建议版本jdk1.7}
	 * @author Mod_J-2016/8
	 */
	public static final class BannerAds implements IAdvertModel {
		
		/**
		 * 广告尺寸适配屏幕
		 */
		public static final int BANNER_FIT_SCREEN = 0;
		
		/**
		 * 尺寸320*50
		 */
		public static final int BANNER_SIZE_320x50 = 1;
		

		/**
		 * 尺寸300*250
		 */
		public static final int BANNER_SIZE_300x250 = 2;
		
		/**
		 * 尺寸468*60
		 */
		public static final int BANNER_SIZE_468x60 = 3;
		
		/**
		 * 尺寸728*90
		 */
		public static final int BANNER_SIZE_728x90  = 4;
		
		public static final String CREATE_VIEW_KEY = UUID.randomUUID().toString();
		
		private final YouMiAdvert youMiAdvert;
		private ViewGroup parentView;
		private static HashMap<String, AdView> mapView;
		private static HashMap<String, AdView> mapAdded;
		private static HashMap<String, Activity> mapIsOnActivity;
		private static boolean isFirstShow = false;
		private AdSize adSize = AdSize.FIT_SCREEN;
		
		/**
		 * @param youMiAdvert
		 * @param parentView - 父视图
		 * @see #setParentView(ViewGroup)
		 * 参数parentView的设置还可以通过此链接设置
		 */
		private BannerAds(YouMiAdvert youMiAdvert, ViewGroup parentView) {
			this.youMiAdvert = youMiAdvert;
			this.parentView = parentView;
			tryCreateMap();
		}
		
		public void setParentView(ViewGroup parentView) {
			this.parentView = parentView;
		}
		
		/**
		 * @since JDK 1.4
		 * @param bannerSize
		 * @return
		 */
		public BannerAds setBannerSize(int bannerSize) {
			switch(bannerSize) {
			default:
			case BANNER_FIT_SCREEN:
				adSize = AdSize.FIT_SCREEN;
				break;
				
			case BANNER_SIZE_320x50:
				adSize = AdSize.SIZE_320x50;
				break;
				
			case BANNER_SIZE_300x250:
				adSize = AdSize.SIZE_300x250;
				break;
				
			case BANNER_SIZE_468x60:
				adSize = AdSize.SIZE_468x60;
				break;
				
			case BANNER_SIZE_728x90:
				adSize = AdSize.SIZE_728x90;
				break;
			}
			return this;
		}
		
		public BannerAds setAdListener(String key, AdViewListener adViewListener) {
			AdView adView = mapView.get(key);
			if(adView != null)
				adView.setAdListener(adViewListener);
			return this;
		}
		
		private void tryCreateMap() {
			if(mapView == null) 
				mapView = new HashMap<String, AdView>();
			if(mapAdded == null)
				mapAdded = new HashMap<String, AdView>();
			if(mapIsOnActivity == null)
				mapIsOnActivity = new HashMap<String, Activity>();
			
		}
		
		/**
		 * 此函數適用於遊戲
		 * @param activity 會使用addContentView方法
		 * @param gravity
		 */
		public void showAtActivity(String key, Activity activity, int gravity) {
			tryCreateMap();
			if(activity == null || mapAdded.containsKey(key))
				return;
			AdView adView = mapView.get(key);
			if(adView == null)
				return;
			
			FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
			layoutParams.gravity = gravity;
			adView.setTag(layoutParams);
			activity.addContentView(adView, layoutParams);
			mapView.put(key, adView);
			mapAdded.put(key, adView);
			mapIsOnActivity.put(key, activity);
		}
		
		public void showAtActivity(Activity activity, int gravity) {
			showAtActivity(CREATE_VIEW_KEY, activity, gravity);
		}
		
		public BannerAds init(String key) {
			tryCreateMap();
			AdView adView = mapView.get(key);
			if(adView == null) {
				adView = new AdView(youMiAdvert.context, adSize);
				mapView.put(key, adView);
				Log.i("INFO", "create new view..");
			}
			return this;
		}

		
		public void show(String key) {
			Log.i("INFO", "show..");
			tryCreateMap();
			
			Log.i("INFO", "parentView:" + parentView);
			Log.i("INFO", "mapAdded:" + !mapAdded.containsKey(key));
			Log.i("INFO", "mapAdded2:" + mapAdded.get(key));
			if(parentView == null || mapAdded.containsKey(key) || !mapView.containsKey(key)) 
				return;
			
			Log.i("INFO", "show....");
			
			AdView adView = mapView.get(key);
			parentView.addView(adView);
			
			if(isFirstShow) {
				parentView.removeView(adView);
				mapAdded.remove(key);
				parentView.addView(adView);
			}
			Log.i("INFO", "childCount:" + parentView.getChildCount());
			Log.i("INFO", "childCount2:" + adView.getChildCount());
			
			mapAdded.put(key, mapView.get(key));
			
			isFirstShow = true;
		}
		
		public void removeView(String key) {
			tryCreateMap();
			
			AdView adView = mapView.get(key);
			if(parentView != null && adView != null && !mapIsOnActivity.containsKey(key)) {
				parentView.removeView(adView);
				mapAdded.remove(key);
				mapView.remove(key);
				isFirstShow = false;
			}
		}
		
		public void removeViewOnActivity(Activity activity, String key) {
			if(activity == null)
				return;
			ViewGroup viewGroup = (ViewGroup) activity.findViewById(android.R.id.content);
			AdView adView = mapView.get(key);
			if(adView != null && mapIsOnActivity.containsKey(key)) {
				viewGroup.removeView(adView);
				mapAdded.remove(key);
				mapView.remove(key);
				mapIsOnActivity.remove(key);
			}
			
		}
		

		@Override
		public void show() {
			show(CREATE_VIEW_KEY);
			
		}
		
		/**
		 * 销毁对象
		 */
		@Override
		public void destroy() {
			
			Set<Entry<String, Activity>> entrySet = new HashSet<Entry<String, Activity>>(mapIsOnActivity.entrySet());
			
			for(Entry<String, Activity> entry : entrySet) 
				removeViewOnActivity(entry.getValue(), entry.getKey());
			
			Set<String> addedViewKey = new HashSet<String>(mapAdded.keySet());
			addedViewKey.removeAll(mapIsOnActivity.keySet());
			for(String key : addedViewKey) 
				removeView(key);
			
			Log.i("INFO", "mapAdd:" + mapAdded.size());
			isFirstShow = false;
			mapView.clear();
			mapAdded.clear();
			mapIsOnActivity.clear();
			mapView = null;
			mapAdded = null;
			parentView = null;
			mapIsOnActivity = null;
		}

		/**
		 * 一般用于屏幕旋转
		 */
		@Override
		public void recovery() {
			recovery(null);
		}
		
		/**
		 * 如果需要恢復activity上的數據，直接傳activity傳入
		 * @param activity
		 */
		public void recovery(Activity activity) {
			tryCreateMap();
			
			Set<Entry<String, Activity>> entrySet = new HashSet<Entry<String, Activity>>(mapIsOnActivity.entrySet());

			for(Entry<String, Activity> entry : entrySet) {
				String key = entry.getKey();
				final AdView adView = mapView.get(key);
				if(adView != null) {
					ViewGroup parent = (ViewGroup) adView.getParent();
					//刪除原來界面上的數據
					if(parent != null)
						parent.removeView(adView);
					if(activity != null) {
						activity.addContentView(adView, (FrameLayout.LayoutParams)adView.getTag());
						//更換activity
						mapIsOnActivity.put(key, activity);
					} else {
						mapIsOnActivity.remove(key);
						mapAdded.remove(key);
						mapView.remove(key);
					}
				}
			}
			
			Set<String> addedViewKey = new HashSet<String>(mapAdded.keySet());
			addedViewKey.removeAll(mapIsOnActivity.keySet());
			
			for(String key : addedViewKey) {
				final AdView adView = mapAdded.get(key);
				if(adView != null) {
					ViewGroup parent = (ViewGroup) adView.getParent();
					if(parent != null)
						parent.removeView(adView);
					parentView.addView(adView);
				}
			}
			
		}

		@Override
		public BannerAds init() {
			// TODO Auto-generated method stub
			return this;
		}
		
	}
	
	/**
	 * 3
	 * 此類為开屏广告 全屏  无积分
	 * @author Mod_J
	 */
	public static final class SpreadAds extends SpreadSpotAds<SpreadAds> {
		
		private final Class<? extends Activity> clazz;
		
		private SpreadAds(YouMiAdvert youMiAdvert, Class<? extends Activity> clazz) {
			super(youMiAdvert);
			this.clazz = clazz;
		}
		
		/**
		 * 暂时不支持横屏
		 */
		@Override
		public void show() {
			setSpotOrientation(SpreadSpotAds.SPOT_ORIENTATION_PORTRAIT);
			SpotManager.getInstance(youMiAdvert.context).showSplashSpotAds(youMiAdvert.context, clazz);
		}

		@Override
		public void destroy() {
			if(youMiAdvert.getSpotAds() != null) {
				if(youMiAdvert.getSpotAds().isDestroy) {
					SpotManager.getInstance(youMiAdvert.context).onDestroy();
				}
			} else {
				SpotManager.getInstance(youMiAdvert.context).onDestroy();
			}
			
			isDestroy = true;
				
			
		}
		
		/**
		 * 空实现
		 */
		@Override
		public void recovery() {
		}

		/**
		 * 空实现
		 */
		@Override
		public void show(SpotStateListener spotStateListener) {
		}
		
		/**
		 * 暂时不支持横屏
		 */
		public void show(SpotStateListener spotStateListener, Window window) {
			show(spotStateListener, window, true, false, true, null);
		}
		
		/**
		 * 暂时不支持横屏
		 */
		public void show(SpotStateListener spotStateListener, Window window, boolean isShowReciprocal) {
			show(spotStateListener, window, isShowReciprocal, false, true, null);
		}
		
		/**
		 * 暂时不支持横屏
		 */
		public void show(SpotStateListener spotStateListener, Window window, boolean isShowReciprocal, boolean isHideCloseBtn) {
			show(spotStateListener, window, isShowReciprocal, isHideCloseBtn, true, null);
		}
		
		/**
		 * 暂时不支持横屏
		 */
		public void show(SpotStateListener spotStateListener, Window window, boolean isShowReciprocal, boolean isHideCloseBtn, boolean isJumpTargetWhenFail) {
			show(spotStateListener, window, isShowReciprocal, isHideCloseBtn, isJumpTargetWhenFail, null);
		}
		
		public void show(SpotStateListener spotStateListener, Window window, boolean isShowReciprocal, boolean isHideCloseBtn, boolean isJumpTargetWhenFail, Intent intent) {
			final SpotDialogListener spotDialogListener = readyShow(spotStateListener);
			setSpotOrientation(SpreadSpotAds.SPOT_ORIENTATION_PORTRAIT);
			SplashView splashView = new SplashView(youMiAdvert.context, clazz);
			
			splashView.setShowReciprocal(isShowReciprocal);
			splashView.hideCloseBtn(isHideCloseBtn);
			if(intent != null)
				splashView.setIntent(intent);
			splashView.setIsJumpTargetWhenFail(isJumpTargetWhenFail);
			
			if(window != null)
				window.setContentView(splashView.getSplashView());
			
			SpotManager.getInstance(youMiAdvert.context).showSplashSpotAds(youMiAdvert.context,
					splashView, spotDialogListener);
		}
		
	}


	/**
	 * 4
	 * 此類為積分墻 可以分享任務 activity或对话框两种形式
	 * @author Mod_J
	 */
	public static final class IntegralWallAds implements IAdvertModel, IIntegral {
		
		private final YouMiAdvert youMiAdvert;
		
		/**
		 * 分享渠道
		 */
		public static final int CHANNEL_WEIXI = 0;
		
		/**
		 * 有米客户端回调
		 */
		public static final int CREATE_POINT_TO_YOUMI = 0;
		
		/**
		 * 自接服务器
		 */
		public static final int CREATE_POINT_TO_SERVER = 1;
		

		/**
		 * 积分存放的地方,默认是使用有米客户端回调
		 */
		private int createPointProcess = CREATE_POINT_TO_YOUMI;
		
		/**
		 * 使用client和server两种策略算法
		 */
		private static YouMiIntegral youMiIntegral = null;
		
		private IntegralWallAds(YouMiAdvert youMiAdvert) {
			this.youMiAdvert = youMiAdvert;
		}

		/**
		 * 使用有米客戶端
		 * @param userid 不能为空 或者 空串,否则设置无效, 字符串长度必须要小于50
		 * @param isUsingServerCallBack 是否告诉sdk使用了服务器回调
		 * @return this
		 */
		@Override
		public IntegralWallAds init() {
			final YouMiIntegral.ClientPointState pointState = null;
			init(pointState);
			return this;
		}
		
		public IntegralWallAds init(YouMiIntegral.ClientPointState pointState) {
			youMiIntegral = YouMiIntegral.getInstance(youMiAdvert.context, YouMiIntegral.TYPE_YOUMI_INTEGRAL_CLIENT);
			youMiIntegral.setPointState(pointState);
			youMiIntegral.init();
			OffersManager.getInstance(youMiAdvert.context).onAppLaunch();
			createPointProcess = CREATE_POINT_TO_YOUMI;
			return this;
		}
		
		/**
		 * 使用自定義服務器
		 * @param userid 不能为空 或者 空串,否则设置无效, 字符串长度必须要小于50
		 * @param isUsingServerCallBack 是否告诉sdk使用了服务器回调
		 * @return this
		 */
		public IntegralWallAds init(String userId, boolean isUsingServerCallBack) {
			return init(userId, isUsingServerCallBack, null);
		}
		
		public IntegralWallAds init(String userId, boolean isUsingServerCallBack, YouMiIntegral.ServerPointState pointState) {
			YouMiIntegral.YouMiIntegralServer youMiIntegralServer = (YouMiIntegralServer) YouMiIntegral.getInstance(youMiAdvert.context, YouMiIntegral.TYPE_YOUMI_INTEGRAL_SERVER);
			youMiIntegralServer.setPointState(pointState);
			youMiIntegralServer.setCustomUserId(userId).setUsingServerCallBack(isUsingServerCallBack).init();
			youMiIntegral = youMiIntegralServer;
			OffersManager.getInstance(youMiAdvert.context).onAppLaunch();
			createPointProcess = CREATE_POINT_TO_SERVER;
			return this;
		}
		
		public IntegralWallAds create(int youMiIntegralProcessType) {
			if(youMiIntegralProcessType == YouMiIntegral.TYPE_YOUMI_INTEGRAL_SERVER) {
				init(YouMiIntegralServer.DEFAULT_USERID, true);
			} else {
				init();
			}
			return this;
		}
		
		public IntegralWallAds init(int youMiIntegralProcessType, YouMiIntegral.PointState pointState) {
			if(youMiIntegralProcessType == YouMiIntegral.TYPE_YOUMI_INTEGRAL_SERVER) {
				init(YouMiIntegralServer.DEFAULT_USERID, true, pointState == null ? null : (YouMiIntegral.ServerPointState)pointState);
			} else {
				init(pointState == null ? null : (YouMiIntegral.ClientPointState) pointState);
			}
			return this;
		}
		
		public IntegralWallAds create(String userid) {
			return init(userid, true);
		}
		
		public IntegralWallAds create(String userid, YouMiIntegral.ServerPointState pointState) {
			return init(userid, true, pointState);
		}
		
		/**
		 * @return 只有两个值:CREATE_POINT_TO_YOUMI和CREATE_POINT_TO_SERVER
		 */
		public int getPointProcess() {
			return createPointProcess;
		}

		@Override
		public void show() {
			OffersManager.getInstance(youMiAdvert.context).showOffersWall();
		}
		
		public void show(Interface_ActivityListener listener) {
			OffersManager.getInstance(youMiAdvert.context).showOffersWall(listener);
		}
		
		
		public void showDialog(Activity activity) {
			OffersManager.getInstance(youMiAdvert.context).showOffersWallDialog(activity);
		}
		
		/**
		 * 传入对话框关闭监听器
		 * @param activity
		 */
		public void showDialog(Activity activity, OffersWallDialogListener listener) {
			OffersManager.getInstance(youMiAdvert.context).showOffersWallDialog(activity, listener);
		}
		
		/**
		 * 传入对话框宽高像素
		 * @param activity
		 */
		public void showDialog(Activity activity, int width, int height) {
			OffersManager.getInstance(youMiAdvert.context).showOffersWallDialog(activity, width, height);
		}
		
		/**
		 * 传入对话框宽高像素以及关闭监听器
		 * @param activity
		 */
		public void showDialog(Activity activity, int width, int height, OffersWallDialogListener listener) {
			OffersManager.getInstance(youMiAdvert.context).showOffersWallDialog(activity, width, height, listener);
		}
		
		/**
		 * 传入对话框宽高所占屏幕比例（0~1）
		 * @param activity
		 */
		public void showDialog(Activity activity, int width, int height, double scaleOfScreenWidth, double scaleOfScreenHeight) {
			OffersManager.getInstance(youMiAdvert.context).showOffersWallDialog(activity, scaleOfScreenWidth, scaleOfScreenHeight);
		}
		
		/**
		 * 传入对话框宽高所占屏幕比例（0~1）以及关闭监听器
		 * @param activity
		 */
		public void showDialog(Activity activity, double scaleOfScreenWidth, double scaleOfScreenHeight, OffersWallDialogListener listener) {
			OffersManager.getInstance(youMiAdvert.context).showOffersWallDialog(activity, scaleOfScreenWidth, scaleOfScreenHeight, listener);
		}
		
		/**
		 * 展示分享任務墻
		 */
		public void showShareWall() {
			OffersManager.getInstance(youMiAdvert.context).showShareWall();
		}
		
		public void showShareWall(Interface_ActivityListener listener) {
			OffersManager.getInstance(youMiAdvert.context).showShareWall(listener);
		}
		
		public void showShareWallDialog(Activity activity) {
			OffersManager.getInstance(youMiAdvert.context).showShareWallDialog(activity);
		}
		
		public void showShareWallDialog(Activity activity, OffersWallDialogListener listener) {
			OffersManager.getInstance(youMiAdvert.context).showShareWallDialog(activity, listener);
		}
		
		public void showShareWallDialog(Activity activity, int width, int height) {
			OffersManager.getInstance(youMiAdvert.context).showShareWallDialog(activity, width, height);
		}
		
		public void showShareWallDialog(Activity activity, int width, int height, OffersWallDialogListener listener) {
			OffersManager.getInstance(youMiAdvert.context).showShareWallDialog(activity, width, height, listener);
		}
		
		public void showShareWallDialog(Activity activity, double scaleOfScreenWidth, double scaleOfScreenHeight) {
			OffersManager.getInstance(youMiAdvert.context).showShareWallDialog(activity, scaleOfScreenWidth, scaleOfScreenHeight);
		}
		
		public void showShareWallDialog(Activity activity, double scaleOfScreenWidth, double scaleOfScreenHeight, OffersWallDialogListener listener) {
			OffersManager.getInstance(youMiAdvert.context).showShareWallDialog(activity, scaleOfScreenWidth, scaleOfScreenHeight, listener);
		}
		
		/**
		 * 設置分享渠道地,目前只能分享到微信
		 * @param channelType 渠道類型 比如：微信
		 * @param appId 從渠道sdk申請的appId
		 */
		public boolean setShareChannel(int channelType, String appId) {
			if(appId == null || appId.trim().equals(""))
				return false;
			switch(channelType) {
			default:
			case CHANNEL_WEIXI:
				Log.i("INFO", "call registerToWx");
				return OffersManager.getInstance(youMiAdvert.context).registerToWx(appId);
			}
		}
		
		/**
		 * 返回結果的處理 重写 onCreate(Bundle savedInstanceState) 和 onNewIntent(Intent intent) 方法，在 super 语句之后调用有米处理代码
		 */
		public void handleIntent(Activity activity) {
			if(activity == null)
				return;
			OffersManager.getInstance(youMiAdvert.context).handleIntent(activity.getIntent());
			
		}
		
		
		/**
		 * 检查积分墙配置是否成功（没有使用“通过 Receiver来获取积分订单”功能），如果配置有误有可能会导致没有收入或者获取不到积分
		 */
		public boolean checkOffsersAdsConfigNoReceiver() {
			return OffersManager.getInstance(youMiAdvert.context).checkOffersAdConfig();
		}
		
		/**
		 * 检查积分墙配置是否成功（使用了“通过 Receiver来获取积分订单”功能），如果配置有误有可能会导致没有收入或者获取不到积分
		 */
		public boolean checkOffsersAdConfigHasReceiver() {
			return OffersManager.getInstance(youMiAdvert.context).checkOffersAdConfig(true);
		}
		
		/**
		 * 积分墙界面定制接口 标题
		 */
		public IntegralWallAds setTitleText(String title) {
			OffersBrowserConfig.setBrowserTitleText(title);
			return this;
		}
		
		public String getTitleText() {
			return OffersBrowserConfig.getBrowserTitleText();
		}
		
		/**
		 * 积分墙界面定制接口 标题背景颜色
		 */
		public IntegralWallAds setTitleBackgroundColor(int color) {
			OffersBrowserConfig.setBrowserTitleBackgroundColor(color);
			return this;
		}
		
		public int getTitleBackgroundColor() {
			return OffersBrowserConfig.getBrowserTitleBackgroundColor();
		}
		
		/**
		 * 积分墙界面定制接口 是否显示右上角积分余额
		 */
		public IntegralWallAds setPointsVisibility(boolean isShow) {
			OffersBrowserConfig.setPointsLayoutVisibility(isShow);
			return this;
		}
		
		public boolean isPointsVisbility() {
			return OffersBrowserConfig.isPointsLayoutVisbility();
		}
		

		/**
		 * 空實現
		 */
		@Override
		public void recovery() {
		}

		@Override
		public void destroy() {
			OffersManager.getInstance(youMiAdvert.context).onAppExit();
			if(youMiIntegral != null) {
				youMiIntegral.destroy();
				youMiIntegral = null;
			}
		}

		/**
		 * 必须小调用create,否则返回-1,destroy之后就不能调用必须再次create
		 */
		@Override
		public float queryPoints() {
			if(youMiIntegral == null)
				return -1;
			return youMiIntegral.queryPoints();
		}

		/**
		 * 必须小调用create,否则返回false,destroy之后就不能调用必须再次create
		 */
		@Override
		public boolean spendPoints(float points) {
			return youMiIntegral == null ? false : youMiIntegral.spendPoints(points);
		}

		/**
		 * 必须小调用create,否则返回false,destroy之后就不能调用必须再次create
		 */
		@Override
		public boolean addPoints(float points) {
			return youMiIntegral == null ? false : youMiIntegral.addPoints(points);
		}
		
		/**
		 * 设置是否关闭积分到账时通知栏提示功能
		 */
		public IntegralWallAds setEnableEarnPointNotification(boolean isEable) {
			if(youMiIntegral instanceof YouMiIntegralClient)
				((YouMiIntegralClient) youMiIntegral).setEnableEarnPointsNotification(isEable);
			return this;
		}
		
		/**
		 * 设置是否关闭积分到账悬浮框提示功能
		 */
		public IntegralWallAds setEnableEarnPointsToastTips(boolean isEable) {
			if(youMiIntegral instanceof YouMiIntegralClient)
				((YouMiIntegralClient) youMiIntegral).setEnableEarnPointsToastTips(isEable);
			return this;
		}
		
	}
	
	
}
