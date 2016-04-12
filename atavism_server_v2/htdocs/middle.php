<?php
switch($middle)
{
	case 'Home':
		include("./en/home.php");
	break;
	case 'ServerTasks':
		include("./en/servertasks.php");
	break;
	case 'Users':
		include("./en/users.php");
	break;
	case 'Backdating':
		include("./en/backdating.php");
	break;
	case 'DBItems':
		include("./en/dbitems.php");
	break;
	case 'DBQuests':
		include("./en/dbquests.php");
	break;
	case 'MakeSQLBackupAtavism':
		include("./makeSQLBackup.php");
	break;
	case 'UpdateItems':
		include("./update_itemtemplates.php");
	break;
	default:
		include("./en/loginpage.php");
	break;
}
?>