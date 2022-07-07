package io.springbatch.springbatch.jobstep;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.step.job.DefaultJobParametersExtractor;
import org.springframework.batch.core.step.job.JobParametersExtractor;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class JobStepConfiguration {
  private final JobBuilderFactory jobBuilderFactory;
  private final StepBuilderFactory stepBuilderFactory;

  @Bean
  public Job parentJob() {
    return jobBuilderFactory.get("parentJob")
        .start(jobStep(null))
        .next(step2())
        .build();
  }

  @Bean
  public Step jobStep(JobLauncher jobLauncher) {
    return stepBuilderFactory.get("jobStep")
        .job(childJob())
        .launcher(jobLauncher)
        .parametersExtractor(jobParametersExtractor()) // stepExecution의 ExecutionContext에 저장된 key, value를 뽑아내서 jobParameters의 key, value로 구성
        .listener(new StepExecutionListener() {
          @Override
          public void beforeStep(StepExecution stepExecution) {
            stepExecution.getExecutionContext().put("name", "user1");
          }

          @Override
          public ExitStatus afterStep(StepExecution stepExecution) {
            return null;
          }
        })
        .build();
  }

  private JobParametersExtractor jobParametersExtractor() {
    DefaultJobParametersExtractor extractor = new DefaultJobParametersExtractor();
    extractor.setKeys(new String[]{"name"}); // stepExecution의 ExecutionContext에서 name이라는 key의 value를 뽑아서 jobParameters에 추가
    return extractor;
  }

  @Bean
  public Job childJob() {
    return jobBuilderFactory.get("childJob")
        .start(step1())
        .build();
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
