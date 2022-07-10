package io.springbatch.springbatch.jobexecutiondecider;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
public class JobExecutionDeciderConfiguration {

  private final JobBuilderFactory jobBuilderFactory;
  private final StepBuilderFactory stepBuilderFactory;

  public JobExecutionDeciderConfiguration(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
    this.jobBuilderFactory = jobBuilderFactory;
    this.stepBuilderFactory = stepBuilderFactory;
  }

  @Bean
  public Job deciderJob() {
    return jobBuilderFactory.get("deciderJob")
        .incrementer(new RunIdIncrementer())
        .start(step())
        .next(decider())
        .from(decider()).on("ODD").to(oddStep())
        .from(decider()).on("EVEN").to(evenStep())
        .end()
        .build()
        ;
  }

  @Bean
  public JobExecutionDecider decider() {
    return new CustomDecider();
  }

  @Bean
  public Step step() {
    return stepBuilderFactory.get("step")
        .tasklet(new Tasklet() {
          @Override
          public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) {
            System.out.println("step was executed");
            return RepeatStatus.FINISHED;
          }
        })
        .build();
  }

  @Bean
  public Step evenStep() {
    return stepBuilderFactory.get("evenStep")
        .tasklet(new Tasklet() {
          @Override
          public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) {
            System.out.println("evenStep was executed");
            return RepeatStatus.FINISHED;
          }
        })
        .build();
  }

  @Bean
  public Step oddStep() {
    return stepBuilderFactory.get("oddStep")
        .tasklet(new Tasklet() {
          @Override
          public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) {
            System.out.println("oddStep was executed");
            return RepeatStatus.FINISHED;
          }
        })
        .build();
  }
}
