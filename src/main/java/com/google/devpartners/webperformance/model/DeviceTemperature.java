package com.google.devpartners.webperformance.model;

import java.util.List;

public class DeviceTemperature extends Device implements Temperature {
	
  private float temperature;

  public DeviceTemperature(String owner, String description, String location, List<type> types) {
    super(owner, description, location, types);
  }

  public float getTemperature() {
    	return temperature;
  }

  public void setTemperature(float temperature) {
    this.temperature = temperature;
  }
}
