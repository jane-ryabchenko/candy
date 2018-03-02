package org.candy.generic.test;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Utilities class for unit tests.
 */
public final class TestUtils {
  private TestUtils() {}

  public static <T extends Throwable> T
      assertThrows(Class<T> expectedException, String expectedMessage, Executor executor) {
    try {
      executor.execute();
      assertTrue("Expected " + expectedException + " was never thrown.", false);
    } catch (Throwable t) {
      assertThat(t.getClass()).isEqualTo(expectedException);
      assertThat(t.getMessage()).isEqualTo(expectedMessage);
      return expectedException.cast(t);
    }
    return null;
  }
}
