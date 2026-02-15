CREATE TABLE IF NOT EXISTS oauth_access_token (
  token_id VARCHAR(256) DEFAULT NULL,
  token BLOB,
  authentication_id VARCHAR(256) DEFAULT NULL,
  user_name VARCHAR(256) DEFAULT NULL,
  client_id VARCHAR(256) DEFAULT NULL,
  authentication BLOB,
  refresh_token VARCHAR(256) DEFAULT NULL
) ^;

CREATE TABLE IF NOT EXISTS oauth_refresh_token (
  token_id VARCHAR(256) DEFAULT NULL,
  token BLOB,
  authentication BLOB
) ^;

CREATE TABLE IF NOT EXISTS `schoolholidays` (
  `recId` bigint(20) NOT NULL AUTO_INCREMENT,
  `holidayName` varchar(255) NOT NULL,
  `holidayDesc` varchar(255) DEFAULT NULL,
  `holidayDate` datetime NOT NULL,
  `mealSchoolId` bigint(20) NOT NULL,
  `createdBy` varchar(250) DEFAULT NULL,
  `createdOn` datetime DEFAULT NULL,
  `modifiedBy` varchar(250) DEFAULT NULL,
  `modifiedOn` datetime DEFAULT NULL,
  PRIMARY KEY (`recId`),
  UNIQUE KEY `schoolAndDateUQ` (`holidayDate`,`mealSchoolId`),
  KEY `mealSchoolIdIndex` (`mealSchoolId`),
  KEY `schoolAndDateIndex` (`holidayDate`,`mealSchoolId`)
) ^;

CREATE TABLE IF NOT EXISTS `requestedemails` (
  `recNo` int(11) NOT NULL AUTO_INCREMENT,
  `emailId` varchar(50) NOT NULL,
  `requestedTime` datetime NOT NULL,
  `linkSendStatus` bit(1) NOT NULL,
  PRIMARY KEY (`recNo`),
  KEY `requestedTimeIndex` (`requestedTime`),
  KEY `linkStatusIndex` (`linkSendStatus`)
) ^;

CREATE TABLE IF NOT EXISTS `OrderMealItemsDetailReport` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `studentId` varchar(45) DEFAULT NULL,
  `studentRecId` int(11) DEFAULT NULL,
  `mealSchoolId` int(11) DEFAULT NULL,
  `grade` varchar(45) DEFAULT NULL,
  `mealId` bigint(15) DEFAULT NULL,
  `mealName` varchar(255) DEFAULT NULL,
  `mealType` varchar(45) DEFAULT NULL,
  `mealPrice` decimal(18,2) DEFAULT '0.00',
  `studentFname` varchar(45) DEFAULT NULL,
  `studentLname` varchar(45) DEFAULT NULL,
  `mealDate` datetime DEFAULT NULL,
  `yearMonth` varchar(45) DEFAULT NULL,
  `mealImage` varchar(100) DEFAULT NULL,
  `orderId` bigint(15) DEFAULT NULL,
  `schoolMealId` bigint(15) DEFAULT NULL,
  `paymentStatus` bit(1) DEFAULT b'0',
  PRIMARY KEY (`id`),
  KEY `schoolAndYearMonthIndex` (`mealSchoolId`,`yearMonth`),
  KEY `gradeAndPaymentStatusIndex` (`grade`,`paymentStatus`)
) ^;

CREATE TABLE IF NOT EXISTS `OrderMealsReport` (
  `recNo` int(11) NOT NULL AUTO_INCREMENT,
  `parentId` int(11) DEFAULT NULL,
  `studentId` varchar(45) DEFAULT NULL,
  `studentRecId` int(11) DEFAULT NULL,
  `studentFName` varchar(45) DEFAULT NULL,
  `studentLName` varchar(45) DEFAULT NULL,
  `grade` varchar(45) DEFAULT NULL,
  `orderDate` datetime DEFAULT NULL,
  `orderPrice` decimal(18,2) DEFAULT '0.00',
  `paymentStatus` bit(1) DEFAULT b'0',
  `payedDate` datetime DEFAULT NULL,
  `totItems` int(11) DEFAULT '0',
  `yearMonth` varchar(45) DEFAULT NULL,
  `mealSchoolId` int(11) DEFAULT NULL,
  `pdfLink` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`recNo`),
  KEY `schoolAndYearMonthIndex` (`mealSchoolId`,`yearMonth`),
  KEY `gradeAndPaymentStatusIndex` (`grade`,`paymentStatus`)
) ^;

DROP TRIGGER IF EXISTS mealOrdersAudit_schoolMeals_AFTER_DELETE ^; 
CREATE TRIGGER `mealOrdersAudit_schoolMeals_AFTER_DELETE` AFTER DELETE ON `mealOrdersAudit_schoolMeals` FOR EACH ROW BEGIN
SET SQL_SAFE_UPDATES=0;
DELETE FROM OrderMealItemsDetailReport WHERE
orderId=old.orderId AND schoolMealId=old.schoolMealId;
SET SQL_SAFE_UPDATES=1;
END ^;

DROP TRIGGER IF EXISTS mealOrdersAudit_schoolMeals_AFTER_INSERT ^; 
CREATE TRIGGER `mealOrdersAudit_schoolMeals_AFTER_INSERT` AFTER INSERT ON `mealOrdersAudit_schoolMeals` FOR EACH ROW BEGIN

INSERT INTO OrderMealItemsDetailReport
(studentId, studentRecId, mealSchoolId, grade, mealId, mealName, 
mealType, mealPrice, studentFname, studentLname, mealDate, yearMonth, 
mealImage,orderId,schoolMealId)
select su.studentId,
su.userId,
su.mealSchool_schoolId,
su.gradeName,
m.mealId,
m.mealName,
m.mealType,
m.mealPrice,
su.firstName,
su.lastName,
mealDate,
moa.yearMonth,
m.mealImage,
moasm.orderId,
moasm.schoolMealId
 from mealOrdersAudit_schoolMeals moasm inner join MealOrdersAudit_v2 moa on moasm.orderId=moa.schoolId
inner join SchoolMeals_v2 sm on moasm.schoolMealId=sm.schoolId
inner join MealMenu_v2 m on sm.mealMenu_Id=m.mealId inner join StudentUser_v2 su on moa.studentUser_userId=su.userId
where moasm.orderId=NEW.orderId and moasm.schoolMealId=NEW.schoolMealId;

END ^;

DROP TRIGGER IF EXISTS MealOrdersAudit_v2_AFTER_INSERT ^; 
CREATE TRIGGER `MealOrdersAudit_v2_AFTER_INSERT` AFTER INSERT ON `MealOrdersAudit_v2` FOR EACH ROW BEGIN
DECLARE V_FNAME VARCHAR(50) DEFAULT '';
DECLARE V_LNAME VARCHAR(50) DEFAULT '';
DECLARE V_GRADE VARCHAR(50) DEFAULT '';
DECLARE V_SID VARCHAR(50) DEFAULT '';
DECLARE V_SCHOOLID INT;

SELECT 
	firstName,
	lastName,
	gradeName,
	mealSchool_schoolId,
    studentId
into 
	V_FNAME,
	V_LNAME,
	V_GRADE,
	V_SCHOOLID,
    V_SID
FROM StudentUser_v2 WHERE userId=NEW.studentUser_userId;

INSERT INTO OrderMealsReport ( 
	studentId,studentRecId,studentFName,
	studentLName,grade,orderDate,orderPrice,paymentStatus,
	payedDate,totItems,yearMonth,mealSchoolId,pdfLink)
VALUES(
	V_SID,NEW.studentUser_userId,V_FNAME,
	V_LNAME,V_GRADE,DATE(NOW()),NEW.totalPrice,NEW.paymentStatus,
	NULL,NEW.Items_count,NEW.yearMonth,V_SCHOOLID,NEW.menuOrderedPdfLink
);

END ^;

DROP TRIGGER IF EXISTS MealOrdersAudit_v2_AFTER_UPDATE ^; 
CREATE TRIGGER `MealOrdersAudit_v2_AFTER_UPDATE` AFTER UPDATE ON `MealOrdersAudit_v2` FOR EACH ROW BEGIN

DECLARE V_FNAME VARCHAR(50) DEFAULT '';
DECLARE V_LNAME VARCHAR(50) DEFAULT '';
DECLARE V_GRADE VARCHAR(50) DEFAULT '';
DECLARE V_SCHOOLID INT;

DECLARE V_PREV_PAYSTATUS BIT DEFAULT 0;
SELECT 	
	mealSchool_schoolId 
into 	
	V_SCHOOLID 
FROM StudentUser_v2 WHERE userId=NEW.studentUser_userId;

SELECT 
	paymentStatus INTO V_PREV_PAYSTATUS
FROM OrderMealsReport WHERE 
	mealSchoolId=V_SCHOOLID AND 
	yearMonth=NEW.yearMonth AND 
	studentRecId=NEW.studentUser_userId ;

SET SQL_SAFE_UPDATES=0;
IF OLD.paymentStatus=0 AND NEW.paymentStatus=0  THEN
	update OrderMealsReport SET 
		orderDate=DATE(NOW()),
		orderPrice=NEW.totalPrice,
		totItems=NEW.Items_count,
		pdfLink=NEW.menuOrderedPdfLink	
	where 
		mealSchoolId=V_SCHOOLID AND 
		yearMonth=NEW.yearMonth AND 
		studentRecId=NEW.studentUser_userId ;
ELSE 
	IF OLD.paymentStatus=0 AND NEW.paymentStatus=1 THEN
		update OrderMealsReport SET 
			paymentStatus=1,
			payedDate=NOW()
		where 
			mealSchoolId=V_SCHOOLID AND 
			yearMonth=NEW.yearMonth AND 
			studentRecId=NEW.studentUser_userId ;
		update OrderMealItemsDetailReport SET 
			paymentStatus=1
		where 
            mealSchoolId=V_SCHOOLID AND
            yearMonth=NEW.yearMonth AND
            studentRecId=NEW.studentUser_userId ;
	END IF;

END IF;
SET SQL_SAFE_UPDATES=1;
END ^;
