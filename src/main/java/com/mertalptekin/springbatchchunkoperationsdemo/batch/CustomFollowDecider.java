package com.mertalptekin.springbatchchunkoperationsdemo.batch;

// Bu tarz sınıflar servis görevi görür. Akışında kendi kod base ile tanımlanmasını sağlar.

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.stereotype.Component;

@Component
public class CustomFollowDecider implements JobExecutionDecider {
    @Override
    public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
        // Nurada bir sonraki stepe geçerken belirli ön işlmelerden gececek bir yapı kuruyoruz
        // veri kaynağına bağlanarak bir karar akışından geçebiliriz.
        return new FlowExecutionStatus("OnProgress");
    }
}
