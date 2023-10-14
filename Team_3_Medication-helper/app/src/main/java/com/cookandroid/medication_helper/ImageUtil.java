package com.cookandroid.medication_helper;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.YuvImage;
import android.media.ExifInterface;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Size;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ImageUtil {

    public static Bitmap resizeBitmap(Bitmap bitmap, int width, int height) {
        return Bitmap.createScaledBitmap(bitmap, width, height, false);
    }

    public static Bitmap resizeBitmapCenterCrop(Bitmap bitmap, int width, int height) {
        float widthTargetRatio = width * 1.0f / bitmap.getWidth();
        float heightTargetRatio = height * 1.0f / bitmap.getHeight();
        float ratio = Math.max(widthTargetRatio, heightTargetRatio);

        Bitmap resizedBitmap = resizeBitmap(bitmap, (int)(bitmap.getWidth() * ratio), (int)(bitmap.getHeight() * ratio));
        Bitmap croppedBitmap = Bitmap.createBitmap(resizedBitmap, (int)((resizedBitmap.getWidth() - width) / 2), (int)((resizedBitmap.getHeight() - height) / 2), width, height);

        return croppedBitmap;
    }

    public static Bitmap blend(Bitmap bitmap, Bitmap maskBitmap) {
        Bitmap blendedBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(blendedBitmap);
        canvas.drawBitmap(bitmap, 0f, 0f, null);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        canvas.drawBitmap(maskBitmap, 0f, 0f, paint);

        return blendedBitmap;
    }

    public static Bitmap blend(Bitmap bitmap, Bitmap maskBitmap, Bitmap backgroundBitmap) {
        Bitmap blendedBitmap = blend(bitmap, maskBitmap);

        Bitmap blendedBitmapWithBackground = Bitmap.createBitmap(backgroundBitmap.getWidth(), backgroundBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(blendedBitmapWithBackground);
        canvas.drawBitmap(backgroundBitmap, 0f, 0f, null);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
        canvas.drawBitmap(blendedBitmap, 0f, 0f, paint);

        return blendedBitmapWithBackground;
    }

    /*
    Bitmap imageProxyToBitmap(ImageProxy imageProxy) {
        //https://developer.android.com/reference/android/media/Image.html#getFormat()
        //https://developer.android.com/reference/android/graphics/ImageFormat#JPEG
        //https://developer.android.com/reference/android/graphics/ImageFormat#YUV_420_888
        if (imageProxy.getFormat() == ImageFormat.JPEG) {
            ByteBuffer buffer = imageProxy.getPlanes()[0].getBuffer();
            buffer.rewind();
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

            return bitmap;
        }
        else if (imageProxy.getFormat() == ImageFormat.YUV_420_888) {
            ByteBuffer yBuffer = imageProxy.getPlanes()[0].getBuffer(); // Y
            ByteBuffer uBuffer = imageProxy.getPlanes()[1].getBuffer(); // U
            ByteBuffer vBuffer = imageProxy.getPlanes()[2].getBuffer(); // V

            int ySize = yBuffer.remaining();
            int uSize = uBuffer.remaining();
            int vSize = vBuffer.remaining();

            byte[] nv21 = new byte[ySize + uSize + vSize];

            yBuffer.get(nv21, 0, ySize);
            vBuffer.get(nv21, ySize, vSize);
            uBuffer.get(nv21, ySize + vSize, uSize);

            YuvImage yuvImage = new YuvImage(nv21, ImageFormat.NV21, imageProxy.getWidth(), imageProxy.getHeight(), null);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            yuvImage.compressToJpeg(new Rect(0, 0, yuvImage.getWidth(), yuvImage.getHeight()), 100, out);
            byte[] imageBytes = out.toByteArray();
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

            return bitmap;
        }
        return null;
    }
*/

    public static byte[] bitmapToByteArray(Bitmap bitmap) {
        byte[] byteArray = new byte[bitmap.getWidth() * bitmap.getHeight() * 3];
        int byteArrayIndex = 0;
        for (int y = 0; y < bitmap.getHeight(); y++) {
            for (int x = 0; x < bitmap.getWidth(); x++) {
                int pixel = bitmap.getPixel(x, y);
                int r = (pixel >> 16) & 0xFF;
                int g = (pixel >> 8) & 0xFF;
                int b = pixel & 0xFF;
                //int r = Color.red(pixel);
                //int g = Color.green(pixel);
                //int b = Color.blue(pixel);
                byteArray[byteArrayIndex++] = (byte)r;
                byteArray[byteArrayIndex++] = (byte)g;
                byteArray[byteArrayIndex++] = (byte)b;
            }
        }

        return byteArray;
    }
/*
int size = bitmap.getRowBytes() * bitmap.getHeight();
ByteBuffer byteBuffer = ByteBuffer.allocate(size);
bitmap.copyPixelsToBuffer(byteBuffer);
byte[] byteArray = byteBuffer.array();
*/

    public static ByteBuffer bitmapToByteBuffer(Bitmap bitmap) {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1 * bitmap.getWidth() * bitmap.getHeight() * 3 * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        for (int y = 0; y < bitmap.getHeight(); ++y) {
            for (int x = 0; x < bitmap.getWidth(); ++x) {
                int pixel = bitmap.getPixel(x, y);
                int r = (pixel >> 16) & 0xFF;
                int g = (pixel >> 8) & 0xFF;
                int b = pixel & 0xFF;
                //int r = Color.red(pixel);
                //int g = Color.green(pixel);
                //int b = Color.blue(pixel);
                byteBuffer.putFloat(r);
                byteBuffer.putFloat(g);
                byteBuffer.putFloat(b);
            }
        }

        return byteBuffer;
    }
/*
                int size = bitmap.getRowBytes() * bitmap.getHeight();
                ByteBuffer byteBuffer = ByteBuffer.allocate(size);
                bitmap.copyPixelsToBuffer(byteBuffer);
*/

    //https://stackoverflow.com/questions/41773621/camera2-output-to-bitmap
    //private Bitmap convertYUV420888ToNV21_bitmap(Image imgYUV420) {
    public static Bitmap mediaImageToBitmap(Image mediaImage) {
        byte[] byteArray = mediaImageToByteArray(mediaImage);
        Bitmap bitmap = null;
        if (mediaImage.getFormat() == ImageFormat.JPEG) {
            bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        }
        else if (mediaImage.getFormat() == ImageFormat.YUV_420_888) {
            YuvImage yuvImage = new YuvImage(byteArray, ImageFormat.NV21, mediaImage.getWidth(), mediaImage.getHeight(), null);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            yuvImage.compressToJpeg(new Rect(0, 0, yuvImage.getWidth(), yuvImage.getHeight()), 100, out);
            byte[] imageBytes = out.toByteArray();
            bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        }

        return bitmap;
    }

    //https://stackoverflow.com/questions/41773621/camera2-output-to-bitmap //https://github.com/EzequielAdrianM/Camera2Vision/blob/master/Camera2/app/src/main/java/com/example/ezequiel/camera2/MainActivity.java
    //private byte[] convertYUV420888ToNV21(Image imgYUV420) {
    public static byte[] mediaImageToByteArray(Image mediaImage) {
// Converting YUV_420_888 data to YUV_420_SP (NV21).
        //https://developer.android.com/reference/android/media/Image.html#getFormat()
        //https://developer.android.com/reference/android/graphics/ImageFormat#JPEG
        //https://developer.android.com/reference/android/graphics/ImageFormat#YUV_420_888
        byte[] byteArray = null;
        if (mediaImage.getFormat() == ImageFormat.JPEG) {
            ByteBuffer buffer0 = mediaImage.getPlanes()[0].getBuffer();
            buffer0.rewind();
            int buffer0_size = buffer0.remaining();
            byteArray = new byte[buffer0_size];
            buffer0.get(byteArray, 0, buffer0_size);
        }
        else if (mediaImage.getFormat() == ImageFormat.YUV_420_888) {
            ByteBuffer buffer0 = mediaImage.getPlanes()[0].getBuffer();
            ByteBuffer buffer2 = mediaImage.getPlanes()[2].getBuffer();
            int buffer0_size = buffer0.remaining();
            int buffer2_size = buffer2.remaining();
            byteArray = new byte[buffer0_size + buffer2_size];
            buffer0.get(byteArray, 0, buffer0_size);
            buffer2.get(byteArray, buffer0_size, buffer2_size);
        }

        return byteArray;
    }

    public static ByteBuffer mediaImageToByteBuffer(Image mediaImage) {
        byte[] byteArray = mediaImageToByteArray(mediaImage);

        ByteBuffer byteBuffer = ByteBuffer.wrap(byteArray);

        return byteBuffer;
    }

    public static String getPathFromUri(Context context, Uri uri) {
        String result = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver( ).query(uri, proj, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst( )) {
                int column_index = cursor.getColumnIndexOrThrow(proj[0]);
                result = cursor.getString(column_index);
            }
            cursor.close();
        }
        if (result == null) {
            result = "Not found";
        }
        return result;
    }

    public static float getRotationDegrees(String imagePath) {
        try{
            ExifInterface exif = new ExifInterface(imagePath);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
            int rotationDegrees = 0;
            switch (orientation){
                case ExifInterface.ORIENTATION_ROTATE_90 :
                    rotationDegrees = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180 :
                    rotationDegrees = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270 :
                    rotationDegrees = 270;
                    break;
            }

            return (float)rotationDegrees;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static float getRotationDegrees(File file) {
        return getRotationDegrees(file.getAbsolutePath());
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, float degree){
        try{
            Matrix matrix = new Matrix();
            matrix.postRotate(degree);
            Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

            return rotatedBitmap;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Bitmap flipHorizontallyBitmap(Bitmap bitmap){
        try{
            Matrix matrix = new Matrix();
            matrix.setScale(-1, 1);
            Bitmap flippedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

            return flippedBitmap;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Bitmap flipVerticallyBitmap(Bitmap bitmap){
        try{
            Matrix matrix = new Matrix();
            matrix.setScale(1, -1);
            Bitmap flippedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

            return flippedBitmap;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Rect makeRect(Size aspectRatio, Rect boundingRect) {
        float heightPerWidthRatio = (float)(aspectRatio.getHeight()) / aspectRatio.getWidth();

        if (aspectRatio.getWidth() >= aspectRatio.getHeight() ) {
            int width = boundingRect.width();
            int height = (int)(width * heightPerWidthRatio);
            int top = (boundingRect.height() - height) / 2;
            int left = 0;
            int right = left + width;
            int bottom = top + height;

            return new Rect(left, top, right, bottom);
        }
        else {
            int height = boundingRect.height();
            int width = (int)(height / heightPerWidthRatio);
            int left = (boundingRect.width() - width) / 2;
            int top = 0;
            int right = left + width;
            int bottom = top + height;

            return new Rect(left, top, right, bottom);
        }
    }

    public static RectF normalizedRectForImageRect(Rect imageRect, int imageWidth, int imageHeight) {
        float left = (float)imageRect.left / imageWidth;
        float top = (float)imageRect.top / imageHeight;
        float right = (float)imageRect.right / imageWidth;
        float bottom = (float)imageRect.bottom / imageHeight;
        RectF normalizedRect = new RectF(left, top, right, bottom);

        return normalizedRect;
    }

    public static RectF normalizedRectForImageRect(RectF imageRect, int imageWidth, int imageHeight) {
        return normalizedRectForImageRect(new Rect((int)imageRect.left, (int)imageRect.top, (int)imageRect.right, (int)imageRect.bottom), imageWidth, imageHeight);
    }

    public static PointF normalizedPointForImagePoint(Point imagePoint, int imageWidth, int imageHeight) {
        float x = (float)imagePoint.x / imageWidth;
        float y = (float)imagePoint.y / imageHeight;
        PointF normalizedPoint = new PointF(x, y);

        return normalizedPoint;
    }

    public static RectF imageRectForNormalizedRect(RectF normalizedRect, int imageWidth, int imageHeight) {
        float left = normalizedRect.left * imageWidth;
        float top = normalizedRect.top * imageHeight;
        float right = normalizedRect.right * imageWidth;
        float bottom = normalizedRect.bottom * imageHeight;
        RectF imageRect = new RectF(left, top, right, bottom);

        return imageRect;
    }

    public static PointF imagePointForNormalizedPoint(PointF normalizedPoint, int imageWidth, int imageHeight) {
        float x = normalizedPoint.x * imageWidth;
        float y = normalizedPoint.y * imageHeight;
        PointF imagePoint = new PointF(x, y);

        return imagePoint;
    }
}
