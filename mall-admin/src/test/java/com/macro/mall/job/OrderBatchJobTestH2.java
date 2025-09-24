package com.macro.mall.job;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * 订单超时自动确认收货批处理任务的集成测试
 */
@SpringBootTest
@SpringBatchTest // 启用Spring Batch的测试特定功能
@ActiveProfiles("test") // 激活 "test" 配置，使用H2数据库
public class OrderBatchJobTestH2 {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils; // Spring Batch提供的测试工具类

    @Autowired
    private JdbcTemplate jdbcTemplate; // 用于准备数据和验证结果

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 在每个测试方法执行前，清空并准备测试数据
     */
    @BeforeEach
    public void setupTestData() {
        // 1. 清空oms_order表，确保测试环境干净
        jdbcTemplate.execute("DELETE FROM oms_order");

        // 2. 准备测试数据 (Arrange)
        // 计算16天前的时间
        String sixteenDaysAgo = LocalDateTime.now().minusDays(16).format(formatter);
        // 计算5天前的时间
        String fiveDaysAgo = LocalDateTime.now().minusDays(5).format(formatter);

        // --- 插入需要被处理的订单 (ID: 101) ---
        // 状态为2(已发货)，发货时间为16天前，符合处理条件
        jdbcTemplate.update("INSERT INTO oms_order (id, member_id, order_sn, status, delivery_time) " +
                "VALUES (101, 1, 'TEST_ORDER_01', 2, ?)", sixteenDaysAgo);

        // --- 插入不应被处理的订单 ---
        // 订单102: 状态正确，但发货时间在15天内，不符合时间条件
        jdbcTemplate.update("INSERT INTO oms_order (id, member_id, order_sn, status, delivery_time) " +
                "VALUES (102, 1, 'TEST_ORDER_02', 2, ?)", fiveDaysAgo);

        // 订单103: 时间符合，但状态不是“已发货”，不符合状态条件
        jdbcTemplate.update("INSERT INTO oms_order (id, member_id, order_sn, status, delivery_time) " +
                "VALUES (103, 1, 'TEST_ORDER_03', 1, ?)", sixteenDaysAgo);

        // 订单104: 状态已经是“已完成”，不应被再次处理
        jdbcTemplate.update("INSERT INTO oms_order (id, member_id, order_sn, status, delivery_time) " +
                "VALUES (104, 1, 'TEST_ORDER_04', 3, ?)", sixteenDaysAgo);
    }

    @Test
    public void testOrderTimeoutJob() throws Exception {
        // --- 执行Job (Act) ---
        // 使用 JobLauncherTestUtils 同步启动 Job
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        // --- 断言结果 (Assert) ---

        // 1. 验证Job的最终状态是否为COMPLETED (已完成)
        Assertions.assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());

        // 2. 验证数据库中的数据状态是否符合预期

        // 验证订单101: 应该被更新
        Map<String, Object> order101 = jdbcTemplate.queryForMap("SELECT status, receive_time FROM oms_order WHERE id = 101");
        Assertions.assertEquals(3, order101.get("status")); // 状态应变为3 (已完成)
        Assertions.assertNotNull(order101.get("receive_time")); // 收货时间应被设置

        // 验证订单102: 不应被更新 (因为时间不符合)
        Map<String, Object> order102 = jdbcTemplate.queryForMap("SELECT status, receive_time FROM oms_order WHERE id = 102");
        Assertions.assertEquals(2, order102.get("status")); // 状态应保持为2
        Assertions.assertNull(order102.get("receive_time")); // 收货时间应为null

        // 验证订单103: 不应被更新 (因为状态不符合)
        Map<String, Object> order103 = jdbcTemplate.queryForMap("SELECT status FROM oms_order WHERE id = 103");
        Assertions.assertEquals(1, order103.get("status")); // 状态应保持为1

        // 验证订单104: 不应被更新 (因为已经是完成状态)
        Map<String, Object> order104 = jdbcTemplate.queryForMap("SELECT status FROM oms_order WHERE id = 104");
        Assertions.assertEquals(3, order104.get("status")); // 状态应保持为3
    }
}