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

package com.partnet.automation.jbehave;

import java.lang.reflect.Type;

import org.jbehave.core.steps.ParameterConverters.EnumConverter;

import com.partnet.automation.jbehave.parameters.StoryParameter;
import com.partnet.automation.jbehave.parameters.StoryParameterFactory;

/**
 * EnumConverter that uses the enum's visible text to determine the enum value.
 * 
 * @author fpedroza
 * @since May 23, 2014
 */
public class StoryParameterEnumConverter
    extends EnumConverter
{

  @Override
  public boolean accept(Type type)
  {
    if (!(type instanceof Class<?>)) return false;
    Class<?> clz = (Class<?>) type;
    return clz.isEnum() && StoryParameter.class.isAssignableFrom(clz);
  }

  @Override
  public Object convertValue(String value, Type type)
  {
    Class<? extends StoryParameter> sub = ((Class<?>) type).asSubclass(StoryParameter.class);
    return StoryParameterFactory.valueOf(sub, value);
  }

}
