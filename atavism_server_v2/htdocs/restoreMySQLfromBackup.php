<?php
if(isset($_SESSION['login_user'])){
	include_once("config.inc.php");
	include_once("./atavismfunctions.inc.php");
	
	// create backup
	//////////////////////////////////////
	
	if ( isset( $_GET['filename'] ) ) {
		$fileName = $_GET['filename'];
	} 
	
	$pathAndfileName = $fileName;
	
	// get backup
	$myrestore = restoreMySQLTable($dbhost,$dbuser,$dbpass,$dbname_atavism,$pathAndfileName);
	
	echo "Restore succesfull<br><br><a href=\"index.php\">Back to Dashboard</a><br><br>";
}else{
	header("location: index.php"); // Redirecting To Other Page	
}
?>