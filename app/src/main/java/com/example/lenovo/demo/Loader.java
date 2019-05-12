package com.example.lenovo.demo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;


import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class Loader {
    private static final String TAG = "Loader";
    public static final int GET_DATA_SUCCESS = 1;
    public static final int NETWORK_ERROR = 2;
    public static final int SERVER_ERROR = 3;
    private Context mcontext;
    private ImageCompresser imageCompresser = new ImageCompresser();
    private LruCache<String, Bitmap> bitmapLruCache;
    Handler mainhandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GET_DATA_SUCCESS:
                    ArrayList<Object> theListFromMessage = (ArrayList<Object>) msg.obj;
                    Bitmap bitmap =  (Bitmap) theListFromMessage.get(0);
                    ImageView imageView = (ImageView) theListFromMessage.get(1);
                    String url= (String) theListFromMessage.get(2);
                    if (url.equals(imageView.getTag(1))){
                        imageView.setImageBitmap(bitmap);
                    }
                    break;
                case NETWORK_ERROR:
                    Toast.makeText(mcontext, "网络连接失败", Toast.LENGTH_SHORT).show();
                    break;
                case SERVER_ERROR:
                    Toast.makeText(mcontext, "服务器发生错误", Toast.LENGTH_SHORT).show();
                    break;


            }
        }
    };

    public Loader(Context context) {
        mcontext = context.getApplicationContext();//获取应用的Context
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 2014);
        int cacheSize = maxMemory / 8;//计算内存大小
        //构造内存缓存，跟着书上写的
        bitmapLruCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(@NonNull String key, @NonNull Bitmap value) {
                return value.getRowBytes()  / 2014;
            }
        };
    }

    public Bitmap LoadBitmap(String url, int reqWidth, int reqHeigh) {


//下载Bitmap,并放入内存

        Bitmap bitmap = downloadBitmapFromUrl(url, reqWidth, reqHeigh);
        String key = toHashKeyFromUrl(url);
        addBitmapToLruCache(key, bitmap);
        // Log.d(TAG, "加入内存"+key);
        return bitmap;
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void bindBitmap(final String url, final ImageView imageView) {
        Bitmap bitmap = loadFromMemCache(url);
        final int reqWidth=imageView.getMaxWidth();
        final int reqHight=imageView.getMaxHeight();
        imageView.setTag(R.id.always,url);//The key must be an application-specific resource id.???
        // Log.d(TAG, String.valueOf(bitmap[0]==null));

        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap    bitmap = LoadBitmap(url, reqWidth, reqHight);
                if (bitmap!=null) {
                    Message msg = Message.obtain();
                    ArrayList<Object> connect = new ArrayList<>();
                    connect.add(0, bitmap);
                    connect.add(1, imageView);
                    connect.add(2,url);
                    msg.obj = connect;
                    msg.what = GET_DATA_SUCCESS;
                    mainhandler.sendMessage(msg);

                }
            }
        }).start();

    }


    public void bindBitmap(final String url, final ImageView imageView, final int reqWidth, final int reqHight, int placeHolderId) {
        Bitmap bitmap = loadFromMemCache(url);
        imageView.setTag(R.id.barrier,url);
        imageView.setImageResource(placeHolderId);
        // Log.d(TAG, String.valueOf(bitmap[0]==null));

        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap    bitmap = LoadBitmap(url, reqWidth, reqHight);
                if (bitmap!=null) {
                    Message msg = Message.obtain();
                    ArrayList<Object> connect = new ArrayList<>();
                    connect.add(0, bitmap);
                    connect.add(1, imageView);
                    connect.add(2,url);
                    msg.obj = connect;
                    msg.what = GET_DATA_SUCCESS;
                    mainhandler.sendMessage(msg);

                }
            }
        }).start();

    }

    private Bitmap downloadBitmapFromUrl(String urlString, int reqWidth, int reqHight) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            throw new RuntimeException("you can not visit network from UI thread");
        }
        Bitmap bitmap = null;
        HttpURLConnection urlConnection = null;
        BufferedInputStream in = null;
        final URL url;
        try {
            url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();

            in = new BufferedInputStream(urlConnection.getInputStream(), 8 * 1024);
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int n = 0;
            while (-1 != (n = in.read(buffer))) {
                output.write(buffer, 0, n);
            }


            bitmap = imageCompresser.deCodeFromInputSteam(output.toByteArray(), reqWidth, reqHight);
            //   bitmap=BitmapFactory.decodeStream(in);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return bitmap;
    }


    private Bitmap loadFromMemCache(String url) {


        final String key = toHashKeyFromUrl(url);
        Bitmap bitmap = getBitmapFromLruCache(key);
        Log.d(TAG, String.valueOf(bitmap==null));
        return bitmap;

    }


    //把url的转为key,为了方便使用
    //跟着书上写的，不知道是怎么转的，以后再去理解
    private String toHashKeyFromUrl(String url) {
        String HashKey = " ";
        final MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(url.getBytes());
            HashKey = byteToHexString(messageDigest.digest());

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return HashKey;
    }

    //同上，跟着书上写的
    private String byteToHexString(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                stringBuilder.append('0');
            }
            stringBuilder.append(hex);
        }
        return stringBuilder.toString();
    }


    //向内存中加入bitmap
    private void addBitmapToLruCache(String key, Bitmap bitmap) {
        if (getBitmapFromLruCache(key) == null&&key!=null&&bitmap!=null) {  //没有才加入


            bitmapLruCache.put(key, bitmap);

        }

    }

    //获取内存中Bitmap
    private Bitmap getBitmapFromLruCache(String key) {
        return bitmapLruCache.get(key);

    }


    class ImageCompresser {

        public Bitmap deCodeFromInputSteam(byte[] bytes, int reqWidth, int reqHeight) {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);

            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);


        }

        public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
            if (reqHeight == 0 || reqWidth == 0) {
                return 1;
            }
            final int height = options.outHeight;
            final int width = options.outWidth;
            int inSampleSize = 1;
            if (height > reqHeight || width > reqWidth) {
                final int halfHeight = height / 2;
                final int halfWidth = width / 2;
                while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                    inSampleSize *= 2;
                }
            }

            return inSampleSize;
        }


    }

}
