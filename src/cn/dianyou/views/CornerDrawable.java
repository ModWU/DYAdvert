package cn.dianyou.views;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import cn.dianyou.utils.BitmapUtils;

public class CornerDrawable extends Drawable {
	
	private Paint mPaint;
	private Bitmap bmp;
	private RectF rectF;
	
	private int width, height;
	
	public CornerDrawable(Bitmap bmp, int width, int height) {
		this.bmp = BitmapUtils.resizeImage(bmp, width, height);
		this.width = this.bmp.getWidth();
		this.height = this.bmp.getHeight();
		BitmapShader shader = new BitmapShader(this.bmp, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setShader(shader);
	}
	
	public CornerDrawable(Bitmap bmp) {
		this.bmp = bmp;
		this.width = bmp.getWidth();
		this.height = bmp.getHeight();
		BitmapShader shader = new BitmapShader(bmp, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setShader(shader);
		
	}
	
	public CornerDrawable(int color, int width, int height) {
		mPaint.setColor(color);
	}
	
	private float roundDegree;
	public void setRoundDegree(float degree) {
		roundDegree = degree;
	}

	@Override
	public void draw(Canvas canvas) {
		canvas.drawRoundRect(rectF, roundDegree, roundDegree, mPaint);
	}
	
	@Override
	public int getIntrinsicHeight() {
		return height;
	}
	
	@Override
	public int getIntrinsicWidth() {
		return width;
	}

	@Override
	public void setAlpha(int alpha) {
		mPaint.setAlpha(alpha);
	}

	@Override
	public void setColorFilter(ColorFilter cf) {
		mPaint.setColorFilter(cf);
	}

	@Override
	public int getOpacity() {
		return PixelFormat.TRANSLUCENT;
	}
	
	@Override
	public void setBounds(int left, int top, int right, int bottom) {
		//super.setBounds(left, top, right, bottom);
		rectF = new RectF(left, top, right, bottom);
	}

}
