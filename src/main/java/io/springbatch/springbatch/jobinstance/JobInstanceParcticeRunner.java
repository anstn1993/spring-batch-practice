package io.springbatch.springbatch.jobinstance;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

//@Component
public class JobInstanceParcticeRunner implements ApplicationRunner {

  @Autowired
  private JobLauncher jobLauncher;

  @Autowired
  @Qualifier("jobInstancePractice")
  private Job job;

  @Override
  public void run(ApplicationArguments args) throws Exception {

    JobParameters jobParameters = new JobParametersBuilder()
        .addString("name", "user2")
        .toJobParameters();

    jobLauncher.run(job, jobParameters);
  }
}
