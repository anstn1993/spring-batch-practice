package io.springbatch.springbatch.itemstream;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
@RequiredArgsConstructor
public class ItemStreamConfiguration {
  private final JobBuilderFactory jobBuilderFactory;
  private final StepBuilderFactory stepBuilderFactory;

  @Bean
  public Job job() {
    return jobBuilderFactory.get("itemStreamJob")
        .incrementer(new RunIdIncrementer())
        .start(step1())
        .next(step2())
        .build();
  }

  @Bean
  public Step step1() {
    return stepBuilderFactory.get("step1")
        .<String, String>chunk(5) // 아이템 2개 단위로 트랜젝션 범위 설정
        .reader(itemReader(null))
        .writer(itemWriter())
        .build()
        ;
  }

  @Bean
  @StepScope
  public ItemReader<String> itemReader(@Value("#{stepExecutionContext['index']}") Integer index) {
    System.out.println("index: " + index);
    List<String> items = new ArrayList<>(10);

    for (int i = 0; i < 10; i++) {
      items.add(String.valueOf(i));
    }

    return new CustomItemStreamReader(items);
  }

  @Bean
  public ItemWriter<String> itemWriter() {
    return new CustomItemStreamWriter();
  }

  @Bean
  public Step step2() {
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
