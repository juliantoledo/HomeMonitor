//Copyright 2015 Google Inc. All Rights Reserved.
//
//Licensed under the Apache License, Version 2.0 (the "License");
//you may not use this file except in compliance with the License.
//You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
//Unless required by applicable law or agreed to in writing, software
//distributed under the License is distributed on an "AS IS" BASIS,
//WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//See the License for the specific language governing permissions and
//limitations under the License.

package com.google.devpartners.homemonitor.rest;

import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;

import com.google.common.collect.Lists;
import com.google.devpartners.homemonitor.RestServer;
import com.google.devpartners.homemonitor.model.DeviceReport;
import com.google.devpartners.homemonitor.model.DeviceTemperatureHumidityReport;
import com.google.devpartners.homemonitor.util.DateUtil;
import com.google.devpartners.homemonitor.util.GsonUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

/**
 * Rest entry point to get, create or update SpeedReports.
 * 
 * @author jtoledo@google.com
 */
public class DeviceTemperatureHumidityReportRest extends AbstractBaseResource {

  /**
   * Gets a PageSpeedReport by Id, URL or a complete list
   * of all PageSpeedReports
   * 
   * @return a JSON array of {@link DeviceReport}s. If a PageSpeedReport ID is included in the request,
   * the single PageSpeedReport will still be returned within an array.
   */
  @Override
  public Representation getHandler() {
    String result = null;

    try {
      Long deviceId = getParameterAsLong("deviceId");
      Date dateStart = getParameterAsDate("dateStart");
      Date dateEnd = getParameterAsDate("dateEnd");
      Boolean isForGraph = getParameterAsBoolean("graph");
      Integer limit = getParameterAsInteger("limit");
      Integer numToSkip = getParameterAsInteger("numToSkip");

      List<DeviceTemperatureHumidityReport> deviceReportList = Lists.newArrayList();
      if (deviceId != null) {
        LOGGER.info("Getting DeviceTemperatureHumidityReports by deviceId");
        deviceReportList = RestServer.getPersister().get(
            DeviceTemperatureHumidityReport.class, DeviceTemperatureHumidityReport.DEVICE_ID, deviceId,
            DeviceTemperatureHumidityReport.DATE, dateStart, dateEnd, numToSkip, limit);
        if (deviceReportList.size() == 0) {
          throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, "No DeviceTemperatureHumidityReports with that deviceId were found");
        }

      } else {
        LOGGER.info("Getting all PageSpeedReport");
        deviceReportList = RestServer.getPersister().get(DeviceTemperatureHumidityReport.class);
      }

      if (isForGraph) {
        // Transform JSON to the google-chart format
        JSONObject data = new JSONObject();
        JSONArray columns = new JSONArray();
        columns.put(new JSONObject("{label: 'Date', type: 'date'}"));
        columns.put(new JSONObject("{label: 'Temperature', type: 'number'}"));
        columns.put(new JSONObject("{label: 'Humidity', type: 'number'}"));
        data.put("cols", columns);

        JSONArray rows = new JSONArray();
        for (DeviceTemperatureHumidityReport deviceReport : deviceReportList ) {
          JSONArray cArray = new JSONArray();

          JSONObject date = new JSONObject();
          date.put("v", DateUtil.getGoogleChartsDateTime(deviceReport.getDate()));
          cArray.put(date);

          JSONObject desktopSpeed = new JSONObject();
          desktopSpeed.put("v", deviceReport.getTemperatureString());
          cArray.put(desktopSpeed);

          JSONObject mobileSpeed = new JSONObject();
          mobileSpeed.put("v", deviceReport.getHumidityString());
          cArray.put(mobileSpeed);

          JSONObject row = new JSONObject();
          row.put("c", cArray);

          rows.put(row);
        }
        data.put("rows", rows);

        result = data.toString();
      } else {
        result = gson.toJson(deviceReportList);  
      }

    } catch (Exception exception) {
      return handleException(exception);
    }

    addReadOnlyHeaders();
    return createJsonResult(result);
  }

  @Override
  public Representation postPutHandler(String json) {
    String result = null;

    try {
      if (this.getReference().getSegments().size() != 1) {
        throw new IllegalArgumentException(
            "We only support Post/Put in the base url: /devicereport (no additional segments /{id})");
      }

      JsonParser jsonParser = new JsonParser();
      JsonElement jsonElement = jsonParser.parse(json);

      DeviceTemperatureHumidityReport deviceReport =
          GsonUtil.getGsonBuilder().create().fromJson(jsonElement, DeviceTemperatureHumidityReport.class);
      LOGGER.info("Persisting DeviceTemperatureHumidityReport...");
      deviceReport = RestServer.getPersister().save(deviceReport);
      result = gson.toJson(deviceReport);

    } catch (Exception exception) {
      return handleException(exception);
    }
    addHeaders();
    return createJsonResult(result);
  }
}
