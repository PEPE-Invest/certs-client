package com.oneops.certs.security;

import com.google.auto.value.AutoValue;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * A random password string generator for CWS.
 *
 * @author Suresh
 */
@AutoValue
public abstract class PasswordGen {

  private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
  private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
  private static final String NUMBER = "0123456789";
  private static final String SPECIAL = "~!@#$%^&*+=";

  private final Random rand = new Random(System.nanoTime());

  /** Set true to include lower case characters. */
  public abstract boolean lower();

  /** Set true to include upper case characters. */
  public abstract boolean upper();

  /** Set true to include digits. */
  public abstract boolean number();

  /** Set true to include special case characters. */
  public abstract boolean special();

  /**
   * Generates a random password with given length based on the properties set.
   *
   * @param length length of the password.
   * @return generated password string.
   */
  public String generate(int length) {

    if (length <= 0) return "";

    List<String> categories = new ArrayList<>(4);
    if (lower()) {
      categories.add(LOWER);
    }
    if (upper()) {
      categories.add(UPPER);
    }
    if (number()) {
      categories.add(NUMBER);
    }
    if (special()) {
      categories.add(SPECIAL);
    }

    // Make random for each generation.
    Collections.shuffle(categories);

    int catSize = categories.size();
    StringBuilder buf = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      String categoryStr = categories.get(i % catSize);
      int pos = rand.nextInt(categoryStr.length());
      buf.append(categoryStr.charAt(pos));
    }
    return buf.toString();
  }

  public abstract Builder toBuilder();

  public static Builder builder() {
    return new AutoValue_PasswordGen.Builder().lower(true).upper(true).number(true).special(true);
  }

  @AutoValue.Builder
  public abstract static class Builder {

    public abstract Builder lower(boolean lower);

    public abstract Builder upper(boolean upper);

    public abstract Builder number(boolean number);

    public abstract Builder special(boolean special);

    public abstract PasswordGen build();
  }
}
