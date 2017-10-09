// Copyright 2015 Google Inc. All Rights Reserved.
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

package com.google.devpartners.webperformance.rest;

import java.lang.reflect.Type;
import java.net.URL;
import java.util.List;

import javax.cache.CacheException;

import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;

import com.google.api.server.spi.config.ApiMethod;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.common.collect.Lists;
import com.google.devpartners.homemonitor.RestServer;
import com.google.devpartners.webperformance.model.Device;
import com.google.devpartners.webperformance.util.CacheUtil;
import com.google.devpartners.webperformance.util.GsonUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

/**
 * Rest entry point to get, create or update WebPageUrls.
 * 
 * @author jtoledo@google.com
 */
public class DeviceRest extends AbstractBaseResource {

  /**
   * Gets a WebPageUrl by PartnerId or a complete list of all the user's WebPageUrls if no PartnerId
   * is provided.
   * 
   * @return a JSON array of {@link Device}s. If a WebPageUrl ID is included in the request, the
   *         single WebPageUrl will still be returned within an array.
   */
  @Override
  @ApiMethod(name = "webpageurl")
  public Representation getHandler() {
    String result = null;

    try {
      String id = getParameter("id");
      URL url = getParameterAsUrl("url");

      List<Device> webPageUrls = Lists.newArrayList();
      if (id != null) {
        LOGGER.info("Getting WebPageUrl by id");
        Device webPageUrl = RestServer.getPersister().getByPrimaryId(Device.class, id);
        if (webPageUrl != null) {
          webPageUrls.add(webPageUrl);
        } else {
          throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND,
              "No WebPageUrl with that id was found");
        }
      } else if (url != null) {
        LOGGER.info("Getting WebPageUrl by url");
        webPageUrls = RestServer.getPersister().get(Device.class, Device.URL, url);
        if (webPageUrls.size() == 0) {
          throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND,
              "No WebPageUrl with that url was found");
        }

      } else {
        result = (String) CacheUtil.getCache().get(Device.WEBPAGEURL);
        if (result == null || result.length() == 0) {
          LOGGER.info("Getting all WebPageUrls");
          // Get all WebPageUrls and cache them
          webPageUrls = cacheWebPageUrls();
        } else {
          LOGGER.info("Getting all WebPageUrls from Cache");
        }
      }

      if (result == null) {
        result = gson.toJson(webPageUrls);
      }

    } catch (Exception exception) {
      return handleException(exception);
    }

    addReadOnlyHeaders();
    return createJsonResult(result);
  }

  @Override
  public Representation deleteHandler() {
    String result = null;

    try {
      String id = getParameter("id");
      if (id != null) {
        LOGGER.info("Deleting WebPageUrl...");
        Device webPageUrlToDelete =
            RestServer.getPersister().getByPrimaryId(Device.class, id);
        if (webPageUrlToDelete == null) {
          throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND,
              "No WebPageUrl with that id was found");  
        }
        RestServer.getPersister().remove(webPageUrlToDelete);
        result = "OK";

      } else {
        throw new IllegalArgumentException("Missing id for deleting WebPageUrl");
      }

      // Changes in WebPageUrls list, re-caching
      cacheWebPageUrls();

    } catch (Exception exception) {
      return handleException(exception);
    }
    addHeaders();
    return createJsonResult(result);
  }

  @Override
  public Representation postPutHandler(String json) {
    String result = null;

    try {
      if (this.getReference().getSegments().size() != 1) {
        throw new IllegalArgumentException(
            "We only support Post/Put in the base url: /webpageurl (no additional segments /{id} or /url/{url)");
      }

      JsonParser jsonParser = new JsonParser();
      JsonElement jsonElement = jsonParser.parse(json);

      List<String> newIds = Lists.newArrayList();
      if (jsonElement.isJsonArray()) {
        Type listType = new TypeToken<List<Device>>() {}.getType();
        List<Device> webPageUrls =
            GsonUtil.getGsonBuilder().create().fromJson(jsonElement, listType);

        for (Device webPageUrl : webPageUrls) {
          webPageUrl.generateId();
          webPageUrl.setCreated();

          Device existingWebPageUrl =
              RestServer.getPersister().getByPrimaryId(Device.class, webPageUrl.getId());
          if (existingWebPageUrl == null) {
            newIds.add(webPageUrl.getId());
          }
        }

        LOGGER.info("Persisting WebPageUrls...");
        RestServer.getPersister().save(webPageUrls);
        // Run task only for newIds
        QueueFactory.getQueue(TasksCreator.QUEUE_TASKCREATORS).add(
            TaskOptions.Builder.withPayload(new TasksCreator(newIds)));
        result = "OK";

      } else {
        Device webPageUrl =
            GsonUtil.getGsonBuilder().create().fromJson(jsonElement, Device.class);
        webPageUrl.generateId();
        webPageUrl.setCreated();

        Device existingWebPageUrl =
            RestServer.getPersister().getByPrimaryId(Device.class, webPageUrl.getId());
        if (existingWebPageUrl == null) {
          newIds.add(webPageUrl.getId());
        }

        LOGGER.info("Persisting WebPageUrl...");
        webPageUrl = RestServer.getPersister().save(webPageUrl);        
        // Run task only for newIds
        QueueFactory.getQueue(TasksCreator.QUEUE_TASKCREATORS).add(
            TaskOptions.Builder.withPayload(new TasksCreator(newIds)));

        result = gson.toJson(webPageUrl);
      }

      // Changes in WebPageUrls list, re-caching
      cacheWebPageUrls();

    } catch (Exception exception) {
      return handleException(exception);
    }
    addHeaders();
    return createJsonResult(result);
  }

  @SuppressWarnings("unchecked")
  public static List<Device> cacheWebPageUrls() throws CacheException {
    CacheUtil.getCache().remove(Device.WEBPAGEURL);
    List<Device> webPageUrls = RestServer.getPersister().get(Device.class);
    CacheUtil.getCache().put(Device.WEBPAGEURL, gson.toJson(webPageUrls));
    return webPageUrls;
  }
}
