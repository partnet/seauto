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

package com.partnet.automation.jbehave.parameters;

/**
 * @author fpedroza
 * @since Sep 23, 2014
 */
public class StoryParameterFactory
{

  private StoryParameterFactory()
  {
    // prevent instance creation
  }

  /**
   * Method that knows how to convert some visible text to a corresponding enum
   * value.
   *
   * @param clazz enum class
   * @param visibleText text appearing in test
   * @param <T> instance of {@link StoryParameter}
   * @return story parameter of the mapped value
   */
  public static <T extends StoryParameter> T valueOf(Class<T> clazz, String visibleText)
  {
    T[] vals = clazz.getEnumConstants();
    if (vals == null) throw new IllegalArgumentException("Enum values undefined: " + clazz.getName());
    for (T val : vals) {
      if (val.getVisibleText().equals(visibleText)) {
        return val;
      }
    }
    throw new IllegalArgumentException(String.format("Unmatched visible text \"%s\" for type %s", visibleText, clazz.getName()));
  }

}
