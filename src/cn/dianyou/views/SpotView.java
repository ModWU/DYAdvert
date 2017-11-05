package cn.dianyou.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import cn.dianyou.views.SpotView.AnimationBitmap;

public class SpotView extends SpotSpreadView {

	private static final int DEFAULT_BACK_COLOR = Color.parseColor("#ffffff");

	private static final int DEFAULT_BACK_ROUND_DREGEE = 6;

	private int backColor = DEFAULT_BACK_COLOR;

	private int backRoundDregee = DEFAULT_BACK_ROUND_DREGEE;

	private RectF backRect;

	/*private Rect imageClipRect;*/

	private Rect srcRect;

	private static final int SHOW_CLOSE_WINDOW_MESSAGE = 0;
	
	private static final int RESPONSE_CLOSE_LISTENER_MESSAGE = 1;
	
	//private static final int SHOW_PART_ANIMATION_MESSAGE = 2;
	
	private float closeRadius = 16;

	private int closeRadiusNeeded = (int) closeRadius;

	private float cRaduisValue = 0f;

	private boolean isShowCloseFinished = false;

	private boolean isShowClose = false;

	private int width, height;
	
	private IOnCloseListener mOnCloseListener;
	private long delayCloseListenerDelayMillis = 0;
	
	public interface IOnCloseListener {
		void close(SpotView view);
	}
	
	public static final class AnimationBitmap {
		public final Bitmap animBitmap;
		public final float left;
		public final float top;
		public final int width, height;
		public final float animDregee;
		public final PointF rotatePoint;
		public final boolean isCenter;
		public final boolean isAnimation;
		
		public AnimationBitmap(Bitmap bitmap, int left, int top, int width, int height, float dregee) {
			this(bitmap, left, top, width, height, dregee, false);
		}
		
		public AnimationBitmap(Bitmap bitmap, int left, int top, int width, int height, float dregee, boolean isCenter) {
			this(bitmap, left, top, width, height, dregee, true, false);
		}
		
		public AnimationBitmap(Bitmap bitmap, int left, int top, int width, int height, float dregee, boolean isAnimation, boolean isCenter) {
			this(bitmap, left, top, width, height, dregee, null, isAnimation, isCenter);
		}
		
		public AnimationBitmap(AnimationBitmap animationBitmap) {
			if(animationBitmap.animDregee != 0) {
				if(animationBitmap.rotatePoint == null) {
					rotatePoint = new PointF();
				} else {
					rotatePoint = animationBitmap.rotatePoint;
				}
			} else {
				rotatePoint = null;
			}
			this.animBitmap = animationBitmap.animBitmap;
			this.left = animationBitmap.left;
			this.top = animationBitmap.top;
			this.width = animationBitmap.width;
			this.height = animationBitmap.height;
			this.animDregee = animationBitmap.animDregee;
			this.isAnimation = animationBitmap.isAnimation;
			this.isCenter = animationBitmap.isCenter;
		}
		
		
		public AnimationBitmap(Bitmap bitmap, int left, int top, int width, int height, float dregee, PointF pointF, boolean isAnimation, boolean isCenter) {
			if(dregee != 0) {
				if(pointF == null) {
					rotatePoint = new PointF();
				} else {
					rotatePoint = pointF;
				}
			} else {
				rotatePoint = null;
			}
			this.animBitmap = bitmap;
			this.left = left;
			this.top = top;
			this.width = width;
			this.height = height;
			this.animDregee = dregee;
			this.isAnimation = isAnimation;
			this.isCenter = isCenter;
		}
	}
	
	public void setOnCloseListener(IOnCloseListener onCloseListener, long delayMillis) {
		mOnCloseListener = onCloseListener;
		delayCloseListenerDelayMillis = delayMillis;
	}
	
	public void setOnCloseListener(IOnCloseListener onCloseListener) {
		setOnCloseListener(onCloseListener, 0);
	}
	
	public boolean isShowCloseFinished() {
		return isShowCloseFinished;
	}
	
	public int getCloseRadiusNeeded() {
		return closeRadiusNeeded;
	}

	private Handler mHandler = new Handler(Looper.getMainLooper()) {
		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
			case SHOW_CLOSE_WINDOW_MESSAGE:
				if (!isShowCloseFinished) {
					isShowClose = true;
					cRaduisValue += 0.08f;
					if (cRaduisValue >= 1.0f) {
						cRaduisValue = 1.0f;
						isShowCloseFinished = true;
						mHandler.removeMessages(SHOW_CLOSE_WINDOW_MESSAGE);
						invalidate();
						return;
					} else {
						invalidate();
						mHandler.sendEmptyMessageDelayed(SHOW_CLOSE_WINDOW_MESSAGE, 10);
					}

				} else {
					cRaduisValue = 1.0f;
					mHandler.removeMessages(SHOW_CLOSE_WINDOW_MESSAGE);
				}
				break;
				
			case RESPONSE_CLOSE_LISTENER_MESSAGE:
				Object obj = msg.obj;
				if(obj instanceof IOnCloseListener) {
					IOnCloseListener listener = (IOnCloseListener) obj;
					listener.close(SpotView.this);
				}
				break;
				
			/*case SHOW_PART_ANIMATION_MESSAGE:
				alertA *= -1;
				animValue = animValue + 0.3f * alertA;
				invalidate();
				mHandler.sendEmptyMessageDelayed(SHOW_PART_ANIMATION_MESSAGE, 200);
				
				break;*/
			}

		};
	};

	public SpotView(Context context) {
		this(context, null);
	}

	public SpotView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SpotView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mPaint.setFilterBitmap(true);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		backRoundDregee *= density;
		closeRadius *= density;
		closeRadiusNeeded = (int) (closeRadius / 3);
	}

	public int BackRoundDregee() {
		return backRoundDregee;
	}

	private Bitmap bitmap;

	@Override
	public void setImageBitmap(Bitmap bm) {
		bitmap = bm;
	}

	public void setCloseRadius(float radius) {
		closeRadius = radius;
	}
	
	public void newData() {
		super.newData();
		isShowClose = false;
		isShowCloseFinished = false;
		cRaduisValue = 0.0f;
	}

	private float closeCenterX = 0;
	private float closeCenterY = 0;

	@Override
	protected void onDraw(Canvas canvas) {
		mPaint.setStyle(Paint.Style.FILL);
		mPaint.setColor(backColor);
		canvas.drawRoundRect(backRect, backRoundDregee, backRoundDregee, mPaint);
		int index_1 = canvas.save();
		//canvas.clipRect(imageClipRect);//这一步还要考虑硬件加速，省略吧
		//缩放图片
		canvas.drawBitmap(bitmap, srcRect, imageClipRect, mPaint);
		//onDrawSomeAnimation(canvas);
		super.onDraw(canvas);
		canvas.restoreToCount(index_1);

		if (isShowClose) {
			mPaint.setColor(backColor);
			/*
			 * final float cicleX = backRect.right - (closeRadius -
			 * closeRadiusNeeded); final float cicleY = backRect.top +
			 * (closeRadius - closeRadiusNeeded);
			 */
			canvas.drawCircle(closeCenterX, closeCenterY, closeRadius * cRaduisValue, mPaint);
			mPaint.setColor(Color.parseColor("#404040"));
			float innerCicleR = closeRadius - 4 * density;
			canvas.drawCircle(closeCenterX, closeCenterY, innerCicleR * cRaduisValue, mPaint);

			mPaint.setStyle(Paint.Style.STROKE);
			mPaint.setStrokeWidth(3 * density * cRaduisValue);
			mPaint.setColor(backColor);

			float lineX = closeCenterX + (-closeRadius / 2 + 4 * density) * cRaduisValue;

			float lineY = closeCenterY + (-closeRadius / 2 + 4 * density) * cRaduisValue;

			float toLineX = closeCenterX + (closeRadius / 2 - 4 * density) * cRaduisValue;

			float toLineY = closeCenterY + (closeRadius / 2 - 4 * density) * cRaduisValue;

			canvas.drawLine(lineX, lineY, toLineX, toLineY, mPaint);

			canvas.drawLine(toLineX, lineY, lineX, toLineY, mPaint);

			mPaint.setStyle(Paint.Style.FILL);

		}
	}

	
	/*private float animValue = 0.0f;
	private int alertA = -1;
	
	private AnimationBitmap[] animationBitmaps;
	private Rect animbpRect;
	private RectF animbpClipRect;*/
	
	public void clear() {
		/*if(animationBitmaps != null) {
			for(AnimationBitmap ab : animationBitmaps) {
				if(ab.animBitmap != null) {
					ab.animBitmap.recycle();
				}
			}
			animationBitmaps = null;
		}*/
		
		if(bitmap != null) {
			bitmap.recycle();
			bitmap = null;
		}
		super.clear();
	}
	
	/*private void onDrawSomeAnimation(Canvas canvas) {
		if(animationBitmaps == null || animbpRect == null || animbpClipRect == null || animationBitmaps.length == 0 || imageClipRect == null ||  canvas == null)
			return;
		
		for(AnimationBitmap bp : animationBitmaps) {
			if(bp.animBitmap == null) continue;
			animbpRect.set(0, 0, bp.animBitmap.getWidth(), bp.animBitmap.getHeight());
			
			float tempAnimValue = animValue;
			
			if(!bp.isAnimation) {
				tempAnimValue = 1.0f;
			}
			
			if(bp.isCenter) {
				animbpClipRect.set(bp.left + bp.width/2 - bp.width/2 * tempAnimValue + backRoundDregee / 2 + closeRadiusNeeded, bp.top + bp.height/2 - bp.height/2 * tempAnimValue + backRoundDregee / 2 + closeRadiusNeeded, bp.left + bp.width/2 + bp.width/2 * tempAnimValue + backRoundDregee / 2 + closeRadiusNeeded, bp.top + bp.height/2 + bp.height/2 * tempAnimValue + backRoundDregee / 2 + closeRadiusNeeded);
			}
			else 
				animbpClipRect.set(bp.left + backRoundDregee / 2 + closeRadiusNeeded, bp.top + bp.height/2 - bp.height/2 * tempAnimValue + backRoundDregee / 2 + closeRadiusNeeded, bp.left + bp.width * tempAnimValue + backRoundDregee / 2 + closeRadiusNeeded, bp.top + bp.height/2 + bp.height/2 * tempAnimValue + backRoundDregee / 2 + closeRadiusNeeded);
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
		
	}*/
	
	/*public void showPartAnimation(long delayMillis) {
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
	}*/

	private boolean isDownCloseRect = false;
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(isClickEnable) {
			return super.onTouchEvent(event);
		}
		int action = event.getAction();
		final RectF closeRect = new RectF(closeCenterX - closeRadius, closeCenterY - closeRadius, closeCenterX + closeRadius, closeCenterY + closeRadius);
		final float x = event.getX();
		final float y = event.getY();
		switch(action) {
		case MotionEvent.ACTION_DOWN:
			if(isShowClose && closeRect.contains(x, y)) {
				isDownCloseRect = true;
				return true;
			}
			break;
			
		case MotionEvent.ACTION_MOVE:
			break;
			
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			if(isDownCloseRect && isShowCloseFinished && closeRect.contains(x, y)) {
				isShowClose = false;
				invalidate();
				if(mOnCloseListener != null) {
					Message msg = Message.obtain();
					msg.what = RESPONSE_CLOSE_LISTENER_MESSAGE;
					msg.obj = mOnCloseListener;
					mHandler.sendMessageDelayed(msg, delayCloseListenerDelayMillis);
				}
				return true;
			}
			isDownCloseRect = false;
			break;
		
		}
		
		return super.onTouchEvent(event);
	}

	public void showClose(long delayMillis) {
		isShowCloseFinished = false;
		cRaduisValue = 0.0f;
		mHandler.sendEmptyMessageDelayed(SHOW_CLOSE_WINDOW_MESSAGE, delayMillis);
	}
	
	public int getGap() {
		return backRoundDregee / 2 + closeRadiusNeeded;
	}

	//@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		width = w;
		height = h;
		backRect = new RectF(closeRadiusNeeded, closeRadiusNeeded, w - closeRadiusNeeded, h - closeRadiusNeeded);

		imageClipRect = new Rect(backRoundDregee / 2 + closeRadiusNeeded, backRoundDregee / 2 + closeRadiusNeeded,
				w - backRoundDregee / 2 - closeRadiusNeeded, h - backRoundDregee / 2 - closeRadiusNeeded);

		srcRect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

		closeCenterX = backRect.right - (closeRadius - closeRadiusNeeded);
		closeCenterY = backRect.top + (closeRadius - closeRadiusNeeded);

		getAccessCircleWaveRect(backRect);
		
		/*if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			if(getLayerType() == View.LAYER_TYPE_HARDWARE) {
				setLayerType(View.LAYER_TYPE_NONE, null);
			}
		}*/
	}

}
