package cn.dianyou.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import cn.dianyou.utils.OtherUtils;

public class RoundCornerImageView extends ImageView {
	
	private Path clipPath;
	
	private RectF rectF;
	
	// private PaintFlagsDrawFilter pfd;  
	
	private float degree;
	
	public RoundCornerImageView(Context context) {
		this(context, null);
	}
	
	public RoundCornerImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public RoundCornerImageView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		clipPath = new Path();
		OtherUtils.manageLayer(this, false);
		// pfd = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);      
	}
	
	public void setRoundDegree(float degree) {
		this.degree = degree;
	}
	
	public void clear() {
		if(srcBitmap != null) {
			srcBitmap.recycle();
			srcBitmap = null;
			System.gc();
		}
	}
	
	private Bitmap srcBitmap;
	
	@Override
	public void setImageBitmap(Bitmap bm) {
		super.setImageBitmap(bm);
		srcBitmap = bm;
	}
	
	
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		rectF = new RectF(0, 0, w, h);
		
		verfityAccessHardware();
	}
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void verfityAccessHardware() {
		if (Build.VERSION.SDK_INT >= 11) {
			setLayerType(View.LAYER_TYPE_NONE, null);
			isAccessHardware = getLayerType() == View.LAYER_TYPE_HARDWARE ? true : false;
		}
	}
	
	boolean isAccessHardware = false;
	
	@Override
	protected void onDraw(Canvas canvas) {
		
		boolean isSuperCanvas = false;
		
		try {
			if(!isAccessHardware) {
				clipPath.addRoundRect(rectF, degree, degree, Path.Direction.CCW);
				canvas.clipPath(clipPath);
				isSuperCanvas = true;
			}
		} catch(Exception e) {
			Log.i("INFO", "clip fail");
			isSuperCanvas = false;
		}
		if(isSuperCanvas) {
			super.onDraw(canvas);
		} else {
			super.onDraw(canvas);
		}
		
		
	}
	
	

}
