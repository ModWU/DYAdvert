package cn.dianyou.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.nineoldandroids.view.ViewHelper;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import cn.dianyou.utils.OtherUtils;

public class ReboundViewPager extends ViewPager {
	
	private List<View> allAdViews = new ArrayList<View>();
	
	private Paint mPaint;
	
	
	/*private Bitmap bmpCircle;
	
	private Canvas bmpCanvas;*/
	
	private float mDensity;
	private float mScreenHeight;
	private float mScreenWidth;
	
	private boolean mFadeEnabled = false;
	
	private boolean isIndexDot = false;
	
	private State mState;
	
	private int oldPage;

	private View mLeft;
	private View mRight;
	private float mTrans;
	private float mScale;
	
	private HashMap<Integer, Object> mObjs = new LinkedHashMap<Integer, Object>();
	
	private TransitionEffect mEffect = TransitionEffect.Stack;

	private static final float SCALE_MAX = 0.5f;
	
	private enum State
	{
		IDLE, GOING_LEFT, GOING_RIGHT
	}
	
	public enum TransitionEffect
	{
		None, Stack, RotateDown
	}
	
	public void setTransitionEffect(TransitionEffect mEffect) {
		this.mEffect = mEffect;
	}
	
	
	class ReboundPagerAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return allAdViews.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}
		
		@Override
		public Object instantiateItem(ViewGroup container, final int position) {
			View view = allAdViews.get(position);
			container.addView(view);
			setObjectForPosition(view, position);
			return view;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object obj)
		{
			container.removeView(findViewFromObject(position));
		}
		
	}
	
	@Override
	public void setCurrentItem(int item) {
		mState = State.GOING_LEFT;
		super.setCurrentItem(item);
		
	}
	
	private boolean isBootAutoGo = false;
	
	
	
	private Timer timer = null;
	private boolean isTurnn = false;
	private long firstDelay, delay;
	private int fac = 1;
	
	
	public void setBootAutoGo(boolean isBoot) {
		isBootAutoGo = isBoot;
	}
	
	public synchronized void autoGo(long firstDelay, long delay, boolean isTurn) {
		
		if(!isBootAutoGo) {
			return;
		}
		
		if(allAdViews != null) {
			if(allAdViews.size() <= 1) 
				return;
		}
		
		if(timer != null)
			timer.cancel();
		
		Log.i("chaochao", "onPageSelected...autoGo in..");
		isTurnn = isTurn;
	
		timer = new Timer("ViewPager auto run timer");
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				synchronized (timer) {
					new Handler(Looper.getMainLooper()).post(new Runnable() {
						
						@Override
						public void run() {
							if(allAdViews == null)
								return;
							if(allAdViews.size() <= 1) {
								timer.cancel();
								return;
							}
							
							/*if(isTurnn) {
								if(ReboundViewPager.this.getCurrentItem() == allAdViews.size() - 1) {
									ReboundViewPager.this.setCurrentItem(ReboundViewPager.this.getCurrentItem() - 1);
									fac = -1;
								} else if(ReboundViewPager.this.getCurrentItem() == 0) {
									ReboundViewPager.this.setCurrentItem(ReboundViewPager.this.getCurrentItem() + 1);
									fac = 1;
								} else {
									ReboundViewPager.this.setCurrentItem(ReboundViewPager.this.getCurrentItem() + fac);
								}
							} else {
								ReboundViewPager.this.setCurrentItem((ReboundViewPager.this.getCurrentItem() + 1) % allAdViews.size());
							}*/
							if(isTurnn) {
								ReboundViewPager.this.setCurrentItem((ReboundViewPager.this.getCurrentItem() + fac) % allAdViews.size());
							} else {
								Log.i("INFO", "fac:" + fac);
								if(fac == 1) {
									ReboundViewPager.this.setCurrentItem((ReboundViewPager.this.getCurrentItem() + fac) % allAdViews.size());
								} else {
									if(ReboundViewPager.this.getCurrentItem() == 0) {
										ReboundViewPager.this.setCurrentItem(allAdViews.size() - 1);
									} else {
										ReboundViewPager.this.setCurrentItem((ReboundViewPager.this.getCurrentItem() + fac) % allAdViews.size());
									}
									
								}
							}
							
							
						}
						
					});
				}
			}
				
		}, firstDelay, delay);
		
		this.firstDelay = firstDelay;
		this.delay = delay;
	}
	
	
	
	
	public synchronized void stopAutoGo() {
		if(isBootAutoGo && timer != null) {
			timer.cancel();
			timer = null;
		}
	}
	
	
	public ReboundViewPager(Context context) {
		this(context, null);
	}

	private void obtainDesity(Context context) {
		DisplayMetrics merics = context.getResources().getDisplayMetrics();
		mDensity = merics.density;
		mScreenHeight = merics.heightPixels;
		mScreenWidth = merics.widthPixels;
		
	}

	private void initPaints() {
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setStyle(Paint.Style.FILL);
		
		
	}
	
	

	
	@SuppressWarnings("deprecation")
	public ReboundViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		initPaints();
		obtainDesity(context);
		setClipChildren(false);
		setFadeEnabled(true);
		ReboundPagerAdapter adapter = new ReboundPagerAdapter();
		setAdapter(adapter);
		
		//if(Build.VERSION.SDK_INT >= 4) {
			/*addOnPageChangeListener(new SimpleOnPageChangeListener() {
				@Override
				public void onPageScrollStateChanged(int state) {
					super.onPageScrollStateChanged(state);
					
				}
			});*/
		//} else {
			setOnPageChangeListener(new OnPageChangeListener() {
				
				private int lastPosition = 0;
				
				private boolean isDragger = false;
				
				@Override
				public void onPageSelected(int position) {
					
					if(isBootAutoGo) {
						if(isTurnn) {
							if(ReboundViewPager.this.getCurrentItem() == allAdViews.size() - 1) {
								fac = -1;
							} else if(ReboundViewPager.this.getCurrentItem() == 0) {
								fac = 1;
							} else {
								if(position >= lastPosition) {
									fac = 1;
								} else {
									fac = -1;
								}
							}
							
							
						} else {
							if(position != allAdViews.size() - 1 && position != 0) {
								if(position >= lastPosition) {
									fac = 1;
								} else {
									fac = -1;
								}
							}
						}
					}
						
					lastPosition = position;
					
					
					
				}
				
				@Override
				public void onPageScrolled(int arg0, float arg1, int arg2) {
					
				}
				
				
				@Override
				public void onPageScrollStateChanged(int state) {
					
					if(isBootAutoGo) {
						if(state == ViewPager.SCROLL_STATE_DRAGGING) {
							stopAutoGo();
							isDragger = true;
							Log.i("chaochao", "SCROLL_STATE_DRAGGING..");
						}
						
						if(state == ViewPager.SCROLL_STATE_IDLE) {
							if(isDragger && timer == null) {
								Log.i("chaochao", "onPageSelected...autoGo");
								autoGo(firstDelay, delay, isTurnn);
								isDragger = false;
							}
							
							Log.i("chaochao", "SCROLL_STATE_IDLE..");
						}
					}
					
					if(mEffect == TransitionEffect.None) {
						return;
					}
					//此处主要是用来防止最后没有完全偏移
					if(state == ViewPager.SCROLL_STATE_IDLE) {
						mLeft = findViewFromObject(getCurrentItem());
						mRight = findViewFromObject(getCurrentItem() + 1);
						
						if(mEffect == TransitionEffect.Stack) {
							mScale = (1 - SCALE_MAX) * 1.0f + SCALE_MAX;
							Log.i("INFO", "scrollX:" + getScrollX());
							Log.i("INFO", "mTrans:" + mTrans);
							ViewHelper.setScaleX(mLeft, mScale);
							ViewHelper.setScaleY(mLeft, mScale);
						} else if(mEffect == TransitionEffect.RotateDown) {
							animateRotate(mLeft, mRight, 0.0f, false);
						}
						
						mTrans = -width - getPageMargin() + width;
						ViewHelper.setTranslationX(mLeft, mTrans);
						scrollTo(getCurrentItem() * width, 0);
						Log.i("INFO", "INFO...end");
					}
					
					
					
					
					
				}
			});
		//}
		
	}
	
	public void setPagerViews(List<View> views) {
		if(views != null && !views.isEmpty()) {
			allAdViews.clear();
			allAdViews.addAll(views);
			if(mOnPagerItemListener != null) {
				for(int i = 0; i < allAdViews.size(); i++) {
					//mOnPagerItemClickListener
					final int viewPosition = i;
					allAdViews.get(i).setOnClickListener(new OnClickListener() {
	
						@Override
						public void onClick(View v) {
							if(mOnPagerItemListener != null) {
								mOnPagerItemListener.onPagerClick(v, viewPosition);
							}
						}
						
					});
				}
			}
			getAdapter().notifyDataSetChanged();
			
		}
	}
	
	
	
	public void addPagerView(View view) {
		if(view != null) {
			allAdViews.add(view);
			getAdapter().notifyDataSetChanged();
		}
	}
	
	@Override
	public void setAdapter(PagerAdapter arg0) {
		super.setAdapter(arg0);
	}
	
	@Override
	public void setPageMargin(int marginPixels) {
		super.setPageMargin(marginPixels);
	}
	
	public void setFadeEnabled(boolean enabled)
	{
		mFadeEnabled = enabled;
	}
	
	
	
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void disableHardwareLayer()
	{
		if (Build.VERSION.SDK_INT < 11)
			return;
		View v;
		for (int i = 0; i < getChildCount(); i++)
		{
			v = getChildAt(i);
			if (v.getLayerType() != View.LAYER_TYPE_NONE)
				v.setLayerType(View.LAYER_TYPE_NONE, null);
		}
	}
	
	private static final float ROT_MAX = 15.0f;
	private float mRot;
	
	private void animateRotate(View left, View right, float positionOffset,
			boolean up)
	{
		if (mState != State.IDLE)
		{
			if (left != null)
			{
				OtherUtils.manageLayer(left, true);
				mRot = (up ? 1 : -1) * (ROT_MAX * positionOffset);
				mTrans = (up ? -1 : 1)
						* (float) (getMeasuredHeight() - getMeasuredHeight()
								* Math.cos(mRot * Math.PI / 180.0f));
				ViewHelper.setPivotX(left, left.getMeasuredWidth() * 0.5f);
				ViewHelper.setPivotY(left, up ? 0 : left.getMeasuredHeight());
				ViewHelper.setTranslationY(left, mTrans);
				ViewHelper.setRotation(left, mRot);
			}
			if (right != null)
			{
				OtherUtils.manageLayer(right, true);
				mRot = (up ? 1 : -1) * (-ROT_MAX + ROT_MAX * positionOffset);
				mTrans = (up ? -1 : 1)
						* (float) (getMeasuredHeight() - getMeasuredHeight()
								* Math.cos(mRot * Math.PI / 180.0f));
				ViewHelper.setPivotX(right, right.getMeasuredWidth() * 0.5f);
				ViewHelper.setPivotY(right, up ? 0 : right.getMeasuredHeight());
				ViewHelper.setTranslationY(right, mTrans);
				ViewHelper.setRotation(right, mRot);
			}
		}
	}
	
	
	protected void animateStack(View left, View right, float positionOffset, int position)
	{
		if (mState != State.IDLE)
		{
			if (right != null)
			{
				Log.i("INFO", "...positionOffset:" + positionOffset);
				Log.i("INFO", "...width:" + width);
				
				OtherUtils.manageLayer(right, true);
				mScale = (1 - SCALE_MAX) * positionOffset + SCALE_MAX;
				mTrans = -width - getPageMargin() + positionOffset * width + 1; //似乎偏移了1,因为没有全部偏移
				
				Log.i("INFO", "scrollX:" + getScrollX());
				Log.i("INFO", "mTrans:" + mTrans);
				ViewHelper.setScaleX(right, mScale);
				ViewHelper.setScaleY(right, mScale);
				ViewHelper.setTranslationX(right, mTrans);
			}
			if (left != null)
			{
				left.bringToFront();
			}
		}
	}
	
	
	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		super.onPageScrolled(position, positionOffset, positionOffsetPixels);
		Log.i("INFO", "childCount: " + allAdViews.size());
		if(mEffect == TransitionEffect.None) {
			return;
		}
		if (mState == State.IDLE && positionOffset > 0)
		{
			oldPage = getCurrentItem();
			mState = position == oldPage ? State.GOING_RIGHT : State.GOING_LEFT;
		}
		boolean goingRight = position == oldPage;
		
		if (mState == State.GOING_RIGHT && !goingRight)
			mState = State.GOING_LEFT;
		else if (mState == State.GOING_LEFT && goingRight)
			mState = State.GOING_RIGHT;

		float effectOffset = isSmall(positionOffset) ? 0 : positionOffset;
		

		mLeft = findViewFromObject(position);
		mRight = findViewFromObject(position + 1);
		
		if (mFadeEnabled)
			animateFade(mLeft, mRight, false, true, effectOffset);
		
		switch(mEffect) {
		case RotateDown:
			animateRotate(mLeft, mRight, effectOffset, false);
			break;
			
		case Stack:
			animateStack(mLeft, mRight, effectOffset, position);
			break;
			
		default:
		}
		
		
		if (effectOffset == 0)
		{
			disableHardwareLayer();
			mState = State.IDLE;
		}
		
		

		
		
	}
	
	private boolean mIsLeftAlpha = true;
	private boolean mIsRightAlpha = true;
	
	public void setLeftRightAlpha(boolean isLeftAlpha, boolean isRightAlpha) {
		mIsLeftAlpha = isLeftAlpha;
		mIsRightAlpha = isRightAlpha;
	}
	
	
	private boolean isSmall(float positionOffset)
	{
		return Math.abs(positionOffset) < 0.0001;
	}

	public void setObjectForPosition(Object obj, int position)
	{
		mObjs.put(Integer.valueOf(position), obj);
	}

	public View findViewFromObject(int position)
	{
		Object o = mObjs.get(Integer.valueOf(position));
		if (o == null)
		{
			return null;
		}
		PagerAdapter a = getAdapter();
		View v;
		for (int i = 0; i < getChildCount(); i++)
		{
			v = getChildAt(i);
			if (a.isViewFromObject(v, o))
				return v;
		}
		return null;
	}
	
	protected void animateFade(View left, View right, boolean isLeftAlpha, boolean isRightAlpha, float positionOffset)
	{
		if(mIsLeftAlpha) {
			if (left != null)
			{
				ViewHelper.setAlpha(left, 1 - positionOffset);
			}
		}
		
		if(mIsRightAlpha) {
			if (right != null)
			{
				ViewHelper.setAlpha(right, positionOffset);
			}
		}
	}
	
	
	public void setIndexDot(boolean isEnable) {
		isIndexDot = isEnable;
	}
	
	@Override
	public void draw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.draw(canvas);
		
		
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		width = w;
		height = h;
		int count = allAdViews.size();
		perSize = (width + height) * dotSizeRadius;
		if(perSize < 0.8f * mDensity)
			perSize = 0.8f * mDensity;
		
		dotMargin = 16 * mDensity + 6 * mDensity * (width * 1.0f / mScreenWidth);
		
		
		dotsSize = perSize * 2 * count + (count - 1) * dotMargin;
		
		final float standardSize = width - 50 * mDensity;
		if(dotsSize >= standardSize) {
			float remindSpace = dotsSize - standardSize;
			
			perSize -= (remindSpace * 0.5f / (count * 2));
			
			dotMargin -= (remindSpace * 0.5f / (count - 1));
			
			dotsSize = perSize * 2 * count + (count - 1) * dotMargin;
			
		}
		
		dotMargin *= dotMarginRadius;
		dotsSize = perSize * 2 * count + (count - 1) * dotMargin;
		
		dotHeightDistance = height - perSize - dotBottomDistance * mDensity + dotBottomDistance * mDensity * (1 - height * 1.0f/mScreenHeight);
		
		
	}
	
	
	
	/**
	 * 默認值為1
	 * 取值越大，边距越大，根据原有边距进行缩放的倍值
	 * @param radius
	 */
	public void setDotMarginRadius(float radius) {
		dotMarginRadius = radius;
	}
	
	/**
	 * 默認值為26
	 * @param distance
	 */
	public void setDotBottomDistance(float distance) {
		dotBottomDistance = distance * mDensity;
	}
	
	/**
	 * 取值范围为0-2,刚刚好,默認值為1
	 * @param radius - 1代表在中心位置
	 */
	public void setDotLeftRadius(float radius) {
		dotLeftRadius = radius;
	}
	
	public void setDotCheckedColor(int color) {
		dotChekedColor = color;
	}
	
	public void setDotUnCheckedColor(int color) {
		dotUnCheckedColor = color;
	}
	
	/**
	 * 可控制点的大小 0.005f 最合适 , 最小是0.8f
	 * @param radius
	 */
	public void setDotSizeRadius(float radius) {
		dotSizeRadius = radius;
	}
	
	
	
	private float dotsSize = 0;
	private float perSize = 0;
	private int width, height;
	private float dotMargin = 0;
	private float dotSizeRadius = 0.0006f;
	
	private float dotHeightDistance;
	
	private float dotLeftRadius = 1.0f;
	
	private float dotMarginRadius = 1.0f;
	
	private float dotBottomDistance = 26.0f;
	
	private int dotChekedColor = Color.WHITE;
	
	private int dotUnCheckedColor = Color.GRAY;
	
	@Override
	protected void dispatchDraw(Canvas canvas) {
		
		super.dispatchDraw(canvas);
		
		int dotCount = allAdViews.size();
		//绘制圆心
		if(isIndexDot && dotCount >= 2) {
			
			
			
			canvas.save();
			canvas.translate(getScrollX(), 0);
			int width = getWidth();
			
			
			/*if (bmpCircle == null) {
				bmpCircle = Bitmap.createBitmap(Math.round(perSize * 2),
						Math.round(perSize * 2),
	                                         Bitmap.Config.ARGB_8888);
				bmpCanvas = new Canvas(bmpCircle);
	        }
			
			bmpCanvas.drawColor(
	                  Color.TRANSPARENT,
	                  PorterDuff.Mode.CLEAR);
			
			mPaint.setColor(Color.parseColor("#33000000"));
			bmpCanvas.drawCircle(perSize, perSize, perSize, mPaint);*/
			
			
			
			
			mPaint.setColor(dotUnCheckedColor);
			
			
			
			float firstX = (width - dotsSize) * 0.5f + perSize;
			
			float leftSpace = firstX - perSize;
			
			
			
			firstX -= leftSpace * (1.0f - dotLeftRadius);
			
			for(int i = 0; i < dotCount; i++) {
				canvas.drawCircle(firstX + perSize * 2 * i + dotMargin * i, dotHeightDistance, perSize, mPaint);
			}
			
			
			
			/*bmpCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
			mPaint.setColor(Color.WHITE);
			bmpCanvas.drawCircle(perSize, perSize, perSize, mPaint);*/
			
			mPaint.setColor(dotChekedColor);
			int currentScrollX = getScrollX();
			
			float totalScrollDistance = ((dotCount - 1) * width) * 1.0f;
			
			float tempScrollRadius = currentScrollX / totalScrollDistance;
			
			if(dotCount <= 1 && Float.isNaN(tempScrollRadius)) {
				tempScrollRadius = 0;
			}
			
			float scrollRadius = Math.min(Math.abs(tempScrollRadius), 1.0f);
			
			Log.i("scroll", "totalScrollDistance:" + totalScrollDistance);
			Log.i("scroll", "currentScrollX:" + currentScrollX);
			Log.i("scroll", "tempScrollRadius:" + tempScrollRadius);
			Log.i("scroll", "scrollRadius:" + scrollRadius);
			
			
			
			canvas.drawCircle(firstX + (dotsSize - perSize * 2) * scrollRadius, dotHeightDistance, perSize, mPaint);
			
			canvas.restore();
		}
	}
	
	boolean isScrollReady = false;
	
	public void clear() {
		destroyDrawingCache();
		
		for(View view : allAdViews) {
			if(view instanceof RoundCornerImageView) {
				RoundCornerImageView rciv = (RoundCornerImageView) view;
				rciv.clear();
			}
		}
		if(timer != null)
			timer.cancel();
		allAdViews.clear();
		mObjs.clear();
		allAdViews = null;
		mObjs = null;
		System.gc();
	}
	
	private OnPagerItemListener mOnPagerItemListener;
	
	public void setOnPagerItemListener(OnPagerItemListener onPagerItemListener) {
		mOnPagerItemListener = onPagerItemListener;
	}


	public interface OnPagerItemListener {
		void onPagerClick(View view, int position);
	}

}
