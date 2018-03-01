package org.candy.generic.comparison;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Compare images by pixels color difference.
 */
public class ColorImageComparator extends ImageComparator {
  private static final double EPS = 216d / 24389;
  private static final double COEFFICIENT = 24389d / 27;
  private static final double X_REF = 0.964221;  // reference white D50
  private static final double Y_REF = 1d;
  private static final double Z_REF = 0.825211;

  @Override
  double calculatePixelsDifference(BufferedImage actual, BufferedImage origin, int minWidth, int minHeight) {
    double diff = 0;
    for (int y = 0; y < minHeight; y++) {
      for (int x = 0; x < minWidth; x++) {
        int actualRGB = actual.getRGB(x, y);
        int originRGB = origin.getRGB(x, y);
        if (actualRGB != originRGB) {
          int[] lab1 = rgb2lab(actualRGB);
          int[] lab2 = rgb2lab(originRGB);
          double dh = Math.sqrt(
              Math.pow(lab2[0] - lab1[0], 2) + Math.pow(lab2[1] - lab1[1], 2) + Math.pow(lab2[2] - lab1[2], 2));
          diff += dh / Math.sqrt(Math.pow(255, 2) + Math.pow(255, 2) + Math.pow(255, 2));
        }
      }
    }
    return diff;
  }

  private static int[] rgb2lab(int rgb) {
    Color color = new Color(rgb);
    // RGB to XYZ
    double r = transform(color.getRed());
    double g = transform(color.getGreen());
    double b = transform(color.getBlue());

    double x = 0.436052025 * r + 0.385081593 * g + 0.143087414 * b;
    double y = 0.222491598 * r + 0.71688606 * g + 0.060621486 * b;
    double z = 0.013929122 * r + 0.097097002 * g + 0.71418547 * b;

    // XYZ to Lab
    double fx = transform(x / X_REF);
    double fy = transform(y / Y_REF);
    double fz = transform(z / Z_REF);

    double ls = (116 * fy) - 16;
    double as = 500 * (fx - fy);
    double bs = 200 * (fy - fz);

    int[] lab = new int[3];
    lab[0] = (int) (2.55 * ls + .5);
    lab[1] = (int) (as + .5);
    lab[2] = (int) (bs + .5);
    return lab;
  }

  private static double transform(int color) {
    double c = color / 255d;
    // assuming sRGB (D65)
    if (c <= 0.04045) {
      return c / 12;
    }
    return Math.pow((c + 0.055d) / 1.055d, 2.4d);
  }

  private static double transform(double xr) {
    // XYZ to Lab
    if (xr > EPS) {
      return Math.pow(xr, 1 / 3d);
    }
    return (COEFFICIENT * xr + 16) / 116d;
  }
}
