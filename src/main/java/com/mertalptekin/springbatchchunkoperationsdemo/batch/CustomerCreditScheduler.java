package com.mertalptekin.springbatchchunkoperationsdemo.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
//@EnableScheduling
public class CustomerCreditScheduler {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    @Qualifier("customerCreditJob")
    private Job job;

    // Scheduled işlemlerde Batch Transaction ile AppTransaction ayrılmadığında bu tarz sorunlar ile karşılabiliriz.

    // her ayın ilk günü
    @Scheduled(cron = "0 0 0 1 * ?")
    public  void run() throws Exception{

        System.out.println("Running Customer Credit Scheduler");
        // Sürekli her bir date paramteresi için kayıt atıyor.
        JobParameters jobParameters = new JobParametersBuilder().addLong("date",System.currentTimeMillis()).toJobParameters();

        jobLauncher.run(job,jobParameters);
    }

}
