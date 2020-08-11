package com.cyy.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;
import android.util.LruCache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 图片的三级缓存工具类
 *
 * @author chenyy
 * @date 2020/8/6
 */

public class BitmapCacheUtil {
    private static final String TAG = BitmapCacheUtil.class.getSimpleName();
    private static LruCache<String, Bitmap> bitmapLruCache;
    private static final String LOCAL_CACHE_PATH = Environment.getExternalStorageDirectory() + File.separator + "BitmapCache" + File.separator;

    static {
        int mCacheSize = (int) (Runtime.getRuntime().maxMemory() / 8);
        bitmapLruCache = new LruCache<String, Bitmap>(mCacheSize){
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount();
            }
        };
    }

    private static class BitmapCacheHolder{
        private static BitmapCacheUtil bitmapCacheUtil = new BitmapCacheUtil();
    }

    public static BitmapCacheUtil getInstance(){
        return BitmapCacheHolder.bitmapCacheUtil;
    }

    private void addBitmap2Cache(String url, Bitmap bitmap, boolean forceUpdate){
        if (bitmapLruCache.get(url) == null || forceUpdate){
            bitmapLruCache.put(url, bitmap);
        }
        File parent = new File(LOCAL_CACHE_PATH);
        parent.mkdirs();
        String path = LOCAL_CACHE_PATH + MD5Util.string2MD5(url) + ".jpg";
        File bitmapFile = new File(path);
        if (!bitmapFile.exists() || forceUpdate){
            addBitmap2LocalFile(bitmapFile, bitmap);
        }
    }

    private void addBitmap2LocalFile(File bitmapFile, Bitmap bitmap) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(bitmapFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getBitmapFromCache(String url, final OnBitmapLoadFinishListener listener){
        if (bitmapLruCache.get(url) != null){
            Log.i(TAG, "load from cache");
            listener.onFinish(true, bitmapLruCache.get(url));
            return;
        }
        String path = LOCAL_CACHE_PATH + MD5Util.string2MD5(url) + ".jpg";
        File bitmapFile = new File(path);
        if (bitmapFile.exists()) {
            try {
                InputStream inputStream = new FileInputStream(bitmapFile);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                bitmapLruCache.put(url, bitmap);
                Log.i(TAG, "load from local file");
                listener.onFinish(true, bitmap);
                return;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        getBitmapFromNetWork(url, listener);
    }

    public void getBitmapFromNetWork(final String url, final OnBitmapLoadFinishListener listener){
        //从网络获取
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                listener.onFinish(false, null);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Bitmap bitmap = BitmapFactory.decodeStream(response.body().byteStream());
                addBitmap2Cache(url, bitmap, false);
                Log.i(TAG, "load from network");
                listener.onFinish(true, bitmap);
            }
        });
    }

    public interface OnBitmapLoadFinishListener{
        void onFinish(boolean success, Bitmap bitmap);
    }
}
