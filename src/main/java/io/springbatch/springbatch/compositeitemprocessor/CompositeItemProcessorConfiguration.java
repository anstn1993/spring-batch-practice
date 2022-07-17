package io.springbatch.springbatch.compositeitemprocessor;


import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.support.builder.CompositeItemProcessorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
@RequiredArgsConstructor
public class CompositeItemProcessorConfiguration {

  private final JobBuilderFactory jobBuilderFactory;
  private final StepBuilderFactory stepBuilderFactory;

  @Bean
  public Job job() {
    return jobBuilderFactory.get("compositeItemProcessorJob")
        .incrementer(new RunIdIncrementer())
        .start(step1())
        .build();
  }

  @Bean
  public Step step1() {
    return stepBuilderFactory.get("step1")
        .<String, String>chunk(10)
        .reader(new ItemReader<>() {
          private int i = 0;

          @Override
          public String read()
              throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
            i++;
            return i > 10 ? null : "item";
          }
        })
        .processor(compositeItemProcessor())
        .writer(System.out::println)
        .build()
        ;
  }

  @Bean
  public ItemProcessor<String, String> compositeItemProcessor() {
    return new CompositeItemProcessorBuilder<String, String>()
        .delegates(List.of(new CustomItemProcessor(), new CustomItemProcessor2()))
        .build();
  }

}
