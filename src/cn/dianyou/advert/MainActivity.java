package cn.dianyou.advert;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.UUID;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import cn.dianyou.advert.adConfig.YouMiConfig;
import cn.dianyou.advert.adEntry.YouMiAdvert;
import cn.dianyou.advert.adEntry.YouMiAdvert.BannerAds;
import cn.dianyou.advert.adEntry.YouMiAdvert.IntegralWallAds;
import cn.dianyou.advert.adEntry.YouMiAdvert.SpotAds;
import cn.dianyou.advert.adEntry.YouMiAdvert.SpotAds.SpotStateListener;
import cn.dianyou.advert.adFactory.AdvertFactory;
import cn.dianyou.advert.adFactory.SDKAdvertFactory;
import cn.dianyou.advert.adFactory.AdvertType.SDKType;
import cn.dianyou.advert.adIntegral.YouMiIntegral.ClientPointState;
import cn.dianyou.advert.adIntegral.YouMiIntegral.PointState;
import cn.dianyou.nets.DYHttpUtils;
import cn.dianyou.nets.DYStringCallback;
import net.youmi.android.offers.EarnPointsOrderList;
import net.youmi.android.offers.OffersManager;
import net.youmi.android.spot.CustomerSpotView;
import net.youmi.android.spot.SpotManager;
import okhttp3.Call;
import okhttp3.OkHttpClient;

public class MainActivity extends Activity {
	
	YouMiAdvert youMiAdvert = null;
	LinearLayout adLayout = null;
	
	private static final String WEIXI_APPID = "wx6a9a4e673d842603";
	private static final String WEIXI_APPSECRET = "82c7564030f2a86bb8930852de07c2dc";
	
	
	private LinearLayout t1;
	private LinearLayout t2;
	
	private ImageView imageView;
	
	 //判定当前的屏幕是竖屏还是横屏
    public int ScreenOrient(Activity activity)
    {
        int orient = activity.getRequestedOrientation(); 
        if(orient != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE && orient != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            WindowManager windowManager = activity.getWindowManager();  
            Display display = windowManager.getDefaultDisplay();  
            int screenWidth  = display.getWidth();  
            int screenHeight = display.getHeight();  
            orient = screenWidth < screenHeight ?  ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE:ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        }
        return orient;
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
        
       
		setContentView(R.layout.main2);
		
		imageView = (ImageView) findViewById(R.id.id_img);
		
		
		SDKAdvertFactory factory = AdvertFactory.newInstance(this, SDKAdvertFactory.class);
		youMiAdvert = (YouMiAdvert) factory.obtainAdvert(SDKType.YouMi, YouMiConfig.Builder.create().setAppId("4901301d3f55b9d0").setAppSecret("4f7f7b7de27611a6").build(), true);
		youMiAdvert.init();
		
		t1 = (LinearLayout) findViewById(R.id.t1);
		
		t2 = (LinearLayout) findViewById(R.id.t2);
		
		 if(savedInstanceState != null){
	            //横屏
	            if( ScreenOrient(this)==ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE  ) {
	               Log.i("INFO", "横屏");
	               youMiAdvert.createSpotAds().recovery(t1, t2);
	               youMiAdvert.createBannerAds().recovery(this);
	            }
	            //竖屏
	            if( ScreenOrient(this)==ActivityInfo.SCREEN_ORIENTATION_PORTRAIT  ) {
	            	 Log.i("INFO", "竖屏");
	            	
	            	 youMiAdvert.createSpotAds().recovery(t1, t2);
	            	 youMiAdvert.createBannerAds().recovery(this);
	            }
	            
	       }
			
		
		 youMiAdvert.createSpreadAds(MainActivity.class).init();
		 iwa = youMiAdvert.createIntegralWallAds();
		 
		 String signatureMd5String = OffersManager.getInstance(this).getSignatureMd5String();
		 Log.i("INFO", "md5:" + signatureMd5String);
		
		/*SpotManager.getInstance(this).loadSpotAds();
		SpotManager.getInstance(this).setSpotOrientation(SpotManager.ORIENTATION_LANDSCAPE);
		SpotManager.getInstance(this).showSpotAds(this);*/
		
		/*AdView adView = new AdView(this, AdSize.FIT_SCREEN);
		LinearLayout adLayout = (LinearLayout) findViewById(R.id.id_ll);
		adLayout.addView(adView);*/
		 
		 dyHttpUtils = DYHttpUtils.initClient(new OkHttpClient());
	}
	
	
	
	@SuppressLint("NewApi")
	@Override
	public void onBackPressed() {
		if(!SpotManager.getInstance(this).disMiss()) {
			super.onBackPressed();
		}
		
	}
	
	@Override
	protected void onDestroy() {
		
		youMiAdvert.destroy();
		Log.i("INFO", "銷毀");
		super.onDestroy();
	}
	
	public void onDes(View view) {
		youMiAdvert.destroy();
	}
	
	public static int i = 0;
	public void onShow1(View view) {
		SpotAds s = youMiAdvert.createSpotAds();
		String key = ++i + "";
		Log.i("INFO", "key:" + key);
		s.init();
		s.showToViewGroup(t1, null, new SpotStateListener() {

			@Override
			public void onShowFailed() {
				Log.i("INFO", "onShowFailed");
				
			}

			@Override
			public void onShowSuccess() {
				Log.i("INFO", "onShowSuccess");
				
			}

			@Override
			public void onSpotClick(boolean arg0) {
				Log.i("INFO", "onSpotClick");
				
			}

			@Override
			public void onSpotClosed() {
				Log.i("INFO", "onSpotClosed");
				
			}

			@Override
			public void ObtainSpotView(CustomerSpotView spotView) {
				Log.i("INFO", "ObtainSpotView");
				
			}
			
		});
		
		
		s.init();
		s.showToViewGroup(t2, null, new SpotStateListener() {

			@Override
			public void onShowFailed() {
				Log.i("INFO", "onShowFailed2");
				
			}

			@Override
			public void onShowSuccess() {
				Log.i("INFO", "onShowSuccess2");
				
			}

			@Override
			public void onSpotClick(boolean arg0) {
				Log.i("INFO", "onSpotClick2");
				
			}

			@Override
			public void onSpotClosed() {
				Log.i("INFO", "onSpotClosed2");
				
			}

			@Override
			public void ObtainSpotView(CustomerSpotView spotView) {
				Log.i("INFO", "ObtainSpotView2");
				
			}
			
		});
		
	}
	public void onShow2(View view) {
		SpotAds s = youMiAdvert.createSpotAds();
		String key = UUID.randomUUID().toString();
		s.init();
		s.show(null);
		
	}
	
	public void onShowAll(View view) {
		/*SpotAds s = youMiAdvert.createSpotAds();
		s.create().showToViewGroup(t1);
		s.showToViewGroup(t2);*/
		
		//youMiAdvert.createBannerAds().create().showAtActivity(this, Gravity.BOTTOM);
		//youMiAdvert.createIntegralWallAds().create().setTitleText("吴超超").setPointsVisibility(false).showDialog(this);
		IntegralWallAds ad = youMiAdvert.createIntegralWallAds();
		ad.init();
		boolean isSccuss = ad.setShareChannel(IntegralWallAds.CHANNEL_WEIXI, WEIXI_APPID);
		Log.i("INFO", "isSuccess:" + isSccuss);
		ad.showShareWall();
		
	}
	
	IntegralWallAds iwa = null;
	public void onAddIntegral(View view) {
		ClientPointState cps = ClientPointState.build(this, new PointState() {
			
			@Override
			public void unRegisterAll() {
				Log.i("INFO", "unRegisterAll");
				Toast.makeText(MainActivity.this, "unRegisterAll", 0).show();
			}
			
			@Override
			public void onViewPoints(Context context) {
				Log.i("INFO", "onViewPoints");
				Toast.makeText(MainActivity.this, "onPointBalanceChange-onViewPoints", 0).show();
			}
			
			@Override
			public void onPointBalanceChange(float pointsBalance) {
				Log.i("INFO", "onPointBalanceChange:" + pointsBalance);
				Toast.makeText(MainActivity.this, "onPointBalanceChange-pointsBalance:" + pointsBalance, 0).show();
			}
			
			@Override
			public void onEarnPoints(Context context, EarnPointsOrderList list) {
				Toast.makeText(MainActivity.this, "onEarnPoints", 0).show();
			}
		}).buildStateChangeNotify().buildStateEarnNotify().buildStateReceiver("4901301d3f55b9d0");
		
		float points = iwa.init(cps.register()).queryPoints();
		Log.i("INFO", "points:" + points);
		boolean flag = iwa.addPoints(23);
		Log.i("INFO", "add success?" + flag);
		float afPoints = iwa.queryPoints();
		
		Log.i("INFO", "afPoints:" + afPoints);
	}
	
	public void onSpreadAds(View view) {
		youMiAdvert.createSpotAds().setSpotAnimationType(SpotAds.SPOT_ORIENTATION_PORTRAIT);
		youMiAdvert.createSpreadAds(MainActivity.class).init();
		youMiAdvert.show();
	}
	private static String url = "http://10.0.3.2:8080/HttpWeb/MyServlet";
	private static String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "aaaaaaaaaa.jpg";
	private DYHttpUtils dyHttpUtils = null;
	public void onSendRequest(View view) throws UnsupportedEncodingException {
		/*DYHttpUtils.get().url(url).addParams("name", URLEncoder.encode("中国", "utf-8")).addParams("age", URLEncoder.encode("23", "utf-8")).build().execute(new DYFileCallBack(Environment.getExternalStorageDirectory().getAbsolutePath(), "aaaaaaaaaa.jpg") {
			@Override
			public void inProgress(float progress, long total, int id) {
				Log.i("INFO", "progress:" + progress + ", total:" + total);
				Log.i("INFO", "Thread:" + Thread.currentThread().toString());
			}
			
			
			@Override
			public void onResponse(File response, int id) {
				Log.i("INFO", "filepath:" + response.getAbsolutePath());
				imageView.setImageBitmap(BitmapFactory.decodeFile(response.getAbsolutePath()));
				
			}
			
			@Override
			public void onError(Call call, Exception e, int id) {
				// TODO Auto-generated method stub
				
			}
		});*/
		
		/*DYHttpUtils.postFile().url(url).file(new File(filePath)).build().execute(new DYStringCallback() {
			@Override
			public void inProgress(float progress, long total, int id) {
				Log.i("INFO", "progress:" + progress + ", total:" + total);
			}
			
			@Override
			public void onResponse(String response, int id) {
				Log.i("INFO", "response:" + response);
				
			}
			
			@Override
			public void onError(Call call, Exception e, int id) {
				// TODO Auto-generated method stub
				
			}
		});*/
		
		String key = UUID.randomUUID().toString();
		youMiAdvert.createBannerAds(t1).init(key).setBannerSize(BannerAds.BANNER_SIZE_468x60).show(key);
		
		Log.i("INFO", "广告条");
	}
}
