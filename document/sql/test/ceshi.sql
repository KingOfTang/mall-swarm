-- oms-order
ALTER TABLE `mall-test`.oms_order MODIFY COLUMN delete_status int(1) DEFAULT 0 NULL COMMENT '删除状态：0->未删除；1->已删除';
ALTER TABLE `mall-test`.oms_order MODIFY COLUMN receiver_phone varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '收货人电话';
ALTER TABLE `mall-test`.oms_order MODIFY COLUMN receiver_name varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '收货人姓名';
