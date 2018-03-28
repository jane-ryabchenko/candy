package org.candy;

import com.google.common.collect.ImmutableList;
import org.candy.generic.comparison.ImageDiff;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;


import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit test for {@link ScreenshotCaptor}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ScreenshotCaptorTest {
  private static final String NAME = "wholePage";
  private static final String PREFIX = "testMethod";
  private static final byte[] SCREENSHOT1 = new byte[1];
  private static final byte[] SCREENSHOT2 = new byte[2];
  private static final String ACTUAL_FILE = "actual/testMethod_wholePage.png";
  private static final String ACTUAL_INFO = "1280x1080";
  private static final String ORIGIN_FILE = "origin/testMethod_wholePage.png";
  private static final String ORIGIN_INFO = "1276x1080";
  private static final ImageDiff DIFF = new ImageDiff(ACTUAL_FILE, ACTUAL_INFO, ORIGIN_FILE, ORIGIN_INFO, 21d);

  @Mock FirefoxDriver driver;
  @Mock WebElement targetElement;
  @Mock WebElement elementToExclude;

  private ScreenshotCaptor captor;

  @Before
  public void before() {
    GlobalContext.setOriginFolder("origin");
    GlobalContext.setActualFolder("actual");
    captor = Mockito.spy(new ScreenshotCaptor(driver));
    captor.initCaptor(PREFIX);
    doNothing().when(captor).writeFile(any(byte[].class), anyString(), anyString());
    doReturn(DIFF).when(captor).compare(anyString(), anyString());
    when(driver.getScreenshotAs(OutputType.BYTES)).thenReturn(SCREENSHOT1);
    when(targetElement.isDisplayed()).thenReturn(true);
    when(targetElement.getScreenshotAs(OutputType.BYTES)).thenReturn(SCREENSHOT2);
  }

  @Test
  public void testScreenshot() {
    captor.screenshot(NAME);

    assertThat(captor.getImageDiffs()).containsExactly(DIFF);

    verify(captor).writeFile(SCREENSHOT1, ACTUAL_FILE, "actual");
    verify(captor).compare(ACTUAL_FILE, ORIGIN_FILE);
  }

  @Test
  public void testScreenshotWithTargetElement() {
    captor.screenshot(NAME, targetElement);

    assertThat(captor.getImageDiffs()).containsExactly(DIFF);

    verify(captor).writeFile(SCREENSHOT2, ACTUAL_FILE, "actual");
    verify(captor).compare(ACTUAL_FILE, ORIGIN_FILE);
  }

  @Test
  public void testScreenshotWithElementsToExclude() {
    captor.screenshot(NAME, targetElement, ImmutableList.of(elementToExclude));

    assertThat(captor.getImageDiffs()).containsExactly(DIFF);

    verify(captor).writeFile(SCREENSHOT2, ACTUAL_FILE, "actual");
    verify(captor).compare(ACTUAL_FILE, ORIGIN_FILE);
  }
}
