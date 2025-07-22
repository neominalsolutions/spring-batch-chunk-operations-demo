package com.mertalptekin.springbatchchunkoperationsdemo.batch;


import com.mertalptekin.springbatchchunkoperationsdemo.model.CustomerCredit;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
public class CustomerCreditJobConfig {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    // Senaryo -> Kredi Notu 600 üstü olan CustomerCredit bilgilerini alıp, Hatalı kayıtları atlatıp, Hatalı olmayan kayıtları okuyup, Bunları dbdeki customers table set edelim. CustomerRepository üzerinden save edelim
    // Writer-> JPAItemWriter
    // Reader -> FlatFileItemReader (CSV,TEXT)

    @Bean
    public ItemReader<CustomerCredit> customerCsvReader() {
        // csv dosya okuma işlemlerini yapan built-in reader
        FlatFileItemReader<CustomerCredit> reader = new FlatFileItemReader<>();
        reader.setResource(new ClassPathResource("customer.csv"));
        reader.setLinesToSkip(1); // ilk kayıt başlık kaydı bunu atlatıyoruz.

        var lineMapper = new DefaultLineMapper<CustomerCredit>();
        var delimeterTokinezer = new DelimitedLineTokenizer();
        lineMapper.setLineTokenizer(delimeterTokinezer);

        // reader setLineMapper ekledik.
        reader.setLineMapper(lineMapper);

        delimeterTokinezer.setNames("name","age","creditScore");
        var beanWrapperFieldSetMapper = new BeanWrapperFieldSetMapper<CustomerCredit>();

        lineMapper.setFieldSetMapper(beanWrapperFieldSetMapper);
        beanWrapperFieldSetMapper.setTargetType(CustomerCredit.class);

        return reader;
    }
    // Process ederken itemları bir filtreden geçirip kredi notu 600 üstünde olanları sadece writer göndereceğiz.
    @Bean
    public ItemProcessor<CustomerCredit,CustomerCredit> customerProcessor() {
        return  customerCredit ->  {
            if(customerCredit.getCreditScore() > 600)
                return  customerCredit;

            return  null;
        };
    }

    @Bean
    public ItemWriter<CustomerCredit> customerWriter() {

        return items -> {
            // Repository ile Save CustomerCredit Entity
            System.out.println("Item Writer Size" + items.size());
        };
    }

    @Bean
    public Step customerCreditStep(){
        return  new StepBuilder("customerCreditStep",jobRepository).<CustomerCredit,CustomerCredit>chunk(10,transactionManager)
                .reader(customerCsvReader())
                .processor(customerProcessor())
                .writer(customerWriter())
                .faultTolerant()
                .skip(RuntimeException.class)
                .skipLimit(10) // 5 hata atlamanın fazlasına geçilirse o zaman Step Failed olsun.Job Failed Olsun
                .retry(RuntimeException.class)
                .retryLimit(3) // Bağlantı kopması vs gibi hata durumlarında job çalıştırmasını 3 kere restart et
                .build();
    }

    @Bean
    public Job customerCreditJob(){
        return  new JobBuilder("customerCreditJob",jobRepository).start(customerCreditStep()).build();
    }


}
