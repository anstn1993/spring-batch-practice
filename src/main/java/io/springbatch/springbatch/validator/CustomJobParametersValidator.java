package io.springbatch.springbatch.validator;

import java.util.Objects;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;

public class CustomJobParametersValidator implements JobParametersValidator {
  @Override
  public void validate(JobParameters jobParameters) throws JobParametersInvalidException {
    if (Objects.isNull(jobParameters.getString("name"))) {
      throw new JobParametersInvalidException("parameter name is not provided");
    }
  }
}
