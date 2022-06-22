package io.springbatch.springbatch.joblauncher;

import java.util.Date;
import javax.validation.Valid;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.batch.BasicBatchConfigurer;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JobLauncherController {

  @Autowired
  @Qualifier("jobLauncherPractice")
  private Job job;

  @Autowired
  private JobLauncher jobLauncher; // 이렇게 주입받은 jobLauncher는 프록시 객체

  @Autowired
  BasicBatchConfigurer basicBatchConfigurer; // basicBatchConfigurer로부터 꺼내는 jobLauncher가 실제 객체

  @PostMapping("/batch")
  public String launch(@RequestBody @Valid Member member)
      throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
    JobParameters jobParameters = new JobParametersBuilder()
        .addString("id", member.getId())
        .addDate("date", new Date())
        .toJobParameters();
    // 동기
    jobLauncher.run(job, jobParameters);
//    SimpleJobLauncher simpleJobLauncher = (SimpleJobLauncher) basicBatchConfigurer.getJobLauncher();
//    simpleJobLauncher.setTaskExecutor(new SimpleAsyncTaskExecutor());
//    simpleJobLauncher.run(job, jobParameters);
    // 비동기
    return "batch completed";
  }
}
