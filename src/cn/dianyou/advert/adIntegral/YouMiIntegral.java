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
 * ��������ɽ�������ǿת��Ŀǰֻ����������:YouMiIntegralClient��YouMiintegralServer
 * @author Mod_J
 */
public abstract class YouMiIntegral implements IIntegral {
	
	/**
	 * Ҏ���yһ�Ľӿڣ��O �e�ֵ��~
	 */
	public interface PointState {
		/**
	     *  ������������䶯֪ͨ,�ûص��� UI �߳��н��У���ֱ���� UI ���н���
	     *  @param pointsBalance ��ǰ�������
	     */
		void onPointBalanceChange(float pointsBalance);
		
		/**
		 * �� SDK ��ȡ���û�׬ȡ���ֵĶ���ʱ�����һʱ����ø÷���֪ͨ����
		 * @param EarnPointsOrderList һ�����ֶ����б������������ﴦ�������ϸ������
		 */
		void onEarnPoints(Context context, EarnPointsOrderList list);
		
		/**
		 * ֻ����]�ԏV������r
		 * �����Ǹ���Ȥ��С���ܣ����û�׬ȡ����֮�󣬻���ǽ SDK ����֪ͨ������ʾһ����ȡ���ֵ���ʾ������û���������֪ͨ���ú����ᱻ���á�
		 * �ò����ǿ�ѡ�ģ������Ҫ�ر�֪ͨ��������ʾ�������PointsManager.setEnableEarnPointsNotification(false);
		 * @param context
		 */
		void onViewPoints(Context context);
		
		/**
		 * �]�N���бO ��
		 */
		void unRegisterAll();
		
	}
	
	/**
	 * youMi�͑��ˎ׷�׃�Ӡ�B�O ��
	 * @author Mod_J
	 */
	public static class ClientPointState implements PointState {
		
		//�׷ֵ��~֪ͨ
		private PointsReceiver pointsReceiver;
		private IntentFilter filter;
		private PointsEarnNotify pointsEarnNotify;
		
		//�N�~׃��֪ͨ
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
		 *  �������~���~�O �����ԱO ֪ͨ�ڵ��c��
		 *  @param appId Ӧ�õ� AppID 
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
		 *  �������~���~�O �������ܱO ֪ͨ�ڵ��c��
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
		 *  ����������������䶯���ѱO 
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
	 * �Զ��x�������׷֠�B׃���O ��
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
	 * ���׿ͻ��˴���
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
		 * �Ƿ�رջ��ֵ���֪ͨ����ʾ����
		 * @param isEable �Ƿ����ʹ��
		 * @return
		 */
		public YouMiIntegralClient setEnableEarnPointsNotification(boolean isEable) {
			PointsManager.setEnableEarnPointsNotification(isEable);
			return this;
		}
		
		/**
		 * �رջ��ֵ�����������ʾ����
		 * @param isEable �Ƿ����ʹ��
		 * @return
		 */
		public YouMiIntegralClient setEnableEarnPointsToastTips(boolean isEable) {
			PointsManager.setEnableEarnPointsToastTips(isEable);
			return this;
		}

		

		//�Ռ��F
		@Override
		public void init() {
		}

		
	}
	
	/**
	 * �������Լ�����������
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
		 * @param userId �Ñ�id ������׻��{�����ķ�����
		 * @return this
		 */
		public YouMiIntegralServer setCustomUserId(String userId) {
			if(userId == null)
				return this;
			this.userId = userId;
			return this;
		}
		
		/**
		 * @param isUseServerCalled �Ƿ���Vsdkʹ���˷������ص�
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
