package org.candy.strategy.impl;

import org.candy.generic.Command;
import org.candy.strategy.CaptureStrategy;
import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.*;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Default implementation of {@link CaptureStrategy}.
 */
public class CaptureStrategyImpl extends CaptureStrategy {
  private JavascriptExecutor javascriptExecutor;
  private TakesScreenshot takesScreenshot;
  private WebElement targetElement;
  private List<Command> preCaptureCommands = new ArrayList<>();
  private List<Command> postCaptureCommands = new ArrayList<>();

  @Override
  public byte[] capture() {
    for (Command preCapture : preCaptureCommands) {
      preCapture.execute();
    }

    byte[] screenshot;
    if (targetElement != null && targetElement.isDisplayed()) {
      screenshot = targetElement.getScreenshotAs(OutputType.BYTES);
    } else {
      screenshot = takesScreenshot.getScreenshotAs(OutputType.BYTES);
    }

    for (Command postCapture : postCaptureCommands) {
      postCapture.execute();
    }
    return screenshot;
  }

  @Override
  protected <T extends WebDriver & JavascriptExecutor & TakesScreenshot> void setWebDriver(T driver) {
    this.javascriptExecutor = checkNotNull(driver);
    this.takesScreenshot = checkNotNull(driver);
  }

  @Override
  protected void setTargetElement(WebElement targetElement) {
    this.targetElement = checkNotNull(targetElement);
  }

  @Override
  protected void setElementsToExclude(Iterable<WebElement> elementsToExclude) {
    for (WebElement exclusion : elementsToExclude) {
      String style = addPreCaptureCommand(exclusion);
      addPostCaptureCommand(exclusion, style);
    }
  }

  private String addPreCaptureCommand(WebElement exclusion) {
    String style = (String) javascriptExecutor.executeScript("arguments[0].style", exclusion);
    preCaptureCommands.add(
        () -> javascriptExecutor.executeScript("arguments[0].style.visibility='hidden'", exclusion));
    return style;
  }

  private void addPostCaptureCommand(WebElement exclusion, String style) {
    postCaptureCommands.add(() -> {
      if (StringUtils.isBlank(style)) {
        javascriptExecutor.executeScript("arguments[0].removeAttribute('style')", exclusion);
      } else {
        javascriptExecutor.executeScript("arguments[0].style='" + style + "'", exclusion);
      }
    });
  }
}
