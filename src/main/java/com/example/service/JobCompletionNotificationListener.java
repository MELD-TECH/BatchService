package com.example.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.example.model.Person;

@Component
public class JobCompletionNotificationListener implements JobExecutionListener {

	private JdbcTemplate jdbcTemplate;
	
	Logger log = LoggerFactory.getLogger(JobCompletionNotificationListener.class);
	
	public JobCompletionNotificationListener(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	@Override
	public void afterJob(JobExecution jobExecution) {
		if(jobExecution.getStatus() == BatchStatus.COMPLETED) {
			
			log.info("Batch Job has completed...");
			
			jdbcTemplate.query("select firstname, lastname from person", new DataClassRowMapper<>(Person.class))
			.forEach(person -> log.info("Found <{{}}> in the database", person));
		}
	}
}
