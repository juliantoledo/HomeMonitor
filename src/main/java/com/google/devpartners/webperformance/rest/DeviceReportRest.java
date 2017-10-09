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

package com.google.devpartners.webperformance.rest;

import java.net.URL;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;

import com.google.common.collect.Lists;
import com.google.devpartners.homemonitor.RestServer;
import com.google.devpartners.webperformance.model.DeviceReport;
import com.google.devpartners.webperformance.util.DateUtil;

/**
 * Rest entry point to get, create or update SpeedReports.
 * 
 * @author jtoledo@google.com
 */
public class DeviceReportRest extends AbstractBaseResource {

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

      String webPageUrlId = getParameter("webPageUrlId");
      URL url = getParameterAsUrl("url");
      Date dateStart = getParameterAsDate("dateStart");
      Date dateEnd = getParameterAsDate("dateEnd");
      Boolean isForGraph = getParameterAsBoolean("graph");
      Integer limit = getParameterAsInteger("limit");
      Integer numToSkip = getParameterAsInteger("numToSkip");

      List<DeviceReport> speedReportList = Lists.newArrayList();
      if (webPageUrlId != null) {
        LOGGER.info("Getting PageSpeedReport by webPageUrlId");
        speedReportList = RestServer.getPersister().get(
          DeviceReport.class, DeviceReport.WEBPAGEURLID, webPageUrlId, DeviceReport.DATE, dateStart, dateEnd, numToSkip, limit);
        if (speedReportList.size() == 0) {
          throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, "No PageSpeedReport with that webPageUrlId were found");
        }

      } else if (url != null) {
        LOGGER.info("Getting PageSpeedReport by url");
        speedReportList = RestServer.getPersister().get(
          DeviceReport.class, DeviceReport.URL, url, DeviceReport.DATE, dateStart, dateEnd, numToSkip, limit);
        if (speedReportList.size() == 0) {
          throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, "No PageSpeedReport with that url were found");
        }

      } else {
        LOGGER.info("Getting all PageSpeedReport");
        speedReportList = RestServer.getPersister().get(DeviceReport.class);
      }

      if (isForGraph) {
        // Transform json to the google-chart format
        JSONObject data = new JSONObject();
        JSONArray columns = new JSONArray();
        columns.put(new JSONObject("{label: 'Date', type: 'date'}"));
        columns.put(new JSONObject("{label: 'Desktop Speed', type: 'number'}"));
        columns.put(new JSONObject("{label: 'Mobile Speed', type: 'number'}"));
        columns.put(new JSONObject("{label: 'Mobile UX', type: 'number'}"));
        data.put("cols", columns);

        JSONArray rows = new JSONArray();
        for (DeviceReport speedReport : speedReportList ) {
          JSONArray cArray = new JSONArray();

          JSONObject date = new JSONObject();
          date.put("v", DateUtil.getGoogleChartsDate(speedReport.getDate()));
          cArray.put(date);

          JSONObject desktopSpeed = new JSONObject();
          desktopSpeed.put("v", speedReport.getPageSpeedDesktopSpeed());
          cArray.put(desktopSpeed);

          JSONObject mobileSpeed = new JSONObject();
          mobileSpeed.put("v", speedReport.getPageSpeedMobileSpeed());
          cArray.put(mobileSpeed);

          JSONObject mobileUX = new JSONObject();
          mobileUX.put("v", speedReport.getPageSpeedMobileUX());
          cArray.put(mobileUX);

          JSONObject row = new JSONObject();
          row.put("c", cArray);

          rows.put(row);
        }
        data.put("rows", rows);

        result = data.toString();
      } else {
        result = gson.toJson(speedReportList);  
      }

    } catch (Exception exception) {
      return handleException(exception);
    }

    addReadOnlyHeaders();
    return createJsonResult(result);
  }
}
