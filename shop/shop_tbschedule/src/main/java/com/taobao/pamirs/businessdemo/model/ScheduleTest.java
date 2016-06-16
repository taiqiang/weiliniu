/*
 * Copyright 2016-2018 autohome.com.cn All right reserved. This software is the
 * confidential and proprietary information of autohome.com.cn ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with autohome.com.cn
 */
package com.taobao.pamirs.businessdemo.model;

import java.io.Serializable;

/**
 * 类scheduleTest.java的实现描述：TODO 类实现描述 
 * @author yichen 2016年6月16日 下午5:44:12
 */
public class ScheduleTest implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    
    private int id;
    private int dealCount;
    private String sts;
    private String ownSign;
    
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getDealCount() {
        return dealCount;
    }
    public void setDealCount(int dealCount) {
        this.dealCount = dealCount;
    }
    public String getSts() {
        return sts;
    }
    public void setSts(String sts) {
        this.sts = sts;
    }
    public String getOwnSign() {
        return ownSign;
    }
    public void setOwnSign(String ownSign) {
        this.ownSign = ownSign;
    }
    

}
