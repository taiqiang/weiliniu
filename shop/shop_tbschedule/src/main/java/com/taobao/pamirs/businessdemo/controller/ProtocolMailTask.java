package com.taobao.pamirs.businessdemo.controller;

import java.util.Comparator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.taobao.pamirs.businessdemo.base.SpringContextUtil;
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
	    IBaseCommonDao baseCommonDao = (IBaseCommonDao) SpringContextUtil.getBean("baseCommonDao");
	    logger.debug("查询业务数据开始");
		List<ScheduleTest> scheduleTestList = baseCommonDao.findScheduleTestList();
		int length = scheduleTestList.size();
		logger.debug("查询业务数据结束");
		logger.debug("查询业务数据长度为"+length);
		System.err.println(length);
		return scheduleTestList;
	}

	@Override
	public boolean execute(ScheduleTest scheduleTest, String ownSign) throws Exception {
		try{
		    IBaseCommonDao baseCommonDao = (IBaseCommonDao) SpringContextUtil.getBean("baseCommonDao");
		    scheduleTest.setSts("Y");
		    logger.debug("修改业务方法开始");
		    System.err.println("修改业务方法开始");
		    baseCommonDao.updateScheduleTest(scheduleTest);
		    logger.debug("修改业务方法结束");
		    System.err.println("修改业务方法结束");
			return true;
		}catch(Exception e){
		    logger.error("执行业务方法错误!");
		}
		return false;
	}

}
