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
 * @author <a href="mailto:bbarker@part.net">bbarker</a>
 */
public enum ValidOrNotValid
    implements StoryParameter
{
  VALID("valid"), NOT_VALID("not valid");

  private final String testText;

  private ValidOrNotValid(String testText)
  {
    this.testText = testText;
  }

  /**
   * Create a boolean value for the given enum
   * 
   * @return true if {@link ValidOrNotValid#VALID}, false if
   *         {@link ValidOrNotValid#NOT_VALID}
   */
  public boolean toBoolean()
  {
    switch (this) {
      case VALID:
        return true;
      case NOT_VALID:
        return false;
      default:
        throw new IllegalArgumentException(String.format("Could not determine the ValidOrNotValid boolean value for %s!", this.name()));
    }
  }

  @Override
  public String getVisibleText()
  {
    return testText;
  }

}
