package com.cookandroid.medication_helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

class TextPoint {
    String text;
    Point point;

    public TextPoint(String text, Point point) {
        this.text = text;
        this.point = point;
    }
}

class BitmapPoint {
    Bitmap bitmap;
    Point point;

    public BitmapPoint(Bitmap bitmap, Point point) {
        this.bitmap = bitmap;
        this.point = point;
    }
}

public class OverlayView extends View {
    List<Rect> rects = new ArrayList<>();
    List<Rect> fillRects = new ArrayList<>();
    List<TextPoint> textPoints = new ArrayList<>();
    List<Point> points = new ArrayList<>();
    List<BitmapPoint> bitmapPoints = new ArrayList<>();
    int fillColor = Color.BLUE;

    public OverlayView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    void addRect(Rect rect) {
        rects.add(rect);
    }

    void addRect(RectF rectf) {
        addRect(new Rect((int)rectf.left, (int)rectf.top, (int)rectf.right, (int)rectf.bottom));
    }

    void addFillRect(Rect rect) {
        fillRects.add(rect);
    }

    void addFillRect(RectF rectf) {
        addFillRect(new Rect((int)rectf.left, (int)rectf.top, (int)rectf.right, (int)rectf.bottom));
    }

    void addLabel(String text, Point point) {
        textPoints.add(new TextPoint(text, point));
    }

    void addLabel(String text, PointF pointf) {
        addLabel(text, new Point((int)pointf.x, (int)pointf.y));
    }

    void addRectWithLabel(Rect rect, String text) {
        addRect(rect);
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(2.0f);
        paint.setStyle(Paint.Style.STROKE);
        paint.setTextSize(28.0f);
        Rect textRect = new Rect();
        paint.getTextBounds(text, 0, text.length(), textRect);
        addFillRect(new Rect(rect.left, rect.top - textRect.height(), rect.left + textRect.width(), rect.top));
        addLabel(text, new Point(rect.left, rect.top));
    }

    void addRectWithLabel(RectF rectf, String text) {
        addRectWithLabel(new Rect((int)rectf.left, (int)rectf.top, (int)rectf.right, (int)rectf.bottom), text);
    }

    void addPoint(Point point) {
        points.add(point);
    }

    void addPoint(PointF pointf) {
        addPoint(new Point((int)pointf.x, (int)pointf.y));
    }

    void addBitmap(Bitmap bitmap, Point point) {
        bitmapPoints.add(new BitmapPoint(bitmap, point));
    }

    void addBitmap(Bitmap bitmap, PointF pointf) {
        addBitmap(bitmap, new Point((int)pointf.x, (int)pointf.y));
    }

    void clear() {
        rects.clear();
        fillRects.clear();
        textPoints.clear();
        points.clear();
        bitmapPoints.clear();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint paint = new Paint();

        //사각형 그리기
        for (Rect rect: rects) {
            paint.setColor(Color.BLUE);
            paint.setStrokeWidth(4.0f);
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawRect(rect, paint);
        }

        //채워진 사각형 그리기
        for (Rect fillRect: fillRects) {
            paint.setColor(fillColor);
            paint.setStrokeWidth(0.0f);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawRect(fillRect, paint);
        }

        //텍스트 그리기
        for (TextPoint textPoint: textPoints) {
            paint.setColor(Color.WHITE);
            paint.setStrokeWidth(2.0f);
            paint.setStyle(Paint.Style.STROKE);
            paint.setTextSize(28.0f);
            canvas.drawText(textPoint.text, textPoint.point.x * 1.0f, textPoint.point.y * 1.0f, paint);
        }

        //점 그리기
        for (Point point: points) {
            paint.setColor(Color.BLUE);
            paint.setStrokeWidth(0.0f);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(point.x * 1.0f, point.y * 1.0f, 4.0f, paint);
        }

        //이미지 그리기
        for (BitmapPoint bitmapPoint: bitmapPoints) {
            canvas.drawBitmap(bitmapPoint.bitmap, (float)bitmapPoint.point.x, (float)bitmapPoint.point.y, null);
        }
    }
}
