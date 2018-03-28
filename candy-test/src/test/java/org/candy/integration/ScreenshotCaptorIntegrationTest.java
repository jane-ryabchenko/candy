package org.candy.integration;

import com.google.common.collect.ImmutableList;
import org.candy.CandyTestWatcher;
import org.candy.ScreenshotCaptor;
import org.candy.annotation.ShouldFail;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 *  Integration test for {@link ScreenshotCaptor}.
 */
@RunWith(JUnit4.class)
public class ScreenshotCaptorIntegrationTest extends BaseScreenshotCaptorIntegrationTest {
  @Rule
  public CandyTestWatcher watcher = new CandyTestWatcher(captor);

  @Override
  public void before() {
    super.before();
    open("test.html");
  }

  @Test
  public void testCaptureScreenshot() {
    WebElement targetElement = driver.findElement(By.tagName("body"));
    WebElement exclusion = driver.findElement(By.id("header2"));
    captor.screenshot("capturedBodyScreenshotWithExclusion", targetElement, ImmutableList.of(exclusion));
    WebElement exclusion2 = driver.findElement(By.id("header1"));
    captor
        .screenshot("capturedBodyScreenshotWithExclusion", targetElement, ImmutableList.of(exclusion, exclusion2));
    captor.screenshot("capturedBodyScreenshot", targetElement);
    captor.screenshot("capturedPageScreenshot");
    captor.screenshot("capturedPageScreenshot");
  }

  @Test
  @ShouldFail
  public void testCaptureFailingScreenshot() {
    WebElement targetElement = driver.findElement(By.tagName("body"));
    WebElement exclusion = driver.findElement(By.id("header2"));
    captor.screenshot("capturedBodyScreenshot", targetElement, ImmutableList.of(exclusion));
  }
}
