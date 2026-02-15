package com.mealManage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.aws.messaging.config.annotation.EnableSqs;
import org.springframework.context.annotation.Import;

import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.config.EnableIntegration;
import springfox.documentation.spring.data.rest.configuration.SpringDataRestConfiguration;

/** Spring Boot Application starter **/
@SpringBootApplication
@Import(SpringDataRestConfiguration.class)
@EnableSqs
@EnableCaching
@IntegrationComponentScan
@EnableIntegration
public class Application extends SpringBootServletInitializer {
	
	/*static final Counter requests = Counter.build()
		     .name("requests_total").help("Total requests.").register();*/
	/*static final Summary receivedBytes = Summary.build()
		     .name("requests_size_bytes").help("Request size in bytes.").register();
	static final Summary requestLatency = Summary.build()
		     .name("requests_latency_seconds").help("Request latency in seconds.").register();*/



	public static void main(String[] args) throws Exception {
		//HTTPServer server = new HTTPServer(8080);
		//SpringApplication.run(applicationClass, args);
		/*try {
            HTTPServer server = new HTTPServer(8296);
        } catch (IOException e) {
            System.out.println("Failed to start metrics endpoint.");
            e.printStackTrace();
        }*/
		SpringApplication app = new SpringApplication(applicationClass);
		app.setAdditionalProfiles(args[0]);
		//requests.inc();
		app.run(args);
	}
	
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(applicationClass);
    }

    private static Class<Application> applicationClass = Application.class;
}
