package com.mealManage.util;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.mealManage.domain.FreeMealEligSurveyEmailReq;
import com.mealManage.domain.MealReminderRequest;
import com.mealManage.domain.NotificationRequest;
import com.mealManage.domain.ParentsNotificationRequest;
import com.mealManage.domain.PublishedMenuNotifReq;
import com.mealManage.domain.StatusUpdateNotificationReq;
import com.mealManage.domain.SupportUserNotificationReq;

/**This util class used for send the notification**/
@Component
public class SendNotificationUtil {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private RestTemplate restTemplate;
	@Value("${mealOrder.update.notification.url}")
	private String mealOrderNotificationURL;
	@Value("${school.reg.email.url}")
	private String notificationURL;
	@Value("${email.notification.url}")
	private String notificationParentURL;
	@Value("${payment.status.notification.url}")
	private String paymentStatusNotificationURL;
	@Value("${lunch.order.reminder}")
	private String lunchOrderReminder;
	@Value("${survey.email.notification}")
	private String surveyEmailUrl;
	@Value("${publish.menu.email.notif}")
	private String publishedMenuNotiURL;
	@Value("${menu.pdf.email.url}")
	private String menuOrderedPdfnotificationURL;
	@Value("${menu.pdf.email.cancel.url}")
	private String menuOrderedPdfnotificationCancelURL;
	@Value("${support.email.notification}")
	private String supportNotificationURL;
	@Value("${datasync.email.notification}")
	private String dataSyncEmailURL;
	@Value("${stripe.setup.email.notification}")
	private String stripeAccSetupEmailURL;
	@Value("${stripe.setup.status.notification}")
	private String stripeAccSetupStatus;
 	@Value("${payment.receipt.notification}")
 	private String paymentReceiptNotifURL;
 	@Value("${caterer.report.notification}")
 	private String catererNotifURL;
 	@Value("${household.application.apprvDecline}")
 	private String applicationApprvDecline;
 	@Value("${household.application.apprvDeclineV2}")
 	private String applicationApprvDeclineV2;
 	@Value("${demo.requestEmail.url}")
 	private String demoRequestEmail;
 	@Value("${household.application.emailUrl}")
 	private String householdAppEmailUrl;
 	@Value("${support.self.notification}")
 	private String supportSelfEmailUrl;
 	@Value("${request.self.notification}")
 	private String requestSelfEmailUrl;
 	@Value("${event.publish.notification}")
 	private String eventPublishNotifUrl;

 	@Value("${district.partner.token}")
 	private String distPartnerToken;
	
	/**This method used for send the notification to the parent user when any lunch meal item changed by caterer**/
	@Async
	public void mealChangeNotificationToParent(Map<String, List<StatusUpdateNotificationReq>> notificationRequest){
		/**Call API for send the notification regarding meal item change to parent user**/
		restTemplate.postForObject(mealOrderNotificationURL, notificationRequest, String.class);
		logger.info("Notification to the parent regarding meal item change has been sent successfully to the relevant parent user.");
	}
	
	/**This method used for send the notification to the school admin user for account activation**/
	@Async
	public void schoolUserAccActivationNotification(NotificationRequest notificationRequest){
		/**Call API to send the notification**/
		restTemplate.postForObject(notificationURL, notificationRequest, String.class);
		logger.info("Notification to the school admin user has been sent successfully for their account activation.");
	}
	
	/**This method used for send the notification to the parent user for their account activation**/
	@Async
	public void parentUserAccActivationNotification(NotificationRequest notificationRequest){
		/**Call API for send the notification**/
		restTemplate.postForObject(notificationParentURL, notificationRequest, String.class);
		logger.info("Notification to the Parent user has been sent successfully for their account activation.");
	}
	
	/**This method used for send the notification to the parent user regarding pending payment reminder**/
	@Async
	public void parentUserReminderNotification(ParentsNotificationRequest parentsNotificationRequest, 
			Map<String, List<StatusUpdateNotificationReq>> notificationRequest){
		/**Call API for send the notification regarding payment status reminder**/
		if(parentsNotificationRequest.getNotificationType().equalsIgnoreCase("PaymentStatus"))
			restTemplate.postForObject(paymentStatusNotificationURL, notificationRequest, String.class);
		logger.info("Notification to the parent user has been sent successfully regarding reminder of pending Payment Status");
	}
	
	/**This method used for send the notification to the parent user regarding lunch order reminder**/
	@Async
	public void lunchReminder(MealReminderRequest mealReminderRequest){
		/**Call API for send the notification regarding lunch reminder to parents**/
		restTemplate.postForObject(lunchOrderReminder, mealReminderRequest, String.class);
		logger.info("Notification to the parent user has been sent successfully regarding reminder of Lunch order");
	}
	
	/**This method used for send the notification to the school admin users for the free meal eligibility request**/
	@Async
	public void freeMealEligMail(Map<String, List<FreeMealEligSurveyEmailReq>> notificationRequest){
		/**Call API for send the notification regarding free meal eligibility to admin users**/
		restTemplate.postForObject(surveyEmailUrl, notificationRequest, String.class);
		logger.info("Notification to the school admin users has been sent successfully regarding free meal eligibility request");
	}
	
	/**This method used for send the notification to the parent users for the published menu reminder**/
	@Async
	public void sendPublishedMenuReminder(Map<String, List<PublishedMenuNotifReq>> menuPublishReminderReq){
		/**Call API for send the notification regarding menu published to parent users**/
		restTemplate.postForObject(publishedMenuNotiURL, menuPublishReminderReq, String.class);
		logger.info("Notification to the parent users has been sent successfully regarding menu published");
	}
	
	/**This method used for send the notification to the parent users with the ordered menu pdf file link**/
	@Async
	public void sendMenuOrderedPdf(NotificationRequest notificationRequest){
		/**Call API for send the notification regarding menu published to parent users**/
		restTemplate.postForObject(menuOrderedPdfnotificationURL, notificationRequest, String.class);
		logger.info("Notification to the parent users has been sent successfully regarding ordered menu");
	}
	
	/**This method used for send the notification to the parent users with the ordered menu pdf file link to update the cancellation**/
	@Async
	public void sendMenuOrderedPdfCancellation(NotificationRequest notificationRequest){
		/**Call API for send the notification regarding menu published to parent users**/
		restTemplate.postForObject(menuOrderedPdfnotificationCancelURL, notificationRequest, String.class);
		logger.info("Notification to the parent users has been sent successfully regarding menu item order cancellation");
	}
	
	/**This method used for send the notification to the school admin users & MM admin user regarding issue faced by parent user**/
	@Async
	public void supportEmailSend(Map<String, List<SupportUserNotificationReq>> notificationRequest){
		/**Call API for send the notification regarding free meal eligibility to admin users**/
		restTemplate.postForObject(supportNotificationURL, notificationRequest, String.class);
		logger.info("Notification to the school admin users has been sent successfully regarding Support message.");
	}
	
	/**This method used for send the notification to the admin users with MealManage DataSync process status**/
	@Async
	public void dataSyncProcessStatus(Map<String, String> notificationRequest){
		/**Call API for send the notification regarding Data Sync process status**/
		restTemplate.postForObject(dataSyncEmailURL, notificationRequest, String.class);
		logger.info("Notification to the admin users has been sent successfully regarding Data Sync process status.");
	}
	
	/**This method used for send the notification to the admin user for stripe account setup**/
	@Async
	public void stripeAccSetupReminder(Map<String, String> notificationRequest){
		/**Call API for send the notification regarding Data Sync process status**/
		restTemplate.postForObject(stripeAccSetupEmailURL, notificationRequest, String.class);
		logger.info("Notification to the admin user has been sent successfully regarding Stripe account setup.");
	}
	
	/**This method used for send the notification to the admin user for stripe account setup status**/
	@Async
	public void stripeAccSetupStatus(Map<String, String> notificationRequest){
		/**Call API for send the notification regarding Data Sync process status**/
		restTemplate.postForObject(stripeAccSetupStatus, notificationRequest, String.class);
		logger.info("Notification to the admin user has been sent successfully regarding Stripe account setup status.");
	}
	
	/**This method used for send the notification to the parent regarding payment receipt**/
	@Async
	public void paymentReceiptNotif(Map<String, String> notificationRequest){
		/**Call API for send the notification regarding payment receipt**/
		restTemplate.postForObject(paymentReceiptNotifURL, notificationRequest, String.class);
		logger.info("Notification to the parent user has been sent successfully regarding payment receipt.");
	}
	
	/**This method used for send the notification to the Caterer regarding Caterer Report**/
	@Async
	public void catererReportNotif(Map<String, String> notificationRequest){
		/**Call API for send the notification regarding Caterer Report**/
		restTemplate.postForObject(catererNotifURL, notificationRequest, String.class);
		logger.info("Notification to the Caterer user has been sent successfully regarding Caterer Report.");
	}
	
	/**This method used for send the notification to the parent regarding application status update for eligibility**/
	@Async
	public void aprvDeclineApplicationEmail(Map<String, String> notificationRequest){
		/**Call API for send the notification regarding application status**/
		restTemplate.postForObject(applicationApprvDecline, notificationRequest, String.class);
		logger.info("Notification to the parent user has been sent successfully regarding payment receipt.");
	}
	
	/**This method used for send the notification to the parent regarding application status update for eligibility**/
	@Async
	public void aprvDeclineApplicationEmailV2(Map<String, String> notificationRequest){
		/**Call API for send the notification regarding application status**/
		restTemplate.postForObject(applicationApprvDeclineV2, notificationRequest, String.class);
		logger.info("Notification to the parent user has been sent successfully regarding household app status change.");
	}
	
	/**This method used for send the notification to the admin user when household application submit by parent**/
	@Async
	public void householdAppEmail(Map<String, String> notificationRequest){
		/**Call API for send the notification regarding household application**/
		restTemplate.postForObject(householdAppEmailUrl, notificationRequest, String.class);
		logger.info("Notification to the admin users has been sent successfully regarding household application.");
	}
	
	/**This method used for send the notification to the MM suport regarding demo request submit**/
	@Async
	public void sendDemoReqEmail(Map<String, String> notificationRequest){
		/**Call API for send the notification regarding demo request**/
		restTemplate.postForObject(demoRequestEmail, notificationRequest, String.class);
		logger.info("Notification to the MM suport email has been sent successfully regarding demo request.");
	}
	
	/**This method used for send the automated support notification**/
	@Async
	public void selfSupportNotification(Map<String, String> selfNotifReq){
		/**call API for send the support automated notification**/
		restTemplate.postForObject(supportSelfEmailUrl, selfNotifReq, String.class);
		logger.info("Notification to the user sent automatically while support request ticket created");
	}
	
	/**This method used for send the automated request acknowledge notification**/
	@Async
	public void selfRequestNotification(Map<String, String> selfNotifReq){
		/**call API for send the support automated notification**/
		restTemplate.postForObject(requestSelfEmailUrl, selfNotifReq, String.class);
		logger.info("Notification to the user sent automatically while request ticket created");
	}
	
	/**This method used for send the notification to the parent users when event publish**/
	@Async
	public void sendEmailWhenEventPublish(Map<String, Object> notificationReq){
		/**Call API for send the notification regarding event published to parent users**/
		restTemplate.postForObject(eventPublishNotifUrl, notificationReq, String.class);
		logger.info("Notification to the parent users has been sent successfully regarding Event publish");
	}
	
	/**This method used to call API for generate token of district partner **/
	public void distPartnerAPI(Long distId, String pId){
		/**Call API for generate token of district partner**/
		restTemplate.postForObject(distPartnerToken.replace("<<distId>>", distId.toString()).replace("<<pId>>", pId),null, String.class);
		logger.info("API successfully called for generate district partner token");
	}
}
