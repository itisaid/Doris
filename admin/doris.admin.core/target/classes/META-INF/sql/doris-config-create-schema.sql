-- 配置实例 
CREATE TABLE  `CONFIG_INSTANCES` (
  `ID` int(11) NOT NULL auto_increment,
  `GMT_CREATE` datetime NOT NULL,
  `CONFIG_CONTENT` text NOT NULL,
  PRIMARY KEY  (`ID`)
);

  
-- 配置节点表
CREATE TABLE  `PHYSICAL_NODES` (
  `ID` int(11) NOT NULL auto_increment,
  `LOGICAL_ID` int(11) default NULL,
  `PHYSICAL_ID` varchar(128) NOT NULL,
  `SERIAL_ID` int(11) default NULL,
  `MACHINE_ID` varchar(100) NOT NULL,
  `IP` varchar(32) NOT NULL,
  `STATUS` int(11) default NULL,
  `GMT_CREATE` datetime NOT NULL,
  `GMT_MODIFIED` datetime NOT NULL,
  `PORT` int(11) NOT NULL,
  PRIMARY KEY  (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;
 
 
-- namespace
CREATE TABLE  `NAMESPACE` (
  `ID` int(11) NOT NULL auto_increment,
  `NAME` varchar(100) NOT NULL,
  `COPY_COUNT` int(1) NOT NULL,
  `COMPRESS_MODE` varchar(128) NOT NULL,
  `COMPRESS_THRESHOLD` int(11) NOT NULL,
  `SERIALIZE_MODE` varchar(128) NOT NULL,
  `FIRST_OWNER` varchar(128) NOT NULL,
  `SECOND_OWNER` varchar(128) NOT NULL,
  `STATUS` varchar(32) NOT NULL,
  `REMARK` varchar(4000) default NULL,
  `GMT_CREATE` datetime NOT NULL,
  `GMT_MODIFIED` datetime NOT NULL,
  `CLASS_NAME` varchar(100) DEFAULT NULL,
  `MULTI_READ` varchar(1) DEFAULT NULL,
  PRIMARY KEY  (`ID`)
)  ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;



-- 性能日志
CREATE TABLE `PREF_LOG` (
  `ID` int(11) NOT NULL auto_increment,
  `GMT_CREATE` datetime NOT NULL,
  `GMT_MODIFIED` datetime NOT NULL,
  `PHYSICAL_ID` varchar(128) NOT NULL,
  `ACTION_NAME` varchar(45) NOT NULL,
  `NAME_SPACE` varchar(45) default NULL,
  `OP_PER_SECOND` float default NULL,
  `BYTES_PER_SECOND` float default NULL,
  `AVG_LATENCY` float default NULL,
  `MIN_LATENCY` int(11) default NULL,
  `MAX_LATENCY` int(11) default NULL,
  `THE_80TH_LATENCY` int(11) default NULL,
  `THE_95TH_LATENCY` int(11) default NULL,
  `THE_99TH_LATENCY` int(11) default NULL,
  `TOTAL_OPERATIONS` bigint(20) default NULL,
  `TOTAL_LATENCY` bigint(20) default NULL,
  `TOTAL_BYTES` bigint(20) default NULL,
  `MAX_CONCURRENCY_LEVEL` int(11) default NULL,
  `TIME_START` datetime default NULL,
  `TIME_USED` int(11) default NULL,
  PRIMARY KEY  (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=10111 DEFAULT CHARSET=utf8;

 

CREATE TABLE  `VIRTUAL_NODE` (
  `ID` int(11) NOT NULL,
  `GMT_CREATE` datetime NOT NULL,
  `VIRTUAL_NODE_NUMBER` int(11) NOT NULL,
  PRIMARY KEY  (`ID`)
);

INSERT INTO VIRTUAL_NODE(ID, GMT_CREATE, VIRTUAL_NODE_NUMBER) VALUES(1,now(), 10000);



/**user**/

CREATE TABLE `DORIS_USER` (
  `ID` int(11) NOT NULL auto_increment,
  `USER_NAME` varchar(32) NOT NULL,
  `PASSWORD` varchar(32) NOT NULL,
  `PRIVILEGE_LEVEL` tinyint(4) default NULL,
  `GMT_CREATE` datetime NOT NULL,
  `GMT_MODIFIED` datetime NOT NULL,
  PRIMARY KEY  (`ID`),
  UNIQUE KEY `USER_NAME_UNIQUE` (`USER_NAME`),
  KEY `USER_NAME` (`USER_NAME`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

CREATE TABLE  `SYSTEM_LOG` (
  `ID` int(11) NOT NULL auto_increment,
  `ACTION_NAME` varchar(20) NOT NULL,
  `ACTION_TIME` datetime NOT NULL,
  `LOG_INFO` varchar(2000) NOT NULL,
  `GMT_CREATE` datetime NOT NULL,
  `GMT_MODIFIED` datetime NOT NULL,
  PRIMARY KEY  (`ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

INSERT INTO `DORIS_USER`
(
`USER_NAME`,
`PASSWORD`,
`PRIVILEGE_LEVEL`,
`GMT_CREATE`,
`GMT_MODIFIED`)
VALUES
('admin', 'admin', 0, now(), now());

INSERT INTO `DORIS_USER`
(
`USER_NAME`,
`PASSWORD`,
`PRIVILEGE_LEVEL`,
`GMT_CREATE`,
`GMT_MODIFIED`)
VALUES
('doris', 'doris', 0, now(), now());


CREATE TABLE `PREF_LOG_ARCHIVED` (
  `ID` int(11) NOT NULL auto_increment,
  `GMT_CREATE` datetime NOT NULL,
  `GMT_MODIFIED` datetime NOT NULL,
  `PHYSICAL_ID` varchar(128) NOT NULL,
  `ACTION_NAME` varchar(45) NOT NULL,
  `NAME_SPACE` varchar(45) default NULL,
  `MIN_LATENCY` int(11) default NULL,
  `MAX_LATENCY` int(11) default NULL,
  `THE_80TH_LATENCY` int(11) default NULL,
  `THE_95TH_LATENCY` int(11) default NULL,
  `THE_99TH_LATENCY` int(11) default NULL,
  `TOTAL_OPERATIONS` bigint(20) default NULL,
  `TOTAL_LATENCY` bigint(20) default NULL,
  `TOTAL_BYTES` bigint(20) default NULL,
  `MAX_CONCURRENCY_LEVEL` int(11) default NULL,
  `TIME_START` datetime default NULL,
  `TIME_USED` int(11) default NULL,
  PRIMARY KEY  (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=10029 DEFAULT CHARSET=utf8;

-- CONSISTENT_REPORT
CREATE TABLE  `CONSISTENT_REPORT` (
  `ID` int(11) NOT NULL auto_increment,
  `GMT_CREATE` datetime NOT NULL,
  `GMT_MODIFIED` datetime NOT NULL,
  `KEY_STR` varchar(512) NOT NULL,
  `NAMESPACE_ID` int(11) NOT NULL,
  `PHISICAL_NODE_IPS` varchar(1024) NOT NULL,
  `CLIENT_IP` varchar(128) default NULL,
  `EXCEPTION_MSG` varchar(1024) default NULL,
  `TIMESTAMP` varchar(64) default NULL,
  `ERROR_TYPE` varchar(32) default NULL,
  PRIMARY KEY  (`ID`)
)  ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

