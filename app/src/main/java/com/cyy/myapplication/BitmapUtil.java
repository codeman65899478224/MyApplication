package com.cyy.myapplication;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.FileDescriptor;
import java.io.FileOutputStream;

/**
 * @author chenyy
 * @date 2020/7/16
 */

public class BitmapUtil {

    /**
     * 通过option压缩图片
     * @param bitmap 图片的bitmap
     * @param width 要求的宽度
     * @param height 要求的高度
     * @return 压缩后的bitmap
     */
    public static Bitmap decodeBitmap(Bitmap bitmap, int width, int height) {
        FileDescriptor fd = new FileDescriptor();
        FileOutputStream fileOutputStream = new FileOutputStream(fd);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFileDescriptor(fd, null, options);
        //选取合适的图片格式
        //options.inPreferredConfig = Bitmap.Config.RGB_565;
        //计算合适的采样率
        options.inSampleSize = calculateSampleSize(options, width, height);

        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFileDescriptor(fd, null, options);
    }

    private static int calculateSampleSize(BitmapFactory.Options options, int width, int height) {
        int inSampleSize = 1;
        while (options.outWidth / (inSampleSize * 2) >= width && options.outHeight / (inSampleSize * 2) >= height) {
            inSampleSize *= 2;
        }
        return inSampleSize;
    }

    /**
     * 通过option压缩图片
     * @param res 应用的Resources
     * @param resId 图片资源id
     * @param width 要求的宽度
     * @param height 要求的高度
     * @return 压缩后的bitmap
     */
    public static Bitmap decodeBitmapFromResource(Resources res, int resId, int width, int height) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);
        //选取合适的图片格式
        //options.inPreferredConfig = Bitmap.Config.RGB_565;
        //计算合适的采样率
        options.inSampleSize = calculateSampleSize(options, width, height);
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeResource(res, resId, options);
    }

}
