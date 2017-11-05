package cn.dianyou.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.ImageView;
import cn.dianyou.listeners.IOnDownloadListener;
import cn.dianyou.utils.BitmapUtils;

public class SpreadView extends SpotSpreadView {

	private boolean isRunning;

	private boolean isPause;

	private boolean isStop = true;

	private Paint mTextPaint;

	public static final int DEFAULT_ADVERT_TIME_MAX = 8;

	private static final int DEFAULT_ADVERT_TIME = 4;// Ä¬ÈÏ4

	private static final int DEFAULT_TEXT_MARGIN_LR = 8;

	private static final int DEFAULT_TEXT_CONTAINER_COLOR = Color.parseColor("#292421");

	private int mTextContainerColor = DEFAULT_TEXT_CONTAINER_COLOR;

	private static final int START_ADVERT_MESSAGE = 0;

	private static final int PAUSE_ADVERT_MESSAGE = 1;

	private static final int STOP_ADVERT_MESSAGE = 2;

	private static final int STOP_RESPONSE_MESSAGE = 3;

	private int adTime = DEFAULT_ADVERT_TIME;

	private int currentGoneTime = adTime;

	private int mTextContainerWidth, mTextContainerHeight;

	private float mTextMarginLR = DEFAULT_TEXT_MARGIN_LR;

	private int mTextSize = DEFAULT_TEXT_SIZE;

	private static final String TEXT_CONTEAINER_CONTENT = "Ãëºó¹Ø±Õ";

	private RectF textRectF;

	private ISpreadStopListener listener;
	private int listener_delay_time = 0;

	private boolean isBootTime;

	public interface ISpreadStopListener {
		void onResponse(SpreadView view);
	}

	private Handler mHandler = new Handler(Looper.getMainLooper()) {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case START_ADVERT_MESSAGE:
				if (currentGoneTime > 0) {
					isBootTime = true;
					currentGoneTime--;
					invalidate();
					mHandler.sendEmptyMessageDelayed(START_ADVERT_MESSAGE, 1000);
				} else {
					stop();
				}
				break;

			case PAUSE_ADVERT_MESSAGE:
				mHandler.removeMessages(START_ADVERT_MESSAGE);
				break;

			case STOP_ADVERT_MESSAGE:
				mHandler.removeMessages(START_ADVERT_MESSAGE);
				mHandler.removeMessages(PAUSE_ADVERT_MESSAGE);
				currentGoneTime = 0;
				invalidate();

				if (isStop) {
					if (listener != null) {
						Message msg_ = new Message();
						msg_.what = STOP_RESPONSE_MESSAGE;
						msg_.obj = listener;
						mHandler.sendMessageDelayed(msg_, listener_delay_time);
					}
				}
				break;

			case STOP_RESPONSE_MESSAGE:
				Object obj = msg.obj;
				if (obj instanceof ISpreadStopListener) {
					ISpreadStopListener listener = (ISpreadStopListener) msg.obj;
					listener.onResponse(SpreadView.this);
				}

				break;

			}
			
		}
	};

	public SpreadView(Context context) {
		this(context, null);
	}

	public SpreadView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SpreadView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		mTextSize *= scaledDensity;
		mTextMarginLR *= density;

		initPaints();

		float textWidth = mTextPaint.measureText(DEFAULT_ADVERT_TIME_MAX + TEXT_CONTEAINER_CONTENT);
		mTextContainerWidth = (int) (textWidth + mTextMarginLR * 2);
		mTextContainerHeight = (int) ((mTextPaint.descent() - mTextPaint.ascent()) + mTextMarginTB * 2);

		textRectF = new RectF(mTextMarginLR, mTextMarginLR / 2, mTextMarginLR + mTextContainerWidth,
				mTextMarginLR / 2 + mTextContainerHeight);

	}

	private void initPaints() {

		mTextPaint = new Paint();
		mTextPaint.setAntiAlias(true);
		mTextPaint.setDither(true);
		mTextPaint.setStyle(Paint.Style.FILL);
		mTextPaint.setTextSize(mTextSize);
		mTextPaint.setColor(Color.WHITE);

	}

	public boolean go() {
		return go(0);
	}

	public boolean go(long delayTime) {
		if (isRunning)
			return false;
		currentGoneTime = adTime;
		mHandler.sendEmptyMessageDelayed(START_ADVERT_MESSAGE, delayTime);
		isRunning = true;
		isPause = false;
		isStop = false;
		return true;
	}

	public boolean continueGo() {
		return continueGo(0);
	}

	public boolean continueGo(long delayTime) {
		if (isPause && !isRunning && !isStop) {
			mHandler.sendEmptyMessageDelayed(START_ADVERT_MESSAGE, delayTime);
			isRunning = true;
			isPause = false;
			return true;
		}
		return false;
	}

	public boolean pause() {
		if (isPause || isStop) {
			return false;
		}
		mHandler.sendEmptyMessage(PAUSE_ADVERT_MESSAGE);
		isRunning = false;
		isPause = true;
		isStop = false;
		return true;
	}

	public boolean stop() {
		if (isStop)
			return false;
		mHandler.sendEmptyMessage(STOP_ADVERT_MESSAGE);
		isBootTime = false;
		isRunning = false;
		isPause = false;
		isStop = true;
		return true;
	}

	public void setStopListener(ISpreadStopListener listener, int listener_delay_time) {
		this.listener = listener;
		this.listener_delay_time = listener_delay_time;
	}

	public void setStopListener(ISpreadStopListener listener) {
		setStopListener(listener, 0);
	}

	public void setAdTime(int adTime) {
		if (adTime < 0)
			adTime = 0;
		if (adTime > DEFAULT_ADVERT_TIME_MAX)
			adTime = 60;
		this.adTime = adTime;
	}

	public int getAdTime() {
		return adTime;
	}

	private Bitmap textContainerBmp;
	public void setTextContainerBitmap(Bitmap bitmap) {
		if (bitmap != null) {
			Bitmap textContainerBitmap = BitmapUtils.resizeImage(bitmap, mTextContainerWidth, mTextContainerHeight);
			BitmapShader shader = new BitmapShader(textContainerBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
			mPaint.setShader(shader);
			textContainerBmp = bitmap;
			this.mTextContainerWidth = textContainerBitmap.getWidth() + 1;
			this.mTextContainerHeight = textContainerBitmap.getHeight() + 1;
		}
	}
	
	public void clear() {
		if(textContainerBmp != null) {
			textContainerBmp.recycle();
			textContainerBmp = null;
			System.gc();
		}
		super.clear();
	}

	public void setTextContainerColor(int color) {
		this.mTextContainerColor = color;
		mPaint.setShader(null);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		Log.i("INFO", "canvas:" + canvas);

		if (currentGoneTime >= 0 && isBootTime) {

			mPaint.setColor(mTextContainerColor);
			canvas.drawRoundRect(textRectF, roundDegree * 2, roundDegree * 2, mPaint);

			final int runTime = currentGoneTime + 1;

			final float textWidth = mTextPaint.measureText(runTime + TEXT_CONTEAINER_CONTENT);

			float txtX = mTextMarginLR + (mTextContainerWidth - textWidth) / 2.0f;
			float txtY = mTextMarginLR / 2 - mTextPaint.ascent() + mTextMarginTB;

			canvas.drawText(runTime + TEXT_CONTEAINER_CONTENT, txtX, txtY, mTextPaint);

		}

	}

}
