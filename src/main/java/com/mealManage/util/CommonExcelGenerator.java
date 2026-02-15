package com.mealManage.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.mealManage.dao.MealManageAPIDao;
import com.mealManage.mealmodel.repository.StudentUserRepository;
import com.mealManage.mealmodel.user.StudentUser;

@Component
/**This common utility class used for generate the Excel file**/
public class CommonExcelGenerator {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private HeaderConstants headerConstants;
	@Autowired
	private StudentUserRepository studentUserRepository;
	@Autowired
	private MealManageAPIDao mealManageAPIDao;
	@Value("${student.export.headers}")
	private String stdExcelHeaders;
	@Autowired
	private AWSUtility awsUtility;
	
	/**This method used to get all the students from database and build in map. type would be dataSync or import
	 * @throws Exception **/
	//@SuppressWarnings("unchecked")
	public String studentExport(Long mealSchoolId, Integer schoolYear, String type, Boolean isExport) throws Exception{
		String fileLink = "";
		try{
			List<StudentUser> studentUsers = studentUserRepository.findByMealSchoolSchoolIdAndSchoolYear(mealSchoolId, schoolYear);
			//ObjectMapper mapper = new ObjectMapper();
			Map<String, Object> map= null;
			List<Map<String, Object>> response = new LinkedList<Map<String, Object>>();
			if (studentUsers != null) {
				Map<String, String> gradesMap = mealManageAPIDao.gradeMapByCountry(studentUsers.get(0).getMealSchool().getCountryCode());
				//ParentUser parentUser = null;
				for (StudentUser studentUser : studentUsers) {
					if (studentUser != null) {
						map= new HashMap<String, Object>();
						/*studentUser.setMealSchool(null);
						parentUser = studentUser.getParentuser();
						studentUser.setParentuser(null);
						map = mapper.convertValue(studentUser, Map.class);*/
						map.put("firstName", studentUser.getFirstName());
						map.put("lastName", studentUser.getLastName());
						map.put("gradeName", gradesMap.get(studentUser.getGradeName().toString()));
						map.put("studentId", studentUser.getStudentId());
						map.put("userName", studentUser.getParentuser().getUserName());
						map.put("parentAltEmail", studentUser.getParentuser().getParentAltEmail());
						map.put("mobileNo", studentUser.getMobileNo());
						map.put("teacherName", studentUser.getTeacherName());				
						map.put("isReducePriceEligible", studentUser.getIsReducePriceEligible() != null && studentUser.getIsReducePriceEligible() ? "Y":"N");
						map.put("isFreeMealEligible", studentUser.getIsFreeMealEligible() != null && studentUser.getIsFreeMealEligible() ? "Y":"N");
						map.put("isBeforeCare", studentUser.isBeforeCare() ? "Yes":"No");
						map.put("hasMilkCard", studentUser.isHasMilkCard() ? "Yes":"No");
						map.put("numberStreetApt", studentUser.getNumberStreetApt());
						map.put("cityStateZip", studentUser.getCityStateZip());
						map.put("allergies", studentUser.getAllergies());
						map.put("accBalance", studentUser.getAccBalance());
						map.put("userId", studentUser.getUserId());
						if(studentUser.getIsEnrollBCAndACPkt() != null)
							map.put("isEnrollBCAndACPkt", studentUser.getIsEnrollBCAndACPkt() ? "Yes":"No");
						else
							map.put("isEnrollBCAndACPkt", "");
						map.put("isRegister", studentUser.isRegister() ? "Yes":"No");
						map.put("isActive", studentUser.getIsActive() != null && studentUser.getIsActive() ? "Yes":"No");
						map.put("schoolStudentId", studentUser.getSchoolStudentId());
						map.put("pin", studentUser.getPin());
						response.add(map);
					}
				}
			}
			String filePath = "Students_"+mealSchoolId+"_"+schoolYear+
					"_"+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())+"_"+type+".xls";
			excelBuild(response, Arrays.asList(stdExcelHeaders.split("\\s*,\\s*")), filePath);
			if(isExport != null && isExport)
				return filePath;
			//This method used for upload the file in S3
			fileLink = awsUtility.fileUploadPath(filePath, "studentBkpFileLink");
			awsUtility.uploadFileToAWSS3Bucket(filePath, "StudentBkp");
			logger.info("Student backup file S3 link: "+fileLink);
			
		}catch(Exception e){
			logger.error("Failed to export the students data due to "+e.getMessage());
		}
		return fileLink;		
	}
	
	/**This method used for generate the excel file**/
	@SuppressWarnings({ "deprecation", "static-access"})
	public void excelBuild(List<Map<String, Object>> excelData, List<String> excelHeaders, String filePath) {
		Workbook workbook = new HSSFWorkbook();
		try {
			Sheet sheet = workbook.createSheet("Students");
			sheet.setDefaultColumnWidth(20);
			CellStyle style = workbook.createCellStyle();
			style.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
			style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			Font font = workbook.createFont();
			font.setColor(IndexedColors.BLACK.getIndex());
			font.setBold(true);
			style.setFont(font);
			
			CellStyle style1 = workbook.createCellStyle();
			style1.setFillForegroundColor(IndexedColors.WHITE.getIndex());
			style1.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			style1.setBorderRight(BorderStyle.NONE);
			Font font1 = workbook.createFont();
			font1.setColor(IndexedColors.BLACK.getIndex());
			font1.setBold(true);
			style1.setFont(font1);
			style1.setAlignment(HorizontalAlignment.CENTER);
			
			CellStyle cellStyle = workbook.createCellStyle();
			cellStyle.setAlignment(HorizontalAlignment.CENTER);
			Row row = null;
			// create header row
			Cell cell = null;
			row = sheet.createRow((short) 0);
			
			cell = row.createCell((short) 0);
			cell.setCellValue("Students Sheet");
			cell.setCellStyle(style1);
			// Create a cellRangeAddress to select a range to merge.
			CellRangeAddress cellRangeAddress = new CellRangeAddress(0, 1, 0, 5);
			// Merge the selected cells.
			sheet.addMergedRegion(cellRangeAddress);
			
			row = sheet.createRow(2);
			int i = 0;
			for (String head : excelHeaders) {
				cell = row.createCell(i);
				cell.setCellValue(headerConstants.getClass().getField(head).get(null).toString());
				cell.setCellStyle(style);
				i++;
			}
			// create data rows
			int rowCount = 3;
			if (excelData != null && excelData.size() > 0) {
				for (Map<String, Object> excelRow : excelData) {
					row = sheet.createRow(rowCount++);
					int colCount = 0;
					for(String head : excelHeaders) {
						cell = row.createCell(colCount);
						String val = excelRow.get(head) != null ? excelRow.get(head).toString() : "";
						if (val.trim().isEmpty()) {
							try {
								cell.setCellType(cell.CELL_TYPE_BLANK);
								cell.setCellValue(val);
							} catch (Exception e) {
								e.printStackTrace();
							}
						} else {
							cell.setCellValue(val);
						}
						colCount++;
					}
				}
			} else {
				row = sheet.createRow(1);
				cell = row.createCell(0, Cell.CELL_TYPE_STRING);
				cell.setCellValue("No Data Available");
				cell.setCellStyle(cellStyle);
			}
			FileOutputStream fileOut = new FileOutputStream(filePath);
			workbook.write(fileOut);
			fileOut.close();
			// Closing the workbook
			workbook.close();
			logger.info("Student export excel file generated successfully");
		}catch(Exception e){
			logger.error("Failed to write the excel file for student export functionality due to "+e.getMessage());
		}
	}
	
	public void exportStudents(Long schoolId, Integer schoolYear, HttpServletResponse response) throws Exception{
		String fileName = studentExport(schoolId, schoolYear, "", true);
		InputStream myStream = new FileInputStream(fileName);
		response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
		response.setContentType("application/vnd.ms-excel");
		IOUtils.copy(myStream, response.getOutputStream());
		response.flushBuffer();
		myStream.close();
		new File(fileName).delete();
	}
	
	@SuppressWarnings("deprecation")
	public void exportBalanceStudents(Long schoolId, Integer schoolYear, HttpServletResponse response) throws Exception{
		try {
			List<StudentUser> studentUsers = studentUserRepository.findByMealSchoolSchoolIdAndSchoolYear(schoolId,
					schoolYear);
			String filePath = "StudentsBalance_" + schoolId + "_" + schoolYear + ".xls";
			Workbook workbook = new HSSFWorkbook();
			Sheet sheet = workbook.createSheet("Students");
			sheet.setDefaultColumnWidth(20);
			CellStyle style = workbook.createCellStyle();
			style.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
			style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			Font font = workbook.createFont();
			font.setColor(IndexedColors.BLACK.getIndex());
			font.setBold(true);
			style.setFont(font);

			CellStyle cellStyle = workbook.createCellStyle();
			cellStyle.setAlignment(HorizontalAlignment.CENTER);
			Row row = null;
			Cell cell = null;
			row = sheet.createRow(0);
			cell = row.createCell(0);
			cell.setCellValue("Student ID");
			cell.setCellStyle(style);
			cell = row.createCell(1);
			cell.setCellValue("First Name");
			cell.setCellStyle(style);
			cell = row.createCell(2);
			cell.setCellValue("Last Name");
			cell.setCellStyle(style);
			cell = row.createCell(3);
			cell.setCellValue("Balance ($)");
			cell.setCellStyle(style);
			// create data rows
			int rowCount = 1;
			if (studentUsers != null && studentUsers.size() > 0) {
				for (StudentUser su : studentUsers) {
					row = sheet.createRow(rowCount++);
					int colCount = 0;
					String val = "";
					for (int i = 1; i <= 4; i++) {
						cell = row.createCell(colCount);
						switch (i) {
						case 1:
							val = su.getStudentId();
							break;
						case 2:
							val = su.getFirstName() != null ? su.getFirstName() : "";
							break;
						case 3:
							val = su.getLastName() != null ? su.getLastName() : "";
							break;
						case 4:
							val = su.getAccBalance() != null ? su.getAccBalance().toString() : "";
							break;
						}
						if (val.trim().isEmpty()) {
							try {
								cell.setCellType(Cell.CELL_TYPE_BLANK);
								cell.setCellValue(val);
							} catch (Exception e) {
								e.printStackTrace();
							}
						} else {
							cell.setCellValue(val);
						}
						colCount++;
					}
				}
			} else {
				row = sheet.createRow(1);
				cell = row.createCell(0, Cell.CELL_TYPE_STRING);
				cell.setCellValue("No Data Available");
				cell.setCellStyle(cellStyle);
			}
			FileOutputStream fileOut = new FileOutputStream(filePath);
			workbook.write(fileOut);
			fileOut.close();
			// Closing the workbook
			workbook.close();

			InputStream myStream = new FileInputStream(filePath);
			response.setHeader("Content-Disposition", "attachment; filename=" + filePath);
			response.setContentType("application/vnd.ms-excel");
			IOUtils.copy(myStream, response.getOutputStream());
			response.flushBuffer();
			myStream.close();
			new File(filePath).delete();
		} catch (Exception e) {
			logger.error("Failed to generate student balance sheet due to "+e.getMessage());
		}
	}
	
	/**This method used for build the grade in map format**/
	/*private Map<String, String> gradeMapBuild(){
		Map<String, String> gradeMap = new HashMap<String, String>();
		gradeMap.put("pk", "PK");
		gradeMap.put("kg", "KG");
		gradeMap.put("k", "KG");
		gradeMap.put("one", "1");
		gradeMap.put("two", "2");
		gradeMap.put("three", "3");
		gradeMap.put("four", "4");
		gradeMap.put("five", "5");
		gradeMap.put("six", "6");
		gradeMap.put("seven", "7");
		gradeMap.put("eight", "8");
		gradeMap.put("nine", "9");
		gradeMap.put("ten", "10");
		gradeMap.put("eleven", "11");
		gradeMap.put("twelve", "12");
		gradeMap.put("thirteen", "13");
		gradeMap.put("staff", "Staff");
		gradeMap.put("year_1", "year_1");
		gradeMap.put("year_2", "year_2");
		gradeMap.put("year_3", "year_3");
		gradeMap.put("year_4", "year_4");
		gradeMap.put("year_5", "year_5");
		gradeMap.put("year_6", "year_6");
		gradeMap.put("year_7", "year_7");
		gradeMap.put("year_8", "year_8");
		gradeMap.put("year_9", "year_9");
		gradeMap.put("year_10", "year_10");
		gradeMap.put("year_11", "year_11");		
		return gradeMap;
	}*/
}
