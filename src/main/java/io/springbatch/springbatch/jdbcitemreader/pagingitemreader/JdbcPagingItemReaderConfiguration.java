package io.springbatch.springbatch.jdbcitemreader.pagingitemreader;


import io.springbatch.springbatch.jdbcitemreader.Customer;
import java.util.Map;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
@RequiredArgsConstructor
public class JdbcPagingItemReaderConfiguration {
  private final JobBuilderFactory jobBuilderFactory;
  private final StepBuilderFactory stepBuilderFactory;
  private final int chunkSize = 10;
  private final DataSource dataSource;

  @Bean
  public Job job() throws Exception {
    return jobBuilderFactory.get("jdbcPagingItemReaderJob")
        .incrementer(new RunIdIncrementer())
        .start(step1())
        .build()
        ;
  }

  @Bean
  public Step step1() throws Exception {
    return stepBuilderFactory.get("step1")
        .<Customer, Customer>chunk(chunkSize)
        .reader(itemReader())
        .writer(itemWriter())
        .build()
        ;
  }

  @Bean
  public JdbcPagingItemReader<Customer> itemReader() throws Exception {
    return new JdbcPagingItemReaderBuilder<Customer>()
        .name("jdbcPagingItemReader")
        .pageSize(chunkSize)
        .dataSource(dataSource)
        .beanRowMapper(Customer.class)
        .queryProvider(queryProvider())
        .parameterValues(Map.of("firstName", "A%"))
        .build()
        ;
  }

  private PagingQueryProvider queryProvider() throws Exception {

    SqlPagingQueryProviderFactoryBean queryProviderFactoryBean = new SqlPagingQueryProviderFactoryBean();
    queryProviderFactoryBean.setDataSource(dataSource);
    queryProviderFactoryBean.setSelectClause("id,firstName,lastName,birthdate");
    queryProviderFactoryBean.setFromClause("from customer");
    queryProviderFactoryBean.setWhereClause("where firstName like :firstName");
    queryProviderFactoryBean.setSortKeys(Map.of("id", Order.ASCENDING));


    return queryProviderFactoryBean.getObject();
  }

  @Bean
  public ItemWriter<Customer> itemWriter() {
    return items -> items.forEach(System.out::println);
  }
}
