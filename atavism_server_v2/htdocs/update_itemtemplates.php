<?php
if(isset($_SESSION['login_user'])){
	include_once 'config.inc.php';
	include_once 'conndb_atavism.inc.php';
	include_once 'atavismfunctions.inc.php';
	
	$msc = microtime(true);
	$loopnumber = 0;
	$UpdateLog = "";
	$globalbackdatingerror = false;
	$globalDestroyedItems = 0;
	$globalNumberofBackdatingerrors = 0;
	
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
		$loopnumber ++;
		$itemIsToBeDeleted = false;
		$objstore_BLOB = $row_atavism_objstore_tables['data'];
		$objstore_obj_id = $row_atavism_objstore_tables['obj_id'];
		$objstore_namespace_int = $row_atavism_objstore_tables['namespace_int'];
		
		$xml = simplexml_load_string($objstore_BLOB);
		// Special Attributes
		$templateID = findValueInXMLParent("void", "property", "templateID", $xml, "itsaint");	
		$OIDReference = findValueInXMLChild("void", "property", "oid", $xml, "property", "data", "itsalong");
		$bagReference = findValueInXMLChild("void", "method", "put", $xml, "property", "data", "itsalong");
	
	
			
		$query_atavism_objstore_templateID  = "SELECT * FROM $Table_Name WHERE id=$templateID";
		$result_objstore_templateID = mysql_query($query_atavism_objstore_templateID, $conndb_world_content);
			
		if(!$result_objstore_templateID){ // check if the TemplateID exist - If not it has been deleted	
			$UpdateLog = $UpdateLog . "Could not find TemplateID #$templateID in items_template DB)";
			$globalNumberofBackdatingerrors++;
		}else{
			while($row_atavism_objstore_templateID = mysql_fetch_array($result_objstore_templateID, MYSQL_ASSOC))
			{
				if($Update_Time < $row_atavism_objstore_templateID['updatetimestamp'] || REPLACEALLOBJSTORE == true)
				{	
					$DoUpdatingItem = true;
					$templateID_name = $row_atavism_objstore_templateID['name'];	//echo $templateID_name;
					$templateID_baseName = $row_atavism_objstore_templateID['name'];
					$templateID_icon = $row_atavism_objstore_templateID['icon'];
					$templateID_category = $row_atavism_objstore_templateID['category'];
					$templateID_subCategory = $row_atavism_objstore_templateID['subcategory'];
					$templateID_itemType = $row_atavism_objstore_templateID['itemType'];
					$templateID_subType = $row_atavism_objstore_templateID['subType'];
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
					
					for($effectNumber = 1; $effectNumber <= EFFECTSCOL; $effectNumber++){
							${"templateID_effect" . $effectNumber . "type"} = $row_atavism_objstore_templateID['effect' . $effectNumber . 'type'];
							${"templateID_effect" . $effectNumber . "name"} = $row_atavism_objstore_templateID['effect' . $effectNumber . 'name'];
							${"templateID_effect" . $effectNumber . "value"} = $row_atavism_objstore_templateID['effect' . $effectNumber . 'value'];
					}
				}else{
					$DoUpdatingItem = false;
				}
			}	
			if($DoUpdatingItem)
			{
				$xmlItemType = "";
				
				$UpdateLog = $UpdateLog . "Item #$loopnumber) $templateID_baseName\n";
				
				$xmlItemType = findValueInXMLParent("void", "property", "itemType", $xml);
				$xmlbackdatingerror = false;
				
				if ($xmlItemType == $templateID_itemType){ //Weapon and Armors cannot change type - Checking in else if they are
					// need to check slot after and then create newxml
					$xmlSlotType = "";
					$xmlSlotType = findValueInXMLParentChild("void", "method", "put", "slot", $xml);
					if($xmlSlotType != ""){
						// This item has a slot
						if($xmlSlotType == $templateID_slot){
							//Proceed with item create
							$areslot = true;	
						}else{
							$UpdateLog = $UpdateLog . "------   $templateID_name is $xmlSlotType, New Slot Type is $templateID_slot - Cannot Backdate item\n";
							$xmlbackdatingerror = true;
							$globalbackdatingerror = true;
							$globalNumberofBackdatingerrors++;
						}
					}else{
						// This item does not have equipslot but type is true
						$areslot = false;
					}	
				}else if($templateID_itemType == "Armor" || $templateID_itemType == "Weapon"){	//If its not Weapon or Armor proceed with change
					$UpdateLog = $UpdateLog . "------   $templateID_name is $xmlItemType, New Item Type is $templateID_itemType - Cannot Backdate item\n";
					$xmlbackdatingerror = true;
					$globalbackdatingerror = true;
					$globalNumberofBackdatingerrors++;
				}
			
				if($xmlbackdatingerror == false){
					/*******************************************************************
					Check for different Attributes of effects
					/*******************************************************************/
					$effectNameArray = array();
					$effectValueArray = array();
					$effectCount = 0;
					$xmlHasStats = false;
					$xmlHasAbility = false;
					$xmlAbilityID = "";
					
					for($effectNumber = 1; $effectNumber <= EFFECTSCOL; $effectNumber++)
					{
						if(${"templateID_effect" . $effectNumber . "type"} == "Stat")
						{
							$xmlHasStats = true;
							$effectCount++;
							$effectNameArray[$effectCount] = ${"templateID_effect" . $effectNumber . "name"};
							$effectValueArray[$effectCount] = ${"templateID_effect" . $effectNumber . "value"};
						}
						if(${"templateID_effect" . $effectNumber . "type"} == "UseAbility")
						{
							$xmlHasAbility = true;
							$xmlAbilityID = ${"templateID_effect" . $effectNumber . "value"};
						}
						
					}
					
					//$displayValue = findValueInXMLParentChild("void", "method", "put", "displayVal", $xml);
					$hasEquipSlotName = findValueInXMLParent("void", "property", "equipSlots", $xml);
					
					
					/*******************************************************************
					Start creation of new javabean XML
					/*******************************************************************/
					$newxml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
					$newxml = $newxml . "<java version=\"1.7.0_76\" class=\"java.beans.XMLDecoder\">\n";
					$newxml = $newxml . autoIndentXML(1, "") . "<object class=\"atavism.agis.objects.AgisItem\" id=\"AgisItem0\">\n";
					/*******************************************************************
					Add different hooks
					/*******************************************************************/
					//if Ability
					 if($xmlHasAbility){
						$newxml = $newxml . createXMLParts("void|object|void", "property|class|property", "activateHook|atavism.agis.core.AbilityActivateHook|abilityID", "int", $xmlAbilityID);	 
					 }else{
						$newxml = $newxml . autoIndentXML(2, "") . "<void property=\"activateHook\">\n"; 
						$newxml = $newxml . autoIndentXML(3, "") . "<object class=\"atavism.agis.core.EquipActivateHook\"/>\n";
						$newxml = $newxml . autoIndentXML(2, "") . "</void>\n"; 
					 }
					//if weapon or armor
					if($templateID_itemType == "Weapon" || $templateID_itemType == "Armor"){
						$newxml = $newxml . createXMLParts("void|object|void|object|void", "property|class|method|class|property", "equipSlots|java.util.ArrayList|add|atavism.agis.objects.AgisEquipSlot|name", "string", returnWeaponArmorValues($templateID_itemType, $templateID_slot, "name"));	
					}
					/*******************************************************************
					Add properties ??? TEMPLATE BY OBJECT TYPE ???
					/*******************************************************************/
					$newxml = $newxml . createXMLParts("void", "property", "icon", "string", $templateID_icon);
					$newxml = $newxml . createXMLParts("void", "property", "itemType", "string", $templateID_itemType);
					$newxml = $newxml . createXMLParts("void", "property", "name", "string", $templateID_name);
					$newxml = $newxml . createXMLParts("void|object|void", "property|class|property", "oid|atavism.server.engine.OID|data", "long", $OIDReference);		
					$newxml = $newxml . createXMLParts("void", "property", "persistenceFlag", "boolean", "true");
					
					$newxml = $newxml . autoIndentXML(1) . "<void id=\"HashMap0\" property=\"propertyMap\">\n"; // opening id Hashmap0
					
					$newxml = $newxml . createXMLParts("void", "method", "put", "string|int", "itemID|" . $templateID);
					$newxml = $newxml . createXMLParts("void", "method", "put", "string|string", "damageType|" . $templateID_damageType);
					$newxml = $newxml . createXMLParts("void", "method", "put", "string|boolean", "sellable|" . $templateID_sellable);
					$newxml = $newxml . createXMLParts("void", "method", "put", "string|int", "purchaseCost|" . $templateID_purchaseCost);
					$newxml = $newxml . createXMLParts("void", "method", "put", "string|boolean", "isUnique|" . $templateID_isUnique);
					$newxml = $newxml . createXMLParts("void", "method", "put", "string|int", "stackLimit|" . $templateID_stackLimit);
					$newxml = $newxml . createXMLParts("void", "method", "put", "string|int", "purchaseCurrency|" . $templateID_purchaseCurrency);
					
					//if slot then add displayvalue
					if($templateID_display != ""){
						$newxml = $newxml . createXMLParts("void", "method", "put", "string|string", "slot|" . $templateID_slot);
						$newxml = $newxml . createXMLParts("void", "method", "put", "string|string", "displayVal|" . $templateID_display);
					}
					$newxml = $newxml . createXMLParts("void", "method", "put", "string|int", "itemGrade|" . $templateID_itemQuality);
					if($bagReference != "")
					{
						$newxml = $newxml . createXMLParts("void|object|void", "method|class|property", "put|atavism.server.engine.OID|data", "long", $bagReference, "", "", "<string>inv.backref</string>", 0);
						//echo createXMLParts("void|object|void", "method|class|property", "put|atavism.server.engine.OID|data", "long", $bagReference, "", "", "<string>inv.backref</string>", 1) . "<br>";
					}else{
						// Object not in use delete
						$itemIsToBeDeleted = true;
						$globalDestroyedItems++;
						$newxml = $newxml . createXMLParts("void", "method", "put", "string|null", "inv.backref|null");
					}
					
					$newxml = $newxml . createXMLParts("void", "method", "put", "string|int", "damage|" . $templateID_damage);
					$newxml = $newxml . createXMLParts("void", "method", "put", "string|string", "raceReq|" . $templateID_raceReq);
					$newxml = $newxml . createXMLParts("void", "method", "put", "string|null", "aspectReq|null");
					$newxml = $newxml . createXMLParts("void", "method", "put", "string|string", "triggerEvent|NULL");
					$newxml = $newxml . createXMLParts("void", "method", "put", "string|string", "category|" . $templateID_category);
					$newxml = $newxml . createXMLParts("void", "method", "put", "string|string", "subCategory|" . $templateID_subCategory);
					$newxml = $newxml . createXMLParts("void", "method", "put", "string|string", "subType|" . $templateID_subType);
					$newxml = $newxml . createXMLParts("void", "method", "put", "string|int", "binding|" . $templateID_binding);
					$newxml = $newxml . createXMLParts("void", "method", "put", "string|string", "baseName|" . $templateID_baseName);
					
					if($effectCount > 0){	
						if($xmlHasStats == true){
							$newxml = $newxml . rebuildXMLBonusStats($effectCount, $effectNameArray, $effectValueArray, $xml, $UpdateLog);
						}
					}else{
						$newxml = $newxml . createXMLParts("void", "method", "put", "string", "bonusStats", "", "", "<object class=\"java.util.HashMap\"/>", 0, "After");
					}
			
					$newxml = $newxml . createXMLParts("void", "method", "put", "string|int", "delay|" . $templateID_delay * 1000); //special multuply delay by 1000
					$newxml = $newxml . createXMLParts("void", "method", "put", "string|int", "levelReq|" . $templateID_levelReq);
					if($templateID_itemType == "Weapon" || $templateID_itemType == "Armor"){
						$newxml = $newxml . createXMLParts("void|void", "method|property", "get|name", "string", returnWeaponArmorValues($templateID_itemType, $templateID_slot, "item_equipInfo"), "", "", "<string>item_equipInfo</string>", 0);
						//echo $templateID_baseName . ": " . returnWeaponArmorValues($templateID_itemType, $templateID_slot, "item_equipInfo") . "<br>";
					}
					if($xmlHasAbility){
						$newxml = $newxml . createXMLParts("void", "method", "put", "string|int", "abilityID|" . $xmlAbilityID);
					 }
			
					$newxml = $newxml . autoIndentXML(1, " ") . "</void>\n"; // closing id Hashmap0
					
					$newxml = $newxml . autoIndentXML(1, " ") . "<void property=\"propertyMap\">\n";
					$newxml = $newxml . autoIndentXML(1, " ") . "<object idref=\"HashMap0\"/>\n";
					$newxml = $newxml . autoIndentXML(1, " ") . "</void>\n";
					$newxml = $newxml . createXMLParts("void", "property", "templateID", "int", $templateID);
					 
					
					/*******************************************
					Closing Java Netbean
					/*******************************************/
					$newxml = $newxml . autoIndentXML(1, "") . "</object>\n";
					$newxml = $newxml . "</java>\n";
			
					//echo $newxml . "<br>";
					
					/*******************************************
					Update the XML in the OBJStore TYPE ITEM
					/*******************************************/
					if ($itemIsToBeDeleted && ITEMTOBEDELETED) //Delete Items if true
					{
						// Delete items
					}else{
						$mynewxml = simplexml_load_string($newxml);
						$query_atavism_objstore_tables_update  = "UPDATE objstore SET data='" . $mynewxml->asXML() . "', name='" . $templateID_name . "' WHERE obj_id='$objstore_obj_id' AND namespace_int='$objstore_namespace_int'";
							
							if (mysql_query($query_atavism_objstore_tables_update, $conndb_atavism2)){
								$UpdateLog = $UpdateLog . "------   Item named " .  $templateID_name . " with obj_id " . $objstore_obj_id . " and " . $objstore_namespace_int . " was updated (TYPE: ITEM)\n";
							}else{
								$UpdateLog = $UpdateLog . "Error updating record: " . $conndb_atavism2 . "\n";
							}	
					}
			
					// Search for the Type unknown associated with ITEM
					$query_atavism_objstore_tables_unknown  = "SELECT * FROM objstore WHERE obj_id='$objstore_obj_id' AND type='unknown'";
					$result_objstore_tables_unknown = mysql_query($query_atavism_objstore_tables_unknown, $conndb_atavism);
			
					/*******************************************
					Update the XML in the OBJStore TYPE unknown
					/*******************************************/
					if ($itemIsToBeDeleted && ITEMTOBEDELETED) //Delete Items if true
					{
						// Delete items
					}else{
						while($row_atavism_objstore_tables_unknown = mysql_fetch_array($result_objstore_tables_unknown, MYSQL_ASSOC))
						{
							$objstore_BLOB_unknown = $row_atavism_objstore_tables_unknown['data'];
							$objstore_obj_id_unknown = $row_atavism_objstore_tables_unknown['obj_id'];
							$objstore_namespace_int_unknown = $row_atavism_objstore_tables_unknown['namespace_int'];
							$xml = simplexml_load_string($objstore_BLOB_unknown);	
							$subObjectNamespacesInt = findValueInXMLParent("void", "property", "subObjectNamespacesInt", $xml, "itsaint");
							
							//echo $subObjectNamespacesInt . "<br>";
							
							/*******************************************
							Creating the XML in the OBJStore TYPE unknown
							/*******************************************/
							$newxml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
							$newxml = $newxml . "<java version=\"1.7.0_76\" class=\"java.beans.XMLDecoder\">\n";
							$newxml = $newxml . " <object class=\"atavism.server.plugins.ObjectManagerPlugin\$MasterObject\" id=\"AgisItem0\">\n";
							
							$newxml = $newxml . createXMLParts("void", "property", "name", "string", $templateID_name);
							$newxml = $newxml . createXMLParts("void|object|void", "property|class|property", "oid|atavism.server.engine.OID|data", "long", $OIDReference);	
							$newxml = $newxml . createXMLParts("void", "property", "persistenceFlag", "boolean", "true");
							$newxml = $newxml . createXMLParts("void", "property", "subObjectNamespacesInt", "int", $subObjectNamespacesInt);
							
							/*******************************************
							Closing Java Netbean OBJStore TYPE unknown
							/*******************************************/
							$newxml = $newxml . " </object>\n";
							$newxml = $newxml . "</java>\n";
			
							$mynewxml = simplexml_load_string($newxml);				
							$query_atavism_objstore_tables_update_unknown  = "UPDATE objstore SET data='" . $mynewxml->asXML() . "', name='" . $templateID_baseName . "' WHERE obj_id='$objstore_obj_id_unknown' AND namespace_int='$objstore_namespace_int_unknown'";
							
							if (mysql_query($query_atavism_objstore_tables_update_unknown, $conndb_atavism2)) {
								$UpdateLog = $UpdateLog . "------   Item named " .  $templateID_name . " with obj_id " . $objstore_obj_id_unknown . " and " . $objstore_namespace_int_unknown . " was updated (TYPE: unknown)\n";
							} else {
								$UpdateLog = $UpdateLog . "Error updating record: " . $conndb_atavism2 . "\n";
							}
						
						}
					}
				}else{
					$UpdateLog = $UpdateLog . "------   COULD NOT BACKDATE ITEM\n";
				}
			}else{
				$UpdateLog = $UpdateLog . "Item#$templateID Does not need updating, skipping\n";
			}
		}
	}
			
		$msc = microtime(true)-$msc;
		
		date_default_timezone_get();
		
		$UpdateLog = $UpdateLog . "Log Items Update: " . date("m/d/Y h:i:s a", time());
		$FileName = ".\logs\Items_" . date("m-d-Y-h_i_s_a", time()) . ".log";
		
		createFileLog($FileName, $UpdateLog);
		
		if($globalbackdatingerror == false){
			echo "Items in the objstore have been updated in $msc seconds for $loopnumber, check the log <a href=\"$FileName\" target=\"_blank\">here</a><br>";
			echo "$globalNumberofBackdatingerrors items have not been updated<br>";
			$UpdateLog = $UpdateLog . "Items in the objstore have been updated in $msc seconds for $loopnumber\n";
			echo "They are $globalDestroyedItems obsolete items in the DB<br>";
			$UpdateLog = $UpdateLog . "They are $globalDestroyedItems obsolete items in the DB\n";
			echo "Update succesfull<br><br>";
			$UpdateLog = $UpdateLog . "Update succesfull"; 
		}else{
			echo "Items in the objstore HAVE NOT ALL BEEN updated in $msc seconds for $loopnumber, check the log <a href=\"$FileName\" target=\"_blank\">here</a><br>";
			echo "$globalNumberofBackdatingerrors items have not been updated<br>";
			$UpdateLog = $UpdateLog . "Items in the objstore HAVE NOT ALL BEEN updated in $msc seconds for $loopnumber\n";
			$UpdateLog = $UpdateLog . "$globalNumberofBackdatingerrors of items have not been updated\n";
			echo "They are $globalDestroyedItems obsolete items in the DB<br>";
			$UpdateLog = $UpdateLog . "They are $globalDestroyedItems obsolete items in the DB\n";
			echo "Update UNSUCCESFULL, check the log<br><br>";
			$UpdateLog = $UpdateLog . "Update UNSUCCESFULL";
		}
	
	$UpdateLog = $UpdateLog . "Log Items Update: " . date("m/d/Y h:i:s a", time());
	$FileName = ".\logs\Items_" . date("m-d-Y-h_i_s_a", time()) . ".log";
	
	createFileLog($FileName, $UpdateLog);
	
	// Update backdating Table with current timestamp
	$query_atavism_backdating_tables_update  = "UPDATE backdating_tables SET LastUpdate=now() WHERE DBName='world_content' AND TableName='item_templates'";
	$result_backdating_tables_updates = mysql_query($query_atavism_backdating_tables_update, $conndb_atavism);
	 
	 if($result_backdating_tables_updates){
	 }else{
		echo "Error updating record: " . $conndb_atavism->error;
	 }
}else{
	header("location: index.php"); // Redirecting To Other Page	
}
?>