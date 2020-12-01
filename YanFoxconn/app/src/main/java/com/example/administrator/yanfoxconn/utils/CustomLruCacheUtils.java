package com.example.administrator.yanfoxconn.utils;

import android.graphics.Bitmap;
import android.util.LruCache;

/**
 * 緩存工具類
 * Created by song on 2017/9/19.
 */

public class CustomLruCacheUtils {private LruCache<String, Bitmap> stringBitmapLruCache;
    int maxMemory = (int) Runtime.getRuntime().maxMemory();//获取最大内存
    int cacheSize = maxMemory / 16;//大小为最大内存的1/16
    private static CustomLruCacheUtils customLruCache;

    /**
     * 私有化构造方法
     */
    private CustomLruCacheUtils() {
        stringBitmapLruCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };
    }

    /**
     * 单例模式获取实例，保证只有一个CustomLruCache对象，同时保证只有一个CustomLruCache.stringBitmapLruCache
     *
     * @return
     */
    public static CustomLruCacheUtils getInstance() {
        if (customLruCache == null) {
            customLruCache = new CustomLruCacheUtils();
        }
        return customLruCache;
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemoryCache(key) != bitmap)//如果缓存中不存在bitmap,就存入缓存
            stringBitmapLruCache.put(key, bitmap);
    }

    public Bitmap getBitmapFromMemoryCache(String key) {
        return stringBitmapLruCache.get(key);
    }
}