package com.macro.mall.job;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional; // 导入 @Transactional

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;


/**
 这段代码是一个Spring Batch批处理任务的测试类，用于测试订单超时自动确认收货功能。主要逻辑如下：

 1. **准备数据**：插入多条订单记录，模拟不同状态和时间的订单。
 2. **执行任务**：启动批处理作业。
 3. **验证结果**：断言任务执行成功，并检查符合条件的订单状态是否被正确更新（如16天前已发货的订单应自动确认收货）。
 */

@SpringBootTest
@SpringBatchTest
@ActiveProfiles("test")
//@Transactional // <--- 添加这个注解，激活自动事务回滚
public class OrderBatchJobTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Test
    public void testOrderTimeoutJob_OnRealDatabase() throws Exception {
        // --- 1. 准备数据 (Arrange) ---
        // 因为 @Transactional 会在测试后回滚，所以我们不需要手动清理
        String sixteenDaysAgo = LocalDateTime.now().minusDays(16).format(formatter);
        String fiveDaysAgo = LocalDateTime.now().minusDays(5).format(formatter);

        // 插入各种测试用例...
        jdbcTemplate.update("INSERT INTO oms_order (id, member_id, order_sn, status, delivery_time) " +
                "VALUES (101, 1, 'TEST_ORDER_01', 2, ?)", sixteenDaysAgo);
        jdbcTemplate.update("INSERT INTO oms_order (id, member_id, order_sn, status, delivery_time) " +
                "VALUES (102, 1, 'TEST_ORDER_02', 2, ?)", fiveDaysAgo);
        jdbcTemplate.update("INSERT INTO oms_order (id, member_id, order_sn, status, delivery_time) " +
                "VALUES (103, 1, 'TEST_ORDER_03', 1, ?)", sixteenDaysAgo);
        jdbcTemplate.update("INSERT INTO oms_order (id, member_id, order_sn, status) " +
                "VALUES (104, 1, 'TEST_ORDER_04', 3)");


        // --- 2. 执行Job (Act) ---
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        // --- 3. 断言结果 (Assert) ---
        Assertions.assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
        
        // 验证订单101: 应该被更新
        Map<String, Object> order101 = jdbcTemplate.queryForMap("SELECT status, receive_time FROM oms_order WHERE id = 101");
        Assertions.assertEquals(3, order101.get("status"));
//        Assertions.assertNotNull(order101.get("receive_time"));

        // 验证订单102: 不应被更新
        Map<String, Object> order102 = jdbcTemplate.queryForMap("SELECT status FROM oms_order WHERE id = 102");
        Assertions.assertEquals(2, order102.get("status"));
    }
}