package com.mealManage;

import java.util.concurrent.Executor;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsynchExecutionConfig implements AsyncConfigurer{
	
	    /* @Bean
	     public MealMenuPdfUtility mealMenuPdf() {
	         return new MealMenuPdfUtility();
	     }*/

	     @Override
	     public Executor getAsyncExecutor() {
	         ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
	         executor.setCorePoolSize(7);
	         executor.setMaxPoolSize(42);
	         executor.setQueueCapacity(11);
	         executor.setThreadNamePrefix("MyExecutor-");
	         executor.initialize();
	         return executor;
	     }

		@Override
		public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
			// TODO Auto-generated method stub
			return null;
		}

	     /*@Override
	     public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
	         return null;
	     }*/
	 

}
