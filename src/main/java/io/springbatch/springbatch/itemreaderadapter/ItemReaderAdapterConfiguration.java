package io.springbatch.springbatch.itemreaderadapter;


import io.springbatch.springbatch.entity.Member;
import javax.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.adapter.ItemReaderAdapter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ItemReaderAdapterConfiguration {
  private final JobBuilderFactory jobBuilderFactory;
  private final StepBuilderFactory stepBuilderFactory;
  private final MemberService memberService;
  private final int chunkSize = 2;


  @Bean
  public Job job() {
    return jobBuilderFactory.get("itemReaderAdapterJob")
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
  public ItemReaderAdapter<Member> itemReader() {
    ItemReaderAdapter<Member> itemReaderAdapter = new ItemReaderAdapter<>();
    itemReaderAdapter.setTargetObject(memberService);
    itemReaderAdapter.setTargetMethod("getMember");
    return itemReaderAdapter;
  }

  @Bean
  public ItemWriter<Member> itemWriter() {
    return items -> items.forEach(System.out::println);
  }
}
