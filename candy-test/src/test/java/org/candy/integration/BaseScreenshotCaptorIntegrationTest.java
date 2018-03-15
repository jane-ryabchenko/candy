package org.candy.integration;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import org.candy.ScreenshotCaptor;
import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.GeckoDriverService;

/**
 * Base class for integration tests.
 */
public class BaseScreenshotCaptorIntegrationTest {
  private static final String EXPECTED_FOLDER = "src/test/resources/candy-origin";
  private static final String OUTPUT_FOLDER = "target/candy-actual";
  FirefoxDriver driver;
  ScreenshotCaptor captor = new ScreenshotCaptor();

  @Before
  public void before() {
    System.setProperty("candy.origin.folder", EXPECTED_FOLDER);
    System.setProperty("candy.actual.folder", OUTPUT_FOLDER);
    System.setProperty(
        GeckoDriverService.GECKO_DRIVER_EXE_PROPERTY, "src/test/resources/web-driver/geckodriver");

    driver = new FirefoxDriver();
    driver.manage().timeouts().pageLoadTimeout(90, TimeUnit.SECONDS);
    driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);

    captor.setDriver(driver);
  }

  @After
  public void after() {
    driver.close();
  }

  void open(String testHtml) {
    Path path = Paths.get("src/test/resources/" + testHtml);
    driver.get("file://" + path.toAbsolutePath());
//    captor.setContentSize(1802, 668);
  }
}
