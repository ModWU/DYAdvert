package cn.dianyou.views;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.ImageView;
import cn.dianyou.listeners.IOnDownloadListener;
import cn.dianyou.views.SpotView.AnimationBitmap;

public class SpotSpreadView extends ImageView {
	
	//private Paint back;
	
	private static final int DEFAULT_ROUND_DEGREE = 3;
	
	protected static final int DEFAULT_TEXT_SIZE = 16;
	
	private static final int DEFAULT_TEXT_MARGIN_TB = 10;
	
	
	private static final String TEXT_DOWN_CONTENT = "¡¢º¥œ¬‘ÿ";
	
	protected float density;
	
	protected float scaledDensity;
	
	
	private boolean isCircleWave;
	
	private boolean isClickUp = false;
	
	private PointF circleRadius = null;
	
	private float cValue = 0;
	
	protected float mTextMarginTB = DEFAULT_TEXT_MARGIN_TB;
	
	protected Paint mPaint;
	
	private float minRadius, maxRadius;
	
	protected int roundDegree = DEFAULT_ROUND_DEGREE;
	
	private int waveA, waveR, waveG, waveB; 
	
	private float mDownTextContainerWidth, mDownTextContainerHeight;
	private RectF mDownTextContainerRectF;
	
	private float mDownTextWidth, mDownTextHeight;
	
	private int mDownTextSize = DEFAULT_TEXT_SIZE - 2;
	
	private int radomColorFac = 0;
	
	protected int width, height;
	
	private static final int START_CICLE_WAVE_MESSAGE = 0;
	private static final int SHOW_PART_ANIMATION_MESSAGE = 2;
	
	protected Rect imageClipRect;
	
	private Handler mHandler = new Handler(Looper.getMainLooper()) {
		public void handleMessage(android.os.Message msg) {
			switch(msg.what) {
			case START_CICLE_WAVE_MESSAGE:
				if(cValue <= 1) {
					mHandler.sendEmptyMessageDelayed(START_CICLE_WAVE_MESSAGE, 10);
					invalidate();
				} else {
					mHandler.removeMessages(START_CICLE_WAVE_MESSAGE);
					isCircleWave = false;
				}
				break;
				
			case SHOW_PART_ANIMATION_MESSAGE:
				alertA *= -1;
				animValue = animValue + 0.3f * alertA;
				invalidate();
				mHandler.sendEmptyMessageDelayed(SHOW_PART_ANIMATION_MESSAGE, 200);
				
				break;
				
			}
		
		};
	
	};
	

	
	private IOnDownloadListener mOnDownloadListener;
	
	public void setOnDownloadListener(IOnDownloadListener listener) {
		mOnDownloadListener = listener;
	}

	public SpotSpreadView(Context context) {
		this(context, null);
		
	}
	
	
	private Bitmap srcBitmap;
	
	@Override
	public void setImageBitmap(Bitmap bm) {
		super.setImageBitmap(bm);
		srcBitmap = bm;
	}
	
	public void clear() {
		if(srcBitmap != null) {
			srcBitmap.recycle();
			srcBitmap = null;
		}
		
		if(animationBitmaps != null) {
			for(AnimationBitmap ab : animationBitmaps) {
				if(ab.animBitmap != null) {
					ab.animBitmap.recycle();
				}
			}
			animationBitmaps = null;
		}
		System.gc();
	}
	
	public SpotSpreadView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		
	}
	
	
	public SpotSpreadView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initScreeanSize(context);
		
		mTextMarginTB *= density;
		roundDegree *= density;
		mDownTextSize *= scaledDensity;
		
		initPaints();
		
		mDownTextWidth = mPaint.measureText(TEXT_DOWN_CONTENT);
		mDownTextHeight = mPaint.descent() - mPaint.ascent();
		mDownTextContainerWidth = mDownTextWidth * 2f;
		mDownTextContainerHeight = mDownTextHeight + mTextMarginTB * 1.5f;
		
		randomColorFac();
		
		
		
	}
	
	private void randomColorFac() {
		radomColorFac = (int) (Math.random() * 256);
		waveA = (int) (Math.random() * (121 - 80) + 80);
		waveR = (int)(Math.random() * (256 - radomColorFac) + radomColorFac);
		waveG = (int)(Math.random() * (256 - radomColorFac) + radomColorFac);
		waveB = (int)(Math.random() * (256 - radomColorFac) + radomColorFac);
	}
	
	private void initPaints() {
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setStyle(Paint.Style.FILL);
		mPaint.setTextSize(mDownTextSize);
		
	}
	
	private void initScreeanSize(Context context) {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		
		scaledDensity = outMetrics.scaledDensity;
		
		density = outMetrics.density;
	}
	
	public void newData() {
		randomColorFac();
		isClickEnable = false;
		isDownDownloadRect = false;
		isDownCircleWave = false;
		isCircleWave = false;
		isClickUp = false;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		onDrawSomeAnimation(canvas);
		if(isCircleWave) {
			if(circleRadius != null) {
				cValue += 0.06;
				if(cValue > 1) 
					cValue = 1;
				
				//Log.i("INFO", "............................." + this.canvas);
				if(canvas != null) {
					mPaint.setColor(Color.argb(waveA, waveR, waveG, waveB));
					canvas.drawCircle(circleRadius.x, circleRadius.y, minRadius + (maxRadius - minRadius) * cValue, mPaint);
					Log.i("INFO", "draw cicle...");
				}
				
			}
			
			if(cValue >= 1) {
				isCircleWave = false;
			}
		}
		
		
		if(isClickUp) {
			mPaint.setColor(Color.parseColor("#88000000"));
			canvas.drawPaint(mPaint);
			
			mPaint.setColor(Color.RED);
			
			canvas.drawRoundRect(mDownTextContainerRectF, roundDegree, roundDegree, mPaint);
			
			mPaint.setColor(Color.WHITE);
			canvas.drawText(TEXT_DOWN_CONTENT, mDownTextContainerRectF.left + (mDownTextContainerRectF.width() - mDownTextWidth)/2, mDownTextContainerRectF.top - mPaint.ascent() + mTextMarginTB*1.5f/2, mPaint);
			
			
		}
	}
	
	
	private float animValue = 0.0f;
	private int alertA = -1;
	
	private AnimationBitmap[] animationBitmaps;
	private Rect animbpRect;
	private RectF animbpClipRect;
	
	private void onDrawSomeAnimation(Canvas canvas) {
		if(animationBitmaps == null || animbpRect == null || animbpClipRect == null || animationBitmaps.length == 0  ||  canvas == null)
			return;
		
		for(AnimationBitmap bp : animationBitmaps) {
			if(bp.animBitmap == null) continue;
			animbpRect.set(0, 0, bp.animBitmap.getWidth(), bp.animBitmap.getHeight());
			
			float tempAnimValue = animValue;
			
			if(!bp.isAnimation) {
				tempAnimValue = 1.0f;
			}
			
			if(bp.isCenter) {
				animbpClipRect.set(bp.left + bp.width/2 - bp.width/2 * tempAnimValue + getGap(), bp.top + bp.height/2 - bp.height/2 * tempAnimValue + getGap(), bp.left + bp.width/2 + bp.width/2 * tempAnimValue + getGap(), bp.top + bp.height/2 + bp.height/2 * tempAnimValue + getGap());
			}
			else 
				animbpClipRect.set(bp.left + getGap(), bp.top + bp.height/2 - bp.height/2 * tempAnimValue + getGap(), bp.left + bp.width * tempAnimValue + getGap(), bp.top + bp.height/2 + bp.height/2 * tempAnimValue + getGap());
			int index_2 = 0;
			if(bp.animDregee != 0) {
				index_2 = canvas.save();
				//float translateX = animbpClipRect.left + animbpClipRect.width()/2.0f;
				//float translateY = animbpClipRect.top + animbpClipRect.height()/2.0f;
				float translateX = bp.rotatePoint.x + getGap();
				float translateY = bp.rotatePoint.y + getGap();
					
				canvas.translate(translateX, translateY);
				canvas.rotate(bp.animDregee);
				//animbpClipRect.set(-animbpClipRect.width()/2.0f, -animbpClipRect.height()/2.0f, animbpClipRect.width()/2.0f, animbpClipRect.height()/2.0f);
				animbpClipRect.set(animbpClipRect.left - translateX, animbpClipRect.top - translateY, animbpClipRect.right - translateX, animbpClipRect.bottom - translateY);
			}
			canvas.drawBitmap(bp.animBitmap, animbpRect, animbpClipRect, mPaint);
			
			if(bp.animDregee != 0) {
				canvas.restoreToCount(index_2);
			}
		}
		
	}
	
	public void showPartAnimation(long delayMillis) {
		if(animationBitmaps == null || animationBitmaps.length <= 0) {
			return;
		}
		alertA = -1;
		animValue = 1.0f;
		mHandler.sendEmptyMessageDelayed(SHOW_PART_ANIMATION_MESSAGE, delayMillis);
	}
	
	public void showPartAnimation() {
		showPartAnimation(0);
	}
	
	public void setAnimationBitmap(AnimationBitmap[] animationBitmaps) {
		if(animationBitmaps != null && animationBitmaps.length > 0) {
			if(animbpRect == null)
				animbpRect = new Rect();
			if(animbpClipRect == null)
				animbpClipRect = new RectF();
		}
		this.animationBitmaps = animationBitmaps;
	}
	
	public void cancelPartAnimation() {
		mHandler.removeMessages(SHOW_PART_ANIMATION_MESSAGE);
	}
	
	public int getGap() {
		return 0;
	}
	
	private boolean isDownCircleWave = false;
	private boolean isDownDownloadRect = false;
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(isClickEnable) {
			return super.onTouchEvent(event);
		}
		int action = event.getAction();
		switch(action) {
		case MotionEvent.ACTION_DOWN:
			Log.i("INFO", "down....");
			if(!mDownTextContainerRectF.contains(event.getX(), event.getY())) {
				if(acessCircleWaveRect != null && acessCircleWaveRect.contains(event.getX(), event.getY())) {
					circleRadius = new PointF(event.getX(), event.getY());
					startCircleWave();
					isDownCircleWave = true;
				}
				Log.i("INFO", "down....2");
			}
			
			if(isClickUp) {
				if(mDownTextContainerRectF.contains(event.getX(), event.getY())) {
					isDownDownloadRect = true;
				}
			}
			break;
		
		case MotionEvent.ACTION_MOVE:
			
			break;
		
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			if(isClickUp) {
				if(isDownDownloadRect && mDownTextContainerRectF.contains(event.getX(), event.getY())) {
					isCircleWave = false;
					mHandler.removeMessages(START_CICLE_WAVE_MESSAGE);
					//if(!isDownStart) {
					isClickUp = false;
					
					//}
					
					if(mOnDownloadListener != null) {
						boolean isDownloadFinished = mOnDownloadListener.download();
						if(isDownloadFinished) {
							isClickUp = true;
						}
					}
					
					invalidate();
					
				}
			} else {
				
				if(isDownCircleWave && acessCircleWaveRect != null && acessCircleWaveRect.contains(event.getX(), event.getY())) {
					isClickUp = true;
					invalidate();
				}
				else {
					if(isDownCircleWave && new RectF(0, 0, width, height).contains(event.getX(), event.getY())) {
						isClickUp = true;
						invalidate();
					}
				}
			}
			
			
			isDownCircleWave = false;
			isDownDownloadRect = false;
			break;
		
		}
		return true;
	}
	
	
	
	
	protected volatile boolean isClickEnable = false;
	
	public synchronized void setClickEnable(boolean isEnable) {
		this.isClickEnable = isEnable;
	}
	
	public boolean isClickEnable() {
		return isClickEnable;
	}
	
	
	private void startCircleWave() {
		mHandler.removeMessages(START_CICLE_WAVE_MESSAGE);
		cValue = 0;
		isCircleWave = true;
		mHandler.sendEmptyMessage(START_CICLE_WAVE_MESSAGE);
	}
	
	
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		this.width = w;
		this.height = h;
		maxRadius = w/3.0f/2;
		minRadius = maxRadius/10;
		
		mDownTextContainerRectF = new RectF((width - mDownTextContainerWidth)/2, (height - mDownTextContainerHeight)/2, (width - mDownTextContainerWidth)/2 + mDownTextContainerWidth, (height - mDownTextContainerHeight)/2 + mDownTextContainerHeight);
		
		getAccessCircleWaveRect(new RectF(0, 0, width, height));
	}
	
	private RectF acessCircleWaveRect = null;
	
	protected void getAccessCircleWaveRect(RectF rect) {
		acessCircleWaveRect = rect;
	}
	
	//protected void circleRound()

}
