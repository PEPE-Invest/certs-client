package com.oneops.certs.model;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonReader;
import com.squareup.moshi.JsonWriter;
import com.squareup.moshi.Moshi;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;

/**
 * A Moshi {@link JsonAdapter} for {@link Optional} types.
 *
 * @author Suresh
 */
public class OptionalAdapterFactory implements JsonAdapter.Factory {

  @Nullable
  @Override
  public JsonAdapter<?> create(Type type, Set<? extends Annotation> annotations, Moshi moshi) {
    if (type instanceof ParameterizedType) {
      ParameterizedType pType = ((ParameterizedType) type);
      if (Optional.class == pType.getRawType()) {
        Type optionalType = pType.getActualTypeArguments()[0];
        return new OptionalAdapter(moshi, optionalType);
      }
    }
    return null;
  }

  /** Optional adapter class. */
  private static class OptionalAdapter extends JsonAdapter<Optional<?>> {
    private final Moshi moshi;
    private final Type type;

    OptionalAdapter(Moshi moshi, Type type) {
      this.moshi = moshi;
      this.type = type;
    }

    @Override
    public Optional<?> fromJson(JsonReader in) throws IOException {
      // Delegate to adapter of the Optional value type.
      return Optional.ofNullable(moshi.adapter(type).fromJson(in));
    }

    @Override
    public void toJson(JsonWriter out, Optional<?> value) throws IOException {
      if (value.isPresent()) {
        // Delegate to adapter of the Optional value type.
        moshi.adapter(type).toJson(out, value.get());
      } else {
        // Write null if no value present.
        out.nullValue();
      }
    }
  }
}
