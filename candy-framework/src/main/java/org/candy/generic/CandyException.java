package org.candy.generic;

/**
 * Basic exception, extends {@link RuntimeException}.
 */
public class CandyException extends RuntimeException {
  public CandyException(String message) {
    super(message);
  }

  public CandyException(Throwable cause) {
    super(cause);
  }

  public CandyException(String message, Throwable cause) {
    super(message, cause);
  }
}
