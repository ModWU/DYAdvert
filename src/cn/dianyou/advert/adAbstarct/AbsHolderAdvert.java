package cn.dianyou.advert.adAbstarct;

import android.content.Context;
import cn.dianyou.advert.adConfig.AbsHolderConfig;

public abstract class AbsHolderAdvert<CONFIG extends AbsHolderConfig> extends AbsAdvert<CONFIG> {
	
	protected AbsHolderAdvert(Context context) {
		super(context);
	}

	protected AbsHolderAdvert(Context context, CONFIG config) {
		super(context, config);
	}

	

}
