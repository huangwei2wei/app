<?php

/*********************************************************************
Function to find a property in XML set via Parent->Child
(property must be unique)
**********************************************************************/
function findValueInXMLChild($element, $methodName, $methodValue, $xml, $inMethodName, $inMethodValue, $typeofvalue = "itsastring")
{
	$item_info = "";
	$foundMethod = false;
  foreach ($xml->xpath("//" . $element) as $tmp)
  {
    if((string) $tmp[$methodName] == $methodValue )
    {
		if($foundMethod == false)
		{
			$foundMethod = true;
			$methodName = $inMethodName;
			$methodValue = $inMethodValue;
		}else{
			switch ($typeofvalue){
				case "itsastring":
					$item_info = $tmp->string[0];
				break;
				case "itsaint":
					$item_info = $tmp->int[0];
					
				break;
				case "itsabool":
					$item_info = $tmp->boolean[0];
				break;
				case "itsalong":
					$item_info = $tmp->long[0];
				break;
			}
			$methodName = "stop";
			$methodValue = "searching";
		}
    }
  }
  	return($item_info);
}

/*********************************************************************
Function to find a property in XML set (property must be unique)
**********************************************************************/
function findValueInXMLParent($element, $methodName, $methodValue ,$xml, $typeofvalue = "itsastring")
{
	$item_info = "";
	foreach ($xml->xpath("//" . $element) as $tmp){
    if((string) $tmp[$methodName] == $methodValue ){
		switch ($typeofvalue){
        		case "itsastring":
					$item_info = $tmp->string[0];
					//echo $tmp->string . " - " . (string) $tmp[$methodName] . " == $methodValue ---- $typeofvalue<br>";
				break;
				case "itsaint":
					$item_info = $tmp->int[0];
					
				break;
				case "itsabool":
					$item_info = $tmp->boolean[0];
				break;
				case "itsalong":
					$item_info = $tmp->long[0];
				break;
			}
    	}
  }
	return($item_info);
}

/*********************************************************************
Function to find a property in XML set 
**********************************************************************/
function findValueInXMLParentChild($element, $methodName, $methodValue, $stringname ,$xml, $typeofvalue = "itsastring")
{
	$item_info = "";
	foreach ($xml->xpath("//" . $element) as $tmp){
    if((string) $tmp[$methodName] == $methodValue ){
		if($tmp->string[0] == $stringname){
			switch ($typeofvalue){
					case "itsastring":
						$item_info = $tmp->string[1];
						//echo $tmp->string . " - " . (string) $tmp[$methodName] . " == $methodValue ---- $typeofvalue<br>";
					break;
					case "itsaint":
						$item_info = $tmp->int[0];
						
					break;
					case "itsabool":
						$item_info = $tmp->boolean[0];
					break;
					case "itsalong":
						$item_info = $tmp->long[0];
					break;
				}
			}
	}
  }
	return($item_info);
}

/*********************************************************************
Function to create the filelog
**********************************************************************/
function createFileLog($filename, $log)
{
	file_put_contents($filename, $log);
}

/*********************************************************************
Function to check in DB and rebuild XML part of bonusStats
**********************************************************************/
function rebuildXMLBonusStats($effectCount, $effectName, $effectValue, $xml, &$UpdateLog)
{					 
				$newxmlbonusstats = autoIndentXML(0) . "<void method=\"put\">\n";
				$newxmlbonusstats = $newxmlbonusstats . autoIndentXML(0) . "<string>bonusStats</string>\n";
				$newxmlbonusstats = $newxmlbonusstats . autoIndentXML(1) . "<object class=\"java.util.HashMap\">\n";
				
				for($arrayno = 1; $arrayno <= $effectCount; $arrayno++)
				{
					$newxmlbonusstats = $newxmlbonusstats . autoIndentXML(2) . "<void method=\"put\">\n";
					$newxmlbonusstats = $newxmlbonusstats . autoIndentXML(3) . "<string>" . $effectName[$arrayno] . "</string>\n";
					$newxmlbonusstats = $newxmlbonusstats . autoIndentXML(3) . "<int>" . $effectValue[$arrayno] . "</int>\n";
					$newxmlbonusstats = $newxmlbonusstats . autoIndentXML(2) . "</void>\n";	
				}
				
				$newxmlbonusstats = $newxmlbonusstats . autoIndentXML(1) . "</object>\n";
				$newxmlbonusstats = $newxmlbonusstats . autoIndentXML(0) . "</void>\n";

 return($newxmlbonusstats);	
}

/*********************************************************************
Function to create XML parts
**********************************************************************/
function createXMLParts($elements, $methods, $methodstrings, $stringtypes, $stringdatas, $extramethod = "", $extramethodname = "", $specialline = "", $speciallinepos = 0, $specialLineWhere = "Before")
{
	$elementsarray = explode("|", $elements);
	$methodsarray = explode("|", $methods);
	$methodsstringsarray = explode("|", $methodstrings);
	$stringstypearray = explode("|", $stringtypes);
	$stringdatasarray = explode("|", $stringdatas);
	$elementcount = count($elementsarray) - 1;
	$stringtypescount = count($stringstypearray) - 1;
	
	$newxml = "";
	
	for($inxml = 0; $inxml <= $elementcount; $inxml++)
	{
	  if ($extramethod != "" && $inxml == 0)
	  {
	  		$newxml = $newxml . autoIndentXML($inxml) . "<" . $elementsarray[$inxml] . " " . $methodsarray[$inxml] . "=\""  . $methodsstringsarray[$inxml] . " $extramethod=\"$extramethodname\"\">\n";	
	  }else{
		 $newxml = $newxml . autoIndentXML($inxml) . "<" . $elementsarray[$inxml] . " " . $methodsarray[$inxml] . "=\""  . $methodsstringsarray[$inxml] . "\">\n";
	  }
	  if($specialline != "" && $speciallinepos == $inxml && $specialLineWhere == "Before")
	  {
		  $newxml = $newxml . autoIndentXML($inxml) . $specialline . "\n";
	  }		
	}
	
	for($instringxml = 0; $instringxml <= $stringtypescount; $instringxml++)
	{
		if($stringstypearray[$instringxml] == "boolean"){
			if($stringdatasarray[$instringxml]){
				$stringdatasarray[$instringxml] = "true";
			}else{
				$stringdatasarray[$instringxml] = "false";
			}
		}
		if($stringdatasarray[$instringxml] != "null"){
			if($stringdatasarray[$instringxml] != "NULL")
			{
	  			$newxml = $newxml . autoIndentXML($inxml++) . "<" . $stringstypearray[$instringxml] . ">" . $stringdatasarray[$instringxml] . "</" . $stringstypearray[$instringxml] . ">\n";	
			}else{
				$newxml = $newxml . autoIndentXML($inxml++) . "<" . $stringstypearray[$instringxml] . "></" . $stringstypearray[$instringxml] . ">\n";	
			}
		}else{
			$newxml = $newxml . autoIndentXML($inxml++) . "<" . $stringstypearray[$instringxml] . " />\n";	
		}
	}
	if($specialline != "" && $specialLineWhere == "After")
	  {
		  $newxml = $newxml . autoIndentXML($inxml - 1) . $specialline . "\n";
	  }
	for($outxml = $elementcount; $outxml >= 0; $outxml--)
	{
	  $newxml = $newxml . autoIndentXML($outxml) . "</" . $elementsarray[$outxml] . ">\n";	
	}	
  return ($newxml);
}

/*********************************************************************
Function to autoindent XML
**********************************************************************/
function autoIndentXML($count, $myspaces = "  ")
{
	for($space = 0; $space <= $count; $space++)
	{
		$myspaces = $myspaces . " ";
	}
	return($myspaces);
}

/*********************************************************************
Function to reuturn values for Weapon and Armors
**********************************************************************/
function returnWeaponArmorValues($type, $slot, $value)
{
	$myvalue = "";
	if($type == "Weapon"){
		if($value == "name"){	
				switch($slot){
					case "Main Hand":
						$myvalue = "primaryWeapon";
					break;
					case "Off Hand":
						$myvalue = "secondaryWeapon";
					break;
					case " Two Hand":
						$myvalue = "twoHandWeapon";
					break;
					case "Two Hand":
						$myvalue = "twoHandWeapon";
					break;
					}
		}elseif($value == "item_equipInfo"){
				switch($slot){
					case "Main Hand":
						$myvalue = "weapon";
					break;
					case "Off Hand":
						$myvalue = "weapon";
					break;
					case " Two Hand":
						$myvalue = "weapon";
					break;
					case "Two Hand":
						$myvalue = "weapon";
					break;		
				}
		}
	}elseif($type == "Armor"){
		if($value == "name"){	
				switch($slot){
					case "Head":
						$myvalue = "head";
					break;
					case "Shoulder":
						$myvalue = "shoulder";
					break;
					case "Chest":
						$myvalue = "chest";
					break;
					case "Hands":
						$myvalue = "hands";
					break;
					case "Back":
						$myvalue = "back";
					break;
					case "Waist":
						$myvalue = "belt";
					break;
					case "Leg":
						$myvalue = "legs";
					break;
					case "Feet":
						$myvalue = "feet";
					break;
					case "Off Hand":
						$myvalue = "secondaryWeapon";
					break;
					}
			}elseif($value == "item_equipInfo"){
				switch($slot){
					case "Head":
						$myvalue = "helm";
					break;
					case "Shoulder":
						$myvalue = "shoulders";
					break;
					case "Chest":
						$myvalue = "chest";
					break;
					case "Hands":
						$myvalue = "hands";
					break;
					case "Back":
						$myvalue = "back";
					break;
					case "Waist":
						$myvalue = "belt";
					break;
					case "Leg":
						$myvalue = "legs";
					break;
					case "Feet":
						$myvalue = "feet";
					break;
					case "Off Hand":
						$myvalue = "weapon";
					break;
				}		
			}
		}
	return $myvalue;	
}

/*********************************************************************
Function to Backup DB
**********************************************************************/
function &backup_tables($host, $user, $pass, $name, $tables = '*')
{
  $data = "\n/*---------------------------------------------------------------".
          "\n  SQL DB BACKUP ".date("d.m.Y H:i")." ".
          "\n  HOST: {$host}".
          "\n  DATABASE: {$name}".
          "\n  TABLES: {$tables}".
          "\n  ---------------------------------------------------------------*/\n";
  $link = mysql_connect($host,$user,$pass);
  mysql_select_db($name,$link);
  mysql_query( "SET NAMES `utf8` COLLATE `utf8_general_ci`" , $link ); // Unicode

  if($tables == '*'){ //get all of the tables
    $tables = array();
    $result = mysql_query("SHOW TABLES");
    while($row = mysql_fetch_row($result)){
      $tables[] = $row[0];
    }
  }else{
    $tables = is_array($tables) ? $tables : explode(',',$tables);
  }

  foreach($tables as $table){
    $data.= "\n/*---------------------------------------------------------------".
            "\n  TABLE: `{$table}`".
            "\n  ---------------------------------------------------------------*/\n"; 
	/*
	-- SPECIAL ADDED FOR ATAVISM FK player_character --
	*/          
	if($table == "objstore")
	{
		$data.= "DROP TABLE IF EXISTS `player_character`;\n";
	}
	/*
	-- END SPECIAL ADDED FOR ATAVISM FK player_character --
	*/   	
	
    $data.= "DROP TABLE IF EXISTS `{$table}`;\n";
    $res = mysql_query("SHOW CREATE TABLE `{$table}`", $link);
    $row = mysql_fetch_row($res);
    $data.= $row[1].";\n";

    $result = mysql_query("SELECT * FROM `{$table}`", $link);
    $num_rows = mysql_num_rows($result);    

    if($num_rows>0){
      $vals = Array(); $z=0;
      for($i=0; $i<$num_rows; $i++){
        $items = mysql_fetch_row($result);
        $vals[$z]="(";
        for($j=0; $j<count($items); $j++){
          if (isset($items[$j])) { $vals[$z].= "'".mysql_real_escape_string( $items[$j], $link )."'"; } else { $vals[$z].= "NULL"; }
          if ($j<(count($items)-1)){ $vals[$z].= ","; }
        }
        $vals[$z].= ")"; $z++;
      }
      $data.= "INSERT INTO `{$table}` VALUES ";      
      $data .= "  ".implode(";\nINSERT INTO `{$table}` VALUES ", $vals).";\n";
    }
  }
  mysql_close( $link );
  return $data;
}


/*********************************************************************
Function to Restore DB
**********************************************************************/
function restoreMySQLTable($mysql_host, $mysql_username, $mysql_password, $mysql_database, $filename)
{
	$queryerror = false; 
	// Connect to MySQL server
	mysql_connect($mysql_host, $mysql_username, $mysql_password) or die('Error connecting to MySQL server: ' . mysql_error());
	// Select database
	mysql_select_db($mysql_database) or die('Error selecting MySQL database: ' . mysql_error());
	
	// Temporary variable, used to store current query
	$templine = '';
	// Read in entire file
	$lines = file($filename);
	// Loop through each line
	foreach ($lines as $line)
	{
		// Skip it if it's a comment
		if (substr($line, 0, 2) == '--' || $line == '')
			continue;
		
		// Add this line to the current segment
		$templine .= $line;
		// If it has a semicolon at the end, it's the end of the query
			if (substr(trim($line), -1, 1) == ';')
			{
				// Perform the query
				mysql_query($templine) or print('Error performing query \'<strong>' . $templine . '\': ' . mysql_error() . '<br /><br />');
				//mysql_query($templine) or $queryerror = true;
				// Reset temp variable to empty
				$templine = '';
			}
		}
	return $queryerror;
}

/*********************************************************************
Function to Restore DB with Trigger -- USE IN INSTALLATION
**********************************************************************/
function restoreMySQLTableSpecialTrigger($mysql_host, $mysql_username, $mysql_password, $mysql_database, $filename)
{
	$itsadelimiter = false;
	$delimiterline = 1;
	$queryerror = false; 
	// Connect to MySQL server
	mysql_connect($mysql_host, $mysql_username, $mysql_password) or die('Error connecting to MySQL server: ' . mysql_error());
	// Select database
	//mysql_select_db($mysql_database) or die('Error selecting MySQL database: ' . mysql_error());
	
	if(!mysql_select_db($mysql_database))
	{
	  // We don't need to create db it will be created
	  echo "Creating DB $mysql_database - New Fresh Install<br>";
	}else{
		echo "Dropping DB $mysql_database - Dropping existing DB, hope you made a backup<br>";	
	}
	
	
	// Temporary variable, used to store current query
	$templine = '';
	// Read in entire file
	$lines = file($filename);
	// Loop through each line
	foreach ($lines as $line)
	{
		// Skip it if it's a comment
		if (substr($line, 0, 2) == '--' || $line == '')
			continue;

		//check if beginnning of Trigger Delimitter
		if (substr($line, 0, 12) == 'DELIMITER ;;' || $itsadelimiter == true)
		{
			$itsadelimiter = true;
			$mytempline = substr($line, 0, 12);
			
			if($mytempline === 'DELIMITER ;;' || substr( $mytempline, 0, 2 ) === ";;")
			{
			  // Do nothing
        //echo "doing nothing<br>";	
			}else if(substr( $mytempline, 0, 9 ) === 'DELIMITER')
			{
        //echo "got closing delimiter<br>";	
				$itsadelimiter = false;
				mysql_query($templine) or print('Error performing query \'<strong>' . $templine . '\': ' . mysql_error() . '<br /><br />');
				$templine = '';
			}else
			{
        //echo "running standard line<br>";	
        $templine .= $line;
			}
			
		
		}else{
		// Add this line to the current segment
		$templine .= $line;
		// If it has a semicolon at the end, it's the end of the query
			if (substr(trim($line), -1, 1) == ';')
			{
				// Perform the query
				mysql_query($templine) or print('Error performing query \'<strong>' . $templine . '\': ' . mysql_error() . '<br /><br />');
				//mysql_query($templine) or $queryerror = true;
				// Reset temp variable to empty
				$templine = '';
			}
		}
	}
	return $queryerror;
}

?>


