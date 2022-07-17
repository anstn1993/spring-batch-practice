package io.springbatch.springbatch.jpa;

import io.springbatch.springbatch.Customer;
import io.springbatch.springbatch.Customer2;
import org.modelmapper.ModelMapper;
import org.springframework.batch.item.ItemProcessor;

public class CustomItemProcessor implements ItemProcessor<Customer, Customer2> {

  private ModelMapper modelMapper = new ModelMapper();

  @Override
  public Customer2 process(Customer customer) throws Exception {
    return modelMapper.map(customer, Customer2.class);
  }
}
