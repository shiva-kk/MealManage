package com.mealManage;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sqs.AmazonSQSAsyncClient;

@Configuration
/**This config class used for setup the configuration of AWS SQS service**/
public class AWSSQSConfig {
	
	@Value("${amazon.s3.sqs.endpoint}")
	private String amazonS3Endpoint;
	@Value("${amazon.s3.accesskey}")
    private String amazonS3AccessKey; 
    @Value("${amazon.s3.secretkey}")
    private String amazonS3SecretKey;
	
	@SuppressWarnings("deprecation")
	@Bean
	@Primary
	public AmazonSQSAsyncClient amazonSQSAsyncClient() {
		AmazonSQSAsyncClient amazonSQSAsyncClient = new AmazonSQSAsyncClient(amazonAWSCredentials());
		if (!StringUtils.isEmpty(amazonS3Endpoint)) {
			amazonSQSAsyncClient.setEndpoint(amazonS3Endpoint);
		}
		return amazonSQSAsyncClient;

	}

	@Bean
	public AWSCredentials amazonAWSCredentials() {
		return new BasicAWSCredentials(amazonS3AccessKey, amazonS3SecretKey);
	}

}
