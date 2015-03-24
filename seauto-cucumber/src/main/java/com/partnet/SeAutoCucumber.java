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

package com.partnet;

import java.io.IOException;

import org.junit.runners.model.InitializationError;
import org.slf4j.MDC;

import cucumber.api.junit.Cucumber;

/**
 * Simple wrapper to help with logging when running tests in parallel
 * 
 * @author <a href="mailto:bbarker@part.net">bbarker</a>
 */
public class SeAutoCucumber
    extends Cucumber
{

  public static final String MDC_RUNNER = "runner";

  public SeAutoCucumber(Class<?> clazz)
      throws InitializationError, IOException
  {
    super(clazz);
    MDC.put(MDC_RUNNER, clazz.getSimpleName());
  }

}
