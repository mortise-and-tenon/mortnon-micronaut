BEGIN;
-- 用户表
CREATE TABLE IF NOT EXISTS `sys_user` (
	`id` BIGINT NOT NULL AUTO_INCREMENT UNIQUE PRIMARY KEY,
	`user_name` VARCHAR(255) NOT NULL UNIQUE,
	`nick_name` VARCHAR(255) NOT NULL,
	`password` VARCHAR(255) NOT NULL,
	`salt` VARCHAR(255) NOT NULL,
	`email` VARCHAR(128) DEFAULT NULL,
	`phone` VARCHAR(32) DEFAULT NULL,
	`avatar` VARCHAR(1024) DEFAULT NULL,
	`sex` TINYINT NOT NULL DEFAULT 1,
	`gmt_create` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
	`gmt_modify` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

INSERT INTO `sys_user`(user_name,nick_name,password,salt,email,phone)
SELECT 'admin','系统管理员','4cc49025256bc1fa355dff5103b5a477d6a9dca293517a5596c8fd5ed95d7e01','q1RL80',
    'admin@mortnon.fun','13012345678'
FROM DUAL WHERE NOT EXISTS(SELECT id FROM `sys_user`);

-- 角色表
CREATE TABLE IF NOT EXISTS `sys_role`(
	`id` BIGINT NOT NULL AUTO_INCREMENT UNIQUE PRIMARY KEY,
	`name` VARCHAR(1024) NOT NULL,
	`identifier` VARCHAR(64) NOT NULL,
	`description` VARCHAR(1024) NULL,
	`gmt_create` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
	`gmt_modify` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

INSERT INTO `sys_role`(name,identifier,description)
SELECT '系统管理员','ROLE_SYS','系统管理员角色可操作整个系统'
FROM DUAL WHERE NOT EXISTS(SELECT id FROM `sys_role`);

-- 组织表
CREATE TABLE IF NOT EXISTS `sys_project`(
	`id` BIGINT NOT NULL AUTO_INCREMENT UNIQUE PRIMARY KEY,
	`name` VARCHAR(1024) NOT NULL,
	`description` VARCHAR(1024) NULL,
	`parent_id` BIGINT NULL,
	`gmt_create` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
	`gmt_modify` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

INSERT INTO `sys_project`(name,description,parent_id)
VALUES ('Mornton总公司','',0),
    ('子公司一','',1);


-- 用户、角色、组织关联表
CREATE TABLE IF NOT EXISTS `sys_assignment`(
	`id` BIGINT NOT NULL AUTO_INCREMENT UNIQUE PRIMARY KEY,
	`user_id` BIGINT NOT NULL,
	`project_id` BIGINT NOT NULL,
	`role_id` BIGINT NOT NULL,
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
	`id` BIGINT NOT NULL AUTO_INCREMENT UNIQUE PRIMARY KEY,
	`name` VARCHAR(64) NOT NULL,
	`identifier` VARCHAR(64) NOT NULL,
	`description` VARCHAR(1024) NULL,
	`api` VARCHAR(1024) NOT NULL,
	`method` ENUM('GET','PUT','POST','DELETE','PATCH') NOT NULL DEFAULT 'GET',
	`gmt_create` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
	`gmt_modify` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

INSERT INTO `sys_permission`(name,identifier,description,api,method)
VALUES ('查询用户','USER_QUERY','查看用户数据','/users/**','GET'),
    ('创建用户','USER_CREATE','创建用户数据','/users','POST'),
    ('修改用户','USER_MODIFY','修改用户数据','/users','PUT'),
    ('删除用户','USER_DELETE','删除用户数据','/users/**','DELETE'),
    ("用户分派","USER_ASSIGNMENT",'分派用户组织角色','/assignment/user/**/project/**/role/**','POST'),
    ("撤销用户分派","USER_ASSIGNMENT",'分派用户组织角色','/assignment/user/**/project/**/role/**','DELETE'),
    ('修改密码','PASSWORD_MODIFY','修改用户密码','/users/password/**','PUT'),
    ('查询角色','ROLE_QUERY','查询角色数据','/roles/**','GET'),
    ('创建角色','ROLE_CREATE','创建角色数据','/roles','POST'),
    ('修改角色','ROLE_MODIFY','修改角色数据','/roles','PUT'),
    ('删除角色','ROLE_DELETE','删除角色数据','/roles/**','DELETE'),
    ('分派用户','USER_ASSIGNMENT','分派用户','/user/assignment','PUT'),
    ('查询权限','PERMISSION_QUERY','查询权限数据','/permissions','GET'),
    ('创建权限','PERMISSION_CREATE','创建权限数据','/permissions','POST'),
    ('删除权限','PERMISSION_DELETE','删除权限数据','/permissions/**','DELETE'),
    ('查询组织','PROJECT_QUERY','查询组织数据','/projects/**','GET'),
    ('创建组织','PROJECT_CREATE','创建组织数据','/projects','POST'),
    ('修改组织','PROJECT_MODIFY','修改组织数据','/projects','PUT'),
    ('删除组织','PROJECT_DELETE','删除组织数据','/projects/**','DELETE'),
    ('查看日志','LOG_QUERY','查看日志数据','/logs','GET');

-- 角色权限关联表
CREATE TABLE IF NOT EXISTS `sys_role_permission`(
	`id` BIGINT NOT NULL AUTO_INCREMENT UNIQUE PRIMARY KEY,
	`role_id` BIGINT NOT NULL,
	`permission_id` BIGINT NOT NULL,
	FOREIGN KEY (role_id) REFERENCES sys_role(id),
	FOREIGN KEY (permission_id) REFERENCES sys_permission(id)
);

INSERT INTO `sys_role_permission`(role_id,permission_id)
SELECT 1,id
FROM sys_permission;

-- 操作日志表
CREATE TABLE IF NOT EXISTS `sys_log` (
	`id` BIGINT NOT NULL AUTO_INCREMENT UNIQUE PRIMARY KEY,
	`action` VARCHAR(255) NOT NULL,
	`user_name` VARCHAR(255) NOT NULL,
	`project_name` VARCHAR(255) NULL,
	`project_id` BIGINT NULL,
	`ip` VARCHAR(255) NOT NULL,
	`result` ENUM('SUCCESS',"FAILURE") DEFAULT 'SUCCESS',
	`level` ENUM('INFO','WARN','DANGER') DEFAULT 'INFO',
	`time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
	`gmt_create` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
	`gmt_modify` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
COMMIT;