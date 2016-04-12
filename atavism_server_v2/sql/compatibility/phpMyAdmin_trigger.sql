CREATE TRIGGER `updatetimestamp` BEFORE UPDATE ON `item_templates` FOR EACH ROW BEGIN
SET NEW.updatetimestamp = CURRENT_TIMESTAMP();
END $