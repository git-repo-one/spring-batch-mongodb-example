package com.example.launcher;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.domain.ReportConfig;
import com.example.repository.ReportConfigRepository;

@Component
public class MyJobLauncher implements CommandLineRunner {

	@Autowired
	private JobLauncher jobLauncher;

	@Autowired
	private Job job;

	@Autowired
	private ReportConfigRepository reportConfigRepository;

	@Override
	public void run(String... args) throws Exception {

		// 1. clean collection
		reportConfigRepository.deleteAll();
		
		//----------------------------------------
		
		// 2. insert data
		//for (int i = 1 ; i <= 50 ; i++) {
		for (int i = 17 ; i <= 18 ; i++) {

			ReportConfig reportConfig = ReportConfig.builder()
					.text("text_" + i)
					.index(i)
					.updateHash("a")
					.build();

			reportConfigRepository.save(reportConfig);
		}
		
		System.exit(0);
		//----------------------------------------
		
		// 3. run job
		JobParameters jobParameters = new JobParametersBuilder()
										.addLong("time", System.currentTimeMillis())
										.toJobParameters();

		jobLauncher.run(job, jobParameters);
		
		System.exit(0);
	}
}
