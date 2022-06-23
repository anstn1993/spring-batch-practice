# spring-batch-practice
스프링 배치 학습 repository

## 스프링 배치 초기 실행
스프링 부트기반의 스프링 배치는 ApplicationRunner의 구현체인 JobLauncherApplicationRunner에 의해서 프로그램이 시작됨과 동시에 빈으로 등록된 모든 job을 실행시킨다.(`spring.batch.job.enabled=true`라는 기본설정 하에) JobLauncherApplicationRunner는 BatchAutoConfiguration 설정 클래스에서 빈으로 등록된다. 아래의 코드를 보자.
```java
@AutoConfiguration(
  after = {HibernateJpaAutoConfiguration.class}
)
@ConditionalOnClass({JobLauncher.class, DataSource.class})
@ConditionalOnBean({JobLauncher.class})
@EnableConfigurationProperties({BatchProperties.class})
@Import({BatchConfigurerConfiguration.class, DatabaseInitializationDependencyConfigurer.class})
public class BatchAutoConfiguration {
  public BatchAutoConfiguration() {
  }

  @Bean
  @ConditionalOnMissingBean
  @ConditionalOnProperty(
    prefix = "spring.batch.job",
    name = {"enabled"},
    havingValue = "true",
    matchIfMissing = true
  )
  public JobLauncherApplicationRunner jobLauncherApplicationRunner(JobLauncher jobLauncher, JobExplorer jobExplorer, JobRepository jobRepository, BatchProperties properties) {
    JobLauncherApplicationRunner runner = new JobLauncherApplicationRunner(jobLauncher, jobExplorer, jobRepository);
    String jobNames = properties.getJob().getNames();
    if (StringUtils.hasText(jobNames)) {
      runner.setJobNames(jobNames); // 여기 주목!!
    }

    return runner;
  }
...
}
```
JobLauncherApplicationRunner가 빈으로 등록되는데 annotation을 보면 `spring.batch.job.enabled`설정의 값이 true일 때만 빈으로 등록이 된다. 즉 application.properties(or yml)에서 저 설정을 false로 지정하면 이 JobLauncherApplicationRunner가 빈으로 등록되지 않고 job은 실행되지 않는다.
