package com.mertalptekin.springbatchchunkoperationsdemo.controller;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/batch/flowJob")
public class FlowJobController {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    @Qualifier("splitFlowJobV1")
    private Job job;

    @Autowired
    @Qualifier("flowDeciderJobv1")
    private Job job2;

    @PostMapping("split")
    public ResponseEntity startSplitFlow() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {

        var params = new JobParametersBuilder().toJobParameters();

        jobLauncher.run(job,params);

        return ResponseEntity.ok().build();
    }

    @PostMapping("decider")
    public ResponseEntity startDeciderFlow() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {

        var params = new JobParametersBuilder().toJobParameters();

        jobLauncher.run(job2,params);

        return ResponseEntity.ok().build();
    }
}
