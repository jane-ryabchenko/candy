package org.candy.generic.comparison;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * Diff info between actual and expected screenshots.
 */
public class ImageDiff {
  private boolean verified;
  private double diffPercentage;
  private String actualFileName;
  private String originFileName;
  private String actualInfo;
  private String originInfo;
  private ImageComparisonException exception;

  public ImageDiff(
      String actualFileName, String actualInfo, String originFileName, String originInfo, double diffPercentage) {
    this.actualFileName = checkNotNull(actualFileName);
    this.originFileName = checkNotNull(originFileName);
    this.actualInfo = actualInfo;
    this.originInfo = originInfo;
    this.diffPercentage = diffPercentage;
    if (isFailed()) {
      String message = String.format(
          "Image difference is %.2f%%\nOrigin: %s %s\nActual: %s %s",
          getDiffPercentage(),
          getOriginFileName(),
          getOriginInfo(),
          getActualFileName(),
          getActualInfo());
      exception = new ImageComparisonException(message);
    }
  }

  public boolean isFailed() {
    // TODO: Add configurable threshold.
    return diffPercentage > 0d;
  }

  public void verify() {
    checkState(!verified, "ImageDiff.verify() should be called ony once.");
    verified = true;
    if (isFailed()) {
      throw exception;
    }
  }

  public double getDiffPercentage() {
    return diffPercentage;
  }

  public String getActualFileName() {
    return actualFileName;
  }

  public String getOriginFileName() {
    return originFileName;
  }

  public String getOriginInfo() {
    return originInfo;
  }

  public String getActualInfo() {
    return actualInfo;
  }
}
