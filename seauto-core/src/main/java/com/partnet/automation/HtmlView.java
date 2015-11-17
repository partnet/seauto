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

package com.partnet.automation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.NotFoundException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;

/**
 * Represents a view of some HTML that can be interacted with via a
 * {@link WebDriver}. Cannot be instantiated directly. Provides convenience
 * (protected) methods that all sub-classes can selectively make use of.
 * 
 * @author fpedroza
 * @since Dec 16, 2014
 */
/*
 * TODO: Dec 17, 2014 (fpedroza) - consider replacing hardcoded timeout values
 * throughout with system property values w/defaults
 */
public abstract class HtmlView
{
  private static final Logger LOG = LoggerFactory.getLogger(HtmlView.class);
  private static final String JAVASCRIPT_AJAX_MESSAGE_ARRAY = "window.document.msgArray";
  protected final WebDriver webDriver;

  // used for javascript alerts for headless browsers
  private static final String ALERT_COOKIE_NAME = "alertMsg";
  private static final String ALERT_NEW_LINE_REPLACE = "#newLine#";

  private static final String WAIT_FOR_PAGE_PROP = "test.config.page.load.timeout";
  
  protected HtmlView(WebDriver webDriver)
  {
    this.webDriver = webDriver;
  }

  /**
   * Get the current browser - convenience method
   */
  protected Browser getBrowser()
  {
    return Browser.getBrowser(webDriver);
  }

  /**
   * <p>
   * This method simply clicks and waits for the page to load.
   *
   * <p>
   * It is required for Internet Explorer to wait when clicking an element that
   * causes the page to reload/refresh; see <a href=
   * "http://selenium.googlecode.com/svn/trunk/docs/api/java/org/openqa/selenium/WebElement.html#click%28%29"
   * >WebElement.click()</a>.
   *
   * <p>
   * The IE web driver attempts to use native events, which would explain having
   * to explicitly wait for the page to load <a
   * href="http://code.google.com/p/selenium/wiki/InternetExplorerDriver"
   * >InternetExplorerDriver wiki</a>.
   *
   * <p>
   * There is a way to turn off native events (and has been turned off for a
   * long time now). However, in my testing it appears it still does not wait
   * for the page to load when a click function has a new page loaded.
   *
   * <p>
   * I am not sure of a way around this fix. Wait for page to load is required
   * for IE to work properly if the element being interacted with causes a new
   * page to be loaded.
   *
   * Reported bugs to selenium that have been closed as "Working as Intended: <a
   * href="http://code.google.com/p/selenium/issues/detail?id=2676">2676</a>, <a
   * href="http://code.google.com/p/selenium/issues/detail?id=2936">2936</a>
   *
   * @author <a href="mailto:bbarker@part.net">bbarker</a>
   */
  protected final HtmlView clickAndWait(WebElement webElement)
  {
    LOG.debug("click element {}", webElement);
    webElement.click();
    waitForPageToLoad();
    return this;
  }

  /**
   * Replaces the text in a WebElement with the provided text.
   *
   * @param webElement
   *          - target for update
   * @param value
   *          - new value for the text of the WebElement
   * @return The {@link HtmlView} instance that invoked this method; enables
   *         fluent calls
   */
  protected HtmlView setValue(WebElement webElement, String value)
  {

    final String previousValue = getValue(webElement);
    LOG.debug("Replacing '{}' with '{}' on WebElement {}", previousValue, value, webElement);

    // htmlunit may not actually be replacing/putting in an empty value
    if (value == null) {
      value = "";
    }

    for (int i = 0; i < 3; i++) {
      // click on elm, as suggested in
      // http://stackoverflow.com/questions/20936403/sendkeys-are-not-working-in-selenium-webdriver
      webElement.click();
      webElement.clear();
      webElement.sendKeys(value);

      String elmContent = webElement.getAttribute("value");
      LOG.debug("New content of WebElement: '{}'", elmContent);

      if (!(elmContent.equalsIgnoreCase(value))) {
        LOG.warn("Setting WebElm {}\n" + "was not set to '{}'\n" + "  but actually '{}'", webElement, value, elmContent);
      }

      // if the target field is not filled, retry. Do not retry with the equals
      // ignore case, because sometimes it
      // is actually testing if the field can be filled with x amount of chars.
      if (StringUtils.isBlank(elmContent) && StringUtils.isNotBlank(value)) {
        LOG.debug("Retry set web elm: {}", i);
        continue;
      }
      // field was correctly set
      break;
    }

    return this;
  }

  /**
   * Get the text value in the given WebElement.
   * 
   * @param elt
   *          - target to get the value of
   */
  protected String getValue(WebElement elt)
  {
    return elt.getAttribute("value");
  }

  /**
   * Selects the option in the given field based on the given visible text and
   * then waits for the page to load.
   * <p>
   * This method should be used for interacting with a Select field that causes,
   * <b><i>EITHER</i></b> a <b>new page</b> is navigated to <b><i>OR</i></b> the
   * current page is <b>reloaded</b>.
   * 
   * @param webElement
   *          - the element for the given select/dropdown field
   * @param visibleText
   *          - the visible text to be selected
   * @return The {@link HtmlView} instance that invoked this method; enables
   *         fluent calls
   */
  protected HtmlView selectByVisibleTextAndWait(WebElement webElement, String visibleText)
  {
    final Select select = new Select(webElement);

    LOG.debug("selectByVisibleTextAndWait - {}", visibleText);
    select.selectByVisibleText(visibleText);

    waitForPageToLoad();

    return this;
  }

  /**
   * Helper method to select a option based on the visible text in the given
   * select element
   * <p>
   * 
   * @param webElement
   *          - the element for the given select/dropdown field
   * @param visibleText
   *          - the visible text to be selected
   * @return The {@link HtmlView} instance that invoked this method; enables
   *         fluent calls
   */
  protected HtmlView selectByVisibleText(WebElement webElement, String visibleText)
  {
    LOG.debug("selectByVisibleText: text '{}', element '{}'", visibleText, webElement);
    final Select select = new Select(webElement);
    select.selectByVisibleText(visibleText);
    return this;
  }

  /**
   * Helper method to select a option based on the value in the given select
   * element
   * <p>
   * 
   * @param webElement
   *          - the element for the given select/dropdown field
   * @param value
   *          - the value to be selected
   * @return The {@link HtmlView} instance that invoked this method; enables
   *         fluent calls
   */
  protected HtmlView selectByValue(WebElement webElement, String value)
  {
    LOG.debug("selectByValue: text '{}', element '{}'", value, webElement);
    final Select select = new Select(webElement);
    select.selectByValue(value);
    return this;
  }

  /**
   * Obtains the first selected option from a dropdown
   * 
   * @return - string of the visible selected text
   */
  protected String getSelectedVisibleText(WebElement webElement)
  {
    LOG.debug("Find first selected visible text for {}", webElement);
    Select sel = new Select(webElement);
    String visibleText = sel.getFirstSelectedOption().getText();
    LOG.debug("VisibleText: {}", visibleText);
    return visibleText;
  }

  /**
   * Obtains the visible text of all entries in a dropdown or select field
   * 
   * @param webElement
   *          - the element to get the text of
   * @return
   */
  protected List<String> getAllDropdownVisibleTextEntries(WebElement webElement)
  {
    List<String> optionsToReturn = new ArrayList<>();

    Select sel = new Select(webElement);
    List<WebElement> allOptions = sel.getOptions();

    for (WebElement option : allOptions) {
      optionsToReturn.add(option.getText());
    }
    LOG.debug("Found options: {}", optionsToReturn);
    return optionsToReturn;
  }

  /**
   * An expectation for checking that an element is present on the DOM of a
   * page. This does not necessarily mean that the element is visible.
   * 
   * @param by
   * @param maxWaitInSeconds
   * @return WebElement
   */
  protected WebElement waitForPresenceOfElement(By by, int maxWaitInSeconds)
  {
    return (new WebDriverWait(webDriver, maxWaitInSeconds)).until(ExpectedConditions.presenceOfElementLocated(by));
  }

  /**
   * An expectation for checking that there is at least one element present on a
   * web page.
   * <p>
   * Use when more that one WebElement could be returned.
   * 
   * @param by
   * @param maxWaitInSeconds
   * @return {@link List} of {@link WebElement}
   */
  protected List<WebElement> waitForPresenceOfAllElements(By by, int maxWaitInSeconds)
  {
    return (new WebDriverWait(webDriver, maxWaitInSeconds)).until(ExpectedConditions.presenceOfAllElementsLocatedBy(by));
  }

  /**
   * Waits for element to be visible (in view area of browser) and able to be
   * clickable (by a user).
   * <p>
   * Returns the <code>WebElement</code> of interest.
   * <p>
   * See also
   * {@link com.partnet.automation.HtmlView#waitForElementToBeClickable(WebElement, int)}.
   * 
   * @param by
   * @param maxWaitInSeconds
   * @return WebElement
   */
  protected WebElement waitForElementToBeClickable(By by, int maxWaitInSeconds)
  {
    return (new WebDriverWait(webDriver, maxWaitInSeconds)).until(ExpectedConditions.elementToBeClickable(by));
  }

  /**
   * Waits for the specified {@link ExpectedCondition} to occur in the page
   * until the max time has passed.
   * <p>
   * Preferably use the {@link ExpectedConditions} static methods to return the
   * desired <code>ExpectedCondition</code> instance. Note that this method is
   * used for <code>ExpectedConditions</code> static methods that return a
   * {@link WebElement} object.
   * <p>
   * Example:
   * 
   * <pre>
   * 
   * private static final int MAX_WAIT_IN_SECONDS = 10;
   * 
   * &#064;FindBy(css = &quot;a#button_idinput[value='Add Transaction']&quot;)
   * private WebElement addTransactionBtn;
   * 
   * private WebElement waitForTransaction()
   * {
   *   return waitForExpectedCondition(ExpectedConditions.elementToBeClickable(addTransactionBtn), MAX_WAIT_IN_SECONDS);
   * }
   * </pre>
   * <p>
   * See also
   * {@link com.partnet.automation.HtmlView#waitForElementToBeClickable(WebElement, int)}.
   * <p>
   * 
   * @param condition
   *          - the {@link ExpectedCondition} instance that defines the
   *          WebElement selenium is waiting for
   * @param maxWaitInSeconds
   *          - the maximum time in seconds before a {@link NotFoundException}
   *          is thrown
   * @return the <code>WebElement</code> instance returned by the
   *         <code>ExpectedCondition</code>
   */
  protected WebElement waitForExpectedCondition(ExpectedCondition<WebElement> condition, int maxWaitInSeconds)
  {
    return (new WebDriverWait(webDriver, maxWaitInSeconds)).until(condition);
  }

  /**
   * Waits for the specified {@link ExpectedCondition} to occur in the page
   * until the max time has passed.
   * <p>
   * Preferably use the {@link ExpectedConditions} static methods to return the
   * desired <code>ExpectedCondition</code> instance. Note that this method is
   * used for <code>ExpectedConditions</code> static methods that return a
   * {@link Boolean} object.
   * <p>
   * Example:
   * 
   * <pre>
   * 
   * private static final int MAX_WAIT_IN_SECONDS = 10;
   * 
   * private static final String optionText = &quot;SelectMe&quot;;
   * 
   * &#064;FindBy(css = &quot;select#id&quot;)
   * private WebElement selectDropDownBox;
   * 
   * private Boolean waitForTransaction()
   * {
   *   return waitForExpectedConditionBoolean(ExpectedConditions.textToBePresentInElement(selectDropDownBox, optionText), MAX_WAIT_IN_SECONDS);
   * }
   * </pre>
   * <p>
   * See also
   * {@link com.partnet.automation.HtmlView#waitForElementToBeClickable(WebElement, int)}.
   * <p>
   * 
   * @param condition
   *          - the {@link ExpectedCondition} instance that defines the
   *          WebElement selenium is waiting for
   * @param maxWaitInSeconds
   *          - the maximum time in seconds before a {@link NotFoundException}
   *          is thrown
   * @return the <code>Boolean</code> outcome of finding the condition
   */
  protected Boolean waitForExpectedConditionBoolean(ExpectedCondition<Boolean> condition, int maxWaitInSeconds)
  {
    return (new WebDriverWait(webDriver, maxWaitInSeconds)).until(condition);
  }

  /**
   * Clicks element and Accepts the alert that follows. This handles the case of
   * a real browser vs the PhantomJs alert strategy. <br>
   * <a href="https://github.com/detro/ghostdriver/issues/20">related info</a>
   * 
   * @param elm
   *          - web element that triggers the alert
   * @return - the string of the accepted alert
   */
  protected String clickAndAcceptAlert(WebElement elm)
  {
    return clickAndHandleAlert(elm, true, true);
  }

  /**
   * Clicks element and dismisses the alert that follows. This handles the case
   * of a real browser vs the PhantomJs alert strategy. <br>
   * <a href="https://github.com/detro/ghostdriver/issues/20">related info</a>
   * 
   * @param elm
   *          - web element that triggers the alert
   * @return - the string of the dismissed alert
   */
  protected String clickAndDismissAlert(WebElement elm)
  {
    return clickAndHandleAlert(elm, false, true);
  }

  protected String clickAndAcceptAlertIfPresent(WebElement elm)
  {
    return clickAndHandleAlert(elm, true, false);
  }

  private String clickAndHandleAlert(WebElement elm, boolean accept, boolean throwIfNoAlertPresent)
  {
    String alertMsg = null;
    LOG.debug("{} alert created by clicking button {}", accept ? "accept" : "dismiss", elm);

    Browser browser = getBrowser();

    // headless browsers need to inject javascript before the button is clicked
    // to handle the alert correctly
    if (browser.isHeadless()) {

      // webDriver.manage().deleteCookieNamed(ALERT_COOKIE_NAME);
      StringBuilder alertJs = new StringBuilder();

      alertJs.append("window.alert = window.confirm = function(msg){ ")
      // .append( "var date = new Date();")
      // .append( "date.setDate(date.getDate() + 1);")

          // cookies don't like to store new lines. This becomes a problem when
          // taking a screenshot for HTMLUNIT, and
          // transferring the cookie to PhantomJs.
          // This prevents newlines from being injected into the cookie. Later
          // on, the return string containing these
          // newline keywords will be replaced with actual newlines.
          .append("msg = msg.replace(/(\\r\\n|\\n|\\r)/gm, '" + ALERT_NEW_LINE_REPLACE + "');").append("document.cookie = '" + ALERT_COOKIE_NAME + "=' + msg + '';").append("return %s;").append("};");
      executeScript(String.format(alertJs.toString(), accept));
    }

    elm.click();

    if (browser.isHeadless()) {
      Cookie alertCookie = webDriver.manage().getCookieNamed(ALERT_COOKIE_NAME);

      for (Cookie cook : webDriver.manage().getCookies()) {
        System.err.print(cook.getName());
      }

      if (alertCookie != null) {
        // replace all newline keywords, to get original message
        alertMsg = StringUtils.trimToNull(alertCookie.getValue());

        if (alertMsg != null) alertMsg = alertMsg.replaceAll(ALERT_NEW_LINE_REPLACE, "\n");

        LOG.debug("Headless browser msg: {}", alertMsg);
      }
      else {
        LOG.debug("Cookie where headless alert messages are stored is null!");
      }

      if (StringUtils.isBlank(alertMsg)) {
        if (throwIfNoAlertPresent) {
          throw new NoAlertPresentException(String.format("No alert message found for headless browser %s!", browser));
        }
      }
    }
    else {

      Alert alert;

      // IE needs to wait for the alert to appear because we are using native
      // events
      try {
        if (browser.isInternetExplorer()) {
          alert = waitForAlertToBePresent();
        }
        else {
          alert = webDriver.switchTo().alert();
        }

        alertMsg = alert.getText();

        if (accept) {
          alert.accept();
        }
        else {
          alert.dismiss();
        }
      }
      catch (NoAlertPresentException | TimeoutException e) {
        if (throwIfNoAlertPresent) {
          throw e;
        }
        else {
          LOG.debug("No alert is present! return...");
        }
        return null;
      }
    }

    LOG.debug("{} alert message: {}", accept ? "Accepted" : "Dismissed", alertMsg);
    return alertMsg;
  }

  protected Object executeScript(String script, Object... args)
  {
    return executeScript(webDriver, script, args);
  }

  private Object executeScript(WebDriver driver, String script, Object... args)
  {
    return ((JavascriptExecutor) driver).executeScript(script, args);
  }

  /**
   * Returns an alert if a alert is present, or null if it is not.
   * <p>
   * <b>Note</b>: This does <b>NOT</b> work with headless browsers!
   * 
   * @return
   */
  protected Alert isAlertPresent()
  {
    Browser browser = getBrowser();

    // TODO: Dec 17, 2014 (bbarker) - Make this headless browser friendly
    if (browser.isInternetExplorer()) {
      try {
        return waitForAlertToBePresent();
      }
      catch (TimeoutException e) {
        return null;
      }
    }
    else {
      try {
        return webDriver.switchTo().alert();
      }
      catch (NoAlertPresentException e) {}
      return null;
    }
  }

  /**
   * Waits 3 seconds for an alert to appear on the page. This is typically only
   * used for Internet Explorer
   * 
   * @throws TimeoutException
   *           - if the timeout has been reached
   */
  protected Alert waitForAlertToBePresent()
  {
    return new WebDriverWait(webDriver, 3).until(conditionAlertPresent);
  }

  /**
   * Waits a specific amount of time for a field to be auto-populated with the
   * specified regex Note: Regex must match the ENTIRE field to pass
   */
  protected void waitForFieldToPopulate(int seconds, String regex, WebElement field)
  {
    // checks to see if field matches the regex
    ToggleFocusCondition fieldPopulated = new ToggleFocusCondition(regex, field, null);
    new WebDriverWait(webDriver, seconds).withMessage(String.format("Field #%s never matched the regex: '%s", field.getAttribute("id"), regex)).until(fieldPopulated);
  }

  /**
   * Generic method to wait for a dialog to complete loading. It returns the
   * WebElement containing the entire dialog scope
   * 
   * @param dialogContentLocator
   * @return - {@link WebElement} containing the entire Dialog scope.
   */
  protected WebElement waitForDialogToAppear(By dialogContentLocator)
  {
    return new WebDriverWait(webDriver, 30).until(new JQueryUiDialogsCondition(dialogContentLocator));
  }

  /**
   * Waits a specified amount of time for an element to become clickable.
   * 
   * @param elm
   * @param maxWaitSeconds
   * @throws TimeoutException
   */
  protected void waitForElementToBeClickable(WebElement elm, int maxWaitSeconds)
  {
    WebDriverWait wait = new WebDriverWait(webDriver, maxWaitSeconds);
    wait.until(ExpectedConditions.elementToBeClickable(elm));
  }

  /**
   * Default wait for page to load method. It will NOT throw a
   * {@link WebDriverException} if found, but continue to check if the page has
   * loaded. This also waits for the "Please Wait" overlay to disappear if
   * present.
   * 
   * @see #waitForPageToLoad(boolean)
   */
  protected void waitForPageToLoad()
  {
    waitForPageToLoad(true);
  }

  // TODO: Nov 13, 2014 (fpedroza) - need javadoc
  /**
   * Waits for a page to load; a set amount of time is allotted.
   * 
   * @param ignoreWebDriverException
   *          - whether or not a {@link WebDriverException} should be ignored.
   *          In certain cases, it can be useful to not ignore exceptions thrown
   *          when waiting for the page to load, however typically this value
   *          should be true.
   */
  protected void waitForPageToLoad(boolean ignoreWebDriverException)
  {
    LOG.debug("Wait for page to load..");
    String stringWaitProp = System.getProperty(WAIT_FOR_PAGE_PROP, "90");
    int waitProp;
    
    try {
      waitProp = Integer.parseInt(stringWaitProp);
    }
    catch (NumberFormatException e) {
      throw new NumberFormatException(String.format("%s, could not determine %s", e.getMessage(), WAIT_FOR_PAGE_PROP));
    }
    
    WebDriverWait wait = new WebDriverWait(webDriver, waitProp);

    if (ignoreWebDriverException) {
      wait.ignoring(WebDriverException.class);
    }

    wait.until(conditionPageLoaded); // wait for the page to load
  }

  /**
   * Switches to a newly opened window that was created during a test, such as
   * clicking a Help Text link.
   * 
   * @param locator
   *          - an element that is expected to be on the page - set to
   *          <code>null</code> to ignore
   * @param optionalPageTitle
   *          - the expected page title of the window to switch to - set to
   *          <code>null</code> to ignore
   * @see #switchToWindow(WebElement, By, String, boolean)
   */
  protected void switchToNewWindow(By locator, String optionalPageTitle)
      throws IllegalStateException, TimeoutException
  {
    switchToWindow(null, locator, optionalPageTitle, true);
  }

  /**
   * Switches back to an existing window that was created during a test. Usually
   * invoked after previously invoking {@link #switchToNewWindow(By, String)}
   * 
   * @param locator
   *          - an element that is expected to be on the page - set to
   *          <code>null</code> to ignore
   * @param optionalPageTitle
   *          - the expected page title of the window to switch to - set to
   *          <code>null</code> to ignore
   * @see #switchToWindow(WebElement, By, String, boolean)
   */
  protected void switchToOpenWindow(By locator, String optionalPageTitle)
      throws IllegalStateException, TimeoutException
  {
    switchToWindow(null, locator, optionalPageTitle, false);
  }

  /**
   * Switches to another window that was created during a test, such as clicking
   * a Help Text link.
   * 
   * @param element
   *          - the expected element on the window that is being switched to -
   *          set to <code>null</code> to ignore
   * @param optionalPageTitle
   *          - the expected page title of the window to switch to - set to
   *          <code>null</code> to ignore
   * @see #switchToWindow(WebElement, By, String, boolean)
   */
  protected void switchToNewWindow(WebElement element, String optionalPageTitle)
      throws IllegalStateException, TimeoutException
  {
    switchToWindow(element, null, optionalPageTitle, true);
  }

  /**
   * Switches back to another window that was created during a test. Usually
   * invoked after previously invoking
   * {@link #switchToNewWindow(WebElement, String)}
   * 
   * @param element
   *          - the expected element on the window that is being switched to -
   *          set to <code>null</code> to ignore
   * @param optionalPageTitle
   *          - the expected page title of the window to switch to - set to
   *          <code>null</code> to ignore
   * @see #switchToWindow(WebElement, By, String, boolean) switchToWindow
   */
  protected void switchToOpenWindow(WebElement element, String optionalPageTitle)
      throws IllegalStateException, TimeoutException
  {
    switchToWindow(element, null, optionalPageTitle, false);
  }

  /**
   * Overloaded method to handle the various cases of switching to a window.
   * 
   * @param element
   *          - the element that should be searched for on the page, or
   *          <code>null</code>
   * @param locator
   *          - the locator that should be present on the page, or
   *          <code>null</code>
   * @param pageTitle
   *          - the title of the page to be switched to, or <code>null</code>
   * @param findNewWindow
   *          - whether or not to find a new (true) or existing (false) window.
   * 
   * @throws IllegalStateException
   * @throws TimeoutException
   */
  private void switchToWindow(WebElement element, By locator, String pageTitle, boolean findNewWindow)
      throws IllegalStateException, TimeoutException
  {
    LOG.debug("Switch to correct window");

    // at least one of the window-identifying parameters must be provided
    if (element == null && pageTitle == null && locator == null) {
      throw new IllegalArgumentException("element, locator, and pageTitle are all null - cannot determine the correct window");
    }
    // sanity check to ensure multiple window-identifying parameters are not
    // provided
    if (element != null && locator != null) {
      throw new IllegalArgumentException("element and locator cannot both be used to identify window");
    }

    WebDriverWait wait = new WebDriverWait(webDriver, 15);

    // TODO: Nov 13, 2014 (fpedroza) - this logic is flawed as it assumes only 2
    // windows will be open at a time
    int windowsToWaitFor = findNewWindow ? 2 : 1;

    // wait for the specified number of windows to be open
    wait.until(new MultipleWindowHandlesVisibleCondition(windowsToWaitFor));

    Set<String> windowHandles = webDriver.getWindowHandles();

    LOG.debug("Window handles: {}", windowHandles);

    // try all windows, because I don't know if the current focused window
    // correct, or if it is a different one
    for (String window : windowHandles) {

      LOG.debug("Switch to window '{}'", window);
      webDriver.switchTo().window(window);

      // wait for switched to window to load, because it still could be in the
      // process of loading when switching to it
      waitForPageToLoad();

      try {
        // Now see if the now focused page has a element that is specific to
        // this page on it.
        if (element != null) {
          element.isDisplayed();
          LOG.debug("Found web element on page!");
        }

        if (locator != null) {
          webDriver.findElement(locator).isDisplayed();
          LOG.debug("Found By locator on page!");
        }
      }
      catch (NoSuchElementException e) {
        LOG.debug("Nope, try again. Msg: {}", e.getMessage().substring(0, e.getMessage().indexOf("\n")));
        continue;
      }

      // optionally check that the page title is correct (if one was provided)
      if (pageTitle != null) {

        String currentPageTitle = webDriver.getTitle();
        if (currentPageTitle.equals(pageTitle)) {
          LOG.debug("Found correct page title!");
          return;
        }
        else {
          LOG.debug("Found window title of '{}' but searching for '{}' - continue", pageTitle, currentPageTitle);
          continue;
        }
      }

      // no additional check need so we're done
      return;
    }

    // if we make it here, the expected page wasn't found so report the error
    throw new IllegalStateException(String.format("Can not find the '%s' page with the element '%s' in the set of window handles!", pageTitle, element == null ? locator.toString() : element.toString()));
  }

  /**
   * Parses through an html description list (tag = dl) and puts the description
   * term (tag = dt) as the key in the map and puts the description description
   * (tag = dd) as the value in map
   * 
   * @param termsAndDescriptions
   *          - this must be a list of dt and dd {@link WebElement}
   * @return Map
   */
  protected Map<String, String> parseDescriptionList(final List<WebElement> termsAndDescriptions)
  {
    Map<String, String> descriptionList = new HashMap<>(termsAndDescriptions.size());
    String term = null;

    for (WebElement elt : termsAndDescriptions) // the WebElement expected is
                                                // the array of a dl's dt/dd
                                                // WebElements
    {
      String tag = elt.getTagName(); // this gets the tag type. It is expecting
                                     // either dt or dd
      String taggedText = elt.getText().trim(); // this gets the value that is
                                                // tagged
      LOG.debug("tag({}) text({})", tag, taggedText);

      if (tag.equals("dt")) { // if tag is <dt> description list term; the "key"
                              // in the map
        term = taggedText;
      }
      else
        if (tag.equals("dd")) { // if tag is <dl> description list
                                // description/definition; the "value" in the
                                // map
          String previousValue = descriptionList.put(term, taggedText);
          if (previousValue != null) {
            throw new IllegalStateException(String.format("Unexpected condition - key (%s) with multiple values (%s) and (%s)", term, previousValue, taggedText));
          }
        }
        else {
          throw new IllegalArgumentException("Unexpected tag in description list, tag is " + tag);
        }
    }

    return descriptionList;
  } // end method

  /**
   * Focus or blurs an element. This comes in handy when an element needs to be
   * focused and then unfocused to trigger a javascript event, such as an
   * autocomplete zip code field.
   * 
   * @param triggerElm
   *          - the element to be acted on
   * @param driver
   *          - the {@link WebDriver} to use
   * @param focusElement
   *          - should the element gain focus or blur?
   */
  private void focusOrBlur(WebElement triggerElm, WebDriver driver, boolean focusElement)
  {
    String opt = focusElement ? "focus" : "blur";

    String script = String.format("$(arguments[0]).trigger('%s');", opt);

    LOG.debug("{} element: {}", opt, triggerElm);
    executeScript(driver, script, triggerElm);
  }

  /**
   * Trigger the <code>focus</code> javascript event on a element.
   * 
   * @see #focusOrBlur(WebElement, WebDriver, boolean)
   */
  protected void focusElement(WebElement triggerElm)
  {
    focusOrBlur(triggerElm, webDriver, true);
  }

  /**
   * Trigger the <code>blur</code> javascript event on a element. (removes focus
   * from the element)
   * 
   * @see #focusOrBlur(WebElement, WebDriver, boolean)
   */
  protected void blurElement(WebElement triggerElm)
  {
    focusOrBlur(triggerElm, webDriver, false);
  }

  /**
   * Waits a specified amount of time for a field to be auto-populated with the
   * given regex. Each time the field is checked, the triggerElm will be
   * focus/blurred.
   * 
   * @param seconds
   *          - amount of time to wait for the field to populate
   * @param regex
   *          - regex to match the entire field
   * @param field
   *          - field to look for the regex
   * @param triggerElm
   *          - element to focus/blur
   */
  protected void triggerAndWaitForFieldToPopulate(int seconds, String regex, WebElement field, WebElement triggerElm)
  {
    // checks to see if field matches the regex
    ToggleFocusCondition fieldPopulated = new ToggleFocusCondition(regex, field, triggerElm);
    new WebDriverWait(webDriver, seconds).withMessage(String.format("Field #%s never matched the regex: '%s", field.getAttribute("id"), regex)).until(fieldPopulated);
  }

  // TODO: Nov 4, 2014 (fpedroza) - need more/better javadoc
  /**
   * Obtains the hidden text for a web element.
   * 
   * @param webElm
   * @see <a
   *      href="http://stackoverflow.com/questions/1359469/innertext-works-in-ie-but-not-in-firefox">1</a>
   * @see <a
   *      href="http://stackoverflow.com/questions/13047056/how-to-read-text-from-hidden-element-with-selenium-webdriver">2</a>
   * @see <a
   *      href="http://stackoverflow.com/questions/24427621/innertext-vs-innerhtml-vs-label-vs-text-vs-textcontent-vs-outertext">3</a>
   * @see <a
   *      href="http://www.kellegous.com/j/2013/02/27/innertext-vs-textcontent">4</a>
   */
  protected String getHiddenText(WebElement webElm)
  {
    Browser currentBrowser = getBrowser();

    Object textContent = executeScript("return arguments[0].textContent", webElm);
    Object innerText = executeScript("return arguments[0].innerText", webElm);

    if (textContent != null && innerText != null) {
      LOG.warn("innerText comparison - both non-null \n textContent({}) and \n innerText({})", textContent, innerText);
    }

    String javascript = (currentBrowser.isFirefox() || currentBrowser.isHeadless()) ? "return arguments[0].textContent" : "return arguments[0].innerText";

    String hiddenText = executeScript(javascript, webElm).toString();

    return hiddenText;
  }

  /**
   * Scrolls an element into view. Usually a {@link clickAndWait} should do
   * this, so use of this method is discouraged unless there is flakiness found
   * with the element not being in view when clicking on it.
   * 
   * @param elm
   *          - the element to scroll into view
   * @return The {@link HtmlView} instance that invoked this method; enables
   *         fluent calls
   */
  protected HtmlView scrollIntoView(WebElement elm)
  {
    LOG.debug("Scroll element into view");
    executeScript("arguments[0].scrollIntoView()", elm);
    return this;
  }

  /**
   * This injects a javascript ajax listener into the current page. If the page
   * is refreshed or changed, this listener will need to be injected again.
   */
  protected void injectAjaxListener()
  {
    executeScript(JAVASCRIPT_AJAX_MESSAGE_ARRAY + " = new Array();" + "$( document ).ajaxSuccess(function( event, xhr, settings ) {" + "var msg = xhr.responseText;" + JAVASCRIPT_AJAX_MESSAGE_ARRAY + ".push(msg);" + "console.log(msg);" + "});");
  }

  /**
   * If multiple requests will happen on the same page, the ajax array will need
   * to be reset to accurately obtain the expected values.
   */
  protected void resetAjaxListenerList()
  {
    executeScript(JAVASCRIPT_AJAX_MESSAGE_ARRAY + " = new Array();");
  }

  /**
   * Waits for an element to appear on the page before returning. Example:
   * WebElement waitElement =
   * fluentWait(By.cssSelector(div[class='someClass']));
   * 
   * @param locator
   * @return
   */
  protected WebElement waitForElementToAppear(final By locator)
  {
    Wait<WebDriver> wait = new FluentWait<WebDriver>(webDriver).withTimeout(30, TimeUnit.SECONDS).pollingEvery(5, TimeUnit.SECONDS).ignoring(NoSuchElementException.class);

    WebElement element = null;
    try {
      element = wait.until(new Function<WebDriver, WebElement>() {

        @Override
        public WebElement apply(WebDriver driver)
        {
          return driver.findElement(locator);
        }
      });
    }
    catch (TimeoutException e) {
      try {
        // I want the error message on what element was not found
        webDriver.findElement(locator);
      }
      catch (NoSuchElementException renamedErrorOutput) {
        // print that error message
        renamedErrorOutput.addSuppressed(e);
        // throw new
        // NoSuchElementException("Timeout reached when waiting for element to be found!"
        // + e.getMessage(), correctErrorOutput);
        throw renamedErrorOutput;
      }
      e.addSuppressed(e);
      throw new NoSuchElementException("Timeout reached when searching for element!", e);
    }

    return element;
  }

  /**
   * Waits for a json response with the desired key
   * 
   * @param key
   * @return
   */
  protected JSONObject waitForAjaxResponse(String key)
  {
    WebDriverWait wait = new WebDriverWait(webDriver, 90);
    wait.pollingEvery(1, TimeUnit.SECONDS);
    return wait.until(new AjaxResponseOccursCondition(key));
  }

  /**
   * Exposed object to wait for page to load. Typically, use the
   * waitForPageToLoad method instead of this one.
   */
  private ExpectedCondition<Boolean> conditionPageLoaded = new ExpectedCondition<Boolean>() {

    @Override
    public Boolean apply(WebDriver driver)
    {
      // Set the state to something to be changed later
      String state = "pre-check";

      state = (String) executeScript(driver, "return document.readyState");
      return (state == null) ? false : (state.equals("complete"));
    }

    // Used when the timeout exception is thrown.
    // Example: Timed out after x seconds waiting for toString()
    @Override
    public String toString()
    {
      return "the page to load";
    }
  };

  /**
   * Waits for an alert dialog to appear.<br>
   * Usage:
   * <code>new WebDriverWait(wdp.get(), 45).until(HtmlView.isAlertPresent);</code>
   */
  private ExpectedCondition<Alert> conditionAlertPresent = new ExpectedCondition<Alert>() {

    @Override
    public Alert apply(WebDriver driver)
    {
      try {
        return driver.switchTo().alert();
      }
      catch (NoAlertPresentException e) {
        return null;
      }
    }

    @Override
    public String toString()
    {
      return "an alert is present";
    }
  };

  /**
   * Implementation of ExpectedCondition to listen for a ajax request json
   * 
   * @author bbarker
   */
  private class AjaxResponseOccursCondition
      implements ExpectedCondition<JSONObject>
  {

    private final String key;

    public AjaxResponseOccursCondition(String key)
    {
      this.key = key;
    }

    @Override
    public JSONObject apply(WebDriver driver)
    {
      List<JSONObject> response = getResponses(driver);

      for (JSONObject obj : response) {
        if (obj.has(key)) {
          return obj;
        }
      }

      return null;
    }

    /**
     * Obtains the ajax response
     * 
     * Note: {@link HtmlView#injectAjaxListener()} will need to be called so the
     * listener is injected into the page.
     * 
     * @return a list of all of the responses obtained by the listener from the
     *         time it was injected until the time this was called
     */
    private List<JSONObject> getResponses(WebDriver driver)
    {
      List<?> allResponses = (List<?>) executeScript(driver, String.format("return %s;", JAVASCRIPT_AJAX_MESSAGE_ARRAY));
      List<JSONObject> listOfJson = new ArrayList<>();

      StringBuilder sb = new StringBuilder();

      if (allResponses != null) {

        sb.append("Ajax response(s):");

        for (Object o : allResponses) {
          String response = (String) o;
          sb.append("\n").append(response);
          try {
            listOfJson.add(new JSONObject(response));
          }
          catch (JSONException e) {
            LOG.error("Could not create new JSON Object", e);
          }
        }

        LOG.debug(sb.toString());
      }
      return listOfJson;
    }

    @Override
    public String toString()
    {
      return String.format("Ajax request with json key '%s' was never found", key);
    }
  }

  /**
   * ExpectedCondition to wait for any current visible jquery dialogs to load.
   * Assumes blockUI is used when dialogs are loading.
   */
  private class JQueryUiDialogsCondition
      implements ExpectedCondition<WebElement>
  {

    private final By dialogContentLocator;

    private WebElement dialogScope;

    public JQueryUiDialogsCondition(By dialogContentLocator)
    {
      this.dialogContentLocator = dialogContentLocator;
    }

    @Override
    public WebElement apply(WebDriver driver)
    {
      List<WebElement> allDialogs = driver.findElements(By.cssSelector(".ui-dialog"));

      for (WebElement singleDialog : allDialogs) {
        List<WebElement> dlaOrContractContact = singleDialog.findElements(dialogContentLocator);
        if (dlaOrContractContact.size() > 0 && singleDialog.isDisplayed()) {
          dialogScope = singleDialog;
          break;
        }
      }

      if (dialogScope != null) {

        int numberOfBlocks = dialogScope.findElements(By.className("blockUI")).size();

        if (numberOfBlocks == 0) {
          LOG.debug("Dialog loaded!");
          return dialogScope;
        }
        else {
          LOG.debug("Dialog loading...");
          return null;
        }
      }
      LOG.debug("Dialog not yet present");
      return null;
    }

    @Override
    public String toString()
    {
      return "JQueryUiDialogsCondition";
    }
  }

  /**
   * ExpectedCondition to look for a given regex on a given web element. If not
   * found, triggers a focus event on a different web element.
   */
  private class ToggleFocusCondition
      implements ExpectedCondition<Boolean>
  {

    private final String regex;

    private final WebElement expectedElm;

    private final WebElement triggerElm;

    public ToggleFocusCondition(String regex, WebElement expectedElm, WebElement triggerElm)
    {
      this.regex = regex;
      this.expectedElm = expectedElm;
      this.triggerElm = triggerElm;
    }

    @Override
    public Boolean apply(WebDriver driver)
    {
      String fieldText = expectedElm.getAttribute("value");
      LOG.debug("Current field text: '{}'", fieldText);

      if (fieldText.matches(regex)) {
        return true;
      }
      else {
        // focus/blur the element that needs to be triggered
        if (triggerElm != null) {
          focusOrBlur(triggerElm, driver, true);
          focusOrBlur(triggerElm, driver, false);
        }

        return false;
      }
    }

    @Override
    public String toString()
    {
      return String.format("ToggleFocusCondition - regex:%s", regex);
    }
  }

  /**
   * Expected condition to wait for multiple window handles. Returns whether or
   * not the specified number of window handles is currently present.
   */
  private class MultipleWindowHandlesVisibleCondition
      implements ExpectedCondition<Boolean>
  {

    private final int windowsToWaitFor;

    public MultipleWindowHandlesVisibleCondition(int windowsToWaitFor)
    {
      if (windowsToWaitFor < 1) {
        throw new IllegalArgumentException(String.format("windowsToWaitFor(%d) must be > 1", windowsToWaitFor));
      }
      this.windowsToWaitFor = windowsToWaitFor;
    }

    @Override
    public Boolean apply(WebDriver driver)
    {
      if (driver.getWindowHandles().size() >= windowsToWaitFor) {
        return true;
      }
      return false;
    }

    // Used when the timeout exception is thrown.
    // Example: Timed out after x seconds waiting for toString()
    @Override
    public String toString()
    {
      return String.format("MultipleWindowHandlesVisibleCondition - windowsToWaitFor:%d", windowsToWaitFor);
    }
  }
}
