# Creating initial database schema

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';


-- -----------------------------------------------------
-- Table `contacts`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `contacts` (
  `contact_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT ,
  `type` ENUM('person', 'organization') NOT NULL ,
  `num_requests` INT NOT NULL ,
  `created` DATETIME NOT NULL ,
  PRIMARY KEY (`contact_id`, `created`) )
  ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `task_priorities`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `task_priorities` (
  `priority_id` TINYINT UNSIGNED NOT NULL ,
  `priority_name` VARCHAR(200) NOT NULL ,
  `icon` VARCHAR(500) NULL ,
  PRIMARY KEY (`priority_id`) )
  ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `task_categories`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `task_categories` (
  `category_id` TINYINT UNSIGNED NOT NULL ,
  `category_name` VARCHAR(200) NOT NULL ,
  `icon` VARCHAR(500) NULL ,
  PRIMARY KEY (`category_id`) )
  ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `task_statuses`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `task_statuses` (
  `status_id` TINYINT UNSIGNED NOT NULL ,
  `status_name` VARCHAR(200) NOT NULL ,
  `icon` VARCHAR(500) NULL ,
  PRIMARY KEY (`status_id`) )
  ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `tasks`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `tasks` (
  `task_id` BIGINT NOT NULL AUTO_INCREMENT ,
  `start_date` DATETIME NOT NULL ,
  `end_date` DATETIME NULL ,
  `description` VARCHAR(5000) NOT NULL ,
  `category` TINYINT UNSIGNED NOT NULL ,
  `priority` TINYINT UNSIGNED NOT NULL ,
  `status` TINYINT UNSIGNED NOT NULL ,
  `paid_amount` INT UNSIGNED NOT NULL ,
  `duration` FLOAT NULL ,
  `created` DATETIME NOT NULL ,
  PRIMARY KEY (`task_id`) ,
  INDEX `fk_tasks_task_priorities1_idx` (`priority` ASC) ,
  INDEX `fk_tasks_task_categories1_idx` (`category` ASC) ,
  INDEX `fk_tasks_task_statuses1_idx` (`status` ASC) ,
  CONSTRAINT `fk_tasks_task_priorities1`
  FOREIGN KEY (`priority` )
  REFERENCES `task_priorities` (`priority_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_tasks_task_categories1`
  FOREIGN KEY (`category` )
  REFERENCES `task_categories` (`category_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_tasks_task_statuses1`
  FOREIGN KEY (`status` )
  REFERENCES `task_statuses` (`status_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
  ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `requests`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `requests` (
  `request_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT ,
  `client_id` BIGINT UNSIGNED NOT NULL ,
  `executor_id` BIGINT UNSIGNED NOT NULL ,
  `task_id` BIGINT NOT NULL ,
  PRIMARY KEY (`request_id`) ,
  INDEX `fk_requests_contacts_idx` (`client_id` ASC) ,
  INDEX `fk_requests_contacts1_idx` (`executor_id` ASC) ,
  INDEX `fk_requests_tasks1_idx` (`task_id` ASC) ,
  CONSTRAINT `fk_requests_contacts`
  FOREIGN KEY (`client_id` )
  REFERENCES `contacts` (`contact_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_requests_contacts1`
  FOREIGN KEY (`executor_id` )
  REFERENCES `contacts` (`contact_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_requests_tasks1`
  FOREIGN KEY (`task_id` )
  REFERENCES `tasks` (`task_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
  ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `persons`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `persons` (
  `contact_id` BIGINT UNSIGNED NOT NULL ,
  `first_name` VARCHAR(200) NOT NULL ,
  `middle_name` VARCHAR(200) NULL ,
  `last_name` VARCHAR(200) NULL ,
  `position` VARCHAR(200) NULL ,
  `cell_phone` BIGINT UNSIGNED NOT NULL ,
  `fixed_phone` BIGINT UNSIGNED NULL ,
  `passport` VARCHAR(1000) NULL ,
  `address` VARCHAR(1000) NOT NULL ,
  `district` VARCHAR(200) NULL ,
  `age` TINYINT UNSIGNED NULL ,
  `profession` VARCHAR(200) NULL ,
  `average_payment` INT NULL ,
  `misc` VARCHAR(5000) NULL ,
  PRIMARY KEY (`contact_id`) ,
  CONSTRAINT `fk_persons_contacts1`
  FOREIGN KEY (`contact_id` )
  REFERENCES `contacts` (`contact_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
  ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `organizations`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `organizations` (
  `contact_id` BIGINT UNSIGNED NOT NULL ,
  `title` VARCHAR(500) NOT NULL ,
  `ceo_name` VARCHAR(200) NULL ,
  `phone` VARCHAR(50) NOT NULL ,
  `fax` VARCHAR(50) NULL ,
  `website` VARCHAR(200) NULL ,
  `email` VARCHAR(200) NULL ,
  `registration_address` VARCHAR(1000) NOT NULL ,
  `postal_address` VARCHAR(1000) NULL ,
  `ogrn` BIGINT UNSIGNED NULL ,
  `inn` BIGINT UNSIGNED NOT NULL ,
  `kpp` INT UNSIGNED NOT NULL ,
  `bank_name` VARCHAR(200) NOT NULL ,
  `bik` INT UNSIGNED NOT NULL ,
  `correspondent_account` BIGINT UNSIGNED NOT NULL ,
  `settlement_account` BIGINT UNSIGNED NOT NULL ,
  `misc` VARCHAR(5000) NULL ,
  PRIMARY KEY (`contact_id`) ,
  CONSTRAINT `fk_organizations_contacts1`
  FOREIGN KEY (`contact_id` )
  REFERENCES `contacts` (`contact_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
  ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `task_templates`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `task_templates` (
  `template_id` BIGINT NOT NULL AUTO_INCREMENT ,
  `template_name` VARCHAR(200) NOT NULL ,
  `description` VARCHAR(5000) NOT NULL ,
  `category` TINYINT UNSIGNED NOT NULL ,
  `priority` TINYINT UNSIGNED NOT NULL ,
  `status` TINYINT UNSIGNED NOT NULL ,
  PRIMARY KEY (`template_id`) ,
  INDEX `fk_task_templates_task_priorities1_idx` (`priority` ASC) ,
  INDEX `fk_task_templates_task_categories1_idx` (`category` ASC) ,
  INDEX `fk_task_templates_task_statuses1_idx` (`status` ASC) ,
  CONSTRAINT `fk_task_templates_task_priorities1`
  FOREIGN KEY (`priority` )
  REFERENCES `task_priorities` (`priority_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_task_templates_task_categories1`
  FOREIGN KEY (`category` )
  REFERENCES `task_categories` (`category_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_task_templates_task_statuses1`
  FOREIGN KEY (`status` )
  REFERENCES `task_statuses` (`status_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
  ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `users`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `users` (
  `user_id` INT UNSIGNED NOT NULL AUTO_INCREMENT ,
  `email` VARCHAR(100) NOT NULL ,
  `password` CHAR(60) NOT NULL ,
  `contact_id` BIGINT UNSIGNED NULL ,
  PRIMARY KEY (`user_id`) ,
  UNIQUE INDEX `email_UNIQUE` (`email` ASC) ,
  INDEX `fk_users_contacts1_idx` (`contact_id` ASC) ,
  CONSTRAINT `fk_users_contacts1`
  FOREIGN KEY (`contact_id` )
  REFERENCES `contacts` (`contact_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
  ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `roles`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `roles` (
  `role_id` TINYINT UNSIGNED NOT NULL ,
  `role_name` VARCHAR(20) NOT NULL ,
  `display_name` VARCHAR(100) NULL ,
  PRIMARY KEY (`role_id`) )
  ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `user_roles`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `user_roles` (
  `role_id` TINYINT UNSIGNED NOT NULL ,
  `user_id` INT UNSIGNED NOT NULL ,
  PRIMARY KEY (`role_id`, `user_id`) ,
  INDEX `fk_table1_has_users_users1_idx` (`user_id` ASC) ,
  INDEX `fk_table1_has_users_table11_idx` (`role_id` ASC) ,
  CONSTRAINT `fk_table1_has_users_table11`
  FOREIGN KEY (`role_id` )
  REFERENCES `roles` (`role_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_table1_has_users_users1`
  FOREIGN KEY (`user_id` )
  REFERENCES `users` (`user_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
  ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `service_groups`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `service_groups` (
  `group_id` TINYINT UNSIGNED NOT NULL AUTO_INCREMENT ,
  `group_name` VARCHAR(200) NOT NULL ,
  `sequence` TINYINT NOT NULL ,
  PRIMARY KEY (`group_id`) )
  ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `service`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `service` (
  `service_id` BIGINT NOT NULL AUTO_INCREMENT ,
  `service_name` VARCHAR(500) NOT NULL ,
  `unit` VARCHAR(600) NOT NULL ,
  `price` FLOAT NOT NULL ,
  `group_id` TINYINT UNSIGNED NULL ,
  PRIMARY KEY (`service_id`) ,
  INDEX `fk_service_service_groups1_idx` (`group_id` ASC) ,
  CONSTRAINT `fk_service_service_groups1`
  FOREIGN KEY (`group_id` )
  REFERENCES `service_groups` (`group_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
  ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `task_services`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `task_services` (
  `task_id` BIGINT NOT NULL ,
  `service_id` BIGINT NOT NULL ,
  `quantity` FLOAT NOT NULL ,
  INDEX `fk_tasks_has_service_service1_idx` (`service_id` ASC) ,
  INDEX `fk_tasks_has_service_tasks1_idx` (`task_id` ASC) ,
  INDEX `search_index` (`task_id` ASC, `service_id` ASC) ,
  CONSTRAINT `fk_tasks_has_service_tasks1`
  FOREIGN KEY (`task_id` )
  REFERENCES `tasks` (`task_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_tasks_has_service_service1`
  FOREIGN KEY (`service_id` )
  REFERENCES `service` (`service_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
  ENGINE = InnoDB;



SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
