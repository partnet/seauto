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

package com.partnet.automation;

import io.appium.java_client.android.AndroidDriver;
import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

/**
 * Represents the available web browser options.
 * 
 * @author fpedroza
 *
 */
public enum Browser
{
  CHROME, FIREFOX, IE, PHANTOMJS, HTMLUNIT, ANDROID;

  public boolean isInternetExplorer()
  {
    return this == IE;
  }

  public boolean isChrome()
  {
    return this == CHROME;
  }

  public boolean isFirefox()
  {
    return this == FIREFOX;
  }

  public boolean isHeadless()
  {
    return (this == PHANTOMJS || this == HTMLUNIT);
  }

  public boolean isAndroid()
  {
    return this == ANDROID;
  }

  /**
   * Returns the Browser enum constant found from the browserName. If
   * browserName is null or blank, null is returned.
   * 
   * @param browserName string representation of the enum constant
   * @return null if browserName is blank or null, otherwise returns the enum
   *         constant
   * @throws IllegalArgumentException
   *           if a Browser enum constant could not be found
   */
  public static Browser valueOfByName(String browserName)
  {
    if (StringUtils.isBlank(browserName)) return null;

    return Browser.valueOf(browserName);
  }

  /**
   * Obtains the browser associated with the given driver.
   * @param driver {@link WebDriver} instance to determine the browser type
   * @return {@link Browser} enum value of the current web driver
   */
  public static Browser getBrowser(WebDriver driver)
  {
    if (driver instanceof ChromeDriver) {
      return Browser.CHROME;
    }

    if (driver instanceof FirefoxDriver) {
      return Browser.FIREFOX;
    }

    if (driver instanceof InternetExplorerDriver) {
      return Browser.IE;
    }

    if (driver instanceof PhantomJSDriver) {
      return Browser.PHANTOMJS;
    }

    if (driver instanceof HtmlUnitDriver) {
      return Browser.HTMLUNIT;
    }

    if (!(driver instanceof RemoteWebDriver)) {
      throw new IllegalArgumentException(String.format("Could not determine the browser type for '%s'", driver.getClass()));
    }

    RemoteWebDriver remoteDriver = (RemoteWebDriver) driver;
    String browserName = remoteDriver.getCapabilities().getBrowserName().toLowerCase();
    switch (browserName) {
      case "chrome":
        return Browser.CHROME;
      case "internet explorer":
        return Browser.IE;
      case "firefox":
        return Browser.FIREFOX;
      case "phantomjs":
        return Browser.PHANTOMJS;
      case "htmlunit":
        return Browser.HTMLUNIT;
      case "android":
        return Browser.ANDROID;

      default:
        throw new IllegalArgumentException(String.format("Could not determine remote web browser name: '%s'", browserName));
    }
  }

}
