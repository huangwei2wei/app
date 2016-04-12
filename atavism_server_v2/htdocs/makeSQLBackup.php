<?php
if(isset($_SESSION['login_user'])){

	include_once("config.inc.php");
	include_once("./atavismfunctions.inc.php");
	// create backup
	//////////////////////////////////////
	
	$backup_file = ".\backups\sql\Atavism-" .date("m-d-Y-h_i_s_a", time()) . ".sql";
	
	// get backup
	$mybackup = backup_tables($dbhost,$dbuser,$dbpass,$dbname_atavism);
	
	// save to file
	$handle = fopen($backup_file,"w+");
	fwrite($handle,$mybackup);
	fclose($handle);
	
}else{
	header("location: index.php"); // Redirecting To Other Page	
}

?>