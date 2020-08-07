package com.cyy.myapplication;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.cyy.pullrefresh.PullRefreshListView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*Executors.newCachedThreadPool();
        Executors.newFixedThreadPool(10);
        Executors.newScheduledThreadPool(10);
        Executors.newSingleThreadExecutor();*/
        /*PullRefreshListView pullRefreshListView = findViewById(R.id.list_parent);
        ListView listView = findViewById(R.id.list);
        String[] strings = new String[100];
        for (int i = 0; i < strings.length; i++){
            strings[i] = String.valueOf(i);
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.list_item, strings);
        listView.setAdapter(arrayAdapter);*/
        /*try {
            ConcurrencyTest.test();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        int permission = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // 请求权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    10);
        }

        final ImageView imageView = findViewById(R.id.image);
        String url = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1596794025422&di=2df4c3071f880762adb54fbfc5f91dd6&imgtype=0&src=http%3A%2F%2Ft8.baidu.com%2Fit%2Fu%3D1484500186%2C1503043093%26fm%3D79%26app%3D86%26f%3DJPEG%3Fw%3D1280%26h%3D853";
        BitmapCacheUtil.getInstance().getBitmapFromCache(url, new BitmapCacheUtil.OnBitmapLoadFinishListener() {
            @Override
            public void onFinish(final boolean success, final Bitmap bitmap) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (success){
                            imageView.setImageBitmap(bitmap);
                        } else {
                            imageView.setImageResource(R.mipmap.ic_launcher_round);
                        }
                    }
                });
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/aaa.txt";
        System.out.println(path);
        try {
            PrintWriter printWriter = new PrintWriter(path);
            printWriter.println("hello world");
            printWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
