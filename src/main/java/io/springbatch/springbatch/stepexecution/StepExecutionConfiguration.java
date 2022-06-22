package io.springbatch.springbatch.stepexecution;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class StepExecutionConfiguration {
  private final JobBuilderFactory jobBuilderFactory;
  private final StepBuilderFactory stepBuilderFactory;

  @Bean
  public Job stepExecutionPractice() {
    return jobBuilderFactory.get("stepExecutionPractice")
        .incrementer(new RunIdIncrementer())
        .start(stepExecutionPracticeStep1())
        .next(stepExecutionPracticeStep2())
        .next(stepExecutionPracticeStep3())
        .build();
  }

  @Bean
  public Step stepExecutionPracticeStep1() {
    return stepBuilderFactory.get("stepExecutionPracticeStep1")
        .tasklet(new Tasklet() {
          @Override
          public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
            System.out.println("step1 was executed");
            return RepeatStatus.FINISHED;
          }
        }).build();
  }

  public Step stepExecutionPracticeStep2() {
    return stepBuilderFactory.get("jobExecutionPracticeStep2")
        .tasklet(new Tasklet() {
          @Override
          public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
            System.out.println("step2 was executed");
//            throw new RuntimeException("step2 was failed"); // step3는 실행되지 않고 BATCH_STEP_EXECUTION에도 추가되지 않음
            return RepeatStatus.FINISHED;
          }
        }).build();
  }

  public Step stepExecutionPracticeStep3() {
    return stepBuilderFactory.get("stepExecutionPracticeStep3")
        .tasklet(new Tasklet() {
          @Override
          public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
            System.out.println("step3 was executed");
            return RepeatStatus.FINISHED;
          }
        }).build();
  }


}
