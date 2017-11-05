package cn.dianyou.advert.adFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import android.content.Context;
import android.util.Log;
import cn.dianyou.advert.adAbstarct.AbsAdvert;
import cn.dianyou.advert.adConfig.IAdvertConfig;

public abstract class  AdvertFactory<ADVERT extends AbsAdvert<? extends IAdvertConfig>, CONFIG extends IAdvertConfig, TYPE extends AdvertType> {
	
	protected Context context;
	
	/**
	 * ʹ�ÿչ��캯��
	 * @param clazz AdvertFactory���Class����
	 * @return һ����������
	 */
	public static <T extends AdvertFactory<? extends AbsAdvert<? extends IAdvertConfig>, ? extends IAdvertConfig, ? extends AdvertType>> T newInstance(Context context, Class<T> clazz) {
		
		T t = null;
		
		if(clazz != null) {
			try {
				Constructor<T> constructor = clazz.getDeclaredConstructor(new Class<?>[]{});
				constructor.setAccessible(true);
				t = constructor.newInstance(new Object[]{});
				
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
				Log.i("INFO", "the method newInstance() in " + AdvertFactory.class.getName() + " NoSuchMethodException!");
			} catch (SecurityException e) {
				e.printStackTrace();
				Log.i("INFO", "the method newInstance() in " + AdvertFactory.class.getName() + " SecurityException!");
			} catch (InstantiationException e) {
				e.printStackTrace();
				Log.i("INFO", "the method newInstance() in " + AdvertFactory.class.getName() + " InstantiationException!");
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				Log.i("INFO", "the method newInstance() in " + AdvertFactory.class.getName() + " IllegalAccessException!");
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				Log.i("INFO", "the method newInstance() in " + AdvertFactory.class.getName() + " IllegalArgumentException!");
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				Log.i("INFO", "the method newInstance() in " + AdvertFactory.class.getName() + " InvocationTargetException!");
			}
			
		}
		
		t.context = context;
		
		return t;
	}
	
	/**�o����
	 * ��ȡһ��������
	 * @param null
	 * @return ���ʵ��
	 */
	public ADVERT obtainAdvert(TYPE type) {
		return obtainAdvert(type, null, true);
	}
	
	/**������
	 * ��ȡһ��������
	 * @param null
	 * @return ���ʵ��
	 */
	protected abstract ADVERT obtainAdvert(TYPE type, CONFIG config, boolean isTestMode);
	
	/**
	 * ͨ�����䴴������
	 */
	@SuppressWarnings("unchecked")
	protected ADVERT create(Class<? extends ADVERT> clazz, Class<?>[] params, Object[] paramsValue) {
		
		ADVERT obj = null;
		
		if(clazz != null) {
			
			if(params == null)
				params = new Class<?>[]{};
				
			if(paramsValue == null)
				paramsValue = new Object[]{};
			
			try {
				Constructor<?> constructor = clazz.getDeclaredConstructor(params);
				constructor.setAccessible(true);
				obj = (ADVERT) constructor.newInstance(paramsValue);
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
				Log.i("INFO", "the method newInstance() in " + AdvertFactory.class.getName() + " create NoSuchMethodException!");
			} catch (SecurityException e) {
				e.printStackTrace();
				Log.i("INFO", "the method newInstance() in " + AdvertFactory.class.getName() + " create SecurityException!");
			} catch (InstantiationException e) {
				e.printStackTrace();
				Log.i("INFO", "the method newInstance() in " + AdvertFactory.class.getName() + " create InstantiationException!");
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				Log.i("INFO", "the method newInstance() in " + AdvertFactory.class.getName() + " create IllegalAccessException!");
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				Log.i("INFO", "the method newInstance() in " + AdvertFactory.class.getName() + " create IllegalArgumentException!");
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				Log.i("INFO", "the method newInstance() in " + AdvertFactory.class.getName() + " create InvocationTargetException!");
			}
			
		}
		
		return obj;
	}
	
	public void setContext(Context context) {
		this.context = context;
	}
	
	public void clear() {
		context = null;
	}

}
