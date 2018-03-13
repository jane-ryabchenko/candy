package org.candy.generic.comparison;

import com.google.common.annotations.VisibleForTesting;
import org.candy.generic.CandyException;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * Compares two images.
 */
public abstract class ImageComparator {
  public ImageDiff compare(String actualFileName, String originFileName) {
    BufferedImage actual = null;
    BufferedImage origin = null;

    String actualInfo = actualFileName;
    String originInfo = originFileName;
    try {
      actual = readImage(actualFileName);
      actualInfo = actual.getWidth() + "x" + actual.getHeight();

      origin = readImage(originFileName);
      originInfo += " " + origin.getWidth() + "x" + origin.getHeight();
    } catch (FileNotFoundException ex) {
      if (actual == null) {
        throw new CandyException("Actual screenshot file not found.", ex);
      }
    } catch (IOException ex) {
      throw new CandyException("Unknown error.", ex);
    }

    double diffPercentage = compare(actual, origin);
    return new ImageDiff(actualInfo, originInfo, diffPercentage);
  }

  @VisibleForTesting
  BufferedImage readImage(String fileName) throws IOException {
    try (FileInputStream inputStream = new FileInputStream(new File(fileName))) {
      return ImageIO.read(inputStream);
    }
  }

  abstract double calculatePixelsDifference(
      BufferedImage actual, BufferedImage origin, int minWidth, int minHeight);

  private double compare(BufferedImage actual, @Nullable BufferedImage origin)  {
    if (origin == null) {
      return 100d;
    }

    final int maxWidth = Math.max(actual.getWidth(), origin.getWidth());
    final int maxHeight = Math.max(actual.getHeight(), origin.getHeight());
    final int minWidth = Math.min(actual.getWidth(), origin.getWidth());
    final int minHeight = Math.min(actual.getHeight(), origin.getHeight());

    double diff = maxWidth * (double) maxHeight - minWidth * (double) minHeight;
    diff += calculatePixelsDifference(actual, origin, minWidth, minHeight);

    return 100d * diff / (origin.getWidth() * origin.getHeight());
  }
}
