package com.mertalptekin.springbatchchunkoperationsdemo.batch;

import com.mertalptekin.springbatchchunkoperationsdemo.model.Hakedis;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
public class HakedisJobConfig {

    @Autowired
    @Qualifier("appDataSource")
    private DataSource dataSource;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    @Qualifier("appTransactionManager")
    private PlatformTransactionManager transactionManager;

    // sayfalama işlemleri yaparken sorgudaki parametrelerin sayfalı birşekilde çalılması için kullanılan bir provider
    @Bean
    public PagingQueryProvider pagingQueryProvider() throws Exception {
        SqlPagingQueryProviderFactoryBean sqlPagingQueryProviderFactoryBean = new SqlPagingQueryProviderFactoryBean();
        sqlPagingQueryProviderFactoryBean.setDataSource(dataSource);
        sqlPagingQueryProviderFactoryBean.setSelectClause("SELECT \n" +
                "    p.calisan_id,\n" +
                "    EXTRACT(MONTH FROM p.tarih) AS ay,\n" +
                "    EXTRACT(YEAR FROM p.tarih) AS yil,\n" +
                "    SUM(CASE WHEN p.tur = 'prim' THEN p.tutar ELSE 0 END) AS toplam_prim,\n" +
                "    SUM(CASE WHEN p.tur = 'kesinti' THEN p.tutar ELSE 0 END) AS toplam_kesinti,\n" +
                "    SUM(CASE WHEN p.tur = 'avans' THEN p.tutar ELSE 0 END) AS toplam_avans,\n" +
                "    SUM(CASE WHEN p.tur = 'prim' THEN p.tutar ELSE 0 END) -\n" +
                "    SUM(CASE WHEN p.tur = 'kesinti' THEN p.tutar ELSE 0 END) +\n" +
                "    SUM(CASE WHEN p.tur = 'avans' THEN p.tutar ELSE 0 END) AS toplam_kazanc,\n" +
                "    CURRENT_DATE AS hesaplama_tarihi");

        sqlPagingQueryProviderFactoryBean.setFromClause("FROM \n" +
                "    prim_kesinti_avans p");

        sqlPagingQueryProviderFactoryBean.setWhereClause("WHERE\n" +
                "    EXTRACT(MONTH FROM p.tarih) = EXTRACT(MONTH FROM CURRENT_DATE)\n" +
                "    AND EXTRACT(YEAR FROM p.tarih) = EXTRACT(YEAR FROM CURRENT_DATE)");

        sqlPagingQueryProviderFactoryBean.setGroupClause("GROUP BY \n" +
                "    p.calisan_id,\n" +
                "    EXTRACT(MONTH FROM p.tarih),\n" +
                "    EXTRACT(YEAR FROM p.tarih)");

        // Not: sorgunun düzgün çalışması için en az bir adet sortkey belirlememiz lazım
        sqlPagingQueryProviderFactoryBean.setSortKey("calisan_id");
        return  sqlPagingQueryProviderFactoryBean.getObject();
    }

    @Bean
    public JdbcPagingItemReader<Hakedis> hakedisReader() throws Exception {
        JdbcPagingItemReader<Hakedis> reader = new JdbcPagingItemReader<>();

        PagingQueryProvider pagingQueryProvider = pagingQueryProvider();

        reader.setQueryProvider(pagingQueryProvider);
        reader.setDataSource(dataSource);
        reader.setRowMapper(new HakedisRowMapper());
        reader.setPageSize(50);

        return  reader;
    }

    @Bean
    public ItemProcessor<Hakedis,Hakedis> hakedisProcessor() {
        return item -> {
            System.out.println(item);
            return item;
        };
    }

    @Bean
    public ItemWriter<Hakedis> hakedisWriter() throws Exception {
        return items ->  {
            System.out.println("Hakedis Writer");
        };
    }

    // pagesize:50 chunksize:10
    @Bean
    public Step hakedisStep(){
        try {
            return  new StepBuilder("hakedisStep",jobRepository).<Hakedis,Hakedis>chunk(10,transactionManager).reader(hakedisReader()).processor(hakedisProcessor()).writer(hakedisWriter()).faultTolerant().build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Bean(name = "hakedisJob")
    public Job hakedisJob(){
        return new JobBuilder("hakedisJob",jobRepository).start(hakedisStep()).build();
    }


}
