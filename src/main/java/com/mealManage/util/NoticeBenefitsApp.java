package com.mealManage.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.itextpdf.text.BaseColor;
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
import com.mealManage.mealmodel.repository.SchoolYearRepository;
import com.mealManage.mealmodel.school.MealSchool;
import com.mealManage.mealmodel.school.SchoolYear;
import com.mealManage.mealmodel.user.HouseholdApplicationForFRM;

/**This utility class used for generate the notice benefits application in pdf**/
@Component
public class NoticeBenefitsApp {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	public static final  Font boldFont=FontFactory.getFont(FontFactory.HELVETICA, 10, Font.BOLD);
	public static final  Font generalFont=FontFactory.getFont(FontFactory.HELVETICA, 9);
	public static final  Font smallerFont=FontFactory.getFont(FontFactory.HELVETICA, 8);
	public static final  Font smallerBoldFont=FontFactory.getFont(FontFactory.HELVETICA, 8, Font.BOLD, BaseColor.ORANGE);
	public static final  Font generalDateFont=FontFactory.getFont(FontFactory.HELVETICA, 13);
	public static final  Font generalDateFont1=FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK);
	public static final  Font smallerFooterFont=FontFactory.getFont(FontFactory.TIMES_ITALIC, 8);
    private Image checkBoxImage;
    private Image checkedBoxImage;
	@Value("${amazon.s3.bucket}")
    private String amazonS3Bucketname;
	@Autowired
	private SchoolYearRepository schoolYearRepository;
	@Autowired
	private AWSUtility awsUtility;
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
	
	/**This method used for generate the pdf of transaction history report
	 * @throws Exception **/
	public void noticeBenefitsPdf(List<HouseholdAppOtherInfo> studentList, Boolean isFreeMeals, 
			HouseholdApplicationForFRM householdApplicationForFRM, String pdfFilePath, String schoolName, Boolean isExport, HttpServletResponse resp) throws Exception{
		Document document=new Document(PageSize.A4);// 40, 40, 40, 40);// For the page 8.5'' X 11'' dimension
		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfFilePath));
		writer.setPageEvent(new PdfHeaderFooter()); //This event used for add the page number at the bottom of each page..
		document.open();
		document.add(createNoticeBenefitsFile(householdApplicationForFRM, isFreeMeals, studentList, schoolName));
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
		
	/**This method used for build the transaction history table**/
	private Element createNoticeBenefitsFile(HouseholdApplicationForFRM householdApplicationForFRM, Boolean isFreeMeals,
			List<HouseholdAppOtherInfo> studentList, String schoolName) throws Exception {
		PdfPTable table = new PdfPTable(new float[] {50,50});
		PdfPCell cell;
		cell = new PdfPCell(new Phrase(schoolName.toUpperCase(), generalDateFont1));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setColspan(2);
		cell.setBorder(0);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("NOTIFICATION  LETTER  FOR  SCHOOL  MEALS", generalDateFont));
		cell.setBorder(Rectangle.BOTTOM);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setPaddingTop(10);
		cell.setColspan(2);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("", generalDateFont));
		cell.setBorder(Rectangle.BOTTOM);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setColspan(2);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("Dear Parent/Guardian,", generalFont));
		cell.setBorder(0);
		cell.setColspan(2);
		cell.setPaddingTop(10);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("This letter is a notification of meal benefits for the child(ren) listed below:", generalFont));
		cell.setBorder(0);
		cell.setColspan(2);
		cell.setPaddingTop(5);
		cell.setPaddingBottom(5);
		table.addCell(cell);
		SchoolYear schoolYear = schoolYearRepository.findByMealSchoolSchoolIdAndSchoolYear(householdApplicationForFRM.getMealSchoolId(), householdApplicationForFRM.getSchoolYear());
		int srNo = 1;
		for(HouseholdAppOtherInfo householdAppOtherInfo : studentList){
			cell = new PdfPCell(new Phrase(String.valueOf(srNo)+"."+" "+householdAppOtherInfo.getLname()+", "
					+householdAppOtherInfo.getFname(), generalFont));
			cell.setBorder(0);
			cell.setColspan(2);
			cell.setPaddingBottom(3);
			cell.setPaddingLeft(25);
			table.addCell(cell);
			srNo++;
		}
		cell = new PdfPCell(new Phrase("Your child(ren) has been:", generalFont));
		cell.setBorder(0);
		cell.setColspan(2);
		cell.setPaddingTop(5);
		table.addCell(cell);
		Chunk apprv = new Chunk("Approved", boldFont);
		apprv.setUnderline(0.1f, -2f); //0.1 thick, -2 y-location
		cell = new PdfPCell(new Phrase(apprv));
		cell.setBorder(0);
		cell.setPaddingTop(5);
		cell.setPaddingLeft(25);
		table.addCell(cell);
		Chunk denie = new Chunk("Denied", boldFont);
		denie.setUnderline(0.1f, -2f); //0.1 thick, -2 y-location
		cell = new PdfPCell(new Phrase(denie));
		cell.setBorder(0);
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
		int i = 1;
		for(HouseholdAppDeclinedReason declineReason : householdApplicationForFRM.getDeclinedReasonList()){
			switch (i){
			case 1 : cell = buildCheckboxWithText(isFreeMeals != null && isFreeMeals ? checkedBoxImage : checkBoxImage, "Approved for free meals");
					cell.setPaddingTop(5);
					cell.setPaddingLeft(25); table.addCell(cell); break;
			case 2 : cell = buildCheckboxWithText(isFreeMeals != null && !isFreeMeals ? checkedBoxImage : checkBoxImage, "Approved for reduced-price meals at ");
					cell.setPaddingTop(5);
					cell.setPaddingLeft(25); table.addCell(cell); break;
			case 3 : cell = new PdfPCell(new Phrase(schoolYear.getLunchReducedCents()+" cents for lunch and "+schoolYear.getBreakfastReducedCents()+" cents for breakfast", generalFont));
					cell.setBorder(0);
					cell.setPaddingTop(5);
					cell.setPaddingLeft(25); table.addCell(cell); break;
			default : cell = new PdfPCell(new Phrase("", generalFont));
					cell.setBorder(0);
					cell.setPaddingTop(5); table.addCell(cell); break;
			}
			
			cell = buildCheckboxWithText((isFreeMeals==null && declineReason.getIsApplicable() != null && declineReason.getIsApplicable())
					? checkedBoxImage: checkBoxImage, declineReason.getName());
			cell.setPaddingTop(5); 
			table.addCell(cell);
			if(declineReason.getDescription() != null && !declineReason.getDescription().trim().isEmpty()){
				cell = new PdfPCell(new Phrase("", generalFont));
				cell.setBorder(0);
				cell.setPaddingTop(2); 
				table.addCell(cell);
				cell = new PdfPCell(new Phrase("Descriptions: "+declineReason.getDescription(), generalFont));
				cell.setBorder(0);
				cell.setPaddingTop(2);
				table.addCell(cell);
			}
			i++;
		}
		MealSchool mealSchool = schoolYear.getMealSchool();
		Paragraph paragraph = new Paragraph();
		paragraph.add(new Chunk(principalTxt+" ", smallerFont));
		paragraph.add(new Chunk((mealSchool.getPrincipalName() != null ? mealSchool.getPrincipalName() : "")+" "+ 
				(mealSchool.getPrincipalPhone() != null ? mealSchool.getPrincipalPhone() : "")+
				", "+(mealSchool.getPrincipalAddress() != null ? mealSchool.getPrincipalAddress() : "")
				+" "+(mealSchool.getPrincipalEmail() != null ? mealSchool.getPrincipalEmail() : ""), smallerBoldFont));
		cell = new PdfPCell(paragraph);
		cell.setBorder(0);
		cell.setColspan(2);
		cell.setPaddingTop(5);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase(foodManagerTxt, smallerFont));
		cell.setBorder(0);
		cell.setPaddingTop(5);
		cell.setColspan(2);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("Sincerely,", smallerFont));
		cell.setBorder(0);
		cell.setPaddingTop(10);
		cell.setColspan(2);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase((mealSchool.getContactPName() != null ? mealSchool.getContactPName() : ""), smallerBoldFont));
		cell.setBorder(0);
		cell.setColspan(2);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase((mealSchool.getContactPPhone() != null ? mealSchool.getContactPPhone() : "")+
				" "+(mealSchool.getContactPEmail() != null ? mealSchool.getContactPEmail() : ""), smallerBoldFont));
		cell.setBorder(0);
		cell.setColspan(2);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase((mealSchool.getContactPAddress() != null ? mealSchool.getContactPAddress() : ""), smallerBoldFont));
		cell.setBorder(0);
		cell.setColspan(2);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase(footer1, smallerFooterFont));
		cell.setBorder(0);
		cell.setColspan(2);
		cell.setPaddingTop(10);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase(footer2, smallerFooterFont));
		cell.setBorder(0);
		cell.setPaddingTop(6);
		cell.setColspan(2);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase(footer3, smallerFooterFont));
		cell.setBorder(0);
		cell.setPaddingTop(6);
		cell.setColspan(2);
		table.addCell(cell);
		table.setWidthPercentage(100);
		return table;
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
