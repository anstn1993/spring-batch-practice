package io.springbatch.springbatch.jobrepository;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JobRepositoryListener implements JobExecutionListener {

  private final JobRepository jobRepository;

  @Override
  public void beforeJob(JobExecution jobExecution) {

  }

  @Override
  public void afterJob(JobExecution jobExecution) {
    String jobName = jobExecution.getJobInstance().getJobName();
    JobParameters jobParameters = new JobParametersBuilder()
        .addString("requestDate", "20220622")
        .toJobParameters();
    JobExecution lastJobExecution = jobRepository.getLastJobExecution(jobName, jobParameters);
    if (lastJobExecution != null) {
      for (StepExecution stepExecution : lastJobExecution.getStepExecutions()) {
        String stepName = stepExecution.getStepName();
        BatchStatus status = stepExecution.getStatus();
        ExitStatus exitStatus = stepExecution.getExitStatus();
        System.out.println("stepName: " + stepName);
        System.out.println("batchStatus: " + status);
        System.out.println("exitStatus: " + exitStatus);
      }
    }
  }
}
