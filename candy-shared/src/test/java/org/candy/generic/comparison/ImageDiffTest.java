package org.candy.generic.comparison;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


import static com.google.common.truth.Truth.assertThat;
import static org.candy.generic.test.TestUtils.assertThrows;

/**
 * Unit test for {@link ImageDiff}.
 */
@RunWith(JUnit4.class)
public class ImageDiffTest {
  private static final String ACTUAL_FILE = "ActualFile";
  private static final String ACTUAL_INFO = "ActualInfo";
  private static final String ORIGIN_FILE = "OriginFile";
  private static final String ORIGIN_INFO = "OriginInfo";
  private static final String EXPECTED_DIFF_MSG = "Image difference is 21.00%\nOrigin: OriginFile OriginInfo\n" +
      "Actual: ActualFile ActualInfo";
  private static final String EXPECTED_VERIFY_MSG = "ImageDiff.verify() should be called ony once.";

  @Test
  public void testSucceedDiff() {
    ImageDiff diff = new ImageDiff(ACTUAL_FILE, ACTUAL_INFO, ORIGIN_FILE, ORIGIN_INFO, 0d);
    assertThat(diff.getActualFileName()).isEqualTo(ACTUAL_FILE);
    assertThat(diff.getActualInfo()).isEqualTo(ACTUAL_INFO);
    assertThat(diff.getOriginFileName()).isEqualTo(ORIGIN_FILE);
    assertThat(diff.getOriginInfo()).isEqualTo(ORIGIN_INFO);
    assertThat(diff.getDiffPercentage()).isEqualTo(0d);
    assertThat(diff.isFailed()).isFalse();
    diff.verify();
    assertThrows(IllegalStateException.class, EXPECTED_VERIFY_MSG, diff::verify);
  }

  @Test
  public void testFailedDiff() {
    ImageDiff diff = new ImageDiff(ACTUAL_FILE, ACTUAL_INFO, ORIGIN_FILE, ORIGIN_INFO, 21d);
    assertThat(diff.getActualFileName()).isEqualTo(ACTUAL_FILE);
    assertThat(diff.getActualInfo()).isEqualTo(ACTUAL_INFO);
    assertThat(diff.getOriginFileName()).isEqualTo(ORIGIN_FILE);
    assertThat(diff.getOriginInfo()).isEqualTo(ORIGIN_INFO);
    assertThat(diff.getDiffPercentage()).isEqualTo(21d);
    assertThat(diff.isFailed()).isTrue();
    assertThrows(ImageComparisonException.class, EXPECTED_DIFF_MSG, diff::verify);
    assertThrows(IllegalStateException.class, EXPECTED_VERIFY_MSG, diff::verify);
  }
}
