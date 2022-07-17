package io.springbatch.springbatch.jdbc;

import io.springbatch.springbatch.Customer;
import java.util.Map;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
@RequiredArgsConstructor
public class JdbcBatchItemWriterConfiguration {
  private final JobBuilderFactory jobBuilderFactory;
  private final StepBuilderFactory stepBuilderFactory;
  private final DataSource dataSource;
  private final int CHUNK_SIZE = 10;

  @Bean
  public Job job() {
    return jobBuilderFactory.get("jdbcBatchItemWriterJob")
        .start(step1())
        .build();
  }

  @Bean
  public Step step1() {
    return stepBuilderFactory.get("step1")
        .<Customer, Customer>chunk(CHUNK_SIZE)
        .reader(itemReader())
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

  // bulk insert 처리
  @Bean
  public ItemWriter<? super Customer> itemWriter() {
    return new JdbcBatchItemWriterBuilder<Customer>()
        .dataSource(dataSource)
        .sql("insert into customer2 values(:id, :firstName, :lastName, :birthdate)")
        .beanMapped()
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
