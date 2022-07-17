package io.springbatch.springbatch.classifiercompositeitemprocessor;


import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.support.ClassifierCompositeItemProcessor;
import org.springframework.classify.Classifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ClassifierCompositeItemProcessorConfiguration {

  private final JobBuilderFactory jobBuilderFactory;
  private final StepBuilderFactory stepBuilderFactory;

  @Bean
  public Job job() {
    return jobBuilderFactory.get("classifierCompositeItemProcessorJob")
        .incrementer(new RunIdIncrementer())
        .start(step1())
        .build();
  }

  @Bean
  public Step step1() {
    return stepBuilderFactory.get("step1")
        .<ProcessorInfo, ProcessorInfo>chunk(10)
        .reader(new ItemReader<>() {
          private int i = 0;

          @Override
          public ProcessorInfo read()
              throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
            i++;
            return i > 3 ? null : ProcessorInfo.builder().id(i).build();
          }
        })
        .processor(classifierCompositeItemProcessor())
        .writer(System.out::println)
        .build()
        ;
  }

  @Bean
  public ItemProcessor<ProcessorInfo, ProcessorInfo> classifierCompositeItemProcessor() {

    Map<Integer, ItemProcessor<?, ? extends ProcessorInfo>> processorMap = Map.of(
        1, new CustomItemProcessor1(),
        2, new CustomItemProcessor2(),
        3, new CustomItemProcessor3()
    );
    Classifier<? super ProcessorInfo, ItemProcessor<?, ? extends ProcessorInfo>> customClassifier = new ProcessorClassifier<>(processorMap);

    ClassifierCompositeItemProcessor<ProcessorInfo, ProcessorInfo> classifierCompositeItemProcessor = new ClassifierCompositeItemProcessor<>();
    classifierCompositeItemProcessor.setClassifier(customClassifier);
    return classifierCompositeItemProcessor;
  }

}
