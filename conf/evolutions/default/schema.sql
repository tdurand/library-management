



-- ---
-- Globals
-- ---

-- SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";
-- SET FOREIGN_KEY_CHECKS=0;

-- ---
-- Table 'Book'
-- 
-- ---

DROP TABLE IF EXISTS `Book`;
        
CREATE TABLE `Book` (
  `id` INTEGER NOT NULL AUTO_INCREMENT,
  `idLibrary` INTEGER NOT NULL,
  `title` VARCHAR(255) NOT NULL,
  `isbn` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`)
);

-- ---
-- Table 'PhysicalBook'
-- 
-- ---

DROP TABLE IF EXISTS `PhysicalBook`;
        
CREATE TABLE `PhysicalBook` (
  `id` INTEGER NOT NULL AUTO_INCREMENT,
  `idBook` INTEGER NOT NULL,
  `idLoan` INTEGER NOT NULL,
  PRIMARY KEY (`id`)
);

-- ---
-- Table 'Loan'
-- 
-- ---

DROP TABLE IF EXISTS `Loan`;
        
CREATE TABLE `Loan` (
  `id` INTEGER NOT NULL AUTO_INCREMENT,
  `idUser` INTEGER NOT NULL,
  PRIMARY KEY (`id`)
);

-- ---
-- Table 'User'
-- 
-- ---

DROP TABLE IF EXISTS `User`;
        
CREATE TABLE `User` (
  `id` INTEGER NOT NULL AUTO_INCREMENT,
  `idLibrary` INTEGER NOT NULL,
  `firstName` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`)
);

-- ---
-- Table 'Library'
-- 
-- ---

DROP TABLE IF EXISTS `Library`;
        
CREATE TABLE `Library` (
  `id` INTEGER NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`)
);

-- ---
-- Foreign Keys 
-- ---

ALTER TABLE `Book` ADD FOREIGN KEY (idLibrary) REFERENCES `Library` (`id`);
ALTER TABLE `PhysicalBook` ADD FOREIGN KEY (idBook) REFERENCES `Book` (`id`);
ALTER TABLE `PhysicalBook` ADD FOREIGN KEY (idLoan) REFERENCES `Loan` (`id`);
ALTER TABLE `Loan` ADD FOREIGN KEY (idUser) REFERENCES `User` (`id`);
ALTER TABLE `User` ADD FOREIGN KEY (idLibrary) REFERENCES `Library` (`id`);

-- ---
-- Table Properties
-- ---

-- ALTER TABLE `Book` ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
-- ALTER TABLE `PhysicalBook` ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
-- ALTER TABLE `Loan` ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
-- ALTER TABLE `User` ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
-- ALTER TABLE `Library` ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ---
-- Test Data
-- ---

-- INSERT INTO `Book` (`id`,`idLibrary`,`title`,`isbn`) VALUES
-- ('','','','');
-- INSERT INTO `PhysicalBook` (`id`,`idBook`,`idLoan`) VALUES
-- ('','','');
-- INSERT INTO `Loan` (`id`,`idUser`) VALUES
-- ('','');
-- INSERT INTO `User` (`id`,`idLibrary`,`firstName`) VALUES
-- ('','','');
-- INSERT INTO `Library` (`id`,`name`) VALUES
-- ('','');

