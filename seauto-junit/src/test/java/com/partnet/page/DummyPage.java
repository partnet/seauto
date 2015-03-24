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

package com.partnet.page;

import com.partnet.automation.Browser;
import com.partnet.automation.DependencyContainer;
import com.partnet.automation.page.Page;

/**
 * @author <a href="mailto:bbarker@part.net">bbarker</a>
 */
public class DummyPage
    extends Page
{

  public DummyPage(DependencyContainer depContainer)
  {
    super(depContainer);
  }

  private boolean verifyCalled = false;

  private boolean readyCalled = false;

  public Browser getCurrentBrowser()
  {
    return getBrowser();
  }

  @Override
  public void verify()
      throws IllegalStateException
  {
    verifyCalled = true;
  }

  @Override
  public void ready()
  {
    readyCalled = true;
  }

  public boolean wasVerifyCalled()
  {
    return verifyCalled;
  }

  public boolean wasReadyCalled()
  {
    return readyCalled;
  }

}
