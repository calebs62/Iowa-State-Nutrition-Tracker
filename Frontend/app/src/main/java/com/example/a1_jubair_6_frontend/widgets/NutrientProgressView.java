package com.example.a1_jubair_6_frontend.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.a1_jubair_6_frontend.R;

import java.util.ArrayList;
import java.util.List;

public class NutrientProgressView extends View {

    private static final float OUTER_CIRCLE_RADIUS_PERCENT = 0.8f;
    private static final float CENTER_CIRCLE_RADIUS_PERCENT = 0.35f;
    private static final float STROKE_WIDTH = 25f;
    private static final float CIRCLE_SPACING = 15f;
    private static final float FLAME_ICON_SIZE_PERCENT = 0.4f;

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
    private float legendSpacing = 50f; // Spacing between legend items
    private float legendTextSize = 36f; // Legend text size

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
        nutrients.add(new NutrientData("Calories", 438f, 842f, Color.BLACK));
        nutrients.add(new NutrientData("Protein", 210f, 220f, getResources().getColor(R.color.Iowa_State_Red)));
        nutrients.add(new NutrientData("Carbs", 160f, 145f, getResources().getColor(R.color.Iowa_State_Gold)));
        nutrients.add(new NutrientData("Fat", 150f, 187f, getResources().getColor(R.color.Iowa_State_Brown)));
        nutrients.add(new NutrientData("Salt", 16f, 45f, getResources().getColor(R.color.Iowa_State_Light_Brown)));
        nutrients.add(new NutrientData("Sugar", 27f, 60f, getResources().getColor(R.color.Iowa_State_White)));

        bulletPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bulletPaint.setStyle(Paint.Style.FILL);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.setTextSize(legendTextSize);
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
        String kcalText = "kcal";

        textPaint.setTextSize(centerAreaRadius * 0.4f);
        canvas.drawText(calorieText, centerX * 0.8f, centerY + centerAreaRadius * 0.2f, textPaint);

        textPaint.setTextSize(centerAreaRadius * 0.25f);
        canvas.drawText(kcalText, centerX * 0.95f, centerY + centerAreaRadius * 0.6f, textPaint);

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
        if (newData.size() == nutrients.size()) {
            nutrients.clear();
            nutrients.addAll(newData);
            invalidate();
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
        float startY = centerY + radius + 60f;

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

    public void setLegendTextSize(float size) {
        legendTextSize = size;
        invalidate();
    }

    public void setBulletRadius(float radius) {
        bulletRadius = radius;
        invalidate();
    }

    public void setLegendSpacing(float spacing) {
        legendSpacing = spacing;
        invalidate();
    }
}
