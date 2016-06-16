/*
 * Copyright 2016-2018 autohome.com.cn All right reserved. This software is the
 * confidential and proprietary information of autohome.com.cn ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with autohome.com.cn
 */
package com.taobao.pamirs.businessdemo.dao.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.taobao.pamirs.businessdemo.dao.IBaseCommonDao;

/**
 * 类BaseCommonDao.java的实现描述：TODO 类实现描述 
 * @author yichen 2016年6月16日 下午7:53:03
 */
@Repository
public class BaseCommonDao implements IBaseCommonDao {
    
    private static final Logger logger = LoggerFactory.getLogger(BaseCommonDao.class);
    
    @Autowired
    protected JdbcTemplate jdbcTemplate;

}
