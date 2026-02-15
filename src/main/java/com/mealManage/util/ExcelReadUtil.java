package com.mealManage.util;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.EnumUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.mealManage.dao.MealManageAPIDao;
import com.mealManage.domain.EligCertReq;
import com.mealManage.mealmodel.meal.ItemTypeConstants;
import com.mealManage.mealmodel.meal.MealType;
import com.mealManage.mealmodel.repository.MealSchoolRepository;
import com.mealManage.mealmodel.school.SchoolGrades;
import com.mealManage.mealmodel.user.ParentUser;
import com.mealManage.mealmodel.user.StudentUser;
import com.mealManage.response.MealCreateJson;
import com.mealManage.response.MealItems;
import com.mealManage.response.MealJsonData;
import com.mealManage.response.SchoolHoliday;
import com.mealManage.response.StudentBalanceImportResp;

@Component
@SuppressWarnings("deprecation")
public class ExcelReadUtil {
	
	private Boolean reducedPriceStatus;
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private MealSchoolRepository mealSchoolRepository;
	@Autowired
	private MealManageAPIDao mealManageAPIDao;
	
	public MealCreateJson mealJson(MultipartFile multipartFile, String yearMonth, List<SchoolHoliday> schoolHolidays, 
			String menuType, Boolean isV2API, Boolean isItemized, Boolean isExtraPreOrder) throws Exception {
		MealItems mealItemsData = new MealItems();
		MealCreateJson mealCreateJson = new MealCreateJson();
		reducedPriceStatus = false;
		Workbook workbook = null;
		if (multipartFile.getOriginalFilename().endsWith("xlsx")) {
			workbook = new XSSFWorkbook(multipartFile.getInputStream());
		} else if (multipartFile.getOriginalFilename().endsWith("xls")) {
			workbook = new HSSFWorkbook(multipartFile.getInputStream());

		}
		/*if(menuType != null && menuType.equalsIgnoreCase("Breakfast")){
			if(workbook.getSheet("Breakfast") == null){
				mealCreateJson.setStatus("Failed");
				mealCreateJson.setStatusCode(417);
				mealCreateJson.setStatusMessage("Breakfast sheet not found!");
				return mealCreateJson;
			}
			mealCreateJson = breakfastSheetItems(workbook.getSheet("Breakfast"), yearMonth, schoolHolidays, isV2API);
			if(mealCreateJson.getStatusCode() != 200)
				return mealCreateJson;
			mealItemsData.setMealMenuItems(mealCreateJson.getMealJsonDataList());
		}else */
		if(isItemized != null && isItemized){
			if(workbook.getSheet("Extra") == null){
				mealCreateJson.setStatus("Failed");
				mealCreateJson.setStatusCode(417);
				mealCreateJson.setStatusMessage("Menu items sheet not found!");
				return mealCreateJson;
			}
			mealCreateJson = getItemizeItems(workbook.getSheet("Extra"), yearMonth, schoolHolidays, isV2API, menuType, null, null);
			if(mealCreateJson.getStatusCode() != 200)
				return mealCreateJson;
			mealItemsData.setMealMenuItems(mealCreateJson.getMealJsonDataList());
		}else{			
			if(menuType != null && menuType.equalsIgnoreCase("Breakfast")){
				if(workbook.getSheet("Breakfast") == null){
					mealCreateJson.setStatus("Failed");
					mealCreateJson.setStatusCode(417);
					mealCreateJson.setStatusMessage("Breakfast sheet not found!");
					return mealCreateJson;
				}
				mealCreateJson = firstSheetItems(workbook.getSheet("Breakfast"), yearMonth, schoolHolidays, isV2API, menuType);
			}else{
				if(workbook.getSheet("Lunch") == null){
					mealCreateJson.setStatus("Failed");
					mealCreateJson.setStatusCode(417);
					mealCreateJson.setStatusMessage("Lunch sheet not found!");
					return mealCreateJson;
				}
				mealCreateJson = firstSheetItems(workbook.getSheet("Lunch"), yearMonth, schoolHolidays, isV2API, menuType);
			}			
			if (mealCreateJson.getStatusCode() != 200) {
				return mealCreateJson;
			}
			List<MealJsonData> mealJsonDataList1 = new ArrayList<MealJsonData>();
			mealItemsData.setMealMenuItems(mealCreateJson.getMealJsonDataList());
			if(isExtraPreOrder != null && isExtraPreOrder){
				mealCreateJson = getItemizeItems(workbook.getSheet("Extra"), yearMonth, schoolHolidays, isV2API, menuType, isExtraPreOrder, mealCreateJson.getSchoolDays());
				if(mealCreateJson.getStatusCode() == 200){
					mealJsonDataList1.addAll(mealItemsData.getMealMenuItems());
					mealJsonDataList1.addAll(mealCreateJson.getMealJsonDataList());
					mealItemsData.setMealMenuItems(mealJsonDataList1);
				}					
			}else{
				if (workbook.getSheet("Extra") != null)
					mealJsonDataList1 = getMealExtra(workbook.getSheet("Extra"));
				mealItemsData.setExtra(mealJsonDataList1);
			}			
		}
		workbook.close();		
		mealItemsData.setReducedPriceStatus(reducedPriceStatus);
		mealCreateJson.setMealItems(mealItemsData);
		logger.info("Meal excel file read successfully");
		return mealCreateJson;
	}
	

	private MealCreateJson firstSheetItems(Sheet worksheet, String yearMonth, List<SchoolHoliday> schoolHolidays, 
			Boolean isV2API, String menuType) throws Exception{
		int i = 3;
		Map<String, Integer> mealItems = new HashMap<String, Integer>();
		List<MealJsonData> mealJsonDataList = new ArrayList<MealJsonData>();
		MealCreateJson mealCreateJson = new MealCreateJson();
		Map<String, String> holidayMap = new HashMap<String, String>();
		int numb = 0;
		String itemType = MealType.MEAL.toString();
		if(menuType.equalsIgnoreCase(ItemTypeConstants.Snack.toString()))
			itemType = MealType.SNACK.toString();
		else if(menuType.equalsIgnoreCase(ItemTypeConstants.Dinner.toString()))
			itemType = MealType.DINNER.toString();
		else if(menuType.equalsIgnoreCase(ItemTypeConstants.Breakfast.toString()))
			itemType = MealType.BREAKFAST.toString();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		for(SchoolHoliday schoolHoliday : schoolHolidays){
			MealJsonData mealJsonData = new MealJsonData();
			mealJsonData.setType("HOLIDAY");
			mealJsonData.setTitle(schoolHoliday.getHolidayName());
			mealJsonData.setDesc(schoolHoliday.getHolidayDesc());
			String date = sdf.format(sdf1.parse(schoolHoliday.getDateOfHoliday()));
			String[] dtArray = date.split("-");
			String dateVal = dtArray[0]+", "+(Integer.parseInt(dtArray[1])-1)+", "+(Integer.parseInt(dtArray[2]));
			mealJsonData.setStart("new Date("+dateVal+"')'"); 
			mealJsonData.setEnd("new Date("+dateVal+"')'"); 
			if(mealJsonData.getTitle() != null && !mealJsonData.getTitle().equalsIgnoreCase("")){
				mealJsonDataList.add(mealJsonData);	
				holidayMap.put(date, schoolHoliday.getHolidayName());
				if(mealItems.get(mealJsonData.getTitle()) == null){
					mealItems.put(mealJsonData.getTitle(), numb);
				numb = numb+1;
			}
			}
		}
		List<Integer> schoolDays = new ArrayList<Integer>();
		// Reads the data from excel file until last row is encountered
		OUTER_LOOP: 
		while (i <= worksheet.getLastRowNum()) {
			Boolean isHoliday = false;
			// Creates an object for the UserInfo Model
			MealJsonData mealJsonData1 = new MealJsonData();
			MealJsonData mealJsonData2 = new MealJsonData();
			MealJsonData mealJsonData3 = new MealJsonData();
			MealJsonData mealJsonData4 = new MealJsonData();
			MealJsonData mealJsonData5 = new MealJsonData();
			MealJsonData mealJsonData6 = new MealJsonData();
			MealJsonData mealJsonData7 = new MealJsonData();
			MealJsonData mealJsonData8 = new MealJsonData();
			// Creates an object representing a single row in excel
			Row row = worksheet.getRow(i++);
			String dateVal = "";
			Date dt;
			String dtArray[] = null;
			// Sets the Read data to the model class
			for(int j=0; j<19; j++){
				if(row == null)
					break OUTER_LOOP;
				Cell cell = row.getCell(j);
				if(cell != null){
				if(j == 0){
						try{
							//cell.setCellType(Cell.CELL_TYPE_NUMERIC);
							if(cell.getDateCellValue() == null)
								break OUTER_LOOP;
							dt = (cell.getDateCellValue());
						}catch(Exception e){
							logger.error("Cell value ::"+cell+" doesn't have valid date format");
							break OUTER_LOOP;
						}						
						if(holidayMap.get(sdf.format(dt)) != null)
							break;
						dtArray = sdf.format(dt).split("-");
						if(!yearMonth.equalsIgnoreCase(dtArray[0]+dtArray[1])){
							mealCreateJson.setStatus("Failed");
							mealCreateJson.setStatusMessage("Menu creation failed as it is belong to wrong date "+sdf.format(dt)+".");
							mealCreateJson.setStatusCode(400);
							logger.info(mealCreateJson.getStatusMessage());
							return mealCreateJson;
						}
						
						dateVal = dtArray[0]+", "+(Integer.parseInt(dtArray[1])-1)+", "+(Integer.parseInt(dtArray[2]));
						mealJsonData1.setStart("new Date("+dateVal+"')'"); 
						mealJsonData2.setStart("new Date("+dateVal+"')'");
						mealJsonData3.setStart("new Date("+dateVal+"')'");
						mealJsonData4.setStart("new Date("+dateVal+"')'");
						mealJsonData5.setStart("new Date("+dateVal+"')'"); 
						mealJsonData6.setStart("new Date("+dateVal+"')'");
						mealJsonData7.setStart("new Date("+dateVal+"')'");
						mealJsonData8.setStart("new Date("+dateVal+"')'");
						mealJsonData1.setEnd("new Date("+dateVal+"')'"); 
						mealJsonData2.setEnd("new Date("+dateVal+"')'");
						mealJsonData3.setEnd("new Date("+dateVal+"')'");
						mealJsonData4.setEnd("new Date("+dateVal+"')'");
						mealJsonData5.setEnd("new Date("+dateVal+"')'"); 
						mealJsonData6.setEnd("new Date("+dateVal+"')'");
						mealJsonData7.setEnd("new Date("+dateVal+"')'");
						mealJsonData8.setEnd("new Date("+dateVal+"')'");
					}
				cell.setCellType(1);
				String cellStrValue = "";
				if(cell.getStringCellValue() != null)
					cellStrValue = cell.getStringCellValue();
					switch(j){
					case 1: if(cellStrValue != null && cellStrValue.equalsIgnoreCase("N"))
						isHoliday = true; break;
					case 2: mealJsonData1.setType(itemType);
							mealJsonData1.setTitle(cellStrValue); break;
					case 3: mealJsonData1.setDesc(cellStrValue.length() < 253 ? cellStrValue : cellStrValue.substring(0, 253)); break;
					case 4: mealJsonData2.setType(itemType);
							mealJsonData2.setTitle(cellStrValue); break;
					case 5: mealJsonData2.setDesc(cellStrValue.length() < 253 ? cellStrValue : cellStrValue.substring(0, 253)); break;
					case 6: mealJsonData3.setType(itemType);
							mealJsonData3.setTitle(cellStrValue); break;
					case 7: mealJsonData3.setDesc(cellStrValue.length() < 253 ? cellStrValue : cellStrValue.substring(0, 253)); break;
					case 8: mealJsonData4.setType(itemType);
							mealJsonData4.setTitle(cellStrValue); break;
					case 9: mealJsonData4.setDesc(cellStrValue.length() < 253 ? cellStrValue : cellStrValue.substring(0, 253)); break;
					case 10: mealJsonData5.setType(itemType);
							mealJsonData5.setTitle(cellStrValue); break;
					case 11: mealJsonData5.setDesc(cellStrValue.length() < 253 ? cellStrValue : cellStrValue.substring(0, 253)); break;
					case 12: mealJsonData6.setType(itemType);
							mealJsonData6.setTitle(cellStrValue); break;
					case 13: mealJsonData6.setDesc(cellStrValue.length() < 253 ? cellStrValue : cellStrValue.substring(0, 253)); break;
					case 14: mealJsonData7.setType(itemType);
							mealJsonData7.setTitle(cellStrValue); break;
					case 15: mealJsonData7.setDesc(cellStrValue.length() < 253 ? cellStrValue : cellStrValue.substring(0, 253)); break;
					case 16: mealJsonData8.setType("SIDE");
							mealJsonData8.setTitle(cellStrValue); break;
					case 17: if(!cellStrValue.isEmpty()){
							mealJsonData1.setPrice(Double.valueOf(cellStrValue));
							mealJsonData2.setPrice(Double.valueOf(cellStrValue));
							mealJsonData3.setPrice(Double.valueOf(cellStrValue));
							mealJsonData4.setPrice(Double.valueOf(cellStrValue));
							mealJsonData5.setPrice(Double.valueOf(cellStrValue));
							mealJsonData6.setPrice(Double.valueOf(cellStrValue));
							mealJsonData7.setPrice(Double.valueOf(cellStrValue));
							mealJsonData8.setPrice(0.0);
						}break;
					case 18: if(!cellStrValue.isEmpty()){
							mealJsonData1.setReducedPrice(Double.valueOf(cellStrValue));
							mealJsonData2.setReducedPrice(Double.valueOf(cellStrValue));
							mealJsonData3.setReducedPrice(Double.valueOf(cellStrValue));
							mealJsonData4.setReducedPrice(Double.valueOf(cellStrValue));
							mealJsonData5.setReducedPrice(Double.valueOf(cellStrValue));
							mealJsonData6.setReducedPrice(Double.valueOf(cellStrValue));
							mealJsonData7.setReducedPrice(Double.valueOf(cellStrValue));
							mealJsonData8.setReducedPrice(0.0);
							if(!reducedPriceStatus)
								reducedPriceStatus = true;
						}break;
					}
				}
				if(isHoliday && j == 2)
					break;
			}
			if(isHoliday){
				mealJsonData1.setType("HOLIDAY");
				if(mealJsonData1.getTitle() == null || mealJsonData1.getTitle().equalsIgnoreCase(""))
					mealJsonData1.setTitle("***No School***");
				mealJsonDataList.add(mealJsonData1);
				if(mealItems.get(mealJsonData1.getTitle()) == null){
					mealItems.put(mealJsonData1.getTitle(), numb);
					numb = numb+1;
				}
			}else{
				if(mealJsonData1.getTitle() != null && !mealJsonData1.getTitle().equalsIgnoreCase("")){
					if(mealJsonData1.getPrice() == null || /*mealJsonData1.getPrice() <= 0 || */mealJsonData1.getReducedPrice() == null /*|| mealJsonData1.getReducedPrice() <= 0*/){
						mealCreateJson.setStatus("Failed");
						mealCreateJson.setStatusCode(417);
						mealCreateJson.setStatusMessage((mealJsonData1.getPrice() == null/* || mealJsonData1.getPrice() <= 0*/ ? "Price" : "Reduced price")+" information missing. Please try again!!");
						return mealCreateJson;
					}
					mealJsonDataList.add(mealJsonData1);
					if(mealItems.get(mealJsonData1.getTitle()) == null){
						mealItems.put(mealJsonData1.getTitle(), numb);
						numb = numb+1;
					}
					if(dtArray != null)
						schoolDays.add(Integer.parseInt(dtArray[2]));
				}
				if(mealJsonData2.getTitle() != null && !mealJsonData2.getTitle().equalsIgnoreCase("") && mealJsonData2.getPrice() != null){
					mealJsonDataList.add(mealJsonData2);	
					if(mealItems.get(mealJsonData2.getTitle()) == null){
						mealItems.put(mealJsonData2.getTitle(), numb);
					numb = numb+1;
				}				
				}
				if(mealJsonData3.getTitle() != null && !mealJsonData3.getTitle().equalsIgnoreCase("") && mealJsonData3.getPrice() != null){
					mealJsonDataList.add(mealJsonData3);
					if(mealItems.get(mealJsonData3.getTitle()) == null){
						mealItems.put(mealJsonData3.getTitle(), numb);
						numb = numb+1;
					}
				}
				if(mealJsonData4.getTitle() != null && !mealJsonData4.getTitle().equalsIgnoreCase("") && mealJsonData4.getPrice() != null){
					mealJsonDataList.add(mealJsonData4);
					if(mealItems.get(mealJsonData4.getTitle()) == null){
						mealItems.put(mealJsonData4.getTitle(), numb);
						numb = numb+1;
					}
				}
				if(mealJsonData5.getTitle() != null && !mealJsonData5.getTitle().equalsIgnoreCase("") && mealJsonData5.getPrice() != null){
					mealJsonDataList.add(mealJsonData5);
					if(mealItems.get(mealJsonData5.getTitle()) == null){
						mealItems.put(mealJsonData5.getTitle(), numb);
						numb = numb+1;
					}
				}
				if(mealJsonData6.getTitle() != null && !mealJsonData6.getTitle().equalsIgnoreCase("") && mealJsonData6.getPrice() != null){
					mealJsonDataList.add(mealJsonData6);
					if(mealItems.get(mealJsonData6.getTitle()) == null){
						mealItems.put(mealJsonData6.getTitle(), numb);
						numb = numb+1;
					}
				}
				if(mealJsonData7.getTitle() != null && !mealJsonData7.getTitle().equalsIgnoreCase("") && mealJsonData7.getPrice() != null){
					mealJsonDataList.add(mealJsonData7);
					if(mealItems.get(mealJsonData7.getTitle()) == null){
						mealItems.put(mealJsonData7.getTitle(), numb);
						numb = numb+1;
					}
				}
				if(mealJsonData8.getTitle() != null && !mealJsonData8.getTitle().equalsIgnoreCase("") && mealJsonData8.getPrice() != null){
					if(isV2API != null && isV2API){
						for(String sidesMenu : mealJsonData8.getTitle().split(",")){
							sidesMenu = sidesMenu.trim();
							MealJsonData mealJsonData = new MealJsonData();
							BeanUtils.copyProperties(mealJsonData, mealJsonData8);
							mealJsonData.setTitle(sidesMenu);
							if(mealItems.get(sidesMenu) == null){
								mealItems.put(sidesMenu, numb);
								numb = numb+1;
							}
							mealJsonDataList.add(mealJsonData);
						}
					}else{
						mealJsonDataList.add(mealJsonData8);
						if(mealItems.get(mealJsonData8.getTitle()) == null){
							mealItems.put(mealJsonData8.getTitle(), numb);
							numb = numb+1;
						}
					}
				}
			}
		}
				
		for(MealJsonData mealJsondata : mealJsonDataList){
			mealJsondata.setId(mealItems.get(mealJsondata.getTitle()));
		}
		mealCreateJson.setMealJsonDataList(mealJsonDataList);
		mealCreateJson.setStatusCode(200);
		mealCreateJson.setStatusMessage("First sheet successfully read.");
		mealCreateJson.setSchoolDays(schoolDays);
		logger.info("First sheet has been read successfully");
	return mealCreateJson;	
	}
	
	private List<MealJsonData> getMealExtra(Sheet worksheet){
		List<MealJsonData> mealJsonDataList = new ArrayList<MealJsonData>();
		Map<String, Integer> mealItems = new HashMap<String, Integer>();
		int numb = 0;
		int i = 3;
		// Reads the data from excel file until last row is encountered
		OUTER_LOOP:
		while (i <= worksheet.getLastRowNum()) {
			// Creates an object for the UserInfo Model
			MealJsonData mealJsonData1 = new MealJsonData();
			// Creates an object representing a single row in excel
			Row row = worksheet.getRow(i++);
			for(int j=0; j<3; j++){
				Cell cell = row.getCell(j);
				if(cell != null){
				cell.setCellType(1);
				String cellStrValue = "";
				if(cell.getStringCellValue() != null)
					cellStrValue = cell.getStringCellValue();
				if(cellStrValue == null || cellStrValue.isEmpty())
					break OUTER_LOOP;
					switch(j){
					case 1: mealJsonData1.setType("EXTRA");
							mealJsonData1.setTitle(cellStrValue); break;
					case 2: mealJsonData1.setPrice(!cellStrValue.isEmpty() ? Double.valueOf(cellStrValue) : null);break;
					}
				}
			}
			if(mealJsonData1.getTitle() != null && !mealJsonData1.getTitle().equalsIgnoreCase("")){
				mealJsonDataList.add(mealJsonData1);
				if(mealItems.get(mealJsonData1.getTitle()) == null){
					mealItems.put(mealJsonData1.getTitle(), numb);
					numb = numb+1;
				}
			}
		}	
		for(MealJsonData mealJsondata : mealJsonDataList){
			mealJsondata.setId(mealItems.get(mealJsondata.getTitle()));
		}	
		logger.info("Meal extra item sheet successfully read");
		return mealJsonDataList;			
	}
	
	private MealCreateJson getItemizeItems(Sheet worksheet, String yearMonth, List<SchoolHoliday> schoolHolidays, 
			Boolean isV2API, String menuType, Boolean isExtraPreOrder, List<Integer> schoolDays) throws Exception{
		MealCreateJson mealCreateJson = new MealCreateJson();
		List<MealJsonData> mealJsonDataList = new ArrayList<MealJsonData>();
		Map<String, Integer> mealItems = new HashMap<String, Integer>();
		String itemType = MealType.MEAL.toString();
		if(menuType.equalsIgnoreCase(ItemTypeConstants.Snack.toString()))
			itemType = MealType.SNACK.toString();
		else if(menuType.equalsIgnoreCase(ItemTypeConstants.Dinner.toString()))
			itemType = MealType.DINNER.toString();
		if(isExtraPreOrder != null && isExtraPreOrder)
			itemType = MealType.EXTRA.toString();
		int numb = 0;
		int i = 3;
		// Reads the data from excel file until last row is encountered
		OUTER_LOOP:
		while (i <= worksheet.getLastRowNum()) {
			// Creates an object for the UserInfo Model
			MealJsonData mealJsonData1 = new MealJsonData();
			// Creates an object representing a single row in excel
			Row row = worksheet.getRow(i++);
			for(int j=0; j<5; j++){
				Cell cell = row.getCell(j);
				if(cell != null){
				cell.setCellType(1);
				String cellStrValue = "";
				if(cell.getStringCellValue() != null)
					cellStrValue = cell.getStringCellValue();
				if((j == 1 || j == 2 ) && (cellStrValue == null || cellStrValue.isEmpty()))
					break OUTER_LOOP;
					switch(j){
						case 1: mealJsonData1.setType(itemType);
							mealJsonData1.setTitle(cellStrValue); break;
						case 2: mealJsonData1.setPrice(!cellStrValue.isEmpty() ? Double.valueOf(cellStrValue) : null);break;
						case 3: mealJsonData1.setAvailabilityDays(cellStrValue); break;
						case 4: mealJsonData1.setDesc(cellStrValue); break;
					}
				}
			}
			if(mealJsonData1.getTitle() != null && !mealJsonData1.getTitle().equalsIgnoreCase("")){
				mealJsonDataList.add(mealJsonData1);
				if(mealItems.get(mealJsonData1.getTitle()) == null){
					mealItems.put(mealJsonData1.getTitle(), numb);
					numb = numb+1;
				}
			}
		}	
		for(MealJsonData mealJsondata : mealJsonDataList){
			mealJsondata.setId(mealItems.get(mealJsondata.getTitle()));
			if(mealJsondata.getPrice() == null/* || mealJsondata.getPrice() <= 0*/){
				mealCreateJson.setStatus("Failed");
				mealCreateJson.setStatusCode(417);
				mealCreateJson.setStatusMessage("Price information missing. Please try again!!");
				return mealCreateJson;
			}
		}	
		logger.info(menuType+" items sheet successfully read");
		int yearVal = Integer.parseInt(yearMonth.substring(0,4));
    	int monthVal = Integer.parseInt(yearMonth.substring(4))-1;
    	Calendar calendar = Calendar.getInstance();
    	calendar.set(yearVal, monthVal, 01);
		int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH); 
		List<MealJsonData> mealJsonDataListFinal = new ArrayList<MealJsonData>();
		for(int day = 01; day <= daysInMonth; day++){
			String dateVal = "new Date("+yearVal+", "+monthVal+", "+day+"')'";
			String date = yearVal+"-"+(monthVal+1)+"-"+day;
			/*String dayName = getDayStringOld(date);
			if(!dayName.equalsIgnoreCase("Fri") && !dayName.equalsIgnoreCase("Sat")){*/
				for(MealJsonData mealJsonData1 : mealJsonDataList){
					MealJsonData mealJsonData = new MealJsonData(); 
					BeanUtils.copyProperties(mealJsonData, mealJsonData1);
					if(schoolDays == null || schoolDays.contains(day)){
						if(mealJsonData.getAvailabilityDays() == null || mealJsonData.getAvailabilityDays().equalsIgnoreCase("All") || 
								mealJsonData.getAvailabilityDays().trim().isEmpty()){
							mealJsonData.setStart(dateVal);
							mealJsonData.setEnd(dateVal);
							mealJsonDataListFinal.add(mealJsonData);
						}else if(Arrays.asList(mealJsonData.getAvailabilityDays().split(",")).contains(getDayStringOld(date))){
							mealJsonData.setStart(dateVal);
							mealJsonData.setEnd(dateVal);
							mealJsonDataListFinal.add(mealJsonData);
						}
					}
				}
			//}
			
		}		
		mealCreateJson.setMealJsonDataList(mealJsonDataListFinal);
		mealCreateJson.setStatusCode(200);
		mealCreateJson.setStatusMessage("Menu sheet mapped to calendar days successfully.");
		logger.info(menuType+" "+mealCreateJson.getStatusMessage());
		return mealCreateJson;			
	}
	
	/**This method used for build breakfast menu items in json 
	 * @throws ParseException 
	 * @throws Exception 
	 * @throws IllegalAccessException **/
	/*private MealCreateJson breakfastSheetItems(Sheet worksheet, String yearMonth, List<SchoolHoliday> schoolHolidays, Boolean isV2API) throws ParseException, IllegalAccessException, Exception{
		int i = 3;
		Map<String, Integer> mealItems = new HashMap<String, Integer>();
		List<MealJsonData> mealJsonDataList = new ArrayList<MealJsonData>();
		MealCreateJson mealCreateJson = new MealCreateJson();
		Map<String, String> holidayMap = new HashMap<String, String>();
		int numb = 0;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		for(SchoolHoliday schoolHoliday : schoolHolidays){
			MealJsonData mealJsonData = new MealJsonData();
			mealJsonData.setType("HOLIDAY");
			mealJsonData.setTitle(schoolHoliday.getHolidayName());
			mealJsonData.setDesc(schoolHoliday.getHolidayDesc());
			String date = sdf.format(sdf1.parse(schoolHoliday.getDateOfHoliday()));
			String[] dtArray = date.split("-");
			String dateVal = dtArray[0]+", "+(Integer.parseInt(dtArray[1])-1)+", "+(Integer.parseInt(dtArray[2]));
			mealJsonData.setStart("new Date("+dateVal+"')'"); 
			mealJsonData.setEnd("new Date("+dateVal+"')'"); 
			if(mealJsonData.getTitle() != null && !mealJsonData.getTitle().equalsIgnoreCase("")){
				mealJsonDataList.add(mealJsonData);	
				holidayMap.put(date, schoolHoliday.getHolidayName());
				if(mealItems.get(mealJsonData.getTitle()) == null){
					mealItems.put(mealJsonData.getTitle(), numb);
				numb = numb+1;
			}
			}
		}
		// Reads the data from excel file until last row is encountered
		OUTER_LOOP: 
		while (i <= worksheet.getLastRowNum()) {
			// Creates an object for the UserInfo Model
			MealJsonData mealJsonData1 = new MealJsonData();
			MealJsonData mealJsonData2 = new MealJsonData();
			MealJsonData mealJsonData3 = new MealJsonData();
			MealJsonData mealJsonData4 = new MealJsonData();
			// Creates an object representing a single row in excel
			Row row = worksheet.getRow(i++);
			Boolean isHoliday = false;
			String dateVal = "";
			Date dt;
			String dtArray[];
			// Sets the Read data to the model class
			for(int j=0; j<11; j++){
				Cell cell = row.getCell(j);
				if(cell != null){
				if(j == 0){
						if(cell.getDateCellValue() == null)
							break OUTER_LOOP;
						dt = (cell.getDateCellValue());
						if(holidayMap.get(sdf.format(dt)) != null)
							break;
						dtArray = sdf.format(dt).split("-");
						if(!yearMonth.equalsIgnoreCase(dtArray[0]+dtArray[1])){
							mealCreateJson.setStatus("Failed");
							mealCreateJson.setStatusMessage("Breakfast menu creation failed as it is belong to wrong date "+sdf.format(dt)+".");
							mealCreateJson.setStatusCode(400);
							logger.info(mealCreateJson.getStatusMessage());
							return mealCreateJson;
						}
						
						dateVal = dtArray[0]+", "+(Integer.parseInt(dtArray[1])-1)+", "+(Integer.parseInt(dtArray[2]));
						mealJsonData1.setStart("new Date("+dateVal+"')'"); 
						mealJsonData2.setStart("new Date("+dateVal+"')'");
						mealJsonData3.setStart("new Date("+dateVal+"')'");
						mealJsonData4.setStart("new Date("+dateVal+"')'");
						mealJsonData1.setEnd("new Date("+dateVal+"')'"); 
						mealJsonData2.setEnd("new Date("+dateVal+"')'");
						mealJsonData3.setEnd("new Date("+dateVal+"')'");
						mealJsonData4.setEnd("new Date("+dateVal+"')'");
					}
				cell.setCellType(1);
				String cellStrValue = "";
				if(cell.getStringCellValue() != null)
					cellStrValue = cell.getStringCellValue();
					switch(j){
					case 1: if(cellStrValue != null && cellStrValue.equalsIgnoreCase("N"))
						isHoliday = true; break;
					case 2: mealJsonData1.setType("BREAKFAST");
							mealJsonData1.setTitle(cellStrValue); break;
					case 3: mealJsonData1.setDesc(cellStrValue.length() < 253 ? cellStrValue : cellStrValue.substring(0, 253)); break;
					case 4: mealJsonData2.setType("BREAKFAST");
							mealJsonData2.setTitle(cellStrValue); break;
					case 5: mealJsonData2.setDesc(cellStrValue.length() < 253 ? cellStrValue : cellStrValue.substring(0, 253)); break;
					case 6: mealJsonData3.setType("BREAKFAST");
							mealJsonData3.setTitle(cellStrValue); break;
					case 7: mealJsonData3.setDesc(cellStrValue.length() < 253 ? cellStrValue : cellStrValue.substring(0, 253)); break;
					case 8: mealJsonData4.setType("SIDE");
							mealJsonData4.setTitle(cellStrValue); break;
					case 9: if(!cellStrValue.isEmpty()){
						mealJsonData1.setPrice(Double.valueOf(cellStrValue));
						mealJsonData2.setPrice(Double.valueOf(cellStrValue));
						mealJsonData3.setPrice(Double.valueOf(cellStrValue));
						mealJsonData4.setPrice(0.0);
					}break;
					case 10: if(!cellStrValue.isEmpty()){
						mealJsonData1.setReducedPrice(Double.valueOf(cellStrValue));
						mealJsonData2.setReducedPrice(Double.valueOf(cellStrValue));
						mealJsonData3.setReducedPrice(Double.valueOf(cellStrValue));
						mealJsonData4.setReducedPrice(0.0);
						if(!reducedPriceStatus)
							reducedPriceStatus = true;
					}break;
					}
				}
			}
			if(isHoliday){
				mealJsonData1.setType("HOLIDAY");
				mealJsonData1.setTitle("***No School***");
				mealJsonDataList.add(mealJsonData1);
				if(mealItems.get(mealJsonData1.getTitle()) == null){
					mealItems.put(mealJsonData1.getTitle(), numb);
					numb = numb+1;
				}
			}else{
				if(mealJsonData1.getTitle() != null && !mealJsonData1.getTitle().equalsIgnoreCase("")){
					if(mealJsonData1.getPrice() == null || mealJsonData1.getPrice() <= 0 || mealJsonData1.getReducedPrice() == null || mealJsonData1.getReducedPrice() <= 0){
						mealCreateJson.setStatus("Failed");
						mealCreateJson.setStatusCode(417);
						mealCreateJson.setStatusMessage((mealJsonData1.getPrice() == null || mealJsonData1.getPrice() <= 0 ? "Price" : "Reduced price")+" information missing. Please try again!!");
						return mealCreateJson;
					}
					mealJsonDataList.add(mealJsonData1);
					if(mealItems.get(mealJsonData1.getTitle()) == null){
						mealItems.put(mealJsonData1.getTitle(), numb);
						numb = numb+1;
					}
				}
				if(mealJsonData2.getTitle() != null && !mealJsonData2.getTitle().equalsIgnoreCase("") && mealJsonData2.getPrice() != null){
					mealJsonDataList.add(mealJsonData2);	
					if(mealItems.get(mealJsonData2.getTitle()) == null){
						mealItems.put(mealJsonData2.getTitle(), numb);
					numb = numb+1;
				}				
				}
				if(mealJsonData3.getTitle() != null && !mealJsonData3.getTitle().equalsIgnoreCase("") && mealJsonData3.getPrice() != null){
					mealJsonDataList.add(mealJsonData3);
					if(mealItems.get(mealJsonData3.getTitle()) == null){
						mealItems.put(mealJsonData3.getTitle(), numb);
						numb = numb+1;
					}
				}
				if(mealJsonData4.getTitle() != null && !mealJsonData4.getTitle().equalsIgnoreCase("") && mealJsonData4.getPrice() != null){
					if(isV2API != null && isV2API){
						for(String sidesMenu : mealJsonData4.getTitle().split(",")){
							sidesMenu = sidesMenu.trim();
							MealJsonData mealJsonData = new MealJsonData();
							BeanUtils.copyProperties(mealJsonData, mealJsonData4);
							mealJsonData.setTitle(sidesMenu);
							if(mealItems.get(sidesMenu) == null){
								mealItems.put(sidesMenu, numb);
								numb = numb+1;
							}
							mealJsonDataList.add(mealJsonData);
						}
					}else{
						mealJsonDataList.add(mealJsonData4);
						if(mealItems.get(mealJsonData4.getTitle()) == null){
							mealItems.put(mealJsonData4.getTitle(), numb);
							numb = numb+1;
						}
					}
				}
			}
		}	
		for(MealJsonData mealJsondata : mealJsonDataList){
			mealJsondata.setId(mealItems.get(mealJsondata.getTitle()));
		}
		mealCreateJson.setMealJsonDataList(mealJsonDataList);
		mealCreateJson.setStatusCode(200);
		mealCreateJson.setStatusMessage("Breakfast sheet read successfully.");
		logger.info("Breakfast sheet read successfully.");
		return mealCreateJson;	
	}*/

	public List<StudentUser> studentUsers(MultipartFile multipartFile, Long schoolId, String loggedUser, Integer schoolYear) throws IOException{
		List<StudentUser> stdList = new ArrayList<StudentUser>();
		//Map<String, String> gradeMap = sqsListner.customGradeKeyVal();
		Map<String, String> gradeMap = mealManageAPIDao.gradeBackMapByCountry(mealSchoolRepository.getSchoolCountry(schoolId));
		int i = 3;
		Workbook workbook = null;
		if (multipartFile.getOriginalFilename().endsWith("xlsx")) {
			workbook = new XSSFWorkbook(multipartFile.getInputStream());
	    } else if (multipartFile.getOriginalFilename().endsWith("xls")) {
	        workbook = new HSSFWorkbook(multipartFile.getInputStream());
	    }
		Sheet worksheet = workbook.getSheetAt(0);
		String gradeName = "";
		// Reads the data in excel file until last row is encountered
		OUTER_LOOP:
		while (i <= worksheet.getLastRowNum()) {
			// Creates an object for the UserInfo Model
			StudentUser students = new StudentUser();
			// Creates an object representing a single row in excel
			Row row = worksheet.getRow(i++);
			ParentUser parentUser = new ParentUser();
			// Sets the Read data to the model class
			for(int j=0; j<16; j++){
				Cell cell = row.getCell(j);
				if(cell != null){
				cell.setCellType(1);
				String cellStrValue = "";
				if(cell.getStringCellValue() != null)
					cellStrValue = cell.getStringCellValue();
					switch(j){
					case 0: students.setFirstName(cellStrValue); break;
					case 1: students.setLastName(cellStrValue); break;
					case 2: gradeName = gradeMap.get(cellStrValue);
						if(gradeName == null || gradeName.isEmpty())
							gradeName = cellStrValue;
						gradeName = CommonUtil.validGrade(gradeName);
						if(!EnumUtils.isValidEnum(SchoolGrades.class, gradeName))
							break OUTER_LOOP;
						students.setGradeName(SchoolGrades.valueOf(gradeName)); break;
					case 3: students.setStudentId(cellStrValue); 
							if(cellStrValue == null || cellStrValue.isEmpty())
								break OUTER_LOOP;
							break;
					
					//case 4: students.setSchoolStudentId(cellStrValue);
					case 4: students.setTeacherName(cellStrValue);break;
					case 5: parentUser.setUserName(cellStrValue); break;
					case 6 : students.setIsReducePriceEligible(cellStrValue.equalsIgnoreCase("Y")?true:false); break;
					case 7: students.setIsFreeMealEligible(cellStrValue.equalsIgnoreCase("Y")?true:false);break;
					case 8: parentUser.setParentAltEmail(cellStrValue); break;
					case 9: students.setMobileNo(cellStrValue);
							parentUser.setMobileNo(cellStrValue);break;
					case 10: students.setNumberStreetApt(cellStrValue);break;
					case 11: students.setCityStateZip(cellStrValue);break;
					case 12: students.setBeforeCare(cellStrValue.equalsIgnoreCase("Yes")?true:false);break;
					case 13: students.setHasMilkCard(cellStrValue.equalsIgnoreCase("Yes")?true:false);break;
					case 14: 
						if(cellStrValue != null && !cellStrValue.trim().equalsIgnoreCase(""))
							students.setIsEnrollBCAndACPkt(cellStrValue.equalsIgnoreCase("Yes")?true:false);
						break;
					case 15: students.setPin(cellStrValue);break;
					
					}
				}
			}
			if(parentUser.getUserName() == null || parentUser.getUserName().trim().isEmpty()){
				if(parentUser.getParentAltEmail() != null && !parentUser.getParentAltEmail().trim().isEmpty())
					parentUser.setUserName(parentUser.getParentAltEmail());
				else
					parentUser.setUserName("NA");
			}
			students.setParentuser(parentUser);
			students.setSchoolYear(schoolYear);
			/*MealSchool mealSchool = new MealSchool();
			mealSchool.setSchoolId(schoolId);
			students.setMealSchool(mealSchool);*/
			students.setLoggedUser(loggedUser);
			students.setCreatedOn(new Date());
			stdList.add(students);
		}			
		workbook.close();	
		return stdList;
	}
	
	public List<EligCertReq> stdEligCertDateImport(MultipartFile multipartFile, Long schoolId, Integer schoolYear) throws IOException{
		List<EligCertReq> stdList = new ArrayList<EligCertReq>();
		int i = 1;
		Workbook workbook = null;
		if (multipartFile.getOriginalFilename().endsWith("xlsx")) {
			workbook = new XSSFWorkbook(multipartFile.getInputStream());
	    } else if (multipartFile.getOriginalFilename().endsWith("xls")) {
	        workbook = new HSSFWorkbook(multipartFile.getInputStream());
	    }
		Sheet worksheet = workbook.getSheetAt(0);
		//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		OUTER_LOOP:
		while (i <= worksheet.getLastRowNum()) {
			// Creates an object for the UserInfo Model
			EligCertReq eligCert = new EligCertReq();
			// Creates an object representing a single row in excel
			Row row = worksheet.getRow(i++);
			// Sets the Read data to the model class
			for(int j=0; j<4; j++){
				Cell cell = row.getCell(j);
				if(cell != null){
					if(j != 3){
						cell.setCellType(1);
						String cellStrValue = "";
						if(cell.getStringCellValue() != null)
							cellStrValue = cell.getStringCellValue();
							switch(j){
								case 0: eligCert.setStudentId(cellStrValue); 
										if(cellStrValue == null || cellStrValue.isEmpty())
											break OUTER_LOOP;
										break;
								case 1: eligCert.setName(cellStrValue); break;
								case 2: eligCert.setPrgSource(cellStrValue); break;		
						}
					}else{
						try {
							eligCert.setCertDate(cell.getDateCellValue());
						} catch (Exception e) {
							logger.error("Failed to convert date::"+cell.getDateCellValue()+" during certification date import due to "+e.getMessage());
						}
					}
					
				}
			}
			stdList.add(eligCert);
		}			
		workbook.close();	
		return stdList;
	}
	
	/**This method used for map the student balance data**/
	public List<StudentBalanceImportResp> studentUsersBalance(MultipartFile multipartFile) throws IOException{
		List<StudentBalanceImportResp> stdBalList = new ArrayList<StudentBalanceImportResp>();
		int i = 1;
		Workbook workbook = null;
		if (multipartFile.getOriginalFilename().endsWith("xlsx")) {
			workbook = new XSSFWorkbook(multipartFile.getInputStream());
	    } else if (multipartFile.getOriginalFilename().endsWith("xls")) {
	        workbook = new HSSFWorkbook(multipartFile.getInputStream());
	    }
		Sheet worksheet = workbook.getSheetAt(0);
		while (i <= worksheet.getLastRowNum()) {
			StudentBalanceImportResp studentBal = new StudentBalanceImportResp();
			// Creates an object representing a single row in excel
			Row row = worksheet.getRow(i++);
			// Sets the Read data to the model class
			for(int j=0; j<4; j++){
				Cell cell = row.getCell(j);
				if(cell != null){
				cell.setCellType(1);
				String cellStrValue = "";
				if(cell.getStringCellValue() != null)
					cellStrValue = cell.getStringCellValue();
					switch(j){
					case 0: studentBal.setStudentId(cellStrValue);break;
					case 1: studentBal.setFirstName(cellStrValue);break;
					case 2: studentBal.setLastName(cellStrValue);break;
					case 3: if(!cellStrValue.isEmpty())
						studentBal.setBalance(Double.valueOf(cellStrValue));break;
					//case 4: studentBal.setBalanceType(cellStrValue);break;
					}
				}
			}
			stdBalList.add(studentBal);
		}			
		workbook.close();	
		return stdBalList;
	}
	
	/**This method used for prepare the list of holidays from excel file**/
	public List<SchoolHoliday> importHolidays(MultipartFile multipartFile, Long mealSchoolId) throws IOException{
		List<SchoolHoliday> schoolHolidays = new ArrayList<SchoolHoliday>();
		int i = 1;
		Workbook workbook = null;
		if (multipartFile.getOriginalFilename().endsWith("xlsx")) {
			workbook = new XSSFWorkbook(multipartFile.getInputStream());
	    } else if (multipartFile.getOriginalFilename().endsWith("xls")) {
	        workbook = new HSSFWorkbook(multipartFile.getInputStream());
	    }
		Sheet worksheet = workbook.getSheetAt(0);
		// Reads the data in excel file until last row is encountered
		while (i <= worksheet.getLastRowNum()) {
			// Creates an object for the UserInfo Model
			SchoolHoliday schoolHoliday = new SchoolHoliday();
			// Creates an object representing a single row in excel
			Row row = worksheet.getRow(i++);
			Date dt;
			// Sets the Read data to the model class
			for(int j=0; j<3; j++){
				Cell cell = row.getCell(j);
				if(cell != null){
					if(j == 0){
						dt = (cell.getDateCellValue());
						schoolHoliday.setHolidayDate(dt);
					}else{
						cell.setCellType(1);
						switch(j){
						case 1: schoolHoliday.setHolidayName(cell.getStringCellValue()); break;
						case 2: schoolHoliday.setHolidayDesc(cell.getStringCellValue()); break;
					}
					}	
				}
			}
			schoolHolidays.add(schoolHoliday);
		}			
		workbook.close();	
		return schoolHolidays;
	}
	
	/**Get day in 3 character**/
	public String getDayStringOld(String date) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	    DateFormat formatter = new SimpleDateFormat("EEEE", Locale.ENGLISH);
	    return formatter.format(sdf.parse(date)).substring(0,3);
	}
	
}
