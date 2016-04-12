<?php
include('config.inc.php');
$conndb_atavism = mysql_connect($dbhost, $dbuser, $dbpass) or die                      ('Error connecting to mysql');
$conndb_atavism2 = mysql_connect($dbhost, $dbuser, $dbpass) or die                      ('Error connecting to mysql');
$conndb_world_content = mysql_connect($dbhost, $dbuser, $dbpass, true) or die                      ('Error connecting to mysql');
$conndb_admin = mysql_connect($dbhost, $dbuser, $dbpass, true) or die                      ('Error connecting to mysql');
$conndb_master = mysql_connect($dbhost, $dbuser, $dbpass, true) or die                      ('Error connecting to mysql');
mysql_select_db($dbname_atavism, $conndb_atavism);
mysql_select_db($dbname_atavism, $conndb_atavism2);
mysql_select_db($dbname_world_content, $conndb_world_content);
mysql_select_db($dbname_admin, $conndb_admin);
mysql_select_db($dbname_master, $conndb_master);
?>