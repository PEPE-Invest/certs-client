package com.oneops.certs.util;

/**
 * Throwable utils.
 *
 * @author Suresh
 */
public class Throwables {

  /**
   * Throws a checked exception without wrapping or without declaring it in method signature.
   *
   * @param t exception to be thrown.
   * @param <T> exception type.
   * @throws T exception.
   * @see <a href="https://stackoverflow.com/a/31316879/416868">Sneaky Throw</a>
   */
  @SuppressWarnings("unchecked")
  public static <T extends Throwable> void sneakyThrow(Throwable t) throws T {
    throw (T) t;
  }
}
