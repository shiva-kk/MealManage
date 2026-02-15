DROP TRIGGER IF EXISTS mealOrdersAudit_calendarMenu_AFTER_INSERT ^; 
CREATE TRIGGER `mealOrdersAudit_calendarMenu_AFTER_INSERT` AFTER INSERT ON `mealOrdersAudit_calendarMenu` FOR EACH ROW BEGIN

INSERT INTO OrderMealItemsDetailReport
(studentId, studentRecId, mealSchoolId, grade, mealId, mealName, 
mealType, mealPrice, studentFname, studentLname, mealDate, yearMonth, 
mealImage,orderId,schoolMealId, menuType)
select su.studentId,
su.userId,
su.mealSchool_schoolId,
su.gradeName,
m.id,
m.name,
m.category,
c.price,
su.firstName,
su.lastName,
c.date,
moa.yearMonth,
m.imageUrl,
moasm.orderId,
moasm.mealCalendarId,
moa.menuType 
 from mealOrdersAudit_calendarMenu moasm inner join MealOrdersAudit_v2 moa on moasm.orderId=moa.schoolId
inner join meal_calendar c on moasm.mealCalendarId=c.id
inner join menu_items m on c.menu_item_id=m.id inner join StudentUser_v2 su on moa.studentUser_userId=su.userId
where moasm.orderId=NEW.orderId and moasm.mealCalendarId=NEW.mealCalendarId;

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
	payedDate,totItems,yearMonth,mealSchoolId,pdfLink, menuType)
VALUES(
	V_SID,NEW.studentUser_userId,V_FNAME,
	V_LNAME,V_GRADE,DATE(NOW()),NEW.orderAmount,NEW.paymentStatus,NULL,NEW.Items_count,NEW.yearMonth,V_SCHOOLID,NEW.menuOrderedPdfLink,NEW.menuType);

END ^;

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
where mealSchoolId=V_SCHOOLID AND yearMonth=NEW.yearMonth AND studentRecId=NEW.studentUser_userId AND menuType = NEW.menuType; 


SET SQL_SAFE_UPDATES=1;
END ^;
