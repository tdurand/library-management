# --- !Ups

CREATE TABLE `Book` (
  `id` INTEGER NOT NULL,
  `idLibrary` INTEGER NOT NULL,
  `title` VARCHAR(255) NOT NULL,
  `isbn` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE `PhysicalBook` (
  `id` INTEGER NOT NULL,
  `idBook` INTEGER NOT NULL,
  PRIMARY KEY (`id`)
);

        
CREATE TABLE `Loan` (
  `id` INTEGER NOT NULL,
  `idUser` INTEGER NOT NULL,
  `idPhysicalBook` INTEGER NOT NULL,
  `dateBorrowed` INTEGER NOT NULL,
  `dateDue` INTEGER NOT NULL,
  `dateReturned` INTEGER NOT NULL,
  PRIMARY KEY (`id`)
);
        
CREATE TABLE `User` (
  `id` INTEGER NOT NULL,
  `idLibrary` INTEGER NOT NULL,
  `firstName` VARCHAR(255) NOT NULL,
  `password` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`)
);

        
CREATE TABLE `Library` (
  `id` INTEGER NOT NULL,
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

CREATE SEQUENCE book_seq start with 1;
CREATE SEQUENCE physicalbook_seq start with 1;
CREATE SEQUENCE loan_seq start with 1;
CREATE SEQUENCE user_seq start with 1;
CREATE SEQUENCE library_seq start with 1;

INSERT INTO `Library` VALUES (nextval('library_seq'),'Default');

# --- !Downs

DROP TABLE IF EXISTS `Book`;
DROP TABLE IF EXISTS `PhysicalBook`;
DROP TABLE IF EXISTS `Loan`;
DROP TABLE IF EXISTS `User`;
DROP TABLE IF EXISTS `Library`;

DROP SEQUENCE IF EXISTS book_seq;
DROP SEQUENCE IF EXISTS physicalbook_seq;
DROP SEQUENCE IF EXISTS loan_seq;
DROP SEQUENCE IF EXISTS user_seq;
DROP SEQUENCE IF EXISTS library_seq;

