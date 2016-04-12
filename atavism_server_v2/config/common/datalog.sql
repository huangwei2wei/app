DROP TABLE IF EXISTS `data_logs`;
CREATE TABLE IF NOT EXISTS `data_logs` (
       `id` BIGINT NOT NULL AUTO_INCREMENT,
       `world_name` VARCHAR(64) NOT NULL,
       `data_name` VARCHAR(64) NOT NULL,
       `data_timestamp` TIMESTAMP NOT NULL DEFAULT 0,
       `source_oid` BIGINT NOT NULL,
       `target_oid` BIGINT NOT NULL DEFAULT 0,
       `amount` INT NOT NULL DEFAULT 0,
       `additional_data` TEXT,
       `process_timestamp` TIMESTAMP DEFAULT NOW(),
       PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
