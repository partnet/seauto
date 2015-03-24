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

import java.io.File;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.partnet.automation.Browser;
import com.partnet.automation.selenium.DriverProvider;

import cucumber.api.Scenario;

/**
 * This class should be used to implement Before/After hooks for the
 * Cucumber-JVM framework to launch and maintain browser state.
 * 
 * @author <a href="mailto:bbarker@part.net">bbarker</a>
 */
public abstract class AbstractDriverLifecycleListener
{

  private static final Logger LOG = LoggerFactory.getLogger(AbstractDriverLifecycleListener.class);

  @Inject
  private DriverProvider driverProvider;

  /**
   * Before scenario hook to launch the Firefox driver
   */
  public void beforeFirefox()
  {
    driverProvider.launch(Browser.FIREFOX);
  }

  /**
   * Before scenario hook to launch the Chrome driver
   */
  public void beforeChrome()
  {
    driverProvider.launch(Browser.CHROME);
  }

  /**
   * Before scenario hook to launch the PhantomJs driver
   */
  public void beforePhantomJs()
  {
    driverProvider.launch(Browser.PHANTOMJS);
  }

  /**
   * Before scenario hook to launch the HTMLUnit driver
   */
  public void beforeHmtlUnit()
  {
    driverProvider.launch(Browser.HTMLUNIT);
  }

  public void beforeIe()
  {
    driverProvider.launch(Browser.IE);
  }

  /**
   * Places the Scenario into the logging {@link MDC}
   * 
   * @param scenario
   */
  public void setup(Scenario scenario)
  {
    MDC.put("scenario", scenario.getName());
  }

  public void after(Scenario scenario)
  {
    if (driverProvider.get() == null) {
      LOG.warn("Driver for scenario {} {} is not running!", scenario.getId(), scenario.getName());
      return;
    }

    if (scenario.isFailed()) {
      StringBuilder sbPath = new StringBuilder();
      sbPath.append(System.getProperty("user.dir")).append(File.separator).append("target").append(File.separator).append("seauto").append(File.separator).append("screenshots").append(File.separator).append(String.format("%s.png", scenario.getId().replaceAll(";", "__")));
      LOG.debug("Save screenshot to {}", sbPath.toString());

      driverProvider.saveScreenshotAs(sbPath.toString());
    }

    driverProvider.end();

  }
}
