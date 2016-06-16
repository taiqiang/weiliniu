package com.taobao.pamirs.businessdemo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import com.taobao.pamirs.businessdemo.base.JobTaskStrategy;

public class ProtocolMailInit extends JobTaskStrategy implements InitializingBean {

	private final Logger logger = LoggerFactory.getLogger(ProtocolMailInit.class);
	
	@Override
	public void afterPropertiesSet() throws Exception {	
		logger.info("ProtocolMailInit...........................");
		init(); //初始化策略和任务信息
	}
}
