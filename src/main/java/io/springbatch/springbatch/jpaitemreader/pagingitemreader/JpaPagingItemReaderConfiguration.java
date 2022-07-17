package io.springbatch.springbatch.jpaitemreader.pagingitemreader;


import io.springbatch.springbatch.entity.Member;
import javax.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
@RequiredArgsConstructor
public class JpaPagingItemReaderConfiguration {
  private final JobBuilderFactory jobBuilderFactory;
  private final StepBuilderFactory stepBuilderFactory;
  private final int chunkSize = 2;
  private final EntityManagerFactory entityManagerFactory;

  @Bean
  public Job job() {
    return jobBuilderFactory.get("jpaCursorItemReaderJob")
        .incrementer(new RunIdIncrementer())
        .start(step1())
        .build()
        ;
  }

  @Bean
  public Step step1() {
    return stepBuilderFactory.get("step1")
        .<Member, Member>chunk(chunkSize)
        .reader(itemReader())
        .writer(itemWriter())
        .build()
        ;
  }

  @Bean
  public JpaPagingItemReader<Member> itemReader() {
    return new JpaPagingItemReaderBuilder<Member>()
        .name("jpaPagingItemReader")
        .entityManagerFactory(entityManagerFactory)
        .pageSize(chunkSize)
        .queryString("select m from member m join fetch m.address")
        .build()
        ;
  }

  @Bean
  public ItemWriter<Member> itemWriter() {
    return items -> items.forEach(item -> System.out.println(item.getAddress().getLocation()));
  }
}
