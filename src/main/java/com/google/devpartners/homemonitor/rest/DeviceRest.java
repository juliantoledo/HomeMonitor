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

package com.google.devpartners.homemonitor.rest;

import java.lang.reflect.Type;
import java.util.List;

import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;

import com.google.common.collect.Lists;
import com.google.devpartners.homemonitor.RestServer;
import com.google.devpartners.homemonitor.model.Device;
import com.google.devpartners.homemonitor.util.GsonUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

/**
 * Rest entry point to get, create or update Devices.
 * 
 * @author jtoledo@google.com
 */
public class DeviceRest extends AbstractBaseResource {

  /**
   * Gets a Device by PartnerId or a complete list of all the user's Devices if no PartnerId
   * is provided.
   * 
   * @return a JSON array of {@link Device}s. If a Device ID is included in the request, the
   *         single Device will still be returned within an array.
   */
  @Override
  public Representation getHandler() {
    String result = null;

    try {
      Long id = getParameterAsLong("id");
      String owner = getParameter("owner");

      List<Device> deviceList = Lists.newArrayList();
      if (id != null) {
        LOGGER.info("Getting Device by id");
        Device device = RestServer.getPersister().getByPrimaryId(Device.class, id);
        if (device != null) {
          deviceList.add(device);
        } else {
          throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND,
              "No Device with that id was found");
        }
      } else if (owner != null) {
        LOGGER.info("Getting Devices by owner");
        deviceList = RestServer.getPersister().get(Device.class, Device.OWNER, owner);
        if (deviceList.size() == 0) {
          throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND,
              "No Device with that owner was found");
        }
      } else {
        LOGGER.info("Getting all Devices");
        deviceList = RestServer.getPersister().get(Device.class);
      }
      result = gson.toJson(deviceList);
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
      Long id = getParameterAsLong("id");
      if (id != null) {
        LOGGER.info("Deleting Device...");
        Device device =
            RestServer.getPersister().getByPrimaryId(Device.class, id);
        if (device == null) {
          throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND,
              "No Device with that id was found");  
        }
        RestServer.getPersister().remove(device);
        result = "OK";

      } else {
        throw new IllegalArgumentException("Missing id for deleting Device");
      }

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
            "We only support Post/Put in the base url: /device (no additional segments /{id} or /owner/{owner)");
      }

      JsonParser jsonParser = new JsonParser();
      JsonElement jsonElement = jsonParser.parse(json);

      if (jsonElement.isJsonArray()) {
        Type listType = new TypeToken<List<Device>>() {}.getType();
        List<Device> deviceList =
            GsonUtil.getGsonBuilder().create().fromJson(jsonElement, listType);

        for (Device device : deviceList) {
          device.setCreated();
        }
        LOGGER.info("Persisting a List of Devices...");
        RestServer.getPersister().save(deviceList);
        result = "OK";

      } else {
        Device device =
            GsonUtil.getGsonBuilder().create().fromJson(jsonElement, Device.class);
        device.setCreated();
        LOGGER.info("Persisting Device...");
        device = RestServer.getPersister().save(device);
        result = gson.toJson(device);
      }

    } catch (Exception exception) {
      return handleException(exception);
    }
    addHeaders();
    return createJsonResult(result);
  }
}
