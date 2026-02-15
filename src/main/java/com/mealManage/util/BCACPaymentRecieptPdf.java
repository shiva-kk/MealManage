package com.mealManage.util;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.html.WebColors;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.mealManage.mealmodel.packages.PackageSubscriptionsTrx;
import com.mealManage.mealmodel.packages.SchoolPackage;
import com.mealManage.mealmodel.packages.SubscriptionsTrxByStd;
import com.mealManage.mealmodel.repository.SchoolPackageRepo;
import com.mealManage.mealmodel.repository.StudentUserRepository;
import com.mealManage.mealmodel.transaction.PaymentType;
import com.mealManage.mealmodel.user.StudentUser;

/**This utility class used for the payment receipt building**/
@Component
public class BCACPaymentRecieptPdf {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
 	@Value("${amazon.s3.bucket}")
    private String amazonS3Bucketname;
 	@Autowired
 	private SendNotificationUtil sendNotificationUtil;
 	@Autowired
 	private SchoolPackageRepo packageRepo;
	@Autowired
	private DateUtilityV2 du;
 	@Autowired
 	private StudentUserRepository studentUserRepository;
	public static final  Font boldFont=FontFactory.getFont(FontFactory.HELVETICA, 8, Font.BOLD);
	public static final  Font boldWithUnderLineFont=FontFactory.getFont(FontFactory.COURIER, 9, Font.BOLD);
	public static final  Font generalFont=FontFactory.getFont(FontFactory.HELVETICA, 7);
	//public static final  Font footerFont=FontFactory.getFont(FontFactory.HELVETICA, 7);
	public static final  Font generalDateFont=FontFactory.getFont(FontFactory.HELVETICA, 8);
	private static SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
    /** The language code for the calendar */
    public static final String LANGUAGE = "en";
    private DecimalFormat df = new DecimalFormat("0.00");
	
	/**This method used for generate the BCAC payment receipt in pdf format**/
	@Async
	public void paymentReceiptGenerate(PackageSubscriptionsTrx pkgTrx, String currencySymbol) {		
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMddHHmmss");
		String pdfFilePath = "BCAC_PaymentReceipt_"+sdf1.format(new Date())+"_"+pkgTrx.getTrxId()+".pdf";
		String logoPath = "";
		if(pkgTrx.getMealSchool().getLogoLink() != null)
			logoPath = pkgTrx.getMealSchool().getLogoLink();
		else
			logoPath = "https://s3.amazonaws.com/"+amazonS3Bucketname+"/mealManageLogo.PNG";	
		Document document=new Document(PageSize.A4);// 40, 40, 40, 40);// For the page 8.5'' X 11'' dimension
		try{
			PdfWriter.getInstance(document, new FileOutputStream(pdfFilePath));
			document.open();
			document.add(createPaymentReceipt(pdfFilePath, logoPath, pkgTrx,currencySymbol));
			document.close();
			byte[] encodedBytes = Files.readAllBytes(Paths.get(pdfFilePath));
			byte[] encoded = Base64.encodeBase64(encodedBytes);
			String encodedString = new String(encoded);
			String parentUserEmail = pkgTrx.getParentUserEmails();
			Map<String, String> paymentReceiptMap = new HashMap<String, String>();
			
			paymentReceiptMap.put("userEmails", parentUserEmail);
			paymentReceiptMap.put("base64Data", encodedString);
			paymentReceiptMap.put("fileName", pdfFilePath);
			paymentReceiptMap.put("schoolName", pkgTrx.getMealSchool().getSchoolName());
			paymentReceiptMap.put("transactionType", "Package");
			paymentReceiptMap.put("adminEmail", pkgTrx.getMealSchool().getContactPEmail() != null ? 
					pkgTrx.getMealSchool().getContactPEmail() : "");
			sendNotificationUtil.paymentReceiptNotif(paymentReceiptMap);
		}catch (Exception e){
			logger.error("Error occurred during build pdf file. "+e.getMessage());
		}	finally{
			new File(pdfFilePath).delete();
		} 	
		//awsUtility.uploadFileToAWSS3Bucket(pdfFilePath, "MealMenu");
	}
	
	/**This method used for create the payment receipt**/
	private Element createPaymentReceipt(String pdfPath, String logoPath, PackageSubscriptionsTrx pkgTrx,String currencySymbol)throws Exception{
		PdfPTable mainTable = new PdfPTable(1);
		PdfPCell cell=new PdfPCell();
		cell.addElement(createMainTable(pdfPath, logoPath, pkgTrx,currencySymbol));
		mainTable.addCell(cell);
		mainTable.setWidthPercentage(100);
		return mainTable;
	}
	
	private Element createMainTable(String pdfPath, String logoPath, PackageSubscriptionsTrx pkgTrx,String currencySymbol) throws Exception{
		PdfPTable mainTable = new PdfPTable(1);
		PdfPCell cell=new PdfPCell(new Phrase("PACKAGE PAYMENT RECEIPT", boldWithUnderLineFont));
		cell.setBorder(0);
		cell.setBackgroundColor(WebColors.getRGBColor("#D3D3D3"));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		mainTable.addCell(cell);
		cell.addElement(createReceiptHeaderTable(pdfPath, logoPath, pkgTrx));
		mainTable.addCell(cell);
		PdfPCell tableView = new PdfPCell();
		tableView.addElement(createPaymentInfoTable(pkgTrx,currencySymbol));
		tableView.setColspan(4);
		tableView.setRowspan(1);
		tableView.setBorder(0);
		mainTable.addCell(tableView);
		
		tableView = new PdfPCell(new Phrase("PAID BY: "+(pkgTrx.getPaymentType().toString() != null ? pkgTrx.getPaymentType().toString() : "").toUpperCase(), generalFont));
		tableView.setBorder(0);
		mainTable.addCell(tableView);
		
		if(pkgTrx.getPaymentType().toString().equalsIgnoreCase(PaymentType.Check.toString())){
			tableView = new PdfPCell(new Phrase("CHECK NO.: "+(pkgTrx.getCheckNumb() != null ? pkgTrx.getCheckNumb() : ""), generalFont));
			tableView.setBorder(0);
			mainTable.addCell(tableView);
		}else if(pkgTrx.getPaymentType().toString().equalsIgnoreCase(PaymentType.CreditCard.toString())){
			tableView = new PdfPCell(new Phrase("TRANSACTION NO.: "+(pkgTrx.getCheckNumb() != null ? pkgTrx.getCheckNumb() : ""), generalFont));
			tableView.setBorder(0);
			mainTable.addCell(tableView);
		}else if(pkgTrx.getPaymentType().toString().equalsIgnoreCase(PaymentType.Online.toString())){
			tableView = new PdfPCell(new Phrase("TRANSFER ID#: "+(pkgTrx.getTransferId() != null ? pkgTrx.getTransferId() : ""), generalFont));
			tableView.setBorder(0);
			mainTable.addCell(tableView);
		}
		
		mainTable.setWidthPercentage(100);
		return mainTable;
	}
	
	/**This method used for the payment receipt content table**/
	private Element createReceiptHeaderTable(String pdfPath, String logoPath, PackageSubscriptionsTrx pkgTrx) throws Exception{
		PdfPTable mainTab = new PdfPTable(2);
		PdfPCell first = new PdfPCell();	
		first.setBorder(0);		
		first.addElement(createReceiptLeftHeader(logoPath, pkgTrx));
		mainTab.addCell(first);	

		first = new PdfPCell();
		first.setBorder(0);		
		first.addElement(createReceiptRightHeader(pkgTrx));
		mainTab.addCell(first);	
		mainTab.setWidthPercentage(100);	
		return mainTab;
	}
	
	/**This method used for the left side header table creation in payment receipt*/
	private Element createReceiptLeftHeader(String logoPath, PackageSubscriptionsTrx pkgTrx) throws Exception{
		PdfPTable schTable = new PdfPTable(1);
		Image image = Image.getInstance(logoPath);
    	image.scaleAbsolute(28f, 28f);
    	image.setAlignment(Image.ALIGN_LEFT);
		//for first row
		PdfPCell first = new PdfPCell();	   
		first.addElement(image);
		first.setBorder(0);
		first.setPaddingLeft(100);
		schTable.addCell(first);
		
		first = new PdfPCell(new Phrase(pkgTrx.getMealSchool().getSchoolName().toUpperCase(), generalFont));
		first.setBorder(0);
		first.setPaddingLeft(50);
		schTable.addCell(first);
		schTable.setWidthPercentage(100);
		return schTable;
	}
	
	/**This method used for the right side header table creation in payment receipt*/
	private Element createReceiptRightHeader(PackageSubscriptionsTrx pkgTrx){
		PdfPTable schTable = new PdfPTable(1);
		PdfPCell schCell;
		schCell = new PdfPCell(new Phrase("", generalFont));
		schCell.setBorder(0);
		schTable.addCell(schCell);
		//SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a");
		schCell = new PdfPCell(new Phrase("DATE: "+du.formatDateToString(
				(pkgTrx.getModifiedOn() != null ? pkgTrx.getModifiedOn() : pkgTrx.getCreatedOn()), "dd-MMM-yyyy hh:mm:ss a", 
				pkgTrx.getMealSchool().getSchoolTimezone().toString()).toUpperCase(), generalFont));
		schCell.setBorder(0);
		schCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		schCell.setPaddingLeft(140);
		schTable.addCell(schCell);
		schCell = new PdfPCell(new Phrase("INVOICE# "+String.format("%012d", pkgTrx.getTrxId()), generalFont));
		schCell.setBorder(0);
		schCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		schCell.setPaddingLeft(140);
		schTable.addCell(schCell);
		schTable.setWidthPercentage(100);
		return schTable;
	}
	
	/**This method used for create the payment info table**/
	private Element createPaymentInfoTable(PackageSubscriptionsTrx pkgTrx, String currencySymbol){
		PdfPTable pdfPTable = new PdfPTable(new float[] { 20, 40, 50, 50, 35});
		PdfPCell calCell;
		List<String> headerTable = null;
		Map<Long, String> packageNameById = new HashMap<Long, String>();
		Map<Long, String> stdNameById = new HashMap<Long, String>();
		headerTable = Arrays.asList("SR. NO.","STUDENT NAME","PACKAGE NAME","DATE","AMOUNT ("+currencySymbol+")");
		for(String day : headerTable){
    		calCell = new PdfPCell(new Phrase(day, boldFont));
    		calCell.setBackgroundColor(WebColors.getRGBColor("#EDEBED"));
    		calCell.setHorizontalAlignment(Element.ALIGN_CENTER);
    		pdfPTable.addCell(calCell);
    	}
    	int srNo = 1;
    	for(SubscriptionsTrxByStd subByStd : pkgTrx.getSubscriptionsTrxByStds()){
    		calCell = new PdfPCell(new Phrase(String.valueOf(srNo), generalFont));
    		calCell.setHorizontalAlignment(Element.ALIGN_CENTER);
    		pdfPTable.addCell(calCell);
			if (packageNameById.get(subByStd.getSchoolPackage().getPackageId()) == null) {
				SchoolPackage pkg = packageRepo.findOne(subByStd.getSchoolPackage().getPackageId());
				packageNameById.put(pkg.getPackageId(), pkg.getPackageName().toUpperCase());
			}
			if (stdNameById.get(subByStd.getStudentUser().getUserId()) == null) {
				StudentUser su = studentUserRepository.findOne(subByStd.getStudentUser().getUserId());
				stdNameById.put(su.getUserId(), ((su.getLastName()+", "+su.getFirstName()).toUpperCase()));
			}
			calCell = new PdfPCell(new Phrase((stdNameById.get(subByStd.getStudentUser().getUserId())), generalFont));
	        pdfPTable.addCell(calCell);
    		calCell = new PdfPCell(new Phrase(packageNameById.get(subByStd.getSchoolPackage().getPackageId()), generalFont));
        	pdfPTable.addCell(calCell);
        	calCell = new PdfPCell(new Phrase((subByStd.getStartDate() != null ? (sdf.format(subByStd.getStartDate())+" - "+sdf.format(subByStd.getEndDate())) : ""), generalFont));
        	pdfPTable.addCell(calCell);
    		calCell = new PdfPCell(new Phrase(df.format(subByStd.getPaidAmt()), generalFont));
    		calCell.setHorizontalAlignment(Element.ALIGN_CENTER);
    		pdfPTable.addCell(calCell);
    		srNo = srNo+1;
    	}
    	
    	for(int i=0; i<4; i++){
    		calCell = new PdfPCell(new Phrase("", generalFont));
    		calCell.setColspan(4);
    		calCell.setBorder(0);
    		pdfPTable.addCell(calCell);
    		calCell = new PdfPCell(new Phrase("0.00", generalFont));
    		calCell.setHorizontalAlignment(Element.ALIGN_CENTER);
    		pdfPTable.addCell(calCell);
    	}
    	if(pkgTrx.getPaymentType().toString().equalsIgnoreCase(PaymentType.Online.toString())){
    		calCell = new PdfPCell(new Phrase("TRANSACTION FEE", boldFont));
    		calCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        	calCell.setColspan(4);
        	calCell.setBorder(0);
        	pdfPTable.addCell(calCell);
        	calCell = new PdfPCell(new Phrase(currencySymbol+df.format((pkgTrx.getTransactionFees() != null ? pkgTrx.getTransactionFees() : 0.0)
        			+ (pkgTrx.getAppFeeAmount() != null ? pkgTrx.getAppFeeAmount() : 0.0)), generalFont));
    		calCell.setHorizontalAlignment(Element.ALIGN_CENTER);
    		pdfPTable.addCell(calCell);
    	}
    	
    	calCell = new PdfPCell(new Phrase("TOTAL AMOUNT ", boldFont));
    	calCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
    	calCell.setColspan(4);
    	calCell.setBorder(0);
    	pdfPTable.addCell(calCell);
    	calCell = new PdfPCell(new Phrase(currencySymbol+df.format(pkgTrx.getTotalPaidAmt()
    			+(pkgTrx.getTransactionFees() != null ? pkgTrx.getTransactionFees() : 0)
    					+(pkgTrx.getAppFeeAmount() != null ? pkgTrx.getAppFeeAmount() : 0)), boldFont));
		calCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		calCell.setBackgroundColor(WebColors.getRGBColor("#EDEBED"));
		pdfPTable.addCell(calCell);
		pdfPTable.setWidthPercentage(100);
		return pdfPTable;
	}
	
}
