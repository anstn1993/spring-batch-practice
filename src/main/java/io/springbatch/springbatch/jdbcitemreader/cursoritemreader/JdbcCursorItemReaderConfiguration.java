package io.springbatch.springbatch.jdbcitemreader.cursoritemreader;


import io.springbatch.springbatch.jdbcitemreader.Customer;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

//@Configuration
@RequiredArgsConstructor
public class JdbcCursorItemReaderConfiguration {
  private final JobBuilderFactory jobBuilderFactory;
  private final StepBuilderFactory stepBuilderFactory;
  private final int chunkSize = 10;
  private final DataSource dataSource;

  @Bean
  public Job job() {
    return jobBuilderFactory.get("jdbcCursorItemReaderJob")
        .incrementer(new RunIdIncrementer())
        .start(step1())
        .build()
        ;
  }

  @Bean
  public Step step1() {
    return stepBuilderFactory.get("step1")
        .<Customer, Customer>chunk(chunkSize)
        .reader(itemReader(null))
        .writer(itemWriter())
        .build()
        ;
  }

  @Bean
  @StepScope
  public JdbcCursorItemReader<Customer> itemReader(@Value("#{stepExecutionContext['jdbcCursorItemReader.read.count']}") String readCount) {
    System.out.println("readCount: " + readCount);
    return new JdbcCursorItemReaderBuilder<Customer>()
        .name("jdbcCursorItemReader")
        .fetchSize(chunkSize)
        .sql("select id, firstName, lastName, birthdate from customer")
        .beanRowMapper(Customer.class)
//        .queryArguments("A%")
        .dataSource(dataSource)
        .queryTimeout(1)
//        .currentItemCount(readCount)
        .build()
        ;
  }

  private ItemWriter<Customer> itemWriter() {
    return items -> {
//      if(items.get(0).getId().equals(11L)) {
//        throw new RuntimeException();
//      }
      items.forEach(System.out::println);
//      throw new RuntimeException();
    };
  }
}
