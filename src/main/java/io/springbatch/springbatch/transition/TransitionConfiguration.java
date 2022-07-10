package io.springbatch.springbatch.transition;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
public class TransitionConfiguration {

  private final JobBuilderFactory jobBuilderFactory;
  private final StepBuilderFactory stepBuilderFactory;

  public TransitionConfiguration(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
    this.jobBuilderFactory = jobBuilderFactory;
    this.stepBuilderFactory = stepBuilderFactory;
  }

  @Bean
  public Job flowJob() {
    return jobBuilderFactory.get("flowJob")
        .incrementer(new RunIdIncrementer())
        .start(step1())// step1의
          .on("FAILED").to(step2())
          .on("*").stop()
        .from(step1())
          .on("*").to(step3())
          .next(step4())
          .on("FAILED").end()
        .end()
        .build()
        ; // FlowJob생성 및 flow 필드에 SimpleFlow 세팅
  }

  @Bean
  public Step step1() {
    return stepBuilderFactory.get("step1")
        .tasklet(new Tasklet() {
          @Override
          public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) {
            System.out.println("step1 was executed");
            stepContribution.setExitStatus(ExitStatus.FAILED);
            return RepeatStatus.FINISHED;
          }
        })
        .build();
  }

  @Bean
  public Step step2() {
    return stepBuilderFactory.get("step2")
        .tasklet(new Tasklet() {
          @Override
          public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) {
            System.out.println("step2 was executed");
            return RepeatStatus.FINISHED;
          }
        })
        .build();
  }

  @Bean
  public Step step3() {
    return stepBuilderFactory.get("step3")
        .tasklet(new Tasklet() {
          @Override
          public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) {
            System.out.println("step3 was executed");
            return RepeatStatus.FINISHED;
          }
        })
        .build();
  }
  @Bean
  public Step step4() {
    return stepBuilderFactory.get("step4")
        .tasklet(new Tasklet() {
          @Override
          public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) {
            System.out.println("step4 was executed");
            return RepeatStatus.FINISHED;
          }
        })
        .build();
  }
}
