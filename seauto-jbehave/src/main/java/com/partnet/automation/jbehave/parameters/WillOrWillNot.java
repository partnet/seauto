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
 * @see StoryParameter
 * @author fpedroza
 * @since Jun 13, 2014
 */
public enum WillOrWillNot
    implements StoryParameter
{
  will("will"), willNot("will not"), ;

  private final String text;

  private WillOrWillNot(String text)
  {
    this.text = text;
  }

  /**
   * Converts the given value to a boolean (if possible)
   * 
   * @see #toBoolean()
   * @param value string representation of will or will not
   * @return true if "will", false if "will not"
   */
  public static boolean toBoolean(String value)
  {
    return StoryParameterFactory.valueOf(WillOrWillNot.class, value).toBoolean();
  }

  /**
   * Returns boolean value
   * 
   * @return true for {@link WillOrWillNot#will}, false for
   *         {@link WillOrWillNot#willNot}
   */
  public boolean toBoolean()
  {
    switch (this) {
      case will:
        return true;
      case willNot:
        return false;
      default:
        throw new IllegalArgumentException(String.format("Could not determine the WillOrWillNot boolean value for %s", this.name()));
    }
  }

  @Override
  public String getVisibleText()
  {
    return text;
  }

}
