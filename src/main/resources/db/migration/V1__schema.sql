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
	`description` VARCHAR(1024) NULL                        COMMENT '组织描述',
	`parent_id` BIGINT NULL                                 COMMENT '父组织 id',
	`ancestors` VARCHAR(1024) NOT NULL                      COMMENT '先辈组织所有id',
	`order` INT NOT NULL DEFAULT 1                          COMMENT '排序',
	`status` TINYINT(1) NOT NULL DEFAULT 1                     COMMENT '状态',
	`gmt_create` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP                                COMMENT '创建时间',
	`gmt_modify` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP    COMMENT '修改时间'
);

INSERT INTO `sys_project`(name,description,parent_id,ancestors)
VALUES ('Mortnon总公司','',0,""),
    ('子公司一','',1,"1");


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
	`api` VARCHAR(1024) NOT NULL                            COMMENT '权限控制的 api',
	`method` ENUM('GET','PUT','POST','DELETE','PATCH') NOT NULL DEFAULT 'GET'   COMMENT '权限控制的 api 的方法',
	`gmt_create` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP                                COMMENT '创建时间',
	`gmt_modify` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP    COMMENT '修改时间'
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
    ('分派用户','USER_ASSIGNMENT','分派用户','/assignment/**','PUT'),
    ('查询权限','PERMISSION_QUERY','查询权限数据','/permissions','GET'),
    ('创建权限','PERMISSION_CREATE','创建权限数据','/permissions','POST'),
    ('删除权限','PERMISSION_DELETE','删除权限数据','/permissions/**','DELETE'),
    ('查询组织','PROJECT_QUERY','查询组织数据','/projects/**','GET'),
    ('创建组织','PROJECT_CREATE','创建组织数据','/projects','POST'),
    ('修改组织','PROJECT_MODIFY','修改组织数据','/projects','PUT'),
    ('删除组织','PROJECT_DELETE','删除组织数据','/projects/**','DELETE'),
    ('查询菜单','MENU_QUERY','查询菜单数据','/menus/**','GET'),
    ('创建菜单','MENU_CREATE','创建菜单数据','/menus','POST'),
    ('修改菜单','MENU_DELETE','修改菜单数据','/menus/**','PUT'),
    ('删除菜单','MENU_DELETE','删除菜单数据','/menus/**','DELETE'),
    ('查看日志','LOG_QUERY','查看日志数据','/logs/**','GET');

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
    `icon` VARCHAR(100) NOT NULL DEFAULT ''                 COMMENT '菜单 图标',
    `permission` VARCHAR(20) DEFAULT NULL                   COMMENT '权限组',
    `status` TINYINT(1) DEFAULT 1                          COMMENT '菜单状态',
    `gmt_create` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP                                COMMENT '创建时间',
    `gmt_modify` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP    COMMENT '修改时间'
);

INSERT INTO `sys_menu` (`id`, `name`, `parent_id`, `order`, `url`, `icon`, `permission`)
VALUES
	(1, '首页', 0, 1, '/', 'home', ''),
	(2, '系统管理', 0, 1, '/system', 'system', ''),
	(3, '用户管理', 2, 1, '/system/user', 'user', 'USER_QUERY'),
	(4, '角色管理', 2, 2, '/system/role', 'peoples', 'ROLE_QUERY'),
	(5, '组织管理', 2, 3, '/system/project', 'tree', 'PROJECT_QUERY'),
	(6, '菜单管理', 2, 4, '/system/menu', 'treetable', 'MENU_QUERY'),
	(7, '日志管理', 2, 5, '/system/log', 'log', 'LOG_QUERY');

COMMIT;