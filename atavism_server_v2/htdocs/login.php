<?php
include('conndb_atavism.inc.php');
session_start(); // Starting Session
$error=''; // Variable To Store Error Message
if (isset($_POST['submit'])) {
	if (empty($_POST['username']) || empty($_POST['password'])) {
		$error = "Username or Password is invalid";
	}else{
		// Define $username and $password
		$username=$_POST['username'];
		$password=$_POST['password'];
		// To protect MySQL injection for Security purpose
		$username = stripslashes($username);
		$password = stripslashes($password);
		$username = mysql_real_escape_string($username);
		$password = mysql_real_escape_string($password);
		$md5password = md5($password);
		// SQL query to fetch information of registerd users and finds user match.
		$query_atavism_login  = "SELECT * FROM account WHERE username='$username' AND password='$md5password'";
		$result_atavism_login = mysql_query($query_atavism_login, $conndb_master);
				
		$rows = mysql_num_rows($result_atavism_login);
		//echo md5($password);
		if ($rows == 1) {
			$_SESSION['login_user'] = $username; // Initializing Session
			//Check the level admin of logged in user
			$query_atavism_admin_level  = "SELECT status FROM account WHERE username='$username'";
			$result_atavism_admin_level = mysql_query($query_atavism_admin_level, $conndb_admin);
			$admin_level = mysql_fetch_row($result_atavism_admin_level);
			
			$_SESSION['admin_level'] = $admin_level[0];
			
			header("location: index.php?middle=Home"); // Redirecting To Other Page
		}else{
			$error = "Username or Password is invalid";
		}
		 //mysql_close($connection); // Closing Connection
	}
}
?>