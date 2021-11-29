package com.example.launcher;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.domain.ReportConfig;
import com.example.processor.ReportConfigItemProcessor;
import com.example.repository.ReportConfigRepository;

import lombok.extern.log4j.Log4j2;

@Component
@Log4j2
public class MyJobLauncher {

	@Autowired
	private JobLauncher jobLauncher;

	@Autowired
	private Job job;

	@Autowired
	private ReportConfigRepository reportConfigRepository;
	
	@Scheduled(cron = "${jobA.cron}")
	public void cronJob() {
		
		log.debug("inside cronJob()");
		
		// 1. clean collection
		reportConfigRepository.deleteAll();

		//----------------------------------------

		// 2. insert data
		for (int i = 1 ; i <= 50 ; i++) {
			//for (int i = 17 ; i <= 18 ; i++) {

			ReportConfig reportConfig = ReportConfig.builder()
					.text("text_" + i)
					.index(i)
					.updateHash("a")
					.build();

			reportConfigRepository.save(reportConfig);
		}

		//----------------------------------------

		// 3. run job
		JobParameters jobParameters = new JobParametersBuilder()
				.addLong("time", System.currentTimeMillis())
				.toJobParameters();

		try {
			jobLauncher.run(job, jobParameters);
		} catch (Exception e) {
			log.error("fail to run job", e);
		}
	}
}
