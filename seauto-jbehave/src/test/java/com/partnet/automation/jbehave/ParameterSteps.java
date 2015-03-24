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

import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.weld.WeldStep;
import org.junit.Assert;

import com.partnet.automation.jbehave.parameters.ValidOrNotValid;
import com.partnet.automation.jbehave.parameters.WillOrWillNot;

/**
 * @author <a href="mailto:bbarker@part.net">bbarker</a>
 */
@WeldStep
public class ParameterSteps
{

  @Then("I $willOrWillNot fail")
  public void thenIWillNotFail(WillOrWillNot willOrWillNot)
  {
    Assert.assertEquals(willOrWillNot, WillOrWillNot.willNot);
  }

  @Then("I have a $validOrNotValid parameter")
  public void thenValidParameter(ValidOrNotValid vonv)
  {
    Assert.assertEquals(vonv, ValidOrNotValid.VALID);
  }
}
