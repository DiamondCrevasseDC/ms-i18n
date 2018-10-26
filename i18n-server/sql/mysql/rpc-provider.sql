/*
 Navicat Premium Data Transfer

 Source Server         : 172.20.23.244
 Source Server Type    : MySQL
 Source Server Version : 50556
 Source Host           : 172.20.23.244
 Source Database       : rpc-provider

 Target Server Type    : MySQL
 Target Server Version : 50556
 File Encoding         : utf-8

 Date: 09/30/2018 13:57:07 PM
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
--  Table structure for `base_attachment`
-- ----------------------------
DROP TABLE IF EXISTS `base_attachment`;
CREATE TABLE `base_attachment` (
  `id` varchar(64) NOT NULL,
  `filename` varchar(100) DEFAULT NULL,
  `accessaddress` varchar(100) DEFAULT NULL,
  `refid` varchar(100) DEFAULT NULL,
  `refname` varchar(100) DEFAULT NULL,
  `create_time` varchar(64) DEFAULT NULL,
  `create_user` varchar(64) DEFAULT NULL,
  `last_modified` varchar(64) DEFAULT NULL,
  `last_modify_user` varchar(64) DEFAULT NULL,
  `ts` varchar(64) DEFAULT NULL,
  `dr` int(1) DEFAULT NULL,
  `originalfilename` varchar(64) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
--  Records of `base_attachment`
-- ----------------------------
BEGIN;
INSERT INTO `base_attachment` VALUES ('382478bd271343eb801ac72833f8c1f6', null, null, 'bb0380a00c904c6cb38d6f75534b493d', 'I18n', '2018-09-28 20:20:32', null, '2018-09-28 20:20:32', null, '2018-09-28 20:20:32', '0', 'base_attachment.sql'), ('218ec325c7524958821adc85799fb4fd', null, null, 'd85b63bbfbf54d458bc5f8d81c1446a5', 'I18n', '2018-09-28 20:28:51', null, '2018-09-28 20:28:51', null, '2018-09-28 20:28:51', '0', 'base_attachment.sql'), ('ae733eb1dd814f14806fa210a736d83a', null, null, 'bfea2e9d2658486d849e937227c6aecd', 'I18n', '2018-09-28 21:29:58', null, '2018-09-28 21:29:58', null, '2018-09-28 21:29:58', '0', 'base_attachment.sql'), ('3d16ef60d8bd4ef181c4aeff6072d92e', null, null, '7b53c555723f41efbe9e9b53a0d6c1ae', 'I18n', '2018-09-28 22:01:57', null, '2018-09-28 22:01:57', null, '2018-09-28 22:01:57', '0', 'Java Is Still Free.pdf'), ('482c5b633aea4a46aa0cd1fd0a62cc27', '23b3d9f6-195e-4339-a226-8c77e8c9044c_.sql', 'http://172.20.23.170:8080/wbalone/images/23b3d9f6-195e-4339-a226-8c77e8c9044c_.sql', '2b38eb416758433796be2b887f4f0d35', 'I18n', '2018-09-28 22:05:48', null, '2018-09-28 22:05:48', null, '2018-09-28 22:05:48', '0', 'base_attachment.sql'), ('97b02591f21043f8a2bb263643f291fb', 'cd1e3644-2920-480a-a7cc-6ac0b09e40d3_.pdf', 'http://localhost:9688/I18n/resources/cd1e3644-2920-480a-a7cc-6ac0b09e40d3_.pdf', '579f6be1a1d7436aa4938c99c27578c2', 'I18n', '2018-09-28 22:18:54', null, '2018-09-28 22:18:54', null, '2018-09-28 22:18:54', '0', 'Java Is Still Free.pdf');
COMMIT;

-- ----------------------------
--  Table structure for `biz_chart_type`
-- ----------------------------
DROP TABLE IF EXISTS `biz_chart_type`;
CREATE TABLE `biz_chart_type` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `charttype` varchar(255) DEFAULT NULL COMMENT '图表类型',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UQ_CHARTTYPE` (`charttype`)
) ENGINE=InnoDB AUTO_INCREMENT=64 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Records of `biz_chart_type`
-- ----------------------------
BEGIN;
INSERT INTO `biz_chart_type` VALUES ('33', '1'), ('55', '11'), ('57', '12'), ('59', '13'), ('63', '15'), ('36', '2'), ('41', '4'), ('43', '5'), ('50', '8'), ('52', '9');
COMMIT;

-- ----------------------------
--  Table structure for `eos_actionlog`
-- ----------------------------
DROP TABLE IF EXISTS `eos_actionlog`;
CREATE TABLE `eos_actionlog` (
  `pk` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'DB主键,为性能提升',
  `id` varchar(36) NOT NULL COMMENT '业务主键',
  `src_provider_id` varchar(36) DEFAULT NULL COMMENT '源租户ID',
  `dest_provider_id` varchar(36) DEFAULT NULL COMMENT '目标租户ID',
  `own_provider_id` varchar(36) DEFAULT NULL COMMENT '事务所属租户ID',
  `src_app_code` varchar(36) DEFAULT NULL COMMENT '调用来源应用编码',
  `dest_app_code` varchar(36) DEFAULT NULL COMMENT '调用目标应用编码',
  `own_app_code` varchar(36) DEFAULT NULL COMMENT '事务所属应用编码',
  `interface_name` varchar(255) DEFAULT NULL COMMENT 'RPC服务接口名称',
  `method_name` varchar(100) DEFAULT NULL COMMENT 'RPC服务接口的方法名',
  `env` varchar(20) DEFAULT NULL COMMENT 'SDK中指定的调用环境',
  `gtx_id` varchar(36) DEFAULT NULL COMMENT '全局事务ID',
  `ptx_id` varchar(36) DEFAULT NULL COMMENT '父事务ID',
  `tx_id` varchar(36) DEFAULT NULL COMMENT '本次调用事务ID',
  `msg_id` varchar(36) DEFAULT NULL COMMENT '消息ID',
  `err_log` varchar(10000) DEFAULT NULL COMMENT '错误事务简单日志',
  `status` varchar(20) DEFAULT NULL COMMENT '事务状态编码（对应枚举类）',
  `sync_direction` varchar(20) DEFAULT NULL COMMENT '同步方向，sdk和云端同步时使用',
  `create_time` bigint(20) DEFAULT NULL COMMENT '错误事务初始入库时间，不能修改',
  `update_time` bigint(20) DEFAULT NULL COMMENT '更新时间，每次修改状态或者字段时候更新',
  `version` int(11) DEFAULT NULL COMMENT '乐观锁version',
  PRIMARY KEY (`pk`),
  UNIQUE KEY `UQ_ID` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8 COMMENT='EOS 异步调用错误日志实体类，一条错误日志代表一次错误的异步调用事务';

-- ----------------------------
--  Table structure for `eos_mqerror`
-- ----------------------------
DROP TABLE IF EXISTS `eos_mqerror`;
CREATE TABLE `eos_mqerror` (
  `pk` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'DB主键,为性能提升',
  `id` varchar(36) NOT NULL COMMENT '业务错误日志主键',
  `logtype` varchar(20) DEFAULT NULL COMMENT '日志类型(0:发送日志,1:接收日志)',
  `mqid` varchar(100) DEFAULT NULL COMMENT '消息id',
  `errlog` varchar(10000) DEFAULT NULL COMMENT '错误日志消息',
  `ts` bigint(20) DEFAULT NULL COMMENT '当前时间',
  `status` varchar(20) DEFAULT NULL COMMENT '未归档/已归档',
  PRIMARY KEY (`pk`),
  UNIQUE KEY `UQ_ID` (`id`),
  KEY `IDX_MQID` (`mqid`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `eos_mqrecv_error`
-- ----------------------------
DROP TABLE IF EXISTS `eos_mqrecv_error`;
CREATE TABLE `eos_mqrecv_error` (
  `pk` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'DB主键,为性能提升',
  `id` varchar(36) NOT NULL COMMENT 'MQID-UUID-主键',
  `txid` varchar(36) DEFAULT NULL COMMENT '事务ID-UUID',
  `ptxid` varchar(36) DEFAULT NULL COMMENT '父事务ID-UUID',
  `gtxid` varchar(36) DEFAULT NULL COMMENT '全局事务ID-UUID',
  `srcqueue` varchar(100) DEFAULT NULL COMMENT '发送队列名称=应用编码-租户ID-环境',
  `destqueue` varchar(100) DEFAULT NULL COMMENT '接收队列名称=应用编码-租户ID-环境',
  `content` varchar(20000) DEFAULT NULL COMMENT '消息内容',
  `createtime` bigint(20) DEFAULT NULL COMMENT '创建时间',
  `updatetime` bigint(20) DEFAULT NULL COMMENT '更新时间',
  `status` varchar(20) DEFAULT NULL COMMENT '状态(0:失败,1成功)',
  `sync` varchar(20) DEFAULT NULL COMMENT '同步状态(0未同步,1已同步)',
  PRIMARY KEY (`pk`),
  UNIQUE KEY `UQ_ID` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `eos_mqrecv_success`
-- ----------------------------
DROP TABLE IF EXISTS `eos_mqrecv_success`;
CREATE TABLE `eos_mqrecv_success` (
  `pk` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'DB主键,为性能提升',
  `id` varchar(36) NOT NULL COMMENT '业务MQID-UUID-主键',
  `txid` varchar(36) DEFAULT NULL COMMENT '事务ID-UUID',
  `ptxid` varchar(36) DEFAULT NULL COMMENT '父事务ID-UUID',
  `gtxid` varchar(36) DEFAULT NULL COMMENT '全局事务ID-UUID',
  `srcqueue` varchar(100) DEFAULT NULL COMMENT '发送队列名称=应用编码-租户ID-环境',
  `destqueue` varchar(100) DEFAULT NULL COMMENT '接收队列名称=应用编码-租户ID-环境',
  `content` varchar(20000) DEFAULT NULL COMMENT '消息内容',
  `createtime` bigint(20) DEFAULT NULL COMMENT '创建时间',
  `updatetime` bigint(20) DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`pk`),
  UNIQUE KEY `UQ_ID` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `eos_mqsend_error`
-- ----------------------------
DROP TABLE IF EXISTS `eos_mqsend_error`;
CREATE TABLE `eos_mqsend_error` (
  `pk` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'DB主键,为性能提升',
  `id` varchar(36) NOT NULL COMMENT '主键-UUID',
  `txid` varchar(36) DEFAULT NULL COMMENT '事务ID-UUID',
  `ptxid` varchar(36) DEFAULT NULL COMMENT '父事务ID-UUID',
  `gtxid` varchar(36) DEFAULT NULL COMMENT '全局事务ID-UUID',
  `srcqueue` varchar(100) DEFAULT NULL COMMENT '发送队列名称=应用编码-租户ID-环境',
  `destqueue` varchar(100) DEFAULT NULL COMMENT '接收队列名称=应用编码-租户ID-环境',
  `content` varchar(20000) DEFAULT NULL COMMENT '消息内容',
  `createtime` bigint(20) DEFAULT NULL COMMENT '创建时间',
  `updatetime` bigint(20) DEFAULT NULL COMMENT '更新时间',
  `status` varchar(20) DEFAULT NULL COMMENT '状态(0失败,1成功)',
  `sync` varchar(20) DEFAULT NULL COMMENT '同步状态(0未同步,1已同步)',
  PRIMARY KEY (`pk`),
  UNIQUE KEY `UQ_ID` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `eos_mqsend_success`
-- ----------------------------
DROP TABLE IF EXISTS `eos_mqsend_success`;
CREATE TABLE `eos_mqsend_success` (
  `pk` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'DB主键(为性能提升)',
  `id` varchar(36) NOT NULL COMMENT '业务主键-UUID',
  `txid` varchar(36) DEFAULT NULL COMMENT '事务ID-UUID',
  `ptxid` varchar(36) DEFAULT NULL COMMENT '父事务ID-UUID',
  `gtxid` varchar(36) DEFAULT NULL COMMENT '全局事务ID-UUID',
  `srcqueue` varchar(100) DEFAULT NULL COMMENT '发送队列名称=应用编码-租户ID-环境',
  `destqueue` varchar(100) DEFAULT NULL COMMENT '接收队列名称=应用编码-租户ID-环境',
  `content` varchar(20000) DEFAULT NULL COMMENT '消息内容',
  `createtime` bigint(20) DEFAULT NULL COMMENT '创建时间',
  `updatetime` bigint(20) DEFAULT NULL COMMENT '更新时间',
  `status` varchar(20) NOT NULL COMMENT '发送状态(0待发送,1发送中,2发送成功)',
  PRIMARY KEY (`pk`),
  UNIQUE KEY `UQ_ID` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=110505885 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `eos_mqsend_success_bak`
-- ----------------------------
DROP TABLE IF EXISTS `eos_mqsend_success_bak`;
CREATE TABLE `eos_mqsend_success_bak` (
  `pk` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '伪主键(为性能提升)',
  `id` varchar(36) NOT NULL COMMENT '主键-UUID',
  `txid` varchar(36) DEFAULT NULL COMMENT '事务ID-UUID',
  `ptxid` varchar(36) DEFAULT NULL COMMENT '父事务ID-UUID',
  `gtxid` varchar(36) DEFAULT NULL COMMENT '全局事务ID-UUID',
  `srcqueue` varchar(100) DEFAULT NULL COMMENT '发送队列名称=应用编码-租户ID-环境',
  `destqueue` varchar(100) DEFAULT NULL COMMENT '接收队列名称=应用编码-租户ID-环境',
  `content` varchar(20000) DEFAULT NULL COMMENT '消息内容',
  `createtime` bigint(20) DEFAULT NULL COMMENT '创建时间',
  `updatetime` bigint(20) DEFAULT NULL COMMENT '更新时间',
  `status` varchar(20) NOT NULL COMMENT '发送状态(0待发送,1发送中,2发送成功)',
  PRIMARY KEY (`pk`),
  UNIQUE KEY `UQ_ID` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `i18n`
-- ----------------------------
DROP TABLE IF EXISTS `i18n`;
CREATE TABLE `i18n` (
  `ID` varchar(64) NOT NULL COMMENT '主键',
  `NAME` varchar(64) DEFAULT NULL COMMENT '名称',
  `DR` int(11) DEFAULT NULL COMMENT '是否删除',
  `TS` varchar(64) DEFAULT NULL COMMENT '时间戳',
  `LAST_MODIFIED` varchar(64) DEFAULT NULL COMMENT '修改时间',
  `LAST_MODIFY_USER` varchar(64) DEFAULT NULL COMMENT '修改人',
  `CREATE_TIME` varchar(64) DEFAULT NULL COMMENT '创建时间',
  `CREATE_USER` varchar(64) DEFAULT NULL COMMENT '创建人',
  `attach_id` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT;

-- ----------------------------
--  Records of `i18n`
-- ----------------------------
BEGIN;
INSERT INTO `i18n` VALUES ('2b38eb416758433796be2b887f4f0d35', 'aaa ', '0', '2018-09-28 22:05:48 761', '2018-09-28 22:05:48 761', null, '2018-09-28 22:05:48 761', null, null), ('579f6be1a1d7436aa4938c99c27578c2', 'aaaaa', '0', '2018-09-28 22:18:54 176', '2018-09-28 22:18:54 176', null, '2018-09-28 22:18:54 176', null, null), ('7b53c555723f41efbe9e9b53a0d6c1ae', 'qwqw', '0', '2018-09-28 22:01:57 527', '2018-09-28 22:01:57 527', null, '2018-09-28 22:01:57 527', null, null), ('bb0380a00c904c6cb38d6f75534b493d', '啊啊', '0', '2018-09-28 20:20:31 900', '2018-09-28 20:20:31 900', null, '2018-09-28 20:20:31 900', null, null), ('bfea2e9d2658486d849e937227c6aecd', 'test', '0', '2018-09-28 21:29:58 207', '2018-09-28 21:29:58 207', null, '2018-09-28 21:29:58 207', null, null), ('d85b63bbfbf54d458bc5f8d81c1446a5', '组织', '0', '2018-09-28 20:28:51 106', '2018-09-28 20:28:51 106', null, '2018-09-28 20:28:51 106', null, null);
COMMIT;

-- ----------------------------
--  Table structure for `pub_filesystem`
-- ----------------------------
DROP TABLE IF EXISTS `pub_filesystem`;
CREATE TABLE `pub_filesystem` (
  `id` varchar(36) NOT NULL,
  `pkfile` varchar(100) NOT NULL,
  `filename` varchar(100) NOT NULL,
  `filepath` varchar(100) NOT NULL,
  `filesize` varchar(100) NOT NULL,
  `groupname` varchar(100) NOT NULL,
  `permission` varchar(20) NOT NULL,
  `uploader` varchar(36) DEFAULT NULL,
  `uploadtime` varchar(100) DEFAULT NULL,
  `sysid` varchar(100) DEFAULT NULL,
  `tenant` varchar(100) DEFAULT NULL,
  `modular` varchar(100) DEFAULT NULL,
  `url` varchar(1000) DEFAULT NULL,
  `SOURCETENANT` varchar(100) DEFAULT NULL,
  `secretkey` varchar(1000) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Records of `pub_filesystem`
-- ----------------------------
BEGIN;
INSERT INTO `pub_filesystem` VALUES ('4NqmMz3bfVRkG82ij3h', 'publictenant/664fa1cf-1eb5-46b2-9d49-4a63ff36e3e8_PUB_FILESYSTEM.sql', 'PUB_FILESYSTEM.sql', 'code', '1997', 'single', 'read', null, '2018-09-28 12:56:58', null, null, 'iuap', 'http://172.20.23.170:9000/iuap-saas-filesystem-service/publictenant/664fa1cf-1eb5-46b2-9d49-4a63ff36e3e8.sql', null, null), ('cac6e3f2-a953-4fd9-a397-5f01644ed8fb', 'publictenant/e8742a12-4be8-4724-84b5-f35dc0c9d98f_JAVA常规问题.pdf', 'JAVA常规问题.pdf', 'code', '85263', 'single', 'read', null, '2018-09-28 11:27:39', null, null, 'iuap', 'http://172.20.23.170:9000/iuap-saas-filesystem-service/publictenant/e8742a12-4be8-4724-84b5-f35dc0c9d98f.pdf', null, null), ('JaHGMSBcSUbNrljhuMO', 'publictenant/22ada248-397e-49a1-b58f-49187012aa82_Java Is Still Free.pdf', 'Java Is Still Free.pdf', 'code', '347016', 'single', 'read', null, '2018-09-28 11:14:57', null, null, 'iuap', 'http://172.20.23.170:9000/iuap-saas-filesystem-service/publictenant/22ada248-397e-49a1-b58f-49187012aa82.pdf', null, null), ('L2efUxZP88Zom0QEvEg', 'publictenant/e8742a12-4be8-4724-84b5-f35dc0c9d98f_JAVA常规问题.pdf', 'JAVA常规问题.pdf', 'code', '85263', 'single', 'read', null, '2018-09-28 11:27:39', null, null, 'iuap', 'http://172.20.23.170:9000/iuap-saas-filesystem-service/publictenant/e8742a12-4be8-4724-84b5-f35dc0c9d98f.pdf', null, null), ('XTs0UcUcm1pN9bEKxZ4', 'publictenant/2324fb30-0a87-428b-afe0-ed7d1b0bc853_JAVA常规问题.docx', 'JAVA常规问题.docx', 'code', '15747', 'single', 'read', null, '2018-09-28 11:43:55', null, null, 'iuap', 'http://172.20.23.170:9000/iuap-saas-filesystem-service/publictenant/2324fb30-0a87-428b-afe0-ed7d1b0bc853.docx', null, null);
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
