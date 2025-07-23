package com.mertalptekin.springbatchchunkoperationsdemo.batch;


import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.support.SimpleFlow;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
public class FlowJobConfig {

    // Flow birden fazla stepin birlşemesinde meydana gelen akışlar

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private  CustomFollowDecider followDecider;


    @Bean
    public Step step1() {
        return new StepBuilder("step1",jobRepository).tasklet((contribution,context)->{

            System.out.println("Step 1 executing....");

            return RepeatStatus.FINISHED;
        },transactionManager).build();
    }

    @Bean
    public Step step2() {
        return new StepBuilder("step2",jobRepository).tasklet((contribution, chunkContext) -> {
            System.out.println("Step 2 executing....");
            return RepeatStatus.FINISHED;
        },transactionManager).build();
    }

    @Bean
    public Step step3() {
        return new StepBuilder("step2",jobRepository).tasklet((contribution, chunkContext) -> {
            System.out.println("Step 2 executing....");
            return RepeatStatus.FINISHED;
        },transactionManager).build();
    }

    // 2 adımlı bir akış
    @Bean
    public Flow flow1() {
        return new FlowBuilder<SimpleFlow>("flow1").start(step1()).next(step2()).build();
    }

    // tek adımdan oluşan bir akış
    @Bean
    public Flow flow2() {
        return new FlowBuilder<SimpleFlow>("flow2").start(step3()).end();
    }


    // aşağıdaki split durumda flow1 deki herhangi bir hata durumunda flow2 etkilenmez.

    @Bean(name = "splitFlowJobV1")
    public Job splitFlowJob() {
        return new JobBuilder("splitFlowJobV1",jobRepository).start(flow1()).split(new SimpleAsyncTaskExecutor()).add(flow2()).end().build();

        // flow2 deki step adımları ayrı bir thread içerisinde çalışır
    }

    // Not:Decider yapıları olmadan akışları sadece birbirlerini tetikleyen yapılar olarak kullanabiliriz.

    // Completed olan status değeriene diğer flowları çalıştırdığımız için flow içersindeki herhangi bir stepde hata meydana gelirse BatchStatus Failed olur.
    // Belirli bir koşula göre sıralı akış yapılarınıda Flowlar destekler.

    @Bean(name = "flowJobCriteria")
    public Job flowJobCriteria() {
        return new JobBuilder("flowJobCriteria",jobRepository).start(flow1()).on("Completed").to(flow2()).end().build();

        // flow2 deki step adımları ayrı bir thread içerisinde çalışır
    }

    // Dinamik olarak akışların ne yönlü çalışması gerektiğine karar vereceğimiz Decider yaklaşımı vardır.

    // flow1 ile başlayan bir iş akışı eğer flowDeciderda OnProgress de kalırsa bu durumda flow2 deki adımları durdur ve yeniden başlat.

    @Bean(name = "flowDeciderJobv1")
    public Job flowDeciderJob() {
        return  new JobBuilder("flowDeciderJobv1",jobRepository).start(flow1()).from(followDecider).on("onProgress").to(flow2()).build().build();
    }

    // birden fazla decider ekleyerek akışlar yönelndirilebilir.
    // followDecider da onProgress statusdan step2 geç step2 adımında iken hernahi bir durumda  * o zaman flow2 çalıştır.
    @Bean(name = "flowDeciderJobv2")
    public Job flowDeciderJob2() {
        return  new JobBuilder("flowDeciderJobv2",jobRepository).start(followDecider).on("onProgress").to(step2()).from(step2()).on("*").to(flow2()).end().build();
    }







}
