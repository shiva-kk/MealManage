DROP TRIGGER IF EXISTS MealOrdersAudit_v2_AFTER_UPDATE ^; 
CREATE TRIGGER `MealOrdersAudit_v2_AFTER_UPDATE` AFTER UPDATE ON `MealOrdersAudit_v2` FOR EACH ROW BEGIN

DECLARE V_FNAME VARCHAR(50) DEFAULT '';
DECLARE V_LNAME VARCHAR(50) DEFAULT '';
DECLARE V_GRADE VARCHAR(50) DEFAULT '';
DECLARE V_SCHOOLID INT;

DECLARE V_PREV_PAYSTATUS BIT DEFAULT 0;
SELECT mealSchool_schoolId into V_SCHOOLID FROM StudentUser_v2 WHERE userId=NEW.studentUser_userId; 

SET SQL_SAFE_UPDATES=0; 

Update OrderMealsReport SET orderDate=DATE(NOW()), orderPrice=NEW.orderAmount, totItems=NEW.Items_count, pdfLink=NEW.menuOrderedPdfLink 
where mealSchoolId=V_SCHOOLID AND yearMonth=NEW.yearMonth AND studentRecId=NEW.studentUser_userId ; 


SET SQL_SAFE_UPDATES=1;
END ^;
