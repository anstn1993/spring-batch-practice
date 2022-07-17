package io.springbatch.springbatch.classifiercompositeitemprocessor;

import org.springframework.batch.item.ItemProcessor;

public class CustomItemProcessor2 implements ItemProcessor<ProcessorInfo, ProcessorInfo> {

  @Override
  public ProcessorInfo process(ProcessorInfo processorInfo) throws Exception {
    System.out.println("CustomItemProcessor2 was invoked!");
    return processorInfo;
  }
}
