package io.springbatch.springbatch.basic;

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

//@Configuration
public class FlowJobConfiguration {

  private final JobBuilderFactory jobBuilderFactory;
  private final StepBuilderFactory stepBuilderFactory;

  public FlowJobConfiguration(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
    this.jobBuilderFactory = jobBuilderFactory;
    this.stepBuilderFactory = stepBuilderFactory;
  }

  @Bean
  public Job flowJob() {
    return jobBuilderFactory.get("flowJob")
        .incrementer(new RunIdIncrementer())
        .start(step1())// step1의
        .on("COMPLETED") // Step1의 결과에 대한 Transition 정의
        .to(step3()) // ExitStatus가 COMPLETED면 step3를 실행
        .from(step1())// 이전 단계(step1)에서 정의한 Transition에 대한 추가적인 정의
        .on("FAILED").to(step2()) // ExitStatus가 FAILED면 step2를 실행
        .end() // SimpleFlow 반환
        .build(); // FlowJob생성 및 flow 필드에 SimpleFlow 세팅
  }

  @Bean
  public Step step1() {
    return stepBuilderFactory.get("step1")
        .tasklet(new Tasklet() {
          @Override
          public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) {
            System.out.println("step1 was executed");
            throw new RuntimeException("step1 was failed");
//            return RepeatStatus.FINISHED;
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
            throw new RuntimeException("step2 was failed");
//            return RepeatStatus.FINISHED;
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
}
