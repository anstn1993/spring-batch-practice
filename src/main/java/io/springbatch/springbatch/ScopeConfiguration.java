package io.springbatch.springbatch;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ScopeConfiguration {

  private final JobBuilderFactory jobBuilderFactory;
  private final StepBuilderFactory stepBuilderFactory;

  @Bean
  public Job job() {
    return jobBuilderFactory.get("scopeJob")
        .incrementer(new RunIdIncrementer())
        .start(step1(null))
        .next(step2())
        .listener(new CustomJobListener())
        .build();
  }

  @Bean
  @JobScope // 프록시 빈으로 반환. 애플리케이션 구동 시점이 아닌 런타임에 해당 스텝을 참조할 때 jobParameters의 값을 꺼내서 파라미터로 주입하여 타겟 빈 생성
  public Step step1(@Value("#{jobParameters['message']}") String message) {
    System.out.println("message = " + message);
    return stepBuilderFactory.get("step1")
        .tasklet(tasklet1(null, null))
        .listener(new CustomStepListener())
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

  @Bean
  @StepScope // 프록시 빈으로 반환. 애플리케이션 구동 시점이 아닌 런타임에 해당 스텝을 참조할 때 jobExecutionContext, stepExecutionContext의 값을 꺼내서 파라미터로 주입하여 타겟 빈 생성
  public Tasklet tasklet1(
      @Value("#{jobExecutionContext['name']}") String name,
      @Value("#{stepExecutionContext['name2']}") String name2
  ) {
    System.out.println("name = " + name);
    System.out.println("name2 = " + name2);
    return (stepContribution, chunkContext) -> {
      System.out.println("step1 was executed");
      return RepeatStatus.FINISHED;
    };
  }

}
