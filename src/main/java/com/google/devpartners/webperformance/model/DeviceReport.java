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

package com.google.devpartners.webperformance.model;

import java.net.URL;
import java.util.Date;

import com.google.devpartners.webperformance.util.DateUtil;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

/**
 * Model class used to store PageSpeed Reports:
 * https://developers.google.com/speed/pagespeed/insights
 * 
 * @author jtoledo@google.com (Julian Toledo)
 */
@Entity
@Cache
public class DeviceReport {

  public static final String ID = "id";
  public static final String WEBPAGEURLID = "webPageUrlId";
  public static final String DATE = "date";
  public static final String URL = "url";

  // Id is webPageUrlId + yyyyMMdd of today to make it unique per day
  @Id
  private String id;

  @Index
  private String webPageUrlId;

  @Index
  private URL url;

  @Index
  private Date date;

  // Data from PageSpeed Insights API
  private Integer pageSpeedMobileSpeed = 0;
  private Integer pageSpeedMobileUX = 0;
  private Integer pageSpeedDesktopSpeed = 0;

  public DeviceReport() {
    date = new Date();
  }

  public DeviceReport(String webPageUrlId, URL url, Integer pageSpeedMobileSpeed,
      Integer pageSpeedMobileUX, Integer pageSpeedDesktopSpeed) {
    this.webPageUrlId = webPageUrlId;
    this.url = url;
    this.pageSpeedMobileSpeed = pageSpeedMobileSpeed;
    this.pageSpeedMobileUX = pageSpeedMobileUX;
    this.pageSpeedDesktopSpeed = pageSpeedDesktopSpeed;
    this.date = new Date();
    // Generate the Id with webPageUrlId + yyyyMMdd of today,
    // this guarantees that we overwrite if ran more than once per day.
    generateId();
  }

  // Used for Gson deserialization
  public void generateId() {
    this.id = webPageUrlId + "-" + DateUtil.formatYearMonthDayNoDash(date);
  }

  public String getId() {
    return id;
  }

  public String getWebPageUrlId() {
    return webPageUrlId;
  }

  public void setWebPageUrlId(String webPageUrlId) {
    this.webPageUrlId = webPageUrlId;
  }

  public URL getUrl() {
    return url;
  }

  public void setUrl(URL url) {
    this.url = url;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public Integer getPageSpeedMobileSpeed() {
    return pageSpeedMobileSpeed;
  }

  public void setPageSpeedMobileSpeed(Integer pageSpeedMobileSpeed) {
    this.pageSpeedMobileSpeed = pageSpeedMobileSpeed;
  }

  public Integer getPageSpeedMobileUX() {
    return pageSpeedMobileUX;
  }

  public void setPageSpeedMobileUX(Integer pageSpeedMobileUX) {
    this.pageSpeedMobileUX = pageSpeedMobileUX;
  }

  public Integer getPageSpeedDesktopSpeed() {
    return pageSpeedDesktopSpeed;
  }

  public void setPageSpeedDesktopSpeed(Integer pgeSpeedDesktopSpeed) {
    this.pageSpeedDesktopSpeed = pgeSpeedDesktopSpeed;
  }
}
