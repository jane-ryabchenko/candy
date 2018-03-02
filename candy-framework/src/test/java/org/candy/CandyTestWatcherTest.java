package org.candy;

import com.google.common.collect.ImmutableList;
import java.lang.annotation.Annotation;
import java.util.List;
import org.candy.annotation.ShouldFail;
import org.candy.generic.CandyException;
import org.candy.generic.comparison.ImageComparisonException;
import org.candy.generic.comparison.ImageDiff;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runners.model.MultipleFailureException;
import org.junit.runners.model.Statement;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


import static com.google.common.truth.Truth.assertThat;
import static org.candy.generic.test.TestUtils.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit test for {@link CandyTestWatcher}.
 */
@RunWith(MockitoJUnitRunner.class)
public class CandyTestWatcherTest {
  private final static String METHOD_NAME = "testMethod";
  private final static String METHOD_DISPLAY_NAME = "testMethod(FooTest)";
  private static final String ACTUAL_FILE = "Actual";
  private static final String ORIGIN_FILE = "Origin";
  private static final String EXPECTED_DIFF_MSG = "Image difference is 21.00%\nOrigin: Origin\nActual: Actual";
  private static final String EXPECTED_CANDY_MSG = "Test method testMethod(FooTest) was expected to fail.";
  private static final String EXPECTED_MESSAGE = "There were 1 errors:\n  " +
      "org.candy.generic.comparison.ImageComparisonException(Image difference is 21.00%\n" +
      "Origin: Origin\nActual: Actual)";

  @Mock ScreenshotCaptor captor;
  @Mock Statement statement;
  @Mock Description description;

  private CandyTestWatcher watcher;

  @Before
  public void before() {
    watcher = new CandyTestWatcher(captor);

    when(description.getAnnotation(ShouldFail.class)).thenReturn(new ShouldFail() {
      @Override
      public Class<? extends Annotation> annotationType() {
        return null;
      }
    });
    when(description.getMethodName()).thenReturn(METHOD_NAME);
    when(description.getDisplayName()).thenReturn(METHOD_DISPLAY_NAME);
    when(captor.getImageDiffs()).thenReturn(ImmutableList.of(new ImageDiff(ACTUAL_FILE, ORIGIN_FILE, 21d)));
  }

  @Test
  public void testApplyShouldNotFailNoDiffs() throws Throwable {
    when(captor.getImageDiffs()).thenReturn(ImmutableList.of(new ImageDiff(ACTUAL_FILE, ORIGIN_FILE, 0d)));
    when(description.getAnnotation(ShouldFail.class)).thenReturn(null);

    watcher.apply(statement, description).evaluate();

    verify(captor).initCaptor(METHOD_NAME);
    verify(statement).evaluate();
  }

  @Test
  public void testApplyShouldFailWithDiffs() throws Throwable {
    watcher.apply(statement, description).evaluate();

    verify(captor).initCaptor(METHOD_NAME);
    verify(statement).evaluate();
  }

  @Test
  public void testApplyShouldNotFailWithDiffs() throws Throwable {
    when(description.getAnnotation(ShouldFail.class)).thenReturn(null);

    MultipleFailureException ex =
        assertThrows(MultipleFailureException.class, EXPECTED_MESSAGE, watcher.apply(statement, description)::evaluate);

    verify(captor).initCaptor(METHOD_NAME);
    verify(statement).evaluate();

    List<Throwable> failures = ex.getFailures();
    assertThat(failures).isNotNull();
    assertThat(failures).hasSize(1);
    assertThat(failures.get(0)).isInstanceOf(ImageComparisonException.class);
    assertThat(failures.get(0).getMessage()).isEqualTo(EXPECTED_DIFF_MSG);
  }

  @Test
  public void testApplyShouldFailNoDiffs() throws Throwable {
    when(captor.getImageDiffs()).thenReturn(ImmutableList.of(new ImageDiff(ACTUAL_FILE, ORIGIN_FILE, 0d)));

    assertThrows(CandyException.class, EXPECTED_CANDY_MSG, watcher.apply(statement, description)::evaluate);

    verify(captor).initCaptor(METHOD_NAME);
    verify(statement).evaluate();
  }
}
