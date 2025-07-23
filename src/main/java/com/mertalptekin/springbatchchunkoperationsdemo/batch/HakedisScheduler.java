package com.mertalptekin.springbatchchunkoperationsdemo.batch;


import org.hibernate.sql.results.spi.LoadContexts;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDate;

@Configuration
@EnableScheduling
public class HakedisScheduler {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    @Qualifier("hakedisJob")
    private Job job;

    @Scheduled(cron = "0 * * * * *")
    public void run() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {

        String monthIndex = LocalDate.now().getMonth().toString();
        String yearIndex = LocalDate.now().getYear() + "";

        JobParameters jobParameters = new JobParametersBuilder().addString("Month",monthIndex).addString("yearIndex",yearIndex).toJobParameters();

        jobLauncher.run(job,jobParameters);
    }


}
