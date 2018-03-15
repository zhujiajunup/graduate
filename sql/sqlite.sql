CREATE TABLE province
(
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  name VARCHAR NOT NULL,
  postcode VARCHAR,
  capital VARCHAR DEFAULT "unknown" NOT NULL
);
CREATE TABLE sqlite_master
(
  type text,
  name text,
  tbl_name text,
  rootpage integer,
  sql text
);
CREATE TABLE sqlite_sequence
(
  name ,
  seq
);
CREATE TABLE weibo_comment
(
  id varchar(128) PRIMARY KEY NOT NULL,
  user_id varchar(128) DEFAULT NULL,
  content text DEFAULT NULL,
  pub_time varchar(32) DEFAULT NULL,
  source varchar(128) DEFAULT NULL,
  tweet_id varchar(32) DEFAULT NULL
);
CREATE TABLE weibo_task
(
  status VARCHAR,
  create_time LONG,
  weibo_id VARCHAR PRIMARY KEY,
  weibo_user VARCHAR,
  weibo_content VARCHAR
);
CREATE TABLE weibo_tweet
(
  id varchar(32) PRIMARY KEY NOT NULL,
  time varchar(32) DEFAULT NULL,
  source varchar(64) DEFAULT NULL,
  like varchar(16) DEFAULT NULL,
  comment varchar(16) DEFAULT NULL,
  transfer varchar(16) DEFAULT NULL,
  content text,
  source_tid varchar(32) DEFAULT NULL,
  flag varchar(16) DEFAULT NULL,
  uid varchar(32) DEFAULT NULL
);
CREATE TABLE weibo_user
(
  id varchar(32) PRIMARY KEY NOT NULL,
  nickname varchar(128) DEFAULT NULL,
  tags varchar(512) DEFAULT NULL,
  gender char(8) DEFAULT NULL,
  place varchar(256) DEFAULT NULL,
  signature varchar(1024) DEFAULT NULL,
  birthday varchar(128) DEFAULT NULL,
  sex_orientation varchar(16) DEFAULT NULL,
  edu_info varchar(512) DEFAULT NULL,
  marriage varchar(32) DEFAULT NULL,
  work_info varchar(512) DEFAULT NULL,
  follow_num INT DEFAULT -1,
  fans_num INT DEFAULT -1,
  tweet_num INT DEFAULT -1,
  head VARCHAR
)