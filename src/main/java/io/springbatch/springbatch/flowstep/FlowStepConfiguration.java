package io.springbatch.springbatch.flowstep;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlowStepConfiguration {

  private final JobBuilderFactory jobBuilderFactory;
  private final StepBuilderFactory stepBuilderFactory;

  public FlowStepConfiguration(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
    this.jobBuilderFactory = jobBuilderFactory;
    this.stepBuilderFactory = stepBuilderFactory;
  }

  @Bean
  public Job flowJob() {
    return jobBuilderFactory.get("flowJob")
        .incrementer(new RunIdIncrementer())
        .start(flowStep()) // 실패하면 step2는 실행되지 않음
        .next(step2())
        .build();
  }

  @Bean
  public Step flowStep() {
    return stepBuilderFactory.get("flowStep")
        .flow(flow()) // flow의 FlowExecutionStatus값에 따라 Step의 BatchStatus와 ExitCode가 결정
        .build();
  }

  private Flow flow() {
    FlowBuilder<Flow> builder = new FlowBuilder<>("flow");
    builder.start(step1())
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
          return RepeatStatus.FINISHED;
        })
        .build();
  }
}
