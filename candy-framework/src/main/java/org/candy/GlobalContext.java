package org.candy;

import java.util.HashMap;
import java.util.Map;
import org.candy.generic.comparison.ImageComparator;
import org.candy.generic.comparison.StraightImageComparator;
import org.candy.strategy.CaptureStrategy;


import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public final class GlobalContext {
  private static Map<String, Object> mapContext = new HashMap<>();

  private GlobalContext() {}

  public static String getOriginFolder() {
    return (String) checkNotNull(mapContext.get("candy.origin.folder"));
  }

  public static void setOriginFolder(String originFolder) {
    mapContext.put("candy.origin.folder", originFolder);
  }

  public static String getActualFoider() {
    return (String) checkNotNull(mapContext.get("candy.actual.folder"));
  }

  public static void setActualFolder(String actualFolder) {
    mapContext.put("candy.actual.folder", actualFolder);
  }

  public static Object getCaptureStrategy() {
    return mapContext.getOrDefault("captureStrategy", CaptureStrategy.class);
  }

  public static void setCaptureStrategy(Class<? extends CaptureStrategy> strategyClass) {
    mapContext.put("captureStrategy", strategyClass);
  }

  public static Object getImageComparator() {
    return mapContext.getOrDefault("imageComparator", StraightImageComparator.class);
  }

  public static void setImageComparator(Class<? extends ImageComparator> comparatorClass) {
    mapContext.put("imageComparator", comparatorClass);
  }

  public static int getRetries() {
    return (int) mapContext.getOrDefault("candy.capture.retries", 1);
  }

  public static void setRetries(int retries) {
    checkArgument(retries > 0);
    mapContext.put("candy.capture.retries", retries);
  }
}
