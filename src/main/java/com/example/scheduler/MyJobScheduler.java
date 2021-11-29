package com.example.scheduler;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.domain.ReportConfig;
import com.example.repository.ReportConfigRepository;

import lombok.extern.log4j.Log4j2;
import net.javacrumbs.shedlock.core.LockAssert;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;

@Component
@Log4j2
public class MyJobScheduler {

	@Autowired
	private JobLauncher jobLauncher;

	@Autowired
	private Job job;

	@Autowired
	private ReportConfigRepository reportConfigRepository;

	@Scheduled(cron = "${jobA.cron}")
	@SchedulerLock(name = "jobA", lockAtMostFor = "50s", lockAtLeastFor = "30s")
	public void cronJob() {

		log.debug("start cronJob()");

		// To assert that the lock is held (prevents misconfiguration errors)
		LockAssert.assertLocked();

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
		
		log.debug("end cronJob()");
	}
}
