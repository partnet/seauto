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

package com.partnet.junit;

import java.lang.annotation.Annotation;

import com.partnet.junit.annotations.browser.Android;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.partnet.automation.Browser;
import com.partnet.automation.selenium.DriverProvider;
import com.partnet.automation.util.PathUtils;
import com.partnet.junit.annotations.browser.Chrome;
import com.partnet.junit.annotations.browser.Firefox;
import com.partnet.junit.annotations.browser.HTMLUnit;
import com.partnet.junit.annotations.browser.IE;
import com.partnet.junit.annotations.browser.PhantomJs;

/**
 * @author <a href="mailto:bbarker@part.net">bbarker</a>
 */
public class SeAuto
    extends BlockJUnit4ClassRunner
{

  private static final Logger log = LoggerFactory.getLogger(SeAuto.class);

  private Class<?> klass;

  private final WeldContainer weld = new Weld().initialize();

  public SeAuto(Class<?> klass)
      throws InitializationError
  {
    super(klass);
    this.klass = klass;
  }

  @Override
  protected Object createTest()
      throws Exception
  {
    // get fully injected instance of the test class
    return weld.instance().select(klass).get();
  }

  @Override
  protected void runChild(FrameworkMethod method, RunNotifier notifier)
  {
    DriverProvider driverProvider = weld.instance().select(DriverProvider.class).get();

    if (super.isIgnored(method)) {
      runChild(method, notifier);
      return;
    }

    // prefer annotations of methods over class, but use class as a fallback.
    Browser browser = getBrowser(method.getAnnotations());

    if (browser == null) {
      browser = getBrowser(klass.getAnnotations());
    }

    try {
      driverProvider.launch(browser);
    } catch (Throwable e) {
      //shut down driver if it is running
      finalizeTest(method, driverProvider);
      throw e;
    }


    try {
      super.runChild(method, notifier);
    } finally {
      //make sure the browser is killed
      finalizeTest(method, driverProvider);
    }
  }


  /**
   * Finalizes the test by taking a screenshot, then killing the browser
   * @param method {@link FrameworkMethod} for the running test
   * @param driverProvider Web driver provider
   */
  private void finalizeTest(FrameworkMethod method, DriverProvider driverProvider)
  {
    if (super.isIgnored(method)) {
      return;
    }

    takeScreenshot(method, driverProvider);
    log.debug("Ending browser for test " + getTestName(method));
    driverProvider.end();
  }

  private void takeScreenshot(FrameworkMethod method, DriverProvider driverProvider)
  {
    String screenshotPath = PathUtils.getProjectPath().appendFolders("target", "screenshot")
        .appendFile(getTestName(method) + ".png").toString();
    log.debug("Screenshot saved to: {}", screenshotPath);
    driverProvider.saveScreenshotAs(screenshotPath);
  }

  private String getTestName(FrameworkMethod method)
  {
    return klass.getName() + "-" + method.getName();
  }

  /**
   * Helper method to determine what browser to launch given the annotations
   * 
   * @param annotations array of {@link Annotation}
   * @return the {@link Browser} enum
   */
  private Browser getBrowser(Annotation[] annotations)
  {

    for (Annotation annot : annotations) {

      if (annot.annotationType() == HTMLUnit.class)
        return Browser.HTMLUNIT;

      if (annot.annotationType() == PhantomJs.class)
        return Browser.PHANTOMJS;

      if (annot.annotationType() == Firefox.class)
        return Browser.FIREFOX;

      if (annot.annotationType() == Chrome.class)
        return Browser.CHROME;

      if (annot.annotationType() == IE.class)
        return Browser.IE;

      if (annot.annotationType() == Android.class)
        return Browser.ANDROID;
    }

    return null;
  }

}
