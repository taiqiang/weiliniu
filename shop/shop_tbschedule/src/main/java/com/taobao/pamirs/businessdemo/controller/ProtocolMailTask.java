package com.taobao.pamirs.businessdemo.controller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.taobao.pamirs.businessdemo.model.scheduleTest;
import com.taobao.pamirs.schedule.IScheduleTaskDealSingle;
import com.taobao.pamirs.schedule.TaskItemDefine;

public class ProtocolMailTask implements IScheduleTaskDealSingle<scheduleTest> {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	
	@Override
	public Comparator<scheduleTest> getComparator() {
		return null;
	}

	@Override
	public List<scheduleTest> selectTasks(String taskParameter, String ownSign,
			int taskItemNum, List<TaskItemDefine> taskItemList,
			int eachFetchDataNum)
			throws Exception {
		List<scheduleTest> scheduleTestList = new ArrayList<scheduleTest>();
		return scheduleTestList;
	}

	@Override
	public boolean execute(scheduleTest scheduleTest, String ownSign) throws Exception {
		try{
			return true;
		}catch(Exception e){
		    logger.error("执行业务方法错误!");
		}
		return false;
	}

}
