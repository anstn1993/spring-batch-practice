package io.springbatch.springbatch;

import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
@RequiredArgsConstructor
public class ChunkOrientedTaskletConfiguration {
  private final JobBuilderFactory jobBuilderFactory;
  private final StepBuilderFactory stepBuilderFactory;

  @Bean
  public Job job() {
    return jobBuilderFactory.get("chunkOrientedJob")
        .incrementer(new RunIdIncrementer())
        .start(step1())
        .next(step2())
        .build();
  }

  private Step step1() {
    return stepBuilderFactory.get("step1")
        .<String, String>chunk(2) // 아이템 2개 단위로 트랜젝션 범위 설정
        .reader(new ListItemReader<>(Arrays.asList("item1", "item2", "item3", "item4", "item5", "item6")))
        .processor(new ItemProcessor<String, String>() {
          @Override
          public String process(String item) throws Exception {
            return "my_" + item;
          }
        })
        .writer(new ItemWriter<String>() {
          @Override
          public void write(List<? extends String> items) throws Exception {
            items.forEach(System.out::println);
          }
        })
        .build()
        ;
  }

  private Step step2() {
    return stepBuilderFactory.get("step2")
        .tasklet(new Tasklet() {
          @Override
          public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
            System.out.println("step2 was executed");
            return RepeatStatus.FINISHED;
          }
        })
        .build()
        ;
  }
}
