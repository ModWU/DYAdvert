package cn.dianyou.advert;




import java.io.File;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;
import cn.dianyou.advert.adEntry.DianYouAdvert;
import cn.dianyou.advert.adFactory.AdvertFactory;
import cn.dianyou.advert.adFactory.AdvertType;
import cn.dianyou.advert.adFactory.HolderAdvertFactory;
import cn.dianyou.managers.DYSpotManager;
import cn.dianyou.utils.FileManageUtils;
import cn.dianyou.views.SpreadView;

public class TestActivity extends Activity {
	
	private SpreadView spreadView;
	
	private LinearLayout linearLayout;
	
	private LinearLayout linearLayout2;
	
	private DianYouAdvert dianYouAdvert;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pingmu);
		linearLayout2 = (LinearLayout) findViewById(R.id.id_LLparent);
		linearLayout = (LinearLayout) findViewById(R.id.id_parent);
		linearLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.i("INFO", "onClick????");
				
			}
			
		});
		
		linearLayout.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				Log.i("INFO", "onTouch????");
				return false;
			}
			
		});
		
		HolderAdvertFactory factory = AdvertFactory.newInstance(this, HolderAdvertFactory.class);
		dianYouAdvert = (DianYouAdvert) factory.obtainAdvert(AdvertType.HolderType.DianYou, null, true);
		dianYouAdvert.init();
		
		/*spreadView = (SpreadView) findViewById(R.id.id_spreadView);
		//spreadView.setTextContainerColor(Color.RED);
		spreadView.setStopListener(new ISpreadStopListener() {
			
			@Override
			public void onResponse(SpreadView view) {
				ViewGroup viewGroup = (ViewGroup) view.getParent();
				viewGroup.removeView(view);
			}
		}, 200);
		spreadView.go(1000);*/
		
		FileManageUtils.deleteFilesAtDir(this, "dianyou_cache_advert", 3, 1);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	protected void onDestroy() {
		dianYouAdvert.destroy();
		super.onDestroy();
	}
	
	public void onStartAd(View view) {
		
		dianYouAdvert.createBannerAds(linearLayout).show();
		
		
		
		//DYSpreadManager.getInstance(this).show();
		
		//DYSpotManager.getInstance(this).setAnimationType(DYSpotManager.SIMPLE_ANIMATION_TYPE).show(this);
		
		Log.i("INFO", "onStartAd");
		//spreadView.go(500);
	}
	
	public void onPauseAd(View view) {
		Log.i("INFO", "onStartAd");
		//spreadView.pause();
		dianYouAdvert.createSpreadAds().show();
	}
	
	public void onStopAd(View view) {
		Log.i("INFO", "onStartAd");
	//	spreadView.stop();
		
	}
	
	public void onContinueAd(View view) {
		Log.i("INFO", "onContinueAd");
		//spreadView.continueGo();
		
		dianYouAdvert.createSpotAds().setAnimationType(DYSpotManager.SIMPLE_ANIMATION_TYPE).show(this);
	}
	
	public void onRebound(View view) {
		
		dianYouAdvert.createReboundAds().show(this);
	}
	
	public void onViewPager(View view) {
		dianYouAdvert.createViewPagerAds(linearLayout2).show();
	}
	
	public void onPost(View view) {
		long size = FileManageUtils.getTotalSizeSD();
		long size2 = FileManageUtils.getTotalSizeInternal();
		
		Log.i("INFO", size + "");
		Log.i("INFO", size2 + "");
		
		long size32 = FileManageUtils.getTotalSizeInternal(FileManageUtils.KIB_SIZE);
		long size3 = FileManageUtils.getTotalSizeInternal(FileManageUtils.MIB_SIZE);
		Log.i("INFO", size32 + "");
		Log.i("INFO", size3 + "");
		
		long size35 =	FileManageUtils.getAvailableSizeInternal(FileManageUtils.MIB_SIZE);
		Log.i("INFO", size35 + "");
		
		boolean isHas = FileManageUtils.isHasSpareSpaceInteranal(FileManageUtils.KIB_SIZE, 4761 * 1024);
		Log.i("INFO", isHas + "");
	}
}
