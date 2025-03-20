BEGIN;
-- 用户表
CREATE TABLE IF NOT EXISTS `sys_user` (
	`id` BIGINT NOT NULL AUTO_INCREMENT UNIQUE PRIMARY KEY  COMMENT '用户 id',
	`user_name` VARCHAR(255) NOT NULL UNIQUE                COMMENT '用户名/登录名',
	`nick_name` VARCHAR(255) NOT NULL                       COMMENT '用户昵称/姓名',
	`password` VARCHAR(255) NOT NULL                        COMMENT '用户密码',
	`salt` VARCHAR(255) NOT NULL                            COMMENT '密码加盐值',
	`email` VARCHAR(128) DEFAULT NULL                       COMMENT '用户邮箱',
	`phone` VARCHAR(32) DEFAULT NULL                        COMMENT '用户手机号',
	`avatar` VARCHAR(1024) DEFAULT NULL                     COMMENT '用户头像',
	`sex` TINYINT(1) NOT NULL DEFAULT 1                     COMMENT '用户性别，0女，1男',
	`status` TINYINT(1) NOT NULL DEFAULT 1                  COMMENT '用户状态',
	`gmt_create` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP                                COMMENT '创建时间',
	`gmt_modify` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP    COMMENT '修改时间'
);

-- 预置 admin 密码：password
INSERT INTO `sys_user`(user_name,nick_name,password,salt,email,phone)
SELECT 'admin','系统管理员','463e71f1d033870e4ea468eb78cda7f0224446bf8ab20cf102d80be559bdfc2a','q1RL80',
    'admin@mortnon.fun','13012345678'
FROM DUAL WHERE NOT EXISTS(SELECT id FROM `sys_user`);

-- 角色表
CREATE TABLE IF NOT EXISTS `sys_role`(
	`id` BIGINT NOT NULL AUTO_INCREMENT UNIQUE PRIMARY KEY  COMMENT '角色 id',
	`name` VARCHAR(1024) NOT NULL                           COMMENT '角色名字',
	`identifier` VARCHAR(64) NOT NULL                       COMMENT '角色标识符',
	`description` VARCHAR(1024) NULL                        COMMENT '角色描述',
	`status` TINYINT(1) NOT NULL DEFAULT 1                  COMMENT '角色状态',
	`gmt_create` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP                                COMMENT '创建时间',
	`gmt_modify` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP    COMMENT '修改时间'
);

INSERT INTO `sys_role`(name,identifier,description)
SELECT '系统管理员','ROLE_SYS','系统管理员角色可操作整个系统'
FROM DUAL WHERE NOT EXISTS(SELECT id FROM `sys_role`);

-- 组织表
CREATE TABLE IF NOT EXISTS `sys_project`(
	`id` BIGINT NOT NULL AUTO_INCREMENT UNIQUE PRIMARY KEY  COMMENT '组织 id',
	`name` VARCHAR(1024) NOT NULL                           COMMENT '组织名字',
	`identifier` VARCHAR(64) NOT NULL                         COMMENT '部门标识值',
	`description` VARCHAR(1024) NULL                        COMMENT '组织描述',
	`parent_id` BIGINT NULL                                 COMMENT '父组织 id',
	`ancestors` VARCHAR(1024) NOT NULL                      COMMENT '先辈组织所有id',
	`order` INT NOT NULL DEFAULT 1                          COMMENT '排序',
	`status` TINYINT(1) NOT NULL DEFAULT 1                     COMMENT '状态',
	`gmt_create` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP                                COMMENT '创建时间',
	`gmt_modify` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP    COMMENT '修改时间'
);

INSERT INTO `sys_project`(name,identifier,description,parent_id,ancestors)
VALUES ('Mortnon科技有限公司','Root','',0,""),
    ('研究开发部','RD','',1,"1");


-- 用户、角色、组织关联表
CREATE TABLE IF NOT EXISTS `sys_assignment`(
	`id` BIGINT NOT NULL AUTO_INCREMENT UNIQUE PRIMARY KEY  COMMENT '关联 id',
	`user_id` BIGINT NOT NULL                               COMMENT '用户 id',
	`project_id` BIGINT NULL                                COMMENT '组织 id',
	`role_id` BIGINT NULL                                   COMMENT '角色 id',
	FOREIGN KEY (user_id) REFERENCES sys_user(id),
	FOREIGN KEY (role_id) REFERENCES sys_role(id),
	FOREIGN KEY (project_id) REFERENCES sys_project(id)
);

INSERT INTO `sys_assignment`(user_id,project_id,role_id)
SELECT 1,1,1
FROM DUAL WHERE NOT EXISTS(SELECT id FROM `sys_assignment`);

-- 权限表
DROP TABLE IF EXISTS `sys_permission`;
CREATE TABLE `sys_permission`(
	`id` BIGINT NOT NULL AUTO_INCREMENT UNIQUE PRIMARY KEY  COMMENT '权限 id',
	`name` VARCHAR(64) NOT NULL                             COMMENT '权限名字',
	`identifier` VARCHAR(64) NOT NULL                       COMMENT '权限标识符',
	`description` VARCHAR(1024) NULL                        COMMENT '权限描述',
	`dependency` VARCHAR(1024) DEFAULT ''                   COMMENT '关联的权限',
	`gmt_create` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP                                COMMENT '创建时间',
	`gmt_modify` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP    COMMENT '修改时间'
);

INSERT INTO `sys_permission`(name,identifier,description,dependency)
VALUES
    ('全局维护','GLOBAL_MAINTENANCE','维护系统全局配置',''),
    ('查询用户','USER_QUERY','查看用户数据',''),
    ('维护用户','USER_UPDATE','维护用户数据','USER_QUERY'),
    ("维护用户分派","USER_ASSIGNMENT",'维护用户分派',''),
    ('查询角色','ROLE_QUERY','查询角色数据',''),
    ('维护角色','ROLE_UPDATE','维护角色数据','ROLE_QUERY'),
    ('查询组织','PROJECT_QUERY','查询组织数据',''),
    ('维护组织','PROJECT_UPDATE','创建组织数据','PROJECT_QUERY'),
    ('查询菜单','MENU_QUERY','查询菜单数据',''),
    ('维护菜单','MENU_UPDATE','维护菜单数据','MENU_QUERY'),
    ('查看日志','LOG_QUERY','查看日志数据','');

-- 权限控制的API表
DROP TABLE IF EXISTS `sys_api`;
CREATE TABLE `sys_api`(
	`id` BIGINT NOT NULL AUTO_INCREMENT UNIQUE PRIMARY KEY  COMMENT 'API id',
	`identifier` VARCHAR(64) NOT NULL                       COMMENT '对应的权限标识符',
	`api` VARCHAR(1024) NOT NULL                            COMMENT '控制的 api',
	`method` ENUM('GET','PUT','POST','DELETE','PATCH') NOT NULL DEFAULT 'GET'   COMMENT '控制的 api 的方法',
	`gmt_create` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP                                COMMENT '创建时间',
	`gmt_modify` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP    COMMENT '修改时间'
);

INSERT INTO `sys_api`(identifier,api,method)
VALUES
    ('GLOBAL_MAINTENANCE','/system/**','GET'),
    ('GLOBAL_MAINTENANCE','/system/**','PUT'),
    ('USER_QUERY','/users','GET'),
    ('USER_QUERY','/users/**','GET'),
    ('USER_UPDATE','/users','POST'),
    ('USER_UPDATE','/users','PUT'),
    ('USER_UPDATE','/users/**','POST'),
    ('USER_UPDATE','/users/**','PUT'),
    ('USER_UPDATE','/users/**','DELETE'),
    ('USER_ASSIGNMENT','/assignment/**','GET'),
    ('USER_ASSIGNMENT','/assignment/**','POST'),
    ('USER_ASSIGNMENT','/assignment/**','PUT'),
    ('USER_ASSIGNMENT','/assignment/**','DELETE'),
    ('USER_UPDATE','/users/password/**','PUT'),
    ('ROLE_QUERY','/roles','GET'),
    ('ROLE_QUERY','/roles/**','GET'),
    ('ROLE_UPDATE','/roles','POST'),
    ('ROLE_UPDATE','/roles','PUT'),
    ('ROLE_UPDATE','/roles/**','DELETE'),
    ('PROJECT_QUERY','/projects','GET'),
    ('PROJECT_QUERY','/projects/**','GET'),
    ('PROJECT_UPDATE','/projects','POST'),
    ('PROJECT_UPDATE','/projects','PUT'),
    ('PROJECT_UPDATE','/projects/**','DELETE'),
    ('MENU_QUERY','/menus','GET'),
    ('MENU_QUERY','/menus/**','GET'),
    ('MENU_UPDATE','/menus','POST'),
    ('MENU_UPDATE','/menus','PUT'),
    ('MENU_UPDATE','/menus/**','DELETE'),
    ('LOG_QUERY','/logs','GET'),
    ('LOG_QUERY','/logs/**','GET');

-- 角色权限关联表
CREATE TABLE IF NOT EXISTS `sys_role_permission`(
	`id` BIGINT NOT NULL AUTO_INCREMENT UNIQUE PRIMARY KEY  COMMENT '关联 id',
	`role_id` BIGINT NOT NULL                               COMMENT '角色 id',
	`permission_id` BIGINT NOT NULL                         COMMENT '权限 id',
	FOREIGN KEY (role_id) REFERENCES sys_role(id),
	FOREIGN KEY (permission_id) REFERENCES sys_permission(id)
);

INSERT INTO `sys_role_permission`(role_id,permission_id)
SELECT 1,id
FROM sys_permission;

-- 操作日志表
CREATE TABLE IF NOT EXISTS `sys_log` (
	`id` BIGINT NOT NULL AUTO_INCREMENT UNIQUE PRIMARY KEY  COMMENT '操作日志 id',
	`action` VARCHAR(255) NOT NULL                          COMMENT '操作行为',
	`action_desc` VARCHAR(255) NOT NULL                     COMMENT '操作行为文字',
	`user_name` VARCHAR(255) NOT NULL                       COMMENT '用户名',
	`project_name` VARCHAR(255) NULL                        COMMENT '组织名',
	`project_id` BIGINT NULL                                COMMENT '组织 id',
	`ip` VARCHAR(255) NOT NULL                              COMMENT '操作 ip',
	`request` VARCHAR(500) DEFAULT ''                       COMMENT '操作对象',
	`message` VARCHAR(1024) DEFAULT ''                      COMMENT '操作响应',
	`result` ENUM('SUCCESS',"FAILURE") DEFAULT 'SUCCESS'    COMMENT '操作结果',
	`level` ENUM('INFO','WARN','DANGER') DEFAULT 'INFO'     COMMENT '日志级别',
	`time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP      COMMENT '操作时间',
	`gmt_create` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP                                COMMENT '创建时间',
	`gmt_modify` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP    COMMENT '修改时间'
);

-- 菜单表
CREATE TABLE IF NOT EXISTS `sys_menu`(
    `id` BIGINT NOT NULL AUTO_INCREMENT UNIQUE PRIMARY KEY  COMMENT '菜单 id',
    `name` VARCHAR(64) NOT NULL                             COMMENT '菜单名字',
    `parent_id` BIGINT DEFAULT 0 NOT NULL                   COMMENT '父菜单 id',
    `order` INT(4) DEFAULT 1                                COMMENT '菜单排序',
    `url` VARCHAR(200) NOT NULL                             COMMENT '菜单 url',
    `type` ENUM('GROUP','LINK') DEFAULT 'LINK'              COMMENT '菜单类型',
    `icon` VARCHAR(100) NOT NULL DEFAULT ''                 COMMENT '菜单 图标',
    `permission` VARCHAR(200) DEFAULT NULL                  COMMENT '权限组',
    `status` TINYINT(1) DEFAULT 1                           COMMENT '菜单状态',
    `gmt_create` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP                                COMMENT '创建时间',
    `gmt_modify` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP    COMMENT '修改时间'
);

INSERT INTO `sys_menu` (`id`, `name`, `parent_id`, `order`, `url`, `type`, `icon`, `permission`)
VALUES
	(1, '首页', 0, 1, '/home', 'LINK', 'home', ''),
	(2, '系统管理', 0, 1, '/system', 'GROUP', 'system', ''),
	(3, '用户管理', 2, 1, '/system/user', 'LINK', 'user', 'USER_QUERY'),
	(4, '角色管理', 2, 2, '/system/role', 'LINK', 'peoples', 'ROLE_QUERY'),
	(5, '部门管理', 2, 3, '/system/project', 'LINK', 'tree', 'PROJECT_QUERY'),
	(6, '菜单管理', 2, 4, '/system/menu', 'LINK', 'treetable', 'MENU_QUERY'),
	(7, '日志管理', 2, 5, '/system/log', 'LINK', 'log', 'LOG_QUERY'),
	(8, '系统配置', 2, 6, '/system/config', 'GROUP', 'setting', ''),
	(9, '安全设置', 8, 1, '/system/config/security', 'GROUP', 'security', ''),
	(10, '登录认证', 9, 1, '/system/config/security/login', 'LINK', 'lock', 'GLOBAL_MAINTENANCE'),
	(11, '信息设置', 8, 2, '/system/config/message', 'GROUP', 'message', ''),
	(12, '电子邮件', 11, 1, '/system/config/message/email', 'LINK', 'mail', 'GLOBAL_MAINTENANCE');

-- 系统配置表
CREATE TABLE IF NOT EXISTS `sys_config`(
    `id` BIGINT NOT NULL AUTO_INCREMENT UNIQUE PRIMARY KEY                                  COMMENT 'id',
    `captcha` ENUM('DISABLE','ARITHMETIC','OTHER') NOT NULL DEFAULT 'OTHER'                 COMMENT '验证码配置',
    `password_encrypt` TINYINT(1) NOT NULL DEFAULT 1                                        COMMENT '密码加密传输',
    `try_count` INT NOT NULL DEFAULT 5                                                      COMMENT '密码重试次数',
    `lock_time` INT NOT NULL DEFAULT 180                                                    COMMENT '锁定时间（秒）',
    `double_factor` ENUM('DISABLE','EMAIL','PHONE','OTHER') NOT NULL DEFAULT 'DISABLE'      COMMENT '双因子认证',
    `gmt_create` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP                                COMMENT '创建时间',
    `gmt_modify` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP    COMMENT '修改时间'
);

INSERT INTO `sys_config`(`password_encrypt`)
VALUES (true);

-- 邮箱服务器配置
CREATE TABLE IF NOT EXISTS `sys_email_config`(
    `id` BIGINT NOT NULL AUTO_INCREMENT UNIQUE PRIMARY KEY                                  COMMENT 'id',
    `enabled` TINYINT(1) NOT NULL DEFAULT 0                                                 COMMENT '启用配置',
    `debug` TINYINT(1) NOT NULL DEFAULT 0                                                   COMMENT '开启调试',
    `host` VARCHAR(128)                                                                     COMMENT '邮箱服务器',
    `port` INT                                                                              COMMENT '邮箱端口',
    `connection_timeout` BIGINT NOT NULL DEFAULT 10000                                      COMMENT '连接超时',
    `timeout` BIGINT NOT NULL DEFAULT 15000                                                 COMMENT '读取超时',
    `https` ENUM('NONE','SSL','TLS') NOT NULL DEFAULT 'NONE'                                COMMENT '安全协议',
    `auth` TINYINT(1) NOT NULL DEFAULT 1                                                    COMMENT '开启认证',
    `email` VARCHAR(128)                                                                    COMMENT '发件邮箱',
    `user_name` VARCHAR(128)                                                                COMMENT '用户名',
    `password` VARCHAR(512)                                                                 COMMENT '用户密码',
    `gmt_create` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP                                COMMENT '创建时间',
    `gmt_modify` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP    COMMENT '修改时间'
);

INSERT INTO `sys_email_config`(`enabled`)
VALUES (0);

-- 消息模板配置
CREATE TABLE IF NOT EXISTS `sys_template`(
    `id` BIGINT NOT NULL AUTO_INCREMENT UNIQUE PRIMARY KEY                                  COMMENT 'id',
    `name` VARCHAR(128) NOT NULL                                                            COMMENT '模板名称',
    `subject` VARCHAR(128) NOT NULL                                                         COMMENT '主题',
    `content` TEXT NOT NULL                                                                 COMMENT '内容',
    `enabled` TINYINT(1) NOT NULL DEFAULT 1                                                 COMMENT '启用',
    `system` TINYINT(1) NOT NULL DEFAULT 1                                                  COMMENT '系统预置',
    `gmt_create` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP                                COMMENT '创建时间',
    `gmt_modify` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP    COMMENT '修改时间'
);

INSERT INTO `sys_template`(`name`,`subject`,`content`)
VALUES ('VERIFY_CODE','验证码通知','<!DOCTYPE html><html><head><meta charset="UTF-8"></head><body><p>您好，您的当前验证码为<strong>#{code}</strong></p></body></html>');

COMMIT;