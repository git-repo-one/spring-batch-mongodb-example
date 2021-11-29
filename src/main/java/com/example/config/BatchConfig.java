package com.example.config;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.example.domain.ReportConfig;
import com.example.processor.ReportConfigItemProcessor;
import com.example.reader.ReportConfigCursorItemReader;
import com.example.writer.ReportConfigItemWriter;

import lombok.extern.log4j.Log4j2;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.mongo.MongoLockProvider;

@EnableBatchProcessing
@Configuration
@Log4j2
public class BatchConfig {

	@Value("${jobA.cursorBatchSize}")
	private int cursorBatchSize;

	@Value("${jobA.pageSize}")
	private int pageSize;

	@Value("${jobA.chunkSize}")
	private int chunkSize;

	@Value("${jobA.threadPoolSize}")
	private int threadPoolSize;

	@Autowired
	private MongoTemplate mongoTemplate;

	private ThreadPoolTaskExecutor threadPoolTaskExecutor;

	@PostConstruct
	private void postConstruct() {
		log.debug("cursorBatchSize={}", cursorBatchSize);
		log.debug("pageSize={}", pageSize);
		log.debug("chunkSize={}", chunkSize);
		log.debug("threadPoolSize={}", threadPoolSize);
	}

	@PreDestroy
	public void preDestroy() {
		log.debug("inside preDestroy()");
		threadPoolTaskExecutor.destroy();
	}

	//1. Item Reader from MongoDB collection
	@Bean
	public ItemReader<ReportConfig> reader(){

		ReportConfigCursorItemReader reader = new ReportConfigCursorItemReader();

		reader.setMongoTemplate(mongoTemplate);
		reader.setBatchSize(cursorBatchSize);
		reader.setPageSize(pageSize);

		return reader;
	}

	//2. Item Processor
	@Bean
	public ItemProcessor<ReportConfig, ReportConfig> processor(){
		return new ReportConfigItemProcessor();
	}

	//#. Item Writer
	@Bean
	public ItemWriter<ReportConfig> writer(){

		ReportConfigItemWriter writer= ReportConfigItemWriter.builder()
				.mongoTemplate(mongoTemplate)
				.build();

		return writer;
	}

	//STEP
	@Autowired
	private StepBuilderFactory sf;

	@Bean
	public TaskExecutor batchTaskExecutor() {
		threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
		threadPoolTaskExecutor.setCorePoolSize(threadPoolSize);
		threadPoolTaskExecutor.setThreadGroupName("taskExecutor-batch");
		return threadPoolTaskExecutor;
	}

	@Bean
	public Step stepA() {
		return sf.get("stepA")
				.<ReportConfig, ReportConfig>chunk(chunkSize)
				.reader(reader())
				.processor(processor())
				.writer(writer())
				.taskExecutor(batchTaskExecutor())
				//.throttleLimit(3)
				.build(); 
	}

	//JOB
	@Autowired
	private JobBuilderFactory jf;

	@Bean
	public Job jobA() {
		return jf.get("jobA")
				.incrementer(new RunIdIncrementer())
				.start(stepA())
				.build();
	}

	// to create the shedLock collection
	@Bean
	public LockProvider lockProvider() {

		return new MongoLockProvider(mongoTemplate.getDb());
	}
}
