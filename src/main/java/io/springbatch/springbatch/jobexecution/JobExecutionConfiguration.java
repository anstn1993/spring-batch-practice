package io.springbatch.springbatch.jobexecution;

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
public class JobExecutionConfiguration {
  private final JobBuilderFactory jobBuilderFactory;
  private final StepBuilderFactory stepBuilderFactory;

  @Bean
  public Job jobExecutionPractice() {
    return jobBuilderFactory.get("jobExecutionPractice")
        .start(jobExecutionPracticeStep1())
        .next(jobExecutionPracticeStep2())
        .build();
  }

  @Bean
  public Step jobExecutionPracticeStep1() {
    return stepBuilderFactory.get("jobExecutionPracticeStep1")
        .tasklet(new Tasklet() {
          @Override
          public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
            System.out.println("step1 was executed");
            return RepeatStatus.FINISHED;
          }
        }).build();
  }

  public Step jobExecutionPracticeStep2() {
    return stepBuilderFactory.get("jobExecutionPracticeStep2")
        .tasklet(new Tasklet() {
          @Override
          public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
            System.out.println("step2 was executed");
            return RepeatStatus.FINISHED;
          }
        }).build();
  }


}
