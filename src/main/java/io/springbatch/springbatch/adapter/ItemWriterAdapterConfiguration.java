package io.springbatch.springbatch.adapter;

import io.springbatch.springbatch.Customer;
import java.util.Map;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.adapter.ItemWriterAdapter;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ItemWriterAdapterConfiguration {
  private final JobBuilderFactory jobBuilderFactory;
  private final StepBuilderFactory stepBuilderFactory;
  private final int CHUNK_SIZE = 10;

  @Bean
  public Job job() {
    return jobBuilderFactory.get("itemWriterAdapterJob")
        .incrementer(new RunIdIncrementer())
        .start(step1())
        .build();
  }

  @Bean
  public Step step1() {
    return stepBuilderFactory.get("step1")
        .<String, String>chunk(CHUNK_SIZE)
        .reader(itemReader())
        .writer(itemWriter())
        .build();
  }

  @Bean
  public ItemReader<String> itemReader() {
    return new ItemReader<String>() {
      private int i;
      @Override
      public String read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        i++;
        return i > 10 ? null : "item" + i;
      }
    };
  }

  // bulk insert 처리
  @Bean
  public ItemWriter<String> itemWriter() {
    ItemWriterAdapter<String> itemWriterAdapter = new ItemWriterAdapter<>();
    itemWriterAdapter.setTargetObject(customService());
    itemWriterAdapter.setTargetMethod("customWrite");
    return itemWriterAdapter;
  }

  @Bean
  public CustomService customService() {
    return new CustomService();
  }
}
