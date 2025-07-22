package com.mertalptekin.springbatchchunkoperationsdemo.listener;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Component
public class CustomJobExecutionListener implements JobExecutionListener {

    @Override
    public void afterJob(JobExecution jobExecution) {
        System.out.println("CustomJobExecutionListener afterJob");
        JobExecutionListener.super.afterJob(jobExecution);
    }

    @Override
    public void beforeJob(JobExecution jobExecution) {
        System.out.println("CustomJobExecutionListener beforeJob");

//        jobExecution.getStepExecutions().forEach(stepExecution -> {
//            stepExecution.
//        })

        JobExecutionListener.super.beforeJob(jobExecution);
    }

}
