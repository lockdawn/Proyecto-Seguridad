package net.gdgmisantla.proyecto;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.text.TextPaint;
import android.view.View;

public class DrawView extends View {
    private TextPaint mTextPaint = null;
    Paint paint = new Paint();
    private int mStartX = 0;
    private int mStartY = 0;
    private int mEndX = 0;
    private int mEndY = 0;
    String accion = "accion";
    Path path = new Path();

    public DrawView(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int ancho = canvas.getWidth();
        int alto = canvas.getHeight();

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.rgb);
        canvas.drawBitmap(bitmap, new Rect(0,0,ancho,alto), new Rect(0,0,ancho,alto), paint);
    }
}