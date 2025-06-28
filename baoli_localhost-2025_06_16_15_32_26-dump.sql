-- MySQL dump 10.13  Distrib 9.0.1, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: baoli
-- ------------------------------------------------------
-- Server version	9.0.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `material`
--

DROP TABLE IF EXISTS `material`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `material` (
  `material_category` varchar(255) DEFAULT NULL COMMENT '材料品类',
  `material_name` varchar(255) DEFAULT NULL COMMENT '材料名称',
  `photo_daban` text COMMENT '大板图片',
  `photo_chengpin` text COMMENT '成品图片',
  `photo_xiaoguo` text COMMENT '施工效果图',
  `price` double DEFAULT NULL,
  `id` int NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `material`
--

LOCK TABLES `material` WRITE;
/*!40000 ALTER TABLE `material` DISABLE KEYS */;
INSERT INTO `material` VALUES ('木饰面','斜纹枫影木皮-人字拼','http://47.115.47.145:9000/baoliphotos/materials/9a364ffd-df75-4d93-8900-49c85a31f623.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=minioadmin%2F20250616%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20250616T071526Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=295c627fd180ca6f4b281d9392c371ade544dab9c8ba3b614dcc25d697d8db0a','http://47.115.47.145:9000/baoliphotos/materials/e6e38ae0-ba2b-4c8b-8250-a9aa128b118a.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=minioadmin%2F20250616%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20250616T071527Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=61f8c0405d4c3865f50f1db3152d64ce06e6f9198b50f6419d1f5413f5bfacfc','http://47.115.47.145:9000/baoliphotos/materials/a670e29c-caf9-4d10-ae2c-def360408f5d.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=minioadmin%2F20250616%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20250616T071528Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=863788ffd976212a0068e7b682403e34d2c344a804abd9c9a4329da0ca49b791',546.15,1),('木饰面','斜纹枫影木皮-直拼','http://47.115.47.145:9000/baoliphotos/materials/56cb9dbb-cbfd-40d7-b321-504bb72d33b0.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=minioadmin%2F20250616%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20250616T071529Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=24814835e39e26594ec8bc47cda8935c224052255e5145e982f06156746a0207','http://47.115.47.145:9000/baoliphotos/materials/f980edaa-034f-4012-9911-d4e7577b0ea7.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=minioadmin%2F20250616%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20250616T071529Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=71b541b068b53845a31d5c083bb2fe7bd45b5015685b446561514e9934f8aec5',NULL,502.68,2),('木饰面','法国尼斯-直拼','http://47.115.47.145:9000/baoliphotos/materials/a5f5f0b7-ce50-4a36-92d2-4d49f817281b.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=minioadmin%2F20250616%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20250616T071529Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=84b596eb0c8c55963fe1ff1c0d214ddef8fc6b67cfd51c2df01e3ebd170a11ed','http://47.115.47.145:9000/baoliphotos/materials/7b668f2d-ee92-455d-8ffb-0e3e4eae2274.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=minioadmin%2F20250616%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20250616T071530Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=101c6857c429daf0a2c2b24ea28c7af21ca3c99ecb874bfe3d24adea34be18c3','http://47.115.47.145:9000/baoliphotos/materials/d4e922b2-8c6e-411a-8c61-44f6c405a1f3.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=minioadmin%2F20250616%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20250616T071530Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=bda4fb321158d732d5a59331d5e5bd1a61c3e4071fc174a3900e09f52c080816',450.51,3),('木饰面','白橡直纹-直拼','http://47.115.47.145:9000/baoliphotos/materials/fffb7f14-03e0-400a-8342-31f377e3ed48.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=minioadmin%2F20250616%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20250616T071531Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=3e3099e07e4bdd35d1709974a15c98296f66692308d26a90eef3ed9bd4259e66',NULL,NULL,398.35,4),('木饰面','白橡直纹精品-直拼','http://47.115.47.145:9000/baoliphotos/materials/056a3760-56e8-4773-a498-5e74cef002e1.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=minioadmin%2F20250616%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20250616T071532Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=dccdddadc10a096c0318be9f6271e690078d2c04756f1cdbe6189b54df3f597b','http://47.115.47.145:9000/baoliphotos/materials/acd2b71d-0c0c-4ffe-ba98-6b6f345a8946.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=minioadmin%2F20250616%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20250616T071533Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=b948ce222e622ebde9d7e5bb6b9309ad68cdfd161e2c4ef4a934aba9c58438fd',NULL,437.47,5),('木饰面','白橡乱拼-直拼','http://47.115.47.145:9000/baoliphotos/materials/f60c2ecb-7729-4d64-9026-0efb400af127.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=minioadmin%2F20250616%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20250616T071546Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=248714099405520e4faeedfa239912c20dd73d466efd760b7da326ac7c005d49',NULL,NULL,398.35,6),('木饰面','非洲胡桃-直拼','http://47.115.47.145:9000/baoliphotos/materials/76383f84-2989-4547-89c5-fb64cd7628cd.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=minioadmin%2F20250616%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20250616T071547Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=9f3ac0665e776c10d4142da7e38cfaafe4074dfee438175a826d0965d39ba633',NULL,NULL,372.27,7),('木饰面','北美黑胡桃-直拼','http://47.115.47.145:9000/baoliphotos/materials/74473f5c-57f5-4524-a68f-dd56e1aed593.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=minioadmin%2F20250616%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20250616T071549Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=180cfe2e0429d8f4f4fc94d074631e6655e1ad96e488ae70dfac6f9a515d2b0a',NULL,NULL,437.47,8),('木饰面','北美黑胡桃直纹-直拼','http://47.115.47.145:9000/baoliphotos/materials/81ab9a3e-c664-4905-9e45-c662763c7d91.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=minioadmin%2F20250616%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20250616T071550Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=ea6ce71f6b7a42cac1104212c0b99404a2b52ad61060f8e955c39f4253ae7cbc','http://47.115.47.145:9000/baoliphotos/materials/b0a2e5de-759e-45da-8323-65bb5fe49511.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=minioadmin%2F20250616%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20250616T071551Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=571b86e9e29054d47039aa450c3d7ad729a3eecfbca8f5b2933f833e25f8be2a',NULL,567.88,9),('木饰面','直纹黑胡桃乱拼-直拼','http://47.115.47.145:9000/baoliphotos/materials/2b09981c-45d4-4dd6-9c81-24d524e6f4b9.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=minioadmin%2F20250616%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20250616T071554Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=067a4a4f937df50c3418aa2a9426f81d537708d3333dd64d960c7c39b0f96fa6',NULL,NULL,424.43,10),('木饰面','黑鸡翅木皮-直拼','http://47.115.47.145:9000/baoliphotos/materials/ef169c6e-4896-4789-94c8-6295ee6a6656.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=minioadmin%2F20250616%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20250616T071556Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=3b306564f6deacf95bafee93cac044046017479760a66720de5a03155d88bfb1',NULL,NULL,398.35,11),('木饰面','红木影木-直拼','http://47.115.47.145:9000/baoliphotos/materials/033616ef-7d34-42f4-ace2-e1089d8e1c5c.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=minioadmin%2F20250616%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20250616T071558Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=69c9cb379f49357b1638351d8da1dcae6089713589a0e659ac413b64aeef90a1',NULL,NULL,385.31,12),('木饰面','黄铁刀-直拼','http://47.115.47.145:9000/baoliphotos/materials/facfd99a-32c7-4bcc-9780-ad04332e07f5.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=minioadmin%2F20250616%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20250616T071559Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=a56ae9810786746725696b9e50c10c8870a6ea419a5d3a4fd512a6344ecc6ba1',NULL,NULL,372.27,13),('木饰面','金影木皮-直拼','http://47.115.47.145:9000/baoliphotos/materials/b22c9c82-bf95-43ce-ad11-1a3a28485576.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=minioadmin%2F20250616%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20250616T071600Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=e9b3a7eaa971ff50224700789c7b9d7bf01e37f5017981bdc58d12d0f868a6e0',NULL,NULL,437.47,14),('木饰面','乱纹枫木-直拼','http://47.115.47.145:9000/baoliphotos/materials/a335afdb-a382-486a-a3e8-8d12c28a9a7e.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=minioadmin%2F20250616%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20250616T071601Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=4ca36daeaccd6a2a204cb1fcda842583c527f9b433144923e1c4d067e4342ef8',NULL,NULL,424.43,15),('木饰面','朽枫-直拼','http://47.115.47.145:9000/baoliphotos/materials/60da3f04-faa9-43db-9732-4bf82531f8a7.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=minioadmin%2F20250616%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20250616T071602Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=a0e7b21f5b1822a41f887a64a0946c815b839b33c7e50dcbab733bf13aab74b0',NULL,NULL,567.88,16),('木饰面','卷纹加球纹枫影-直拼','http://47.115.47.145:9000/baoliphotos/materials/3cee854d-27e5-49f8-8322-c519a65b5c98.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=minioadmin%2F20250616%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20250616T071603Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=c3f96a52763ccb0297a3321127071d29874946f20bc9f5330d32dec89bc648ec',NULL,NULL,515.72,17),('木饰面','雀眼-直拼','http://47.115.47.145:9000/baoliphotos/materials/f3ef255a-fffc-4e60-8813-fd41dba86945.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=minioadmin%2F20250616%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20250616T071604Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=ff123177a7c4d1c19701171579a8fdda8af63c74080f67350d0c0b24a13077d2',NULL,NULL,828.7,18),('木饰面','柚木王又名大美木豆-直拼','http://47.115.47.145:9000/baoliphotos/materials/81a89dfa-efab-459a-87cb-91d312ce461e.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=minioadmin%2F20250616%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20250616T071605Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=422effed316006902f0871126ee887113ae6dc2c3ce0ad9d82a94a4f795d0766','http://47.115.47.145:9000/baoliphotos/materials/f14263b3-09ac-4933-ad4c-9c9659d7bca7.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=minioadmin%2F20250616%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20250616T071606Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=e4856b7cc90336316910bff36cc3882a677a8ab48dbf50d97a231a104ea2dca5',NULL,372.27,19),('木饰面','红橡山纹-直拼','http://47.115.47.145:9000/baoliphotos/materials/5e486048-ecc3-41cb-89a6-8097b94d2297.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=minioadmin%2F20250616%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20250616T071607Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=3ccba9f059253b67234d48c6ee26b1137133d3139d5365e71a360398a2c21b87','http://47.115.47.145:9000/baoliphotos/materials/d3cda2fe-558c-48df-9abc-419202cfba32.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=minioadmin%2F20250616%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20250616T071608Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=25676997d97418c0e15e0f551622ab206972430b5e39a9d0023b2ce8cd5db6b2',NULL,346.19,20);
/*!40000 ALTER TABLE `material` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `process_method`
--

DROP TABLE IF EXISTS `process_method`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `process_method` (
  `id` int NOT NULL AUTO_INCREMENT,
  `material_category` varchar(255) DEFAULT NULL,
  `method` varchar(255) DEFAULT NULL COMMENT '施工工艺',
  `price` double DEFAULT NULL COMMENT '人辅费用',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `process_method`
--

LOCK TABLES `process_method` WRITE;
/*!40000 ALTER TABLE `process_method` DISABLE KEYS */;
/*!40000 ALTER TABLE `process_method` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-06-16 15:32:26
