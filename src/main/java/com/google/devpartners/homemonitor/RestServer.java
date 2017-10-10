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

package com.google.devpartners.homemonitor;

import java.io.IOException;
import java.util.logging.Logger;

import org.restlet.Restlet;
import org.restlet.ext.swagger.SwaggerApplication;
import org.restlet.routing.Router;
import org.restlet.service.CorsService;

import com.google.common.collect.Sets;
import com.google.devpartners.homemonitor.model.Device;
import com.google.devpartners.homemonitor.model.DeviceTemperatureHumidityReport;
import com.google.devpartners.homemonitor.persistence.objectify.EntityPersister;
import com.google.devpartners.homemonitor.persistence.objectify.ObjectifyEntityPersister;
import com.google.devpartners.homemonitor.rest.DeviceRest;
import com.google.devpartners.homemonitor.rest.DeviceTemperatureHumidityReportRest;
import com.googlecode.objectify.ObjectifyService;

/**
 * Main class for the Server, it routes request to the Rest entry points.
 *
 * @author jtoledo@google.com (Julian Toledo)
 */
public class RestServer extends SwaggerApplication {

  protected static final Logger LOGGER = Logger.getLogger(RestServer.class.getName());

  protected static EntityPersister persister;

  public static EntityPersister getPersister() {
    if (persister == null) {
      synchronized (RestServer.class) {
        if (persister == null) {
          initApplicationContextAndProperties();
        }
      }
    }
    return persister;
  }

  public RestServer() throws IOException {
    setName("HomeMonitor");
    CorsService corsService = new CorsService();
    corsService.setAllowedOrigins(Sets.newHashSet("*"));
    corsService.setAllowedCredentials(true);
    getServices().add(corsService);
  }

  /**
   * Creates a root Restlet that will process all incoming calls.
   */
  @Override
  public synchronized Restlet createInboundRoot() {
    initApplicationContextAndProperties();

    Router router = new Router(getContext());

    router.attach("/device", DeviceRest.class);
    router.attach("/device/{id}", DeviceRest.class);
    router.attach("/device/owner/{owner}", DeviceRest.class);

    router.attach("/devicereport", DeviceTemperatureHumidityReportRest.class);
    router.attach("/devicereport/{deviceId}", DeviceTemperatureHumidityReportRest.class);

    return router;
  }

  /**
   * Initialize the application context, adding the properties configuration
   * file depending on the specified path.
   */
  protected synchronized static void initApplicationContextAndProperties() {

    persister = new ObjectifyEntityPersister();

    // Resister all Model Objects in the ObjectifyService
    ObjectifyService.register(Device.class);
    ObjectifyService.register(DeviceTemperatureHumidityReport.class);
  }
}
