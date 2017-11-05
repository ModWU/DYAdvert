package cn.dianyou.advert.adAbstarct;

import android.content.Context;
import cn.dianyou.advert.adConfig.IAdvertConfig;

public abstract class AbsAdvert<CONFIG extends IAdvertConfig> implements IAdvertModel {
	
	protected Context context;
	protected CONFIG config;
	protected boolean isTestMode = true;
	protected boolean isInitFinished = false;
	protected boolean isDestroy = false;
	
	public static class FactoryCreator {
		
	}
	
	protected AbsAdvert(Context context) {
		this.context = context;
	}
	
	protected AbsAdvert(Context context, CONFIG config) {
		this.context = context;
		this.config = config;
	}
	
	public CONFIG getAdvertConfig() {
		return config;
	}
	public void setAdvertConfig(CONFIG config) {
		this.config = config;
	}
	
	public boolean isTestMode() {
		return isTestMode;
	}

	public void setTestMode(boolean isTestMode) {
		this.isTestMode = isTestMode;
	}
	
	@Override
	public void destroy() {
		context = null;
	}
	
	public boolean isInitFinished() {
		return isInitFinished;
	}
	
	public boolean isDestroy() {
		return isDestroy;
	}

	
	
}
