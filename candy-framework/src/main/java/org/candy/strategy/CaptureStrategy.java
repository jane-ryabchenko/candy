package org.candy.strategy;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import org.candy.strategy.impl.CaptureStrategyImpl;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Interface for screenshot capture strategy.
 */
public abstract class CaptureStrategy {
  public static final class Builder<T extends WebDriver & JavascriptExecutor & TakesScreenshot> {
    private T driver;
    private WebElement targetElement;
    private Iterable<WebElement> elementsToExclude;

    private Builder(T driver) {
      this.driver = driver;
    }

    public Builder withTargetElement(WebElement targetElement) {
      this.targetElement = checkNotNull(targetElement);
      return this;
    }

    public Builder withElementsToExclude(Iterable<WebElement> elementsToExclude) {
      this.elementsToExclude = checkNotNull(elementsToExclude);
      return this;
    }

    public CaptureStrategy build() {
      CaptureStrategy strategy = new CaptureStrategyImpl();
      strategy.setWebDriver(checkNotNull(driver));
      if (targetElement != null) {
        strategy.setTargetElement(targetElement);
      }
      if (elementsToExclude != null) {
        strategy.setElementsToExclude(elementsToExclude);
      }
      return strategy;
    }
  }

  public static <T extends WebDriver & JavascriptExecutor & TakesScreenshot> Builder builder(T driver) {
    return new Builder<>(driver);
  }

  public abstract byte[] capture();

  protected abstract <T extends WebDriver & JavascriptExecutor & TakesScreenshot> void setWebDriver(T driver);
  protected abstract void setTargetElement(WebElement targetElement);
  protected abstract void setElementsToExclude(Iterable<WebElement> elementsToExclude);
}
