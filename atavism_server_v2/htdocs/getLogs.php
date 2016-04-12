<?php
if(isset($_SESSION['login_user'])){
	$directory = ".\logs\\";
	$logFiles = glob($directory . "*.log");
	
	echo "<br><br>Already processed logs files:<br>";
	
	foreach($logFiles as $logfile)
	{
		echo "<a href=$logfile>".basename($logfile)."</a><br>";
	}
}else{
	header("location: index.php"); // Redirecting To Other Page	
}
?>