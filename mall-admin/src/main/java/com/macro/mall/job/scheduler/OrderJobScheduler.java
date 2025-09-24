// src/main/java/com/macro/mall/job/scheduler/OrderJobScheduler.java
package com.macro.mall.job.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 订单批处理任务调度器
 */
@Component
public class OrderJobScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderJobScheduler.class);

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job orderTimeoutJob; // Spring会自动注入我们上面定义的Job Bean

    /**
     * CRON表达式：每天凌晨1点执行
     * 格式: [秒] [分] [时] [日] [月] [周]
     */
//    @Scheduled(cron = "0 0 1 * * ?")
    @Scheduled(cron = "0 3 * * * ?")
    public void handleOrderTimeout() {
        LOGGER.info("Starting orderTimeoutJob execution...");
        try {
            // 使用JobParametersBuilder来创建唯一的JobParameters，确保每次任务都可执行
            // 我们添加一个时间戳作为参数，使其每次都不同
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();
            
            jobLauncher.run(orderTimeoutJob, jobParameters);
            LOGGER.info("orderTimeoutJob execution finished successfully.");
        } catch (Exception e) {
            LOGGER.error("Exception while running orderTimeoutJob", e);
        }
    }
}