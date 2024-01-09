package com.example.service;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import com.example.model.Person;

@Configuration
public class PersonConfiguration {

	@Bean
	public FlatFileItemReader<Person> reader(){
		
		return new FlatFileItemReaderBuilder<Person>()
				.name("personReader")
				.resource(new ClassPathResource("sample-data.csv"))
				.delimited()
				.names("lastname", "firstname")
				.strict(true)
				.targetType(Person.class)
				.build();
	}
	
	@Bean
	public JdbcBatchItemWriter<Person> writer(DataSource datasource){
		
		return new JdbcBatchItemWriterBuilder<Person>()
				.sql("insert into person (firstname, lastname) values (:firstname, :lastname) ")
				.dataSource(datasource)
				.beanMapped()
				.build();
	}
	
	@Bean
	public PersonProcessor processor() {
		return new PersonProcessor();
	}
	
	@Bean
	public Job userJobs(JobRepository jobRepository, Step step1, JobCompletionNotificationListener listener) {
		
		return new JobBuilder("userJobsImport", jobRepository)
				.incrementer(new RunIdIncrementer())
				.listener(listener)
				.start(step1)				
				.build();
				
	}
	
	@Bean
	public Step step1(JobRepository jobRepository, DataSourceTransactionManager transaction, 
			FlatFileItemReader<Person> reader, PersonProcessor processor, JdbcBatchItemWriter<Person> writer) {
		
		return new StepBuilder("step1", jobRepository)
				.<Person, Person>chunk(15, transaction)
				.reader(reader)
				.processor(processor)
				.writer(writer)
				.build();
		
	}
	
	@Bean
	public DataSourceTransactionManager dataSourceTransactionManager(DataSource dataSource) {
		DataSourceTransactionManager dd = new DataSourceTransactionManager();
		dd.setDataSource(dataSource);
		return dd;
	}
}
