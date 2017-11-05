package cn.dianyou.advert;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import cn.dianyou.managers.DYAdvertManager;
import cn.dianyou.managers.DYReboundManager;
import cn.dianyou.utils.SharedPreferencesUtils;
import cn.dianyou.views.ReboundViewPager;
import cn.dianyou.views.WrapReboundViewPager;

public class TestActivity2 extends Activity {
	
	private ReboundViewPager viewPager;
	
	private final int[] IMG_IDS = new int[]{R.drawable.ad1, R.drawable.ad2, R.drawable.a, R.drawable.b};
	
	private WrapReboundViewPager wrapViewPager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test2);
		
		
		
		//initViews();
		
		//viewPager.setPagerViews(getDataViews());
	}
	
	
	public void onShow(View view) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				SharedPreferencesUtils sp = SharedPreferencesUtils.newInstance(TestActivity2.this, "yu");
				sp.saveString("a", "a1").saveString("b", "b1").commit();
				
				SharedPreferencesUtils sp2 = SharedPreferencesUtils.newInstance(TestActivity2.this, "yu3");
				sp2.saveString("b", "b1").saveString("c", "bc1fffffffffff").commit();
			}
			
		}).start();
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				SharedPreferencesUtils sp3 = SharedPreferencesUtils.newInstance(TestActivity2.this, "yu");
				sp3.saveString("t", "t1").saveString("ty", "ty1").commit();
				
				SharedPreferencesUtils sp4 = SharedPreferencesUtils.newInstance(TestActivity2.this, "yu4");
				sp4.saveString("t4", "t4").saveString("t5", "ty5").commit();
				
				SharedPreferencesUtils sp5 = SharedPreferencesUtils.newInstance(TestActivity2.this, "yu3");
				sp5.saveString("t--", "t4").saveString("t----", "ty5").commit();
			}
			
		}).start();
		
	}

	/*private List<View> getDataViews() {
		List<View> list = new ArrayList<View>();
		
		for(int i = 0; i < IMG_IDS.length; i++) {
			ImageView imageView = new ImageView(this);
			imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
			imageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), IMG_IDS[i]));
			imageView.setScaleType(ScaleType.FIT_XY);
			list.add(imageView);
		}
		
		
		return list;
	}

	private void initViews() {
		viewPager = (ReboundViewPager) findViewById(R.id.id_viewpager);
		viewPager.setIndexDot(true);
		
		wrapViewPager = (WrapReboundViewPager) findViewById(R.id.id_viewpagerwrap);
		wrapViewPager.setOnCloseListener(new IOnCloseListener() {
			
			@Override
			public void close() {
				Toast.makeText(TestActivity2.this, "µã»÷¹Ø±ÕÁË!", 0).show();
				
			}
		});
		
	}*/
}
