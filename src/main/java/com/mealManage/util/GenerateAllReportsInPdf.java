package com.mealManage.util;

import org.springframework.stereotype.Component;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.client.RestTemplate;
import com.mealManage.domain.ReportsAttachmentNotiReq;
import com.mealManage.mealmodel.meal.ItemTypeConstants;
import com.mealManage.response.SchoolMealReportResp;

/** This utility class used for build the all reports (i.e. School & Caterer)**/
@Component
public class GenerateAllReportsInPdf {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Value("${amazon.s3.bucket}")
    private String amazonS3Bucketname;
	
	@Autowired
	private AWSUtility awsUtility;
	@Value("${reports.email.notification}")
	private String reportsnotificationURL;
	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	private CatererPdfReportGeneration catererPdfReportGeneration;
	@Autowired
	private SchoolPdfReportGeneration schoolPdfReportGeneration;
	
	/**This method used for generate the pdf of School & Caterer report
	 * @throws Exception **/
    @Async
	public void exportAllReports(SchoolMealReportResp schoolMealReportResp, String logoLink, String schoolName, 
			Long mealSchoolId, String loggedUser, Map<String, Map<String, Integer>> catererMealMap, List<String> gradeNames, Boolean isItemized,
			ItemTypeConstants menuType,List<Integer> nonSchoolDays) throws Exception{
		String pdfFilePath = "SchoolReport_"+mealSchoolId+"_"+schoolMealReportResp.getStartDate().replace("-", "")+"_"+
			schoolMealReportResp.getEndDate().replace("-", "")+".pdf";
		HttpServletResponse response = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String schoolReportPdfLink = null;
		ReportsAttachmentNotiReq reportsAttachmentNotiReq = new ReportsAttachmentNotiReq();
		try
		{
			if(schoolMealReportResp != null && schoolMealReportResp.getStudentWithMeal() != null && schoolMealReportResp.getStudentWithMeal().size()>0){
				schoolPdfReportGeneration.exportSchoolPdfReport(schoolMealReportResp, response, pdfFilePath, mealSchoolId);
				schoolReportPdfLink = awsUtility.fileUploadPath(pdfFilePath, "schoolReportLink");
				awsUtility.uploadFileToAWSS3Bucket(pdfFilePath, "SchoolReport");
			}else
				schoolReportPdfLink = "No data available.";			
			String catererPdfFileName = "CatererReport_"+mealSchoolId+"_"+schoolMealReportResp.getStartDate().replace("-", "")+"_"+
					schoolMealReportResp.getEndDate().replace("-", "")+".pdf";
			catererPdfReportGeneration.exportCaterePdfReport(schoolName, gradeNames, sdf.parse(schoolMealReportResp.getStartDate()),
					response, logoLink, catererMealMap, sdf.parse(schoolMealReportResp.getEndDate()), catererPdfFileName, mealSchoolId, null, null, null, isItemized, menuType, schoolMealReportResp.getCountryCode(),"MM/dd/yyyy",nonSchoolDays);
			String catererReportPdfLink = awsUtility.fileUploadPath(catererPdfFileName, "catererReportLink");
			awsUtility.uploadFileToAWSS3Bucket(catererPdfFileName, "CatererReport");
			Map<String, String> reportsPdfLink = new HashMap<String, String>();
			if(loggedUser != null){
				reportsPdfLink.put("schoolReport", schoolReportPdfLink);
				reportsPdfLink.put("catererReport", catererReportPdfLink);
				reportsAttachmentNotiReq.setSchoolName(schoolName.toUpperCase());
				reportsAttachmentNotiReq.setStartDate(schoolMealReportResp.getStartDate());
				reportsAttachmentNotiReq.setEndDate(schoolMealReportResp.getEndDate());
				reportsAttachmentNotiReq.setAttachments(reportsPdfLink);
				reportsAttachmentNotiReq.setEmails(loggedUser);
				}
			if(reportsAttachmentNotiReq.getEmails() != null){
				/**Call API for send the notification**/
				restTemplate.postForObject(reportsnotificationURL, reportsAttachmentNotiReq, String.class);	
			}
		}catch (Exception e){
			logger.error("Error occurred during build pdf file of report. "+e.getMessage());
		}
	}
}
