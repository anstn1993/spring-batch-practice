package io.springbatch.springbatch.jobparameter;

import java.util.Date;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
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
public class JobParameterConfiguration {

  private final JobBuilderFactory jobBuilderFactory;
  private final StepBuilderFactory stepBuilderFactory;

  @Bean
  public Job jobParameterPractice() {
    return jobBuilderFactory.get("jobParameterPractice")
        .start(jobParameterPracticeStep1())
        .next(jobParameterPracticeStep2())
        .build();
  }

  @Bean
  public Step jobParameterPracticeStep1() {
    return stepBuilderFactory.get("jobParameterPracticeStep1")
        .tasklet(new Tasklet() {
          @Override
          public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
            JobParameters jobParameters = stepContribution.getStepExecution().getJobExecution().getJobParameters();
            String name = jobParameters.getString("name");
            Long seq = jobParameters.getLong("seq");
            Date date = jobParameters.getDate("date");
            Double age = jobParameters.getDouble("age");
            System.out.println("================ jobParameters ================");
            System.out.println("name: " + name);
            System.out.println("seq: " + seq);
            System.out.println("date: " + date);
            System.out.println("age: " + age);
            System.out.println("===============================================");
            System.out.println("step1 was executed");
            return RepeatStatus.FINISHED;
          }
        }).build();
  }

  @Bean
  public Step jobParameterPracticeStep2() {
    return stepBuilderFactory.get("jobParameterPracticeStep2")
        .tasklet(new Tasklet() {
          @Override
          public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
            Map<String, Object> jobParameters = chunkContext.getStepContext().getJobParameters();
            String name = (String) jobParameters.get("name");
            Long seq = (Long) jobParameters.get("seq");
            Date date = (Date) jobParameters.get("date");
            Double age = (Double) jobParameters.get("age");
            System.out.println("================ jobParameters ================");
            System.out.println("name: " + name);
            System.out.println("seq: " + seq);
            System.out.println("date: " + date);
            System.out.println("age: " + age);
            System.out.println("===============================================");
            System.out.println("step2 was executed");
            return RepeatStatus.FINISHED;
          }
        }).build();
  }
}
