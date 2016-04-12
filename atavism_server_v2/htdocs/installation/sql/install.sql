drop database if exists atavism;

-- create a database called atavism
create database atavism;

-- switch to the atavism database
use atavism;

-- create table for persistent game objects - metadata is used for
-- the deserializer to look for objects it would be interested in
-- and not have to scan every single stored object
-- TODO: add world_id INT NOT NULL,
create table objstore (
    obj_id BIGINT NOT NULL,
	namespace_int TINYINT NOT NULL,
    world_name VARCHAR(64),
    locX INT,
    locY INT,
    locZ INT,
    instance BIGINT,
    metadata VARCHAR(255),
    type VARCHAR(255),
    name VARCHAR(255),
    persistence_key VARCHAR(255),
    data LONGBLOB,
    PRIMARY KEY (obj_id, namespace_int),
    INDEX (metadata),
    INDEX (type),
    INDEX (name),
    INDEX (persistence_key));

-- mapping from multiverse UID to objstore ID for player characters
create table player_character (
    account_id BIGINT,
    world_name VARCHAR(64) NOT NULL,
    obj_id BIGINT NOT NULL,
    namespace_int TINYINT NOT NULL,
	FOREIGN KEY (obj_id, namespace_int) REFERENCES objstore (obj_id, namespace_int));

-- free oids - token should always be 1, this just makes sure there is 
-- only one value, ever
create table oid_manager (
    token INT NOT NULL,
    free_oid BIGINT,
    PRIMARY KEY (token));

-- create table to hold the mapping from namespace string to int, read
-- by the server at startup
create table namespaces (
    namespace_string VARCHAR(64) NOT NULL,
    namespace_int TINYINT NOT NULL,
    PRIMARY KEY (namespace_string),
    UNIQUE(namespace_int));

-- create a "MEMORY" table into which plugins deposit their status, 
-- so we can manage them.
create table plugin_status (
    world_name VARCHAR(64) NOT NULL,
    agent_name VARCHAR(64) NOT NULL,
	plugin_name VARCHAR(64) NOT NULL,
    plugin_type VARCHAR(16) NOT NULL,
    host_name VARCHAR(64) NOT NULL,
	pid INT,
	run_id BIGINT,
	percent_cpu_load INT,
	last_update_time BIGINT,
    next_update_time BIGINT,
    status VARCHAR(255),
    info VARCHAR(255),
	INDEX USING HASH(plugin_name),
	INDEX USING BTREE(world_name),
	INDEX USING BTREE(agent_name))
    ENGINE = MEMORY;

insert into oid_manager values (1, 1);

-- create the built-in namespaces
insert into namespaces values ('NS.transient', 1);
insert into namespaces values ('NS.master', 2);
insert into namespaces values ('NS.wmgr', 3);
insert into namespaces values ('NS.combat', 4);
insert into namespaces values ('NS.mob', 5);
insert into namespaces values ('NS.inv', 6);
insert into namespaces values ('NS.item', 7);
insert into namespaces values ('NS.quest', 8);
insert into namespaces values ('NS.playerqueststates', 9);
insert into namespaces values ('NS.voice', 10);

-- create a profanity words table for name checks
create table custom_profanity (
    name VARCHAR(20) NOT NULL,
    reason VARCHAR(255),
    bannedby VARCHAR(20), 
    PRIMARY KEY (name));

create table custom_banned (
    name VARCHAR(20) NOT NULL,
    reason VARCHAR(255),
    bannedby VARCHAR(20),
    expire DATETIME default NULL,
    PRIMARY KEY (name));
    
DROP TABLE IF EXISTS `backdating_tables`;
CREATE TABLE `backdating_tables` (
  `DBName` varchar(255) DEFAULT NULL,
  `TableName` varchar(255) DEFAULT NULL,
  `oid_manager_Type` varchar(255) DEFAULT NULL,
  `DisplayName` varchar(255) DEFAULT NULL,
  `LastUpdate` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

INSERT INTO `backdating_tables` VALUES ('world_content', 'item_templates', 'ITEM', 'Item Template DB', '2015-08-15 00:15:33.000000');    
