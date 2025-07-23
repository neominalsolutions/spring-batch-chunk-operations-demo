package com.mertalptekin.springbatchchunkoperationsdemo.batch;

import com.mertalptekin.springbatchchunkoperationsdemo.model.Hakedis;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.partition.PartitionHandler;
import org.springframework.batch.core.partition.support.SimplePartitioner;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
public class PartitionConfig {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    @Qualifier("appTransactionManager")
    private PlatformTransactionManager transactionManager;


    @Autowired
    private LogginTask logginTask;

    @Bean
    public HakedisJdbcItemReader jdbcItemReader() {
        return new HakedisJdbcItemReader();
    }

    @Bean
    public ItemWriter<Hakedis> hakedisItemWriter() {
        return items -> {

        };
    }

    @Bean
    public ItemProcessor<Hakedis,Hakedis> hakedisItemProcessor() {
        return item-> {
            return item;
        };
    }

    // Partioner yapısı sadece chnuk orinted desteklenir.
    // Asıl işlemi yapan step burası
    @Bean
    public Step customStep() {
        return  new StepBuilder("customStep",jobRepository).<Hakedis,Hakedis>chunk(10,transactionManager).reader(jdbcItemReader()).processor(hakedisItemProcessor()).writer(hakedisItemWriter()).faultTolerant().build();
    }

    // Partioner Step için gerekli bir adım asenkron split bu sayede oluyor
    @Bean
    public TaskExecutor taskExecutor() {
//        SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor();
//        taskExecutor.setThreadNamePrefix("taskExecutor-");

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(25);
        executor.setThreadNamePrefix("partition-thread-");
        executor.initialize();
        return executor;
    }

    // bölme işleminde sorumlu Bean
    @Bean
    public PartitionHandler partitionHandler() {

        TaskExecutorPartitionHandler ts = new TaskExecutorPartitionHandler();
        ts.setGridSize(2); // 10 farklı thread üzerinde işlem yap
        ts.setTaskExecutor(taskExecutor());
        ts.setStep(customStep());

        return  ts;
    }

    // bir step'i partitionlara ayıran özel bir step tanımı
    @Bean
    public  Step partitionerStep() {
        SimplePartitioner partitioner = new SimplePartitioner();
        return new StepBuilder("partionerStep",jobRepository)
                .partitioner("customStep",partitioner)
                .step(customStep())
                .build();
    }

    // süreci partitionerStep dan başlatıyoruz.
    @Bean(name = "partitionerJob")
    public Job partitionerJob(){
        return  new JobBuilder("partitionerJob",jobRepository).start(partitionerStep()).build();
    }



}
