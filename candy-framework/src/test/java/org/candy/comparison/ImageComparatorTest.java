package org.candy.generic.comparison;

import com.google.common.collect.ImmutableList;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import org.candy.generic.CandyException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;


import static com.google.common.truth.Truth.assertThat;
import static org.candy.generic.test.TestUtils.assertThrows;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

/**
 * Unit test for implementations of {@link ImageComparator}.
 */
@RunWith(Parameterized.class)
public class ImageComparatorTest {
  private static final int WIDTH = 100;
  private static final int HEIGHT = 100;
  private static final int RGB = Color.GRAY.getRGB();
  private static final String ACTUAL_FILE = "Actual";
  private static final String ORIGIN_FILE = "Origin";
  private static final String NO_FILE = "NoFile";

  @Parameters
  public static Collection<Object[]> parameters() {
    return ImmutableList.of(
        new Object[] { StraightImageComparator.class, 1d },
        new Object[] { ColorImageComparator.class, 0.32d }
    );
  }

  @Mock BufferedImage originImage;
  @Mock BufferedImage actualImage;

  private double diffPercentage;

  private ImageComparator comparator;

  public ImageComparatorTest(
      Class<? extends ImageComparator> comparatorClass, double diffPercentage) throws ReflectiveOperationException {
    this.diffPercentage = diffPercentage;

    comparator = spy(comparatorClass.getConstructor().newInstance());
  }

  @Before
  public void before() throws IOException {
    MockitoAnnotations.initMocks(this);

    when(originImage.getWidth()).thenReturn(WIDTH);
    when(originImage.getHeight()).thenReturn(HEIGHT);
    when(originImage.getRGB(anyInt(), anyInt())).thenReturn(RGB);

    when(actualImage.getWidth()).thenReturn(WIDTH);
    when(actualImage.getHeight()).thenReturn(HEIGHT);
    when(actualImage.getRGB(anyInt(), anyInt())).thenReturn(RGB);

    doThrow(FileNotFoundException.class).when(comparator).readImage(NO_FILE);
    doReturn(actualImage).when(comparator).readImage(ACTUAL_FILE);
    doReturn(originImage).when(comparator).readImage(ORIGIN_FILE);
  }

  @Test
  public void testWithoutActualImage() {
    assertThrows(CandyException.class, "Actual screenshot file not found.", () -> comparator.compare(NO_FILE, NO_FILE));
  }

  @Test
  public void testWithIOException() throws IOException {
    doThrow(IOException.class).when(comparator).readImage(NO_FILE);
    assertThrows(CandyException.class, "Unknown error.", () -> comparator.compare(NO_FILE, NO_FILE));
  }

  @Test
  public void testWithoutOriginImage() {
    ImageDiff diff = comparator.compare(ACTUAL_FILE, NO_FILE);
    assertThat(diff.isFailed()).isTrue();
    assertThat(diff.getDiffPercentage()).isEqualTo(100d);
  }

  @Test
  public void testSameImages() {
    ImageDiff diff = comparator.compare(ACTUAL_FILE, ORIGIN_FILE);
    assertThat(diff.isFailed()).isFalse();
    assertThat(diff.getDiffPercentage()).isEqualTo(0d);
  }

  @Test
  public void testImagesWithPixelsDifference() {
    when(actualImage.getRGB(anyInt(), anyInt())).thenAnswer((Answer<Integer>) invocationOnMock -> {
      int x = invocationOnMock.getArgumentAt(0, Integer.class);
      int y = invocationOnMock.getArgumentAt(1, Integer.class);
      if (x < 10 && y < 10) {
        return Color.BLUE.getRGB();
      }
      return RGB;
    });

    ImageDiff diff = comparator.compare(ACTUAL_FILE, ORIGIN_FILE);
    assertThat(diff.isFailed()).isTrue();
    assertThat(diff.getDiffPercentage()).isAtLeast(diffPercentage);
  }

  @Test
  public void testImagesWithDimensionsDifference() {
    when(actualImage.getWidth()).thenReturn(WIDTH + 10);
    when(actualImage.getHeight()).thenReturn(HEIGHT + 10);
    ImageDiff diff = comparator.compare(ACTUAL_FILE, ORIGIN_FILE);
    assertThat(diff.isFailed()).isTrue();
    assertThat(diff.getDiffPercentage()).isEqualTo(21d);
  }
}
