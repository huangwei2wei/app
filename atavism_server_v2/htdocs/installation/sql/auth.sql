drop database if exists master;

-- create a database called multiverse
create database master;

-- switch to the multiverse database
use master;

-- create a table for the masterserver - multiverse users (main account)
create table account (
    id INT NOT NULL AUTO_INCREMENT, 
    username VARCHAR(64) UNIQUE, 
    password VARCHAR(128), 
    email VARCHAR(64),
    status INT(11) DEFAULT 1, 
    birthdate DATE,
    activated TINYINT(1),
    suspended TINYINT(1),
    activation_key VARCHAR(32),
    created_at DATETIME,
    last_modified_at DATETIME,
    INDEX (username), 
    PRIMARY KEY (id));

-- create a table of registered developers
create table developer (
    dev_id INT NOT NULL AUTO_INCREMENT, 
    email VARCHAR(64) NOT NULL UNIQUE,
    company VARCHAR(64), 
    password VARCHAR(64),
    size VARCHAR(64),
    skill VARCHAR(64),
    prior VARCHAR(64), 
    genre VARCHAR(64), 
    idea TEXT,
    INDEX (email), 
    PRIMARY KEY (dev_id));

-- to create a table of world servers
create table world (
    world_id INT NOT NULL AUTO_INCREMENT,
    dev_id INT NOT NULL,
    world_name VARCHAR(64) UNIQUE,
    pretty_name VARCHAR(64),
    description TEXT,
    server_name VARCHAR(64),
    server_port INT,
    public INT,
    approved TINYINT(1),
    patcher_URL VARCHAR(255),
    media_URL VARCHAR(255),
    logo_URL VARCHAR(255), 
    detail_URL VARCHAR(255),
    display_order INT,
    INDEX (world_name), 
    PRIMARY KEY (world_id));

-- create some worlds: use full dns name for the server_name
insert into world (dev_id, world_name, server_name, server_port, patcher_URL, media_URL) 
    values (1, "atavism_demo", "localhost", 5040, "", "");
