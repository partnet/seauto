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

import java.net.URL;

import org.json.JSONObject;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.partnet.TestHtmlView;
import com.partnet.automation.DependencyContainer;
import com.partnet.automation.page.Page;

/**
 * @author <a href="mailto:bbarker@part.net">bbarker</a>
 */
public class HtmlTestPage
    extends Page
{

  @FindBy(id = "testAlert")
  private WebElement alertBtn;

  @FindBy(id = "testConfirm")
  private WebElement confirmBtn;

  @FindBy(id = "infoTextBox")
  private WebElement infoTextBox;

  @FindBy(id = "manufact")
  private WebElement manufactDropdown;

  @FindBy(id = "jsonAjaxButton")
  private WebElement jsonAjaxBtn;

  public HtmlTestPage(DependencyContainer depContainer)
  {
    super(depContainer);
  }

  public String clickAlertBtnAndAcceptAlert()
  {
    return super.clickAndAcceptAlert(alertBtn);
  }

  public String clickConfirmBtnAndAcceptAlert()
  {
    return super.clickAndAcceptAlert(confirmBtn);
  }

  @Override
  public void verify()
      throws IllegalStateException
  {

  }

  @Override
  public void ready()
  {
    // typically this isn't the way to get to a site
    // however, for this basic test this is the way to accomplish this.

    URL htmlPath = TestHtmlView.class.getClassLoader().getResource("TestHtml.html");
    // System.out.println(htmlPath.toString());
    // webDriver.get("http://localhost/TestHtml.html");
    webDriver.get(htmlPath.toString());
  }

  public HtmlTestPage setInfoBox(String value)
  {
    setValue(infoTextBox, value);
    return this;
  }

  public String getInfoBoxMsg()
  {
    return infoTextBox.getAttribute("value");
  }

  public HtmlTestPage selectManufactOptionByVisibleText(String visibleText)
  {
    selectByVisibleText(manufactDropdown, visibleText);
    return this;
  }

  public HtmlTestPage selectManufactOptionByValue(String value)
  {
    selectByValue(manufactDropdown, value);
    return this;
  }

  public String getManufactSelectedOption()
  {
    return getSelectedVisibleText(manufactDropdown);
  }

  public JSONObject clickAndWaitForAjaxResponse()
  {
    super.injectAjaxListener();
    jsonAjaxBtn.click();
    return super.waitForAjaxResponse("glossary");
  }
}
