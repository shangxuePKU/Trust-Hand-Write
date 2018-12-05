package com.aisino.trusthandwrite.model;


import com.google.gson.annotations.SerializedName;

/**
 * Created by HXQ on 2017/5/17.
 */

public class StaticSignPos {
    private float left;//固定位置签署x
    private float top;//固定位置签署y
    private float page;//固定位置签署所在页面
    @SerializedName("PageWidth")
    private float pageWidth;//固定位置签署所在页面宽带
    @SerializedName("PageHeight")
    private float pageHeight;//固定位置签署所在页面高度
    private float width;//固定位置签章宽度
    private float height;//固定位置签章高度；

    public float getLeft() {
        return left;
    }

    public void setLeft(float left) {
        this.left = left;
    }

    public float getTop() {
        return top;
    }

    public void setTop(float top) {
        this.top = top;
    }

    public float getPage() {
        return page;
    }

    public void setPage(float page) {
        this.page = page;
    }

    public float getPageWidth() {
        return pageWidth;
    }

    public void setPageWidth(float pageWidth) {
        this.pageWidth = pageWidth;
    }

    public float getPageHeight() {
        return pageHeight;
    }

    public void setPageHeight(float pageHeight) {
        this.pageHeight = pageHeight;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }
}
