package io.springbatch.springbatch.itemstream;

import java.util.ArrayList;
import java.util.List;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

public class CustomItemStreamReader implements ItemStreamReader<String> {

  private final List<String> items;
  private int index;
  private boolean restart = false;


  public CustomItemStreamReader(List<String> items) {
    this.items = new ArrayList<>(items);
    this.index = 0;
  }

  @Override
  public String read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {

    String item = null;

    if (this.index < this.items.size()) {
      item = this.items.get(index++);
    }

    if (this.index == 6 && !restart) {
      throw new RuntimeException("Restart is required");
    }

    return item;
  }

  // 리소스 초기화 작업(최초 1회만 호출)
  @Override
  public void open(ExecutionContext executionContext) throws ItemStreamException {
    if (executionContext.containsKey("index")) {
      this.index = executionContext.getInt("index");
      this.restart = true;
    } else {
      this.index = 0;
      executionContext.put("index", index);
    }
  }

  // 한 chunk size를 돌때마다 호출되어서 중간중간 상태값 업데이트 가능
  @Override
  public void update(ExecutionContext executionContext) throws ItemStreamException {
    // 한 chunk size만큼 돌았을 때 index를 저장하여 재시작시 중단 index부터 진행할 수 있도록 업데이트
    executionContext.put("index", index);
  }

  // 리소스 반납 작업(예외 발생시에도 finally로 호출)
  @Override
  public void close() throws ItemStreamException {
    System.out.println("close");
  }
}
