// Copyright 2017 Google Inc. All Rights Reserved.
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

package com.google.devpartners.homemonitor.model;

import java.util.Date;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

/**
 * Model class used to store Device Reports
 * 
 * @author jtoledo@google.com (Julian Toledo)
 */
@Entity
@Cache
abstract public class DeviceReport {

  public static final String ID = "id";
  public static final String DEVICE_ID = "deviceId";
  public static final String DATE = "date";

  @Id
  private Long id;

  @Index
  private Long deviceId;

  @Index
  private Date date;

  public DeviceReport() {
    date = new Date();
  }

  public DeviceReport(Long deviceId) {
    this.deviceId = deviceId;
    date = new Date();
  }

  public Long getId() {
    return id;
  }

  public Long deviceId() {
    return deviceId;
  }

  public void setDeviceId(Long deviceId) {
    this.deviceId = deviceId;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }
}
