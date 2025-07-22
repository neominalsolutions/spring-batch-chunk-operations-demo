package com.mertalptekin.springbatchchunkoperationsdemo.listener;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

@Component
public class CustomStepExecutionListener implements StepExecutionListener {

    @Override
    public void beforeStep(StepExecution stepExecution) {
        System.out.println("CustomStepExecutionListener.beforeStep");
        StepExecutionListener.super.beforeStep(stepExecution);
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        // Step içerisinde manuel durumları stepten çıktından sonra yönetmemizi sağlayan listener.
        System.out.println("CustomStepExecutionListener.afterStep");
        return StepExecutionListener.super.afterStep(stepExecution);
    }
}
