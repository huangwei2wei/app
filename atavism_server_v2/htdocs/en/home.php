<?php
if(isset($_SESSION['login_user'])){
 echo "This will be the CMS for Atavism. It is under heavy developement!<br><br>";
}else{
	header("location: index.php"); // Redirecting To Other Page
}
?>