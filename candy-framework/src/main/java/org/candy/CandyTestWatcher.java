package org.candy;

import com.google.common.annotations.VisibleForTesting;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.candy.annotation.ShouldFail;
import org.candy.generic.CandyException;
import org.candy.generic.comparison.ImageDiff;
import org.candy.report.TestReport;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runners.model.MultipleFailureException;
import org.junit.runners.model.Statement;


import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Test watcher for Candy.
 */
public class CandyTestWatcher extends TestWatcher {
  private ScreenshotCaptor captor;

  public CandyTestWatcher(ScreenshotCaptor captor) {
    this.captor = checkNotNull(captor);
  }

  @Override
  public Statement apply(Statement base, Description description) {
    Statement statement = new Statement() {
      @Override
      public void evaluate() throws Throwable {
        captor.initCaptor(description.getMethodName());

        base.evaluate();

        TestReport report = new TestReport(description);
        List<Throwable> failedScreenshotExceptions = new ArrayList<>();
        for (ImageDiff diff : captor.getImageDiffs()) {
          try {
            report.addComparison(diff);
            diff.verify();
          } catch (CandyException ex) {
            failedScreenshotExceptions.add(ex);
          }
        }
        writeReport(report, description.getClassName() + "_" + description.getMethodName() + ".xml");

        boolean shouldFail = description.getAnnotation(ShouldFail.class) != null;
        if (shouldFail && failedScreenshotExceptions.isEmpty()) {
          throw new CandyException("Test method " + description.getDisplayName() + " was expected to fail.");
        } else if (!shouldFail && !failedScreenshotExceptions.isEmpty()) {
          throw new MultipleFailureException(failedScreenshotExceptions);
        }
      }
    };
    return super.apply(statement, description);
  }

  @VisibleForTesting
  void writeReport(TestReport report, String fileName) {
    report.writeReport(new File(GlobalContext.getActualFoider() + "/" + fileName));
  }
}
