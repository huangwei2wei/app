<?php
$directory = ".\backups\sql\\";
$logFiles = glob($directory . "*.sql");

echo "<br><br>Already processed SQL Backup files:<br>";

foreach($logFiles as $logfile)
{
	echo "<a href=$logfile>" . basename($logfile) . "</a>  --- <a href=restoreMySQLfromBackup.php?filename=$logfile>Restore</a><br>";
}

?>