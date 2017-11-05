package cn.dianyou.advert.adConfig;

public abstract class AbsSDKConfig implements IAdvertConfig {
	
	protected String appId;
	
	public static abstract class Builder<T extends Builder<T>> implements IAdvertConfig.Builder {
		
		protected String appId;
		
		@SuppressWarnings("unchecked")
		@Override
		public T setAppId(String appId) {
			this.appId = appId;
			return (T) this;
		}
		
		public abstract AbsSDKConfig build();
		
	}
	
	protected AbsSDKConfig() {
	}
	
	protected AbsSDKConfig(String appId) {
		this.appId = appId;
	}
	
	public String getAppId() {
		return appId;
	}
}
