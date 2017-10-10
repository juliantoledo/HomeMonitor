// Copyright 2012 Google Inc. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.devpartners.homemonitor.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

/**
 * Util class for Gson Builder.
 *
 * @author jtoledo@google.com (Julian Toledo)
 */
public class GsonUtil {

  private static final GsonBuilder gsonBuilder;

  static {
    gsonBuilder = new GsonBuilder();
    gsonBuilder.setDateFormat(DateUtil.FULL_DATE_TIME_FORMAT);
  }

  /**
   * @return the {@link GsonBuilder} used by this class.
   */
  public static GsonBuilder getGsonBuilder() {
    return gsonBuilder;
  }

  /**
   * Writes the list of objects to the {@link OutputStream} in the readable
   * form, using the configured {@link Gson} instance.
   *
   * @param gson the {@code Gson} instance
   * @param out the {@code OutputStream} to write the JSon
   * @param list the list of objects to be written on the {@code OutputStream}
   * @throws IOException error writing to the {@code OutputStream}
   */
  public static <T> void writeObjectsToStreamAsJson(Gson gson, OutputStream out, List<T> list)
      throws IOException {

    JsonWriter writer = new JsonWriter(new OutputStreamWriter(out, "UTF-8"));
    writer.setIndent("  ");
    writer.beginArray();
    for (T t : list) {
      gson.toJson(t, t.getClass(), writer);
    }
    writer.endArray();
    writer.close();
  }

  public static JsonObject readJsonStream(InputStream in) throws IOException {
    JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
    JsonElement jsonElement =
        GsonUtil.getGsonBuilder().create().fromJson(reader, JsonElement.class);
    reader.close();
    return jsonElement.getAsJsonObject();
  }

  public static JsonObject readJsonStream(InputStream in, List<String> filter) throws IOException {
    if (filter == null) {
      return readJsonStream(in);
    } else {
      JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
      StringWriter stringWriter = new StringWriter();
      JsonWriter writer = new JsonWriter(stringWriter);
      filterByJsonName(reader, writer, filter);
      JsonElement jsonElement =
          GsonUtil.getGsonBuilder().create().fromJson(stringWriter.toString(), JsonElement.class);
      return jsonElement.getAsJsonObject();
    }
  }

  static void filterByJsonName(JsonReader reader, JsonWriter writer, List<String> filter) throws IOException {
    while (true) {
      JsonToken token = reader.peek();
      switch (token) {
        case BEGIN_ARRAY:
          reader.beginArray();
          writer.beginArray();
          break;
        case END_ARRAY:
          reader.endArray();
          writer.endArray();
          break;
        case BEGIN_OBJECT:
          reader.beginObject();
          writer.beginObject();
          break;
        case END_OBJECT:
          reader.endObject();
          writer.endObject();
          break;
        case NAME:
          String name = reader.nextName();
          if (filter != null && filter.contains(name)) {
            reader.skipValue();
          } else {
            writer.name(name);
          }
          break;
        case STRING:
          String s = reader.nextString();
          writer.value(s);
          break;
        case NUMBER:
          String n = reader.nextString();
          writer.value(new BigDecimal(n));
          break;
        case BOOLEAN:
          boolean b = reader.nextBoolean();
          writer.value(b);
          break;
        case NULL:
          reader.nextNull();
          writer.nullValue();
          break;
        case END_DOCUMENT:
          writer.flush();
          reader.close();
          writer.close();
          return;
      }
    }
  }
}
