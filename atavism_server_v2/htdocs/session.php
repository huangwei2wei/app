<?php
include('conndb_atavism.inc.php');
session_start();// Starting Session
// Storing Session
$user_check=$_SESSION['login_user'];
// SQL Query To Fetch Complete Information Of User
$ses_sql=mysql_query("select username from account where username='$user_check'", $conndb_master);
$row = mysql_fetch_assoc($ses_sql);
$login_session =$row['username'];
if(!isset($login_session)){
	//mysql_close($connection); // Closing Connection
	header('Location: index.php'); // Redirecting To Home Page
}
?>