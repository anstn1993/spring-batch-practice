package io.springbatch.springbatch.classifiercompositeitemprocessor;

import java.util.Map;
import org.springframework.classify.Classifier;
import org.springframework.util.Assert;

public class ProcessorClassifier<C, T> implements Classifier<C, T> {

  private final Map<Integer, T> processorMap;

  public ProcessorClassifier(Map<Integer, T> processorMap) {
    this.processorMap = processorMap;
  }

  @Override
  public T classify(C c) {
    Assert.isInstanceOf(ProcessorInfo.class, c);
    return processorMap.get(((ProcessorInfo) c).getId());
  }
}
