DROP TRIGGER IF EXISTS mealOrdersAudit_calendarMenu_AFTER_INSERT ^; 
CREATE TRIGGER `mealOrdersAudit_calendarMenu_AFTER_INSERT` AFTER INSERT ON `mealOrdersAudit_calendarMenu` FOR EACH ROW BEGIN

INSERT INTO OrderMealItemsDetailReport
(studentId, studentRecId, mealSchoolId, grade, mealId, mealName, 
mealType, mealPrice, studentFname, studentLname, mealDate, yearMonth, 
mealImage,orderId,schoolMealId)
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
moasm.mealCalendarId
 from mealOrdersAudit_calendarMenu moasm inner join MealOrdersAudit_v2 moa on moasm.orderId=moa.schoolId
inner join meal_calendar c on moasm.mealCalendarId=c.id
inner join menu_items m on c.menu_item_id=m.id inner join StudentUser_v2 su on moa.studentUser_userId=su.userId
where moasm.orderId=NEW.orderId and moasm.mealCalendarId=NEW.mealCalendarId;

END ^;