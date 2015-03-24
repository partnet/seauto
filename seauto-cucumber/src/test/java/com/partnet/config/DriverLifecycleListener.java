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

package com.partnet.config;

import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;

/**
 * @author <a href="mailto:bbarker@part.net">bbarker</a>
 */
public class DriverLifecycleListener
    extends AbstractDriverLifecycleListener
{

  @Override
  @Before("@firefox")
  public void beforeFirefox()
  {
    super.beforeFirefox();
  }

  @Override
  @Before("@chrome")
  public void beforeChrome()
  {
    super.beforeChrome();
  }

  @Override
  @Before("@phantomjs")
  public void beforePhantomJs()
  {
    super.beforePhantomJs();
  }

  @Override
  @Before("@htmlunit")
  public void beforeHmtlUnit()
  {
    super.beforeHmtlUnit();
  }

  @Override
  @Before
  public void setup(Scenario scenario)
  {
    super.setup(scenario);
  }

  @Override
  @After
  public void after(Scenario scenario)
  {
    super.after(scenario);
  }

}
