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

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.partnet.automation.page.PageProvider;
import com.partnet.junit.SeAuto;
import com.partnet.junit.annotations.browser.HTMLUnit;
import com.partnet.junit.annotations.browser.PhantomJs;
import com.partnet.page.HtmlTestPage;

/**
 * @author <a href="mailto:bbarker@part.net">bbarker</a>
 */
@RunWith(SeAuto.class)
public class TestHtmlView
{

  @Inject
  PageProvider pageProvider;

  @Test
  public void test_SetValue()
  {
    { // Verify the box is filled by default
      String expectedFill = "Default Text";
      String actualFill = pageProvider.get(HtmlTestPage.class).getInfoBoxMsg();

      Assert.assertEquals("The default value is not what is expected!", expectedFill, actualFill);
    }

    { // Verify the field changes
      String expectedFill = "fksadfdsf890dsaf8dsa90f8sda9fds89fs7d8a9fas";
      String actualFill = pageProvider.get(HtmlTestPage.class).setInfoBox(expectedFill).getInfoBoxMsg();

      Assert.assertEquals("Setting value of a text box did not work correctly!", expectedFill, actualFill);
    }
  }

  @Test
  public void test_selectByVisibleTextDefaultOption()
  {
    String expectedOption = "Geo";
    String actualOption = pageProvider.get(HtmlTestPage.class).getManufactSelectedOption();
    Assert.assertEquals("Default selected option was not correct!", expectedOption, actualOption);
  }

  @Test
  public void test_selectByVisibleText()
  {
    String expectedOption = "Ford";
    String actualOption = pageProvider.get(HtmlTestPage.class).selectManufactOptionByVisibleText(expectedOption).getManufactSelectedOption();

    Assert.assertEquals("Option was not selected correctly!", expectedOption, actualOption);
  }

  @Test
  public void test_selectByValue()
  {
    String expectedOption = "Chevy";
    String expectedOptionValue = "chevy";

    String actualOption = pageProvider.get(HtmlTestPage.class).selectManufactOptionByValue(expectedOptionValue).getManufactSelectedOption();

    Assert.assertEquals("Option was not selected correctly!", expectedOption, actualOption);
  }

  @Test
  public void test_ajaxListener()
  {

    JSONObject jsonRepsonse = pageProvider.get(HtmlTestPage.class).clickAndWaitForAjaxResponse();

    Assert.assertEquals("JSON response title was not what was expected!", "example glossary", jsonRepsonse.getJSONObject("glossary").get("title"));

  }

  // These tests can't be run by HTMLUnit by default because
  // the file needs to be hosted in a web container or the protocol needs to be
  // http to allow cookies to work. They can run with PhantomJs, but
  // we want to keep the tests as slim as possible.
  @Test
  @PhantomJs
  public void test_HeadlessAlert()
  {
    String expectedMessage = "Test Alert Message";

    String actualMessage = pageProvider.get(HtmlTestPage.class).clickAlertBtnAndAcceptAlert();

    Assert.assertEquals("The expected message from the alert was not what was expected!", expectedMessage, actualMessage);
    
    //ensure the driver is still working
    ensureDriverStillResponding();
  }

  @Test
  @PhantomJs
  public void test_HeadlessConfirm()
  {
    String expectedMessage = "Test Confirm Message";

    String actualMessage = pageProvider.get(HtmlTestPage.class).clickConfirmBtnAndAcceptAlert();

    actualMessage = pageProvider.get(HtmlTestPage.class).clickConfirmBtnAndAcceptAlert();

    Assert.assertEquals("The expected message from the alert was not what was expected!", expectedMessage, actualMessage);
    
    //ensure the driver is still working
    ensureDriverStillResponding();
    
  }
  
  @Test
  @PhantomJs
  public void test_pageReload()
  {
    pageProvider.get(HtmlTestPage.class).clickReloadBtnAndWait();
    ensureDriverStillResponding();
  }
  
  private void ensureDriverStillResponding()
  {
    String expectedInfoMsg = "Default Text";
    String actualInfoMsg = pageProvider.get(HtmlTestPage.class).getInfoBoxMsg();

    Assert.assertEquals("Looks like the driver may not be responding correctly!", expectedInfoMsg, actualInfoMsg);
  }
}
