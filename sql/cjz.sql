CREATE TABLE `fwddzb_ex` (
  `guid` varchar(38) NOT NULL COMMENT '主键',
  `kqzyh` double(14,2) DEFAULT NULL COMMENT '快去住优惠',
  `xpze` double(14,2) DEFAULT NULL COMMENT '选配总额',
  `jdyh` double(14,2) DEFAULT NULL COMMENT '酒店优惠',
  `xszf` double(4,3) DEFAULT NULL COMMENT '线上支付',
  `fyjl` double(4,3) DEFAULT NULL COMMENT '房佣金率',
  `pyjl` double(4,3) DEFAULT NULL COMMENT '配佣金率',
  `hysx` varchar(50) DEFAULT NULL COMMENT '会员属性',
  PRIMARY KEY (`guid`)
) ENGINE=MyISAM DEFAULT CHARSET=gbk;



CREATE
    VIEW `samweb`.`hotel_sum` 
    AS
(SELECT 
  COUNT(1) AS cont
  ,SUM(rzts) AS ts
  ,f.`qtmc` AS hotelName
  ,SUM(b.xpze) AS xpze
   ,SUM( a.ffyj*b.jdyh) AS yhje
  
    ,SUM( (Ffyj-Kqzyh+Xpze-( a.ffyj*b.jdyh))*b.Xszf) AS xfje
    ,SUM( (Ffyj-Kqzyh+Xpze-( a.ffyj*b.jdyh))*b.fyjl) AS ffyj
    ,SUM( xpze*b.pyjl) AS xpyj
    ,SUM( (Ffyj-Kqzyh+Xpze-( a.ffyj*b.jdyh))*(1-b.Xszf)) AS jdfx
FROM

  fwddzb a 
  LEFT JOIN fwddzb_ex b 
    ON a.guid = b.guid 
  LEFT JOIN house c 
    ON a.houseGuid = c.guid 
  LEFT JOIN fwqtxx f 
    ON (a.guid = f.houseGuid)
   
   GROUP BY f.`qtmc`);
   
   
   
 --房间清洁信息 
   CREATE TABLE `house_clean` (
  `Id` VARCHAR(38) NOT NULL COMMENT '主键',
  `grpId` VARCHAR(38) NOT NULL COMMENT '组主键',
 `hcy` DOUBLE(4,3) DEFAULT NULL COMMENT '核查员',
   `qjzt` varchar(38) DEFAULT NULL COMMENT '清洁状态',
`jcsj` date DEFAULT NULL COMMENT '检查时间',
`wtyy` varchar(50) DEFAULT NULL COMMENT '问题原因',
`yyly` varchar(50) DEFAULT NULL COMMENT '语音留言',
`tp` varchar(50) DEFAULT NULL COMMENT '图片',
  PRIMARY KEY (`Id`)
) DEFAULT CHARSET=gbk;

CREATE TABLE `house_clean_grp` (
`id` VARCHAR(38) NOT NULL COMMENT '主键',
  `grp` VARCHAR(32) DEFAULT NULL COMMENT '小组',
  `bc` VARCHAR(32) DEFAULT NULL COMMENT '班次',
  `sbsj` date DEFAULT NULL COMMENT '上班时间',
  `xbsj` date DEFAULT NULL COMMENT '下班时间',
  `pb` VARCHAR(32) DEFAULT NULL COMMENT '排班',
  `fjzs` DOUBLE(4,3) DEFAULT NULL COMMENT '房间指数',

  `zrs`  integer DEFAULT NULL COMMENT '组人数',
`zxs` date DEFAULT NULL COMMENT '组系数',


  `qjsj` date DEFAULT NULL COMMENT '清洁时间',
 
 PRIMARY KEY (`id`)
) DEFAULT CHARSET=gbk;

CREATE TABLE `house_clean_grp_menber` (
  `id` VARCHAR(38) NOT NULL COMMENT '主键',
    `grpId` VARCHAR(38) NOT NULL COMMENT '组主键',
  `name` VARCHAR(32) DEFAULT NULL COMMENT '姓名',
  `grxs` DOUBLE(4,3) DEFAULT NULL COMMENT '个人系数',
 
 PRIMARY KEY (`id`)
)DEFAULT CHARSET=gbk;
