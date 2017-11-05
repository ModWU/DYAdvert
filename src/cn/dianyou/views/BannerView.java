package cn.dianyou.views;

import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.ImageView;
import android.widget.Toast;
import cn.dianyou.listeners.IOnDownloadListener;

public class BannerView extends ImageView {
	
	private Paint mPaint;
	
	private Paint mBannerPaint;
	
	private int width, height;
	
	private float mDensity;
	private float mScaledDensity;
	private int mScreenW, mScreenH;
	
	private boolean isShowDownLoad;
	
	private static final int SHOW_DOWNLOAD_MESSAGE = 0;
	
	private static final int CANCEL_DOWNLOAD_MESSAGE = 1;
	
	private static final int RUNNING_BANNER_MESSAGE = 2;
	
	private static final String DOWNLOAD_CONTENT = "免费下载";
	private static final int DEFAULT_DOWNLOAD_RIGHT_MARGIN = 16;
	private static final int DEFAULT_DOWNLOAD_TEXT_TOPMARGIN = 8;
	private static final int DEFAULT_DOWNLOAD_TEXT_SIZE = 14;
	private static final int DEFAULT_DOWNLOAD_DREGEE = 4;
	private int mDownloadBackColor = Color.parseColor("#32CD32");
	private int mDownloadBoundDregee = DEFAULT_DOWNLOAD_DREGEE;
	private int mDownloadRightMargin = DEFAULT_DOWNLOAD_RIGHT_MARGIN;
	private int mDownloadTextSize = DEFAULT_DOWNLOAD_TEXT_SIZE;
	private int mDownloadTextTopMargin = DEFAULT_DOWNLOAD_TEXT_TOPMARGIN;
	
	private float downloadContainerWidth, downloadContainerHeight;
	
	private float textWidth, textHeight;
	
	private RectF downloadContainerRectF;
	
	private boolean isHasShader;
	
	private boolean isDownGone = false;
	
	
	private int cf = 1;
	
	private IOnDownloadListener mIDownloadListener;
	
	private ViewConfiguration configuration;
	
	public void setDownloadListener(IOnDownloadListener listener) {
		mIDownloadListener = listener;
	}
	
	
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch(msg.what) {
			case SHOW_DOWNLOAD_MESSAGE:
				removeMessages(SHOW_DOWNLOAD_MESSAGE);
				removeMessages(CANCEL_DOWNLOAD_MESSAGE);
				isShowDownLoad = true;
				//invalidate();
				sendEmptyMessageDelayed(CANCEL_DOWNLOAD_MESSAGE, 20000);
				break;
				
			case CANCEL_DOWNLOAD_MESSAGE:
				removeMessages(CANCEL_DOWNLOAD_MESSAGE);
				removeMessages(SHOW_DOWNLOAD_MESSAGE);
				isShowDownLoad = false;
				//invalidate();
				break;
				
			case RUNNING_BANNER_MESSAGE:
				bannerRunValue += (0.01 * cf);
				
				if(bannerRunValue >= 0.5) {
					bannerRunValue = 0.5f;
					cf = -1;
				}
				
				if(bannerRunValue <= 0) {
					bannerRunValue = 0;
					cf = 1;
				}
				
				random = new Random(47);
				invalidate();
				mHandler.sendEmptyMessageDelayed(RUNNING_BANNER_MESSAGE, 25);
				break;
			
			}
			
			
		};
	};
	
	public BannerView(Context context, int width, int height) {
		this(context, null);
		
		
		if(width > 0) {
			this.width = width;
		}
		
		if(height > 0) {
			this.height = height;
		}
		
	}
	
	public BannerView(Context context) {
		this(context, null);
	}
	
	public BannerView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public BannerView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		initScreeanSize(context);
		
		initPaints();

		height = (int) (mScreenH/12 + 5 * mDensity);
		
		mDownloadRightMargin *= mDensity;
		
		mDownloadBoundDregee *= mDensity;
		
		mDownloadTextTopMargin *= mDensity;
		
		textWidth = mPaint.measureText(DOWNLOAD_CONTENT);
		textHeight = mPaint.descent() - mPaint.ascent();
		
		downloadContainerWidth = textWidth * 2;
		downloadContainerHeight = textHeight + mDownloadTextTopMargin * 2;
		
		configuration = ViewConfiguration.get(context);
	}
	
	
	public void go() {
		mHandler.sendEmptyMessage(RUNNING_BANNER_MESSAGE);
	}
	
	private void initPaints() {
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mDownloadTextSize *= mScaledDensity;
		mPaint.setTextSize(mDownloadTextSize);
		
		mBannerPaint = new Paint();
		mBannerPaint.setAntiAlias(true);
		mBannerPaint.setDither(true);
		mBannerPaint.setStyle(Paint.Style.FILL);
		mBannerPaint.setColor(Color.GREEN);
		
		
	}
	
	public void setDownloadBitmap(Bitmap downloadBackBmp) {
		if(downloadBackBmp == null)
			return;
		
		BitmapShader shader = new BitmapShader(downloadBackBmp, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
		mPaint.setShader(shader);
		isHasShader = true;
	}

	private void initScreeanSize(Context context) {
		
		DisplayMetrics outMetrics = context.getResources().getDisplayMetrics();
		
		mScaledDensity = outMetrics.scaledDensity;
		
		mDensity = outMetrics.density;
		
		mScreenW = outMetrics.widthPixels;
		
		mScreenH = outMetrics.heightPixels;
	}
	
	private float mDownX = 0;
	private float mDownY = 0;
	
	private float mMoveMax = 0;
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		switch(event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mDownX = x;
			mDownY = y;
			break;
		
		case MotionEvent.ACTION_MOVE:
			mMoveMax = Math.max(Math.max(Math.abs(x - mDownX), Math.abs(y - mDownY)), mMoveMax);
			break;
		
		case MotionEvent.ACTION_UP:
			if(mMoveMax < configuration.getScaledTouchSlop()) {
				if(!isShowDownLoad)
					mHandler.sendEmptyMessage(SHOW_DOWNLOAD_MESSAGE);
				else
					mHandler.sendEmptyMessage(CANCEL_DOWNLOAD_MESSAGE);
				
				if(isShowDownLoad) {
					if(downloadContainerRectF.contains(event.getX(), event.getY())) {
						
						if(mIDownloadListener != null) {
							mIDownloadListener.download();
						}
						
						if(!isDownGone) {
							Log.i("INFO", "开始下载中...");
							Toast.makeText(BannerView.this.getContext(), "开始下载xxx文件中....", Toast.LENGTH_SHORT).show();
							isDownGone = true;
						} else {
							Log.i("INFO", "已经开始下载了，可以删掉文件再次下载。。。");
							Toast.makeText(BannerView.this.getContext(), "已经下载过了,文件路径:xxx,请删掉文件再重新下载...", Toast.LENGTH_SHORT).show();
						}
					}
				}
			}
			
			
			mDownX = 0;
			mDownY = 0;
			mMoveMax = 0;
			break;
		}
		return true;
	}
	
	private Random random = new Random(47);
	
	private float bannerRunValue = 0f;
	
	@Override
	public void setImageBitmap(Bitmap bm) {
		super.setImageBitmap(bm);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		
		//繪製動畫
		mBannerPaint.setStyle(Paint.Style.FILL);
		canvas.drawPaint(mBannerPaint);
		mBannerPaint.setStyle(Paint.Style.STROKE);
		
		
		canvas.save();
		
		float juli =  height * 1.5f * bannerRunValue;
		
		canvas.translate(0, juli);
		
		mPaint.setStyle(Paint.Style.STROKE);
		
		for(int i = 0; i < 20; i++) {
			
			final float valueW = random.nextFloat();
			
			final float valueH = random.nextFloat();
			
			float alphaTemp =  random.nextFloat();
			
			final float alpha = alphaTemp >= 0.25f ? 0.25f : alphaTemp;
			
			final float lineWidth = random.nextFloat();
			
			mPaint.setColor(Color.argb((int)(255 * alpha), 255, 255, 255));
			
			float strokeWidth = (3 * (lineWidth < 0.2f ? 0.2f : lineWidth))  * mDensity;
			mPaint.setStrokeWidth(strokeWidth);
			
			if(i % 2 == 0) {
				canvas.drawLine(0, height * 1.5f * valueH, width * 1.5f * valueW, height * 1.5f * valueH, mPaint);
		
				canvas.drawLine(width * 1.5f * valueW - strokeWidth/2, height * 1.5f * valueH - strokeWidth/2, width * 1.5f * valueW - strokeWidth/2, 0 - juli, mPaint);
				
			}
			
			if(i % 2 == 1) {
				canvas.drawLine(width - width * 1.5f * valueW, height - height * 1.5f * valueH, width, height - height * 1.5f * valueH, mPaint);
				canvas.drawLine(width - width * 1.5f * valueW  + strokeWidth/2, height - height * 1.5f * valueH  + strokeWidth/2, width - width * 1.5f * valueW, height + juli, mPaint);
			}
			
		}
		
		canvas.restore();
		
		super.onDraw(canvas);
		
		mPaint.setStyle(Paint.Style.FILL);
		
		
		//繪製下載
		if(isShowDownLoad) {
			Log.i("INFO", ".......isshowload?");
			mPaint.setColor(Color.parseColor("#88000000"));
			canvas.drawPaint(mPaint);
			mPaint.setColor(mDownloadBackColor);
			canvas.drawRoundRect(downloadContainerRectF, mDownloadBoundDregee, mDownloadBoundDregee, mPaint);
			if(!isHasShader) {
				mPaint.setColor(Color.WHITE);
				canvas.drawText(DOWNLOAD_CONTENT, downloadContainerRectF.left + (downloadContainerRectF.width() - textWidth)/2, downloadContainerRectF.top + (downloadContainerRectF.height() - (mPaint.ascent() + mPaint.descent()) )/2, mPaint);
			}
		}
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = MeasureSpec.getSize(widthMeasureSpec);
		if(width <= 0) {
			width = this.width;
		}
		
		if(this.width <= 0 || this.width > mScreenW) {
			if(width <= 0) {
				width = mScreenW;
			}
		} else {
			width = this.width;
		}
		setMeasuredDimension(width, height);
		
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		width = w;
		height = h;
		if(height < downloadContainerHeight) {
			float fact = height / downloadContainerHeight;
			
			downloadContainerHeight = height;
			mPaint.setTextSize(height - (int)(8 * fact * mDensity));
			mDownloadTextTopMargin = (int) (4 * fact * mDensity);
			textHeight = mPaint.descent() - mPaint.ascent();
			
			textWidth = mPaint.measureText(DOWNLOAD_CONTENT);
		}
		
		if(width < downloadContainerWidth) {
			float fact = width / downloadContainerWidth;
			
			mDownloadTextSize *= fact;
			
			if(mDownloadTextSize < 10 * mScaledDensity) {
				mDownloadTextSize = (int) (10 * mScaledDensity);
			}
			
			mPaint.setTextSize(mDownloadTextSize);
			textWidth = mPaint.measureText(DOWNLOAD_CONTENT);
			textHeight = mPaint.descent() - mPaint.ascent();
			
			mDownloadRightMargin = 0;
			downloadContainerWidth = width;
		}
		
		
		downloadContainerRectF = new RectF(width - downloadContainerWidth - mDownloadRightMargin, (height - downloadContainerHeight)/2, width - mDownloadRightMargin, (height - downloadContainerHeight)/2 + downloadContainerHeight);
		LinearGradient lg=new LinearGradient(0,0,width,height,Color.parseColor("#AE00AE"),Color.parseColor("#EA7500"),Shader.TileMode.REPEAT);
		mBannerPaint.setShader(lg);
	}

}
