package g313.vakulenko.hw2_4_lms;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class ArtView extends View {
    private Canvas canvas;
    private Paint paint;
    private Path path;
    private Bitmap bitmap;
    private int defaultColor = Color.BLACK; // Цвет по умолчанию
    private boolean eraserMode = false;

    public ArtView(Context context, AttributeSet attrs) {
        super(context, attrs);
        path = new Path();
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(10);
        paint.setColor(defaultColor); // Устанавливаем цвет по умолчанию
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        canvas.drawPath(path, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                path.moveTo(x, y);
                return true;
            case MotionEvent.ACTION_MOVE:
                path.lineTo(x, y);
                break;
            case MotionEvent.ACTION_UP:
                canvas.drawPath(path, paint);
                path.reset();
                break;
            default:
                return false;
        }

        invalidate();
        return true;
    }

    public void clearCanvas() {
        bitmap.eraseColor(Color.WHITE);
        invalidate();
    }

    public void setColor(int color) {
        eraserMode = false;
        paint.setColor(color);
    }

    public void setEraserMode(boolean enabled) {
        eraserMode = enabled;
        if (enabled) {
            paint.setColor(Color.WHITE); // Белый цвет для режима стирания
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        } else {
            paint.setColor(defaultColor); // Возвращаем цвет по умолчанию
            paint.setXfermode(null);
        }
        invalidate(); // Перерисовываем View
    }

    public boolean isEraserMode() {
        return eraserMode;
    }
}

