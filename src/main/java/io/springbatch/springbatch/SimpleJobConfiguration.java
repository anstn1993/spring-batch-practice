package io.springbatch.springbatch;

import io.springbatch.springbatch.incrementer.CustomJobParametersIncrementer;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.DefaultJobParametersValidator;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class SimpleJobConfiguration {
  private final JobBuilderFactory jobBuilderFactory;
  private final StepBuilderFactory stepBuilderFactory;

  @Bean
  public Job job() {
    return this.jobBuilderFactory.get("job")
        .start(step())
//        .validator(new CustomJobParametersValidator())
        // spring batch가 기본 제공하는 validator 구현체
        .validator(new DefaultJobParametersValidator(new String[]{"name", "date"}, new String[]{"count"}))
        .preventRestart() // job이 실패해도 재시작 불가
//        .incrementer(new RunIdIncrementer()) // 1씩 자동증가하는 run.id라는 파라미터를 통해 run.id를 제외한 동일한 jobParameter로 job 재시작 가능
        .incrementer(new CustomJobParametersIncrementer())
        .build();
  }

  @Bean
  public Step step() {
    return stepBuilderFactory.get("step")
        .tasklet((stepContribution, chunkContext) -> RepeatStatus.FINISHED)
        .build();
  }
}
