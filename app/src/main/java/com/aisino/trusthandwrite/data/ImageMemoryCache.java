package com.aisino.trusthandwrite.data;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import java.lang.ref.SoftReference;
import java.util.LinkedHashMap;

/**
 * Created by HXQ on 2017/5/4.
 */

public class ImageMemoryCache {
    /**
     * 从内存读取数据速度是最快的，为了更大限度使用内存，这里使用了两层缓存。
     * 硬引用缓存不会轻易被回收，用来保存常用数据，不常用的转入软引用缓存。
     */
    private static final int SOFT_CACHE_SIZE = 15;  //软引用缓存容量
    private static LruCache<Integer, Bitmap> mLruCache;  //硬引用缓存
    private static LinkedHashMap<Integer, SoftReference<Bitmap>> mSoftCache;  //软引用缓存

    public ImageMemoryCache(Context context) {
        int memClass = ((ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
        int cacheSize = 1024 * 1024 * memClass / 4;  //硬引用缓存容量，为系统可用内存的1/4
        mLruCache = new LruCache<Integer, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(Integer key, Bitmap value) {
                if (value != null)
                    return value.getRowBytes() * value.getHeight();
                else
                    return 0;
            }

            @Override
            protected void entryRemoved(boolean evicted, Integer key, Bitmap oldValue, Bitmap newValue) {
                if (oldValue != null)
                    // 硬引用缓存容量满的时候，会根据LRU算法把最近没有被使用的图片转入此软引用缓存
                    mSoftCache.put(key, new SoftReference<Bitmap>(oldValue));
            }
        };
        mSoftCache = new LinkedHashMap<Integer, SoftReference<Bitmap>>(SOFT_CACHE_SIZE, 0.75f, true) {
            private static final long serialVersionUID = 6040103833179403725L;
            @Override
            protected boolean removeEldestEntry(Entry<Integer, SoftReference<Bitmap>> eldest) {
                if (size() > SOFT_CACHE_SIZE){
                    return true;
                }
                return false;
            }
        };
    }

    /**
     * 从缓存中获取图片
     */
    public Bitmap getBitmapFromCache(Integer key) {
        Bitmap bitmap;
        //先从硬引用缓存中获取
        synchronized (mLruCache) {
            bitmap = mLruCache.get(key);
            if (bitmap != null) {
                //如果找到的话，把元素移到LinkedHashMap的最前面，从而保证在LRU算法中是最后被删除
                mLruCache.remove(key);
                mLruCache.put(key, bitmap);
                return bitmap;
            }
        }
        //如果硬引用缓存中找不到，到软引用缓存中找
        synchronized (mSoftCache) {
            SoftReference<Bitmap> bitmapReference = mSoftCache.get(key);
            if (bitmapReference != null) {
                bitmap = bitmapReference.get();
                if (bitmap != null) {
                    //将图片移回硬缓存
                    mLruCache.put(key, bitmap);
                    mSoftCache.remove(key);
                    return bitmap;
                } else {
                    mSoftCache.remove(key);
                }
            }
        }
        return null;
    }

    /**
     * 添加图片到缓存
     */
    public void addBitmapToCache(Integer key, Bitmap bitmap) {
        if (bitmap != null) {
            synchronized (mLruCache) {
                mLruCache.put(key, bitmap);
            }
        }
    }

    public void clearCache() {
        mSoftCache.clear();
    }
}
