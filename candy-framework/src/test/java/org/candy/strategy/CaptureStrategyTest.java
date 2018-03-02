package org.candy.strategy;

import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;


import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit test for {@link CaptureStrategy}.
 */
@RunWith(MockitoJUnitRunner.class)
public class CaptureStrategyTest {
  @Mock FirefoxDriver driver;
  @Mock WebElement targetElement;
  @Mock WebElement elementToExclude;

  private CaptureStrategy strategy;

  @Test
  public void testCaptureWholeScreen() {
    strategy = CaptureStrategy.builder(driver).build();
    strategy.capture();

    verify(driver).getScreenshotAs(OutputType.BYTES);
  }

  @Test
  public void testCaptureTargetElement() {
    when(targetElement.isDisplayed()).thenReturn(true);

    strategy = CaptureStrategy.builder(driver).withTargetElement(targetElement).build();
    strategy.capture();

    verify(targetElement).getScreenshotAs(OutputType.BYTES);
  }

  @Test
  public void testCaptureWithElementsToExclude() {
    strategy = CaptureStrategy.builder(driver)
        .withTargetElement(targetElement)
        .withElementsToExclude(ImmutableList.of(elementToExclude))
        .build();
    strategy.capture();
    verify(driver).executeScript("arguments[0].style", elementToExclude);
    verify(driver).executeScript("arguments[0].style.visibility='hidden'", elementToExclude);
    verify(driver).getScreenshotAs(OutputType.BYTES);
    verify(driver).executeScript("arguments[0].removeAttribute('style')", elementToExclude);
  }

  @Test
  public void testCaptureWithElementsToExcludeRestoreStyle() {
    when(driver.executeScript("arguments[0].style", elementToExclude)).thenReturn("display: block;");
    strategy = CaptureStrategy.builder(driver)
        .withTargetElement(targetElement)
        .withElementsToExclude(ImmutableList.of(elementToExclude))
        .build();
    strategy.capture();
    verify(driver).executeScript("arguments[0].style", elementToExclude);
    verify(driver).executeScript("arguments[0].style.visibility='hidden'", elementToExclude);
    verify(driver).getScreenshotAs(OutputType.BYTES);
    verify(driver).executeScript("arguments[0].style='display: block;'", elementToExclude);
  }
}
