package cn.dianyou.advert.adConfig;

public final class YouMiConfig extends AbsSDKConfig {
	
	private String appSecret;
	
	public static class Builder extends AbsSDKConfig.Builder<Builder> {
		
		private String appSecret;
		
		private Builder() {}

		
		public static Builder create() {
			return new Builder();
		}
		

		@Override
		public YouMiConfig build() {
			return new YouMiConfig(appId, appSecret);
		}

		
		
		public Builder setAppSecret(String appSecret) {
			this.appSecret = appSecret;
			return this;
		}
		
	}
	
	private YouMiConfig(String appId, String appSecret) {
		super(appId);
		this.appSecret = appSecret;
	}

	public String getAppSecret() {
		return appSecret;
	}

}
