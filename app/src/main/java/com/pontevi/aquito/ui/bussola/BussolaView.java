package com.pontevi.aquito.ui.bussola;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class BussolaView extends View {

    private final Paint paintCirculo = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint paintAgulha = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint paintTexto = new Paint(Paint.ANTI_ALIAS_FLAG);

    private float azimute = 0f;
    private float bearing = 0f;

    public BussolaView(Context context, AttributeSet attrs) {
        super(context, attrs);

        paintCirculo.setColor(Color.LTGRAY);
        paintCirculo.setStyle(Paint.Style.STROKE);
        paintCirculo.setStrokeWidth(4f);

        paintAgulha.setColor(Color.RED);
        paintAgulha.setStrokeWidth(8f);

        paintTexto.setColor(Color.BLACK);
        paintTexto.setTextSize(48f);
        paintTexto.setTextAlign(Paint.Align.CENTER);
    }

    public void setAzimute(float azimute) {
        this.azimute = azimute;
        invalidate();
    }

    public void setBearing(float bearing) {
        this.bearing = bearing;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float cx = getWidth() / 2f;
        float cy = getHeight() / 2f;
        float raio = Math.min(cx, cy) * 0.8f;

        canvas.drawCircle(cx, cy, raio, paintCirculo);

        canvas.drawText("N", cx, cy - raio + 60f, paintTexto);
        canvas.drawText("S", cx, cy + raio + 10f, paintTexto);
        canvas.drawText("L", cx + raio - 10f, cy + 16f, paintTexto);
        canvas.drawText("O", cx - raio + 10f, cy + 16f, paintTexto);

        canvas.save();
        canvas.rotate(bearing - azimute, cx, cy);
        canvas.drawLine(cx, cy, cx, cy - raio * 0.7f, paintAgulha);
        canvas.restore();
    }
}