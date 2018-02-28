package org.candy.generic.comparison;

import java.awt.image.BufferedImage;

/**
 *  Compares two images pixel by pixel.
 */
public class StraightImageComparator extends ImageComparator {
  @Override
  double calculatePixelsDifference(BufferedImage actual, BufferedImage origin, int minWidth, int minHeight) {
    double diff = 0;
    for (int y = 0; y < minHeight; y++) {
      for (int x = 0; x < minWidth; x++) {
        if (actual.getRGB(x, y) != origin.getRGB(x, y)) {
          diff++;
        }
      }
    }
    return diff;
  }
}
