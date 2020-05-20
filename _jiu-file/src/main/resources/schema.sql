/*
Navicat MySQL Data Transfer

Source Server         : docker
Source Server Version : 50729
Source Host           : 127.0.0.1:3306
Source Database       : 222

Target Server Type    : MYSQL
Target Server Version : 50729
File Encoding         : 65001

Date: 2020-05-20 23:00:23
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for f_file
-- ----------------------------
DROP TABLE IF EXISTS `f_file`;
CREATE TABLE `f_file` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `data_type` varchar(255) DEFAULT 'IMAGE' COMMENT '数据类型\n#DataType{DIR:目录;IMAGE:图片;VIDEO:视频;AUDIO:音频;DOC:文档;OTHER:其他}',
  `submitted_file_name` varchar(255) DEFAULT '' COMMENT '原始文件名',
  `tree_path` varchar(255) DEFAULT ',' COMMENT '父目录层级关系',
  `grade` int(11) DEFAULT '1' COMMENT '层级等级\n从1开始计算',
  `is_delete` bit(1) DEFAULT b'0' COMMENT '是否删除\n#BooleanStatus{TRUE:1,已删除;FALSE:0,未删除}',
  `folder_id` bigint(20) DEFAULT '0' COMMENT '父文件夹ID',
  `url` varchar(1000) DEFAULT '' COMMENT '文件访问链接\n需要通过nginx配置路由，才能访问',
  `size` bigint(20) DEFAULT '0' COMMENT '文件大小\n单位字节',
  `folder_name` varchar(255) DEFAULT '' COMMENT '父文件夹名称',
  `group_` varchar(255) DEFAULT '' COMMENT 'FastDFS组\n用于FastDFS',
  `path` varchar(255) DEFAULT '' COMMENT 'FastDFS远程文件名\n用于FastDFS',
  `relative_path` varchar(255) DEFAULT '' COMMENT '文件的相对路径 ',
  `file_md5` varchar(255) DEFAULT '' COMMENT 'md5值',
  `context_type` varchar(255) DEFAULT '' COMMENT '文件类型\n取上传文件的值',
  `filename` varchar(255) DEFAULT '' COMMENT '唯一文件名',
  `ext` varchar(64) DEFAULT '' COMMENT '文件名后缀 \n(没有.)',
  `icon` varchar(64) DEFAULT '' COMMENT '文件图标\n用于云盘显示',
  `create_month` varchar(10) DEFAULT NULL COMMENT '创建时年月\n格式：yyyy-MM 用于统计',
  `create_week` varchar(10) DEFAULT NULL COMMENT '创建时年周\nyyyy-ww 用于统计',
  `create_day` varchar(12) DEFAULT NULL COMMENT '创建时年月日\n格式： yyyy-MM-dd 用于统计',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `create_user` bigint(20) DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT NULL COMMENT '最后修改时间',
  `update_user` bigint(20) DEFAULT NULL COMMENT '最后修改人',
  PRIMARY KEY (`id`) USING BTREE,
  FULLTEXT KEY `FU_TREE_PATH` (`tree_path`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COMMENT='文件表';

-- ----------------------------
-- Records of f_file
-- ----------------------------
INSERT INTO `f_file` VALUES ('4', 'IMAGE', '874963-20190320172835025-125393288.png', ',', '1', '\0', '2', 'http://127.0.0.1:80/null/2020/05/395718f3-1170-49b2-8207-9fcdcf4584d1.png', '71911', '', '', '', 'null\\2020\\05', '', 'image/png', '395718f3-1170-49b2-8207-9fcdcf4584d1.png', 'png', 'el-icon-picture', '2020年05月', '2020年21周', '2020年05月17日', null, null, null, null);
INSERT INTO `f_file` VALUES ('5', 'IMAGE', '874963-20190320172835025-125393288.png', ',', '1', '\0', '2', 'http://127.0.0.1:80/null/2020/05/a2506b5f-1cbc-4bce-b492-e606006afb4e.png', '71911', '', '', '', 'null\\2020\\05', '', 'image/png', 'a2506b5f-1cbc-4bce-b492-e606006afb4e.png', 'png', 'el-icon-picture', '2020年05月', '2020年21周', '2020年05月17日', null, null, null, null);
INSERT INTO `f_file` VALUES ('6', 'IMAGE', '874963-20190320172835025-125393288.png', ',', '1', '\0', '2', 'http://127.0.0.1:80/null/2020/05/ff27c8df-7ad4-4e70-bfae-cae13bb9b17b.png', '71911', '', '', '', 'null\\2020\\05', '', 'image/png', 'ff27c8df-7ad4-4e70-bfae-cae13bb9b17b.png', 'png', 'el-icon-picture', '2020年05月', '2020年21周', '2020年05月17日', null, null, null, null);
INSERT INTO `f_file` VALUES ('7', 'IMAGE', '874963-20190320172835025-125393288.png', ',', '1', '\0', '2', 'http://127.0.0.1:80/null/2020/05/a251af4b-bb70-4417-a247-33f21327e0a2.png', '71911', '', '', '', 'null\\2020\\05', '', 'image/png', 'a251af4b-bb70-4417-a247-33f21327e0a2.png', 'png', 'el-icon-picture', '2020年05月', '2020年21周', '2020年05月17日', null, null, null, null);
INSERT INTO `f_file` VALUES ('8', 'IMAGE', '874963-20190320172835025-125393288.png', ',', '1', '\0', '2', 'http://127.0.0.1:80/jiu/2020/05/90ef5561-75a4-4902-a42c-d8d6b09275bc.png', '71911', '', '', '', 'jiu\\2020\\05', '', 'image/png', '90ef5561-75a4-4902-a42c-d8d6b09275bc.png', 'png', 'el-icon-picture', '2020年05月', '2020年21周', '2020年05月17日', null, null, null, null);
INSERT INTO `f_file` VALUES ('9', 'IMAGE', '874963-20190320172835025-125393288.png', ',', '1', '\0', '3', 'http://127.0.0.1:80/image/jiu/2020/05/e67dc2ef-b889-4b4a-b940-37c0616fd8b2.png', '71911', '', '', '', 'jiu\\2020\\05', '', 'image/png', 'e67dc2ef-b889-4b4a-b940-37c0616fd8b2.png', 'png', 'el-icon-picture', '2020年05月', '2020年21周', '2020年05月20日', null, null, null, null);
INSERT INTO `f_file` VALUES ('10', 'IMAGE', 'QQ图片20200515211100.jpg', ',', '1', '\0', '3', 'http://127.0.0.1:80/image/jiu/2020/05/964a5d37-cba2-4091-aead-772a292b8000.jpg', '1112942', '', '', '', 'jiu\\2020\\05', '', 'image/jpeg', '964a5d37-cba2-4091-aead-772a292b8000.jpg', 'jpg', 'el-icon-picture', '2020年05月', '2020年21周', '2020年05月20日', null, null, null, null);
