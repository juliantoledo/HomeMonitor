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

package com.google.devpartners.webperformance.model;

import java.util.Date;
import java.util.List;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

/**
 * Model class used to store URL info.
 * 
 * @author jtoledo@google.com (Julian Toledo)
 */
@Entity
@Cache
abstract public class Device {

  public static String DEVICE = "Device";

  public static String ID = "id";
  public static String OWNER = "owner";

  public enum type {
    Temperature, Humidity, Altitude, Pressure, Camera, Motion, Proximity, 
  }

  @Id
  private Long id;

  @Index
  private String owner;

  private String description;

  private String location;

  private List<type> types;

  @Index
  private Date created;

  @Index
  private Date updated;

  public Device(String owner, String description, String location, List<type> types) {
    this.owner = owner;
    this.description = description;
    this.location = location;
    this.types = types;
    setCreated();
  }

  public Long getId() {
    return id;
  }

  public String getOwner() {
    return owner;
  }

  public void setOwner(String owner) {
    this.owner = owner;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Date getCreated() {
    return created;
  }

  public void setCreated() {
    if (this.created == null) {
    Date now = new Date();
      this.created = now;
      this.updated = now;
    }
  }

  public Date getUpdated() {
    return updated;
  }

  public void setUpdated() {
    this.updated = new Date();
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public List<type> getTypes() {
    return types;
  }

  public void setTypes(List<type> types) {
    this.types = types;
  }
}
