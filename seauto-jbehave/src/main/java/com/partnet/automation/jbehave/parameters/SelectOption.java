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
 * Used to represent something with visible text and a value. Often used as a
 * template to define a dropdown enum.
 * 
 * @see StoryParameter
 * @author bbarker
 * @author fpedroza
 */
public interface SelectOption
    extends StoryParameter
{

  /**
   * Gets the value of the select option
   */
  String getValue();

}
