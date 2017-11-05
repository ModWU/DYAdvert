package cn.dianyou.advert.adFactory;

import android.content.Context;
import cn.dianyou.advert.adAbstarct.AbsSDKAdvert;
import cn.dianyou.advert.adConfig.AbsSDKConfig;
import cn.dianyou.advert.adConfig.YouMiConfig;
import cn.dianyou.advert.adEntry.YouMiAdvert;
import cn.dianyou.advert.adFactory.AdvertType.SDKType;



public class SDKAdvertFactory extends AdvertFactory<AbsSDKAdvert<? extends AbsSDKConfig>, AbsSDKConfig, SDKType> {
	
	private SDKAdvertFactory() {}

	@Override
	public AbsSDKAdvert<? extends AbsSDKConfig> obtainAdvert(SDKType type, AbsSDKConfig config, boolean isTestMode) {
		if(type != null) {
			switch(type) {
			case YouMi:
				return create(YouMiAdvert.class, new Class<?>[]{Context.class, YouMiConfig.class, boolean.class}, new Object[]{context, config, isTestMode});
			}
		}
		return null;
	}
	
}
