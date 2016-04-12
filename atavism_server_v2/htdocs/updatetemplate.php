<?php
	include_once 'connection.inc.php';
	include_once 'conndb_atavism.inc.php';
	include_once 'atavismfunctions.inc.php';
	
			if ( isset( $_GET['table_name'] ) ) {
				$Table_Name = $_GET['table_name'];
			} 
			if ( isset( $_GET['update_time'] ) ) {
				$Update_Time = $_GET['update_time'];
			} 	
			if ( isset( $_GET['type_of_backdating'] ) ) {
				$Type_of_Backdating = $_GET['type_of_backdating'];
			} 
	
	$query_atavism_objstore_tables  = "SELECT * FROM objstore WHERE type='$Type_of_Backdating'";
	$result_objstore_tables = mysql_query($query_atavism_objstore_tables, $conndb_atavism);
	
	
	while($row_atavism_objstore_tables = mysql_fetch_array($result_objstore_tables, MYSQL_ASSOC))
	{
		$objstore_BLOB = $row_atavism_objstore_tables['data'];
		$objstore_obj_id = $row_atavism_objstore_tables['obj_id'];
		$objstore_namespace_int = $row_atavism_objstore_tables['namespace_int'];
		
		$xml = simplexml_load_string($objstore_BLOB);
		
		//print_r($xml);
		//echo "<br>$value<br>";
		//echo "<br><br><br><br>";
		//echo $xml->object[0]->void[0]->object[0]->void[0]->int; 
		//echo $xml->object[0]->void[8]->int;
		/*
		foreach ( $xml->object[0]->void as $tmp) {
			if((string) $tmp['property'] == "templateID") {
			echo $tmp->int;
			}
		}*/
		
		foreach ($xml->object as $layer0)
		{
		  if((string) $layer0['class'] == "atavism.agis.objects.AgisItem")
		  {
			foreach ( $layer0->void as $tmp)
			{
			  if((string) $tmp['property'] == "templateID")
			  {
				//echo $tmp->int;
				$templateID =  $tmp->int;
			  }
			}
		  }
		}
		
	
		$query_atavism_objstore_templateID  = "SELECT * FROM $Table_Name WHERE id=$templateID";
		$result_objstore_templateID = mysql_query($query_atavism_objstore_templateID, $conndb_world_content);
		
		while($row_atavism_objstore_templateID = mysql_fetch_array($result_objstore_templateID, MYSQL_ASSOC))
		{	
			$templateID_name = $row_atavism_objstore_templateID['name'];	//echo $templateID_name;
			$templateID_icon = $row_atavism_objstore_templateID['icon'];
			$templateID_category = $row_atavism_objstore_templateID['category'];
			$templateID_subcategory = $row_atavism_objstore_templateID['subcategory'];
			$templateID_itemtype = $row_atavism_objstore_templateID['itemType'];
			$templateID_subtype = $row_atavism_objstore_templateID['subType'];
			$templateID_slot = $row_atavism_objstore_templateID['slot'];
			$templateID_display = $row_atavism_objstore_templateID['display'];
			$templateID_itemQuality = $row_atavism_objstore_templateID['itemQuality'];
			$templateID_binding = $row_atavism_objstore_templateID['binding'];
			$templateID_isUnique = $row_atavism_objstore_templateID['isUnique'];
			$templateID_stackLimit = $row_atavism_objstore_templateID['stackLimit'];
			$templateID_duration = $row_atavism_objstore_templateID['duration'];
			$templateID_purchaseCurrency = $row_atavism_objstore_templateID['purchaseCurrency'];
			$templateID_purchaseCost = $row_atavism_objstore_templateID['purchaseCost'];
			$templateID_sellable = $row_atavism_objstore_templateID['sellable'];
			$templateID_levelReq = $row_atavism_objstore_templateID['levelReq'];
			$templateID_aspectReq = $row_atavism_objstore_templateID['aspectReq'];
			$templateID_raceReq = $row_atavism_objstore_templateID['raceReq'];
			$templateID_damage = $row_atavism_objstore_templateID['damage'];
			$templateID_damageType = $row_atavism_objstore_templateID['damageType'];
			$templateID_delay = $row_atavism_objstore_templateID['delay'];
			$templateID_toolTip = $row_atavism_objstore_templateID['toolTip'];
			$templateID_triggerEvent = $row_atavism_objstore_templateID['triggerEvent'];
			$templateID_triggerAction1Type = $row_atavism_objstore_templateID['triggerAction1Type'];
			$templateID_triggerAction1Data = $row_atavism_objstore_templateID['triggerAction1Data'];
			$templateID_effect1type = $row_atavism_objstore_templateID['effect1type'];
			$templateID_effect1name = $row_atavism_objstore_templateID['effect1name'];
			$templateID_effect1value = $row_atavism_objstore_templateID['effect1value'];
			$templateID_effect2type = $row_atavism_objstore_templateID['effect2type'];
			$templateID_effect2name = $row_atavism_objstore_templateID['effect2name'];
			$templateID_effect2value = $row_atavism_objstore_templateID['effect2value'];
			$templateID_effect3type = $row_atavism_objstore_templateID['effect3type'];
			$templateID_effect3name = $row_atavism_objstore_templateID['effect3name'];
			$templateID_effect3value = $row_atavism_objstore_templateID['effect3value'];
			$templateID_effect4type = $row_atavism_objstore_templateID['effect4type'];
			$templateID_effect4name = $row_atavism_objstore_templateID['effect4name'];
			$templateID_effect4value = $row_atavism_objstore_templateID['effect4value'];
			$templateID_effect5type = $row_atavism_objstore_templateID['effect5type'];
			$templateID_effect5name = $row_atavism_objstore_templateID['effect5name'];
			$templateID_effect5value = $row_atavism_objstore_templateID['effect5value'];
			$templateID_effect6type = $row_atavism_objstore_templateID['effect6type'];
			$templateID_effect6name = $row_atavism_objstore_templateID['effect6name'];
			$templateID_effect6value = $row_atavism_objstore_templateID['effect8value'];
			$templateID_effect7type = $row_atavism_objstore_templateID['effect7type'];
			$templateID_effect7name = $row_atavism_objstore_templateID['effect7name'];
			$templateID_effect7value = $row_atavism_objstore_templateID['effect7value'];
			$templateID_effect8type = $row_atavism_objstore_templateID['effect8type'];
			$templateID_effect8name = $row_atavism_objstore_templateID['effect8name'];
			$templateID_effect8value = $row_atavism_objstore_templateID['effect8value'];
			$templateID_effect9type = $row_atavism_objstore_templateID['effect9type'];
			$templateID_effect9name = $row_atavism_objstore_templateID['effect9name'];
			$templateID_effect9value = $row_atavism_objstore_templateID['effect9value'];
			$templateID_effect10type = $row_atavism_objstore_templateID['effect10type'];
			$templateID_effect10name = $row_atavism_objstore_templateID['effect10name'];
			$templateID_effect10value = $row_atavism_objstore_templateID['effect10value'];
			$templateID_effect11type = $row_atavism_objstore_templateID['effect11type'];
			$templateID_effect11name = $row_atavism_objstore_templateID['effect11name'];
			$templateID_effect11value = $row_atavism_objstore_templateID['effect11value'];
			$templateID_effect12type = $row_atavism_objstore_templateID['effect12type'];
			$templateID_effect12name = $row_atavism_objstore_templateID['effect12name'];
			$templateID_effect12value = $row_atavism_objstore_templateID['effect12value'];
			$templateID_isactive = $row_atavism_objstore_templateID['isactive'];
	
			
		}
		
	$xml = replaceProperties("void", "property", "name", $templateID_name, $xml);
	$xml = replaceProperties("void", "property", "icon", $templateID_icon, $xml);
	$xml = replaceProperties("void", "property", "category", $templateID_category, $xml);
	
	
	//$xml_mysql_escape_string = mysql_escape_string($xml);
	
	//print_r($xml_mysql_escape_string);
	//print_r($xml);
	
	$string = <<<XML
	$xml
	XML;
	
	//$xml = new SimpleXMLElement($string);
	
	//echo $xml->asXML();
	
	$query_atavism_objstore_tables_update  = "UPDATE objstore SET data= '" . $xml->asXML() . "' WHERE obj_id='$objstore_obj_id' AND namespace_int='$objstore_namespace_int'";
	
	//$result_objstore_tables_update = mysql_query($query_atavism_objstore_tables, $conndb_atavism2);	
	//echo $query_atavism_objstore_tables_update;
	
	if (mysql_query($query_atavism_objstore_tables_update, $conndb_atavism2)) {
		echo "Record updated successfully";
	} else {
		echo "Error updating record: " . $conndb_atavism2;
	}	
	
	}
?>