package io.springbatch.springbatch.joblauncher;

import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class Member {
  @NotEmpty
  private String id;
}
