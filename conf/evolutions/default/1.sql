# --- !Ups

CREATE TABLE `Book` (
  `id` INTEGER NOT NULL AUTO_INCREMENT,
  `idLibrary` INTEGER NOT NULL,
  `title` VARCHAR(255) NOT NULL,
  `isbn` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE `PhysicalBook` (
  `id` INTEGER NOT NULL AUTO_INCREMENT,
  `idBook` INTEGER NOT NULL,
  PRIMARY KEY (`id`)
);

        
CREATE TABLE `Loan` (
  `id` INTEGER NOT NULL AUTO_INCREMENT,
  `idUser` INTEGER NOT NULL,
  `idPhysicalBook` INTEGER NOT NULL,
  `dateBorrowed` INTEGER NOT NULL,
  `dateDue` INTEGER NOT NULL,
  `dateReturned` INTEGER NOT NULL,
  PRIMARY KEY (`id`)
);
        
CREATE TABLE `User` (
  `id` INTEGER NOT NULL AUTO_INCREMENT,
  `idLibrary` INTEGER NOT NULL,
  `firstName` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`)
);

        
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
ALTER TABLE `Loan` ADD FOREIGN KEY (idUser) REFERENCES `User` (`id`);
ALTER TABLE `Loan` ADD FOREIGN KEY (idPhysicalBook) REFERENCES `PhysicalBook` (`id`);
ALTER TABLE `User` ADD FOREIGN KEY (idLibrary) REFERENCES `Library` (`id`);

# --- !Downs

DROP TABLE IF EXISTS `Book`;
DROP TABLE IF EXISTS `PhysicalBook`;
DROP TABLE IF EXISTS `Loan`;
DROP TABLE IF EXISTS `User`;
DROP TABLE IF EXISTS `Library`;

