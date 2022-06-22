package io.springbatch.springbatch.executioncontext;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@Component
public class ExecutionContextTasklet4 implements Tasklet {

  @Override
  public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
    String stepName = chunkContext.getStepContext().getStepName();
    System.out.println(stepName + "was executed");
    ExecutionContext jobExecutionContext = chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext();
    System.out.println("name: " + jobExecutionContext.get("name"));

    return RepeatStatus.FINISHED;
  }
}
