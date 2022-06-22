package io.springbatch.springbatch.executioncontext;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ExecutionContextConfiguration {
  private final JobBuilderFactory jobBuilderFactory;
  private final StepBuilderFactory stepBuilderFactory;
  private final ExecutionContextTasklet1 executionContextTasklet1;
  private final ExecutionContextTasklet2 executionContextTasklet2;
  private final ExecutionContextTasklet3 executionContextTasklet3;
  private final ExecutionContextTasklet4 executionContextTasklet4;
  @Bean
  public Job executionContextPractice() {
    return jobBuilderFactory.get("executionContextPractice")
        .start(executionContextPracticeStep1())
        .next(executionContextPracticeStep2())
        .next(executionContextPracticeStep3())
        .next(executionContextPracticeStep4())
        .build();
  }

  @Bean
  public Step executionContextPracticeStep1() {
    return stepBuilderFactory.get("executionContextPracticeStep1")
        .tasklet(executionContextTasklet1).build();
  }

  public Step executionContextPracticeStep2() {
    return stepBuilderFactory.get("executionContextPracticeStep2")
        .tasklet(executionContextTasklet2).build();
  }
  @Bean
  public Step executionContextPracticeStep3() {
    return stepBuilderFactory.get("executionContextPracticeStep3")
        .tasklet(executionContextTasklet3).build();
  }

  public Step executionContextPracticeStep4() {
    return stepBuilderFactory.get("executionContextPracticeStep4")
        .tasklet(executionContextTasklet4).build();
  }
}
