package cn.dianyou.advert.adIntegral;

import android.content.Context;
import android.content.IntentFilter;
import android.util.Log;
import net.youmi.android.offers.EarnPointsOrderList;
import net.youmi.android.offers.OffersManager;
import net.youmi.android.offers.PointsChangeNotify;
import net.youmi.android.offers.PointsEarnNotify;
import net.youmi.android.offers.PointsManager;
import net.youmi.android.offers.PointsReceiver;

/**
 * 根据需求可进行类型强转，目前只有两种类型:YouMiIntegralClient和YouMiintegralServer
 * @author Mod_J
 */
public abstract class YouMiIntegral implements IIntegral {
	
	/**
	 * 規定統一的接口，監聽積分到賬
	 */
	public interface PointState {
		/**
	     *  积分余额增减变动通知,该回调在 UI 线程中进行，可直接与 UI 进行交互
	     *  @param pointsBalance 当前积分余额
	     */
		void onPointBalanceChange(float pointsBalance);
		
		/**
		 * 当 SDK 获取到用户赚取积分的订单时，会第一时间调用该方法通知您。
		 * @param EarnPointsOrderList 一个积分订单列表，您可以在这里处理积分详细订单。
		 */
		void onEarnPoints(Context context, EarnPointsOrderList list);
		
		/**
		 * 只限於註冊廣播的情況
		 * 这里是个有趣的小功能，当用户赚取积分之后，积分墙 SDK 会在通知栏上显示一条获取积分的提示，如果用户点击了这个通知，该函数会被调用。
		 * 该操作是可选的，如果需要关闭通知栏积分提示，请调用PointsManager.setEnableEarnPointsNotification(false);
		 * @param context
		 */
		void onViewPoints(Context context);
		
		/**
		 * 註銷所有監聽器
		 */
		void unRegisterAll();
		
	}
	
	/**
	 * youMi客戶端幾分變動狀態監聽器
	 * @author Mod_J
	 */
	public static class ClientPointState implements PointState {
		
		//幾分到賬通知
		private PointsReceiver pointsReceiver;
		private IntentFilter filter;
		private PointsEarnNotify pointsEarnNotify;
		
		//餘額變動通知
		private PointsChangeNotify pointsChangeNotify;
		
		private PointState pointState;
		
		private Context context;
		
		
		private ClientPointState(Context context, PointState pointState) {
			this.context = context;
			this.pointState = pointState;
		}
		
		public static ClientPointState build(Context context, PointState pointState) {
			return new ClientPointState(context, pointState);
		}
		
		public static ClientPointState build(Context context) {
			return new ClientPointState(context, null);
		}
		
		
		/**
		 *  構建余額到賬監聽，可以監聽通知欄的點擊
		 *  @param appId 应用的 AppID 
		 */
		public ClientPointState buildStateReceiver(String appId) {
			
			if(pointsReceiver != null)
				return this;
			
			pointsReceiver = new PointsReceiver() {

				@Override
				protected void onEarnPoints(Context context, EarnPointsOrderList list) {
					ClientPointState.this.onEarnPoints(context, list);
				}

				@Override
				protected void onViewPoints(Context context) {
					ClientPointState.this.onViewPoints(context);
				}
				
			};
			
			if(filter == null) {
				if(appId != null) {
					filter = new IntentFilter();
					filter.addAction("ep_" + appId);
					filter.addAction("vp_" + appId);
				}
			}
			
			if(filter == null || appId == null)
				pointsReceiver = null;
			
			return this;
		}
		
		/**
		 *  構建余額到賬監聽，但不能監聽通知欄的點擊
		 */
		public ClientPointState buildStateEarnNotify() {
			
			if(pointsEarnNotify != null)
				return this;
			
			pointsEarnNotify = new PointsEarnNotify() {
				
				@Override
				public void onPointEarn(Context context, EarnPointsOrderList list) {
					ClientPointState.this.onEarnPoints(context, list);
				}
			};
			
			return this;
		}

		/**
		 *  構建积分余额增减变动提醒監聽
		 */
		public ClientPointState buildStateChangeNotify() {
			
			if(pointsChangeNotify != null)
				return this;
			
			pointsChangeNotify = new PointsChangeNotify() {
				
				@Override
				public void onPointBalanceChange(float pointsBalance) {
					ClientPointState.this.onPointBalanceChange(pointsBalance);
				}
			};
			
			
			
			return this;
		}
		
		public ClientPointState register() {
			if(pointsChangeNotify != null)
				PointsManager.getInstance(context).registerNotify(pointsChangeNotify);
			
			if(pointsEarnNotify != null)
				PointsManager.getInstance(context).registerPointsEarnNotify(pointsEarnNotify);
			
			if(pointsReceiver != null && filter != null)
				context.registerReceiver(pointsReceiver, filter);
			
			Log.i("INFO", "register");
			
			return this;
		}
		
		public void setPointState(PointState pointState) {
			this.pointState = pointState;
		}


		@Override
		public void onPointBalanceChange(float pointsBalance) {
			if(pointState != null)
				pointState.onPointBalanceChange(pointsBalance);
				
		}


		@Override
		public void onEarnPoints(Context context, EarnPointsOrderList list) {
			if(pointState != null)
				pointState.onEarnPoints(context, list);
		}


		@Override
		public void onViewPoints(Context context) {
			if(pointState != null)
				pointState.onViewPoints(context);
		}


		@Override
		public void unRegisterAll() {
			
			if(pointState != null)
				pointState.unRegisterAll();
			
			if(pointsChangeNotify != null) {
				PointsManager.getInstance(context).unRegisterNotify(pointsChangeNotify);
				pointsChangeNotify = null;
			}
			
			if(pointsReceiver != null) {
				context.unregisterReceiver(pointsReceiver);
				pointsReceiver = null;
			}
			
			if(pointsEarnNotify != null) {
				PointsManager.getInstance(context).unRegisterPointsEarnNotify(pointsEarnNotify);
				pointsEarnNotify = null;
			}
			
			
			filter = null;
			
		}
		
	}
	
	/**
	 * 自定義服務器幾分狀態變化監聽器
	 * @author Mod_J
	 */
	public static class ServerPointState implements PointState {

		@Override
		public void onPointBalanceChange(float pointsBalance) {
			
		}

		@Override
		public void onEarnPoints(Context context, EarnPointsOrderList list) {
			
		}

		@Override
		public void onViewPoints(Context context) {
			
		}

		@Override
		public void unRegisterAll() {
			
		}
		
	}
	

	protected Context context;
	
	public static final int TYPE_YOUMI_INTEGRAL_CLIENT = 0;
	
	public static final int TYPE_YOUMI_INTEGRAL_SERVER = 1;
	
	private static PointState pointState;
	
	public static YouMiIntegral getInstance(Context context, int type) {
		switch(type) {
		default:
		case TYPE_YOUMI_INTEGRAL_CLIENT:
			return new YouMiIntegralClient(context);
		case TYPE_YOUMI_INTEGRAL_SERVER:
			return new YouMiIntegralServer(context);
		}
	}
	
	protected YouMiIntegral(Context context) {
		this.context = context;
	}
	
	public abstract void init();
	
	public void setPointState(PointState pointState) {
		YouMiIntegral.pointState = pointState;
	}
	
	@Override
	public void destroy() {
		if(pointState != null) {
			pointState.unRegisterAll();
			pointState = null;
		}
		
	}
	
	
	/**
	 * 有米客户端处理
	 * @author Mod_J
	 */
	public final static class YouMiIntegralClient extends YouMiIntegral {
		

		private YouMiIntegralClient(Context context) {
			super(context);
		}
		
		@Override
		public float queryPoints() {
			return PointsManager.getInstance(context).queryPoints();
		}

		@Override
		public boolean spendPoints(float points) {
			return PointsManager.getInstance(context).spendPoints(points);
		}

		@Override
		public boolean addPoints(float points) {
			return PointsManager.getInstance(context).awardPoints(points);
		}
		
		/**
		 * 是否关闭积分到账通知栏提示功能
		 * @param isEable 是否可以使用
		 * @return
		 */
		public YouMiIntegralClient setEnableEarnPointsNotification(boolean isEable) {
			PointsManager.setEnableEarnPointsNotification(isEable);
			return this;
		}
		
		/**
		 * 关闭积分到账悬浮框提示功能
		 * @param isEable 是否可以使用
		 * @return
		 */
		public YouMiIntegralClient setEnableEarnPointsToastTips(boolean isEable) {
			PointsManager.setEnableEarnPointsToastTips(isEable);
			return this;
		}

		

		//空實現
		@Override
		public void init() {
		}

		
	}
	
	/**
	 * 开发者自己服务器处理
	 * @author Mod_J
	 */
	public final static class YouMiIntegralServer extends YouMiIntegral {
		
		public static final String DEFAULT_USERID = "USER_DEFAULT";
		
		private String userId = DEFAULT_USERID;
		private boolean isUserCall = true;
		

		private YouMiIntegralServer(Context context) {
			super(context);
		}
		
		
		@Override
		public float queryPoints() {
			// TODO Auto-generated method stub
			return 0;
		}
		
		public String getUserId() {
			return userId;
		}

		@Override
		public boolean spendPoints(float points) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean addPoints(float points) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void destroy() {
			// TODO Auto-generated method stub
			
		}
		/**
		 * @param userId 用戶id 用於有米回調到您的服務器
		 * @return this
		 */
		public YouMiIntegralServer setCustomUserId(String userId) {
			if(userId == null)
				return this;
			this.userId = userId;
			return this;
		}
		
		/**
		 * @param isUseServerCalled 是否告訴sdk使用了服务器回调
		 * @return this
		 */
		public YouMiIntegralServer setUsingServerCallBack(boolean isUseServerCalled) {
			this.isUserCall = isUseServerCalled;
			return this;
		}

		@Override
		public void init() {
			if(userId == null)
				userId = DEFAULT_USERID;
			OffersManager.getInstance(context).setCustomUserId(userId);
			OffersManager.getInstance(context).setUsingServerCallBack(isUserCall);
		}
		
	}
	
	

}
