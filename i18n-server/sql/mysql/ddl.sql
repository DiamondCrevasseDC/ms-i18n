/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`rpc-provider` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `rpc-provider`;

/*Table structure for table `biz_chart_type` */

DROP TABLE IF EXISTS `biz_chart_type`;

CREATE TABLE `biz_chart_type` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `charttype` varchar(255) DEFAULT NULL COMMENT '图表类型',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UQ_CHARTTYPE` (`charttype`)
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8;

/*Table structure for table `eos_actionlog` */

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

/*Table structure for table `eos_mqerror` */

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

/*Table structure for table `eos_mqrecv_error` */

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

/*Table structure for table `eos_mqrecv_success` */

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

/*Table structure for table `eos_mqsend_error` */

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

/*Table structure for table `eos_mqsend_success` */

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

/*Table structure for table `eos_mqsend_success_bak` */

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

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
