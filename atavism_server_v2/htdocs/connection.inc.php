<?php
$dbhost = "localhost";
$dbuser = "Atavism";
$dbpass = "Qwerty80*";

$dbname_atavism = 'atavism';
$dbname_world_content = 'world_content';

// Create connection
$conn = new mysqli($dbhost, $dbuser, $dbpass);

// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
} 
//echo "Connected successfully";
?>