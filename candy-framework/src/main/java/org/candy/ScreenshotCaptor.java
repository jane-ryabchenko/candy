package org.candy;

import com.google.common.annotations.VisibleForTesting;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.Nullable;
import org.candy.generic.CandyException;
import org.candy.generic.comparison.ImageComparator;
import org.candy.generic.comparison.ImageDiff;
import org.candy.generic.comparison.StraightImageComparator;
import org.candy.strategy.CaptureStrategy;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;


import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Screenshot captor class to take an actual screenshot and compare with expected.
 */
public class ScreenshotCaptor {
  private WebDriver driver;
  private Map<String, ImageDiff> imageDiffMap;
  private String screenshotPrefix;
  private ImageComparator imageComparator;

  public ScreenshotCaptor() {
    imageComparator = new StraightImageComparator();
    initCaptor("");
  }

  public <T extends WebDriver & JavascriptExecutor & TakesScreenshot> ScreenshotCaptor(T driver) {
    this();
    setDriver(driver);
  }

  public <T extends WebDriver & JavascriptExecutor & TakesScreenshot> void setDriver(T driver) {
    this.driver = checkNotNull(driver);
  }

  public void screenshot(String name) {
    CaptureStrategy captureStrategy =
        CaptureStrategy.builder((WebDriver & JavascriptExecutor & TakesScreenshot) driver).build();
    captureScreenshot(name, captureStrategy);
  }

  public void screenshot(String name, WebElement targetElement) {
    CaptureStrategy captureStrategy =
        CaptureStrategy
            .builder((WebDriver & JavascriptExecutor & TakesScreenshot) driver)
            .withTargetElement(targetElement)
            .build();
    captureScreenshot(name, captureStrategy);
  }

  public void screenshot(String name, WebElement targetElement, Iterable<WebElement> elementsToExclude) {
    CaptureStrategy captureStrategy =
        CaptureStrategy
            .builder((WebDriver & JavascriptExecutor & TakesScreenshot) driver)
            .withTargetElement(targetElement)
            .withElementsToExclude(elementsToExclude)
            .build();
    captureScreenshot(name, captureStrategy);
  }

  void initCaptor(String screenshotPrefix) {
    this.screenshotPrefix = screenshotPrefix;
    imageDiffMap = new LinkedHashMap<>();
  }

  Collection<ImageDiff> getImageDiffs() {
    return imageDiffMap.values();
  }

  @VisibleForTesting
  void writeFile(byte[] content, String fileName, @Nullable String folderName) {
    if (folderName != null) {
      File dir = new File(folderName);
      if (dir.exists()) {
        dir.delete();
      }
      dir.mkdirs();
    }
    try (FileOutputStream output = new FileOutputStream(fileName)) {
      output.write(content);
    } catch (IOException ex) {
      throw new CandyException(ex);
    }
  }

  @VisibleForTesting
  ImageDiff compare(String actualFileName, String originFileName) {
    return imageComparator.compare(actualFileName, originFileName);
  }

  private void captureScreenshot(String name, CaptureStrategy captureStrategy) {
    int tries = Math.max(Integer.valueOf(System.getProperty("candy.capture.tries", "1")), 1);
    name = getUniqueName(name);
    ImageDiff diff;
    for (int i = 0; i < tries; i++) {
      byte[] screenshot = captureStrategy.capture();
      String actualFile = writeScreenshot(name, screenshot);
      String originFile =
          System.getProperty("candy.origin.folder", "origin") + "/" + screenshotPrefix + "_" + name + ".png";
      diff = compare(actualFile, originFile);
      if (!diff.isFailed() || i == tries - 1) {
        imageDiffMap.put(name, diff);
        break;
      }
    }
  }

  private String writeScreenshot(String name, byte[] screenshot) {
    String folderName = System.getProperty("candy.actual.folder", "actual");
    String fileName = folderName + "/" + screenshotPrefix + "_" + name + ".png";
    writeFile(screenshot, fileName, folderName);
    return fileName;
  }

  private String getUniqueName(String name) {
    if (imageDiffMap.containsKey(name)) {
      int c = 1;
      do {
        c++;
      } while (imageDiffMap.containsKey(name + c));
      name += c;
    }
    return name;
  }
}
