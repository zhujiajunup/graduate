CREATE TABLE `weibo_user` (
`id`  varchar(32) COLLATE NOCASE NOT NULL ,
`nickname`  varchar(128) COLLATE NOCASE DEFAULT NULL ,
`tags`  varchar(512) COLLATE NOCASE DEFAULT NULL ,
`gender`  char(8)  COLLATE NOCASE DEFAULT NULL ,
`place`  varchar(256) COLLATE NOCASE DEFAULT NULL ,
`signature`  varchar(1024) COLLATE NOCASE DEFAULT NULL ,
`birthday`  varchar(128) COLLATE NOCASE DEFAULT NULL ,
`sex_orientation`  varchar(16) COLLATE NOCASE DEFAULT NULL ,
`edu_info`  varchar(512) COLLATE NOCASE DEFAULT NULL ,
`marriage`  varchar(32) COLLATE NOCASE DEFAULT NULL ,
`work_info`  varchar(512) COLLATE NOCASE DEFAULT NULL ,
PRIMARY KEY (`id`)
)
;