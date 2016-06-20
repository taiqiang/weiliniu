/*
 * Copyright 2016-2018 autohome.com.cn All right reserved. This software is the
 * confidential and proprietary information of autohome.com.cn ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with autohome.com.cn
 */
package com.taobao.pamirs.businessdemo.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.taobao.pamirs.businessdemo.dao.IBaseCommonDao;
import com.taobao.pamirs.businessdemo.model.ScheduleTest;

/**
 * 类BaseCommonDao.java的实现描述：TODO 类实现描述 
 * @author yichen 2016年6月16日 下午7:53:03
 */
@Repository
public class BaseCommonDao implements IBaseCommonDao {
    
    private static final Logger logger = LoggerFactory.getLogger(BaseCommonDao.class);
    
    @Autowired
    protected JdbcTemplate jdbcTemplate;

    /* (non-Javadoc)
     * @see com.taobao.pamirs.businessdemo.dao.IBaseCommonDao#findScheduleTestList()
     */
    @Override
    public List<ScheduleTest> findScheduleTestList() {
        List<ScheduleTest>  scheduleTestList = new ArrayList<ScheduleTest>();
        logger.debug("jdbcTemplate"+jdbcTemplate.toString()+"\n hashCode"+jdbcTemplate.hashCode());
        List rows = jdbcTemplate.queryForList(" SELECT ID,DEAL_COUNT,STS,OWN_SIGN FROM schedule_test limit 10000"); 
        for(int i=0;i<rows.size();i++){ 
            ScheduleTest scheduleTest = new ScheduleTest();
            Map userMap=(Map) rows.get(i);  
            logger.debug("userMap"+userMap.toString()+"\n hashCode"+userMap.hashCode());
            scheduleTest.setId(Integer.parseInt(String.valueOf(userMap.get("id"))));
            scheduleTest.setDealCount(Integer.parseInt(String.valueOf(userMap.get("DEAL_COUNT"))));
            scheduleTest.setSts(String.valueOf(userMap.get("STS")));
            scheduleTest.setOwnSign(String.valueOf(userMap.get("OWN_SIGN")));
            scheduleTestList.add(scheduleTest);
        }  
        return scheduleTestList;
    }

    /* (non-Javadoc)
     * @see com.taobao.pamirs.businessdemo.dao.IBaseCommonDao#updateScheduleTest()
     */
    @Override
    public void updateScheduleTest(ScheduleTest scheduleTest) {
        logger.debug("userMap"+scheduleTest.toString()+"\n hashCode"+scheduleTest.hashCode());
        int id = scheduleTest.getId();
        int dealCount = scheduleTest.getDealCount();
        String sts = scheduleTest.getSts();
        String ownSign = scheduleTest.getOwnSign();
        jdbcTemplate.update("UPDATE schedule_test SET ID = "+id+", DEAL_COUNT = "+dealCount+",STS='"+sts+"',ownSign='"+ownSign+"' WHERE ID="+id+" ");
    }

}
