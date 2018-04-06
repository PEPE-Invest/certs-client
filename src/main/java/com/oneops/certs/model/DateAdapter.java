package com.oneops.certs.model;

import static java.time.format.DateTimeFormatter.ofPattern;

import com.squareup.moshi.FromJson;
import com.squareup.moshi.ToJson;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Json date adapter.
 *
 * @author Suresh
 */
public class DateAdapter {

  /** The date pattern used in cert service request/response. */
  private static final DateTimeFormatter formatter = ofPattern("MM/dd/yyyy HH:mm:ss");

  @ToJson
  String toJson(LocalDateTime date) {
    return formatter.format(date);
  }

  @FromJson
  LocalDateTime fromJson(String date) {
    return LocalDateTime.from(formatter.parse(date));
  }
}
