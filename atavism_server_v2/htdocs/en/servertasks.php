<?php
if(isset($_SESSION['login_user'])){
//Put stuff here
}else{
	header("location: index.php"); // Redirecting To Other Page
}
?>