package com.aisino.trusthandwrite.model;

/**
 * Created by HXQ on 2017/5/16.
 */

public class Template {
    private long customerTemplateId;
    private long customerId;
    private String name;
    private String descr;
    private String createTime;
    private String updateTime;

    public long getCustomerTemplateId() {
        return customerTemplateId;
    }

    public void setCustomerTemplateId(long customerTemplateId) {
        this.customerTemplateId = customerTemplateId;
    }

    public long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(long customerId) {
        this.customerId = customerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}
