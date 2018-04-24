package com.oneops.certs.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.oneops.certs.model.PreConditions;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Password generator unit tests.
 *
 * @author Suresh
 */
class PasswordGenTest {

  private Pattern lowerCharPattern = Pattern.compile("([a-z])");
  private Pattern upperCharPattern = Pattern.compile("([A-Z])");
  private Pattern numberPattern = Pattern.compile("([\\d])");
  private Pattern specialCharPattern = Pattern.compile("([~!@#$%^&*+=])");

  @Test
  @DisplayName("Cws Password gen test")
  void generate() {
    PasswordGen pg = PasswordGen.builder().build();
    for (int i = 0; i < 10; i++) {
      int passwdLength = 15;
      String passwd = pg.generate(passwdLength);
      PreConditions.checkPassword(passwd);
      int lower = countMatch(lowerCharPattern.matcher(passwd));
      int upper = countMatch(upperCharPattern.matcher(passwd));
      int number = countMatch(numberPattern.matcher(passwd));
      int special = countMatch(specialCharPattern.matcher(passwd));

      assertTrue(lower > 0);
      assertTrue(upper > 0);
      assertTrue(number > 0);
      assertTrue(special > 0);
      assertEquals(passwdLength, passwd.length());
      assertEquals(passwdLength, lower + upper + number + special);
    }
  }

  @Test
  @DisplayName("Random Password gen test")
  void generate1() {
    PasswordGen pg = PasswordGen.builder().lower(false).number(false).build();
    for (int i = 0; i < 5; i++) {
      int passwdLength = 20;
      String passwd = pg.generate(passwdLength);
      int upper = countMatch(upperCharPattern.matcher(passwd));
      int special = countMatch(specialCharPattern.matcher(passwd));

      assertTrue(upper > 0);
      assertTrue(special > 0);
      assertEquals(passwdLength, passwd.length());
      assertEquals(passwdLength, upper + special);
    }
  }

  /** Returns the match group count. */
  int countMatch(Matcher matcher) {
    int count = 0;
    while (matcher.find()) {
      count++;
    }
    return count;
  }
}
