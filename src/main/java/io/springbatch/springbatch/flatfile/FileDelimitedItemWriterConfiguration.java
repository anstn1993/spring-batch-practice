package io.springbatch.springbatch.flatfile;

import java.util.Arrays;
import java.util.List;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

@Configuration
@RequiredArgsConstructor
public class FileDelimitedItemWriterConfiguration {
  private final JobBuilderFactory jobBuilderFactory;
  private final StepBuilderFactory stepBuilderFactory;
  private final DataSource dataSource;
  private final int CHUNK_SIZE = 10;

  @Bean
  public Job job() {
    return jobBuilderFactory.get("fileDelimitedItemWriterJob")
        .incrementer(new RunIdIncrementer())
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
    List<Customer> customers = Arrays.asList(
        new Customer(1, "munsoo", 30),
        new Customer(2, "munsook", 31),
        new Customer(3, "munsoon", 32)
        );

    return new ListItemReader<>(customers);
  }

  @Bean
  public ItemWriter<? super Customer> itemWriter() {
    return new FlatFileItemWriterBuilder<>()
        .name("flatFileWriter")
        .resource(new FileSystemResource("/Users/nhn/Desktop/springbatch/src/main/resources/customer.txt"))
        .delimited()
        .delimiter("|")
        .names(new String[]{"id", "name", "age"})
        .build();
  }
}
