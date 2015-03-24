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

import com.partnet.automation.selenium.DriverProvider;

/**
 * Responsible for creating and initializing {@link Site} object instances that
 * can be used for testing.
 *
 * @author fpedroza
 *
 * @param <T>
 *          application-specific type of {@link Site} that is created by this
 *          provider
 */
public abstract class SiteProvider<T extends Site>
{

  @Inject
  protected DriverProvider driverProvider;

  /**
   * Used by {@link #createSite()} to create new application-specific
   * {@link Site} instance to be used.
   */
  abstract protected T newSiteInstance();

  /**
   * Creates and initializes a {@link Site} instance to be used.
   * 
   * @see {@link #newSiteInstance()}
   */
  public T createSite()
  {
    try {
      T site = newSiteInstance();
      org.openqa.selenium.support.PageFactory.initElements(driverProvider.get(), site);
      return site;
    }
    catch (Exception e) {
      throw new RuntimeException("Unable to create new Site instance", e);
    }
  }

}
