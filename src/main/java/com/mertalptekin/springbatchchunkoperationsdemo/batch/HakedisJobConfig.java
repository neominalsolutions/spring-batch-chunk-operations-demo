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
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
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
        sqlPagingQueryProviderFactoryBean.setSelectClause("SELECT" +
                " p.calisan_id,  -- Çalışan ID'si" +
                "EXTRACT(MONTH FROM p.tarih) AS ay,  -- Aylık veriler" +
                "EXTRACT(YEAR FROM p.tarih) AS yil,  -- Yıl bazında" +
                "SUM(CASE WHEN p.tur = 'prim' THEN p.tutar ELSE 0 END) AS toplam_prim,  -- Prim toplamı" +
                "SUM(CASE WHEN p.tur = 'kesinti' THEN p.tutar ELSE 0 END) AS toplam_kesinti,  -- Kesinti toplamı" +
                "    SUM(CASE WHEN p.tur = 'avans' THEN p.tutar ELSE 0 END) AS toplam_avans,  -- Avans toplamı" +
                "    SUM(CASE WHEN p.tur = 'prim' THEN p.tutar ELSE 0 END)" +
                "    SUM(CASE WHEN p.tur = 'kesinti' THEN p.tutar ELSE 0 END)" +
                "    SUM(CASE WHEN p.tur = 'avans' THEN p.tutar ELSE 0 END) AS toplam_kazanc,  -- Toplam kazanç (Prim - Kesinti + Avans)" +
                "    CURRENT_DATE AS hesaplama_tarihi  -- Hesaplama tarihi (sorgunun çalıştığı gün)" +
                "FROM" +
                "    prim_kesinti_avans p");

        sqlPagingQueryProviderFactoryBean.setWhereClause("WHERE" +
                "    EXTRACT(MONTH FROM p.tarih) = EXTRACT(MONTH FROM CURRENT_DATE)" +
                "    AND EXTRACT(YEAR FROM p.tarih) = EXTRACT(YEAR FROM CURRENT_DATE)");

        sqlPagingQueryProviderFactoryBean.setGroupClause("GROUP BY" +
                "    p.calisan_id,  -- Çalışan bazında grupla" +
                "    EXTRACT(MONTH FROM p.tarih),  -- Ay bazında grupla" +
                "    EXTRACT(YEAR FROM p.tarih) ");

        // Not: sorgunun düzgün çalışması için en az bir adet sortkey belirlememiz lazım
        sqlPagingQueryProviderFactoryBean.setSortKey("calisan_id");
        return  sqlPagingQueryProviderFactoryBean.getObject();
    }

    @Bean
    public JdbcPagingItemReader<Hakedis> hakedisReader() throws Exception {
        JdbcPagingItemReader<Hakedis> reader = new JdbcPagingItemReader<>();
        reader.setQueryProvider(pagingQueryProvider());
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

    @Bean
    public Job hakedisJob(){
        return new JobBuilder("hakedisJob",jobRepository).start(hakedisStep()).build();
    }


}
