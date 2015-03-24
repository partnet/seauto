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

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.partnet.automation.DependencyContainer;
import com.partnet.automation.HtmlView;
import com.partnet.automation.page.panel.PanelProvider;

/**
 * Base page object. Provides a common constructor for subclasses and various
 * helper methods to support {@link #initialize()} and {@link #verify()}.
 * 
 * @author <a href="mailto:rbascom@part.net">rbascom</a>
 * @author fpedroza
 */
public abstract class Page
    extends HtmlView
{

  protected final PanelProvider panelProvider;

  private static final Logger LOG = LoggerFactory.getLogger(Page.class);

  /**
   * The superclass for all Page Objects. It allows access to the
   * {@link DependencyContainer} and initialized @FindBy elements
   * 
   * @param depContainer
   *          TODO
   */
  public Page(DependencyContainer depContainer)
  {
    this(depContainer.get(WebDriver.class), depContainer.get(PanelProvider.class));
  }

  /**
   * The superclass for all Page Objects. It allows access to the
   * {@link WebDriver}, {@link PanelProvider}, and initialized @FindBy elements
   */
  private Page(WebDriver webDriver, PanelProvider panelProvider)
  {
    super(webDriver);
    this.panelProvider = panelProvider;
  }

  /**
   * Get the current title of the current page the browser is on
   */
  protected String getTitle()
  {
    return webDriver.getTitle();
  }

  /**
   * Verifies that the Page object used in a step class represents the current
   * browser page.
   * <p/>
   * This method is called whenever a new page object is requested. It can also
   * be called manually. Implementations should call the other verify methods
   * such as:
   * <li>{@link #verifyByTitle(String)}
   * <li>{@link #verifyBySelector(By)}
   * <li>{@link #verifyByWebElement(WebElement)}
   * <p/>
   * For example, when the page title is dynamic, another element on the page
   * other than title may be used that provides a consistent value.
   * 
   * @return void
   * @throws IllegalStateException
   *           - thrown if the page could not be verified
   */
  public abstract void verify()
      throws IllegalStateException;

  /**
   * Verifies existence of a page title as specified by <code>title</code> in
   * the current browser page.
   *
   * @param title
   *          - expected page title
   * @return void
   * @throws IllegalStateException
   *           - thrown if the page could not be verified
   * @see {@link #verify()}
   */
  protected final void verifyByTitle(String title)
      throws IllegalStateException
  {
    if (!webDriver.getTitle().equals(title)) {
      throw new IllegalStateException(String.format("\nERROR - The page in the browser does not match the page class being used in the step class.\n" + "The expected identifier is '%s', but '%s' was returned.\n" + "URL of failure:%s\n", title, webDriver.getTitle(), webDriver.getCurrentUrl()));
    }
  }

  /**
   * Verifies existence of an element on the browser page using
   * <code>WebDriver.By</code> as defined by the Page object used in a step
   * class.
   * <p/>
   * All <code>WebDriver.By</code> searches are valid including: id, css,
   * linkText, name, etc.
   * 
   * @param by
   *          - locator to use to locate expected page element
   * @throws IllegalStateException
   *           - thrown if the page could not be verified
   * @see {@link #verify()}
   */
  protected final void verifyBySelector(By by)
      throws IllegalStateException
  {
    if (webDriver.findElements(by).isEmpty()) {
      throw new IllegalStateException(String.format("\nERROR - The page in the browser does not match the page class being used in the step class.\n" + "The element '%s' on the browser page was not found.\n\n", by.toString()));
    }
  }

  /**
   * Verifies existence of an element on the browser page using
   * <code>WebElement.isDisplayed()</code>.
   * <p/>
   * All <code>WebDriver.By</code> searches are valid including: id, css,
   * linkText, name, etc.
   * 
   * @param element
   *          - expected element to be found on the page
   * @throws IllegalStateException
   *           - thrown if the page could not be verified
   * @see {@link #verify()}
   */
  protected final void verifyByWebElement(WebElement element)
      throws IllegalStateException
  {
    try {
      element.isDisplayed();
    }
    catch (NoSuchElementException e) {
      throw new IllegalStateException(String.format("\nERROR - The page in the browser does not match the page class being used in the step class.\n\n"), e);
    }
  }

  /**
   * Initializes page for use in client code (i.e. step classes).
   * <p/>
   * Implicitly verifies page and assures it is in a ready state. Intended to be
   * called only by the testing framework (i.e. {@link PageProvider}.
   * <p/>
   * Override {@link #ready()} and {@link #verify()} for page specific behavior.
   * <p/>
   * 
   * @throws IllegalStateException
   */
  public final void initialize()
      throws IllegalStateException
  {
    ready();
    verify();
  }

  /**
   * Defines state of page when it is ready for interaction.
   * <p/>
   * Override this method in child page classes for pages with complex load
   * processes (e.g. AJAX callbacks, pagination framework use)
   * <p/>
   * Use one of the <code>wait</code> methods to cause the web driver to poll
   * until the page is ready to be used.
   */
  protected void ready()
  {
    // Intentionally left blank for child classes to optionally implement
    LOG.debug("Empty ready() method called for {}", this.getClass().getName());
  }

}
