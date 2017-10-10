// Copyright 2013 Google Inc. All Rights Reserved.
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

package com.google.devpartners.homemonitor.persistence.objectify;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * The {@code ReportEntitiesPersister} is the base interface to persist the
 * retrieved data from the AWAPI.
 *
 * The implementations should provide a way to persist the entities, retrieve
 * the entities and delete a given {@code Collection} of {@code Report}
 * entities.
 *
 * It is responsibility of the implementation to decide how is the best way to
 * perform all the operations, i.e. the most performing manner or the least
 * resource demanding.
 *
 * @author gustavomoreira@google.com (Gustavo Moreira)
 *
 */
public interface EntityPersister {

  /**
   * Gets the entity for the given class.
   *
   * @param classT the entity T class
   * @return the list of entities for the given class
   */
  <T> List<T> get(Class<T> classT);

  /**
   * Gets the entity for the given class by its primary Id.
   *
   * @param classT the entity T class
   * @param id the primary Id
   * @return T class object
   */
  <T> T getByPrimaryId(Class<T> classT, String value);

  /**
   * Gets the entity for the given class by its primary Id.
   *
   * @param classT the entity T class
   * @param id the primary Id
   * @return T class object
   */
  <T> T getByPrimaryId(Class<T> classT, Long value);

  /**
   * Gets the entity for the given class in a paginated fashion.
   *
   * @param classT the entity T class
   * @param numToSkip the number of entities to be skipped
   * @param limit the limit of the page size
   *
   * @return the list of entities for the given class
   */
  <T> List<T> get(Class<T> classT, Integer numToSkip, Integer limit);

  /**
   * Gets the entity that contains the given value on the given property.
   *
   * @param classT the entity T class
   * @param key the property name
   * @param value the property value
   * @return the list of entities that were found.
   */
  <T, V> List<T> get(Class<T> classT, String key, V value);

  /**
   * Gets the entity that contains the given values on the given property.
   *
   * @param classT the entity T class
   * @param key the property name
   * @param values the property values
   * @return the list of entities that were found.
   */
  <T, V> List<T> get(Class<T> classT, String key, List<V> values);

  /**
   * Gets the entity that contains the given value on the given property in a
   * paginated fashion.
   *
   * @param classT the entity T class
   * @param key the property name
   * @param value the property value
   * @param numToSkip the number of entities found to be skipped
   * @param limit the limit of the page
   * @return the list of entities that were found.
   */
  <T, V> List<T> get(Class<T> classT, String key, V value, Integer numToSkip, Integer limit);

  /**
   * Gets the entity that contains the given value on the given property, and
   * between the dates.
   *
   * @param classT the entity T class
   * @param key the property name
   * @param value the property value
   * @param dateKey the name of the date property
   * @param dateStart the first date
   * @param dateEnd the last date
   * @return the list of entities that were found.
   */
  <T> List<T> get(Class<T> classT, String key, Object value, String dateKey, Date dateStart,
      Date dateEnd);

  <T> List<T> get(Class<T> classT, String key, Object value, String keyToCompare,
      String valueGreaterEqual, String valueLessEqual);

  /**
   * Gets the entity that contains the given value on the given property, and
   * between the dates in a paginated fashion.
   *
   * @param classT the entity T class
   * @param key the property name
   * @param value the property value
   * @param dateKey the name of the date property
   * @param dateStart the first date
   * @param dateEnd the last date
   * @param numToSkip the number of entities found that will be skipped
   * @param limit the limit of the page size
   * @return the list of entities that were found.
   */
  <T> List<T> get(Class<T> classT, String key, Object value, String dateKey, Date dateStart,
      Date dateEnd, Integer numToSkip, Integer limit);

  /**
   * Gets the entity that contains all the values for the given properties.
   *
   * @param classT the entity T class
   * @param keyValueList the map containing all the property name,value pairs.
   * @return the list of entities that were found
   */
  <T, V> List<T> get(Class<T> classT, Map<String, V> keyValueList);

  /**
   * Gets the entity that contains all the values for the given properties in a
   * paginated fashion.
   *
   * @param classT the entity T class
   * @param keyValueList the map containing all the property name,value pairs.
   * @param numToSkip the number of entities found that will be skipped
   * @param limit the limit of the page size
   * @return the list of entities that were found
   */
  <T, V> List<T> get(Class<T> classT, Map<String, V> keyValueList, Integer numToSkip,
      Integer limit);

  /**
   * Removes the entity.
   *
   * @param entity the entity to be removed
   */
  <T> void remove(T entity);

  /**
   * Removes the collection of entities.
   *
   * @param entities the entities to be removed
   */
  <T> void remove(Collection<T> entities);

  /**
   * Removes the collection of entities by key,value
   *
   * @param classT the entity T class
   * @param key the property name
   * @param value the property value
   */
  <T, V> void remove(Class<T> classT, String key, V value);

  /**
   * Removes the collection of entities by key,values
   *
   * @param classT the entity T class
   * @param key the property name
   * @param values a list of values
   */
  <T, V> void remove(Class<T> classT, String key, List<V> values);

  /**
   * Saves the entity.
   *
   * @param entity to be saved
   * @return the persisted entity
   */
  <T> T save(T entity);

  /**
   * Saves the list of entities.
   *
   * @param entities the list with the entities
   */
  <T> void save(List<T> entities);

  /**
   * Adds a field as a DB index
   *
   * @param classT the entity T class
   * @param key the entity key
   */
  <T> void createIndex(Class<T> classT, String key);

  /**
   * Adds a list of fields as DB indexes
   *
   * @param classT the entity T class
   * @param keys the entity keys
   */
  <T> void createIndex(Class<T> classT, List<String> keys);

}
