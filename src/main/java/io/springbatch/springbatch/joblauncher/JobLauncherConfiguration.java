package io.springbatch.springbatch.joblauncher;

import io.springbatch.springbatch.jobrepository.JobRepositoryListener;
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
public class JobLauncherConfiguration {
  private final JobBuilderFactory jobBuilderFactory;
  private final StepBuilderFactory stepBuilderFactory;
  private final JobRepositoryListener jobRepositoryListener;

  @Bean
  public Job jobLauncherPractice() {
    return jobBuilderFactory.get("jobLauncherPractice")
        .start(jobLauncherPracticeStep1())
        .next(jobLauncherPracticeStep2())
        .listener(jobRepositoryListener)
        .build();
  }

  @Bean
  public Step jobLauncherPracticeStep1() {
    return stepBuilderFactory.get("jobLauncherPracticeStep1")
        .tasklet(new Tasklet() {
          @Override
          public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
            System.out.println("step1 was executed");
            Thread.sleep(3000);
            return RepeatStatus.FINISHED;
          }
        }).build();
  }

  public Step jobLauncherPracticeStep2() {
    return stepBuilderFactory.get("jobLauncherPracticeStep2")
        .tasklet(new Tasklet() {
          @Override
          public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
            System.out.println("step2 was executed");
            return RepeatStatus.FINISHED;
          }
        }).build();
  }


}
