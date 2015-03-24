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

package com.partnet.automation.selenium;

import org.openqa.selenium.WebDriver;

import com.partnet.automation.Browser;

/**
 * @author <a href="mailto:bbarker@part.net">bbarker</a>
 */
public interface DriverProvider
{

  /**
   * Starts the default web driver, for calling thread
   */
  void launch();

  /**
   * Starts the specified web driver, for calling thread
   */
  void launch(Browser browser);

  /**
   * Gets the web driver for calling thread
   * 
   * @return
   */
  WebDriver get();

  /**
   * Ends the web driver for calling thread
   */
  void end();

  /**
   * Takes screenshot of the driver for the calling thread
   * 
   * @param path
   * @return
   */
  boolean saveScreenshotAs(String path);

}
