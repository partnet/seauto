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

import org.openqa.selenium.WebDriver;

import com.partnet.automation.HtmlView;
import com.partnet.automation.page.Page;

/**
 * Base panel object. Only purpose is to provide a common constructor for
 * subclasses.
 * 
 * A panel represents part of a web page. Panels provide the following benefits:
 * <ul>
 *   <li>Reusable page interactions. Following the DRY principle.</li>
 *   <li>Reusable page element locators.</li>
 *   <li>Panels are different than {@link Page} objects. Unlike Pages, Panels may
 * provide WebElement access.</li>
 * </ul>
 * 
 * @author <a href="mailto:rbascom@part.net">rbascom</a>
 */
public abstract class Panel
    extends HtmlView
{

  public Panel(WebDriver webDriver)
  {
    super(webDriver);
  }

}
