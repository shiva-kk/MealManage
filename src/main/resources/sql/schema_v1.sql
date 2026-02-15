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
	V_LNAME,V_GRADE,DATE(NOW()),NEW.orderAmount,NEW.paymentStatus,
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
		orderPrice=NEW.orderAmount,
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
