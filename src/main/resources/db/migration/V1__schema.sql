-- 用户表
CREATE TABLE IF NOT EXISTS `sys_user` (
	`id` BIGINT NOT NULL AUTO_INCREMENT UNIQUE PRIMARY KEY,
	`user_name` VARCHAR(255) NOT NULL UNIQUE,
	`nick_name` VARCHAR(255) NOT NULL,
	`password` VARCHAR(255) NOT NULL,
	`salt` VARCHAR(255) NOT NULL,
	`email` VARCHAR(128) DEFAULT NULL,
	`phone` VARCHAR(32) DEFAULT NULL,
	`head` VARCHAR(1024) DEFAULT NULL,
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
	`role_id` BIGINT NOT NULL,
	`project_id` BIGINT NOT NULL,
	FOREIGN KEY (user_id) REFERENCES sys_user(id),
	FOREIGN KEY (role_id) REFERENCES sys_role(id),
	FOREIGN KEY (project_id) REFERENCES sys_project(id)
);

INSERT INTO `sys_assignment`(user_id,role_id,project_id)
SELECT 1,1,1
FROM DUAL WHERE NOT EXISTS(SELECT id FROM `sys_assignment`);

-- 权限表
DROP TABLE IF EXISTS `sys_permission`;
CREATE TABLE `sys_permission`(
	`id` BIGINT NOT NULL AUTO_INCREMENT UNIQUE PRIMARY KEY,
	`name` VARCHAR(64) NOT NULL,
	`identifier` VARCHAR(64) NOT NULL,
	`description` VARCHAR(1024) NULL,
	`gmt_create` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
	`gmt_modify` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

INSERT INTO `sys_permission`(name,identifier,description)
VALUES ('查询用户','USER_QUERY','查看所有用户数据'),
    ('创建用户','USER_CREATE','创建用户数据'),
    ('修改用户','USER_MODIFY','修改用户数据'),
    ('删除用户','USER_DELETE','删除用户数据');

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

-- API 权限表
DROP TABLE IF EXISTS `sys_api_permission`;

CREATE TABLE `sys_api_permission`(
	`id` BIGINT NOT NULL AUTO_INCREMENT UNIQUE PRIMARY KEY,
	`api` VARCHAR(1024) NOT NULL,
	`method` ENUM('GET','PUT','POST','DELETE','PATCH') NOT NULL DEFAULT 'GET',
	`permission` VARCHAR(1024) NOT NULL
);

INSERT INTO `sys_api_permission`(api,method,permission)
VALUES ('/users','GET','USER_QUERY'),
    ('/users','POST','USER_CREATE'),
    ('/users','PUT','USER_MODIFY'),
    ('/users','DELETE',"USER_DELETE");