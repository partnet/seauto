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

import org.junit.After;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import com.partnet.config.SampleTestStoryContext;
import com.partnet.junit.SeAuto;
import com.partnet.junit.annotations.browser.HTMLUnit;

/**
 * @author <a href="mailto:bbarker@part.net">bbarker</a>
 */
@RunWith(SeAuto.class)
public class TestSite
{

  @Inject
  SampleTestStoryContext context;
  
  @Rule
  public ExpectedException expectedEx = ExpectedException.none();
  
  private static final String URL_SYS_PROP_NAME = "test.config.url";
  private static final String TEST_URL = "http://url-to-no-where-234242/";
  
  @After
  public void tearDown()
  {
    System.clearProperty(URL_SYS_PROP_NAME);
  }
  
  @Test
  @HTMLUnit
  public void test_ensureSiteOpenPullsFromTestConfigUrlProperty()
  {
    System.setProperty(URL_SYS_PROP_NAME, TEST_URL);
    context.site().open();
    Assert.assertEquals("test.config.url property isn't working correctly!", 
        TEST_URL, context.site().getCurrentPageUrl());
  }
  
  @Test
  @HTMLUnit
  public void test_ensureTestConfigUrlIsRequired()
  {
    expectedEx.expect(IllegalStateException.class);
    expectedEx.expectMessage("Missing required property test.config.url");
    context.site().open();
  }

}
