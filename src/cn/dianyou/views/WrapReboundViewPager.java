package cn.dianyou.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class WrapReboundViewPager extends FrameLayout {
	
	private Paint mPaint;
	private Context context;
	private int width, height;
	
	private float mDensity;
	private int mScreenW;
	
	private ReboundViewPager reboundViewPager;
	
	private int viewPagerHeight;
	private int closeDistanceCenterToBottom;
	
	private RectF lineRectF = new RectF();
	
	private RectF lineEnableRectF = new RectF();
	
	//¼ÆËãÏßµÄ
	private int lineLengthHalf = 6;
	
	public WrapReboundViewPager(Context context) {
		this(context, null, null, 0);
	}
	
	public WrapReboundViewPager(Context context, AttributeSet attrs) {
		this(context, null, attrs, 0);
	}

	public WrapReboundViewPager(Context context, ReboundViewPager reboundViewPager, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initPaints();
		obtainScreenInfo(context);
		this.context = context;
		this.reboundViewPager = reboundViewPager;
		
	}
	
	public void clear() {
		if(reboundViewPager != null) {
			reboundViewPager.clear();
			reboundViewPager = null;
			System.gc();
		}
	}
	
	private void obtainScreenInfo(Context context) {
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		mDensity = metrics.density;
		mScreenW = metrics.widthPixels;
	}

	public WrapReboundViewPager(Context context, ReboundViewPager reboundViewPager) {
		this(context, reboundViewPager, null, 0);
	}
	
	public void addReboundViewPager(ReboundViewPager viewPager) {
		reboundViewPager = viewPager;
	}
	
	public ReboundViewPager getReboundViewPager() {
		return reboundViewPager;
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		width = w;
		height = h;
		int count = getChildCount();
		boolean isHasChild = false;
		if(count > 0) {
			View childView = getChildAt(0);
			if(childView instanceof ReboundViewPager) {
				reboundViewPager = (ReboundViewPager) childView;
				isHasChild = true;
			}
		}
		if(reboundViewPager != null) {
			initViewPager();
			if(!isHasChild)
				addView(reboundViewPager);
		}
	}

	private void initViewPager() {
		lineLengthHalf *= mDensity;
		viewPagerHeight = (int) (height / 8.0f * 7);
		lineLengthHalf = (int) (lineLengthHalf + 6 * mDensity * (width * 1.0f/mScreenW));
		closeDistanceCenterToBottom = (int) ((height - viewPagerHeight) * 0.5f + viewPagerHeight);
		lineRectF = new RectF(width/2.0f - lineLengthHalf, closeDistanceCenterToBottom - lineLengthHalf, width/2.0f + lineLengthHalf, closeDistanceCenterToBottom + lineLengthHalf);
		float expendRange = 4 * mDensity;
		lineEnableRectF = new RectF(lineRectF.left - expendRange, lineRectF.top - expendRange, lineRectF.right + expendRange, lineRectF.bottom + expendRange);
		if(reboundViewPager == null)
			reboundViewPager = new ReboundViewPager(context);
		ViewGroup.LayoutParams vlp = reboundViewPager.getLayoutParams();
		vlp.width = ViewGroup.LayoutParams.MATCH_PARENT;
		vlp.height = viewPagerHeight;
		reboundViewPager.setLayoutParams(vlp);
		Log.i("INFO", "initViewPager...");
	}

	private void initPaints() {
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setColor(Color.WHITE);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
	}
	
	@Override
	protected void dispatchDraw(Canvas canvas) {
		
		super.dispatchDraw(canvas);
		mPaint.setStrokeWidth(1);
		if(reboundViewPager != null) {
			float space = 0;
			if(isDownClose) {
				mPaint.setColor(Color.parseColor("#88000000"));
				space = 4 * mDensity;
			} else {
				mPaint.setColor(Color.WHITE);
				space = 0;
			}
			canvas.drawLine(lineRectF.left + space, lineRectF.top + space, lineRectF.right - space, lineRectF.bottom - space, mPaint);
			
			canvas.drawLine(lineRectF.left + space, lineRectF.bottom - space, lineRectF.right - space, lineRectF.top + space, mPaint);
			
			if(isDownClose) {
				mPaint.setColor(Color.parseColor("#88ffffff"));
				float r = (float) Math.sqrt(Math.pow(lineLengthHalf, 2) + Math.pow(lineLengthHalf, 2));
				canvas.drawCircle(lineRectF.centerX(), lineRectF.centerY(), r, mPaint);
			}
		}
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return false;
	}
	
	private boolean isDownClose = false;
	
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		if(event.getAction() == MotionEvent.ACTION_DOWN) {
			if(lineEnableRectF.contains(event.getX(), event.getY())) {
				isDownClose = true;
			}
				
			invalidate();
			
		} else if(event.getAction() == MotionEvent.ACTION_MOVE) {
			
			if(lineEnableRectF.contains(event.getX(), event.getY())) {
				if(!isDownClose) {
					isDownClose = true;
					invalidate();
				}
				
			} else {
				if(isDownClose) {
					isDownClose = false;
					invalidate();
				}
				
			}
			
		} else if(event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
			Log.i("INFO", "up..");
			if(lineEnableRectF.contains(event.getX(), event.getY())) {
				if(mOnCloseListener != null)
					mOnCloseListener.close();
			}
			isDownClose = false;
			invalidate();
		}
		
		return true;
	}
	
	private IOnCloseListener mOnCloseListener;
	public void setOnCloseListener(IOnCloseListener onCloseListener) {
		mOnCloseListener = onCloseListener;
	}
	
	public interface IOnCloseListener {
		void close();
	}

}
