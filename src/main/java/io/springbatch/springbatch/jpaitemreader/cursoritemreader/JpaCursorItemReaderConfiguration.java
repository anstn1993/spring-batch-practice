package io.springbatch.springbatch.jpaitemreader.cursoritemreader;


import io.springbatch.springbatch.entity.Customer;
import java.util.Map;
import javax.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;

//@Configuration
@RequiredArgsConstructor
public class JpaCursorItemReaderConfiguration {
  private final JobBuilderFactory jobBuilderFactory;
  private final StepBuilderFactory stepBuilderFactory;
  private final int chunkSize = 5;
  private final EntityManagerFactory entityManagerFactory;

  @Bean
  public Job job() {
    return jobBuilderFactory.get("jpaCursorItemReaderJob")
        .incrementer(new RunIdIncrementer())
        .start(step1())
        .build()
        ;
  }

  @Bean
  public Step step1() {
    return stepBuilderFactory.get("step1")
        .<Customer, Customer>chunk(chunkSize)
        .reader(itemReader())
        .writer(itemWriter())
        .build()
        ;
  }

  @Bean
  public JpaCursorItemReader<Customer> itemReader() {
    return new JpaCursorItemReaderBuilder<Customer>()
        .name("jpaCursorItemReader")
        .entityManagerFactory(entityManagerFactory)
        .queryString("select c from customer c where firstName like :firstName") // jpql
        .parameterValues(Map.of("firstName", "A%"))
        .build()
        ;
  }

  private ItemWriter<Customer> itemWriter() {
    return items -> items.forEach(System.out::println);
  }
}
