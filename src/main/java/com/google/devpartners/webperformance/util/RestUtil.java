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

package com.google.devpartners.webperformance.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.restlet.Client;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Preference;
import org.restlet.data.Protocol;
import org.restlet.representation.Representation;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Util class for Rest Calls.
 *
 * @author jtoledo@google.com (Julian Toledo)
 */
public class RestUtil {

  /**
   * Writes the list of objects to the {@link OutputStream} in the readable
   * form, using the configured {@link Gson} instance.
   *
   * @param URL the {@code URL} to call
   * @return JSONObject the {@code JSONObject} with the results
   * @throws IOException with the response if cannot be parsed to JSON
   */
  public static JsonObject restGet(String URL) throws IOException {
    return restGet(URL, null);
  }

  /**
   * Writes the list of objects to the {@link OutputStream} in the readable
   * form, using the configured {@link Gson} instance.
   *
   * @param URL the {@code URL} to call
   * @return JSONObject the {@code JSONObject} with the results
   * @throws IOException with the response if cannot be parsed to JSON
   */
  public static JsonObject restGet(String URL, List<String> filter) throws IOException {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    Client client = new Client(Protocol.HTTP);
    Request request = new Request();
    request.setResourceRef(URL.toString());
    request.setMethod(Method.GET);
    request.getClientInfo().getAcceptedMediaTypes()
        .add(new Preference<MediaType>(MediaType.APPLICATION_JSON));

    Response response = client.handle(request);
    if (response.getStatus().isSuccess()) {
      response.getEntity().write(outputStream);
      ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
      // remove screenshot in the memory stream while parsing the JSON
      return GsonUtil.readJsonStream(inputStream, filter).getAsJsonObject();
    } else {
      Representation representation = response.getEntity();
      throw new HttpRequestException(response.getStatus().getCode(),
          "Request was not successfull (" + response.getStatus().toString() + "), URL: " + URL + " " + representation.getText());
    }
  }
}
