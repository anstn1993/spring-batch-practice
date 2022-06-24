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
runner가 job을 어떻게 실행하는지 알아보려면 위 코드에서 `runner.setJobNames(jobNames)`를 보면 된다. jobNames는 BatchProperties로부터 가져오게 된다.

```java
@ConfigurationProperties(
  prefix = "spring.batch"
)
public class BatchProperties {
  private final BatchProperties.Job job = new BatchProperties.Job();
  private final BatchProperties.Jdbc jdbc = new BatchProperties.Jdbc();
  ...
  public static class Jdbc {
    private static final String DEFAULT_SCHEMA_LOCATION = "classpath:org/springframework/batch/core/schema-@@platform@@.sql";
    private BatchProperties.Isolation isolationLevelForCreate;
    private String schema = "classpath:org/springframework/batch/core/schema-@@platform@@.sql";
    private String platform;
    private String tablePrefix;
    private DatabaseInitializationMode initializeSchema;
    ...
  }
  ...
  public static class Job {
    private String names = "";
    ...
  }
}
```
BatchProperties는 크게 job, jdbc라는 멤버를 가지게 되고 job은 names를 jdbc는 isolationLevelForCreate, schema, platform, tablePrefix, initializeSchema를 멤버로 가진다. 
각각의 프로퍼티는 spring.batch.job, spring.batch.jdbc의 하위에 지정해줄 수 있다. 그 중에서도 우린 지금 jobNames를 보고 있었으니 거기에 다시 집중하면 결국 BatchProperties의 멤버인 job의 멤버인 names로부터 꺼내오게 되는 것이다.
```yaml
spring:
  batch:
    job:
      names: job1,job2,job3
```
위와 같이 실행시킬 job들을 명시하면 저 job들만 실행하게 된다.

그럼 이제 JobLauncherApplicationRunner가 job을 실행시키는 로직을 보자.
```java
public class JobLauncherApplicationRunner implements ApplicationRunner, Ordered, ApplicationEventPublisherAware {
  ...
  @Autowired(
      required = false
  )
  public void setJobs(Collection<Job> jobs) {
    this.jobs = jobs;
  }
  ...
  private void executeLocalJobs(JobParameters jobParameters) throws JobExecutionException {
    Iterator var2 = this.jobs.iterator();

    while(true) {
      while(var2.hasNext()) {
        Job job = (Job)var2.next();
        if (StringUtils.hasText(this.jobNames)) {
          String[] jobsToRun = this.jobNames.split(",");
          if (!PatternMatchUtils.simpleMatch(jobsToRun, job.getName())) {
            logger.debug(LogMessage.format("Skipped job: %s", job.getName()));
            continue;
          }
        }

        this.execute(job, jobParameters);
      }

      return;
    }
  }
  ...
}
```
빈으로 등록된 job들을 컬렉션으로 주입받고 반복자를 이용하여 하나씩 순회하면서 실행하게 된다. 이때 jobNames가 빈 문자열이 아니라면 jobNames에 기재된 이름의 job만 실행을 하고 나머지는 무시하게 된다. jobNames를 따로 지정하지 않은 상태라면 모든 job이 실행된다.
