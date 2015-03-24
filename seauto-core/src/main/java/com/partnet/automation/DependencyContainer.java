/*
 * Copyright 2015 Partnet, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.partnet.automation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages the available dependencies within an application that cannot be
 * injected.
 * 
 * @author fpedroza
 * @since Oct 13, 2014
 */
public class DependencyContainer
{

  private final Map<Class<?>, Object> depenencies = new ConcurrentHashMap<>();

  private boolean finalized = false;

  /**
   * Retrieve a dependency from the container.
   * 
   * @param clazz
   *          type of dependency to get
   * @throws IllegalArgumentException
   *           if dependency of type does not exist
   */
  public <T extends Object> T get(Class<T> clazz)
  {
    @SuppressWarnings("unchecked")
    T dep = (T) depenencies.get(clazz);
    if (dep == null) {
      throw new IllegalArgumentException(String.format("No dependency of type %s has been defined", clazz.getName()));
    }
    return dep;
  }

  /**
   * Add a new dependency to the container.
   * 
   * @param clazz
   *          type of dependency to add
   * @param instance
   *          the reference to the dependency to be added
   * @throws IllegalStateException
   *           if container is finalized
   * @throws IllegalArgumentException
   *           if dependency of type already exists
   */
  public synchronized <T extends Object> void add(Class<T> clazz, T instance)
  {
    if (finalized) {
      throw new IllegalStateException("Container finalized: can no longer add objects");
    }
    if (depenencies.containsKey(clazz)) {
      throw new IllegalArgumentException(String.format("Dependency of type %s has already been defined", clazz.getName()));
    }
    depenencies.put(clazz, instance);
  }

  /**
   * Once finalized, nothing else can be added.
   */
  public void markFinalized()
  {
    finalized = true;
  }

}
