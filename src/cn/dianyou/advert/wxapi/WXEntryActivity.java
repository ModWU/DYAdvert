package cn.dianyou.advert.wxapi;

import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import cn.dianyou.advert.R;
import net.youmi.android.offers.OffersManager;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
	
	//@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		// 添加有米的处理方法
	    OffersManager.getInstance(this).handleIntent(getIntent());
	    
	    
		
		setContentView(R.layout.activity_weixin);
		
		 Toast.makeText(this, "成功进入WXEntryActivity类里", 0).show();
		
        
        Log.i("INFO", "WXEntryActivity-->onCreate");
       // Toast.makeText(this, "成功进入WXEntryActivity类里", 0).show();
	}
	
	//@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		
		// 添加有米的处理方法
		OffersManager.getInstance(this).handleIntent(getIntent());
        
        Log.i("INFO", "WXEntryActivity-->onNewIntent");
        Toast.makeText(this, "成功进入WXEntryActivity类里", 0).show();
	}

	@Override
	public void onReq(BaseReq req) {
		
		 Toast.makeText(this, "分享请求...", Toast.LENGTH_LONG).show();  
	}

	@Override
	public void onResp(BaseResp resp) {
		 switch (resp.errCode) {  
	        case BaseResp.ErrCode.ERR_OK:  
	             
	            Toast.makeText(this, "发送成功", Toast.LENGTH_LONG).show();  
	            finish();  
	            break;  
	        case BaseResp.ErrCode.ERR_USER_CANCEL:  
	             
	            Toast.makeText(this, "分享取消", Toast.LENGTH_LONG).show();  
	            finish();  
	            break;  
	        case BaseResp.ErrCode.ERR_AUTH_DENIED:  
	            Toast.makeText(this, "分享被拒绝", Toast.LENGTH_LONG).show();  
	            finish();  
	            break;  
	        default:  
	            Toast.makeText(this, "分享返回", Toast.LENGTH_LONG).show();  
	            break;  
	        }  
		
	}

}
