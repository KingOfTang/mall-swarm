// src/main/java/com/macro/mall/job/config/OrderBatchJobConfig.java
package com.macro.mall.job.config;

import com.macro.mall.model.OmsOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import javax.sql.DataSource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 订单超时自动确认收货批处理任务配置
 */
@Configuration
public class OrderBatchJobConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderBatchJobConfig.class);

    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    @Autowired
    private DataSource dataSource;

    // --- 1. 定义读取器 (Reader) ---
    @Bean
    public JdbcPagingItemReader<OmsOrder> orderTimeoutReader() {
        LOGGER.info("Initializing orderTimeoutReader...");
        // 使用PagingQueryProvider来构建分页查询
        MySqlPagingQueryProvider queryProvider = new MySqlPagingQueryProvider();
        queryProvider.setSelectClause("id, status, delivery_time");
        queryProvider.setFromClause("from oms_order");
        // 查询条件：状态为“已发货”(2)，且发货时间超过15天
        queryProvider.setWhereClause("status = 2 AND delivery_time < DATE_SUB(NOW(), INTERVAL 15 DAY)");
        
        Map<String, Order> sortKeys = new HashMap<>();
        sortKeys.put("id", Order.ASCENDING);
        queryProvider.setSortKeys(sortKeys); // 分页查询必须指定排序键

        return new JdbcPagingItemReaderBuilder<OmsOrder>()
                .name("orderTimeoutReader")
                .dataSource(dataSource)
                .queryProvider(queryProvider)
                .pageSize(100) // 每次读取100条
                .rowMapper(new BeanPropertyRowMapper<>(OmsOrder.class))
                .build();
    }

    // --- 2. 定义处理器 (Processor) ---
    @Bean
    public ItemProcessor<OmsOrder, OmsOrder> orderTimeoutProcessor() {
        return order -> {
            // 业务逻辑：将订单状态更新为“已完成”(3)，并设置收货时间
            LOGGER.info("Processing order id: {}", order.getId());
            order.setStatus(3); // 3: 已完成
            order.setReceiveTime(new Date());
            return order;
        };
    }

    // --- 3. 定义写入器 (Writer) ---
    @Bean
    public JdbcBatchItemWriter<OmsOrder> orderTimeoutWriter() {
        LOGGER.info("Initializing orderTimeoutWriter...");
        return new JdbcBatchItemWriterBuilder<OmsOrder>()
                .dataSource(dataSource)
                // 更新SQL语句，使用命名参数
//                .sql("UPDATE oms_order SET status = :status, receive_time = :receive_time WHERE id = :id")
                .sql("UPDATE oms_order SET status = :status WHERE id = :id")
                // 从OmsOrder对象中提取参数
                .beanMapped()
                .build();
    }

    // --- 4. 定义步骤 (Step) ---
    @Bean
    public Step orderTimeoutStep() {
        LOGGER.info("Building orderTimeoutStep...");
        return stepBuilderFactory.get("orderTimeoutStep")
                .<OmsOrder, OmsOrder>chunk(100) // 批处理块大小，与Reader的pageSize一致
                .reader(orderTimeoutReader())
                .processor(orderTimeoutProcessor())
                .writer(orderTimeoutWriter())
                .build();
    }

    // --- 5. 定义作业 (Job) ---
    @Bean
    public Job orderTimeoutJob() {
        LOGGER.info("Building orderTimeoutJob...");
        return jobBuilderFactory.get("orderTimeoutJob")
                .incrementer(new RunIdIncrementer()) // 使用ID增量器，确保每次运行Job实例唯一
                .flow(orderTimeoutStep())
                .end()
                .build();
    }

}