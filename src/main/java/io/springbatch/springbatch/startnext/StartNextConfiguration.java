package io.springbatch.springbatch.startnext;

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
public class StartNextConfiguration {

  private final JobBuilderFactory jobBuilderFactory;
  private final StepBuilderFactory stepBuilderFactory;

  public StartNextConfiguration(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
    this.jobBuilderFactory = jobBuilderFactory;
    this.stepBuilderFactory = stepBuilderFactory;
  }

  @Bean
  public Job flowJob() {
    return jobBuilderFactory.get("flowJob")
        .incrementer(new RunIdIncrementer())
        .start(flowA())// step1의
        .next(step3())
        .next(flowB())
        .next(step6())
        .end()
        .build(); // FlowJob생성 및 flow 필드에 SimpleFlow 세팅
  }

  @Bean
  public Flow flowA() {
    FlowBuilder<Flow> flowBuilder = new FlowBuilder<>("flowA");
    flowBuilder.start(step1())
        .next(step2())
        .end();
    return flowBuilder.build();
  }

  @Bean
  public Flow flowB() {
    FlowBuilder<Flow> flowBuilder = new FlowBuilder<>("flowB");
    flowBuilder.start(step4())
        .next(step5())
        .end();
    return flowBuilder.build();
  }

  @Bean
  public Step step1() {
    return stepBuilderFactory.get("step1")
        .tasklet(new Tasklet() {
          @Override
          public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) {
            System.out.println("step1 was executed");
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
  @Bean
  public Step step5() {
    return stepBuilderFactory.get("step5")
        .tasklet(new Tasklet() {
          @Override
          public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) {
            System.out.println("step5 was executed");
            return RepeatStatus.FINISHED;
          }
        })
        .build();
  }
  @Bean
  public Step step6() {
    return stepBuilderFactory.get("step6")
        .tasklet(new Tasklet() {
          @Override
          public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) {
            System.out.println("step6 was executed");
            return RepeatStatus.FINISHED;
          }
        })
        .build();
  }
}
