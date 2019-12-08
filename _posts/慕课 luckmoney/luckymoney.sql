# Host: localhost  (Version: 5.5.53)
# Date: 2019-07-14 12:28:34
# Generator: MySQL-Front 5.3  (Build 4.234)

/*!40101 SET NAMES utf8 */;

#
# Structure for table "hibernate_sequence"
#

CREATE TABLE `hibernate_sequence` (
  `next_val` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

#
# Data for table "hibernate_sequence"
#

INSERT INTO `hibernate_sequence` VALUES (12);

#
# Structure for table "luckymoney"
#

CREATE TABLE `luckymoney` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `consumer` varchar(255) DEFAULT NULL,
  `money` decimal(5,2) DEFAULT NULL,
  `producer` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;

#
# Data for table "luckymoney"
#

INSERT INTO `luckymoney` VALUES (10,NULL,123.00,'张三');
