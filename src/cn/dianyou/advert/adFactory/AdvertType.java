package cn.dianyou.advert.adFactory;

import cn.dianyou.advert.adAbstarct.AbsAdvert;
import cn.dianyou.advert.adAbstarct.AbsHolderAdvert;
import cn.dianyou.advert.adAbstarct.AbsSDKAdvert;
import cn.dianyou.advert.adConfig.AbsHolderConfig;
import cn.dianyou.advert.adConfig.AbsSDKConfig;
import cn.dianyou.advert.adConfig.IAdvertConfig;
import cn.dianyou.advert.adEntry.DianYouAdvert;
import cn.dianyou.advert.adEntry.YouMiAdvert;

public interface AdvertType {
	
	Class<? extends AbsAdvert<? extends IAdvertConfig>> getTypeClass();
	
	enum SDKType implements AdvertType {
		
		YouMi(YouMiAdvert.class),;
		
		private Class<? extends AbsSDKAdvert<? extends AbsSDKConfig>> clazz;
		
		private SDKType(Class<? extends AbsSDKAdvert<? extends IAdvertConfig>> clazz) {
			this.clazz = clazz;
		}

		@Override
		public Class<? extends AbsSDKAdvert<? extends IAdvertConfig>> getTypeClass() {
			return clazz;
		}

	}
	
	enum HolderType implements AdvertType {
		
		DianYou(DianYouAdvert.class);
		
		private Class<? extends AbsHolderAdvert<? extends AbsHolderConfig>> clazz;
		
		private HolderType(Class<? extends AbsHolderAdvert<? extends IAdvertConfig>> clazz) {
			this.clazz = clazz;
		}

		@Override
		public Class<? extends AbsHolderAdvert<? extends IAdvertConfig>> getTypeClass() {
			return clazz;
		}
		
	}
	
}
