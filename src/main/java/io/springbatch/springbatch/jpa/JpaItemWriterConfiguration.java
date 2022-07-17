package io.springbatch.springbatch.jpa;

import io.springbatch.springbatch.Customer;
import io.springbatch.springbatch.Customer2;
import java.util.Map;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
@RequiredArgsConstructor
public class JpaItemWriterConfiguration {
  private final JobBuilderFactory jobBuilderFactory;
  private final StepBuilderFactory stepBuilderFactory;
  private final DataSource dataSource;
  private final EntityManagerFactory entityManagerFactory;
  private final int CHUNK_SIZE = 10;

  @Bean
  public Job job() {
    return jobBuilderFactory.get("jpaItemWriterJob")
        .incrementer(new RunIdIncrementer())
        .start(step1())
        .build();
  }

  @Bean
  public Step step1() {
    return stepBuilderFactory.get("step1")
        .<Customer, Customer2>chunk(CHUNK_SIZE)
        .reader(itemReader())
        .processor(itemProcessor())
        .writer(itemWriter())
        .build();
  }

  @Bean
  public ItemReader<? extends Customer> itemReader() {
    return new JdbcPagingItemReaderBuilder<Customer>()
        .name("jdbcPagingItemReader")
        .queryProvider(queryProvider())
        .parameterValues(Map.of("firstName", "A%"))
        .pageSize(CHUNK_SIZE)
        .beanRowMapper(Customer.class)
        .dataSource(dataSource)
        .build()
        ;
  }

  @Bean
  public ItemProcessor<Customer, Customer2> itemProcessor() {
    return new CustomItemProcessor();
  }

  // bulk insert 처리
  @Bean
  public ItemWriter<Customer2> itemWriter() {
    return new JpaItemWriterBuilder<Customer2>()
        .entityManagerFactory(entityManagerFactory)
        .build()
        ;
  }

  private PagingQueryProvider queryProvider() {
    MySqlPagingQueryProvider mySqlPagingQueryProvider = new MySqlPagingQueryProvider();
    mySqlPagingQueryProvider.setSelectClause("id,firstName,lastName,birthdate");
    mySqlPagingQueryProvider.setFromClause("from customer");
    mySqlPagingQueryProvider.setWhereClause("where firstName like :firstName");
    mySqlPagingQueryProvider.setSortKeys(Map.of("id", Order.ASCENDING));
    return mySqlPagingQueryProvider;
  }
}
