-- MariaDB dump 10.19  Distrib 10.4.32-MariaDB, for Win64 (AMD64)
--
-- Host: localhost    Database: student
-- ------------------------------------------------------
-- Server version	10.4.32-MariaDB

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Current Database: `student`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `student` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci */;

USE `student`;

--
-- Table structure for table `accounts`
--

DROP TABLE IF EXISTS `accounts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `accounts` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `email` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  `role` enum('USER','ADMIN','SUPER_ADMIN') DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_22fvt0prnafepjuqjhs2da68e` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=43 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `accounts`
--

LOCK TABLES `accounts` WRITE;
/*!40000 ALTER TABLE `accounts` DISABLE KEYS */;
INSERT INTO `accounts` VALUES (17,'admin@email.com','$2a$10$njpKJtMD9PWB4cKNjwJbXOwu6tIcuP7hXi18YkZJ40TzaKHkBXCM.','administrator','ADMIN'),(30,'minitest@email.com','$2a$10$Tboh2aNkUvL2hEE0x6TTJO0T2/KckHsQVhwNEleBLZ0R/sBl.Ir/u','mini','ADMIN'),(33,'medor@email.com','$2a$10$zRbTkGGQgDddV2l3.71wDeu31cg/hpnatc2Swc0uXPXV2IrfOscNS','medort','USER'),(35,'admin@email.com','$2a$10$cRykw09bPVzQY9X8J/OpCeok9m0.7XGhoAHg1CrCl/y5J6mqaQDbG','admin','SUPER_ADMIN'),(37,'live@gmail.com','$2a$10$ojLRaxGQ.RUcRdVMUnZL..cNp/2LFwBDyffFir0XBA3GGL6RGAWY.','live','USER'),(39,'test@gmail.com','$2a$10$Bc.6G0SruxPV2TVkjN06n.8qQjlr0qKI/nyBmYBZFEJ.C5nHtMOji','test','USER'),(40,'account@email.com','$2a$10$afSiAowfNF92fiXJW1gPKue298P9Re.4ouCZTAOiFvOGa.NuKdnyq','newaccount','USER'),(41,'account@email.com','$2a$10$um6sFZuvotRRJLNyaS9CPe//qTK0QPpwuzwcmS/njvLhbWwrI0hd.','another','USER');
/*!40000 ALTER TABLE `accounts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `students`
--

DROP TABLE IF EXISTS `students`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `students` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `email` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `phone_number` varchar(255) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `validated` bit(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=138 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `students`
--

LOCK TABLES `students` WRITE;
/*!40000 ALTER TABLE `students` DISABLE KEYS */;
INSERT INTO `students` VALUES (99,'frédéric.renard@email.com','Frédéric RENARD','0628007190','9 PAS DE SOUCI',''),(104,'jeandumas@email.com','Jean DUMAS','0687469638','24 RUE SALOMON DE CAUS','\0'),(107,'alainboucher@email.com','Alain BOUCHER','0677677035','4 RUE COMMINES',''),(114,'mathias@email.com','Mathias Desplan','0648777048','22 rue de la république','\0'),(115,'simon.moucadel@email.com','Simon Moucadel','0789458945','12 rue de l\'oie','\0'),(116,'panard@email.com','Frédéric Panard','0648778048','24 rue de la république',''),(118,'davidpetit@email.com','David PETIT','0646341253','3 RUE PAPIN','\0'),(119,'jacquelinegerard@email.com','Jacqueline GERARD','0627837530','5 RUE PAUL DUBOIS',''),(120,'pierreantoine@email.com','Pierre ANTOINE','0696459360','7 RUE DES ARCHIVES','\0'),(121,'nathalieguichard@email.com','Nathalie GUICHARD','0677920950','19 CHEZ SAMUEL',''),(122,'marieperrin@email.com','Marie PERRIN','0654413280','24 PL THEODOR HERZL',''),(123,'véroniqueroux@email.com','Véronique ROUX','0672296150','7 RUE DES QUATRE FILS',''),(124,'jacquelineherve@email.com','Jacqueline HERVE','0681843780','19 RUE MONTGOLFIER','\0'),(125,'samuel.email@com','Samuel Bellaton','0647859615','12 rue de la république',''),(126,'jeremy@email.com','Jeremy Gallo','0649758462','23 avenue foch',''),(127,'fabien@email.com','Fabio','0215487946','8 rue du temple',''),(128,'geoffrey@email.com','Geoffrey Delahaye','0659847596','5 rue ortega',''),(129,'nazebil@email.com','Nabil Aghezzaf','0234586461','9 rue du bas peuple','\0'),(130,'anne.breton@email.com','Anne BRETON','0670461005','15 CAR DES THEATRES','\0'),(131,'thomas.poulain@email.com','Thomas POULAIN','0740045294','22 MAR DU TEMPLE',''),(132,'chantal.rolland@email.com','Chantal ROLLAND','0600154394','25 RUE DES ARCHIVES','\0'),(133,'nicole.carpentier@email.com','Nicole CARPENTIER','0743909831','22 RUE VAUCANSON',''),(134,'catherine.david@email.com','Catherine DAVID','0604531123','18 RUE VIEILLE DU TEMPLE',''),(135,'martine.fabre@email.com','Martine FABRE','0648654218','24 RUE DES FRANCS BOURGEOIS',''),(136,'chantal.chevalier@email.com','Chantal CHEVALIER','0739203215','4 RUE DU FOIN','\0'),(137,'chantal.perrier@email.com','Chantal PERRIER','0669771971','29 RUE MESLAY','');
/*!40000 ALTER TABLE `students` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-06-17 10:38:05
