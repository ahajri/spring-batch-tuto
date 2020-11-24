package com.ahajri.knoor.config;

import com.ahajri.knoor.batch.ArticleItemProcessor;
import com.ahajri.knoor.batch.ArticleJsonReader;
import com.ahajri.knoor.batch.JobCompletionNotificationListener;
import com.ahajri.knoor.model.Article;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.UrlResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.net.MalformedURLException;

@Configuration
@EnableBatchProcessing
//@Transactional
@EnableScheduling
public class BatchConfig {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    public JdbcTemplate jdbcTemplate;

    @Autowired
    JobCompletionNotificationListener listener;

    @Autowired
    private DataSource dataSource;

    @Autowired
    JobLauncher jobLauncher;

    @Value("${app.resource.input}")
    private String jsonInputPath;

    @Bean
    public JsonItemReader jsonItemReader() throws MalformedURLException {
        return new JsonItemReaderBuilder()
                .jsonObjectReader(new ArticleJsonReader(new UrlResource(jsonInputPath)))
                .resource(new UrlResource(jsonInputPath))
                .name("articleItemReader")
                .build();
    }


    @Bean
    public ArticleItemProcessor processor() {
        return new ArticleItemProcessor();
    }


    @Bean
    public JdbcBatchItemWriter<Article> writer() {

        return new JdbcBatchItemWriterBuilder<Article>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<Article>())
                .sql("INSERT INTO ARTICLE (ID, BODY) VALUES (:id, :body)")
                .dataSource(dataSource)
                .build();
    }

    public Job importArticleJob(Step step1) {
        return jobBuilderFactory.get("importArticleJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(step1)
                .end()
                .build();
    }

    @Bean
    public Step step1() throws MalformedURLException {
        return stepBuilderFactory.get("step1")
                .<Article, Article>chunk(10)
                .reader(jsonItemReader())
                .processor(new ArticleItemProcessor())
                .writer(writer())
                .build();
    }


    @Scheduled(cron = "30 51 13 * * *")
    public void perform() throws Exception {
        JobParameters params = new JobParametersBuilder()
                .addString("JobID", String.valueOf(System.currentTimeMillis()))
                .toJobParameters();
        jobLauncher.run(importArticleJob(step1()), params);
    }
}
