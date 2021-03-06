# spring-batch-practice
---
스프링 배치 학습 repository
<br>
스프링 배치의 모든 기본 내용을 다 담는 저장소는 아니고(담을 수도 없고..ㅎㅎ), 내부적인 동작 방식과 같은 부가적으로 살펴보고 기록할 필요가 있는 내용들을 위주로 채운다.

## 스프링 배치 초기 실행
---
스프링 부트기반의 스프링 배치는 ApplicationRunner의 구현체인 JobLauncherApplicationRunner에 의해서 프로그램이 시작됨과 동시에 빈으로 등록된 모든 job을 실행시킨다.(`spring.batch.job.enabled=true`라는 기본설정 하에) 
<br>
JobLauncherApplicationRunner는 BatchAutoConfiguration 설정 클래스에서 빈으로 등록된다. 아래의 코드를 보자.
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
<br>
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
<br>
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

## Job의 생성과정
---
JobBuilderFactory에서 시작하여 Job이 생성되는 과정을 탐험하고, 최종적으로 Job이 지닌 속성들이 어떻게 세팅되는지 살펴보자.
```java
@Bean
public Job job() {
  return this.jobBuilderFactory.get("job") // JobBuilder 반환
      .start(step()) // SimpleJobBuilder 반환
      // 이 아래부터는 SimpleJob의 속성을 세팅하는 api
      .validator(jobParametersValidator()) // JobParameters의 유효성 검사
      .preventRestart() // job이 실패해도 재시작 불가
      .incrementer(jobParametersIncrementer()) // 동일한 jobParameter로 job 재시작 가능
      .listener(jobExecutionListener()) // job의 시작과 종료시에 호출시킬 콜백 리스너
      .build(); // SimpleJob 반환
}
```
JobBuilderFactory.get() 메서드를 호출하면 JobBuilder를 반환하게 된다.
```java
public JobBuilder get(String name) {
  JobBuilder builder = (JobBuilder)(new JobBuilder(name)).repository(this.jobRepository);
  return builder;
}
```
JobBuilder를 반환할 때 빈으로 등록되어있던 JobRepository를 세팅하게 된다. JobBuilder는 start()나 flow()를 통해서 SimpleJobBuilder나 JobFlowBuilder를 반환하게 된다.
```java
public class JobBuilder extends JobBuilderHelper<JobBuilder> {
  public JobBuilder(String name) {
    super(name);
  }

  public SimpleJobBuilder start(Step step) {
    return (new SimpleJobBuilder(this)).start(step);
  }

  public JobFlowBuilder start(Flow flow) {
    return (new FlowJobBuilder(this)).start(flow);
  }

  public JobFlowBuilder flow(Step step) {
    return (new FlowJobBuilder(this)).start(step);
  }
}
```
JobBuilder.start()메서드의 인자로 Step이 전달되면 SimpleJobBuilder를, Flow를 전달하면 JobFlowBuilder를 반환하게 된다. flow()메서드를 호출해도 JobFlowBuilder를 반환하게 되는데, 여기서 중요한 건 JobBuilder가 구체적으로 생성해야할 대상체의 Builder를 라우팅해서 반환해준다는 것이다. 위의 코드에서는 start() 메서드에 Step을 전달하기 때문에 최종적으로 Job의 구현체 중에서 SimpleJob을 생성하는 빌더인 SimpleJobBuilder를 반환하게 된다. 
<br>
JobBuilder, SimpleJobBuilder, JobFlowBuilder모두 JobBuilderHelper라는 추상 클래스를 상속받고 있는데, 헬퍼라는 이름에 걸맞게 Job의 생성에 필요한 여러 공통적인 api를 제공하고 있다. 즉, Job의 타입을 막론하고 공통적으로 필요한 api는 JobBuilderHelper에 정의해두고 이를 상속받는 Builder들은 자신들에게 필요한 기능들을 추가 구현한 형태를 가진다. 위에서 본 JobBuilderFactory로 Job을 생성하는 코드에서 validator(), preventRestart(), incrementer(), listener()는 모두 JobBuilderHelper에 정의된 메서드들이다. 그리고 JobBuilderHelper클래스는 자신의 내부 클래스인 CommonJobProperties를 속성으로 가지고 있는데, 이 클래스는 위에 나열된 api들을 통해 Job의 속성을 선행적으로 보관하다가 실제로 SimpleJob을 build하는 순간에 SimpleJob으로 속성을 전달해주는 역할을 수행한다. 말로하니 역시 어렵다. 코드를 보자.
```java
public abstract class JobBuilderHelper<B extends JobBuilderHelper<B>> {
  private final JobBuilderHelper.CommonJobProperties properties;
  
  ...
  
  public B validator(JobParametersValidator jobParametersValidator) {
    this.properties.jobParametersValidator = jobParametersValidator;
    return this;
  }

  public B incrementer(JobParametersIncrementer jobParametersIncrementer) {
    this.properties.jobParametersIncrementer = jobParametersIncrementer;
    return this;
  }
  
  public B listener(JobExecutionListener listener) {
    this.properties.addJobExecutionListener(listener);
    return this;
  }

  public B preventRestart() {
    this.properties.restartable = false;
    return this;
  }
  
  ...
  
  public static class CommonJobProperties {
    private Set<JobExecutionListener> jobExecutionListeners = new LinkedHashSet();
    private boolean restartable = true;
    private JobRepository jobRepository;
    private JobParametersIncrementer jobParametersIncrementer;
    private JobParametersValidator jobParametersValidator;
    private String name;
    
    ...
    
  }
}
```
보다시피 CommomJobProperties는 job의 공통 속성들을 가지고 있고 각각의 api들에서 인자로 전달된 인스턴스를 CommonJobProperties의 속성으로 세팅해주게 된다. 그리고 최종적으로 SimpleJobBuilder의 build()메서드를 호출하게 되면 JobBuilderHelper의 enhance()메서드가 내부적으로 호출되는데 그 구현은 아래와 같다.
```java
protected void enhance(Job target) {
    if (target instanceof AbstractJob) {
      AbstractJob job = (AbstractJob)target;
      job.setJobRepository(this.properties.getJobRepository());
      JobParametersIncrementer jobParametersIncrementer = this.properties.getJobParametersIncrementer();
      if (jobParametersIncrementer != null) {
        job.setJobParametersIncrementer(jobParametersIncrementer);
      }

      JobParametersValidator jobParametersValidator = this.properties.getJobParametersValidator();
      if (jobParametersValidator != null) {
        job.setJobParametersValidator(jobParametersValidator);
      }

      Boolean restartable = this.properties.getRestartable();
      if (restartable != null) {
        job.setRestartable(restartable);
      }

      List<JobExecutionListener> listeners = this.properties.getJobExecutionListeners();
      if (!listeners.isEmpty()) {
        job.setJobExecutionListeners((JobExecutionListener[])listeners.toArray(new JobExecutionListener[0]));
      }
    }

  }
```
보다시피 commonJobProperties의 속성들을 모두 Job의 속성으로 주입해주게 된다.
