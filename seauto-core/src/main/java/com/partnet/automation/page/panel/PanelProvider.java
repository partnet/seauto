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

package com.partnet.automation.page.panel;

import javax.inject.Inject;

import org.openqa.selenium.WebDriver;

import com.partnet.automation.selenium.DriverProvider;

/**
 * Responsible for creating and initializing {@link Panel} object instances that
 * can be used for testing.
 * 
 * @author <a href="mailto:rbascom@part.net">rbascom</a>
 * @author fpedroza
 */
public class PanelProvider
{

  @Inject
  private DriverProvider driverProvider;

  /**
   * Obtain a panel.
   * 
   * @param clazz
   *          type of Panel requested
   * @return Panel of the specified type
   */
  public <T extends Panel> T get(final Class<T> clazz)
  {
    return createPanel(clazz);
  }

  private <T extends Panel> T createPanel(Class<T> panelClass)
  {
    try {
      T panel = panelClass.getConstructor(WebDriver.class).newInstance(this.driverProvider.get());
      org.openqa.selenium.support.PageFactory.initElements(this.driverProvider.get(), panel);
      return panel;
    }
    catch (Exception e) {
      throw new RuntimeException("Unable to create instance of class " + panelClass, e);
    }
  }

}
