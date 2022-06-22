package io.springbatch.springbatch.stepexecution;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

//@Component
public class StepExecuitonPracticeRunner implements ApplicationRunner {

  @Autowired
  private JobLauncher jobLauncher;

  @Autowired
  @Qualifier("stepExecutionPractice")
  private Job job;

  @Override
  public void run(ApplicationArguments args) throws Exception {
    jobLauncher.run(job, new JobParameters());
  }
}
