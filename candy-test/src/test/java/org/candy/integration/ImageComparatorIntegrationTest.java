package org.candy.integration;

import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.util.Collection;
import org.candy.generic.comparison.ColorImageComparator;
import org.candy.generic.comparison.ImageComparator;
import org.candy.generic.comparison.ImageDiff;
import org.candy.generic.comparison.StraightImageComparator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;


import static com.google.common.truth.Truth.assertThat;

/**
 * Integration test for implementations of {@link ImageComparator}.
 */
@RunWith(Parameterized.class)
public class ImageComparatorIntegrationTest {
  private static final String EXPECTED_FOLDER = "src/test/resources/candy-origin/";

  @Parameters
  public static Collection<Object[]> parameters() {
    return ImmutableList.of(
        new Object[] { StraightImageComparator.class, 0.48d },
        new Object[] { ColorImageComparator.class, 0.21d }
    );
  }

  private double diffPercentage;

  private ImageComparator comparator;

  public ImageComparatorIntegrationTest(
      Class<? extends ImageComparator> comparatorClass, double diffPercentage) throws ReflectiveOperationException {
    this.diffPercentage = diffPercentage;

    comparator = comparatorClass.getConstructor().newInstance();
  }

  @Test
  public void compareEqualImages() throws IOException {
    ImageDiff diff = compare(
        "testCaptureScreenshot_capturedPageScreenshot.png",
        "testCaptureScreenshot_capturedPageScreenshot2.png");
    assertThat(diff.isFailed()).isFalse();
    assertThat(diff.getDiffPercentage()).isEqualTo(0d);
  }

  @Test
  public void compareNonEqualImages() throws IOException {
    ImageDiff diff = compare(
        "testCaptureScreenshot_capturedBodyScreenshot.png",
        "testCaptureScreenshot_capturedBodyScreenshotWithExclusion.png");
    assertThat(diff.isFailed()).isTrue();
    assertThat(diff.getDiffPercentage()).isGreaterThan(diffPercentage);
  }

  private ImageDiff compare(String actualFileName, String expectedFileName) throws IOException {
    actualFileName = EXPECTED_FOLDER + actualFileName;
    expectedFileName = EXPECTED_FOLDER + expectedFileName;
    return comparator.compare(actualFileName, expectedFileName);
  }
}
