package net.ideahut.springboot.template.job;

import org.quartz.JobExecutionContext;

import net.ideahut.springboot.job.JobBase;
import net.ideahut.springboot.job.dto.JobTriggerDto;

public class PrintSystemNanoJob extends JobBase {

	@Override
	protected void run(JobExecutionContext context) throws Exception {
		String prefix = getConfigValue(context, String.class, "PREFIX", "---");
	
		JobTriggerDto trigger = getTrigger(context);
		String lastRunData = trigger.getLastRunData();
		Long lasRunTime = trigger.getLastRunTime();
		logger().info("++ lastRunData: {}", lastRunData);
		logger().info("++ lasRunTime: {}", lasRunTime);
		
		String currentData = prefix + System.nanoTime();
		context.setResult(currentData);
		logger().info("++ currentData: {}", currentData);
		String str = prefix + System.nanoTime();
		logger().info("{}", str);
	}

}
