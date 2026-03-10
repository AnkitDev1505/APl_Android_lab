package com.ab15.ballthread;



import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

public class BallView extends View {

    Paint paint = new Paint();

    public BallView(Context context) {
        super(context);
        paint.setColor(0xFFFF0000);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float radius = Math.min(getWidth(), getHeight()) / 2;
        canvas.drawCircle(radius, radius, radius, paint);
    }
}