package com.aisino.trusthandwrite.model;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.aisino.trusthandwrite.data.ImageMemoryCache;

/**
 * Created by HXQ on 2017/5/31.
 */

public class OtherData {
    private String code;
    private long contractId;
    private String backUrl;
    private int pages;//合同总页数
    private int page;//当前显示的合同的页数
    private ImageMemoryCache images;//合同的图片
    private boolean isStaticPOS;//是否固定位置签署
    private StaticSignPos staticSignPos;//固定位置签章的信息

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public long getContractId() {
        return contractId;
    }

    public void setContractId(long contractId) {
        this.contractId = contractId;
    }

    public String getBackUrl() {
        return backUrl;
    }

    public void setBackUrl(String backUrl) {
        this.backUrl = backUrl;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public ImageMemoryCache getImages() {
        return images;
    }

    public void setImages(ImageMemoryCache images) {
        this.images = images;
    }

    public boolean isStaticPOS() {
        return isStaticPOS;
    }

    public void setStaticPOS(boolean staticPOS) {
        isStaticPOS = staticPOS;
    }

    public StaticSignPos getStaticSignPos() {
        return staticSignPos;
    }

    public void setStaticSignPos(StaticSignPos staticSignPos) {
        this.staticSignPos = staticSignPos;
    }

    public OtherData() {
    }

    public OtherData(Context context) {
        pages = 0;
        page = 1;
        images = new ImageMemoryCache(context);
        isStaticPOS = false;
    }

    public void clear(){
        this.code = null;
        this.backUrl = null;
        images = null;
    }
}
