<?php
$conndb_atavism = mysql_connect($dbhost, $dbuser, $dbpass, true) or die                      ('Error connecting to mysql');
mysql_select_db($dbname_world_content);
?>