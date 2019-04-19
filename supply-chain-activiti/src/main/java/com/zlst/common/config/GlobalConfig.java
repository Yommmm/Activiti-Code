package com.zlst.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.zlst.quartz.service.TaskService;

@Configuration
public class GlobalConfig {

	@Bean(name = "QuartzTaskService")
	public TaskService getTaskService() {
		return new TaskService();
	}
	
}
