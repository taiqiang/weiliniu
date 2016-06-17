package com.taobao.pamirs.businessdemo.controller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.taobao.pamirs.businessdemo.dao.IBaseCommonDao;
import com.taobao.pamirs.businessdemo.model.ScheduleTest;
import com.taobao.pamirs.schedule.IScheduleTaskDealSingle;
import com.taobao.pamirs.schedule.TaskItemDefine;

public class ProtocolMailTask implements IScheduleTaskDealSingle<ScheduleTest> {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	
	@Override
	public Comparator<ScheduleTest> getComparator() {
		return null;
	}

	@Override
	public List<ScheduleTest> selectTasks(String taskParameter, String ownSign,
			int taskItemNum, List<TaskItemDefine> taskItemList,
			int eachFetchDataNum)
			throws Exception {
	    IBaseCommonDao baseCommonDao = (IBaseCommonDao) SpringContextUtil.getBean("carArchivesService");
		List<ScheduleTest> scheduleTestList = new ArrayList<ScheduleTest>();
		return scheduleTestList;
	}

	@Override
	public boolean execute(ScheduleTest scheduleTest, String ownSign) throws Exception {
		try{
			return true;
		}catch(Exception e){
		    logger.error("执行业务方法错误!");
		}
		return false;
	}

}
