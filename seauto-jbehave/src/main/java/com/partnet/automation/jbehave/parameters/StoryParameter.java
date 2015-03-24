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
 * Used to represent something with visible text. Used frequently for enums
 * where the enum value has some text that gets rendered on the UI. We often
 * want to be able to recall the enum value based on this visible text.
 * 
 * @author fpedroza
 * @since Jun 13, 2014
 * @see {@link StoryParameterFactory#valueOf(Class, String)}
 */
public interface StoryParameter
{

  /**
   * Gets the text of the select option
   */
  String getVisibleText();

}
