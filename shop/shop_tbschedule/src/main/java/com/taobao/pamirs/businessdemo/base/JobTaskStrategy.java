package com.taobao.pamirs.businessdemo.base;

import java.util.Properties;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.taobao.pamirs.schedule.strategy.ScheduleStrategy;
import com.taobao.pamirs.schedule.strategy.TBScheduleManagerFactory;
import com.taobao.pamirs.schedule.taskmanager.ScheduleTaskType;
import com.taobao.pamirs.schedule.zk.ZKManager;
import com.taobao.pamirs.schedule.zk.ZKTools;

public class JobTaskStrategy
{

	private static final Logger LOGGER = LoggerFactory.getLogger(JobTaskStrategy.class);

	private static final String IP_STR = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";

	private TBScheduleManagerFactory scheduleManagerFactory;

	public void setScheduleManagerFactory(TBScheduleManagerFactory tbScheduleManagerFactory)
	{
		this.scheduleManagerFactory = tbScheduleManagerFactory;
	}

	/**
	 * 是否更新任务信息
	 */
	private boolean updateTaskType = true;

	/************************** ScheduleStrategy的属性开始 ********************************/

	/**
	 * ip地址,使用英文逗号分隔<br/>
	 * 127.0.0.1和localhost表示所有机器上都可以执行
	 */
	protected String ip = "127.0.0.1";

	/**
	 * 单JVM最大线程组数量<br/>
	 * 默认为1，单JVM最大线程组数量，如果是0，则表示没有限制.每台机器运行的线程组数量 =总量/机器数
	 */
	protected int numOfSingleServer = 1;

	/**
	 * 最大线程组数量<br/>
	 * 默认为10，所有服务器总共运行的最大数量
	 */
	protected int assignNum = 10;

	/**
	 * 服务状态<br/>
	 * 默认暂停，取值为：pause,resume
	 */
	protected String status = ScheduleStrategy.STS_PAUSE;

	/************************** ScheduleStrategy的属性结束 ********************************/

	/************************** ScheduleTaskType的属性开始 ********************************/

	/**
	 * 任务类型
	 */
	protected String baseTaskType;

	/**
	 * 向配置中心更新心跳信息的频率<br/>
	 * 默认2秒
	 */
	protected long heartBeatRate = 2 * 1000;

	/**
	 * 判断一个服务器死亡的周期。为了安全，至少是心跳周期的两倍以上<br/>
	 * 默认20秒
	 */
	protected long judgeDeadInterval = 20 * 1000;

	/**
	 * 当没有数据的时候，休眠的时间<br/>
	 * 默认5秒
	 */
	protected int sleepTimeNoData = 5 * 1000;

	/**
	 * 在每次数据处理完后休眠的时间<br/>
	 * 默认2秒
	 */
	protected int sleepTimeInterval = 2 * 1000;

	/**
	 * 每次获取数据的数量<br/>
	 * 默认500
	 */
	protected int fetchDataNumber = 500;

	/**
	 * 在批处理的时候，每次处理的数据量<br/>
	 * 默认100
	 */
	protected int executeNumber = 100;

	/**
	 * 批处理线程数量<br/>
	 * 默认为1
	 */
	protected int threadNumber = 1;

	/**
	 * 调度器类型
	 */
	protected String processorType = "SLEEP";

	/**
	 * 允许执行的开始时间
	 */
	protected String permitRunStartTime;

	/**
	 * 允许执行的结束时间
	 */
	protected String permitRunEndTime;

	/**
	 * 清除过期环境信息的时间间隔,以天为单位
	 */
	protected double expireOwnSignInterval = 1;

	/**
	 * 处理任务的BeanName
	 */
	protected String dealBeanName;

	/**
	 * 任务bean的参数，由用户自定义格式的字符串
	 */
	protected String taskParameter;

	/**
	 * 任务项,使用英文逗号分隔<br/>
	 * 说明：<br/>
	 * 1、将一个数据表中所有数据的ID按10取模，就将数据划分成了0,1,2,3,4,5,6,7,8,9供10个任务项。<br/>
	 * taskItems = "0,1,2,3,4,5,6,7,8,9" <br/>
	 * 2、将一个目录下的所有文件按文件名称的首字母(不区分大小写)， 就划分成了A,B,C ... X,Y,Z供26个任务项。<br/>
	 * taskItems = "0:{A},1:{B},2:{C} ... 23:{X},24:{Y},25:{Z}" <br/>
	 * 3、任务项是进行任务分配的最小单位。一个任务队列只能由一个ScheduleServer来进行处理。但一个Server可以处理任意数量的任务项。<br/>
	 */
	protected String taskItems = "0";

	/************************** ScheduleTaskType的属性结束 ********************************/

	protected void init()
	{
		//校验参数是否合法
		validate();

		// 初始化ScheduleTaskType
		ScheduleTaskType taskType = initScheduleTaskType();

		// 初始化Strategy
		initScheduleStrategy(taskType);
	}

	/**
	 * 初始化调度任务信息
	 * @return
	 */
	private ScheduleTaskType initScheduleTaskType()
	{
		try
		{
			//每次重启清理原来的任务信息
			while (this.scheduleManagerFactory.isZookeeperInitialSucess() == false)
			{
				LOGGER.info("...............................................");
				Thread.sleep(1000);
			}
			this.scheduleManagerFactory.stopServer(null);
			Thread.sleep(1000);
			try
			{
				this.scheduleManagerFactory.getScheduleDataManager().deleteTaskType(this.baseTaskType);
			} catch (Exception e)
			{
				LOGGER.error(this.baseTaskType + "删除失败", e);
			}

			ScheduleTaskType taskType = new ScheduleTaskType();
			taskType.setDealBeanName(dealBeanName);
			taskType.setTaskItems(ScheduleTaskType.splitTaskItem(taskItems));
			taskType.setBaseTaskType(baseTaskType);
			taskType.setExecuteNumber(executeNumber);
			taskType.setExpireOwnSignInterval(expireOwnSignInterval);
			taskType.setFetchDataNumber(fetchDataNumber);
			taskType.setHeartBeatRate(heartBeatRate);
			taskType.setJudgeDeadInterval(judgeDeadInterval);
			taskType.setPermitRunEndTime(permitRunEndTime);
			taskType.setPermitRunStartTime(permitRunStartTime);
			taskType.setThreadNumber(threadNumber);
			taskType.setSleepTimeNoData(sleepTimeNoData);
			taskType.setSleepTimeInterval(sleepTimeInterval);
			taskType.setExecuteNumber(executeNumber);
			taskType.setProcessorType(processorType);

			this.scheduleManagerFactory.getScheduleDataManager().createBaseTaskType(taskType);
			LOGGER.info(this.baseTaskType + "创建调度任务成功:" + taskType.toString());

			return taskType;
		} catch (Exception e)
		{
			LOGGER.error(this.baseTaskType + "创建调度任务失败", e);
			throw new RuntimeException("更新任务失败", e);
		}
	}

	/**
	 * 初始化调度策略信息
	 * @param taskType 调度任务
	 */
	private void initScheduleStrategy(ScheduleTaskType taskType)
	{
		try
		{
			String strategyName = taskType.getBaseTaskType() + "-strategy";
			String taskName = taskType.getBaseTaskType() + "$TASK";

			//每次重启清理原来的策略信息
			try
			{
				this.scheduleManagerFactory.getScheduleStrategyManager().deleteMachineStrategy(strategyName, true);
			} catch (Exception e)
			{
				LOGGER.error(strategyName + "删除失败", e);
			}

			// 初始化ScheduleStrategy
			ScheduleStrategy strategy = new ScheduleStrategy();
			strategy.setStrategyName(strategyName);
			strategy.setKind(ScheduleStrategy.Kind.Schedule);
			strategy.setTaskName(taskName);
			strategy.setTaskParameter(this.taskParameter);
			strategy.setNumOfSingleServer(this.numOfSingleServer);
			strategy.setAssignNum(this.assignNum);
			strategy.setIPList(this.ip.split(","));

			this.scheduleManagerFactory.getScheduleStrategyManager().createScheduleStrategy(strategy);
			LOGGER.info(strategy.getStrategyName() + "创建调度策略成功:" + strategy.toString());
		} catch (Exception e)
		{
			LOGGER.error(this.baseTaskType + "创建调度策略失败 :", e);
			throw new RuntimeException(this.baseTaskType + "创建调度策略成功失败", e);
		}
	}

	/**
	 * 校验参数是否合法
	 * @return
	 */
	private boolean validate()
	{
		if (StringUtils.isEmpty(this.getBaseTaskType()))
		{
			LOGGER.error("baseTaskType 为空");
			throw new RuntimeException("baseTaskType 为空");
		}

		if (StringUtils.isEmpty(this.getDealBeanName()))
		{
			LOGGER.error("dealBeanName 为空");
			throw new RuntimeException("dealBeanName 为空");
		}

		if (StringUtils.isEmpty(this.getTaskItems()))
		{
			LOGGER.error("taskItems 为空");
			throw new RuntimeException("taskItems 为空");
		}

		if (StringUtils.isEmpty(this.ip))
		{
			LOGGER.error("ip is null");
			throw new RuntimeException("ip is null");
		}

		String[] ipList = this.ip.split(",");
		for (String tmp : ipList)
		{
			if (tmp != null && !tmp.matches(IP_STR) && !"localhost".equals(tmp))
			{
				LOGGER.error("ip 配置错误");
				throw new RuntimeException("ip 配置错误");
			}
		}

		return true;
	}

	public String getIp()
	{
		return ip;
	}

	public void setIp(String ip)
	{
		this.ip = ip;
	}

	public int getNumOfSingleServer()
	{
		return numOfSingleServer;
	}

	public void setNumOfSingleServer(int numOfSingleServer)
	{
		this.numOfSingleServer = numOfSingleServer;
	}

	public int getAssignNum()
	{
		return assignNum;
	}

	public void setAssignNum(int assignNum)
	{
		this.assignNum = assignNum;
	}

	public String getStatus()
	{
		return status;
	}

	public void setStatus(String status)
	{
		if (!ScheduleStrategy.STS_PAUSE.equalsIgnoreCase(status) && !ScheduleStrategy.STS_RESUME.equalsIgnoreCase(status))
		{
			LOGGER.error("status illegal");
			throw new RuntimeException("status illegal");
		}
		this.status = status.toLowerCase();
	}

	public String getBaseTaskType()
	{
		return baseTaskType;
	}

	public void setBaseTaskType(String baseTaskType)
	{
		this.baseTaskType = baseTaskType;
	}

	public long getHeartBeatRate()
	{
		return heartBeatRate;
	}

	public void setHeartBeatRate(long heartBeatRate)
	{
		this.heartBeatRate = heartBeatRate;
	}

	public long getJudgeDeadInterval()
	{
		return judgeDeadInterval;
	}

	public void setJudgeDeadInterval(long judgeDeadInterval)
	{
		this.judgeDeadInterval = judgeDeadInterval;
	}

	public int getSleepTimeNoData()
	{
		return sleepTimeNoData;
	}

	public void setSleepTimeNoData(int sleepTimeNoData)
	{
		this.sleepTimeNoData = sleepTimeNoData;
	}

	public int getSleepTimeInterval()
	{
		return sleepTimeInterval;
	}

	public void setSleepTimeInterval(int sleepTimeInterval)
	{
		this.sleepTimeInterval = sleepTimeInterval;
	}

	public int getFetchDataNumber()
	{
		return fetchDataNumber;
	}

	public void setFetchDataNumber(int fetchDataNumber)
	{
		this.fetchDataNumber = fetchDataNumber;
	}

	public int getExecuteNumber()
	{
		return executeNumber;
	}

	public void setExecuteNumber(int executeNumber)
	{
		this.executeNumber = executeNumber;
	}

	public int getThreadNumber()
	{
		return threadNumber;
	}

	public void setThreadNumber(int threadNumber)
	{
		this.threadNumber = threadNumber;
	}

	public String getPermitRunStartTime()
	{
		return permitRunStartTime;
	}

	public void setPermitRunStartTime(String permitRunStartTime)
	{
		this.permitRunStartTime = permitRunStartTime;
	}

	public String getPermitRunEndTime()
	{
		return permitRunEndTime;
	}

	public void setPermitRunEndTime(String permitRunEndTime)
	{
		this.permitRunEndTime = permitRunEndTime;
	}

	public double getExpireOwnSignInterval()
	{
		return expireOwnSignInterval;
	}

	public void setExpireOwnSignInterval(double expireOwnSignInterval)
	{
		this.expireOwnSignInterval = expireOwnSignInterval;
	}

	public String getDealBeanName()
	{
		return dealBeanName;
	}

	public void setDealBeanName(String dealBeanName)
	{
		this.dealBeanName = dealBeanName;
	}

	public String getTaskParameter()
	{
		return taskParameter;
	}

	public void setTaskParameter(String taskParameter)
	{
		this.taskParameter = taskParameter;
	}

	public String getTaskItems()
	{
		return taskItems;
	}

	public void setTaskItems(String taskItems)
	{
		this.taskItems = taskItems;
	}

	public boolean isUpdateTaskType()
	{
		return updateTaskType;
	}

	public void setUpdateTaskType(boolean updateTaskType)
	{
		this.updateTaskType = updateTaskType;
	}

}
