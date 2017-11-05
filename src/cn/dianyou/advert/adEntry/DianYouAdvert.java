package cn.dianyou.advert.adEntry;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.ViewGroup;
import cn.dianyou.advert.adAbstarct.AbsHolderAdvert;
import cn.dianyou.advert.adAbstarct.IAdvertModel;
import cn.dianyou.advert.adConfig.DianYouConfig;
import cn.dianyou.managers.DYAdvertManager;
import cn.dianyou.managers.DYBannerManager;
import cn.dianyou.managers.DYReboundManager;
import cn.dianyou.managers.DYSpotManager;
import cn.dianyou.managers.DYSpreadManager;
import cn.dianyou.managers.DYViewPagerManager;
import cn.dianyou.utils.ContantsUtils;
import cn.dianyou.utils.SharedPreferencesUtils;

public final class DianYouAdvert extends AbsHolderAdvert<DianYouConfig> {
	
	private SpotAds spotAds;
	
	private BannerAds bannerAds;
	
	private SpreadAds spreadAds;
	
	private ReboundAds reboundAds;
	
	private ViewPagerAds viewPagerAds;

	private DianYouAdvert(Context context) {
		super(context);
	}
	
	private DianYouAdvert(Context context, DianYouConfig dianYouConfig, boolean isTestMode) {
		super(context, dianYouConfig);
		this.isTestMode = isTestMode;
	}
	
	
	public String obtainAdvertId() {
		SharedPreferencesUtils sp = SharedPreferencesUtils.newInstance(context, ContantsUtils.DianYouKey.PRE_ADVERT_FILENAME);
		return sp.getString(ContantsUtils.DianYouKey.ADVERT_ID, null);
	}
	
	public boolean checkAdvertId() {
		SharedPreferencesUtils sp = SharedPreferencesUtils.newInstance(context, ContantsUtils.DianYouKey.PRE_ADVERT_FILENAME);
		String id = sp.getString(ContantsUtils.DianYouKey.ADVERT_ID, null);
		return id != null;
	}
	
	public void saveAdvertId(String advertId) {
		SharedPreferencesUtils sp = SharedPreferencesUtils.newInstance(context, ContantsUtils.DianYouKey.PRE_ADVERT_FILENAME);
		sp.saveString(ContantsUtils.DianYouKey.ADVERT_ID, advertId).commit();
	}


	@Override
	public void show() {
		if(spotAds != null)
			spotAds.show();

		if(bannerAds != null)
			bannerAds.show();
		
		if(spreadAds != null)
			spreadAds.show();
		
		if(reboundAds != null)
			reboundAds.show();
		
		if(viewPagerAds != null)
			viewPagerAds.show();
		
	}

	@Override
	public void recovery() {
		if(spotAds != null)
			spotAds.recovery();

		if(bannerAds != null)
			bannerAds.recovery();
		
		if(spreadAds != null)
			spreadAds.recovery();
		
		if(reboundAds != null)
			reboundAds.recovery();
		
		if(viewPagerAds != null)
			viewPagerAds.recovery();
		
	}

	@Override
	public void destroy() {
		super.destroy();
		
		if(spotAds != null)
			spotAds.destroy();

		if(bannerAds != null)
			bannerAds.destroy();
		
		if(spreadAds != null)
			spreadAds.destroy();
		
		if(reboundAds != null)
			reboundAds.destroy();
		
		if(viewPagerAds != null)
			viewPagerAds.destroy();
		
	}

	@Override
	public DianYouAdvert init() {
		isInitFinished = true;
		//这里急需修改
		checkAndRefAppId();
		return this;
	}
	
	private void checkAndRefAppId() {
		
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
	public BannerAds createBannerAds(ViewGroup parent) {
		bannerAds =  new BannerAds(this, parent);
		isDestroy = false;
		return bannerAds;
	}
	
	/**
	 * 创建开屏广告
	 */
	public SpreadAds createSpreadAds() {
		spreadAds = new SpreadAds(this);
		isDestroy = false;
		return spreadAds;
	}
	
	/**
	 * 创建弹性广告,某些手机受限
	 */
	public ReboundAds createReboundAds() {
		reboundAds = new ReboundAds(this);
		isDestroy = false;
		return reboundAds;
	}
	
	/**
	 * 创建viewpager广告
	 */
	public ViewPagerAds createViewPagerAds(ViewGroup parent) {
		viewPagerAds = new ViewPagerAds(this, parent);
		isDestroy = false;
		return viewPagerAds;
	}
	
	@SuppressWarnings("unchecked")
	private static <T extends DYAdvertManager<T>>T getManager(Class<T> clazz, String methodName, Context context) {
		T t = null;
		if(clazz != null) {
			try {
				Method method = clazz.getDeclaredMethod(methodName, new Class<?>[]{Context.class});
				method.setAccessible(true);
				t = (T) method.invoke(clazz, new Object[]{context});
			} catch (Exception e) {
				
				Log.i("INFO", "getManager ex:" + e.toString());
			}
			
		}
		return t;
	}
	
	public static final class SpotAds implements IAdvertModel {
		
		private DianYouAdvert youAdvert;
		
		private DYSpotManager dySpotManager;
		
		private List<Bitmap> failListBmp;
		
		private SpotAds(DianYouAdvert youAdvert) {
			this.youAdvert = youAdvert;
			dySpotManager = DianYouAdvert.getManager(DYSpotManager.class, "getInstance", youAdvert.context);
		}
		
		public SpotAds setAnimationType(int animationType) {
			dySpotManager.setAnimationType(animationType);
			return this;
		}
		
		public SpotAds setFailListImage(List<Bitmap> failListBmp) {
			if(failListBmp != null)
				this.failListBmp = new ArrayList<Bitmap>(failListBmp);
			return this;
		}


		@Override
		public void show() {
			dySpotManager.show(youAdvert.context, failListBmp);
			
		}
		
		public void show(Activity activity) {
			dySpotManager.show(activity, failListBmp);
		}

		@Override
		public void recovery() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void destroy() {
			if(dySpotManager != null) {
				dySpotManager.detroy();
				dySpotManager = null;
			}
			
			if(failListBmp != null)
				failListBmp.clear();
		}

		@Override
		public SpotAds init() {
			// TODO Auto-generated method stub
			return this;
		}
		
	}
	
	public static final class BannerAds implements IAdvertModel {
		
		private DianYouAdvert youAdvert;
		
		private DYBannerManager dyBannerManager;
		
		private List<Bitmap> failListBmp;
		
		private ViewGroup parent;
		
		private BannerAds(DianYouAdvert youAdvert, ViewGroup parent) {
			this.youAdvert = youAdvert;
			dyBannerManager = DianYouAdvert.getManager(DYBannerManager.class, "getInstance", youAdvert.context);
			this.parent = parent;
		}
		
		public BannerAds setFailListImage(List<Bitmap> failListBmp) {
			if(failListBmp != null)
				this.failListBmp = new ArrayList<Bitmap>(failListBmp);
			return this;
		}


		@Override
		public void show() {
			dyBannerManager.show(parent, failListBmp);
		}
		
		public void show(ViewGroup parent) {
			dyBannerManager.show(parent, failListBmp);
		}

		@Override
		public void recovery() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void destroy() {
			if(dyBannerManager != null) {
				dyBannerManager.detroy();
				dyBannerManager = null;
			}
			
			if(failListBmp != null)
				failListBmp.clear();
			
			if(parent != null)
				parent = null;
			
		}


		@Override
		public BannerAds init() {
			// TODO Auto-generated method stub
			return this;
		}
		
	}
	
	public static final class SpreadAds implements IAdvertModel {
		
		private DianYouAdvert youAdvert;
		
		private DYSpreadManager spreadManager;
		
		private List<Bitmap> failListBmp;
		
		private SpreadAds(DianYouAdvert youAdvert) {
			this.youAdvert = youAdvert;
			spreadManager = DianYouAdvert.getManager(DYSpreadManager.class, "getInstance", youAdvert.context);
		}


		@Override
		public void show() {
			spreadManager.show(failListBmp);
		}
		
		public SpreadAds setFailListImage(List<Bitmap> failListBmp) {
			if(failListBmp != null)
				this.failListBmp = new ArrayList<Bitmap>(failListBmp);
			return this;
		}

		@Override
		public void recovery() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void destroy() {
			if(spreadManager != null) {
				spreadManager.detroy();
				spreadManager = null;
			}
			
			if(failListBmp != null)
				failListBmp.clear();
			
		}


		@Override
		public SpreadAds init() {
			// TODO Auto-generated method stub
			return this;
		}
		
	}
	
	public static final class ReboundAds implements IAdvertModel {
		
		private DianYouAdvert youAdvert;
		
		private DYReboundManager dyReboundManager;
		
		private List<Bitmap> failListBmp;
		
		private ReboundAds(DianYouAdvert youAdvert) {
			this.youAdvert = youAdvert;
			dyReboundManager = DianYouAdvert.getManager(DYReboundManager.class, "getInstance", youAdvert.context);
		}
		
		public ReboundAds setFailListImage(List<Bitmap> failListBmp) {
			if(failListBmp != null)
				this.failListBmp = new ArrayList<Bitmap>(failListBmp);
			return this;
		}


		@Override
		public void show() {
			dyReboundManager.show(youAdvert.context, failListBmp);
		}
		
		public void show(Activity activity) {
			dyReboundManager.show(activity, failListBmp);
		}

		@Override
		public void recovery() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void destroy() {
			if(dyReboundManager != null) {
				dyReboundManager.detroy();
				dyReboundManager = null;
			}
			
			if(failListBmp != null)
				failListBmp.clear();
			
		}


		@Override
		public ReboundAds init() {
			// TODO Auto-generated method stub
			return this;
		}
		
	}
	
	public static final class ViewPagerAds implements IAdvertModel {
		
		private DianYouAdvert youAdvert;
		private DYViewPagerManager dyViewPagerManager;
		private ViewGroup parent;
		private List<Bitmap> failListBmp;
		

		private ViewPagerAds(DianYouAdvert youAdvert, ViewGroup parent) {
			this.youAdvert = youAdvert;
			this.parent = parent;
			dyViewPagerManager = DianYouAdvert.getManager(DYViewPagerManager.class, "getInstance", youAdvert.context);
		}
		
		@Override
		public IAdvertModel init() {
			// TODO Auto-generated method stub
			return this;
		}
		
		public ViewPagerAds setFailListImage(List<Bitmap> failListBmp) {
			if(failListBmp != null)
				this.failListBmp = new ArrayList<Bitmap>(failListBmp);
			return this;
		}

		@Override
		public void show() {
			dyViewPagerManager.show(parent, failListBmp);
			
		}
		
		public void show(ViewGroup parent) {
			dyViewPagerManager.show(parent, failListBmp);
		}

		@Override
		public void recovery() {
			
		}
		
		public void handleOnStart() {
			dyViewPagerManager.handleOnStart();
		}
		
		public void handleOnStop() {
			dyViewPagerManager.handleOnStop();
		}
		

		@Override
		public void destroy() {
			
			if(dyViewPagerManager != null) {
				dyViewPagerManager.detroy();
				dyViewPagerManager = null;
			}
			
			if(failListBmp != null)
				failListBmp.clear();
			
			if(parent != null)
				parent = null;
		}
		
	}

}
