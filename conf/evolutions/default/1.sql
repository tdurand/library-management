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
  `dateBorrowed` timestamp NOT NULL,
  `dateDue` timestamp NOT NULL,
  `dateReturned` timestamp,
  PRIMARY KEY (`id`)
);
        
CREATE TABLE `User` (
  `id` INTEGER NOT NULL,
  `idLibrary` INTEGER NOT NULL,
  `login` VARCHAR(255) NOT NULL,
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

-- ---
-- Test Data
-- ---

INSERT INTO `Book` (`id`,`idLibrary`,`title`,`isbn`) VALUES (nextval('book_seq'),'1','Millénium 1','2929ANDJS');
INSERT INTO `Book` (`id`,`idLibrary`,`title`,`isbn`) VALUES (nextval('book_seq'),'1','Millénium 2','928AJZNSN');
INSERT INTO `Book` (`id`,`idLibrary`,`title`,`isbn`) VALUES (nextval('book_seq'),'1','Millénium 3','2882ANSBN');

INSERT INTO `PhysicalBook` (`id`,`idBook`) VALUES (nextval('physicalbook_seq'),'1');
INSERT INTO `PhysicalBook` (`id`,`idBook`) VALUES (nextval('physicalbook_seq'),'1');

INSERT INTO `User` (`id`,`idLibrary`,`login`,`password`) VALUES (nextval('user_seq'),'1','tdurand','test');
INSERT INTO `User` (`id`,`idLibrary`,`login`,`password`) VALUES (nextval('user_seq'),'1','jpedro','test');

INSERT INTO `Loan` (`id`,`idUser`,`idPhysicalBook`,`dateBorrowed`,`dateDue`,`dateReturned`) VALUES (nextval('loan_seq'),'1','1','2001-02-16 20:14:12','2001-02-16 20:14:12',NULL);
INSERT INTO `Loan` (`id`,`idUser`,`idPhysicalBook`,`dateBorrowed`,`dateDue`,`dateReturned`) VALUES (nextval('loan_seq'),'1','2','2001-02-16 20:14:12','2001-02-16 20:14:12',NULL);

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



