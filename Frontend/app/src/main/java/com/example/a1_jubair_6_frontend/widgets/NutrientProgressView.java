package com.example.a1_jubair_6_frontend.widgets;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.a1_jubair_6_frontend.R;

import java.util.ArrayList;
import java.util.List;

public class NutrientProgressView extends View {

    private static final float OUTER_CIRCLE_RADIUS_PERCENT = 0.7f;
    private static final float CENTER_CIRCLE_RADIUS_PERCENT = 0.3f;
    private static final float STROKE_WIDTH = 20f;
    private static final float CIRCLE_SPACING = 12f;
    private static final float FLAME_ICON_SIZE_PERCENT = 0.35f;
    private static final long ANIMATION_DURATION = 1000;

    private List<NutrientData> nutrients;
    private Paint paint;
    private Paint textPaint;
    private float centerX;
    private float centerY;
    private float radius;
    private Drawable flameIcon;

    private Paint bulletPaint;
    private float bulletRadius = 8f; // Size of bullet point
    private float textStartPadding = 24f; // Padding after bullet point
    private float legendSpacing = 40f; // Spacing between legend items
    private float legendTextSize = 30f; // Legend text size

    private ValueAnimator progressAnimator;
    private List<Float> currentProgress;
    private List<Float> targetProgress;

    private boolean isAttached = false;

    public NutrientProgressView(Context context) {
        super(context);
        init();
    }

    public NutrientProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.NutrientProgressView);
            try {
                legendTextSize = a.getDimension(R.styleable.NutrientProgressView_legendTextSize, legendTextSize);
                bulletRadius = a.getDimension(R.styleable.NutrientProgressView_bulletRadius, bulletRadius);
                legendSpacing = a.getDimension(R.styleable.NutrientProgressView_legendSpacing, legendSpacing);
                textStartPadding = a.getDimension(R.styleable.NutrientProgressView_textStartPadding, textStartPadding);
            } finally {
                a.recycle();
            }
        }

        init();
    }

    public NutrientProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(40f);

        flameIcon = ContextCompat.getDrawable(getContext(), R.drawable.ic_flame);

        nutrients = new ArrayList<>();

        bulletPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bulletPaint.setStyle(Paint.Style.FILL);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.setTextSize(legendTextSize);

        currentProgress = new ArrayList<>();
        targetProgress = new ArrayList<>();

        progressAnimator = ValueAnimator.ofFloat(0f, 1f);
        progressAnimator.setDuration(ANIMATION_DURATION);
        progressAnimator.setInterpolator(new DecelerateInterpolator());
        progressAnimator.addUpdateListener(animation -> {
            if (!isAttached) return;
            float fraction = (float) animation.getAnimatedValue();
            for (int i = 0; i < nutrients.size(); i++) {
                float target = targetProgress.get(i);
                float current = currentProgress.get(i);
                nutrients.get(i).setCurrent(current + (target - current) * fraction);
            }
            invalidate();
        });
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        centerX = w / 2f;
        centerY = h / 2f;
        radius = Math.min(w, h) / 2f * OUTER_CIRCLE_RADIUS_PERCENT;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (nutrients.isEmpty()) {
            return;
        }

        float maxRadius = radius;
        float circleRadius = maxRadius;

        // Draw background circles
        for (int i = nutrients.size() - 1; i >= 0; i--) {
            paint.setColor(Color.LTGRAY);
            paint.setStrokeWidth(STROKE_WIDTH);
            paint.setAlpha(50);
            canvas.drawCircle(centerX, centerY, circleRadius, paint);
            circleRadius -= (STROKE_WIDTH + CIRCLE_SPACING);
        }

        // Reset radius for progress arcs
        circleRadius = maxRadius;

        // Draw progress arcs
        for (int i = nutrients.size() - 1; i >= 0; i--) {
            NutrientData nutrient = nutrients.get(i);
            paint.setColor(nutrient.getColor());
            paint.setStrokeWidth(STROKE_WIDTH);
            paint.setAlpha(255);

            float sweepAngle = 360f * (nutrient.getCurrent() / nutrient.getMax());

            canvas.drawArc(
                    centerX - circleRadius,
                    centerY - circleRadius,
                    centerX + circleRadius,
                    centerY + circleRadius,
                    -90f,
                    sweepAngle,
                    false,
                    paint
            );

            circleRadius -= (STROKE_WIDTH + CIRCLE_SPACING);
        }

        // Calculate center area size
        float centerAreaRadius = radius * CENTER_CIRCLE_RADIUS_PERCENT;

        // flame icon
        if (flameIcon != null) {
            int iconSize = (int)(centerAreaRadius * FLAME_ICON_SIZE_PERCENT);
            int iconTop = (int)(centerY - (centerAreaRadius * 0.6f));
            flameIcon.setBounds(
                    (int)(centerX - iconSize/2),
                    iconTop,
                    (int)(centerX + iconSize/2),
                    iconTop + iconSize
            );
            flameIcon.draw(canvas);
        }

        // Draw center text
        String calorieText = (int)nutrients.get(0).getCurrent() + "/" + (int)nutrients.get(0).getMax();
        textPaint.setTextSize(centerAreaRadius * 0.25f);
        String kcalText = "kcal";
        float kcalWidth = textPaint.measureText(kcalText);

        String currentCal = String.valueOf((int)nutrients.get(0).getCurrent());
        String maxCal = String.valueOf((int)nutrients.get(0).getMax());

        textPaint.setTextSize(centerAreaRadius * 0.4f);
        float slashWidth = textPaint.measureText("/");
        float currentWidth = textPaint.measureText(currentCal);
        float maxWidth = textPaint.measureText(maxCal);

        float slashX = centerX;
        float currentX = slashX - slashWidth/2 - currentWidth;
        float maxX = slashX + slashWidth/2;

        canvas.drawText(currentCal, currentX, centerY + centerAreaRadius * 0.2f, textPaint);
        canvas.drawText("/", slashX - slashWidth/2, centerY + centerAreaRadius * 0.2f, textPaint);
        canvas.drawText(maxCal, maxX, centerY + centerAreaRadius * 0.2f, textPaint);
        canvas.drawText(kcalText, centerX - kcalWidth, centerY + centerAreaRadius * 0.6f, textPaint);

        textPaint.setTextSize(centerAreaRadius * 0.25f);

        drawLegend(canvas);
    }

    public void updateNutrient(int index, float current, float max) {
        if (index >= 0 && index < nutrients.size()) {
            nutrients.get(index).setCurrent(current);
            nutrients.get(index).setMax(max);
            invalidate();
        }
    }

    public void updateAllNutrients(List<NutrientData> newData) {
        if (!isAttached || newData == null) return;

        currentProgress.clear();
        targetProgress.clear();

        nutrients.clear();
        nutrients.addAll(newData);

        for (NutrientData nutrient : nutrients) {
            currentProgress.add(0f);
            targetProgress.add(nutrient.getCurrent());
            nutrient.setCurrent(0f);
        }

        if (progressAnimator != null) {
            if (progressAnimator.isRunning()) {
                progressAnimator.cancel();
            }
            progressAnimator.start();
        }
    }

    public static class NutrientData {
        private String name;
        private float current;
        private float max;
        private int color;

        public NutrientData(String name, float current, float max, int color) {
            this.name = name;
            this.current = current;
            this.max = max;
            this.color = color;
        }

        public String getName() { return name; }
        public float getCurrent() { return current; }
        public float getMax() { return max; }
        public int getColor() { return color; }

        public void setCurrent(float current) { this.current = current; }
        public void setMax(float max) { this.max = max; }
    }

    private void drawLegend(Canvas canvas) {
        float startX = 10f;
        float startY = centerY + radius + 40f;

        // For each nutrient (except calories which is index 0)
        for (int i = 1; i < nutrients.size(); i++) {
            NutrientData nutrient = nutrients.get(i);

            // Draw bullet point
            bulletPaint.setColor(nutrient.getColor());
            canvas.drawCircle(startX + bulletRadius, startY - bulletRadius/2, bulletRadius, bulletPaint);

            // Draw text
            textPaint.setTextSize(legendTextSize);
            String legendText = String.format("%s %d/%dgr",
                    nutrient.getName(),
                    (int)nutrient.getCurrent(),
                    (int)nutrient.getMax());
            canvas.drawText(legendText, startX + bulletRadius*2 + textStartPadding, startY, textPaint);

            // Move to next line
            startY += legendSpacing;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        isAttached = false;
        if (progressAnimator != null) {
            progressAnimator.cancel();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        isAttached = true;
    }
}
