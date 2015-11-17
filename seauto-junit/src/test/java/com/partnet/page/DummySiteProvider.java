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

import javax.inject.Inject;
import javax.inject.Singleton;

import com.partnet.automation.RuntimeConfiguration;
import com.partnet.automation.page.SiteProvider;

/**
 * @author <a href="mailto:bbarker@part.net">bbarker</a>
 */
@Singleton
public class DummySiteProvider 
  extends SiteProvider<DummySite>
{
  
  @Inject
  private RuntimeConfiguration runConfig;
  
  @Override
  protected DummySite newSiteInstance()
  {
    return new DummySite(driverProvider, runConfig);
  }

}
