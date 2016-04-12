<!doctype html>
<html>
<head>
<meta charset="utf-8">
<title>Atavism Installation Script</title>
</head>

<body>

Make sure you remove this folder after installation <br>
<br>
<?php
include("../atavismfunctions.inc.php");
include("../config.inc.php");

ini_set('max_execution_time', 300);

$dbuser = "root"; //Enter your root MySQL Username
$dbpass = "test"; //Enter your MySQL Password


	$dbrestore_master = restoreMySQLTableSpecialTrigger($dbhost,$dbuser,$dbpass,$dbname_master,"./sql/auth.sql");
	$dbrestore_admin = restoreMySQLTableSpecialTrigger($dbhost,$dbuser,$dbpass,$dbname_admin,"./sql/admin.sql");
	$dbrestore_atavism = restoreMySQLTableSpecialTrigger($dbhost,$dbuser,$dbpass,$dbname_atavism,"./sql/install.sql");
	$dbrestore_world_content = restoreMySQLTableSpecialTrigger($dbhost,$dbuser,$dbpass,$dbname_world_content,"./sql/world_content.sql");
	
echo "<br><br>If you do not see any errors, everything has been imported correctly. Your databases are ready to go.";

?>

</body>
</html>