package com.oneops.certs.util;

/**
 * A throwing supplier. Useful in lambda expressions/streams.
 *
 * @param <T> the type of results supplied by this supplier
 * @param <E> the type of checked exception thrown by this supplier.
 */
@FunctionalInterface
public interface ThrowingSupplier<T, E extends Exception> {

  /**
   * Gets a result.
   *
   * @return a result
   */
  T get() throws E;
}
