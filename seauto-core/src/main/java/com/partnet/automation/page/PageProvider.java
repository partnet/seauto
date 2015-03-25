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

package com.partnet.automation.page;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.openqa.selenium.WebDriver;

import com.partnet.automation.DependencyContainer;
import com.partnet.automation.page.panel.PanelProvider;
import com.partnet.automation.selenium.DriverProvider;

/**
 * Responsible for creating and initializing {@link Page} object instances that
 * can be used for testing.
 * 
 * @author <a href="mailto:rbascom@part.net">rbascom</a>
 * @author fpedroza
 */
@Singleton
public class PageProvider
{

  @Inject
  private DriverProvider driverProvider;

  @Inject
  private PanelProvider panelProvider;

  /**
   * Obtains a page object. Checks/waits for ready state and verifies the page
   * object.
   * 
   * @param clazz
   *          type of Page requested
   * @return Page of the specified type
   * @see Page#initialize()
   */
  public <T extends Page> T get(final Class<T> clazz)
  {
    T page = createPage(clazz);
    page.initialize();
    return page;
  }

  /**
   * Initializes the superset of dependencies needed by the various pages used
   * for testing. Subclasses may choose to override this method if additional
   * dependencies are needed above the necessary minimum {@link WebDriver} and
   * {@link PanelProvider}.
   * 
   * @param depContainer
   *          - an empty {@link DependencyContainer} to populate with the
   *          necessary dependencies
   */
  protected void initializeDependencies(DependencyContainer depContainer)
  {
    depContainer.add(WebDriver.class, this.driverProvider.get());
    depContainer.add(PanelProvider.class, this.panelProvider);
  }

  /**
   * @see #initializeDependencies()
   */
  private <T extends Page> T createPage(Class<T> pageClass)
  {
    DependencyContainer depContainer = new DependencyContainer();
    initializeDependencies(depContainer);
    depContainer.markFinalized();

    try {
      T page = pageClass.getConstructor(DependencyContainer.class).newInstance(depContainer);
      org.openqa.selenium.support.PageFactory.initElements(this.driverProvider.get(), page);
      return page;
    }
    catch (Exception e) {
      throw new RuntimeException("Unable to create instance of class " + pageClass, e);
    }
  }
}
