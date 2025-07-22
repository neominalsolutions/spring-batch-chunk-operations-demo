package com.mertalptekin.springbatchchunkoperationsdemo.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
//@EnableScheduling
public class CustomerCreditScheduler {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job job;

    @Scheduled(cron = "0 */1 * * * *")
    public  void run() throws Exception{

        JobParameters jobParameters = new JobParametersBuilder().toJobParameters();

        jobLauncher.run(job,jobParameters);
    }

}
