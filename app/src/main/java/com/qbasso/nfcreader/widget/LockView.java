package com.qbasso.nfcreader.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;
import com.qbasso.nfcreader.R;


public class LockView extends View {

    private Paint paint;
    private int padding;
    private int left;
    private int top;
    private int right;
    private int bottom;
    private int height;
    private int cornerRadius;
    private int insideBarLeft;
    private int insideBarRight;
    private int insideBarTop;
    private int insideBarBottom;
    private int insideBarConrerRadius;
    private int toggleRadius;
    private int togglePostitionY;
    private int toggleMinLeft;
    private int toggleMaxRight;
    private int ovalLeft;
    private int ovalTop;
    private int ovalRight;
    private int ovalBottom;
    private int innerOvalLeft;
    private int innerOvalRight;
    private int innerOvalTop;
    private int innerOvalBottom;
    private Paint fillPaint;
    private Spring armSpringAnimation;
    private int toggleCurrentX;
    private ValueAnimator openAnimator;
    private ValueAnimator closeAnimator;
    private Paint togglePaint;
    private int defaultFillColor;

    private int armRotationAngle;
    private int currentRotationAngle;
    private int toggleCurrentRadius;

    private int toggleRadiusMaxVal;

    private static String TAG = LockView.class.getName();

    private ValueAnimator.AnimatorUpdateListener openListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            float value = (float) animation.getAnimatedValue();
            if (value > 0.3f) {
                toggleCurrentRadius = toggleRadius;
            } else {
                toggleCurrentRadius = (int) (toggleRadius + remap(value, 0, 0.3f, 0, toggleRadiusMaxVal - toggleRadius));
            }
            togglePaint.setColorFilter(new PorterDuffColorFilter(adjustAlpha(Color.WHITE, value), PorterDuff.Mode.SRC_ATOP));
            int newX = toggleMinLeft + (int) ((toggleMaxRight - toggleMinLeft) * value);
            Log.d(TAG, "New toggle x: " + newX + " animated value: " + value);
            toggleCurrentX = newX;
            postInvalidate();
        }
    };

    private ValueAnimator.AnimatorUpdateListener closeListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            float value = (float) animation.getAnimatedValue();
            togglePaint.setColor(defaultFillColor);
            if (value > 0.3f) {
                toggleCurrentRadius = toggleRadius;
            } else {
                toggleCurrentRadius = (int) (toggleRadius + remap(value, 0, 0.3f, 0, toggleRadiusMaxVal - toggleRadius));
            }
            togglePaint.setColorFilter(new PorterDuffColorFilter(adjustAlpha(Color.WHITE, 1 - value), PorterDuff.Mode.SRC_ATOP));
            currentRotationAngle = (int) (armRotationAngle * (1 - value));
            int newX = toggleMaxRight - (int) ((toggleMaxRight - toggleMinLeft) * value);
            Log.d(TAG, "New toggle x: " + newX + " animated value: " + value);
            toggleCurrentX = newX;
            postInvalidate();
        }
    };

    private float remap(float value, float from1, float to1, float from2, float to2) {
        return (value - from1) / (to1 - from1) * (to2 - from2) + from2;
    }

    public LockView(Context context) {
        super(context);
        init();
    }

    public LockView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LockView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        armRotationAngle = 15;
        currentRotationAngle = 0;
        padding = 16;
        initPaints();
        initAnimators();
        setUpArmSpringAnimation();
    }

    private void initAnimators() {
        openAnimator = ValueAnimator.ofFloat(0f, 1f);
        openAnimator.setDuration(500);
        openAnimator.addUpdateListener(openListener);
        closeAnimator = ValueAnimator.ofFloat(0f, 1f);
        closeAnimator.setDuration(400);
        closeAnimator.addUpdateListener(closeListener);
    }

    private void initPaints() {
        defaultFillColor = getResources().getColor(R.color.main_background);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(8);
        paint.setStyle(Paint.Style.STROKE);
        fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fillPaint.setColor(defaultFillColor);
        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setStrokeWidth(0);
        togglePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        togglePaint.setColor(defaultFillColor);
        togglePaint.setStyle(Paint.Style.FILL);
    }

    private void setUpArmSpringAnimation() {
        SpringSystem system = SpringSystem.create();
        armSpringAnimation = system.createSpring();
        armSpringAnimation.setRestSpeedThreshold(20);
        armSpringAnimation.setRestDisplacementThreshold(1);
        SpringConfig config = new SpringConfig(100, 5);
        armSpringAnimation.setSpringConfig(config);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int squareSide = Math.min(w, h - h / 3);
        int width = w - 2 * padding;
        height = h - 2 * padding;
        left = padding + (width - squareSide) / 2;
        top = padding;
        right = w - padding - (width - squareSide) / 2;
        bottom = h - padding;
        cornerRadius = (int) (width * 0.1);
        int lockCenterX = (right + left) / 2;
        int lockCenterY = (int) ((bottom + height / 2f) / 2);
        int insideBarWidth = width / 5;
        int insideBarHeight = height / 10;
        insideBarLeft = lockCenterX - insideBarWidth / 2;
        insideBarRight = insideBarLeft + insideBarWidth;
        insideBarTop = lockCenterY - insideBarHeight / 2;
        insideBarBottom = insideBarTop + insideBarHeight;
        insideBarConrerRadius = insideBarHeight / 2;
        toggleRadius = (int) (insideBarConrerRadius * 1.1);
        toggleCurrentRadius = toggleRadius;
        toggleRadiusMaxVal = (int) (insideBarConrerRadius * 1.5);
        togglePostitionY = lockCenterY;
        toggleMinLeft = insideBarLeft + toggleRadius / 2;
        toggleMaxRight = insideBarRight - toggleRadius / 2;
        toggleCurrentX = toggleMinLeft;

        ovalLeft = left + squareSide / 7;
        ovalRight = right - squareSide / 7;
        int ovalRadius = (ovalRight - ovalLeft) / 2;
        ovalTop = top + height / 2 - ovalRadius;
        ovalBottom = ovalTop + ovalRadius * 2;

        innerOvalLeft = left + squareSide / 4;
        innerOvalRight = right - squareSide / 4;
        int innerOvalRadius = (innerOvalRight - innerOvalLeft) / 2;
        innerOvalTop = top + height / 2 - innerOvalRadius;
        innerOvalBottom = innerOvalTop + innerOvalRadius * 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setStyle(Paint.Style.STROKE);
        drawPaddlockArm(canvas);
        drawPadlockRectangle(canvas);
        drawToggle(canvas);
    }

    private void drawPadlockRectangle(Canvas canvas) {
        drawRoundRect(canvas, left, top + height / 2, right, bottom, cornerRadius, paint);
        drawRoundRect(canvas, left, top + height / 2, right, bottom, cornerRadius, fillPaint);
    }

    private void drawToggle(Canvas canvas) {
        drawRoundRect(canvas, insideBarLeft, insideBarTop, insideBarRight, insideBarBottom, insideBarConrerRadius, paint);
        drawRoundRect(canvas, insideBarLeft, insideBarTop, insideBarRight, insideBarBottom, insideBarConrerRadius, fillPaint);
        canvas.drawOval(new RectF(toggleCurrentX - toggleCurrentRadius, togglePostitionY - toggleRadius, toggleCurrentX + toggleCurrentRadius, togglePostitionY + toggleRadius), paint);
        canvas.drawOval(new RectF(toggleCurrentX - toggleCurrentRadius, togglePostitionY - toggleRadius, toggleCurrentX + toggleCurrentRadius, togglePostitionY + toggleRadius), togglePaint);
    }

    private void drawPaddlockArm(Canvas canvas) {
        Matrix transformation = new Matrix();
        RectF bound = new RectF();
        Path path = new Path();
        path.arcTo(new RectF(innerOvalLeft, innerOvalTop, innerOvalRight, innerOvalBottom), 180, 200);
        path.arcTo(new RectF(ovalLeft, ovalTop, ovalRight, ovalBottom), 20, -200);
        path.close();
        path.computeBounds(bound, true);
        transformation.postRotate(currentRotationAngle, bound.right, bound.bottom);
        path.transform(transformation);
        canvas.drawPath(path, paint);
        canvas.drawPath(path, fillPaint);
    }

    public void openAnimation() {
        openAnimator.start();
        armSpringAnimation.addListener(new SimpleSpringListener() {
            @Override
            public void onSpringUpdate(Spring spring) {
                currentRotationAngle = (int) spring.getCurrentValue();
                postInvalidate();
            }
        });
        armSpringAnimation.setEndValue(armRotationAngle);
    }

    public void closeAnimator() {
        armSpringAnimation.removeAllListeners();
        armSpringAnimation.setEndValue(0);
        closeAnimator.start();
    }

    private void drawRoundRect(Canvas canvas, int left, int top, int right, int bottom, int radius, Paint paint) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            canvas.drawRoundRect(left, top, right, bottom, radius, radius, paint);
        } else {
            canvas.drawRoundRect(new RectF(left, top, right, bottom), radius, radius, paint);
        }
    }

    public int adjustAlpha(int color, float factor) {
        int alpha = Math.round(255 * factor);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }
}
