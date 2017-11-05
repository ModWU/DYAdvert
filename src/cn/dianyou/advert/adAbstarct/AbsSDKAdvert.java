package cn.dianyou.advert.adAbstarct;

import android.content.Context;
import cn.dianyou.advert.adConfig.AbsSDKConfig;

public abstract class AbsSDKAdvert<CONFIG extends AbsSDKConfig> extends AbsAdvert<CONFIG> {
	
	protected AbsSDKAdvert(Context context) {
		super(context);
	}
	
	protected AbsSDKAdvert(Context context, CONFIG config) {
		super(context, config);
	}
}
