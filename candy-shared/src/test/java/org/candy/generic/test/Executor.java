package org.candy.generic.test;

/**
 * Executor functionsl interface for {@link TestUtils}.
 */
@FunctionalInterface
public interface Executor {
  void execute() throws Throwable;
}
