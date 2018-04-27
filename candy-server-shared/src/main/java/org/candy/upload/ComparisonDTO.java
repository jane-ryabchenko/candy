package org.candy.upload;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
class ComparisonDTO implements Serializable {
  private int comparisonNumber;

  private String testClass;

  private String testMethod;

  private double diffPercentage;

  private String actualFileName;

  private String actualFileInfo;

  private String originFileName;

  private String originFileInfo;
}
