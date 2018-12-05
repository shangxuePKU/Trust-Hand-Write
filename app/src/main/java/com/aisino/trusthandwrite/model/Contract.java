package com.aisino.trusthandwrite.model;

import android.content.Context;

import com.aisino.trusthandwrite.data.ImageMemoryCache;
import com.google.gson.annotations.SerializedName;

/**
 * Created by HXQ on 2017/5/3.
 */

public class Contract {
    private long contractId;//合同ID
    private String name;//合同名称
    private int status;//合同状态
    private String lastOpTime;//合同最后操作时间
    private int pages;//合同总页数
    private int page;//当前显示的合同的页数
    private ImageMemoryCache images;//合同的图片
    private boolean isSign;//是否签署
    private boolean isStaticPOS;//是否固定位置签署

    @SerializedName("signPos")
    private StaticSignPos staticSignPos;//固定位置签章的信息

    private String otherData;

    public long getContractId() {
        return contractId;
    }

    public void setContractId(long contractId) {
        this.contractId = contractId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getLastOpTime() {
        return lastOpTime;
    }

    public void setLastOpTime(String lastOpTime) {
        this.lastOpTime = lastOpTime;
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

    public boolean isSign() {
        return isSign;
    }

    public void setSign(boolean sign) {
        isSign = sign;
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

    public String getOtherData() {
        return otherData;
    }

    public void setOtherData(String otherData) {
        this.otherData = otherData;
    }

    public Contract() {
    }

    public Contract(Context context) {
        contractId = -1;
        pages = 0;
        page = 1;
        images = new ImageMemoryCache(context);
        isSign = false;
        isStaticPOS = false;
        staticSignPos = new StaticSignPos();
    }
}
