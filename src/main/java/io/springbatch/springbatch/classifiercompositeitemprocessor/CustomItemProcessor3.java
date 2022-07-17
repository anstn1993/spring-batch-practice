package io.springbatch.springbatch.classifiercompositeitemprocessor;

import org.springframework.batch.item.ItemProcessor;

public class CustomItemProcessor3 implements ItemProcessor<ProcessorInfo, ProcessorInfo> {

  @Override
  public ProcessorInfo process(ProcessorInfo processorInfo) throws Exception {
    System.out.println("CustomItemProcessor3 was invoked!");
    return processorInfo;
  }
}
