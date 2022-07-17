package io.springbatch.springbatch.itemstream;

import java.util.List;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamWriter;

public class CustomItemStreamWriter implements ItemStreamWriter<String> {

  // 최초 한번만 호출
  @Override
  public void open(ExecutionContext executionContext) throws ItemStreamException {
    System.out.println("open");
  }

  @Override
  public void update(ExecutionContext executionContext) throws ItemStreamException {
    System.out.println("update");
  }

  @Override
  public void close() throws ItemStreamException {
    System.out.println("close");
  }

  @Override
  public void write(List<? extends String> items) throws Exception {
    items.forEach(System.out::println);
  }
}
