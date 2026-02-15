package com.mealManage.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.mealManage.domain.HouseholdAppDeclinedReason;
import com.mealManage.domain.HouseholdAppOtherInfo;
import com.mealManage.domain.HouseholdIncompleteApp;
import com.mealManage.mealmodel.repository.MealSchoolRepository;
import com.mealManage.mealmodel.user.HouseholdApplicationForFRM;

/**This utility class used for generate the notice benefits application in pdf**/
@Component
public class NoticeBenefitsLetterV2 {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	public static final  Font boldFont=FontFactory.getFont(FontFactory.HELVETICA, 9, Font.BOLD);
	public static final  Font generalFont1=FontFactory.getFont(FontFactory.HELVETICA, 10);
	public static final  Font generalFont=FontFactory.getFont(FontFactory.HELVETICA, 9);
	public static final  Font generalDateFont=FontFactory.getFont(FontFactory.HELVETICA, 14);
	private Image checkBoxImage;
    private Image checkedBoxImage;
	@Value("${amazon.s3.bucket}")
    private String amazonS3Bucketname;
	@Value("${amazon.s3.endpoint}")
	private String amazonS3Endpoint;
	@Autowired
	private DateUtilityV2 du;
	@Autowired
	private AWSUtility awsUtility;
	@Autowired
	private MealSchoolRepository mealSchoolRepository;
	@Autowired
	private NoticeBenefitsLetterSpanish noticeBenefitsLetterSpanish;
	@Value("${notice.benefits.principalTxt}")
	private String principalTxt;
	@Value("${notice.benefits.FoodManagerTxt}")
	private String foodManagerTxt;
	@Value("${notice.benefits.footer1}")
	private String footer1;
	@Value("${notice.benefits.footer2}")
	private String footer2;
	@Value("${notice.benefits.footer3}")
	private String footer3;
	private String logoLink;
	
	/**This method used for generate the pdf of transaction history report
	 * @throws Exception **/
	public void noticeBenefitsPdf(List<HouseholdAppOtherInfo> studentList, Boolean isFreeMeals, 
			HouseholdApplicationForFRM householdApplicationForFRM, String pdfFilePath, String schoolName, Boolean isExport, HttpServletResponse resp) throws Exception{
		Document document=new Document(PageSize.A4);// 40, 40, 40, 40);// For the page 8.5'' X 11'' dimension
		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfFilePath));
		writer.setPageEvent(new PdfHeaderFooter()); //This event used for add the page number at the bottom of each page..
		document.open();
		document.add(createNoticeBenefitsFile(householdApplicationForFRM, isFreeMeals, studentList, schoolName));
		document.newPage();
		document.add(noticeBenefitsLetterSpanish.createNoticeBenefitsFile(householdApplicationForFRM, isFreeMeals, studentList, schoolName));
		logger.info("Notice Benefits letter generated successfully");
		document.close();
		if(isExport){
			InputStream myStream = new FileInputStream(pdfFilePath);
	    	resp.setHeader("Content-Disposition", "attachment; filename=" + pdfFilePath);
	    	IOUtils.copy(myStream, resp.getOutputStream());
	    	resp.flushBuffer();
	    	myStream.close();
	    	new File(pdfFilePath).delete();
		}else
			awsUtility.uploadFileToAWSS3Bucket(pdfFilePath, "benefitsNotice");
	}
	
	/**This method used for generate the pdf of transaction history report
	 * @throws Exception **/
	public void noticeIncBenefitsPdf(List<HouseholdAppOtherInfo> studentList, 
			HouseholdApplicationForFRM householdApplicationForFRM, String pdfFilePath, String schoolName, Boolean isExport, HttpServletResponse resp) throws Exception{
		Document document=new Document(PageSize.A4);// 40, 40, 40, 40);// For the page 8.5'' X 11'' dimension
		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfFilePath));
		writer.setPageEvent(new PdfHeaderFooter()); //This event used for add the page number at the bottom of each page..
		document.open();
		document.add(createNoticeBenefitsINCFile(householdApplicationForFRM, studentList, schoolName));
		document.newPage();
		document.add(noticeBenefitsLetterSpanish.createNoticeBenefitsINCFile(householdApplicationForFRM, studentList, schoolName));
		logger.info("Notice Benefits letter generated successfully");
		document.close();
		if(isExport){
			InputStream myStream = new FileInputStream(pdfFilePath);
	    	resp.setHeader("Content-Disposition", "attachment; filename=" + pdfFilePath);
	    	IOUtils.copy(myStream, resp.getOutputStream());
	    	resp.flushBuffer();
	    	myStream.close();
	    	new File(pdfFilePath).delete();
		}else
			awsUtility.uploadFileToAWSS3Bucket(pdfFilePath, "benefitsNotice");
	}
		
	/**This method used for build the approved/declined notice letter**/
	private Element createNoticeBenefitsFile(HouseholdApplicationForFRM householdApplicationForFRM, Boolean isFreeMeals,
			List<HouseholdAppOtherInfo> studentList, String schoolName) throws Exception {
		PdfPTable table = new PdfPTable(new float[] {45,20,35});
		PdfPCell cell;
		cell = new PdfPCell();
		String currentTimezoneDt=du.formatDateToString(new Date(), "MM/dd/yyyy", 
				mealSchoolRepository.getSchoolTimezone(householdApplicationForFRM.getMealSchoolId()));
		cell.addElement(createLetterHead(studentList, householdApplicationForFRM,currentTimezoneDt));
		cell.setBorder(0);
		cell.setColspan(3);
		table.addCell(cell);
		Paragraph paragraph = new Paragraph();
		paragraph.add(new Chunk("Thank you for submitting the Application for Free and Reduced Price School Meals.", generalFont));
		paragraph.add(new Chunk(" Even though the district is providing free meals to all students throughout the 2021-2022 academic school year, "
				+ "receipt of the application is still important and used to determine eligibility for P-EBT benefits, state funding, and local assistance opportunities.", boldFont));
		cell = new PdfPCell(paragraph);
		cell.setBorder(0);
		cell.setPaddingTop(5);
		cell.setColspan(3);
		table.addCell(cell);
		
		cell = new PdfPCell(new Phrase("Your Application for Free and Reduced Price School Meals has been reviewed with the following results. Effective "+currentTimezoneDt+
				", your application has been:", generalFont));
		cell.setBorder(0);
		cell.setColspan(3);
		cell.setPaddingTop(5);
		table.addCell(cell);
		
		String checkedBoxPath = "https://s3.amazonaws.com/"+amazonS3Bucketname+"/checkedCheckbox.PNG";
		checkedBoxImage = Image.getInstance(checkedBoxPath);
		checkedBoxImage.scaleAbsolute(11f, 11f);
		checkedBoxImage.setAlignment(Image.ALIGN_CENTER);
		String checkBoxPath = "https://s3.amazonaws.com/"+amazonS3Bucketname+"/checkBox.PNG";
		checkBoxImage = Image.getInstance(checkBoxPath);
		checkBoxImage.scaleAbsolute(11f, 11f);
		checkBoxImage.setAlignment(Image.ALIGN_CENTER);
		cell = buildCheckboxWithText(isFreeMeals != null ? checkedBoxImage : checkBoxImage, "APPROVED");
		cell.setPaddingTop(10);
		cell.setColspan(3);
		cell.setPaddingLeft(10); 
		table.addCell(cell);
		if(isFreeMeals != null){
			cell = buildCheckboxWithText(isFreeMeals ? checkedBoxImage : checkBoxImage, "Free");
			cell.setPaddingTop(5);
			cell.setColspan(3);
			cell.setPaddingLeft(30); 
			table.addCell(cell);
			cell = buildCheckboxWithText(!isFreeMeals ? checkedBoxImage : checkBoxImage, "Reduced Price Meals");
			cell.setPaddingTop(5);
			cell.setColspan(3);
			cell.setPaddingLeft(30); 
			table.addCell(cell);
		}
		cell = buildCheckboxWithText(isFreeMeals == null ? checkedBoxImage : checkBoxImage, "DENIED");
		cell.setPaddingTop(10);
		cell.setColspan(3);
		cell.setPaddingLeft(10); 
		table.addCell(cell);
		HouseholdAppDeclinedReason incomeApp = null;
		HouseholdAppDeclinedReason incompleteApp = null;
		for(HouseholdAppDeclinedReason declineReason : householdApplicationForFRM.getDeclinedReasonList()){
			if(declineReason.getName().contains("Income over the allowable amount"))
				incomeApp = declineReason;
			else if(declineReason.getName().contains("Incomplete application"))
				incompleteApp = declineReason;
		}
		cell = buildCheckboxWithText((incompleteApp.getIsApplicable() != null && incompleteApp.getIsApplicable())
				? checkedBoxImage: checkBoxImage, "Your application is still incomplete.");
		cell.setPaddingTop(5); 
		cell.setColspan(3);
		cell.setPaddingLeft(30); 
		table.addCell(cell);
		cell = buildCheckboxWithText((incomeApp.getIsApplicable() != null && incomeApp.getIsApplicable())
					? checkedBoxImage: checkBoxImage, "Total household income exceeds the federal income eligibility guidelines.");
		cell.setPaddingTop(5); 
		cell.setColspan(3);
		cell.setPaddingLeft(30); 
		table.addCell(cell);
		
		cell = new PdfPCell(new Phrase("Please note that your child will continue to receive free meals throughout the school year but may not receive the "
				+ "additional benefits listed above. If you do not agree with the above decision, you may discuss it with a school "
				+ "official and you have the right to a fair hearing. This can be done by calling or writing to the following official: "+"Stephen Frost, "
						+ "Assistant Business Administrator / sfrost@rtnj.org/ 973-361-0808 x8202", generalFont));
		cell.setBorder(0);
		cell.setColspan(3);
		cell.setPaddingTop(5);
		cell.setPaddingLeft(25); 
		table.addCell(cell);
		
		cell = new PdfPCell(new Phrase("If you are not eligible now but have a decrease in household income, become unemployed, or have an increase in "
				+ "household size, you may fill out another application at that time to reapply for benefits. If your household does not qualify for"
				+ " benefits, there are other resources that may help. Check out https://www.nj211.org/ or http://www.endhungernj.org/.", generalFont));
		cell.setBorder(0);
		cell.setColspan(3);
		cell.setPaddingTop(5);
		cell.setPaddingLeft(25); 
		table.addCell(cell);
			
		cell = new PdfPCell();
		cell.addElement(createFooterText());
		cell.setBorder(0);
		cell.setColspan(3);
		table.addCell(cell);
		
		table.setWidthPercentage(100);
		return table;
	}
	
	/**This method used for build the incomplete notice letter**/
	private Element createNoticeBenefitsINCFile(HouseholdApplicationForFRM householdApplicationForFRM,
			List<HouseholdAppOtherInfo> studentList, String schoolName) throws Exception {
		PdfPTable table = new PdfPTable(new float[] {45,20,35});
		PdfPCell cell;
		cell = new PdfPCell();
		String timezone = mealSchoolRepository.getSchoolTimezone(householdApplicationForFRM.getMealSchoolId());
		String currentTimezoneDt=du.formatDateToString(new Date(), "MM/dd/yyyy", timezone);
		cell.addElement(createLetterHead(studentList, householdApplicationForFRM,currentTimezoneDt));
		cell.setBorder(0);
		cell.setColspan(3);
		table.addCell(cell);
		Paragraph paragraph = new Paragraph();
		paragraph.add(new Chunk("Even though the district is providing free meals to all students throughout the 2021-2022 academic school year, "
				+ "the Application for Free and Reduced-Price School Meals is also used to determine eligibility for P-EBT benefits, state "
				+ "funding, and local assistance opportunities.", generalFont));
		cell = new PdfPCell(paragraph);
		cell.setBorder(0);
		cell.setPaddingTop(5);
		cell.setColspan(3);
		table.addCell(cell);
		
		cell = new PdfPCell(new Phrase("Your Application for Free and Reduced Price School Meals cannot be approved because the application submitted is "
				+ "incomplete. The missing or incorrect information is indicated below:", generalFont));
		cell.setBorder(0);
		cell.setColspan(3);
		cell.setPaddingTop(5);
		table.addCell(cell);
		
		String checkedBoxPath = "https://s3.amazonaws.com/"+amazonS3Bucketname+"/checkedCheckbox.PNG";
		checkedBoxImage = Image.getInstance(checkedBoxPath);
		checkedBoxImage.scaleAbsolute(11f, 11f);
		checkedBoxImage.setAlignment(Image.ALIGN_CENTER);
		String checkBoxPath = "https://s3.amazonaws.com/"+amazonS3Bucketname+"/checkBox.PNG";
		checkBoxImage = Image.getInstance(checkBoxPath);
		checkBoxImage.scaleAbsolute(11f, 11f);
		checkBoxImage.setAlignment(Image.ALIGN_CENTER);
		for(HouseholdIncompleteApp incompleteReason : householdApplicationForFRM.getIncompleteReasonList()){
			cell = buildCheckboxWithText((incompleteReason.getIsApplicable() != null && incompleteReason.getIsApplicable())
					? checkedBoxImage: checkBoxImage, incompleteReason.getName());
			cell.setPaddingTop(5); 
			cell.setColspan(3);
			cell.setPaddingLeft(30); 
			table.addCell(cell);
			if(incompleteReason.getDescription() != null && !incompleteReason.getDescription().trim().isEmpty()){
				cell = new PdfPCell(new Phrase("Descriptions: "+incompleteReason.getDescription(), generalFont));
				cell.setBorder(0);
				cell.setColspan(3);
				cell.setPaddingLeft(50); 
				table.addCell(cell);
			}
		}
		
		cell = new PdfPCell(new Phrase("The above information must be provided by "+du.formatDateToString(householdApplicationForFRM.getIncompleteDueDate(), "MM/dd/yyyy", timezone)+
				". Failure to provide this Information will result in the application being denied. If you have any questions, please call Stephen Frost at sfrost@rtnj.org/ 973-361-0808 x8202.", generalFont));
		cell.setBorder(0);
		cell.setColspan(3);
		cell.setPaddingTop(5);
		cell.setPaddingLeft(25); 
		table.addCell(cell);
			
		cell = new PdfPCell();
		cell.addElement(createFooterText());
		cell.setBorder(0);
		cell.setColspan(3);
		table.addCell(cell);
		
		table.setWidthPercentage(100);
		return table;
	}
	
	/**This method used for create letter head header**/
	private Element createLetterHead(List<HouseholdAppOtherInfo> studentList, 
			HouseholdApplicationForFRM householdApplicationForFRM,String currentTimezoneDt) throws Exception{
		PdfPTable table = new PdfPTable(new float[] {45,20,35});
		PdfPCell cell;
		cell = new PdfPCell(new Phrase("RANDOLPH TOWNSHIP", generalDateFont));
		cell.setBorder(0);
		table.addCell(cell);
		logoLink = amazonS3Endpoint+"/"+amazonS3Bucketname+"/District_Logo.JPG";
		Image image = Image.getInstance(logoLink);
    	image.scaleAbsolute(45f, 45f);
    	image.setAlignment(Image.ALIGN_CENTER);
    	cell = new PdfPCell();	   
    	cell.addElement(image);
    	cell.setBorder(0);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("PUBLIC SCHOOLS", generalDateFont));
		cell.setBorder(0);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("25 SCHOOL HOUSE ROAD, RANDOLPH, NJ 07869", generalFont1));
		cell.setBorder(0);
		cell.setColspan(2);
		cell.setRowspan(2);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("(973) 361-0808", generalFont1));
		cell.setBorder(0);
		cell.setPaddingLeft(40); 
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("(973) 361-2405 (FAX)", generalFont1));
		cell.setBorder(0);
		cell.setPaddingLeft(40); 
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("Letter to Notify Household of Eligibility Status", generalFont1));
		cell.setBorder(0);
		cell.setColspan(3);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell);
		
		cell = new PdfPCell(new Phrase("", generalDateFont));
		cell.setBorder(Rectangle.BOTTOM);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setColspan(3);
		cell = new PdfPCell(new Phrase("", generalDateFont));
		cell.setBorder(Rectangle.BOTTOM);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setColspan(3);
		table.addCell(cell);
		String stdNames = "";
		
		for(HouseholdAppOtherInfo householdAppOtherInfo : studentList){
			stdNames=stdNames+""+householdAppOtherInfo.getFname()+" "+householdAppOtherInfo.getLname()+",";
		}
		stdNames = stdNames.substring(0, stdNames.length()-1);
		cell = new PdfPCell(new Phrase("Parent or Guardian:  "+stdNames, generalFont));
		cell.setBorder(0);
		cell.setColspan(2);
		cell.setPaddingTop(5);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("Date:  "+currentTimezoneDt, generalFont));
		cell.setBorder(0);
		cell.setPaddingTop(5);
		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		table.addCell(cell);
		table.setWidthPercentage(100);
		return table;
	}
	
	/**This method used  for created letter footer**/
	private Element createFooterText(){
		PdfPTable table = new PdfPTable(new float[] {45,20,35});
		PdfPCell cell;
		cell = new PdfPCell(new Phrase("Sincerely,", generalFont));
		cell.setBorder(0);
		cell.setColspan(3);
		cell.setPaddingTop(5);
		table.addCell(cell);
			
		cell = new PdfPCell(new Phrase("Stephen Frost", generalFont));
		cell.setBorder(0);
		cell.setColspan(3);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("Assistant Business Administrator", generalFont));
		cell.setBorder(0);
		cell.setColspan(3);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("sfrost@rtnj.org", generalFont));
		cell.setBorder(0);
		cell.setColspan(3);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("In accordance with Federal civil rights law and U.S. Department of Agriculture (USDA) civil rights regulations and policies, "
				+ "the USDA, its Agencies, offices, and employees, and institutions participating in or administering USDA programs are prohibited from discriminating "
				+ "based on race, color, national origin, sex, disability, age, or reprisal or retaliation for prior civil rights activity in any program or activity "
				+ "conducted or funded by USDA.", generalFont));
		cell.setBorder(0);
		cell.setColspan(3);
		cell.setPaddingTop(5);
		table.addCell(cell);
		
		cell = new PdfPCell(new Phrase("Persons with disabilities who require alternative means of communication for program information (e.g. Braille, large "
				+ "print, audiotape, American Sign Language, etc.), should contact the Agency (State or local) where they applied for benefits. "
				+ "Individuals who are deaf, hard of hearing or have speech disabilities may contact USDA through the Federal Relay Service "
				+ "at (800) 877-8339. Additionally, program information may be made available in languages other than English.", generalFont));
		cell.setBorder(0);
		cell.setColspan(3);
		cell.setPaddingTop(5);
		table.addCell(cell);
		
		cell = new PdfPCell(new Phrase("To file a program complaint of discrimination, complete the USDA Program Discrimination Complaint Form, (AD-3027) "
				+ "found online at: How to File a Complaint, and at any USDA office, or write a letter addressed to USDA and provide in the "
				+ "letter all of the information requested in the form. To request a copy of the complaint form, call (866) 632-9992. Submit "
				+ "your completed form or letter to USDA by:", generalFont));
		cell.setBorder(0);
		cell.setColspan(3);
		cell.setPaddingTop(5);
		table.addCell(cell);
		
		cell = new PdfPCell();
		cell.setBorder(0);
		cell.setColspan(3);
		cell.addElement(createUSDAText());
		table.addCell(cell);
		
		cell = new PdfPCell(new Phrase("This institution is an equal opportunity provider.", generalFont));
		cell.setBorder(0);
		cell.setColspan(3);
		cell.setPaddingTop(5);
		table.addCell(cell);
		table.setWidthPercentage(100);
		return table;
	}
	
	/**This method used for create  USDA text**/
	private Element createUSDAText(){
		PdfPTable table = new PdfPTable(new float[] {8,40,52});
		PdfPCell cell = new PdfPCell();
		
		cell = new PdfPCell(new Phrase("(1)", generalFont));
		cell.setBorder(0);
		cell.setPaddingTop(5);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("mail: U.S. Department of Agriculture Office of the Assistant Secretary for Civil Rights 1400 Independence Avenue, SW Washington, D.C. 20250-9410;", generalFont));
		cell.setBorder(0);
		cell.setPaddingTop(5);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("", generalFont));
		cell.setBorder(0);
		cell.setPaddingTop(5);
		table.addCell(cell);
		
		cell = new PdfPCell(new Phrase("(2)", generalFont));
		cell.setBorder(0);
		cell.setPaddingTop(5);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("fax: (202) 690-7442; or", generalFont));
		cell.setBorder(0);
		cell.setPaddingTop(5);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("", generalFont));
		cell.setBorder(0);
		cell.setPaddingTop(5);
		table.addCell(cell);
		
		cell = new PdfPCell(new Phrase("(3)", generalFont));
		cell.setBorder(0);
		cell.setPaddingTop(5);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("email: program.intake@usda.gov.", generalFont));
		cell.setBorder(0);
		cell.setPaddingTop(5);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("", generalFont));
		cell.setBorder(0);
		cell.setPaddingTop(5);
		table.addCell(cell);
		table.setWidthPercentage(100);
		return table;
	}
	
	{
		
	}
	
	/**This method used for build the text with checkbox**/
	private PdfPCell buildCheckboxWithText(Image image, String text){
		PdfPCell cellFinal = new PdfPCell();
		PdfPTable pdfPTable = new PdfPTable(new float[] {7,93});
		PdfPCell cell;
		cell = new PdfPCell();
		cell.setBorder(0);
        cell.addElement(image);
        pdfPTable.addCell(cell);
        cell = new PdfPCell(new Phrase(text, generalFont));
		cell.setBorder(0);
		pdfPTable.addCell(cell);
		pdfPTable.setWidthPercentage(100);
		cellFinal.addElement(pdfPTable);
		cellFinal.setBorder(0);
		return cellFinal;
	}
}
