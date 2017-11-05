package cn.dianyou.advert.adFactory;

import android.content.Context;
import cn.dianyou.advert.adAbstarct.AbsHolderAdvert;
import cn.dianyou.advert.adConfig.AbsHolderConfig;
import cn.dianyou.advert.adConfig.DianYouConfig;
import cn.dianyou.advert.adEntry.DianYouAdvert;
import cn.dianyou.advert.adFactory.AdvertType.HolderType;

public class HolderAdvertFactory extends AdvertFactory<AbsHolderAdvert<? extends AbsHolderConfig>, AbsHolderConfig, HolderType> {

	@Override
	public AbsHolderAdvert<? extends AbsHolderConfig> obtainAdvert(HolderType type, AbsHolderConfig config,
			boolean isTestMode) {
		if(type != null) {
			switch(type) {
			case DianYou:
				return create(DianYouAdvert.class, new Class<?>[]{Context.class, DianYouConfig.class, boolean.class}, new Object[]{context, config, isTestMode});
			}
		}
		return null;
	}

}
