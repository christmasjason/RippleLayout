package com.christmas.ripplelayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.RelativeLayout;

import java.util.ArrayList;

/**
 * 点击波纹效果布局.
 */
public class RippleLayout extends RelativeLayout {
  private Paint paint;
  private DispatchUpTouchEventRunnable dispatchUpTouchEventRunnable = new DispatchUpTouchEventRunnable();
  private View touchView;

  private float zoomScale;
  private float radiusMax = 0;
  private float startX = -1;
  private float startY = -1;

  private int touchViewWidth;
  private int touchViewHeight;
  private int touchViewLeftInRippleLayout;
  private int touchViewTopInRippleLayout;
  private int touchViewRightInRippleLayout;
  private int touchViewBottomInRippleLayout;

  private int rippleColor = getResources().getColor(android.R.color.background_dark);
  private int zoomDuration;
  private int percent = 0;
  private int frameRate = 10;
  private int rippleAlpha = 90;
  private int rippleDuration = 400;

  private boolean rippleZoom = false;
  private boolean centerStart = false;
  private boolean animationRunning = false;

  public RippleLayout(Context context) {
    this(context, null);
  }

  public RippleLayout(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public RippleLayout(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);

    init(context, attrs);
  }

  private void init(Context context, AttributeSet attributeSet) {
    final TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.RippleLayout);
    rippleColor = typedArray.getColor(R.styleable.RippleLayout_rippleColor, rippleColor);
    centerStart = typedArray.getBoolean(R.styleable.RippleLayout_rippleCenterStart, false);
    rippleDuration = typedArray.getInteger(R.styleable.RippleLayout_rippleDuration, rippleDuration);
    frameRate = typedArray.getInteger(R.styleable.RippleLayout_rippleFrameRate, frameRate);
    rippleAlpha = typedArray.getInteger(R.styleable.RippleLayout_rippleAlpha, rippleAlpha);
    rippleZoom = typedArray.getBoolean(R.styleable.RippleLayout_rippleZoom, false);
    zoomScale = typedArray.getFloat(R.styleable.RippleLayout_rippleZoomScale, 1.03f);
    zoomDuration = typedArray.getInt(R.styleable.RippleLayout_rippleZoomDuration, 200);
    typedArray.recycle();

    paint = new Paint();
    paint.setAntiAlias(true);
    paint.setStyle(Paint.Style.FILL);
    paint.setColor(rippleColor);

    this.setWillNotDraw(false);
  }

  private View getTouchTarget(View view, int rawX, int rawY) {
    View target = null;
    ArrayList<View> TouchableViews = view.getTouchables();
    for (View child : TouchableViews) {
      if (isTouchPointInView(child, rawX, rawY)) {
        target = child;
        break;
      }
    }

    return target;
  }

  private boolean isTouchPointInView(View touchView, int rawX, int rawY) {
    int[] location = new int[2];
    touchView.getLocationOnScreen(location);
    int leftOnScreen = location[0];
    int topOnScreen = location[1];
    int rightOnScreen = leftOnScreen + touchView.getMeasuredWidth();
    int bottomOnScreen = topOnScreen + touchView.getMeasuredHeight();

    return touchView.isClickable() &&
        rawY >= topOnScreen && rawY <= bottomOnScreen &&
        rawX >= leftOnScreen && rawX <= rightOnScreen;
  }

  private void calculateTouchViewBounds() {
    touchViewWidth = touchView.getWidth();
    touchViewHeight = touchView.getHeight();

    int[] rippleLayoutLocation = new int[2];
    this.getLocationOnScreen(rippleLayoutLocation);

    int[] touchViewLocation = new int[2];
    touchView.getLocationOnScreen(touchViewLocation);
    touchViewLeftInRippleLayout = touchViewLocation[0] - rippleLayoutLocation[0];
    touchViewTopInRippleLayout = touchViewLocation[1] - rippleLayoutLocation[1];
    touchViewRightInRippleLayout = touchViewLeftInRippleLayout + touchView.getMeasuredWidth();
    touchViewBottomInRippleLayout = touchViewTopInRippleLayout + touchView.getMeasuredHeight();
  }

  @Override
  public boolean dispatchTouchEvent(@NonNull MotionEvent event) {
    int rawX = (int) event.getRawX();
    int rawY = (int) event.getRawY();
    int action = event.getAction();
    if (action == MotionEvent.ACTION_DOWN) {
      if (!animationRunning) {
        View touchTargetView = getTouchTarget(this, rawX, rawY);
        if (touchTargetView != null && touchTargetView.isEnabled() && touchTargetView.isClickable()) {
          this.touchView = touchTargetView;
          calculateTouchViewBounds();
          createAnimation(event.getX(), event.getY(), touchTargetView);
          postInvalidateDelayed(frameRate);
        }
      }

    } else if (action == MotionEvent.ACTION_UP) {
      dispatchUpTouchEventRunnable.event = event;
      postDelayed(dispatchUpTouchEventRunnable, rippleDuration);
      return true;

    }

    return super.dispatchTouchEvent(event);
  }

  @Override
  protected void dispatchDraw(@NonNull Canvas canvas) {
    super.dispatchDraw(canvas);

    if (isInEditMode() || !animationRunning || touchView == null) {
      return;
    }

    if (rippleDuration <= percent * frameRate) {
      animationRunning = false;
      percent = 0;
      canvas.restore();
      postInvalidateDelayed(frameRate, touchViewLeftInRippleLayout, touchViewTopInRippleLayout, touchViewRightInRippleLayout, touchViewBottomInRippleLayout);
      return;

    } else {
      postInvalidateDelayed(frameRate, touchViewLeftInRippleLayout, touchViewTopInRippleLayout, touchViewRightInRippleLayout, touchViewBottomInRippleLayout);

    }

    if (percent == 0) {
      canvas.save();
      canvas.clipRect(touchViewLeftInRippleLayout, touchViewTopInRippleLayout, touchViewRightInRippleLayout, touchViewBottomInRippleLayout);
    }

    canvas.drawCircle(startX, startY, (radiusMax * (((float) percent * frameRate) / rippleDuration)), paint);
    paint.setAlpha((int) (rippleAlpha - ((rippleAlpha) * (((float) percent * frameRate) / rippleDuration))));

    percent++;
  }

  private void createAnimation(final float x, final float y, View touchTargetView) {
    if (touchTargetView.isEnabled() && !animationRunning) {
      if (rippleZoom) {
        createZoomAnimation(touchTargetView);
      }

      radiusMax = Math.max(touchViewWidth, touchViewHeight);

      if (centerStart) {
        this.startX = touchTargetView.getLeft() + touchTargetView.getWidth() / 2;
        this.startY = touchTargetView.getTop() + touchTargetView.getHeight() / 2;
      } else {
        this.startX = x;
        this.startY = y;
      }

      animationRunning = true;
    }
  }

  private void createZoomAnimation(View touchTargetView) {
    ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, zoomScale, 1.0f, zoomScale,
        (touchViewRightInRippleLayout + touchViewLeftInRippleLayout) / 2,
        (touchViewBottomInRippleLayout + touchViewTopInRippleLayout) / 2);
    scaleAnimation.setDuration(zoomDuration);
    scaleAnimation.setRepeatMode(Animation.REVERSE);
    scaleAnimation.setRepeatCount(1);
    touchTargetView.startAnimation(scaleAnimation);
  }

  private class DispatchUpTouchEventRunnable implements Runnable {
    public MotionEvent event;

    @Override
    public void run() {
      if (touchView == null || !touchView.isEnabled()) {
        return;
      }

      if (isTouchPointInView(touchView, (int) event.getRawX(), (int) event.getRawY())) {
        touchView.dispatchTouchEvent(event);
      }
    }
  }
}
