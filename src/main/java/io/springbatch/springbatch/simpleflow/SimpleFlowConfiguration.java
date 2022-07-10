package io.springbatch.springbatch.simpleflow;

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
public class SimpleFlowConfiguration {

  private final JobBuilderFactory jobBuilderFactory;
  private final StepBuilderFactory stepBuilderFactory;

  public SimpleFlowConfiguration(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
    this.jobBuilderFactory = jobBuilderFactory;
    this.stepBuilderFactory = stepBuilderFactory;
  }

  @Bean
  public Job flowJob() {
    return jobBuilderFactory.get("flowJob")
        .incrementer(new RunIdIncrementer())
        .start(flow1())
          .on("COMPLETED").to(flow2())
        .from(flow1())
          .on("FAILED").to(flow3())
        .end()
        .build();
  }

  @Bean
  public Flow flow1() {
    FlowBuilder<Flow> builder = new FlowBuilder<>("flow1");
    builder.start(step1())
        .next(step2())
        .end();
    return builder.build();
  }
  @Bean
  public Flow flow2() {
    FlowBuilder<Flow> builder = new FlowBuilder<>("flow2");
    builder.start(flow3())
        .next(step5())
        .next(step6())
        .end();
    return builder.build();
  }
  @Bean
  public Flow flow3() {
    FlowBuilder<Flow> builder = new FlowBuilder<>("flow3");
    builder.start(step3())
        .next(step4())
        .end();
    return builder.build();
  }

  @Bean
  public Step step1() {
    return stepBuilderFactory.get("step1")
        .tasklet((stepContribution, chunkContext) -> {
          System.out.println("step1 was executed");
          return RepeatStatus.FINISHED;
        })
        .build();
  }

  @Bean
  public Step step2() {
    return stepBuilderFactory.get("step2")
        .tasklet((stepContribution, chunkContext) -> {
          System.out.println("step2 was executed");
//          stepContribution.setExitStatus(ExitStatus.FAILED);
          return RepeatStatus.FINISHED;
        })
        .build();
  }

  @Bean
  public Step step3() {
    return stepBuilderFactory.get("step3")
        .tasklet((stepContribution, chunkContext) -> {
          System.out.println("step3 was executed");
          return RepeatStatus.FINISHED;
        })
        .build();
  }
  @Bean
  public Step step4() {
    return stepBuilderFactory.get("step4")
        .tasklet((stepContribution, chunkContext) -> {
          System.out.println("step4 was executed");
          return RepeatStatus.FINISHED;
        })
        .build();
  }

  @Bean
  public Step step5() {
    return stepBuilderFactory.get("step5")
        .tasklet((stepContribution, chunkContext) -> {
          System.out.println("step5 was executed");
          return RepeatStatus.FINISHED;
        })
        .build();
  }

  @Bean
  public Step step6() {
    return stepBuilderFactory.get("step6")
        .tasklet((stepContribution, chunkContext) -> {
          System.out.println("step6 was executed");
          return RepeatStatus.FINISHED;
        })
        .build();
  }
}
