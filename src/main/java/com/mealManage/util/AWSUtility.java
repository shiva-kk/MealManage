package com.mealManage.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.amazonaws.SdkClientException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;

@Component
@PropertySource("classpath:application.properties")
public class AWSUtility {
	
		@Value("${amazon.s3.endpoint}")
		private String amazonS3Endpoint;
	
	 	@Value("${amazon.s3.bucket}")
	    private String amazonS3Bucketname;
	 
	    @Value("${amazon.s3.accesskey}")
	    private String amazonS3AccessKey;
	 
	    @Value("${amazon.s3.secretkey}")
	    private String amazonS3SecretKey;
	    
	    @Value("${amazon.s3.schoolLogo.folder}")
	    private String mealSchoolLogoFolder;
	    
	    @Value("${amazon.s3.mealsExcelImport.folder}")
	    private String menuExcelImportFolder;
	    
	    @Value("${amazon.s3.mealMenuPdf.folder}")
	    private String mealMenuPdfFolder;
	    
	    @Value("${amazon.s3.orderedMenuPdf.folder}")
	    private String orderedMenuPdfFolder;
	    
	    @Value("${amazon.s3.schoolReport.folder}")
	    private String schoolReportFolder;
	    
	    @Value("${amazon.s3.catererReport.folder}")
	    private String catererReportFolder;
	    @Value("${amazon.s3.studentBkp.folder}")
	    private String studentBkpFiles;
	    @Value("${amazon.s3.schoolPdf.folder}")
	    private String schoolPdfFolder;
	    @Value("${amazon.s3.benefitsNotice.folder}")
	    private String benefitsNoticeFolder;
	    @Value("${amazon.s3.stdCertImport.folder}")
	    private String stdCertFileFolder;
		@Value("${amazon.s3.menuItemImage.folder}")
		private String menuItemImageFolder;
		
		private final Logger logger = LoggerFactory.getLogger(this.getClass());
	    
		/*@SuppressWarnings("deprecation")
		public String uploadFileToS3Bucket(String filePath, String filepurpose) throws Exception
		{
			String fileName = "";
			String finalFilePath = "";
			switch(filepurpose){
				case "MealSchoolLogo" : fileName = mealSchoolLogoFolder+new File(filePath).getName(); break;
				case "OrderedMenu" : fileName = orderedMenuPdfFolder+new File(filePath).getName(); break;
				case "MealsExcelFile" : fileName = menuExcelImportFolder+new File(filePath).getName();break;
				case "MealMenu" : fileName = mealMenuPdfFolder+new File(filePath).getName();break;
			}
			
			finalFilePath = amazonS3Endpoint+"/"+amazonS3Bucketname+"/"+fileName; 
			try
			{
				BasicAWSCredentials awsCreds = new BasicAWSCredentials(amazonS3AccessKey, amazonS3SecretKey);
				AmazonS3 s3 = new AmazonS3Client(awsCreds).withRegion(Region.getRegion(Regions.US_EAST_1));
				s3.putObject(new PutObjectRequest(amazonS3Bucketname, fileName,new File(filePath)).
						withCannedAcl(CannedAccessControlList.PublicRead));
				}catch(Exception e){
					throw new Exception("Error while uploading file to S3 Bucket");					
		 	}
			finally{
				new File(filePath).delete();
			}
	    	return finalFilePath;	    	
		}*/
		
		@SuppressWarnings("deprecation")
		public void uploadFileToAWSS3Bucket(String filePath, String filepurpose) throws Exception
		{ 
			try
			{
				String fileName = "";
				switch(filepurpose){
					case "MealSchoolLogo" : fileName = mealSchoolLogoFolder+new File(filePath).getName(); break;
					case "OrderedMenu" : fileName = orderedMenuPdfFolder+new File(filePath).getName(); break;
					case "MealsExcelFile" : fileName = menuExcelImportFolder+new File(filePath).getName();break;
					case "MealMenu" : fileName = mealMenuPdfFolder+new File(filePath).getName();break;
					case "SchoolReport" : fileName = schoolReportFolder+new File(filePath).getName();break;
					case "CatererReport" : fileName = catererReportFolder+new File(filePath).getName();break;
					case "StudentBkp" : fileName = studentBkpFiles+new File(filePath).getName();break;
					case "schoolPdfFile" : fileName = schoolPdfFolder+new File(filePath).getName();break;
					case "benefitsNotice" : fileName = benefitsNoticeFolder+new File(filePath).getName();break;
					case "studentCertBkp" : fileName = stdCertFileFolder+new File(filePath).getName();break;
				}
				BasicAWSCredentials awsCreds = new BasicAWSCredentials(amazonS3AccessKey, amazonS3SecretKey);
				AmazonS3 s3 = new AmazonS3Client(awsCreds).withRegion(Region.getRegion(Regions.US_EAST_1));
				s3.putObject(new PutObjectRequest(amazonS3Bucketname, fileName,new File(filePath)).
						withCannedAcl(CannedAccessControlList.PublicRead));
				}catch(Exception e){
					logger.error("Error occurred while uploading file to S3 Bucket");;
					throw new Exception("Error while uploading file to S3 Bucket");					
		 	}
			finally{
				new File(filePath).delete();
			} 
			logger.info("File has been generated and uploaded to S3 bucket successfully");
		}
		
		@Async
		public void uploadMenuExcel(String filePath, String filepurpose) throws Exception{
			uploadFileToAWSS3Bucket(filePath, filepurpose);
		}
		
		@Async
		public void uploadSchoolLogo(String filePath, String filepurpose) throws Exception{
			uploadFileToAWSS3Bucket(filePath, filepurpose);
		}
		
		/*public String excelMenuFilelink(String fileName){
			String fileName1 = menuExcelImportFolder+new File(fileName).getName();
			String finalFilePath = amazonS3Endpoint+"/"+amazonS3Bucketname+"/"+fileName1; 
			return finalFilePath;	
		}
		
		public String schoolLogoLink(String fileName){
			String fileName1 = mealSchoolLogoFolder+new File(fileName).getName();
			String finalFilePath = amazonS3Endpoint+"/"+amazonS3Bucketname+"/"+fileName1; 
			return finalFilePath;	
		}
		
		public String schoolReportLink(String fileName){
			String fileName1 = schoolReportFolder+new File(fileName).getName();
			String finalFilePath = amazonS3Endpoint+"/"+amazonS3Bucketname+"/"+fileName1; 
			return finalFilePath;	
		}
		
		public String catererReportLink(String fileName){
			String fileName1 = catererReportFolder+new File(fileName).getName();
			String finalFilePath = amazonS3Endpoint+"/"+amazonS3Bucketname+"/"+fileName1; 
			return finalFilePath;	
		}
		
		public String studentBkpFileLink(String fileName){
			String fileName1 = studentBkpFiles+new File(fileName).getName();
			String finalFilePath = amazonS3Endpoint+"/"+amazonS3Bucketname+"/"+fileName1; 
			return finalFilePath;	
		}
		
		public String schoolPdfUploadFilePath(String fileName){
			String fileName1 = schoolPdfFolder+new File(fileName).getName();
			String finalFilePath = amazonS3Endpoint+"/"+amazonS3Bucketname+"/"+fileName1; 
			return finalFilePath;	
		}*/
		
		public String fileUploadPath(String fileName, String filePurpose){
			String fileName1 = "";
			switch (filePurpose) {
			case "excelMenuFilelink":
				fileName1 = menuExcelImportFolder+new File(fileName).getName();
				break;
			case "schoolLogoLink":
				fileName1 = mealSchoolLogoFolder+new File(fileName).getName();
				break;
			case "schoolReportLink":
				fileName1 = schoolReportFolder+new File(fileName).getName();
				break;
			case "catererReportLink":
				fileName1 = catererReportFolder+new File(fileName).getName();
				break;
			case "studentBkpFileLink":
				fileName1 = studentBkpFiles+new File(fileName).getName();
				break;
			case "schoolPdfUploadFilePath":
				fileName1 = schoolPdfFolder+new File(fileName).getName();
				break;
			case "noticeBenefits":
				fileName1 = benefitsNoticeFolder+new File(fileName).getName();
				break;
			case "studentCertBkpFileLink":
				fileName1 = stdCertFileFolder+new File(fileName).getName();
				break;
			}
			String finalFilePath = amazonS3Endpoint+"/"+amazonS3Bucketname+"/"+fileName1; 
			return finalFilePath;
		}
		
		@SuppressWarnings("deprecation")
		/**This method used for upload item image**/
		public String uploadImage(String base64,String fileName) throws Exception {
			File convFile = new File(fileName);
			String filePath = convFile.getAbsolutePath();
			String uploadedFilePath = null;
			try {
				byte[] imageByteArray = decodeImage(base64);
				FileOutputStream imageOutFile = new FileOutputStream(new File(fileName));
				imageOutFile.write(imageByteArray);
				BasicAWSCredentials awsCreds = new BasicAWSCredentials(amazonS3AccessKey, amazonS3SecretKey);
				AmazonS3 s3 = new AmazonS3Client(awsCreds).withRegion(Region.getRegion(Regions.US_EAST_1));
				/*PutObjectResult result = */s3.putObject(new PutObjectRequest(amazonS3Bucketname, menuItemImageFolder+fileName,new File(filePath)).
						withCannedAcl(CannedAccessControlList.PublicRead));
				imageOutFile.close();
				 uploadedFilePath = "https://"+amazonS3Bucketname+".s3.amazonaws.com/"+menuItemImageFolder+fileName;
			}  catch (IOException e) {
				logger.error("**** Exception while converting the base64 to image : " + e.getMessage());
				throw e;
			} catch (SdkClientException e) {
				logger.error("**** Exception while uploading file to S3 : " + e.getMessage());
				throw e;
			}
			finally{
				Files.deleteIfExists(new File(filePath).toPath());
			}
			return uploadedFilePath;
		}

		private static byte[] decodeImage(String imageDataString) {
			return Base64.decodeBase64(imageDataString);
		}
}
