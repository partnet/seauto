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
 * @see {@link StoryParameter}
 * @author fpedroza
 * @since Jun 13, 2014
 */
public enum WithOrWithout
    implements StoryParameter
{
  WITH("with"), WITHOUT("without"), ;

  private final String text;

  private WithOrWithout(String text)
  {
    this.text = text;
  }

  @Override
  public String getVisibleText()
  {
    return text;
  }

}
