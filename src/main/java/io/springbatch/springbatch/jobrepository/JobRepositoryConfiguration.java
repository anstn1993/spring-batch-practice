package io.springbatch.springbatch.jobrepository;

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
public class JobRepositoryConfiguration {
  private final JobBuilderFactory jobBuilderFactory;
  private final StepBuilderFactory stepBuilderFactory;
  private final JobRepositoryListener jobRepositoryListener;

  @Bean
  public Job jobRepositoryPractice() {
    return jobBuilderFactory.get("jobRepositoryPractice")
        .start(jobRepositoryPracticeStep1())
        .next(jobRepositoryPracticeStep2())
        .listener(jobRepositoryListener)
        .build();
  }

  @Bean
  public Step jobRepositoryPracticeStep1() {
    return stepBuilderFactory.get("jobRepositoryPracticeStep1")
        .tasklet(new Tasklet() {
          @Override
          public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
            System.out.println("step1 was executed");
            return RepeatStatus.FINISHED;
          }
        }).build();
  }

  public Step jobRepositoryPracticeStep2() {
    return stepBuilderFactory.get("jobRepositoryPracticeStep2")
        .tasklet(new Tasklet() {
          @Override
          public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
            System.out.println("step2 was executed");
            return RepeatStatus.FINISHED;
          }
        }).build();
  }


}
