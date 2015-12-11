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

package com.partnet;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.partnet.automation.page.PageProvider;
import com.partnet.junit.SeAuto;
import com.partnet.page.DummyPage;

/**
 * @author <a href="mailto:bbarker@part.net">bbarker</a>
 */
@RunWith(SeAuto.class)
public class TestPageState
{

  @Inject
  private PageProvider pageProvider;

  @Test
  public void test_readyCalledInPageObjects()
  {
    DummyPage dummyPage = pageProvider.get(DummyPage.class, false);

    Assert.assertTrue(dummyPage.wasReadyCalled());

  }

  @Test
  public void test_verifyCalledInPageObjects()
  {
    DummyPage dummyPage = pageProvider.get(DummyPage.class, false);

    Assert.assertTrue(dummyPage.wasVerifyCalled());
  }

}
