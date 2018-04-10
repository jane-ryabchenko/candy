package org.candy.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.candy.data.entity.Comparison;

import java.io.Serializable;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;
import static com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY;

/**
 * DTO for comparison data.
 */
@Getter
@Setter
public class ComparisonDTO implements Serializable {
  private int comparisonNumber;

  private String testClass;

  private String testMethod;

  private double diffPercentage;

  private String actualFileName;

  private String originFileName;

  @JsonProperty(access = WRITE_ONLY)
  private byte[] actualImage;

  @JsonProperty(access = WRITE_ONLY)
  private byte[] originImage;

  @JsonProperty(access = READ_ONLY)
  private String actualImageId;

  @JsonProperty(access = READ_ONLY)
  private String originImageId;

  public ComparisonDTO() {}

  public ComparisonDTO(Comparison comparison) {
    testClass = comparison.getTestClass();
    testMethod = comparison.getTestMethod();
    diffPercentage = comparison.getDiffPercentage();
    actualFileName = comparison.getActualFileName();
    originFileName = comparison.getOriginFileName();
    actualImageId = comparison.getActualImage().getId();
    if (comparison.getOriginImage() != null) {
      originImageId = comparison.getOriginImage().getId();
    }
  }
}
