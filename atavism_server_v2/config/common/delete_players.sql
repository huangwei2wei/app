delete from objstore where obj_id in (select obj_id from player_character);
delete from player_character;
