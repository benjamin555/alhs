CREATE TABLE `fwddzb_ex` (
  `guid` varchar(38) NOT NULL COMMENT '����',
  `kqzyh` double(14,2) DEFAULT NULL COMMENT '��ȥס�Ż�',
  `xpze` double(14,2) DEFAULT NULL COMMENT 'ѡ���ܶ�',
  `jdyh` double(14,2) DEFAULT NULL COMMENT '�Ƶ��Ż�',
  `xszf` double(4,3) DEFAULT NULL COMMENT '����֧��',
  `fyjl` double(4,3) DEFAULT NULL COMMENT '��Ӷ����',
  `pyjl` double(4,3) DEFAULT NULL COMMENT '��Ӷ����',
  `hysx` varchar(50) DEFAULT NULL COMMENT '��Ա����',
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
   
   
   
 --���������Ϣ 
   CREATE TABLE `house_clean` (
  `Id` VARCHAR(38) NOT NULL COMMENT '����',
  `grpId` VARCHAR(38) NOT NULL COMMENT '������',
 `hcy` DOUBLE(4,3) DEFAULT NULL COMMENT '�˲�Ա',
   `qjzt` varchar(38) DEFAULT NULL COMMENT '���״̬',
`jcsj` date DEFAULT NULL COMMENT '���ʱ��',
`wtyy` varchar(50) DEFAULT NULL COMMENT '����ԭ��',
`yyly` varchar(50) DEFAULT NULL COMMENT '��������',
`tp` varchar(50) DEFAULT NULL COMMENT 'ͼƬ',
  PRIMARY KEY (`Id`)
) DEFAULT CHARSET=gbk;

CREATE TABLE `house_clean_grp` (
`id` VARCHAR(38) NOT NULL COMMENT '����',
  `grp` VARCHAR(32) DEFAULT NULL COMMENT 'С��',
  `bc` VARCHAR(32) DEFAULT NULL COMMENT '���',
  `sbsj` date DEFAULT NULL COMMENT '�ϰ�ʱ��',
  `xbsj` date DEFAULT NULL COMMENT '�°�ʱ��',
  `pb` VARCHAR(32) DEFAULT NULL COMMENT '�Ű�',
  `fjzs` DOUBLE(4,3) DEFAULT NULL COMMENT '����ָ��',

  `zrs`  integer DEFAULT NULL COMMENT '������',
`zxs` date DEFAULT NULL COMMENT '��ϵ��',


  `qjsj` date DEFAULT NULL COMMENT '���ʱ��',
 
 PRIMARY KEY (`id`)
) DEFAULT CHARSET=gbk;

CREATE TABLE `house_clean_grp_menber` (
  `id` VARCHAR(38) NOT NULL COMMENT '����',
    `grpId` VARCHAR(38) NOT NULL COMMENT '������',
  `name` VARCHAR(32) DEFAULT NULL COMMENT '����',
  `grxs` DOUBLE(4,3) DEFAULT NULL COMMENT '����ϵ��',
 
 PRIMARY KEY (`id`)
)DEFAULT CHARSET=gbk;
