<?php
if(isset($_SESSION['login_user']) && $_SESSION['admin_level'] > 4){
		echo "Make sure to backup your Atavism database before backdating by clicking <a href=\"index.php?middle=MakeSQLBackupAtavism\">here</a><br><br>";
		include_once("./getBackdatingDB.php");
		include_once("./getLogs.php");
		include_once("./getSQLBackup.php");	
}else if(isset($_SESSION['login_user'])){
	echo "You do not have enought rights to use this function";
}else{
	header("location: index.php"); // Redirecting To Other Page
}
?>